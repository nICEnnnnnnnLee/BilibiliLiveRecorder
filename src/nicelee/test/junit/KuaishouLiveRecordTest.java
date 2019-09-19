package nicelee.test.junit;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import nicelee.bilibili.live.FlvChecker;
import nicelee.bilibili.live.impl.RoomDealerKuaishou;
import nicelee.bilibili.util.HttpCookies;
import nicelee.bilibili.util.HttpHeaders;
import nicelee.bilibili.util.HttpRequestUtil;

public class KuaishouLiveRecordTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testCopyFiles() {
		RoomDealerKuaishou rd = new RoomDealerKuaishou();
		HttpRequestUtil util = new HttpRequestUtil();
		String param = "{\"operationName\":\"userInfoQuery\",\"variables\":{\"principalId\":\"Yxlmhuige\"},\"query\":\"query userInfoQuery($principalId: String) {\\n  userInfo(principalId: $principalId) {\\n    id\\n    principalId\\n    kwaiId\\n    eid\\n    userId\\n    profile\\n    name\\n    description\\n    sex\\n    constellation\\n    cityName\\n    living\\n    watchingCount\\n    isNew\\n    privacy\\n    feeds {\\n      eid\\n      photoId\\n      thumbnailUrl\\n      timestamp\\n      __typename\\n    }\\n    verifiedStatus {\\n      verified\\n      description\\n      type\\n      new\\n      __typename\\n    }\\n    countsInfo {\\n      fan\\n      follow\\n      photo\\n      liked\\n      open\\n      playback\\n      private\\n      __typename\\n    }\\n    bannedStatus {\\n      banned\\n      defriend\\n      isolate\\n      socialBanned\\n      __typename\\n    }\\n    __typename\\n  }\\n}\\n\"}";
		String result = util.postContent("https://live.kuaishou.com/graphql", 
				new HttpHeaders().getKuaishouHeaders("Yxlmhuige"), 
				param, HttpCookies.convertCookies("clientid=3; did=web_b49724cc62a3b45da08e16dddf593af4; client_key=65890b29; didv=1563932749000; kuaishou.live.bfb1s=3e261140b0cf7444a0ba411c6f227d88; needLoginToWatchHD=1"));
		System.out.println(param);
		System.out.println(result);
	}
//	@Test
	public void test() {
		RoomDealerKuaishou rd = new RoomDealerKuaishou();
		//rd.getRoomInfo("ZFYS8888");
		rd.getRoomInfo("fengyeYS");
	}
	
//	@Test
	public void testGetFLVUrl() {
		RoomDealerKuaishou rd = new RoomDealerKuaishou();
		//rd.getRoomInfo("ZFYS8888");
		System.out.println(rd.getLiveUrl("fengyeYS", "0"));
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
