package nicelee.bilibili;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

import nicelee.bilibili.enums.StatusEnum;
import nicelee.bilibili.live.FlvChecker;
import nicelee.bilibili.live.RoomDealer;
import nicelee.bilibili.live.domain.RoomInfo;
import nicelee.bilibili.live.impl.RoomDealerBilibili;
import nicelee.bilibili.util.Logger;
import nicelee.bilibili.util.TrustAllCertSSLUtil;
import nicelee.bilibili.util.ZipUtil;

public class Main {

	final static String version = "v2.4";
	static boolean autoCheck;
	static boolean splitScriptTagsIfCheck;
	static boolean deleteOnchecked;
	static boolean flagZip;
	static String liver;
	static String shortId;
	static String qn;
	static int maxFailCnt;
	static int failCnt;

	static long splitFileSize;
	static long splitRecordPeriod;
	volatile static boolean flagSplit;

	static String fileName = "{name}-{shortId} 的{liver}直播{startTime}-{seq}";
	static String saveFolder;

	/**
	 * 程序入口
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
//		 args = new String[]{"debug=false&liver=bili&id=221602&qn=10000&delete=false&check=false"};  			// 清晰度全部可选，可不需要cookie
//		 args = new String[]{"debug=true&check=true&liver=douyu&id=35954&zip=true&saveFolder=D:\\Workspace&fileName=测试{liver}-{name}-{startTime}-{seq}"};  	// 清晰度全部可选，但部分高清需要cookie 
//		args = new String[]{"debug=true&check=true&liver=kuaishou&id=mianf666&qn=0&delete=false"};  					// 清晰度全部可选，可不需要cookie asd199895
//		args = new String[]{"debug=true&check=false&liver=huya&id=660137"}; 				// 清晰度全部可选，可不需要cookie 
//		args = new String[]{"debug=true&check=true&liver=yy&id=28581146&qn=1"}; 		// 只支持默认清晰度 54880976
//		args = new String[] { "debug=true&check=true&liver=zhanqi&id=90god" }; 			// 清晰度全部可选，可不需要cookie 90god huashan ydjs
//		args = new String[] { "debug=true&check=true&liver=huajiao&id=278581432&qn=1" }; // 只支持默认清晰度(似乎只有一种清晰度)
		// 初始化默认值
		autoCheck = true;
		splitScriptTagsIfCheck = false;
		deleteOnchecked = true;
		Logger.debug = false;
		liver = "bili";
		maxFailCnt = 5;
		failCnt = 0;
		splitFileSize = 0;
		splitRecordPeriod = 0;
		flagSplit = false;
		flagZip = false;
		// 根据参数初始化值
		if (args != null && args.length >= 1) {
			String value = getValue(args[0], "check");
			if ("false".equals(value)) {
				autoCheck = false;
			}
			value = getValue(args[0], "splitScriptTags");
			if ("true".equals(value)) {
				splitScriptTagsIfCheck = true;
			}
			value = getValue(args[0], "delete");
			if ("false".equals(value)) {
				deleteOnchecked = false;
			}
			value = getValue(args[0], "debug");
			if ("true".equals(value)) {
				Logger.debug = true;
			}
			value = getValue(args[0], "zip");
			if ("true".equals(value)) {
				flagZip = true;
			}
			value = getValue(args[0], "liver");
			if (value != null && !value.isEmpty()) {
				liver = value;
			}
			value = getValue(args[0], "id");
			if (value != null && !value.isEmpty()) {
				shortId = value;
			}
			value = getValue(args[0], "qn");
			if (value != null) {// && !value.isEmpty()
				qn = value;
			}
			value = getValue(args[0], "retry");
			if (value != null && !value.isEmpty()) {
				maxFailCnt = Integer.parseInt(value);
			}
			value = getValue(args[0], "fileSize"); // 单位： MB
			if (value != null && !value.isEmpty()) {
				splitFileSize = Long.parseLong(value) * 1024 * 1024;
			}
			value = getValue(args[0], "filePeriod"); // 单位：min
			if (value != null && !value.isEmpty()) {
				splitRecordPeriod = Long.parseLong(value) * 60 * 1000;
			}
			value = getValue(args[0], "proxy"); // http(s)代理 e.g. 127.0.0.1:8888
			if (value != null && !value.isEmpty()) {
				String argus[] = value.split(":");
				System.setProperty("proxyHost", argus[0]);
				System.setProperty("proxyPort", argus[1]);
			}
			value = getValue(args[0], "socksProxy"); // socks代理 e.g. 127.0.0.1:1080
			if (value != null && !value.isEmpty()) {
				String argus[] = value.split(":");
				System.setProperty("socksProxyHost", argus[0]);
				System.setProperty("socksProxyPort", argus[1]);
			}
			value = getValue(args[0], "trustAllCert"); // 信任所有SSL证书
			if (value != null && !value.isEmpty()) {
				if ("true".equals(value)) {
					try {
						HttpsURLConnection.setDefaultSSLSocketFactory(TrustAllCertSSLUtil.getFactory());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			value = getValue(args[0], "saveFolder");
			if (value != null && !value.isEmpty()) {
				saveFolder = value;
			}
			value = getValue(args[0], "fileName");
			if (value != null && !value.isEmpty()) {
				fileName = value;
			}
		}

		System.out.println(liver + " 直播录制 version " + version);

		// 如果没有传入房间号，等待输入房间号
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		if (shortId == null) {
			System.out.println("请输入房间号(直播网址是https://xxx.com/xxx，那么房间号就是xxx)");
			while (true) {
				String line = reader.readLine();
				shortId = line;
				break;
			}
		}

		// 加载cookies
		String cookie = null;
		try {
			BufferedReader buReader = new BufferedReader(new FileReader(liver + "-cookie.txt"));
			cookie = buReader.readLine();
			buReader.close();
			// Logger.println(cookie);
		} catch (Exception e) {
			// e.printStackTrace();
		}
		final String fcookie = cookie;

		RoomDealer roomDealer = getRoomDealer(liver);
		if (cookie != null) {
			roomDealer.setCookie(cookie);
		}
		// 获取房间信息
		RoomInfo roomInfo = roomDealer.getRoomInfo(shortId);

		if (roomInfo == null) {
			System.err.println("解析失败！！");
		}
		// 查看是否在线
		if (roomInfo.getLiveStatus() != 1) {
			System.out.println("当前没有在直播");
			return;
		}

		// 输入清晰度后，获得直播视频地址
		if (qn == null) {
			System.out.println("请输入清晰度代号(:之前的内容，不含空格)");
			qn = reader.readLine();
		}
		// String url = roomDealer.getLiveUrl(roomInfo.getRoomId(),
		// roomInfo.getAcceptQuality()[0]);
		String url = roomDealer.getLiveUrl((roomInfo.getRoomId()), "" + qn, roomInfo.getRemark(), cookie);
		Logger.println(url);

		// 开始录制
		new Thread(new Runnable() {
			@Override
			public void run() {
				System.out.println("开始录制，输入stop停止录制");
				List<String> fileList = new ArrayList<String>(); // 用于存放
				record(roomDealer, roomInfo, url, fileList);

				while (true) {
					if (roomDealer.util.getStatus() == StatusEnum.STOP && flagSplit) {
						// 判断当前状态 如果文件超过配置大小，那么重命名后重新录制
						// 重置状态
						roomDealer.util.init();
						flagSplit = false;
						failCnt = 0;
						System.out.println("文件大小或录制时长超过阈值，重新尝试录制");
						String url = roomDealer.getLiveUrl((roomInfo.getRoomId()), "" + qn, roomInfo.getRemark(),
								fcookie);
						Logger.println(url);
						record(roomDealer, roomInfo, url, fileList);
					} else if (roomDealer.util.getStatus() == StatusEnum.FAIL && maxFailCnt >= failCnt) {
						// 判断当前状态 如果异常连接导致失败，那么重命名后重新录制
						failCnt++;
						System.out.println("连接异常，重新尝试录制");
						String url = roomDealer.getLiveUrl((roomInfo.getRoomId()), "" + qn, roomInfo.getRemark(),
								fcookie);
						Logger.println(url);
						record(roomDealer, roomInfo, url, fileList);
					} else {
						break;
					}
				}

				System.out.println("下载停止");
				if (".flv".equals(roomDealer.getType())) {
					if (autoCheck) {
						try {
							for (String path : fileList) {
								System.out.println("校对时间戳开始...");
								new FlvChecker().check(path, deleteOnchecked, splitScriptTagsIfCheck);
								System.out.println("校对时间戳完毕。");
							}
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					if(flagZip) {
						// 获取所有要压缩的文件
						List<File> files2Zip = new ArrayList<File>(); // 用于存放
						for (String path : fileList) {
							// 如果不校正时间戳, 直接加入列表即可
							if(!autoCheck) {
								files2Zip.add(new File(path));
							}else {
							// 如果校正时间戳，一个个文件名进行尝试，直至不存在
								for(int count = 0; ; count++) {
									String path_i = path.replaceFirst(".flv$", "-checked" + count +".flv");
									File f = new File(path_i);
									if(f.exists())
										files2Zip.add(f);
									else
										break;
								}
							}
						}
						// 压缩文件
						ZipUtil.zipFiles(files2Zip, fileList.get(0) + ".zip");
					}
				} else if (".ts".equals(roomDealer.getType())) {
//					System.out.println("正在合并...");
//					M3u8Downloader m3u8 = new M3u8Downloader();
//					m3u8.merge(file, roomDealer.currentIndex, true);
//					// 删除可能存在的part文件
//					String part = String.format("%s-%d%s.part", filename, roomDealer.currentIndex,
//							roomDealer.getType());
//					new File(file.getParent(), part).delete();
//					// 将ts文件移动到上一层文件夹
//					dstFile.renameTo(new File(dstFile.getParentFile().getParentFile(), dstFile.getName()));
//					dstFile.getParentFile().delete();
//					System.out.println("合并结束...");
				}

				System.exit(1);
			}
		}, "thread-record").start();

		// 输出进度，超过指定大小后重新开始一次
		new Thread(new Runnable() {
			long beginTime = System.currentTimeMillis();
			long fileBeginTime = beginTime;

			@Override
			public void run() {
				while (true) {
					try {
						Thread.sleep(10000); // 每10s汇报一次情况
					} catch (InterruptedException e) {
					}
					// 查看当前文件大小, 如果超过阈值，那么重新开始新的录制
					if (splitFileSize != 0 && roomDealer.util.getDownloadedFileSize() >= splitFileSize) {
						fileBeginTime = System.currentTimeMillis();
						flagSplit = true;
						roomDealer.stopRecord();
					}
					// 查看当前录制时长, 如果超过阈值，那么重新开始新的录制
					if (splitRecordPeriod != 0 && System.currentTimeMillis() - fileBeginTime >= splitRecordPeriod) {
						fileBeginTime = System.currentTimeMillis();
						flagSplit = true;
						roomDealer.stopRecord();
					}
					if (".flv".equals(roomDealer.getType())) {
						if (roomDealer.util.getStatus() == StatusEnum.DOWNLOADING) {
							int period = (int) ((System.currentTimeMillis() - beginTime) / 1000);
							int hour = period / 3600;
							int minute = period / 60 - hour * 60;
							int second = period - minute * 60 - hour * 3600;
							if (hour == 0) {
								System.out.printf("已经录制了%dm%ds, ", minute, second);
							} else {
								System.out.printf("已经录制了%dh%dm%ds, ", hour, minute, second);
							}
							System.out.println(
									"当前进度： " + RoomDealer.transToSizeStr(roomDealer.util.getDownloadedFileSize()));
						} else {
							System.out.println("正在处理，请稍等 ");
						}
					} else {
						int period = (int) ((System.currentTimeMillis() - beginTime) / 1000);
						int hour = period / 3600;
						int minute = period / 60 - hour * 60;
						int second = period - minute * 60 - hour * 3600;
						if (hour == 0) {
							System.out.printf("已经录制了%dm%ds, ", minute, second);
						} else {
							System.out.printf("已经录制了%dh%dm%ds, ", hour, minute, second);
						}
						System.out.println("当前进度： " + RoomDealer
								.transToSizeStr(roomDealer.util.getTotalFileSize() * roomDealer.currentIndex));
					}

				}
			}
		}, "thread-monitoring").start();
		String line;
		while ((line = reader.readLine()) != null) {
			if (line.startsWith("stop") || line.startsWith("q")) {
				roomDealer.stopRecord();
				reader.close();
				break;
			} else {
				System.out.println("输入stop 或 q 停止录制");
			}
		}
	}

	private static void record(RoomDealer roomDealer, RoomInfo roomInfo, String url, List<String> fileList) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH.mm");
		// "{name}-{shortId} 的{liver}直播{startTime}-{seq}";
		String realName = fileName.replace("{name}", roomInfo.getUserName())
				.replace("{shortId}", roomInfo.getShortId())
				.replace("{roomId}", roomInfo.getRoomId())
				.replace("{liver}", liver)
				.replace("{startTime}", sdf.format(new Date()))
				.replace("{seq}", "" + fileList.size())
				.replaceAll("[\\\\|\\/|:\\*\\?|<|>|\\||\\\"$]", ".");
		// 如果saveFolder不为空
		if (saveFolder != null) {
			roomDealer.util.setSavePath(saveFolder);
		}
		roomDealer.startRecord(url, realName, roomInfo.getShortId());// 此处一直堵塞， 直至停止
		File file = roomDealer.util.getFileDownload();
		File partFile = new File(file.getParent(), realName + roomDealer.getType() + ".part");
		File dstFile = new File(file.getParent(), realName + roomDealer.getType());
		// 将可能的.flv.part文件重命名为.flv
		partFile.renameTo(dstFile);
		// 加入已下载列表
		fileList.add(dstFile.getAbsolutePath());
	}

	/**
	 * 从参数字符串中取出值 "key1=value1&key2=value2 ..."
	 * 
	 * @param param
	 * @param key
	 * @return
	 */
	private static String getValue(String param, String key) {
		Pattern pattern = Pattern.compile(key + "=([^&]*)");
		Matcher matcher = pattern.matcher(param);
		if (matcher.find()) {
			return matcher.group(1);
		}
		return null;
	}

	/**
	 * 获取正确的视频录制器
	 * 
	 * @param liver
	 * @return
	 */
	private static RoomDealer getRoomDealer(String liver) {
		Class<?> clazz = PackageScanLoader.dealerClazz.get(liver);
		try {
			return (RoomDealer) clazz.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("当前没有发现合适的视频录制器： " + liver);
			RoomDealer dealer = new RoomDealerBilibili();
			return dealer;
		}
	}

}
