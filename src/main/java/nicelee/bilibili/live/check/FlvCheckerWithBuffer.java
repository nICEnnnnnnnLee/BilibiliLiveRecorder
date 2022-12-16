package nicelee.bilibili.live.check;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nicelee.bilibili.Config;
import nicelee.bilibili.util.Logger;

public class FlvCheckerWithBuffer {

	public static void main(String[] args) throws IOException {
//		args = new String[] {"D:\\Workspace\\javaweb-springboot\\BilibiliLiveRecord\\download\\样本\\虎牙-原始样本1.flv" };
//		args = new String[] {"D:\\Workspace\\javaweb-springboot\\BilibiliLiveRecord\\download\\样本\\斗鱼-原始样本1.flv", "true", "false" };
//		args = new String[] {"D:\\Workspace\\测试douyu-余霜Yscandice-2020-01-16 13.41-2020-01-16 13.42-0.flv", "true", "true" };
//		args = new String[] {"D:\\Workspace\\javaweb-springboot\\BilibiliLiveRecord\\download\\样本\\B站-原始样本1.flv" };
//		args = new String[] {"D:\\Workspace\\javaweb-springboot\\BilibiliLiveRecord\\download\\样本\\快手-原始样本1.flv", "true", "false" };
//		args = new String[] { "D:\\Workspace\\javaweb-springboot\\BilibiliLiveRecord\\download\\样本\\快手-header.flv",
//				"true", "true" };
//		args = new String[] {"D:\\Workspace\\javaweb-springboot\\BilibiliLiveRecord\\download\\样本\\快手-原始样本2.flv" };
//		args = new String[] { "flv=D:\\Workspace\\javaweb-springboot\\BilibiliLiveRecord\\download\\样本\\快手-header.flv"
//				+ "&debug=true&splitScripts=true" };

		FlvCheckerWithBuffer fChecker = new FlvCheckerWithBuffer();
		boolean splitScripts = false;
		boolean splitAVHeaders = false;
		String saveFolder = null;
		if (args != null) {
			// 如果是flv=xxx&debug=xxx的形式
			if (args.length == 1 && args[0].startsWith("flv=")) {
				String flv = Config.getValue(args[0], "flv");

				String value = Config.getValue(args[0], "saveFolder");
				if (value != null) {
					saveFolder = value;
					File f = new File(saveFolder);
					if (!f.exists())
						f.mkdirs();
				}

				value = Config.getValue(args[0], "debug");
				if ("false".equals(value))
					Logger.debug = false;

				value = Config.getValue(args[0], "splitScripts");
				splitScripts = "true".equals(value);
				splitAVHeaders = splitScripts;

				value = Config.getValue(args[0], "splitAVHeaders");
				if (value != null)
					splitAVHeaders = "true".equals(value);

				value = Config.getValue(args[0], "deleteOnchecked");
				boolean deleteOnchecked = "true".equals(value);

				value = Config.getValue(args[0], "maxAudioHeaderSize");
				if (value != null)
					TagOptions.maxAudioHeaderSize = Integer.parseInt(value);

				value = Config.getValue(args[0], "maxVideoHeaderSize");
				if (value != null)
					TagOptions.maxVideoHeaderSize = Integer.parseInt(value);

				System.out.println("校对时间戳开始...");
				fChecker.check(flv, deleteOnchecked, splitScripts, splitAVHeaders, saveFolder);
				System.out.println("校对时间戳完毕。");
			} else {
				// 为了兼容旧版本
				if (args.length >= 4) {
					saveFolder = args[3];
					File f = new File(saveFolder);
					if (!f.exists())
						f.mkdirs();
				}
				if (args.length >= 3 && "false".equals(args[2]))
					Logger.debug = false;

				if (args.length >= 2)
					splitScripts = "true".equals(args[1]);

				if (args.length >= 1) {
					System.out.println("校对时间戳开始...");
					fChecker.check(args[0], false, splitScripts, splitScripts, saveFolder);
					System.out.println("校对时间戳完毕。");
				} else {
					System.out.println("请输入正确的文件路径");
				}
			}
		}
	}

	/**
	 * 从头部开始Check, 重新锚定时间戳, 将最后一帧(不管是否完整)去掉
	 * 
	 * @param path
	 * @throws IOException
	 */
	// 用于统计时间戳
	protected int lastTimestampRead[] = { -1, -1 }, lastTimestampWrite[] = { -1, -1 };
	// 用于缓冲
	private static byte[] buffer = new byte[1024 * 1024 * 4];

	public void check(String path) throws IOException {
		check(path, false, false);
	}

	public void check(String path, boolean deleteOnchecked) throws IOException {
		check(path, deleteOnchecked, false);
	}

	public void check(String path, boolean deleteOnchecked, boolean splitScripts) throws IOException {
		check(path, deleteOnchecked, splitScripts, splitScripts, null);
	}

	public void check(String path, boolean deleteOnchecked, boolean splitScripts, String saveFolder)
			throws IOException {
		check(path, deleteOnchecked, splitScripts, splitScripts, saveFolder);
	}

	public void check(String path, boolean deleteOnchecked, boolean splitScripts, boolean splitAVHeaders,
			String saveFolder) throws IOException {
		Logger.println("校对时间戳开始...");
		File file = new File(path);
		RafRBuffered raf = new RafRBuffered(file, "r");

		File destFolder = null;
		if (saveFolder != null) {
			destFolder = new File(saveFolder);
		} else {
			destFolder = file.getParentFile();
		}
		File fileNew = null;
		Pattern pattern = Pattern.compile("-checked([0-9]+).flv$");
		Matcher matcher = pattern.matcher(file.getName());
		if (matcher.find()) {
			int index = Integer.parseInt(matcher.group(1));
			index++;
			fileNew = new File(destFolder, file.getName().replaceFirst("[0-9]+.flv$", index + ".flv"));
		} else {
			fileNew = new File(destFolder, file.getName().replaceFirst(".flv$", "-checked0.flv"));
		}
		RafWBuffered rafNew = new RafWBuffered(fileNew, "rw");
		// 复制头部
		raf.read(buffer, 0, 9);
		rafNew.write(buffer, 0, 9);
		// 处理Tag内容
		checkTag(raf, rafNew, fileNew, splitScripts, splitAVHeaders);

		raf.close();
		rafNew.close();
		changeDuration(fileNew.getAbsolutePath(), this.getDuration() / 1000);
		if (deleteOnchecked) {
			file.delete();
		}
	}

	private void checkTag(RafRBuffered raf, RafWBuffered rafNew, File fileNew, boolean splitScripts,
			boolean splitAVHeaders) {
		checkTag(raf, rafNew, fileNew, splitScripts, splitAVHeaders, false, new TagOptions());
	}

	/**
	 * @param raf
	 * @param rafNew
	 */
	private void checkTag(RafRBuffered raf, RafWBuffered rafNew, File fileNew, boolean splitScripts,
			boolean splitAVHeaders, boolean invokedSplitByHeader, TagOptions options) {
		// 用于排除无效尾巴帧
		long currentLength = 9L, latsValidLength = currentLength;
		try {
			int remain = 40;
			boolean isFirstScriptTag = invokedSplitByHeader ? false : true;
			boolean isFirstAudioHeader = invokedSplitByHeader ? false : true;
			boolean isFirstVideoHeader = invokedSplitByHeader ? false : true;
			boolean needSplitAudioHeader = false;
			boolean needSplitVideoHeader = false;
			boolean lastFrameSkipped = false;
			int skipContentFrames = 0;
			int timestamp = 0;
			while (true) {// && remain>=0
				remain--;
				if(lastFrameSkipped) {
					lastFrameSkipped = false;
				} else {
					// 读取前一个tag size
					int predataSize = readBytesToInt(raf, 4);
					if (options.tagSize != null) {
						rafNew.write(int2Bytes(options.tagSize));
						options.tagSize = null;
					} else {
						rafNew.write(buffer, 0, 4);
					}
					// 记录当前新文件位置，若下一tag无效，则需要回退
					latsValidLength = currentLength;
				}
				currentLength = rafNew.getFilePointer();
				// Logger.print("前一个长度为：" + predataSize);

				// 读取tag
				// tag 类型
				int tagType = raf.read();
				Logger.print("当前tag 类型为：" + tagType);
				if (tagType == 8 || tagType == 9) {// 8/9 audio/video
					// tag data size 3个字节。表示tag data的长度。从streamd id 后算起。
					int dataSize = readBytesToInt(raf, 3);
					// Logger.print(" ,当前tag data 长度为：" + dataSize);

					boolean isHeader = false;
					if (tagType == 8 && dataSize < options.maxAudioHeaderSize) {
						if (!isFirstAudioHeader) {
							needSplitAudioHeader = true;
							// System.out.println("AudioHeaderSize: " + dataSize);
						}
						isFirstAudioHeader = false;
						isHeader = true;
						options.pAudio = raf.getFilePointer() - 3; // 记录最后一个audio header位置
					}
					if (tagType == 9 && dataSize < options.maxVideoHeaderSize) {
						if (!isFirstVideoHeader) {
							needSplitVideoHeader = true;
							System.out.println("VideoHeaderSize: " + dataSize);
						}
						isFirstVideoHeader = false;
						isHeader = true;
						options.pVideo = raf.getFilePointer() - 3; // 记录最后一个video header位置
					}
					// 一直到keyframe，如果需要分割文件，那么开始
					if (splitAVHeaders && !isHeader && (needSplitVideoHeader || needSplitAudioHeader)) {
						// 1. 新建一份文件
						File fileNew2 = null;
						Pattern pattern = Pattern.compile("-checked([0-9]+).flv$");
						Matcher matcher = pattern.matcher(fileNew.getName());
						if (matcher.find()) {
							int index = Integer.parseInt(matcher.group(1));
							index++;
							fileNew2 = new File(fileNew.getParentFile(),
									fileNew.getName().replaceFirst("[0-9]+.flv$", index + ".flv"));
						} else {
							fileNew2 = new File(fileNew.getParentFile(),
									fileNew.getName().replaceFirst(".flv$", "-checked0.flv"));
						}
						RafWBuffered rafNew2 = new RafWBuffered(fileNew2, "rw");
						// 记录当前位置
						long pos = raf.getFilePointer(); // headerPosition; // raf.getFilePointer();
						// 复制 header + 最近一个script tag
						// header 9， 上一个tag size 4，tag type 1，data Size 3， 时间戳 4，id 3， 数据 data Size
						byte[] header = new byte[9 + 4 + 1];
						raf.seek(0);
						raf.read(header);
						rafNew2.write(header);

						raf.seek(options.pMeta);
						int tDataSize = readBytesToInt(raf, 3);
						rafNew2.write(buffer, 0, 3);

						raf.skipBytes(7);
						byte[] zeroTimestampNid = new byte[] { 0, 0, 0, 0, 0, 0, 0 };
						rafNew2.write(zeroTimestampNid);

						raf.read(buffer, 0, tDataSize);
						rafNew2.write(buffer, 0, tDataSize);

						// 复制audio header
						rafNew2.write(int2Bytes(tDataSize + 11)); // 上一个tag size
						rafNew2.write(8); // tag type 1
						raf.seek(options.pAudio);
						tDataSize = readBytesToInt(raf, 3);
						rafNew2.write(buffer, 0, 3); // data Size 3
						raf.skipBytes(7);
						rafNew2.write(zeroTimestampNid); // 时间戳 4，id 3
						raf.read(buffer, 0, tDataSize);
						rafNew2.write(buffer, 0, tDataSize); // 数据

						// 复制video header
						rafNew2.write(int2Bytes(tDataSize + 11)); // 上一个tag size
						rafNew2.write(9); // tag type 1
						raf.seek(options.pVideo);
						tDataSize = readBytesToInt(raf, 3);
						// System.out.println("VideoHeader tDataSize: " + tDataSize);
						rafNew2.write(buffer, 0, 3); // data Size 3
						raf.skipBytes(7);
						rafNew2.write(zeroTimestampNid); // 时间戳 4，id 3
						raf.read(buffer, 0, tDataSize);
						rafNew2.write(buffer, 0, tDataSize); // 数据
						options.tagSize = tDataSize + 11;
						// 恢复位置
						raf.seek(pos - 3 - 5);
						// 2. 处理新文件
						FlvCheckerWithBuffer fc = this.getClass().newInstance();
						fc.checkTag(raf, rafNew2, fileNew2, splitScripts, splitAVHeaders, true, options);
						// 3. 收尾并处理时长
						rafNew2.close();
						changeDuration(fileNew2.getAbsolutePath(), fc.getDuration() / 1000);
					} else {
						if(skipContentFrames == TagOptions.contentFramesToSkip ||
							(tagType == 8 && dataSize < options.maxAudioHeaderSize) ||
							(tagType == 9 && dataSize < options.maxVideoHeaderSize)
						) {
							rafNew.write(tagType);
							rafNew.write(buffer, 0, 3);
							// 时间戳 3
							timestamp = readBytesToInt(raf, 3);
							int timestampEx = raf.read() << 24;
							timestamp += timestampEx;
							dealTimestamp(rafNew, timestamp, tagType - 8);
							raf.read(buffer, 0, 3 + dataSize);
							rafNew.write(buffer, 0, 3 + dataSize);
						} else {
							skipContentFrames++;
							lastFrameSkipped = true;
							timestamp = readBytesToInt(raf, 3);
							int timestampEx = raf.read() << 24;
							timestamp += timestampEx;
							dealTimestamp(rafNew, timestamp, tagType - 8);
							raf.skipBytes(3 + dataSize + 4);
						}
					}
				} else if (tagType == 18) { // 18 scripts
					Logger.println("scripts");
					options.pMeta = raf.getFilePointer();
					if (!splitScripts || isFirstScriptTag) {
//						// 如果是scripts脚本，默认为第一个tag，此时将前一个tag Size 置零
//						rafNew.seek(rafNew.getFilePointer() - 4);
//						byte[] zeroTimestamp = new byte[] { 0, 0, 0, 0 };
//						rafNew.write(zeroTimestamp);
						rafNew.write(tagType);
						isFirstScriptTag = false;

						int dataSize = readBytesToInt(raf, 3);
						rafNew.write(buffer, 0, 3);
						Logger.println(" 当前tag data 长度为：" + dataSize);

						raf.skipBytes(4);
						byte[] zeros = new byte[] { 0, 0, 0 };
						rafNew.write(zeros); // 时间戳 0
						rafNew.write(0); // 时间戳扩展 0
						raf.read(buffer, 0, 3 + dataSize);
						rafNew.write(buffer, 0, 3 + dataSize);
					} else {
						Logger.println("第二个scripts脚本");
						// 当有第二个scripts脚本时
						// 1. 从第二个script tag起始新创建一份文件
						File fileNew2 = null;
						Pattern pattern = Pattern.compile("-checked([0-9]+).flv$");
						Matcher matcher = pattern.matcher(fileNew.getName());
						if (matcher.find()) {
							int index = Integer.parseInt(matcher.group(1));
							index++;
							fileNew2 = new File(fileNew.getParentFile(),
									fileNew.getName().replaceFirst("[0-9]+.flv$", index + ".flv"));
						} else {
							fileNew2 = new File(fileNew.getParentFile(),
									fileNew.getName().replaceFirst(".flv$", "-checked0.flv"));
						}
						RafWBuffered rafNew2 = new RafWBuffered(fileNew2, "rw");
						// 记录当前位置
						long pos = raf.getFilePointer();
						// 复制 header
						byte[] header = new byte[9];
						raf.seek(0);
						raf.read(header);
						rafNew2.write(header);
						options.tagSize = 0;
						// 恢复位置
						raf.seek(pos - 5);
						// 2. 处理新文件
						FlvCheckerWithBuffer fc = this.getClass().newInstance();
						fc.checkTag(raf, rafNew2, fileNew2, splitScripts, splitAVHeaders, false, options);
						// 3. 收尾并处理时长
						rafNew2.close();
						changeDuration(fileNew2.getAbsolutePath(), fc.getDuration() / 1000);
					}
				} else {
					Logger.print("未知类型");
					Logger.print(tagType);
					rafNew.setLength(latsValidLength);
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		Logger.println();
		Logger.println("lastTimestamp 为：" + lastTimestampWrite[0]);
		Logger.println("currentLength 为：" + currentLength);
	}

	/**
	 * 处理音/视频时间戳
	 * 
	 * @param raf
	 * @param timestamp
	 * @throws IOException
	 * @return 是否忽略该tag
	 */
	protected boolean dealTimestamp(RafWBuffered raf, int timestamp, int tagType) throws IOException {
		Logger.print("上一帧读取timestamps 为：" + lastTimestampRead[tagType]);
		Logger.print("上一帧写入timestamps 为：" + lastTimestampWrite[tagType]);

		// 如果是首帧
		if (lastTimestampRead[tagType] == -1) {
			lastTimestampWrite[tagType] = 0;
		} else if (timestamp >= lastTimestampRead[tagType]) {// 如果时序正常
			// 间隔十分巨大(1s)，那么重新开始即可
			if (timestamp > lastTimestampRead[tagType] + 1000) {
				lastTimestampWrite[tagType] += 10;
				Logger.print("---");
			} else {
				lastTimestampWrite[tagType] = timestamp - lastTimestampRead[tagType] + lastTimestampWrite[tagType];
			}
		} else {// 如果出现倒序时间戳
				// 如果间隔不大，那么如实反馈
			if (lastTimestampRead[tagType] - timestamp < 5 * 1000) {
				int tmp = timestamp - lastTimestampRead[tagType] + lastTimestampWrite[tagType];
				tmp = tmp > 0 ? tmp : 1;
				lastTimestampWrite[tagType] = tmp;
			} else {// 间隔十分巨大，那么重新开始即可
				lastTimestampWrite[tagType] += 10;
				Logger.print("---rewind");
			}
		}
		lastTimestampRead[tagType] = timestamp;

		// 低于0xffffff部分
		int lowCurrenttime = lastTimestampWrite[tagType] & 0xffffff;
		raf.write(int2Bytes(lowCurrenttime), 1, 3);
		// 高于0xffffff部分
		int highCurrenttime = lastTimestampWrite[tagType] >> 24;
		raf.write(highCurrenttime);
		Logger.print(" ,读取timestamps 为：" + timestamp);
		Logger.print(" ,写入timestamps 为：" + lastTimestampWrite[tagType]);
		Logger.println();
		return true;
	}

	/**
	 * @param raf
	 * @param byteLength
	 * @return
	 * @throws IOException
	 */
	protected int readBytesToInt(RafRBuffered raf, int byteLength) throws IOException {
		raf.read(buffer, 0, byteLength);
		return bytes2Int(buffer, byteLength);
	}

	protected byte[] int2Bytes(int value) {
		byte[] byteRet = new byte[4];
		for (int i = 0; i < 4; i++) {
			byteRet[3 - i] = (byte) ((value >> 8 * i) & 0xff);
			// Logger.printf("%x ",byteRet[3-i]);
		}
		return byteRet;
	}

	protected int bytes2Int(byte[] bytes, int byteLength) {
		int result = 0;
		for (int i = 0; i < byteLength; i++) {
			result |= ((bytes[byteLength - 1 - i] & 0xff) << (i * 8));
			// System.out.printf("%x ",(bytes[i] & 0xff));
		}
		return result;
	}

	protected byte[] double2Bytes(double d) {
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

	public double bytes2Double(byte[] arr) {
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

	byte[] durationHeader = { 0x08, 0x64, 0x75, 0x72, 0x61, 0x74, 0x69, 0x6f, 0x6e };
	int pDurationHeader = 0;

	public void changeDuration(String path, double duration) throws IOException {
		// 08 64 75 72 61 74 69 6f 6e duration
		// 00 bytes x8
		File file = new File(path);
		RandomAccessFile raf = new RandomAccessFile(file, "rw");

		int lenRead = raf.read(buffer);
		int pDuration = checkBufferForDuration();
		boolean findDuration = false;
		while (lenRead > -1) {
			long offset = 0;
			if (pDuration != -1) {
				findDuration = true;
				raf.seek(offset + pDuration + 1);
				raf.write(0x00);
				raf.write(double2Bytes(duration));
				break;
			}
			// Logger.println("当前完成度: " + cnt*100/total + "%");
			lenRead = raf.read(buffer);
			if (!findDuration) {
				pDuration = checkBufferForDuration();
			}
			offset += lenRead;
		}
		raf.close();

	}

	/**
	 * 检查buffer 是否包含duration头部
	 * 
	 * @return duration末尾在 buffer中的位置
	 */
	int checkBufferForDuration() {
		for (int i = 0; i < buffer.length; i++) {
			if (buffer[i] == durationHeader[pDurationHeader]) {
				pDurationHeader++;
				if (pDurationHeader == durationHeader.length) {
					pDurationHeader = 0;
					return i;
				}
			}
		}
		return -1;
	}

	public double getDuration() {
		return (double) lastTimestampWrite[0];
	}
}
