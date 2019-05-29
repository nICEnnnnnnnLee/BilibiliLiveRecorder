package nicelee.bilibili;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nicelee.bilibili.enums.StatusEnum;
import nicelee.bilibili.live.FlvChecker;
import nicelee.bilibili.live.RoomDealer;
import nicelee.bilibili.live.domain.RoomInfo;
import nicelee.bilibili.live.impl.RoomDealerBilibili;
import nicelee.bilibili.util.Logger;

public class Main {

	final static String version = "v1.4";
	static boolean autoCheck;
	static String liver;
	static Long shortId;
	static Integer qn;
	/**
	 * 程序入口
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		//args = new String[]{"debug=false&liver=bili"};
		//args = new String[]{"debug=false&check=false&liver=douyu&id=233233&qn=0"};
		// 初始化默认值
		autoCheck = true;
		Logger.debug = false;
		liver = "bili";
		
		// 根据参数初始化值
		if (args != null && args.length >= 1) {
			String value = getValue(args[0], "check");
			if ("false".equals(value)) {
				autoCheck = false;
			}
			value = getValue(args[0], "debug");
			if ("true".equals(value)) {
				Logger.debug = true;
			}
			value = getValue(args[0], "liver");
			if(value != null && !value.isEmpty()) {
				liver = value;
			}
			value = getValue(args[0], "id");
			if(value != null && !value.isEmpty()) {
				shortId = Long.parseLong(value);
			}
			value = getValue(args[0], "qn");
			if(value != null && !value.isEmpty()) {
				qn = Integer.parseInt(value);
			}
		}
		
		System.out.println(liver + " 直播录制 version " + version);
		
		
		// 如果没有传入房间号，等待输入房间号
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		if(shortId == null) {
			System.out.println("请输入房间号(直播网址是https://xxx.com/xxx，那么房间号就是xxx)");
			while (true) {
				String line = reader.readLine();
				try {
					shortId = Long.parseLong(line);
					break;
				} catch (Exception e) {
					System.out.println("请输入正确的房间号！！");
				}
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
			//e.printStackTrace();
		}
		
		RoomDealer roomDealer = getRoomDealer(liver);
		// 获取房间信息
		RoomInfo roomInfo = roomDealer.getRoomInfo(shortId);
		
		// 查看是否在线
		if (roomInfo != null && roomInfo.getLiveStatus() != 1) {
			System.out.println("当前没有在直播");
			return;
		}
		
		// 输入清晰度后，获得直播视频地址
		if(qn == null) {
			System.out.println("请输入清晰度代号");
			qn = 0;
			try {
				qn = Integer.parseInt(reader.readLine());
			} catch (Exception e) {
				System.out.println("清晰度有误，尝试使用0代替");
			}
		}
		// String url = roomDealer.getLiveUrl(roomInfo.getRoomId(),
		// roomInfo.getAcceptQuality()[0]);
		String url = roomDealer.getLiveUrl(roomInfo.getRoomId(), "" + qn, roomInfo.getRemark(), cookie);
		Logger.println(url);
		
		// 开始录制
		new Thread(new Runnable() {
			@Override
			public void run() {
				System.out.println("开始录制，输入stop停止录制");
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH.mm");
				String filename = String.format("%s-%d 的%s直播 %s.flv",
						roomInfo.getUserName().replaceAll("[\\\\|\\/|:\\*\\?|<|>|\\||\\\"$]", "."),
						roomInfo.getShortId(), liver, sdf.format(new Date()));
				roomDealer.startRecord(url, filename, roomInfo.getShortId());
				// 此处一直堵塞， 直至停止
				File file = roomDealer.util.getFileDownload();
				File partFile = new File(file.getParent(), filename + ".part");
				File flvFile = new File(file.getParent(), filename);
				System.out.println("下载完毕");
				
				if (autoCheck) {
					try {
						System.out.println("校对时间戳开始...");
						if (partFile.exists()) { // 人工停止
							FlvChecker.check(partFile.getAbsolutePath());
						} else { // 主播下播，正常结束
							FlvChecker.check(flvFile.getAbsolutePath());
						}
						System.out.println("校对时间戳完毕。");
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				// 将后缀.part去掉
				partFile.renameTo(flvFile);
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
					if (roomDealer.util.getStatus() == StatusEnum.DOWNLOADING) {
						int period = (int) ((System.currentTimeMillis() - beginTime) / 1000);
						System.out.print("已经录制了 " + period);
						System.out.println(
								"s, 当前进度： " + RoomDealer.transToSizeStr(roomDealer.util.getDownloadedFileSize()));
					} else {
						System.out.print("正在处理时间戳，请稍等 ");
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
		return "";
	}
	
	/**
	 * 获取正确的视频录制器
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
