package nicelee.test.junit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import nicelee.bilibili.live.domain.RoomInfo;
import nicelee.bilibili.live.impl.RoomDealerDouyu;
import nicelee.bilibili.util.JSEngine;

public class DouyuLiveRecordTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	//@Test
	public void testGetBasicInfo() {
		RoomDealerDouyu rd = new RoomDealerDouyu();
//		rd.getRoomInfo(312212L);
		rd.getRoomInfo(233233L);
	}
	
	//@Test
	public void testEncryptJS() {
		RoomDealerDouyu rd = new RoomDealerDouyu();
		RoomInfo roomInfo = rd.getRoomInfo(233233L);
		String param = JSEngine.run(
				roomInfo.getRemark(),
				"ub98484234", roomInfo.getRoomId(), "2206c59057010dd04573c76400081501","1558961099");
		System.out.println(param);
	}
	
	@Test
	public void testGetFLVLink() {
		long start = System.currentTimeMillis();
		RoomDealerDouyu rd = new RoomDealerDouyu();
		RoomInfo roomInfo = rd.getRoomInfo(233233);
		if(roomInfo.getLiveStatus() == 1) {
			rd.getLiveUrl(roomInfo.getRoomId(), roomInfo.getQualityByName("超清"), roomInfo.getRemark(), null);
		}
		
		roomInfo = rd.getRoomInfo(312212);
		if(roomInfo.getLiveStatus() == 1) {
			rd.getLiveUrl(roomInfo.getRoomId(), roomInfo.getQualityByName("超清"), roomInfo.getRemark());
		}
		
		roomInfo = rd.getRoomInfo(3487376);
		if(roomInfo.getLiveStatus() == 1) {
			rd.getLiveUrl(roomInfo.getRoomId(), roomInfo.getQualityByName("超清"), roomInfo.getRemark());
		}
		
		
		
		long end = System.currentTimeMillis();
		System.out.println("总用时：  " + (end - start) + "ms");
	}

}
