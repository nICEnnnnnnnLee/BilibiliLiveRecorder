package nicelee.bilibili.live;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import nicelee.bilibili.util.Logger;

/**
 * 原理
 * https://www.cnblogs.com/lidabo/p/9018548.html
 */
public class FlvChecker {

	
	public static void main(String[] args) throws IOException {
		if(args != null) {
			System.out.println("校对时间戳开始...");
			check(args[0]);
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
	public static void check(String path) throws IOException {
		File file = new File(path);
		RandomAccessFile raf = new RandomAccessFile(file, "rw");

		// 跳过头部
		raf.skipBytes(9);

		// 用于统计时间戳
		int firstTimeStamp = -1, lastTimestamp = -1;
		// 用于排除无效尾巴帧
		long currentLength = 9L, latsValidLength = currentLength;
		try {
			while (true) {
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
				} else {
					Logger.print(tagType);
					raf.setLength(latsValidLength);
					break;
				}
				// tag data size 3个字节。表示tag data的长度。从streamd id 后算起。
				int dataSize = readBytesToInt(raf, 3);
				Logger.print(" ,当前tag data 长度为：" + dataSize);
				// 时间戳 3+1
				int timestamp = readBytesToInt(raf, 3);
				if (firstTimeStamp <= 0) {
					firstTimeStamp = timestamp;
					raf.seek(raf.getFilePointer() - 3);
					byte[] zeros = { 0, 0, 0 };
					raf.write(zeros, 0, 3);
				} else {
					// 登记有效时间戳
					if (timestamp > 0) {
						lastTimestamp = timestamp;
					}
					// 修改当前时间戳
					int currentTime = lastTimestamp - firstTimeStamp;
					raf.seek(raf.getFilePointer() - 3);
					raf.write(int2Bytes(currentTime), 1, 3);
				}

				Logger.println(" ,当前timestamps 为：" + timestamp);
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
		Logger.println("currentLength 为：" + currentLength);
		raf.close();
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
			while (true) {
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
				Logger.println(" ,当前timestamps 为：" + timestamp);
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
		}
		return result;
	}

//	private static byte[] double2Bytes(double d) {
//		long value = Double.doubleToRawLongBits(d);
//		byte[] byteRet = new byte[8];
//		for (int i = 0; i < 8; i++) {
//			byteRet[i] = (byte) ((value >> 8 * i) & 0xff);
//		}
//		byte[] byteReverse = new byte[8];
//		for (int i = 0; i < 8; i++) {
//			byteReverse[i] = byteRet[7 - i];
//			// Logger.printf("%x ",byteReverse[i]);
//		}
//		// Logger.println();
//		return byteReverse;
//	}

//	private static double bytes2Double(byte[] arr) {
//		byte[] byteReverse = new byte[8];
//		for (int i = 0; i < 8; i++) {
//			byteReverse[i] = arr[7 - i];
//		}
//
//		long value = 0;
//		for (int i = 0; i < 8; i++) {
//			value |= ((long) (byteReverse[i] & 0xff)) << (8 * i);
//		}
//		return Double.longBitsToDouble(value);
//	}
	
//	static byte[] buffer = new byte[1024 * 1024];
//	static byte[] durationHeader = {0x08, 0x64 , 0x75 , 0x72 , 0x61 , 0x74 , 0x69 , 0x6f , 0x6e};
//	static int pDurationHeader = 0;
//	public static void changeDuration(String path, double duration) throws IOException{
//		//08 64 75 72 61 74 69 6f 6e   duration
//		//00 bytes x8
//		File file = new File(path);
//		File fileNew = new File(path + ".flv");
//		fileNew.delete();
//		RandomAccessFile raf = new RandomAccessFile(file, "r");
//		RandomAccessFile rafNew = new RandomAccessFile(fileNew, "rw");
//		
//		int lenRead = raf.read(buffer);
//		int pDuration = checkBufferForDuration();
//		boolean findDuration = false;
//		long offset = 0;
//		while (lenRead > -1) {
//			
//			if(pDuration == -1) {
//				rafNew.write(buffer, 0, lenRead);
//			}else { // 假设没有超过buffer界限
//				findDuration = true;
//				rafNew.write(buffer, 0, pDuration + 1);
//				rafNew.write(0x00);
//				rafNew.write(double2Bytes(duration));
//				rafNew.write(buffer, pDuration + 10 , lenRead - pDuration - 10);
//			}
//			// Logger.println("当前完成度: " + cnt*100/total + "%");
//			lenRead = raf.read(buffer);
//			if(!findDuration) {
//				pDuration = checkBufferForDuration();
//			}
//			offset += lenRead;
//		}
//		rafNew.close();
//		raf.close();
//		
//	}
//	
//	/**
//	 * 检查buffer 是否包含duration头部
//	 * @return duration末尾在 buffer中的位置
//	 */
//	static int checkBufferForDuration() {
//		for(int i=0; i<buffer.length; i++) {
//			if(buffer[i] == durationHeader[pDurationHeader]) {
//				pDurationHeader ++;
//				if(pDurationHeader == durationHeader.length) {
//					pDurationHeader = 0;
//					return i;
//				}
//			}
//		}
//		return -1;
//	}
}
