package nicelee.test.junit;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import nicelee.bilibili.live.domain.RoomInfo;
import nicelee.bilibili.live.impl.RoomDealerZhanqi;

public class ZhanqiLiveRecordTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	//@Test
	public void testGenLiveURL() {
//		System.setProperty("proxyHost", "127.0.0.1");
//		System.setProperty("proxyPort", "8888");
		RoomDealerZhanqi rd = new RoomDealerZhanqi();
		String url = rd.getLiveUrl("huashan", "0");
		System.out.println(url);
	}
	
	@Test
	public void test() {
		System.setProperty("proxyHost", "127.0.0.1");
		System.setProperty("proxyPort", "8888");
		RoomDealerZhanqi rd = new RoomDealerZhanqi();
		RoomInfo roomInfo = rd.getRoomInfo("huashan");
		roomInfo.getDescription();
	}

}
