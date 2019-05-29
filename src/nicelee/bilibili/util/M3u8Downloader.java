package nicelee.bilibili.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class M3u8Downloader{

	public static ExecutorService m3u8ThreadPool = Executors.newFixedThreadPool(3);
	
	volatile Boolean isSuccess;
	volatile Integer taskCnt = 0;
	volatile Integer failCnt = 0;
	volatile Integer doneCnt = 0;
	
	
	public static void main(String args[]) {
		String urlM3u8 = args[0];
		String fileName = args[1];
		M3u8Downloader m3u8 = new M3u8Downloader();
		try {
			URL url = new URL(urlM3u8);
			m3u8.download(new InputStreamReader(url.openStream()), fileName, new HttpHeaders().getHeaders(), urlM3u8);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param reader
	 * @param fileName
	 * @param headers 不可为空
	 * @param baseUrl
	 */
	public void download(Reader reader, String fileName, HashMap<String, String> headers, String...baseUrl) {
		BufferedReader buReader = new BufferedReader(reader);
		String line = null;
		boolean isRealSrc = true;
		try {
			while((line = buReader.readLine())!=null) {
				line = line.trim();
				// 当前只是m3u8的目录，需要换种方式解析下载
				if(line.startsWith("#EXT-X-STREAM-INF")) {
					isRealSrc = false;
					break;
				}
				
				if(!line.startsWith("#") && !line.isEmpty()) {
					// 如果是相对路径，补全
					if(!line.startsWith("http")) {
						line = genABUrl(line, baseUrl[0]);
					}
					
					Logger.println(line);
					final String url = line;
					final String fname = String.format("%s-%d.ts", fileName, taskCnt);
					taskCnt ++;
					m3u8ThreadPool.execute(new Runnable() {
						@Override
						public void run() {
							HttpRequestUtil util = new HttpRequestUtil();
							if(util.download(url, fname, headers)) {
								synchronized (doneCnt) {
									doneCnt ++;
								}
								// 分段下载完毕后判断是否全部下载完成
								if(isSuccess != null && doneCnt > 0 && doneCnt.equals(taskCnt)) {
									merge(util.getFileDownload(), taskCnt-1, true);
									isSuccess = true;
								}
							}else {
								synchronized (failCnt) {
									failCnt ++;
								}
								
							}
						}
					});
				}
			}
			
			// 解析真正的下载源
			while((line = buReader.readLine())!=null) {
				line = line.trim();
				// 当前只是m3u8的目录，需要换种方式解析下载
				if(line.startsWith("#EXT-X-STREAM-INF")) {
					break;
				}
				Logger.println(line);
				if(!line.startsWith("#") && !line.isEmpty()) {
					// 如果是相对路径，补全
					if(!line.startsWith("http")) {
						line = genABUrl(line, baseUrl[0]);
						Logger.println(line);
						URL url = new URL(line);
						this.download(new InputStreamReader(url.openStream()), fileName, headers, line);
						break;
					}
				}
			}
			
			isSuccess = false;
			buReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
//		new Thread(new Runnable() {
//			@Override
//			public void run() {
				while(isSuccess != true & isRealSrc) {
					System.out.println(progress());
					System.out.println(isSuccess);
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				
//			}
//		}, "monitoring-thread").start();
	}
	
	/**
	 * 生成绝对路径
	 */
	final static Pattern hostPattern = Pattern.compile("^https?\\://[^/]+");
	final static Pattern rootPattern = Pattern.compile("^https?\\://.*/");
	private String genABUrl(String url, String parentUrl) {
		if(url.startsWith("http")) {
			// 如果是绝对路径，直接返回
			return url;
		}else if(url.startsWith("//")) {
			// 如果缺scheme，补上https?
			if(parentUrl.startsWith("https")) {
				return "https"+ url;
			}else {
				return "http"+ url;
			}
		}else if(url.startsWith("/")) {
			// 补上host
			Matcher m1 = hostPattern.matcher(parentUrl);
			m1.find();
			return m1.group() + url;
		}else {
			// 纯相对路径
			Matcher m2 = rootPattern.matcher(parentUrl);
			m2.find();
			return m2.group() + url;
		}
	}
	/**
	 * 获取当前状况
	 * @return
	 */
	public String progress() {
		return String.format("当前已下载 %d/%d, 失败任务数 %d", doneCnt, taskCnt, failCnt);
	}
	
	private final static Pattern tsPart = Pattern.compile("^(.*)-([0-9]+)\\.ts$");
	private byte[] buffer = new byte[1024 * 1024];
	
	/**
	 * 直接文件的堆积，好像不对劲，还是留着ffmpeg吧
	 * @param file 最后一个下载完成的文件名
	 * @param lastIndex 最大文件index
	 */
private void merge(File file, int lastIndex, boolean deleteFiles) {
		
		Matcher matcher = tsPart.matcher(file.getName());
		matcher.find();
		String realName = matcher.group(1);
		Logger.println(realName);
		
		RandomAccessFile dstFile = null;
		try {
			dstFile = new RandomAccessFile(new File(file.getParentFile(), realName + ".ts"), "rw");
			for(int i = 0; i <= lastIndex; i++) {
				File file2 = new File(file.getParentFile(), realName+ "-" + i + ".ts");
				RandomAccessFile ri = new RandomAccessFile(file2, "r");
				int lenRead = ri.read(buffer);
				while (lenRead > -1) {
					dstFile.write(buffer, 0, lenRead);
					lenRead = ri.read(buffer);
				}
				ri.close();
				if(deleteFiles) {
					file2.delete();
				}
				//Logger.println(i + " 部分合并完毕");
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				dstFile.close();
			} catch (IOException e) {
			}
		}
	}
}
