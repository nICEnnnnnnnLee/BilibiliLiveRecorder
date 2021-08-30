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
import nicelee.bilibili.util.Logger;

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
		if (shortId.startsWith("https://v.douyin.com")) {
			try {
				URL url = new URL(shortId);
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setInstanceFollowRedirects(false);
				conn.setRequestProperty("User-Agent",
						"Mozilla/5.0 (Android 9.0; Mobile; rv:68.0) Gecko/68.0 Firefox/68.0");
				conn.connect();
				String location = conn.getHeaderField("Location");
				Logger.println(location);
				if (location != null && location.startsWith("https://www.iesdouyin.com")) {
					url = new URL(location);
					conn = (HttpURLConnection) url.openConnection();
					conn.setInstanceFollowRedirects(false);
					conn.setRequestProperty("User-Agent",
							"Mozilla/5.0 (Android 9.0; Mobile; rv:68.0) Gecko/68.0 Firefox/68.0");
					conn.connect();
					location = conn.getHeaderField("Location");
					Logger.println(location);
				}

				if (location != null && location.startsWith("https://webcast.amemv.com")) {
					Matcher matcher = pShortId.matcher(location);
					if (matcher.find())
						shortId = matcher.group(1);
				}
			} catch (IOException e) {
			}
		}

		RoomInfo roomInfo = new RoomInfo();
		roomInfo.setShortId(shortId);
		try {
			String roomId = shortId;

			HashMap<String, String> mobile = new HashMap<>();
			mobile.put("User-Agent", "Mozilla/5.0 (Android 9.0; Mobile; rv:68.0) Gecko/68.0 Firefox/68.0");
			mobile.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
			mobile.put("Accept-Language", "zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5,en-US;q=0.3,en;q=0.2");
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
			if (html.contains("直播已结束")) {
				roomInfo.setLiveStatus(0);
			} else {
				roomInfo.setLiveStatus(1);
				roomInfo.setTitle(json.getString("title"));
				roomInfo.setDescription(owner.getString("signature"));
				JSONArray jArray = stream_url.getJSONObject("live_core_sdk_data").getJSONObject("pull_data")
						.getJSONObject("options").getJSONArray("qualities");
				int qualityLen = jArray.length();
				String[] qn = new String[qualityLen];
				String[] qnDesc = new String[qualityLen];
				for (int i = 0; i < qualityLen; i++) {
					JSONObject obj = jArray.getJSONObject(i);
					int level = obj.optInt("level");
					qn[i] = "" + i;
					qnDesc[qualityLen - level] = obj.getString("name");
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
			HashMap<String, String> mobile = new HashMap<>();
			mobile.put("User-Agent", "Mozilla/5.0 (Android 9.0; Mobile; rv:68.0) Gecko/68.0 Firefox/68.0");
			mobile.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
			mobile.put("Accept-Language", "zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5,en-US;q=0.3,en;q=0.2");
			String html = util.getContent("https://webcast.amemv.com/webcast/reflow/" + roomId, mobile);

			Matcher matcher = pJson.matcher(html);
			matcher.find();
			JSONObject json = new JSONObject(matcher.group(1)).getJSONObject("/webcast/reflow/:id")
					.getJSONObject("room");
			JSONObject stream_url = json.getJSONObject("stream_url");

			JSONArray flv_sources = stream_url.getJSONObject("live_core_sdk_data").getJSONObject("pull_data")
					.getJSONObject("options").getJSONArray("qualities");
			int flv_sources_len = flv_sources.length();
			String sdk_key = null;
			for (int i = 0; i < flv_sources_len; i++) {
				JSONObject quality = flv_sources.getJSONObject(i);
				int level = quality.getInt("level");
				// 这里要与前面一致
				if (qn.equals("" + (flv_sources_len - level))) {
					sdk_key = quality.getString("sdk_key");
					break;
				}
			}

			String pull_data = stream_url.getJSONObject("live_core_sdk_data").getJSONObject("pull_data")
					.getString("stream_data");
			JSONObject data = new JSONObject(pull_data).getJSONObject("data");
			String link = data.getJSONObject(sdk_key).getJSONObject("main").getString("flv");
			Logger.println(link);
			return link;
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
