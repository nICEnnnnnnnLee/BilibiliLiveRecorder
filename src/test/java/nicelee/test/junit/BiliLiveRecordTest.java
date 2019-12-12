package nicelee.test.junit;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import nicelee.bilibili.live.FlvChecker;

public class BiliLiveRecordTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	 @Test
	public void test() {
		byte[] arr = { 0x40, 0x5b, 0x20, (byte) 0xf5, (byte) 0xc2, (byte) (0x8f), 0x5c, 0x29 };
		System.out.printf("\n当前的值为\n%.2f \n", new FlvChecker().bytes2Double(arr));
		System.out.println(System.currentTimeMillis());
	}

	//@Test
	public void changeSize() {
		File file = new File("D:\\Workspace\\javaweb-springboot\\BilibiliLiveRecord\\release\\download\\主播阿迎-198859 的douyu直播 2019-08-30 11.57-0.flv");
		try {
			RandomAccessFile raf = new RandomAccessFile(file, "rw");
			raf.setLength(raf.length() - 9);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

//	@Test
	public void testCheck() {
//		String path = "D:\\Workspace\\javaweb-springboot\\BilibiliLiveRecord\\download\\哔哩哔哩英雄联盟赛事-6 的直播 2019-05-18 17.43.flv";
//		String path = "D:\\Workspace\\javaweb-springboot\\BilibiliLiveRecord\\release\\download\\主播阿迎-198859 的douyu直播 2019-08-30 11.57-0.flv";
		String path = "D:\\Workspace\\javaweb-springboot\\BilibiliLiveRecord\\release\\download\\主播阿迎-198859 的douyu直播 2019-08-30 11.57-0-checked0.flv";
//		String path = "D:\\Workspace\\javaweb-springboot\\BilibiliLiveRecord\\release\\download\\bili-11247219-原始.flv";
		try {
//			new FlvChecker().check(path, false);
			new FlvChecker().checkFromEnd(path);
			// FlvChecker.changeDuration(path, 0);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
