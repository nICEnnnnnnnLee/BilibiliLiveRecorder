package nicelee.bilibili.live.impl;

import java.net.HttpCookie;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

import nicelee.bilibili.live.RoomDealer;
import nicelee.bilibili.live.domain.RoomInfo;
import nicelee.bilibili.util.HttpRequestUtil;
import nicelee.bilibili.util.Logger;

public class RoomDealerAcfun extends RoomDealer{


	final public static String liver = "acfun";
	
	final static Pattern pName = Pattern.compile("<title>(.*?)正在直播");
	final static Pattern pTitle = Pattern.compile("<h1 class=\"live-content-title-text\">(.*?)</h1>");
	
	@Override
	public String getType() {
		return ".flv";
	}
	
	/**
	 * @param shortId
	 * @return
	 */
	@Override
	public RoomInfo getRoomInfo(String shortId) {
		RoomInfo roomInfo = new RoomInfo();
		roomInfo.setShortId(shortId);
		try {
			String roomId = shortId;
			String did = null;
			
			HttpRequestUtil util = new HttpRequestUtil();
			HashMap<String, String> mobile = new HashMap<>();
			mobile.put("User-Agent", "Mozilla/5.0 (Android 9.0; Mobile; rv:68.0) Gecko/68.0 Firefox/68.0");
			mobile.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
			mobile.put("Accept-Language", "zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5,en-US;q=0.3,en;q=0.2");
			mobile.put("Accept-Encoding", "gzip");
			// 先访问获取cookie
			String html = util.getContent("https://m.acfun.cn/live/detail/" + roomId, mobile);
			
			roomInfo.setRoomId(roomId);
			roomInfo.setUserId(Long.parseLong(roomId));
			
			Matcher matcher = pName.matcher(html);
			matcher.find();
			roomInfo.setUserName(matcher.group(1));
			
			if(html.contains("直播已结束")) {
				roomInfo.setLiveStatus(0);
			}else {
				roomInfo.setLiveStatus(1);
				
				matcher = pTitle.matcher(html);
				matcher.find();
				roomInfo.setTitle(matcher.group(1));
				roomInfo.setDescription(roomInfo.getTitle());
				
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
				headers.put("Accept-Encoding", "gzip");
				headers.put("Content-Type", "application/x-www-form-urlencoded");
				headers.put("Origin", "https://m.acfun.cn");
				headers.put("Referer", "https://m.acfun.cn");
				
				String result = util.postContent(url, headers, param);
				Logger.println(result);
				
				JSONObject json = new JSONObject(result);
				String userId = json.optString("userId");
				String api_st = json.optString("acfun.api.visitor_st");
				url = "https://api.kuaishouzt.com/rest/zt/live/web/startPlay?subBiz=mainApp&kpn=ACFUN_APP&userId=%s&did=%s&acfun.api.visitor_st=%s";
				url = String.format(url, userId, did, api_st);
				
				//param = "authorId=%s&pullStreamType=SINGLE_HLS";//m3u8
				param = "authorId=%s";
				param = String.format(param, roomId);
				
				headers = new HashMap<>();
				headers.put("User-Agent", "Mozilla/5.0 (Android 9.0; Mobile; rv:68.0) Gecko/68.0 Firefox/68.0");
				headers.put("Accept", "application/json, text/plain, */*");
				headers.put("Accept-Language", "zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5,en-US;q=0.3,en;q=0.2");
				headers.put("Accept-Encoding", "gzip");
				headers.put("Content-Type", "application/x-www-form-urlencoded");
				headers.put("Origin", "https://m.acfun.cn");
				headers.put("Referer", "https://m.acfun.cn/live/detail/" + roomId);
				
				result = util.postContent(url, headers, param);
				Logger.println(result);
				
				// 获取直播可提供的清晰度
				JSONObject jData = new JSONObject(result).getJSONObject("data");
				JSONArray jArray = new JSONObject(jData.getString("videoPlayRes"))
						.getJSONArray("liveAdaptiveManifest")
						.getJSONObject(0)
						.getJSONObject("adaptationSet")
						.getJSONArray("representation");
				String[] qn = new String[jArray.length()];
				String[] qnDesc = new String[jArray.length()];
				// 为了使清晰度从高到低排序，此处需要降序
				for (int i = 0; i < jArray.length(); i++) {
					JSONObject obj = jArray.getJSONObject(jArray.length() - 1 - i);
					qn[i] = "" + obj.optInt("id");
					qnDesc[i] = obj.getString("name");
				}
				roomInfo.setAcceptQuality(qn);
				roomInfo.setAcceptQualityDesc(qnDesc);
			}
			roomInfo.print();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return roomInfo;
	}

	
	@Override
	public String getLiveUrl(String roomId, String qn, Object...obj) {
		try {
			String did = null;
			
			HttpRequestUtil util = new HttpRequestUtil();
			HashMap<String, String> mobile = new HashMap<>();
			mobile.put("User-Agent", "Mozilla/5.0 (Android 9.0; Mobile; rv:68.0) Gecko/68.0 Firefox/68.0");
			mobile.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
			mobile.put("Accept-Language", "zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5,en-US;q=0.3,en;q=0.2");
			mobile.put("Accept-Encoding", "gzip");
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
			headers.put("Accept-Encoding", "gzip");
			headers.put("Content-Type", "application/x-www-form-urlencoded");
			headers.put("Origin", "https://m.acfun.cn");
			headers.put("Referer", "https://m.acfun.cn");
			
			String result = util.postContent(url, headers, param);
			Logger.println(result);
			
			JSONObject json = new JSONObject(result);
			String userId = json.optString("userId");
			String api_st = json.optString("acfun.api.visitor_st");
			url = "https://api.kuaishouzt.com/rest/zt/live/web/startPlay?subBiz=mainApp&kpn=ACFUN_APP&userId=%s&did=%s&acfun.api.visitor_st=%s";
			url = String.format(url, userId, did, api_st);
			
			//param = "authorId=%s&pullStreamType=SINGLE_HLS";//m3u8
			param = "authorId=%s";
			param = String.format(param, roomId);
			
			headers = new HashMap<>();
			headers.put("User-Agent", "Mozilla/5.0 (Android 9.0; Mobile; rv:68.0) Gecko/68.0 Firefox/68.0");
			headers.put("Accept", "application/json, text/plain, */*");
			headers.put("Accept-Language", "zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5,en-US;q=0.3,en;q=0.2");
			headers.put("Accept-Encoding", "gzip");
			headers.put("Content-Type", "application/x-www-form-urlencoded");
			headers.put("Origin", "https://m.acfun.cn");
			headers.put("Referer", "https://m.acfun.cn/live/detail/" + roomId);
			
			result = util.postContent(url, headers, param);
			Logger.println(result);
			
			JSONObject jData = new JSONObject(result).getJSONObject("data");
			JSONArray jArray = new JSONObject(jData.getString("videoPlayRes"))
					.getJSONArray("liveAdaptiveManifest")
					.getJSONObject(0)
					.getJSONObject("adaptationSet")
					.getJSONArray("representation");
			for (int i = 0; i < jArray.length(); i++) {
				JSONObject obj1 = jArray.getJSONObject(i);
				if(qn.equals(obj1.optString("id"))) {
					System.out.printf("查询%s的链接，得到%s的链接\r\n", qn, qn);
					return obj1.getString("url");
				}
			}
			JSONObject obj1 = jArray.getJSONObject(0);
			System.out.printf("查询%s的链接，得到%s的链接\r\n", qn, obj1.optString("id"));
			return obj1.getString("url");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 开始录制
	 * 
	 * @param url
	 * @param fileName
	 * @param shortId
	 * @return
	 */
	@Override
	public void startRecord(String url, String fileName, String shortId) {
		util.download(url, fileName + ".flv", new HashMap<>());
	}

}
