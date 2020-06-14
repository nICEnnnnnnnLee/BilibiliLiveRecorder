package nicelee.bilibili.threads;


import java.io.BufferedReader;
import java.io.IOException;

import nicelee.bilibili.Config;
import nicelee.bilibili.live.RoomDealer;

public class ThCommand extends Thread {

	RoomDealer roomDealer;
	BufferedReader reader;
	Thread thRecord;
	
	public ThCommand(RoomDealer roomDealer, Thread thRecord, BufferedReader reader) {
		this.roomDealer = roomDealer;
		this.thRecord = thRecord;
		this.reader = reader;
		this.setName("thread-command");
		this.setDaemon(true);
	}
	
	@Override
	public void run() {
		String line;
		try {
			while ((line = reader.readLine()) != null) {
				if (line.startsWith("stop") || line.startsWith("q")) {
					Config.flagStopAfterOffline = true;
					roomDealer.stopRecord();
					reader.close();
					thRecord.interrupt();
					break;
				} else {
					System.out.println("输入stop 或 q 停止录制");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
