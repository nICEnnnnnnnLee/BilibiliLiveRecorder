package nicelee.bilibili.live;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import nicelee.bilibili.util.Logger;

/**
 * 原理
 * https://www.cnblogs.com/lidabo/p/9018548.html
 */
public class FlvChecker2 {

	
	public static void main(String[] args) throws IOException {
		//args = new String[] {"D:\\Workspace\\javaweb-springboot\\BilibiliLiveRecord\\download\\test.flv"};
		args = new String[] {"D:\\Workspace\\javaweb-springboot\\BilibiliLiveRecord\\release\\download\\test....flv"};
		
		FlvChecker2 fChecker = new FlvChecker2();
		if(args != null && args.length>=1) {
			System.out.println("校对时间戳开始...");
			fChecker.check(args[0]);
//			fChecker.checkFromEnd(args[0]);
			System.out.println("校对时间戳完毕。");
		}else {
			System.out.println("请输入正确的文件路径");
		}
	}

	/**
	 * 从头部开始Check, 重新锚定时间戳, 将最后一帧(不管是否完整)去掉
	 * 
	 * @param path
	 * @throws IOException
	 */
	private int duration;
	// 用于统计时间戳
	private int firstTimeStamp = -1, lastTimestamp = -1, cnt = 0;
	// 用于统计frame序号
	private int frameNo = 0;
	public void check(String path) throws IOException {
		Logger.println("校对时间戳开始...");
		File file = new File(path);
		RandomAccessFile raf = new RandomAccessFile(file, "rw");
		// 跳过头部
		raf.skipBytes(9);
		// 用于排除无效尾巴帧
		long currentLength = 9L, latsValidLength = currentLength;
		try {
			int remain = 40;
			boolean isFirstScriptTag = true;
			int timestamp=0;
			while (true && remain>=0) {//&& remain>=0
				remain--;
				// 读取前一个tag size
				int predataSize = readBytesToInt(raf, 4);
				latsValidLength = currentLength; currentLength = raf.getFilePointer();
				Logger.print("前一个长度为：" + predataSize);
				// 读取tag
				// tag 类型
				int tagType = raf.read();
				Logger.print("当前tag 类型为：");
				if (tagType == 8) {
					Logger.print("audio");
				} else if (tagType == 9) {
					Logger.print("video");
				} else if (tagType == 18) {
					Logger.print("scripts");
					if(isFirstScriptTag) {
						isFirstScriptTag = false;
						if(timestamp > 30*60*1000) {
						}
					}else{
//						// 当有第二个scripts脚本时
//						// 1. 从第二个script tag起始新创建一份文件
//						File newFile = new File(file.getParentFile(), file.getName().replaceFirst(".flv$", "..flv"));
//						RandomAccessFile newRaf = new RandomAccessFile(newFile, "rw");
//						//  复制 header
//						byte[] header = new byte[13];
//						raf.seek(0);
//						raf.read(header);
//						newRaf.write(header);
//						// 复制内容
//						raf.seek(currentLength);
//						byte[] buffer = new byte[1024*1024];
//						int len = raf.read(buffer);
//						while(len > 0) {
//							newRaf.write(buffer, 0, len);
//							len = raf.read(buffer);
//						}
//						newRaf.close();
//						// 2. 处理新文件
//						new FlvChecker().check(newFile.getAbsolutePath());
//						// 3. 将旧文件多余部分剪掉
//						raf.setLength(latsValidLength);
						break;
					}
				} else {
					Logger.print(tagType);
					raf.setLength(latsValidLength);
					break;
				}
				// tag data size 3个字节。表示tag data的长度。从streamd id 后算起。
				int dataSize = readBytesToInt(raf, 3);
				Logger.print(" ,当前tag data 长度为：" + dataSize);
				// 时间戳 3
				timestamp = readBytesToInt(raf, 3);
				int timestampEx = raf.read() << 24;
				timestamp += timestampEx;
				// 回滚时间戳
				raf.seek(raf.getFilePointer() - 4);
				// 分情况讨论时间戳
				switch(tagType) {
					case 8 :// audio
						dealTimestamp(raf, timestamp, 0);
						break;
					case 9 :// video
						dealTimestamp(raf, timestamp, 1);
						break;
					case 18: //scripts
						dealTimestamp(raf, timestamp, 2);
						break;
				}
				// timestampEx 扩展保留 1个字节 + stream id 3个字节。总是0
				//raf.write(0);
				// 跳过数据
				raf.skipBytes(3 + dataSize);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		Logger.println();
		duration = lastTimestamp - firstTimeStamp;
		Logger.println("firstTimeStamp 为：" +  firstTimeStamp);
		Logger.println("lastTimestamp 为：" + lastTimestamp);
		Logger.println("duration 为：" + duration);
		Logger.println("currentLength 为：" + currentLength);
		raf.close();
		double dura = duration;
		dura = dura / 1000;
		changeDuration(path, dura);
	}

	/**
	 * 处理音/视频时间戳
	 * @param raf
	 * @param timestamp
	 * @throws IOException
	 */
	private void dealTimestamp(RandomAccessFile raf, int timestamp, int type) throws IOException {
		frameNo++;
		// 尚未找到非0 起始时间戳， 此时，一直递增
		if (firstTimeStamp < 0) { 
			firstTimeStamp = timestamp;
			lastTimestamp = timestamp;
			byte[] counts = { 0, 0, (byte) cnt };
			raf.write(counts, 0, 3);
			raf.write(0); // 高位补0
			cnt++;
			Logger.print(" ,修改前timestamps 为：" + timestamp);
			Logger.print(" ,当前timestamps 为-：" + cnt);
			Logger.println();
		}else if(timestamp - lastTimestamp > 30000 && frameNo < 100) { // 前100帧确认初始帧时间戳firstTimeStamp
			// 登记有效时间戳
			lastTimestamp = timestamp;
			// 如果中间间隔太大，那么认为起始值不对，仍然需要调整
			cnt++;
			byte[] counts = { 0, 0, (byte) cnt };
			raf.write(counts, 0, 3);
			raf.write(0); // 高位补0
			firstTimeStamp = lastTimestamp - cnt;
			Logger.print(" ,修改前timestamps 为：" + timestamp);
			Logger.print(" ,当前timestamps cnt为：" + cnt);
			Logger.println();
		}else {
			// 登记有效时间戳
			if (timestamp >= lastTimestamp && timestamp - lastTimestamp <= 30000) {
				lastTimestamp = timestamp;
			}else { // 必须确保递增
				lastTimestamp++;
			}
			// 修改当前时间戳
			int currentTime = lastTimestamp - firstTimeStamp  + cnt;
			// 低于0xffffff部分
			int lowCurrenttime = currentTime & 0xffffff;
			raf.write(int2Bytes(lowCurrenttime), 1, 3);
			// 高于0xffffff部分
			int highCurrenttime = currentTime >> 24;
			raf.write(highCurrenttime);
			Logger.print(" ,修改前timestamps 为：" + timestamp);
			Logger.print(" ,当前timestamps 为：" + currentTime);
			Logger.println();
		}
	}
	
	/**
	 * 从尾部开始Check, 单纯检查，没有对文件进行操作
	 * 
	 * @param path
	 * @throws IOException
	 */
	public static void checkFromEnd(String path) throws IOException {
		File file = new File(path);
		RandomAccessFile raf = new RandomAccessFile(file, "r");

		int firstTimeStamp = 0, lastTimestamp = 0;
		int dataSize = 0;
		try {
			long position = raf.length();
			//int remain = 20;
			while (true) {//&& remain>=0
				//remain--;
				raf.seek(position - 4);
				// 读取前一个tag size
				int predataSize = readBytesToInt(raf, 4);
				Logger.print("前一个长度为：" + predataSize);
				position -= (predataSize + 4);
				raf.seek(position);

				// 读取tag
				// tag 类型
				int tagType = raf.read();
				Logger.print("当前tag 类型为：");
				if (tagType == 8) {
					Logger.print("audio");
				} else if (tagType == 9) {
					Logger.print("video");
				} else if (tagType == 18) {
					Logger.print("scripts");
				} else {
					Logger.print(tagType);
					break;
				}
				// tag data size 3个字节。表示tag data的长度。从streamd id 后算起。
				dataSize = readBytesToInt(raf, 3);
				Logger.print(" ,当前tag data 长度为：" + dataSize);
				// 时间戳 3+1
				int timestamp = readBytesToInt(raf, 3);
				if (firstTimeStamp == 0) {
					firstTimeStamp = timestamp;
				}
				lastTimestamp = timestamp;
				Logger.print(" ,当前timestamps 为：" + timestamp);
				Logger.println();
				raf.skipBytes(1);
				// stream id 3个字节。总是0
				raf.skipBytes(3);
				// 跳过数据
				raf.skipBytes(dataSize);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		Logger.println();
		Logger.println("firstTimeStamp 为：" + firstTimeStamp);
		Logger.println("lastTimestamp 为：" + lastTimestamp);
		Logger.println("duration 为：" + (lastTimestamp - firstTimeStamp));
		raf.close();
	}

	/**
	 * @param raf
	 * @param byteLength
	 * @return
	 * @throws IOException
	 */
	private static int readBytesToInt(RandomAccessFile raf, int byteLength) throws IOException {
		byte data[] = new byte[byteLength];
		raf.read(data);
		return bytes2Int(data);
	}

	private static byte[] int2Bytes(int value) {
		byte[] byteRet = new byte[4];
		for (int i = 0; i < 4; i++) {
			byteRet[3 - i] = (byte) ((value >> 8 * i) & 0xff);
			// Logger.printf("%x ",byteRet[3-i]);
		}
		return byteRet;
	}

	private static int bytes2Int(byte[] bytes) {
		int result = 0;
		for (int i = 0; i < bytes.length; i++) {
			result |= ((bytes[bytes.length - 1 - i] & 0xff) << (i * 8));
			//System.out.printf("%x ",(bytes[i] & 0xff));
		}
		return result;
	}

	private static byte[] double2Bytes(double d) {
		long value = Double.doubleToRawLongBits(d);
		byte[] byteRet = new byte[8];
		for (int i = 0; i < 8; i++) {
			byteRet[i] = (byte) ((value >> 8 * i) & 0xff);
		}
		byte[] byteReverse = new byte[8];
		for (int i = 0; i < 8; i++) {
			byteReverse[i] = byteRet[7 - i];
			// System.out.printf("%x ",byteReverse[i]);
		}
		 Logger.println();
		return byteReverse;
	}

	public static double bytes2Double(byte[] arr) {
		byte[] byteReverse = new byte[8];
		for (int i = 0; i < 8; i++) {
			byteReverse[i] = arr[7 - i];
		}

		long value = 0;
		for (int i = 0; i < 8; i++) {
			value |= ((long) (byteReverse[i] & 0xff)) << (8 * i);
		}
		return Double.longBitsToDouble(value);
	}
	
	byte[] buffer = new byte[1024 * 1024];
	byte[] durationHeader = {0x08, 0x64 , 0x75 , 0x72 , 0x61 , 0x74 , 0x69 , 0x6f , 0x6e};
	int pDurationHeader = 0;
	public void changeDuration(String path, double duration) throws IOException{
		//08 64 75 72 61 74 69 6f 6e   duration
		//00 bytes x8
		File file = new File(path);
		RandomAccessFile raf = new RandomAccessFile(file, "rw");
		
		int lenRead = raf.read(buffer);
		int pDuration = checkBufferForDuration();
		boolean findDuration = false;
		while (lenRead > -1) {
			long offset = 0;
			if(pDuration != -1) {
				findDuration = true;
				raf.seek(offset + pDuration + 1);
				raf.write(0x00);
				raf.write(double2Bytes(duration));
				break;
			}
			// Logger.println("当前完成度: " + cnt*100/total + "%");
			lenRead = raf.read(buffer);
			if(!findDuration) {
				pDuration = checkBufferForDuration();
			}
			offset += lenRead;
		}
		raf.close();
		
	}
	
	/**
	 * 检查buffer 是否包含duration头部
	 * @return duration末尾在 buffer中的位置
	 */
	int checkBufferForDuration() {
		for(int i=0; i<buffer.length; i++) {
			if(buffer[i] == durationHeader[pDurationHeader]) {
				pDurationHeader ++;
				if(pDurationHeader == durationHeader.length) {
					pDurationHeader = 0;
					return i;
				}
			}
		}
		return -1;
	}
}
