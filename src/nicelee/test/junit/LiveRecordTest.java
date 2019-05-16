package nicelee.test.junit;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import nicelee.bilibili.live.FlvChecker;

public class LiveRecordTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		byte[] arr = { 0x40, 0x5b, 0x20, (byte) 0xf5, (byte) 0xc2, (byte) (0x8f), 0x5c, 0x29 };
		System.out.printf("\n当前的值为\n%.2f \n", FlvChecker.bytes2Double(arr));
	}
	
	@Test
	public void testCheck() {
		String path = "D:\\Workspace\\javaweb-springboot\\BilibiliLiveRecord\\download\\灵药LoveLing 的直播2019-05-16 22.35.flv";
		try {
			FlvChecker.check(path);
			//FlvChecker.checkFromEnd(path);
			//FlvChecker.changeDuration(path, 0);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
