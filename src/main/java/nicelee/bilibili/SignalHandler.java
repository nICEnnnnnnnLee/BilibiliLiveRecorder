package nicelee.bilibili;

import java.util.concurrent.locks.Lock;
import nicelee.bilibili.live.RoomDealer;

public class SignalHandler extends Thread {

	Lock lockOfRecord;
	Lock lockOfCheck;
	RoomDealer roomDealer;

	public SignalHandler(Lock lockOfRecord, Lock lockOfCheck, RoomDealer roomDealer) {
		this.lockOfRecord = lockOfRecord;
		this.lockOfCheck = lockOfCheck;
		this.roomDealer = roomDealer;
		this.setDaemon(true);
	}

	@Override
	public void run() {
		System.out.println("SignalHandler is running");
		Config.flagSplit = false;
		Config.flagStopAfterOffline = true;
		roomDealer.stopRecord();
		Main.thRecord.interrupt();
		lockOfRecord.lock();// 等待录制完毕，
		try {
			sleep(1000); // 确保文件处理线程能够优先获得锁
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		lockOfCheck.lock();// 等待文件处理完成
		lockOfCheck.unlock();
		lockOfRecord.unlock();
	}

}
