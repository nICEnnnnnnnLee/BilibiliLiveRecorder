package nicelee.bilibili;

import java.util.concurrent.locks.Lock;
import nicelee.bilibili.live.RoomDealer;

public class SignalHandler extends Thread {

	Lock lock;
	RoomDealer roomDealer;

	public SignalHandler(Lock lock, RoomDealer roomDealer) {
		this.lock = lock;
		this.roomDealer = roomDealer;
		this.setDaemon(true);
	}

	@Override
	public void run() {
		Main.flagSplit = false;
		roomDealer.stopRecord();
		lock.lock();// 等待录制完毕，时间戳处理完成
		lock.unlock();
	}

}
