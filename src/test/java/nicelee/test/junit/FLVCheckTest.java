package nicelee.test.junit;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import nicelee.bilibili.live.FlvChecker;
import nicelee.bilibili.live.check.FlvCheckerWithBuffer;
import nicelee.bilibili.util.Logger;

public class FLVCheckTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}


	@Test
	public void testCheckSpeed() {
		String path = "D:\\Workspace\\javaweb-springboot\\BilibiliLiveRecord\\download\\样本\\斗鱼-原始样本2.flv";
		try {
			Logger.debug = false;
			long begin1 = System.currentTimeMillis();
			new FlvChecker().check(path, false, true, null);
			long end1 = System.currentTimeMillis();
			long begin2 = System.currentTimeMillis();
			new FlvCheckerWithBuffer().check(path, false, true, null);
			long end2 = System.currentTimeMillis();
			
			System.out.println("不使用缓存花费ms：" + (end1 - begin1));
			System.out.println("使用缓存花费ms："+ (end2 - begin2));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
//	@Test
	public void testCheckValid() {
		String path = "D:\\Workspace\\javaweb-springboot\\BilibiliLiveRecord\\download\\样本\\斗鱼-原始样本2.flv";
//		String path = "D:\\Workspace\\javaweb-springboot\\BilibiliLiveRecord\\download\\样本\\斗鱼-原始样本2-checked0.flv";
		try {
			new FlvChecker().checkFromEnd(path);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

}
