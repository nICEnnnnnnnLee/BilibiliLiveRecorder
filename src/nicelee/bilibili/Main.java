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

import nicelee.bilibili.enums.StatusEnum;
import nicelee.bilibili.live.FlvChecker;
import nicelee.bilibili.live.RoomDealer;
import nicelee.bilibili.live.domain.RoomInfo;
import nicelee.bilibili.live.impl.RoomDealerBilibili;
import nicelee.bilibili.util.Logger;

public class Main {

	final static String version = "v1.8";
	static boolean autoCheck;
	static boolean deleteOnchecked;
	static String liver;
	static String shortId;
	static String qn;
	static int maxFailCnt;

	/**
	 * 程序入口
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
//		 args = new String[]{"debug=false&liver=bili&id=221602&qn=10000&delete=false&check=false"};  			// 清晰度全部可选，可不需要cookie
//		 args = new String[]{"debug=true&check=true&liver=douyu&id=6566716"};  	// 清晰度全部可选，但部分高清需要cookie 
//		args = new String[]{"debug=true&check=true&liver=kuaishou&id=mianf666&qn=0&delete=false"};  					// 清晰度全部可选，可不需要cookie asd199895
//		args = new String[]{"debug=true&check=false&liver=huya&id=660137"}; 				// 清晰度全部可选，可不需要cookie 
//		args = new String[]{"debug=true&check=true&liver=yy&id=28581146&qn=1"}; 		// 只支持默认清晰度 54880976
//		args = new String[] { "debug=true&check=true&liver=zhanqi&id=90god" }; 			// 清晰度全部可选，可不需要cookie 90god huashan ydjs
//		args = new String[] { "debug=true&check=true&liver=huajiao&id=278581432&qn=1" }; // 只支持默认清晰度(似乎只有一种清晰度)
		// 初始化默认值
		autoCheck = true;
		deleteOnchecked = true;
		Logger.debug = false;
		liver = "bili";
		maxFailCnt = 5;
		
		// 根据参数初始化值
		if (args != null && args.length >= 1) {
			String value = getValue(args[0], "check");
			if ("false".equals(value)) {
				autoCheck = false;
			}
			value = getValue(args[0], "delete");
			if ("false".equals(value)) {
				deleteOnchecked = false;
			}
			value = getValue(args[0], "debug");
			if ("true".equals(value)) {
				Logger.debug = true;
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
			if (value != null ) {//&& !value.isEmpty()
				qn = value;
			}
			value = getValue(args[0], "retry");
			if (value != null && !value.isEmpty()) {
				maxFailCnt = Integer.parseInt(value);
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
			Logger.println(cookie);
		} catch (Exception e) {
			// e.printStackTrace();
		}
		final String fcookie = cookie;

		RoomDealer roomDealer = getRoomDealer(liver);
		if(cookie != null) {
			roomDealer.setCookie(cookie);
		}
		// 获取房间信息
		RoomInfo roomInfo = roomDealer.getRoomInfo(shortId);

		// 查看是否在线
		if (roomInfo != null && roomInfo.getLiveStatus() != 1) {
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
				List<String> fileList = new ArrayList<String>(); //用于存放
				record(roomDealer, roomInfo, url, fileList);
				
				// 判断当前状态 如果异常连接导致失败，那么重命名后重新录制
				while(roomDealer.util.getStatus() == StatusEnum.FAIL && maxFailCnt >= fileList.size()) {
					System.out.println("连接异常，重新尝试录制");
					String url = roomDealer.getLiveUrl((roomInfo.getRoomId()), "" + qn, roomInfo.getRemark(), fcookie);
					Logger.println(url);
					record(roomDealer, roomInfo, url, fileList);
				}
				
				System.out.println("下载停止");
				if (".flv".equals(roomDealer.getType())) {
					if (autoCheck) {
						try {
							for(String path: fileList) {
								System.out.println("校对时间戳开始...");
								new FlvChecker().check(path, deleteOnchecked);
								System.out.println("校对时间戳完毕。");
							}
						} catch (IOException e) {
							e.printStackTrace();
						}
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

		// 输出进度
		new Thread(new Runnable() {
			long beginTime = System.currentTimeMillis();

			@Override
			public void run() {
				while (true) {
					try {
						Thread.sleep(10000); // 每10s汇报一次情况
					} catch (InterruptedException e) {
					}
					if (".flv".equals(roomDealer.getType())) {
						if (roomDealer.util.getStatus() == StatusEnum.DOWNLOADING) {
							int period = (int) ((System.currentTimeMillis() - beginTime) / 1000);
							int hour = period / 3600;
							int minute = period / 60 - hour*60;
							int second = period - minute*60 - hour*3600;
							if(hour==0) {
								System.out.printf("已经录制了%dm%ds, ", minute, second);
							}else {
								System.out.printf("已经录制了%dh%dm%ds, ", hour, minute, second);
							}
							System.out.println(
									"当前进度： " + RoomDealer.transToSizeStr(roomDealer.util.getDownloadedFileSize()));
						} else {
							System.out.println("正在处理时间戳，请稍等 ");
						}
					} else {
						int period = (int) ((System.currentTimeMillis() - beginTime) / 1000);
						int hour = period / 3600;
						int minute = period / 60 - hour*60;
						int second = period - minute*60 - hour*3600;
						if(hour==0) {
							System.out.printf("已经录制了%dm%ds, ", minute, second);
						}else {
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
		String filename = String.format("%s-%s 的%s直播 %s-%d",
				roomInfo.getUserName().replaceAll("[\\\\|\\/|:\\*\\?|<|>|\\||\\\"$]", "."),
				roomInfo.getShortId(), liver, sdf.format(new Date()), fileList.size());
		roomDealer.startRecord(url, filename, roomInfo.getShortId());// 此处一直堵塞， 直至停止
		File file = roomDealer.util.getFileDownload();
		File partFile = new File(file.getParent(), filename + roomDealer.getType() + ".part");
		File dstFile = new File(file.getParent(), filename + roomDealer.getType());
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
