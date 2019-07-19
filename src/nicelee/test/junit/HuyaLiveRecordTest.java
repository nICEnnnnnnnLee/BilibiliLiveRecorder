package nicelee.test.junit;

import java.io.IOException;
import java.util.HashMap;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import nicelee.bilibili.live.FlvChecker;
import nicelee.bilibili.live.impl.RoomDealerHuya;
import nicelee.bilibili.util.HttpHeaders;
import nicelee.bilibili.util.HttpRequestUtil;

public class HuyaLiveRecordTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	//@Test
	public void testCheck() {
		String path = "D:\\Workspace\\javaweb-springboot\\BilibiliLiveRecord\\download\\test1.flv.flv";
		try {
			FlvChecker.check(path);
//			 FlvChecker.checkFromEnd(path);
			// FlvChecker.changeDuration(path, 0);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testDownloadM3u8() {
		
		RoomDealerHuya rd = new RoomDealerHuya();
		new Thread(new Runnable() {
			@Override
			public void run() {
				System.out.println("开始");
				
				rd.startRecord("http://ws4.streamhls.huya.com/hqlive/90077-2562758913-11006965718867509248-34850612-10057-A-0-1.m3u8?wsSecret=85316cdb59546ecf6205bceb5a9efbab&wsTime=27f6df13&&sphdcdn=al_2-tx_2-js_2-ws_2-bd_2-hw_2&sphdDC=huya&sphd=264_*", 
						"虎牙直播", null);
				System.out.println("结束");
			}
		}).start();

		try {
			Thread.sleep(20000);
			rd.stopRecord();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	//@Test
	public void testDownload() {
		
		HttpRequestUtil util = new HttpRequestUtil();
		new Thread(new Runnable() {
			@Override
			public void run() {
				System.out.println("开始");
				
				HttpHeaders headers = new HttpHeaders();
				HashMap<String, String> headerMap = headers.getHeaders();
				headerMap.put("Accept", "*/*");
				headerMap.put("Accept-Encoding", "gzip, deflate, sdch, br");
				headerMap.put("Accept-Language", "zh-CN,zh;q=0.8");
				headerMap.put("Host", "js.p2p.huya.com");
				headerMap.put("Origin", "https://www.huya.com");
				headerMap.put("Referer", "https://www.huya.com/baozha");
				headerMap.put("User-Agent",
						"Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36 SE 2.X MetaSr 1.0");
				util.download(
						"https://js.p2p.huya.com/huyalive/90077-2562758913-11006965718867509248-34850612-10057-A-0-1_405_0_66.slice?wsSecret=4bf4894fae77f2ea519917674e2a74c5&wsTime=5cef49a0&fs=bgct&&sphdcdn=al_2-tx_2-js_2-ws_2-bd_2-hw_2&sphdDC=huya&sphd=264_*&ex1=0&baseIndex=0&quickTime=2000&timeStamp=2019-05-30_11:16:45.273&u=7326302074&t=100&sv=1905281454",
						"test1.flv", headerMap);
				System.out.println("结束");
			}
		}).start();
		
		try {
			Thread.sleep(10000);
			util.stopDownload();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
