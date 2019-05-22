package nicelee.bilibili;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;

import nicelee.bilibili.enums.StatusEnum;
import nicelee.bilibili.live.FlvChecker;
import nicelee.bilibili.live.RoomDealer;
import nicelee.bilibili.live.domain.RoomInfo;
import nicelee.bilibili.util.Logger;

public class Main {
	
	static boolean autoCheck = true;
	public static void main(String[] args) throws IOException {
		//args = new String[]{"debug"};
		if(args != null && args.length >= 1) {
			if(args[0].contains("debug")) {
				Logger.debug = true;
			}
			if(args[0].contains("noCheck")) {
				autoCheck = false;
			}
		}else {
			Logger.debug = false;
		}
		// 等待输入房间号
		System.out.println("bilibili 直播录制 version v1.3");
		System.out.println("请输入房间号(直播网址是https://live.bilibili.com/xxx，那么房间号就是xxx)");
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		long shortId;
		while (true) {
			String line = reader.readLine();
			try {
				shortId = Long.parseLong(line);
				break;
			} catch (Exception e) {
				System.out.println("请输入正确的房间号！！");
			}
		}

		RoomDealer roomDealer = new RoomDealer();
		// 获取房间信息
		RoomInfo roomInfo = roomDealer.getRoomInfo(shortId);

		// 查看是否在线
		if (roomInfo != null && roomInfo.getLiveStatus() != 1) {
			System.out.println("当前没有在直播");
			return;
		}

		// 获得直播视频地址
		String url = roomDealer.getLiveUrl(roomInfo.getRoomId(), roomInfo.getAcceptQuality()[0]);
		Logger.println(url);

		// 开始录制
		new Thread(new Runnable() {
			@Override
			public void run() {
				System.out.println("开始录制，输入stop停止录制");
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH.mm");
				String filename = String.format("%s-%d 的直播 %s.flv", 
						roomInfo.getUserName().replaceAll("[\\\\|\\/|:\\*\\?|<|>|\\||\\\"$]", "."),
						roomInfo.getShortId(),
						sdf.format(new Date()));
				roomDealer.startRecord(url, filename, roomInfo.getShortId());
				// 此处一直堵塞， 直至停止
				File file = roomDealer.util.getFileDownload();
				File partFile = new File(file.getParent(), filename + ".part");
				File flvFile = new File(file.getParent(), filename);
				System.out.println("下载完毕");
				
				if(autoCheck) {
					try {
						System.out.println("校对时间戳开始...");
						if(partFile.exists()) { // 人工停止
							FlvChecker.check(partFile.getAbsolutePath());
						}else {	// 主播下播，正常结束
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
					if(roomDealer.util.getStatus() == StatusEnum.DOWNLOADING) {
						int period = (int) ((System.currentTimeMillis() - beginTime) / 1000);
						System.out.print("已经录制了 " + period);
						System.out.println("s, 当前进度： " + RoomDealer.transToSizeStr(roomDealer.util.getDownloadedFileSize()));
					}else {
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
			}else {
				System.out.println("输入stop 或 q 停止录制");
			}
		}
	}
}
