package nicelee.bilibili.live.check;

import java.io.File;
import java.io.IOException;

import nicelee.bilibili.Config;
import nicelee.bilibili.util.Logger;

public class FlvCheckerWithBufferEx extends FlvCheckerWithBuffer {

	public static void main(String[] args) throws IOException {

//		args = new String[] { "D:\\Workspace\\javaweb-springboot\\BilibiliLiveRecord\\download\\样本\\快手-header.flv",
//				"true", "false" };
//		args = new String[] { "D:\\Workspace\\javaweb-springboot\\BilibiliLiveRecord\\release\\download\\虎牙中韩明星对抗赛-lolnewyear 的huya直播 2019-08-29 20.22-0..flv",
//				"true", "false" };
//		args = new String[] { "flv=D:\\Workspace\\javaweb-springboot\\BilibiliLiveRecord\\release\\download\\虎牙中韩明星对抗赛-lolnewyear 的huya直播 2019-08-29 20.22-0..flv"
//				+ "&debug=false&splitScripts=true&splitAVHeader=true" };
		
		FlvCheckerWithBufferEx fChecker = new FlvCheckerWithBufferEx();
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

				value = Config.getValue(args[0], "contentFramesToSkip");
				if (value != null)
					TagOptions.contentFramesToSkip = Integer.parseInt(value);
				
				value = Config.getValue(args[0], "maxPeriodBetween2Frame");
				if (value != null)
					TagOptions.maxPeriodBetween2Frame = Integer.parseInt(value);
				
				long t1 = System.currentTimeMillis();
				System.out.println("校对时间戳开始...");
				fChecker.check(flv, deleteOnchecked, splitScripts, splitAVHeaders, saveFolder);
				System.out.println("校对时间戳完毕。");
				long t2 = System.currentTimeMillis();
				System.out.println("校对耗时(s)：" + (t2 - t1)/ 1000);
				
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
	
	private long count = 0;
	private int firstTimeStamp = 0;
	private int lastTimeStamp = 0;
	/**
	 * 不再对时间戳进行额外处理，也不再分音视频进行处理，基本上完整保留原来的相对时间戳大小
	 */

	@Override
	protected boolean dealTimestamp(RafWBuffered raf, int timestamp, int tagType) throws IOException {
		// 只考虑从前10帧获取最初的timestamp
		if (count < 10) {
			// 如果时间差大于30s，当前时间可以算是初始时间戳，但由于前面可能还有几帧数据，时间戳应该非严格?递增，写入的时间戳最小只能是lastTimeStamp
			// 本来 timestamp -> ▲0, 现在timestamp -> ▲lastTimeStamp, 即
			if (timestamp - lastTimeStamp > TagOptions.maxPeriodBetween2Frame) {
				firstTimeStamp = timestamp - lastTimeStamp;
			}
		}

		//要写入的时间戳为当前相对于第一帧的差值
		lastTimeStamp = timestamp - firstTimeStamp;
		lastTimestampWrite[0] = lastTimeStamp;
		// 低于0xffffff部分
		int lowCurrenttime = lastTimeStamp & 0xffffff;
		raf.write(int2Bytes(lowCurrenttime), 1, 3);
		// 高于0xffffff部分
		int highCurrenttime = lastTimeStamp >> 24;
		raf.write(highCurrenttime);
		Logger.print(" ,读取timestamps 为：" + timestamp);
		Logger.print(" ,写入timestamps 为：" + lastTimeStamp);
		Logger.println();
		count++;
//		if(count > 20)
//			System.exit(1);
		return true;
	}
}
