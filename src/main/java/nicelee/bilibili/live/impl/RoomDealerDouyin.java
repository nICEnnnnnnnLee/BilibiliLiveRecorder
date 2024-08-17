package nicelee.bilibili.live.impl;

import java.io.IOException;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

import nicelee.bilibili.live.RoomDealer;
import nicelee.bilibili.live.domain.RoomInfo;
import nicelee.bilibili.util.HttpCookies;
import nicelee.bilibili.util.HttpRequestUtil;
import nicelee.bilibili.util.Logger;

public class RoomDealerDouyin extends RoomDealer {

	final public static String liver = "douyin2";

	final static Pattern pShortId = Pattern.compile("live.douyin.com/([0-9]+)");
	final static Pattern pWebcastId = Pattern.compile("webcast.amemv.com/webcast/reflow/([0-9]+)");

	@Override
	public String getType() {
		return ".flv";
	}

	@Override
	public RoomInfo getRoomInfo(String shortId) {
		if (shortId.startsWith("https://v.douyin.com")) {
			try {
				URL url = new URL(shortId);
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setInstanceFollowRedirects(false);
				conn.connect();

				String location = conn.getHeaderField("Location");
				Logger.println(location);
				if (location.startsWith("https://www.iesdouyin.com")) {
					// https://www.iesdouyin.com/share/live/6825590732829657870?anchor_id=59592712724
					url = new URL(location);
					conn = (HttpURLConnection) url.openConnection();
					conn.setInstanceFollowRedirects(false);
					conn.setRequestProperty("User-Agent",
							"Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:86.0) Gecko/20100101 Firefox/86.0");
					conn.connect();
					location = conn.getHeaderField("Location");
					Logger.println(location);
				}
				if (location.startsWith("https://webcast.amemv.com")) {
					// https://webcast.amemv.com/webcast/reflow/6825590732829657870
					url = new URL(location);
					conn = (HttpURLConnection) url.openConnection();
					conn.setInstanceFollowRedirects(false);
					conn.setRequestProperty("User-Agent",
							"Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:86.0) Gecko/20100101 Firefox/86.0");
					conn.connect();
					if (conn.getResponseCode() >= 300 && conn.getResponseCode() < 400) {
						location = conn.getHeaderField("Location");
						Logger.println(location);
					} else {
						Matcher matcher = pWebcastId.matcher(location);
						matcher.find();
						String webcastId = matcher.group(1);
						shortId = getLiveDataByWebcastId(webcastId).getJSONObject("data").getJSONObject("room")
								.getJSONObject("owner").getString("web_rid");
					}

				}
				if (location != null) {
					// e.g. https://live.douyin.com/4795593332 ...
					Matcher matcher = pShortId.matcher(location);
					if (matcher.find())
						shortId = matcher.group(1);
				}
			} catch (IOException e) {
				System.err.println("不支持这种短链接的解析!!");
				throw new RuntimeException(e);
			}
		}

		RoomInfo roomInfo = new RoomInfo();
		roomInfo.setShortId(shortId);
		try {
			String roomId = shortId;
			JSONObject data = getLiveDataByShortId(roomId).getJSONObject("data");

			JSONObject user = data.getJSONObject("user");
			JSONObject room = data.getJSONArray("data").getJSONObject(0);
			JSONObject stream_url = room.optJSONObject("stream_url");

			roomInfo.setUserName(user.getString("nickname"));
			roomInfo.setRoomId(roomId);
			roomInfo.setUserId(user.optLong("id_str"));
			roomInfo.setTitle(room.getString("title"));
			roomInfo.setDescription(user.getString("nickname") + " 的直播间");
			roomInfo.setRemark(room.optString("id_str"));
			if (stream_url == null) {
				roomInfo.setLiveStatus(0);
			} else {
				roomInfo.setLiveStatus(1);
				JSONArray flv_sources = stream_url.getJSONObject("live_core_sdk_data").getJSONObject("pull_data")
						.getJSONObject("options").getJSONArray("qualities");
				int flv_sources_len = flv_sources.length();
				String[] qn = new String[flv_sources_len];
				String[] qnDesc = new String[flv_sources_len];
				for (int i = 0; i < flv_sources_len; i++) {
					// 为了让0, 1, 2, 3 数字越小清晰度越高
					JSONObject obj = flv_sources.getJSONObject(i);
					int level = obj.getInt("level");
					qn[i] = "" + i;
					qnDesc[flv_sources_len - level] = obj.getString("name");
				}
				roomInfo.setAcceptQuality(qn);
				roomInfo.setAcceptQualityDesc(qnDesc);
			}
			roomInfo.print();
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("抖音需要cookie, 请确认cookie是否存在或失效, 或尝试以下cookie");
			tryGetCookie(shortId);
			return null;
		}
		return roomInfo;
	}

	private String tryGetCookie(String shortId) {
		try {
			util.getContent("https://live.douyin.com/" + shortId, getPCHeader(), null);
			util.getContent("https://live.douyin.com/" + shortId, getPCHeader(), null);
			CookieStore cookieStore = HttpRequestUtil.DefaultCookieManager().getCookieStore();
			URI taobaoUri;
			taobaoUri = new URI("https://douyin.com");
			List<HttpCookie> cookies = cookieStore.get(taobaoUri);
			StringBuilder sb = new StringBuilder();
			for (HttpCookie c : cookies) {
				sb.append(c.getName()).append("=").append(c.getValue()).append("; ");
			}
			int len = sb.length();
			if(len > 2)
				sb.setLength(len - 2);
			String c = sb.toString();
			Logger.println(c);
			return c;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public String getLiveUrl(String roomId, String qn, Object... obj) {
		try {
			JSONObject stream_url = null;
			try {
				JSONObject data = getLiveDataByShortId(roomId).getJSONObject("data");

				JSONObject room = data.getJSONArray("data").getJSONObject(0);
				stream_url = room.optJSONObject("stream_url");
			} catch (Exception e) {
				e.printStackTrace();
				Logger.println("尝试抖音解析后备方案");
				String webcastId = (String) obj[0];
				JSONObject room = getLiveDataByWebcastId(webcastId).getJSONObject("data").getJSONObject("room");
				stream_url = room.optJSONObject("stream_url");
			}
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
			JSONObject data2 = new JSONObject(pull_data).getJSONObject("data");
			String link = data2.getJSONObject(sdk_key).getJSONObject("main").getString("flv");
			Logger.println(link);
			return link;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	JSONObject getLiveDataByShortId(String shortId) {
		String apiUrl = "https://live.douyin.com/webcast/room/web/enter/?aid=6383&live_id=1&device_platform=web"
				+ "&language=zh-CN&enter_from=web_live&cookie_enabled=true&screen_width=1536&screen_height=864"
				+ "&browser_language=zh-CN&browser_platform=Win32&browser_name=Chrome&browser_version=94.0.4606.81"
				+ "&room_id_str=&enter_source=&web_rid=" + shortId;

		String json_str = util.getContent(apiUrl, getPCHeader(), HttpCookies.convertCookies(cookie));
		Logger.println(json_str);
		return new JSONObject(json_str);
	}

	JSONObject getLiveDataByWebcastId(String webcastId) {
		String apiUrl = "https://webcast.amemv.com/webcast/room/reflow/info/?verifyFp=&type_id=0&live_id=1"
				+ "&sec_user_id=&app_id=1128&msToken=&X-Bogus=&room_id=" + webcastId;
		Logger.println(apiUrl);
		String json_str = util.getContent(apiUrl, getMobileHeader(), HttpCookies.convertCookies(cookie));
		Logger.println(json_str);
		return new JSONObject(json_str);
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
		util.download(url, fileName + ".flv", getMobileHeader());
	}

	private HashMap<String, String> mobileHeader;
	private HashMap<String, String> pcHeader;

	private HashMap<String, String> getPCHeader() {
		if (pcHeader == null) {
			pcHeader = new HashMap<>();
			pcHeader.put("User-Agent",
					"Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:86.0) Gecko/20100101 Firefox/86.0");
			pcHeader.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
			pcHeader.put("Accept-Language", "zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5,en-US;q=0.3,en;q=0.2");
		}
		return pcHeader;
	}

	private HashMap<String, String> getMobileHeader() {
		if (mobileHeader == null) {
			mobileHeader = new HashMap<>();
			mobileHeader.put("User-Agent", "Mozilla/5.0 (Android 9.0; Mobile; rv:68.0) Gecko/68.0 Firefox/68.0");
			mobileHeader.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
			mobileHeader.put("Accept-Language", "zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5,en-US;q=0.3,en;q=0.2");
		}
		return mobileHeader;
	}

}
