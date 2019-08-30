package nicelee.test.junit;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import nicelee.bilibili.live.FlvChecker;
import nicelee.bilibili.live.impl.RoomDealerKuaishou;

public class KuaishouLiveRecordTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		RoomDealerKuaishou rd = new RoomDealerKuaishou();
		//rd.getRoomInfo("ZFYS8888");
		rd.getRoomInfo("tianmei88888");
	}
	
	//@Test
	public void testCheck() {
		String path = "D:\\Workspace\\javaweb-springboot\\BilibiliLiveRecord\\download\\哔哩哔哩英雄联盟赛事-6 的直播 2019-05-18 17.43.flv";
		try {
			new FlvChecker().check(path, false);
			//FlvChecker.checkFromEnd(path);
			//FlvChecker.changeDuration(path, 0);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
