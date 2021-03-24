package nicelee.bilibili.live.impl;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

import nicelee.bilibili.live.RoomDealer;
import nicelee.bilibili.live.domain.RoomInfo;
import nicelee.bilibili.util.HttpRequestUtil;

public class RoomDealerDouyin extends RoomDealer {

	final public static String liver = "douyin";

	final static Pattern pJson = Pattern.compile("<script>window.__INIT_PROPS__ *= *(.*?)</script>");
	final static Pattern pShortId = Pattern.compile("webcast.amemv.com/webcast/reflow/([0-9]+)");

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
		if(shortId.startsWith("https://v.douyin.com")) {
			try {
				URL url = new URL(shortId);
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setInstanceFollowRedirects(false);
				conn.connect();
				
				String location = conn.getHeaderField("Location");
				System.out.println(location);
				if(location != null) {
					Matcher matcher = pShortId.matcher(location);
					if(matcher.find())
						shortId = matcher.group(1);
				}
			} catch (IOException e) {
			}
		}
		
		
		RoomInfo roomInfo = new RoomInfo();
		roomInfo.setShortId(shortId);
		try {
			String roomId = shortId;

			HttpRequestUtil util = new HttpRequestUtil();
			HashMap<String, String> mobile = new HashMap<>();
			mobile.put("User-Agent", "Mozilla/5.0 (Android 9.0; Mobile; rv:68.0) Gecko/68.0 Firefox/68.0");
			mobile.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
			mobile.put("Accept-Language", "zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5,en-US;q=0.3,en;q=0.2");
			mobile.put("Accept-Encoding", "gzip");
			String html = util.getContent("https://webcast.amemv.com/webcast/reflow/" + roomId, mobile);

			Matcher matcher = pJson.matcher(html);
			matcher.find();
			JSONObject json = new JSONObject(matcher.group(1)).getJSONObject("/webcast/reflow/:id")
					.getJSONObject("room");
			JSONObject owner = json.getJSONObject("owner");
			JSONObject stream_url = json.getJSONObject("stream_url");
			
			
			roomInfo.setUserName(owner.getString("nickname"));
			roomInfo.setRoomId(roomId);
			roomInfo.setUserId(owner.optLong("short_id"));
			if(html.contains("直播已结束")) {
				roomInfo.setLiveStatus(0);
			}else {
				roomInfo.setLiveStatus(1);
				roomInfo.setTitle(json.getString("title"));
				roomInfo.setDescription(owner.getString("signature"));
				JSONArray jArray = stream_url.getJSONObject("live_core_sdk_data").getJSONObject("pull_data").getJSONObject("options").getJSONArray("qualities");
				
				String[] qn = new String[jArray.length()];
				String[] qnDesc = new String[jArray.length()];
				for (int i = 0; i < jArray.length(); i++) {
					JSONObject obj = jArray.getJSONObject(i);
					qn[i] = "" + obj.optInt("level");
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
	public String getLiveUrl(String roomId, String qn, Object... obj) {
		try {
			HttpRequestUtil util = new HttpRequestUtil();
			HashMap<String, String> mobile = new HashMap<>();
			mobile.put("User-Agent", "Mozilla/5.0 (Android 9.0; Mobile; rv:68.0) Gecko/68.0 Firefox/68.0");
			mobile.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
			mobile.put("Accept-Language", "zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5,en-US;q=0.3,en;q=0.2");
			mobile.put("Accept-Encoding", "gzip");
			String html = util.getContent("https://webcast.amemv.com/webcast/reflow/" + roomId, mobile);

			Matcher matcher = pJson.matcher(html);
			matcher.find();
			JSONObject json = new JSONObject(matcher.group(1)).getJSONObject("/webcast/reflow/:id")
					.getJSONObject("room");
			JSONObject stream_url = json.getJSONObject("stream_url");

			System.out.printf("查询%s的链接，得到默认链接\r\n", qn);
			return stream_url.getString("rtmp_pull_url");
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
		HashMap<String, String> mobile = new HashMap<>();
		mobile.put("User-Agent", "Mozilla/5.0 (Android 9.0; Mobile; rv:68.0) Gecko/68.0 Firefox/68.0");
		util.download(url, fileName + ".flv", mobile);
	}

}
