package nicelee.test.junit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import nicelee.bilibili.live.impl.RoomDealerYY;
import nicelee.bilibili.util.Logger;

public class YYLiveRecordTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetBasicInfo() {
		RoomDealerYY rd = new RoomDealerYY();
//		rd.getRoomInfo("1330802494");
		rd.getLiveUrl("1330802494", "");
		Logger.println(System.currentTimeMillis());
		// 5715753189491779538
		// 96597706038372544
		//       1559400482712
	}
	
}
