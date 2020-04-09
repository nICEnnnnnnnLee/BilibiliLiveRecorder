package nicelee.bilibili.threads;


import nicelee.bilibili.Config;
import nicelee.bilibili.enums.StatusEnum;
import nicelee.bilibili.live.RoomDealer;

public class ThMonitor extends Thread {

	
	long beginTime;
	long fileBeginTime;
	RoomDealer roomDealer;
	
	public ThMonitor(RoomDealer roomDealer) {
		this.roomDealer = roomDealer;
		this.beginTime = System.currentTimeMillis();
		this.fileBeginTime = beginTime;
		this.setName("thread-monitoring");
		this.setDaemon(true);
	}
	
	@Override
	public void run() {
		while (true) {
			try {
				Thread.sleep(10000); // 每10s汇报一次情况
			} catch (InterruptedException e) {
			}
			// 查看当前文件大小, 如果超过阈值，那么重新开始新的录制
			if (Config.splitFileSize != 0 && roomDealer.util.getDownloadedFileSize() >= Config.splitFileSize) {
				fileBeginTime = System.currentTimeMillis();
				Config.flagSplit = true;
				roomDealer.stopRecord();
			}
			// 查看当前录制时长, 如果超过阈值，那么重新开始新的录制
			if (Config.splitRecordPeriod != 0
					&& System.currentTimeMillis() - fileBeginTime >= Config.splitRecordPeriod) {
				fileBeginTime = System.currentTimeMillis();
				Config.flagSplit = true;
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
				} else if (roomDealer.util.getStatus() == StatusEnum.SUCCESS && !Config.flagStopAfterOffline) {
					// 主播下播后的等待时间
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
}
