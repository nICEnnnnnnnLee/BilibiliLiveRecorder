package nicelee.bilibili.live;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import nicelee.bilibili.util.Logger;

/**
 * 原理
 * https://www.cnblogs.com/boonya/p/8572052.html
 * https://blog.csdn.net/pirateleo/article/details/7590056/
 * 
 * https://blog.csdn.net/dragoo1/article/details/49659969
 * 
 * 概述 https://www.cnblogs.com/shakin/p/8543719.html
 * 		https://www.jianshu.com/p/dee55d54fd54
 * 
 * mvhd - 	https://blog.csdn.net/NB_vol_1/article/details/58297422
 * trak - 	https://blog.csdn.net/NB_vol_1/article/details/58300045 
 * 	  tkhd- https://blog.csdn.net/NB_vol_1/article/details/58586220
 * elst- http://blog.jianchihu.net/mp4-elst-box.html
 * 
 * fmp4 - https://www.cnblogs.com/gardenofhu/p/10044853.html
 */
public class Mp4Checker {

	
	public static void main(String[] args) throws IOException {
		Mp4Checker.check("D:\\Workspace\\javaweb-springboot\\BilibiliDown\\download\\test_only_video.m4s");
	}

	/**
	 * 
	 * @param path
	 * @throws IOException
	 */
	public static void check(String path) throws IOException {
		File file = new File(path);
		RandomAccessFile raf = new RandomAccessFile(file, "r");
		// 跳过头部
		//raf.skipBytes(9);
		// 用于排除无效尾巴帧
		checkBox(raf, "mpeg", raf.length(), 0);
		Logger.println();
		raf.close();
	}

	
	public static void checkBox(RandomAccessFile raf, String currentBoxType, long dataSize, int boxNo) throws IOException {
		try {
			int cnt = 10;
			long currentPosition = raf.getFilePointer(); // 记住当前位置
			long offset = 0; // 计数，用于统计
			while (offset < dataSize) {//&& cnt >=0
				//Logger.printf(boxNo,"offset: %d, dataSize: %d", offset, dataSize);
				// Header - size - 4
				int size = readBytesToInt(raf, 4) & 0xffffffff;
				// Header - type - 4
				byte[] buffer4Bytes = new byte[4];
				raf.read(buffer4Bytes);
				String type = new String(buffer4Bytes);
				Logger.printf(boxNo,"%s 当前pointer: %s, size： %s, type：%s", currentBoxType, raf.getFilePointer(), size, type);
				
//				raf.seek(raf.getFilePointer() - 4);
//				raf.read(buffer4Bytes);
//				type = new String(buffer4Bytes);
//				Logger.printf(boxNo,"%s 当前size： %s, type：%s", currentBoxType, size, type);
				
				if(size == 1) {
					// Length (size)
					raf.skipBytes(8);
					// Header(Full Box) - version 1B + flags 3B
					raf.skipBytes(4);
					Logger.println(boxNo,"大文件！！！");
					break;
				}
				
				if("ftyp".equals(type)) { 
					// major_brand 4
					raf.read(buffer4Bytes);
					String major_brand = new String(buffer4Bytes);
					Logger.println(boxNo,"----major_brand： " + major_brand);
					// minor_version 4
					Logger.println(boxNo,"----minor_version： " + readBytesToInt(raf, 4));
//					// compatible_brands 因为
					// 		size = header(4 + 4) + data； 
					// 		data =  major_brand(4) +  minor_version(4) + compatible_brands
					// ===> compatible_brands = size - 8 - 8
					raf.skipBytes(size - 16);
					//raf.skipBytes(size);
				}else if("free".equals(type)) {
					raf.skipBytes(size - 8);
				}else if("moov".equals(type)) {
					checkBox(raf, "moov", size - 8, boxNo + 1);
				}else if("mvhd".equals(type)) {
					// Header(Full Box) - version 1B + flags 3B
					raf.skipBytes(4);
					
					// (in seconds since midnight, Jan. 1, 1904, in UTC time) 
					int createTime = readBytesToInt(raf, 4);
					int lastModifiedTime = readBytesToInt(raf, 4);
					int timescale = readBytesToInt(raf, 4); // 1/timescale
					int duration = readBytesToInt(raf, 4);
					Logger.printf(boxNo,"----mvhd 视频时长 %d/%d = %d s", duration, timescale, duration/timescale);
					
//					raf.seek(raf.getFilePointer() - 4);
//					raf.write(int2Bytes(240000));
					
					raf.skipBytes(4);  // rate 媒体速率, 这个值代表原始倍速
					raf.skipBytes(2);  // volume 媒体音量, 这个值代表满音量
					raf.skipBytes(10); // 保留位 
					raf.skipBytes(36); // matrix 视频变换矩阵
					raf.skipBytes(20); // pre-defined 4x5:preview_time preview_duration poster_time selection_time selection_duration
					int current_time = readBytesToInt(raf, 4); 
					int next_track_id = readBytesToInt(raf, 4);
				}else if("trak".equals(type)) {
					checkBox(raf, "trak", size - 8, boxNo + 1);
				}else if("mdia".equals(type)) {
					checkBox(raf, "mdia", size - 8, boxNo + 1);
				}else if("minf".equals(type)) {
					checkBox(raf, "minf", size - 8, boxNo + 1);
				}else if("stbl".equals(type)) {
					checkBox(raf, "stbl", size - 8, boxNo + 1);
				}else if("mdhd".equals(type)) {
					// Header(Full Box) - version 1B + flags 3B
					int version = raf.read();
					raf.skipBytes(3);
					if(version != 0) {
						throw new Exception("sidx version 1 尚未支持解析！！");
					}
					raf.skipBytes(8); // creation_time 4 + modification_time 4
					int timescale = readBytesToInt(raf, 4);
					int duration = readBytesToInt(raf, 4);
					
//					raf.seek(raf.getFilePointer() - 4);
//					raf.write(int2Bytes(3840000));
					
					raf.skipBytes(2);  // pad 1bit language 15bit
					raf.skipBytes(2);
					Logger.printf(boxNo,"----mdhd 视频时长 %d/%d = %d s", duration, timescale, duration/timescale);
					//raf.skipBytes(size - 8);
				}else if("hdlr".equals(type)) {
					// Header(Full Box) - version 1B + flags 3B
					raf.skipBytes(4);
					raf.skipBytes(4); // pre_defined 0
					
					raf.read(buffer4Bytes);
					String handler_type = new String(buffer4Bytes); 
					// vide : Video track/ soun : Audio track/ auxv : Auxiliary Video track
				    // hint : Hint track/ meta : Timed Metadata track
					Logger.printf(boxNo, "----hdlr handler_type - %s", handler_type);
					raf.skipBytes(12); // reserved 0
//					Logger.printf(boxNo, "0x%02x", raf.read());
//					Logger.printf(boxNo, "0x%02x", raf.read());
//					Logger.printf(boxNo, "0x%02x", raf.read());
//					Logger.printf(boxNo, "0x%02x", raf.read());
					byte[] name = new byte[size - 8 - 12 - 12]; //name
					raf.read(name);
					Logger.printf(boxNo, "----hdlr name %s", new String(name));
					//raf.skipBytes(size - 8 - 12 - 12);
				}else if("tkhd".equals(type)) {
					// Header(Full Box) - version 1B + flags 3B
					raf.skipBytes(4);
					raf.skipBytes(4*4); // creation_time + modification_time + track_id + reserved1
					int duration = readBytesToInt(raf, 4);
					raf.skipBytes(8); // reserved2
					raf.skipBytes(2); // layer[2]; // 视频层，默认为0，值小的在上层  
					raf.skipBytes(2); // group[2]; // track分组信息，默认为0表示该track未与其他track有群组关系  
					//raf.skipBytes(2); // volume[2]; // 0x0100 音频 0x0000 其它
					Logger.printf(boxNo, "----tkhd 是否音频- %d", raf.read());
					raf.skipBytes(1);
					raf.skipBytes(2); // reverved3[2];  
					raf.skipBytes(36);// matrix[36]; // 视频变换矩阵  
					int width = readBytesToInt(raf, 2); // 宽 
					raf.skipBytes(2);
					int height = readBytesToInt(raf, 2); // 高
					raf.skipBytes(2);
					//Logger.printf(boxNo,"0x%02x 0x%02x 0x%02x 0x%02x", raf.read(), raf.read(), raf.read(), raf.read());
					//Logger.printf(boxNo,"0x%02x 0x%02x 0x%02x 0x%02x", raf.read(), raf.read(), raf.read(), raf.read());
					Logger.printf(boxNo,"----tkhd width x height : %dx%d", width, height);
				}else if("edts".equals(type)) {
					checkBox(raf, "edts", size - 8, boxNo + 1);
				}else if("elst".equals(type)) {
					// Header(Full Box) - version 1B + flags 3B
					int version = raf.read();
					raf.skipBytes(3);
					
					int entryCnt = readBytesToInt(raf, 4);
					for(int i=1; i<= entryCnt; i++) {
						if(version == 1) {
							long segment_duration = readBytesToInt(raf, 4); // 表示该edit段的时长
							segment_duration = segment_duration<<32 + readBytesToInt(raf, 4);
							long media_time = readBytesToInt(raf, 4);	// 表示该edit段的起始时间
							media_time = media_time<<32 + readBytesToInt(raf, 4);
							Logger.printf(boxNo,"----elst entry%d- version: %d, segment_duration: %d, media_time: %d", i, version, segment_duration, media_time);
						}else {
							long segment_duration = readBytesToInt(raf, 4);
							long media_time = readBytesToInt(raf, 4);
							Logger.printf(boxNo,"----elst entry%d- version: %d, segment_duration: %d, media_time: %d", i, version, segment_duration, media_time);
						}
						int media_rate = raf.read() << 8 + raf.read();
						raf.skipBytes(2);// media_rate_fraction = 0
						Logger.printf(boxNo,"----elst rate: %d", media_rate);
					}
					
					//raf.skipBytes(size - 8);
				}else if("sidx".equals(type)) { // TODO
					// Header(Full Box) - version 1B + flags 3B
					int version = raf.read();
					raf.skipBytes(3);
					if(version != 0) {
						throw new Exception("sidx version 1 尚未支持解析！！");
					}
					
					int reference_ID = readBytesToInt(raf, 4);  
					int timescale = readBytesToInt(raf, 4); 
					int earliest_presentation_time = readBytesToInt(raf, 4); 
					int first_offset = readBytesToInt(raf, 4);
					Logger.printf(boxNo,"----sidx reference_ID: %d", reference_ID);
					Logger.printf(boxNo,"----sidx first_offset: %d, %d", first_offset, raf.getFilePointer());
					raf.skipBytes(2); // reserved = 0
//					Logger.printf(boxNo, "0x%02x", raf.read());
					long reference_count = raf.read() << 8;
					reference_count |= raf.read();
					Logger.printf(boxNo,"----sidx reference文件数: %d", reference_count);
					for(int i=0; i<reference_count; i++) {
						int reference_type = raf.read();
						long referenced_size = reference_type & 0x7F;
						referenced_size = (referenced_size << 8) | raf.read();
						referenced_size = (referenced_size << 8) | raf.read();
						referenced_size = (referenced_size << 8) | raf.read();
						reference_type = reference_type >> 7;
						int subsegment_duration = readBytesToInt(raf, 4);
						Logger.printf(boxNo,"----sidx reference-%d- reference_type:%d, referenced_size: %d, subsegment_duration: %d", i, reference_type, referenced_size, subsegment_duration);
						raf.skipBytes(4);
//					    bit(1) starts_with_SAP;  //whether the referenced subsegments start with a SAP
//					    unsigned int(3) SAP_type;  //SAP type as specified in Annex I, or the value 0.
//					    unsigned int(28) SAP_delta_time;  //indicates TSAP of the first SAP, in decoding order, in the referenced subsegment for the reference stream
					}
				}else if("moof".equals(type)) { // TODO
					checkBox(raf, "moof", size - 8, boxNo + 1);
				}else if("mdat".equals(type)) { // TODO
					checkBox(raf, "mdat", size - 8, boxNo + 1);
				}else if("mvex".equals(type)) { // TODO
					checkBox(raf, "mvex", size - 8, boxNo + 1);
				}else {  
					raf.skipBytes(size - 8);
					//checkBox(raf, type, size - 8, boxNo + 1);
				}
				offset += size;
				cnt--;
			}
			// 为了防止文件不规范，在box结束后，回调位置，然后再往前进boxSize
			raf.seek(currentPosition + dataSize);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @param raf
	 * @param byteLength
	 * @return
	 * @throws IOException
	 */
	private static int readBytesToInt(RandomAccessFile raf, int byteLength) throws IOException {
		byte data[] = new byte[byteLength];
		int len = raf.read(data);
		if(len != byteLength) {
			return -1;
		}
		return bytes2Int(data);
	}

	private static byte[] int2Bytes(int value) {
		byte[] byteRet = new byte[4];
		for (int i = 0; i < 4; i++) {
			byteRet[3 - i] = (byte) ((value >> 8 * i) & 0xff);
			// Logger.printf(boxNo,"%x ",byteRet[3-i]);
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

	static byte[] double2Bytes(double d) {
		long value = Double.doubleToRawLongBits(d);
		byte[] byteRet = new byte[8];
		for (int i = 0; i < 8; i++) {
			byteRet[i] = (byte) ((value >> 8 * i) & 0xff);
		}
		byte[] byteReverse = new byte[8];
		for (int i = 0; i < 8; i++) {
			byteReverse[i] = byteRet[7 - i];
			// Logger.printf("%x ",byteReverse[i]);
		}
		// Logger.println();
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
}
