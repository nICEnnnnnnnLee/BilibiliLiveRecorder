package nicelee.bilibili.threads;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import nicelee.bilibili.Config;
import nicelee.bilibili.Main;
import nicelee.bilibili.SignalHandler;
import nicelee.bilibili.enums.StatusEnum;
import nicelee.bilibili.live.RoomDealer;
import nicelee.bilibili.live.domain.RoomInfo;
import nicelee.bilibili.plugin.Plugin;
import nicelee.bilibili.util.Logger;

public class ThRecord extends Thread {

	RoomDealer roomDealer;
	RoomInfo roomInfo;
	String cookie;
	Plugin plugin;

	Lock lockOfRecord;
	Lock lockOfCheck;

	public ThRecord(RoomDealer roomDealer, RoomInfo roomInfo, String cookie, Plugin plugin) {
		this.roomInfo = roomInfo;
		this.roomDealer = roomDealer;
		this.cookie = cookie;
		this.plugin = plugin;
		this.setName("thread-Record");
		this.lockOfRecord = new ReentrantLock(true);
		this.lockOfCheck = new ReentrantLock(true);
	}

	@Override
	public void run() {

		String url = roomDealer.getLiveUrl((roomInfo.getRoomId()), "" + Config.qn, roomInfo.getRemark(), cookie);
		Logger.println(url);
		System.out.println("开始录制，输入stop停止录制");
		List<String> fileList = new ArrayList<String>(); // 用于存放录制产生的初始flv文件
		// 在开启录制之前，添加对退出信号的捕捉处理
		lockOfRecord.lock();
		Runtime.getRuntime().addShutdownHook(new SignalHandler(lockOfRecord, lockOfCheck, roomDealer));
		record(roomDealer, roomInfo, url, fileList);

		while (true) {
			if ((roomDealer.util.getStatus() == StatusEnum.STOP && Config.flagSplit)
					|| (roomDealer.util.getStatus() == StatusEnum.SUCCESS && !Config.flagStopAfterOffline)) {
				// 判断当前状态
				if (roomDealer.util.getStatus() == StatusEnum.STOP) {
					System.out.println("文件大小或录制时长超过阈值，重新尝试录制");
				} else {
					System.out.println("主播下播，等待下一次录制");
					// 另起线程处理媒体文件
					Thread th = new ThCheckMedia(roomDealer, fileList, lockOfCheck, plugin);
					fileList = new ArrayList<String>();
					th.start();
					lockOfRecord.unlock();
					try {
						System.out.println(Config.retryAfterMinutes + "分钟左右后重试");
						sleep((long) (Config.retryAfterMinutes * 60000));
					} catch (InterruptedException e) {
						break;
					}
					roomInfo = Main.getRoomInfo(roomDealer);
					if (roomInfo.getLiveStatus() != 1)
						break;
					lockOfRecord.lock();
				}

				// 重置状态
				roomDealer.util.init();
				Config.flagSplit = false;
				Config.failCnt = 0;
				url = roomDealer.getLiveUrl((roomInfo.getRoomId()), "" + Config.qn, roomInfo.getRemark(), cookie);
				Logger.println(url);
				record(roomDealer, roomInfo, url, fileList);
			} else if (roomDealer.util.getStatus() == StatusEnum.FAIL && Config.maxFailCnt >= Config.failCnt) {
				// 判断当前状态 如果异常连接导致失败，那么重命名后重新录制
				Config.failCnt++;
				System.out.println("连接异常，1min后重新尝试录制");
				try {
					sleep(60000);
				} catch (InterruptedException e) {
					break;
				}
				url = roomDealer.getLiveUrl((roomInfo.getRoomId()), "" + Config.qn, roomInfo.getRemark(), cookie);
				Logger.println(url);
				record(roomDealer, roomInfo, url, fileList);
			} else {
				break;
			}
		}

		System.out.println("下载停止");
		if (fileList.size() > 0) {
			Thread th = new ThCheckMedia(roomDealer, fileList, lockOfCheck, plugin);
			th.start();
		}
		try {
			lockOfRecord.unlock();
		} catch (Exception e) {
		}
	}

	static void record(RoomDealer roomDealer, RoomInfo roomInfo, String url, List<String> fileList) {
		SimpleDateFormat sdf = new SimpleDateFormat(Config.timeFormat);
		// "{name}-{shortId} 的{liver}直播{startTime}-{seq}";
		String realName = Config.fileName.replace("{name}", roomInfo.getUserName())
				.replace("{shortId}", roomInfo.getShortId()).replace("{roomId}", roomInfo.getRoomId())
				.replace("{liver}", Config.liver).replace("{startTime}", sdf.format(new Date()))
				.replace("{seq}", "" + fileList.size()).replaceAll("[\\\\|\\/|:\\*\\?|<|>|\\||\\\"$]", ".");
		// 如果saveFolder不为空
		if (Config.saveFolder != null) {
			roomDealer.util.setSavePath(Config.saveFolder);
		}
		roomDealer.startRecord(url, realName, roomInfo.getShortId());// 此处一直堵塞， 直至停止
		File file = roomDealer.util.getFileDownload();

		File partFile = new File(file.getParent(), realName + roomDealer.getType() + ".part");
		File completeFile = new File(file.getParent(), realName + roomDealer.getType());
		realName = realName.replace("{endTime}", sdf.format(new Date()));
		File dstFile = new File(file.getParent(), realName + roomDealer.getType());

		if (partFile.exists())
			partFile.renameTo(dstFile);
		else
			completeFile.renameTo(dstFile);

		// 加入已下载列表
		fileList.add(dstFile.getAbsolutePath());
	}
}
