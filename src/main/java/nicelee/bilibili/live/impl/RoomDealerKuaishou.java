package nicelee.bilibili.live.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

import nicelee.bilibili.live.RoomDealer;
import nicelee.bilibili.live.domain.RoomInfo;
import nicelee.bilibili.util.HttpCookies;
import nicelee.bilibili.util.HttpHeaders;
import nicelee.bilibili.util.Logger;

public class RoomDealerKuaishou extends RoomDealer {

	final public static String liver = "kuaishou";

	final static Pattern pJson = Pattern.compile("window\\.__INITIAL_STATE__ *= *(\\{.*?\\}) *; *\\(function\\(\\)");

	public RoomDealerKuaishou() {
	}

	@Override
	public String getType() {
		return ".flv";
	}

	/**
	 * https://live.kuaishou.com/u/shortId 根据url的shortId获取房间信息(从网页里面爬)
	 * 
	 * @param shortId
	 * @return
	 */
	@Override
	public RoomInfo getRoomInfo(String shortId) {
		RoomInfo roomInfo = new RoomInfo();
		roomInfo.setShortId(shortId);
		try {

			JSONObject raw = getLiveInfoObj(shortId);
			JSONObject live = null;
			try {
				live = raw.getJSONObject("liveStream");
				// 直播状态信息
				if (live != null && live.getJSONObject("playUrls").getJSONObject("h264").has("adaptationSet"))
					roomInfo.setLiveStatus(1);
				else
					roomInfo.setLiveStatus(0);
			} catch (Exception e) {
				e.printStackTrace();
				roomInfo.setLiveStatus(0);
			}

			JSONObject user = raw.getJSONObject("author");
			// 真实房间id
			roomInfo.setRoomId(shortId);
			// 房间主id
			roomInfo.setUserId(user.optLong("originUserId", 0));
			// 房间主名称
			roomInfo.setUserName(user.optString("name", "空"));

			// 直播描述
			roomInfo.setDescription(user.optString("description", "无"));

			if (roomInfo.getLiveStatus() == 1) {
				roomInfo.setTitle(live.optString("caption", roomInfo.getDescription()));
				// 清晰度
				JSONArray jArray = live.getJSONObject("playUrls").getJSONObject("h264").getJSONObject("adaptationSet")
						.getJSONArray("representation");
				String[] qn = new String[jArray.length()];
				String[] qnDesc = new String[jArray.length()];
				for (int i = 0, j = jArray.length() - 1; i < jArray.length(); i++, j--) {
					JSONObject objTemp = jArray.getJSONObject(j);
//					qn[i] = "" + (objTemp.optInt("id"));
					qn[i] = "" + i;
					qnDesc[i] = objTemp.getString("name");
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

	/**
	 * 获取直播地址的下载链接
	 * 
	 * @param roomId
	 * @param qn
	 * @return
	 */
	@Override
	public String getLiveUrl(String roomId, String qn, Object... params) {
		try {
			JSONObject obj = getLiveInfoObj(roomId).getJSONObject("liveStream");
			JSONArray jArray = obj.getJSONObject("playUrls").getJSONObject("h264").getJSONObject("adaptationSet")
					.getJSONArray("representation");
			for (int i = 0, j = jArray.length() - 1; i < jArray.length(); i++, j--) {
				JSONObject objTemp = jArray.getJSONObject(j);
				if (qn.equals("" + i)) {
					Logger.println("选取质量：" + objTemp.getString("name"));
					return objTemp.getString("url");
				}
			}
			JSONObject lastObj = jArray.getJSONObject(jArray.length() - 1);
			Logger.println("没有匹配的清晰度，选取质量：" + lastObj.getString("name"));
			return lastObj.getString("url");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * @param roomId
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	private JSONObject getLiveInfoObj(String roomId) throws UnsupportedEncodingException {
		String html = util.getContent("https://live.kuaishou.com/u/" + roomId,
				new HttpHeaders().getKuaishouHeaders(roomId), HttpCookies.convertCookies(cookie));
//		Logger.println(html);
		Matcher matcher = pJson.matcher(html);
		matcher.find();
		String json_str = URLDecoder.decode(matcher.group(1), "UTF-8");
		Logger.println(json_str);
		JSONObject raw = new JSONObject(json_str).getJSONObject("liveroom");
		JSONArray plist = raw.optJSONArray("playList");
		if(plist != null && plist.length() > 0) {
			raw = plist.getJSONObject(0);
		}
		return raw;
	}

	@Override
	public void startRecord(String url, String fileName, String shortId) {
		util.download(url, fileName + ".flv", headers.getKuaishouLiveRecordHeaders(url, shortId));
	}

}
