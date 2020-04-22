package nicelee.test.junit;

import java.net.HttpCookie;
import java.util.HashMap;

import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import nicelee.bilibili.util.HttpRequestUtil;

public class AcfunLiveRecordTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
	
	/*
	 * Android手机端
POST https://api.kwaizt.com/rest/zt/live/startPlay?mod=手机品牌&subBiz=mainApp&userId=游客id&kpf=ANDROID_PHONE&did=游客did&kpn=ACFUN_APP&net=WIFI&os=android&gid=这个暂时没法整&countryCode=CN&c=貌似是个固定的&sys=ANDROID_6.0&appver=6.21.0.925&ftt=&language=zh-cn&ver=6.21 HTTP/1.1
Accept-Language: zh-cn
Connection: keep-alive
Cookie: acfun.api.visitor_st=游客st;
X-REQUESTID: 一个数字
Content-Type: application/x-www-form-urlencoded
Content-Length: 85
Host: api.kwaizt.com
Accept-Encoding: gzip
User-Agent: okhttp/3.13.1

authorId=直播间id&__clientSign=这个暂时没法整
	 */
	
	/**
	 * 测试使用已登录cookie获取直播链接(移动Web端)
	 */
	@Test
	public void testGetLiveUrlWithoutLogin() {
//		String roomId = "13945614";
		String roomId = "10515090";
		String did = null;
		
		HttpRequestUtil util = new HttpRequestUtil();
		HashMap<String, String> mobile = new HashMap<>();
		mobile.put("User-Agent", "Mozilla/5.0 (Android 9.0; Mobile; rv:68.0) Gecko/68.0 Firefox/68.0");
		mobile.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		mobile.put("Accept-Language", "zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5,en-US;q=0.3,en;q=0.2");
		mobile.put("Accept-Encoding", "gzip, deflate, br");
		// 先访问获取cookie
		util.getContent("https://m.acfun.cn/live/detail/" + roomId, mobile);
		
		for(HttpCookie cookie: HttpRequestUtil.DefaultCookieManager().getCookieStore().getCookies()) {
			if(cookie.getDomain().contains("acfun.cn") && cookie.getName().equals("_did")) {
				did = cookie.getValue();
				break;
			}
		}
		
		//游客登录，获取参数
		String url = "https://id.app.acfun.cn/rest/app/visitor/login";
		String param = "sid=acfun.api.visitor";
		
		HashMap<String, String> headers = new HashMap<>();
		headers.put("User-Agent", "Mozilla/5.0 (Android 9.0; Mobile; rv:68.0) Gecko/68.0 Firefox/68.0");
		headers.put("Accept", "application/json, text/plain, */*");
		headers.put("Accept-Language", "zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5,en-US;q=0.3,en;q=0.2");
		headers.put("Accept-Encoding", "gzip, deflate, br");
		headers.put("Content-Type", "application/x-www-form-urlencoded");
		headers.put("Origin", "https://m.acfun.cn");
		headers.put("Referer", "https://m.acfun.cn");
		
		String result = util.postContent(url, headers, param);
		System.out.println(result);
		
		JSONObject json = new JSONObject(result);
		String userId = json.optString("userId");
		String api_st = json.optString("acfun.api.visitor_st");
		url = "https://api.kuaishouzt.com/rest/zt/live/web/startPlay?subBiz=mainApp&kpn=ACFUN_APP&userId=%s&did=%s&acfun.api.visitor_st=%s";
		url = String.format(url, userId, did, api_st);
		System.out.println(url);
		
		//param = "authorId=%s&pullStreamType=SINGLE_HLS";//m3u8
		param = "authorId=%s";
		param = String.format(param, roomId);
		
		headers = new HashMap<>();
		headers.put("User-Agent", "Mozilla/5.0 (Android 9.0; Mobile; rv:68.0) Gecko/68.0 Firefox/68.0");
		headers.put("Accept", "application/json, text/plain, */*");
		headers.put("Accept-Language", "zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5,en-US;q=0.3,en;q=0.2");
		headers.put("Accept-Encoding", "gzip, deflate, br");
		headers.put("Content-Type", "application/x-www-form-urlencoded");
		headers.put("Origin", "https://m.acfun.cn");
		headers.put("Referer", "https://m.acfun.cn/live/detail/" + roomId);
		
		result = util.postContent(url, headers, param);
		System.out.println(result);
		
		JSONObject jData = new JSONObject(result).getJSONObject("data");
		String str = jData.getString("videoPlayRes");
		System.out.println(str);
	}

	/**
	 * 测试使用已登录cookie获取直播链接
	 */
	//@Test
	public void testGetLiveUrl() {
		String roomId = "13945614";
		String cookie = "_did=web_xxx; ...";
		// 从cookie获得 _did=
		String did = "xxx";

		HttpRequestUtil util = new HttpRequestUtil();
		String url = "https://id.app.acfun.cn/rest/web/token/get";
		String param = "sid=acfun.midground.api";

		HashMap<String, String> headers = new HashMap<>();
		headers.put("User-Agent", "Mozilla/5.0 (Android 9.0; Mobile; rv:68.0) Gecko/68.0 Firefox/68.0");
		headers.put("Accept", "application/json, text/plain, */*");
		headers.put("Accept-Language", "zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5,en-US;q=0.3,en;q=0.2");
		headers.put("Accept-Encoding", "gzip, deflate, br");
		headers.put("Content-Type", "application/x-www-form-urlencoded");
		headers.put("Origin", "https://m.acfun.cn");
		headers.put("Referer", "https://m.acfun.cn/live/detail/" + roomId);
		headers.put("Cookie", cookie);

		String result = util.postContent(url, headers, param);
		System.out.println(result);

		JSONObject json = new JSONObject(result);
		String userId = json.optString("userId");
		String api_st = json.optString("acfun.midground.api_st");
		url = "https://api.kuaishouzt.com/rest/zt/live/web/startPlay?subBiz=mainApp&kpn=ACFUN_APP&userId=%s&did=%s&acfun.midground.api_st=%s";
		url = String.format(url, userId, did, api_st);

		//param = "authorId=%s&pullStreamType=SINGLE_HLS";//m3u8
		param = "authorId=%s";
		param = String.format(param, roomId);

		headers = new HashMap<>();
		headers.put("User-Agent", "Mozilla/5.0 (Android 9.0; Mobile; rv:68.0) Gecko/68.0 Firefox/68.0");
		headers.put("Accept", "application/json, text/plain, */*");
		headers.put("Accept-Language", "zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5,en-US;q=0.3,en;q=0.2");
		headers.put("Accept-Encoding", "gzip, deflate, br");
		headers.put("Content-Type", "application/x-www-form-urlencoded");
		headers.put("Origin", "https://m.acfun.cn");
		headers.put("Referer", "https://m.acfun.cn/live/detail/" + roomId);

		result = util.postContent(url, headers, param);
		System.out.println(result);
	}

}
