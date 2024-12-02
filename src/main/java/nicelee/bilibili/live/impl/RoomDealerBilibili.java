package nicelee.bilibili.live.impl;

import org.json.JSONArray;
import org.json.JSONObject;

import nicelee.bilibili.live.RoomDealer;
import nicelee.bilibili.live.domain.RoomInfo;
import nicelee.bilibili.util.Logger;

public class RoomDealerBilibili extends RoomDealer{


	final public static String liver = "bili";
	
	@Override
	public String getType() {
		return ".flv";
	}
	
	/**
	  *  根据url的shortId获取房间信息(从api获取，查询了 3 次)
	 * @param shortId
	 * @return
	 */
	@Override
	public RoomInfo getRoomInfo(String shortId) {
		RoomInfo roomInfo = new RoomInfo();
		roomInfo.setShortId(shortId);
		try {
			// 获取基础信息
			String basicInfoUrl = String.format("https://api.live.bilibili.com/room/v1/Room/get_info?id=%s&from=room", shortId);
			String jsonStr = util.getContent(basicInfoUrl, headers.getBiliLiveJsonAPIHeaders(Long.parseLong(shortId)), null);
			Logger.println(jsonStr);
			
			if(jsonStr.isEmpty()) {
				roomInfo.setTitle("网络超时，尚未获得信息");
				roomInfo.setLiveStatus(0);
			}else {
				JSONObject jObj = new JSONObject(jsonStr).getJSONObject("data");
				roomInfo.setRoomId("" + jObj.getLong("room_id"));
				roomInfo.setUserId(jObj.getLong("uid"));
				roomInfo.setTitle(jObj.getString("title"));
				roomInfo.setDescription(jObj.getString("description"));// .replaceAll("</?(h4|a|p|span)[^>]*>", " "));
				roomInfo.setLiveStatus(jObj.getInt("live_status"));
			}

			if (roomInfo.getLiveStatus() == 1) {
				// 获取该房间的主播信息 - 名称等
				String liverInfoUrl = String.format("https://api.live.bilibili.com/live_user/v1/UserInfo/get_anchor_in_room?roomid=%s",
						roomInfo.getRoomId());
				String jsonLiverStr = util.getContent(liverInfoUrl, headers.getBiliLiveJsonAPIHeaders(Long.parseLong(shortId)), null);
				Logger.println(jsonLiverStr);
				String uname = new JSONObject(jsonLiverStr).getJSONObject("data").getJSONObject("info").getString("uname");
				roomInfo.setUserName(uname);
				
				// 获取直播可提供的清晰度
				JSONObject jData = getLiveJson("" + roomInfo.getRoomId(), "4").getJSONObject("data");
				JSONArray jArray = jData.getJSONArray("quality_description");
				String[] qn = new String[jArray.length()];
				String[] qnDesc = new String[jArray.length()];
				for (int i = 0; i < jArray.length(); i++) {
					JSONObject obj = jArray.getJSONObject(i);
					qn[i] = "" + obj.getInt("qn");
					qnDesc[i] = obj.getString("desc");
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
	 *  获取直播源的相关信息json
	 * @param roomId
	 * @param qn
	 * @return
	 */
	private JSONObject getLiveJson(String roomId, String qn) {
		String url = String.format("https://api.live.bilibili.com/room/v1/Room/playUrl?cid=%s&quality=%s&platform=web",
				roomId, qn);
		String jsonStr = util.getContent(url, headers.getBiliLiveJsonAPIHeaders(Long.parseLong(roomId)), null);
		Logger.println(url);
		Logger.println(jsonStr);
		JSONObject obj = new JSONObject(jsonStr);
		return obj;
	}

	@Override
	public String getLiveUrl(String shortId, String qn, Object...obj) {
		try {
			JSONObject json = getLiveJson("" + shortId, qn);
			JSONArray jObj = json.getJSONObject("data").getJSONArray("durl");
			return jObj.getJSONObject(0).getString("url");
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
		util.download(url, fileName + ".flv", headers.getBiliLiveRecordHeaders(url, Long.parseLong(shortId)));
	}

}
