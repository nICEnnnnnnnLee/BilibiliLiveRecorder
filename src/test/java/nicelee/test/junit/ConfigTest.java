package nicelee.test.junit;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import nicelee.bilibili.Config;
import nicelee.bilibili.util.Logger;

public class ConfigTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	//@Test
	public void testDefault() {
		 String args[] = new String[] {"&"};
		 Config.init(args);
		 assertEquals(false, Logger.debug);
		 assertEquals(true, Config.autoCheck);
		 assertEquals(true, Config.deleteOnchecked);
		 assertEquals(false, Config.flagZip);
		 assertEquals("bili", Config.liver);
		 assertEquals(null, Config.shortId);
		 assertEquals(null, Config.qn);
		 assertArrayEquals(null, Config.qnPriority);
		 assertEquals(5, Config.maxFailCnt);
		 assertEquals(0, Config.splitFileSize);
		 assertEquals(0, Config.splitRecordPeriod);
		 assertEquals(false, Config.splitScriptTagsIfCheck);
		 assertEquals(false, Config.splitAVHeaderTagsIfCheck);
		 assertEquals("{name}-{shortId} 的{liver}直播{startTime}-{seq}", Config.fileName);
		 assertEquals("yyyy-MM-dd HH.mm", Config.timeFormat);
		 assertEquals(null, Config.saveFolder);
		 assertEquals(null, Config.saveFolderAfterCheck);
		 assertEquals(false, Config.retryIfLiveOff);
		 assertEquals(0, Config.maxRetryIfLiveOff);
		 assert(5.0 ==  Config.retryAfterMinutes);
		 assertEquals(false, Config.flagPlugin);
	}
	
	//@Test
	public void testConfigFromArgs() {
		String args[] = new String[] {"debug=true&check=false&delete=false&zip=true&liver=douyu&id=666&qn=1000&qnPri=蓝光4M>蓝光>超清&retry=8&fileSize=2"
				+ "&filePeriod=3&splitScriptTags=true&fileName=hhhhh&timeFormat=yyyy-MM-dd&saveFolder=D://test1/kong ge/"
				+ "&saveFolderAfterCheck=D://test1/kong ge/&retryIfLiveOff=true&maxRetryIfLiveOff=8&retryAfterMinutes=9.5&plugin=true"
//				+ "&splitAVHeaderTags"
		};
		Config.init(args);
		assertEquals(true, Logger.debug);
		assertEquals(false, Config.autoCheck);
		assertEquals(false, Config.deleteOnchecked);
		assertEquals(true, Config.flagZip);
		assertEquals("douyu", Config.liver);
		assertEquals("666", Config.shortId);
		assertEquals("1000", Config.qn);
		assertArrayEquals(new String[] {"蓝光4M", "蓝光", "超清"}, Config.qnPriority);
		assertEquals(8, Config.maxFailCnt);
		assertEquals(2*1024*1024, Config.splitFileSize);
		assertEquals(3*60*1000, Config.splitRecordPeriod);
		assertEquals(true, Config.splitScriptTagsIfCheck);
		assertEquals(true, Config.splitAVHeaderTagsIfCheck);
		assertEquals("hhhhh", Config.fileName);
		assertEquals("yyyy-MM-dd", Config.timeFormat);
		assertEquals("D://test1/kong ge/", Config.saveFolder);
		assertEquals("D://test1/kong ge/", Config.saveFolderAfterCheck);
		assertEquals(true, Config.retryIfLiveOff);
		assertEquals(8, Config.maxRetryIfLiveOff);
		assert(9.5 ==  Config.retryAfterMinutes);
		assertEquals(true, Config.flagPlugin);
	}
	
	@Test
	public void testConfigFromJson() {
		String args[] = new String[] {"options=D:\\Workspace\\javaweb-springboot\\BilibiliLiveRecord\\release\\config.json"
				+ "&liver=huya"
		};
		Config.init(args);
		assertEquals(true, Logger.debug);
		assertEquals(false, Config.autoCheck);
		assertEquals(false, Config.deleteOnchecked);
		assertEquals(true, Config.flagZip);
		assertEquals("huya", Config.liver);
		assertEquals("666", Config.shortId);
		assertEquals("1000", Config.qn);
		assertArrayEquals(new String[] {"蓝光4M", "蓝光", "超清"}, Config.qnPriority);
		assertEquals(8, Config.maxFailCnt);
		assertEquals(2*1024*1024, Config.splitFileSize);
		assertEquals(3*60*1000, Config.splitRecordPeriod);
		assertEquals(true, Config.splitScriptTagsIfCheck);
		assertEquals(true, Config.splitAVHeaderTagsIfCheck);
		assertEquals("hhhhh", Config.fileName);
		assertEquals("yyyy-MM-dd", Config.timeFormat);
		assertEquals("D://test1/kong ge/", Config.saveFolder);
		assertEquals("D://test1/kong ge/", Config.saveFolderAfterCheck);
		assertEquals(true, Config.retryIfLiveOff);
		assertEquals(8, Config.maxRetryIfLiveOff);
		assert(9.5 ==  Config.retryAfterMinutes);
		assertEquals(true, Config.flagPlugin);
	}

}
