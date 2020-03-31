package nicelee.test.junit;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import nicelee.bilibili.live.impl.RoomDealerHuajiao;
import nicelee.bilibili.util.Logger;

public class HuajiaoLiveRecordTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		RoomDealerHuajiao rd = new RoomDealerHuajiao();
		rd.getRoomInfo("278581432");
		Logger.print(rd.getLiveUrl("278581432", ""));
	}
	

}
