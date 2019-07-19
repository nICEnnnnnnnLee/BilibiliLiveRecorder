package nicelee.bilibili.live.impl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

import nicelee.bilibili.live.RoomDealer;
import nicelee.bilibili.live.domain.RoomInfo;
import nicelee.bilibili.util.Logger;

public class RoomDealerKuaishou extends RoomDealer{

	final public static String liver = "kuaishou";

	@Override
	public String getType() {
		return ".flv";
	}
	
	/**
	 * https://live.kuaishou.com/u/shortId
	 * 根据url的shortId获取房间信息(从网页里面爬)
	 * 
	 * @param shortId
	 * @return
	 */
	@Override
	public RoomInfo getRoomInfo(String shortId) {
		RoomInfo roomInfo = new RoomInfo();
		roomInfo.setShortId(shortId);
		try {
			// 获取基础信息
			String basicInfoUrl = String.format("https://live.kuaishou.com/u/%s",
					shortId);
			String html = util.getContent(basicInfoUrl, headers.getCommonHeaders("live.kuaishou.com"), null);
			
			
			Pattern pJson = Pattern.compile("window\\.VUE_MODEL_INIT_STATE\\['liveDetailModel'\\]=(.*);[ \r\n\t]+<\\/script>");
			Matcher matcher = pJson.matcher(html);
			matcher.find();
			Logger.println(matcher.group(1));
			JSONObject obj = new JSONObject(matcher.group(1));
			JSONObject live = obj.getJSONObject("liveStream");
			JSONObject user = obj.getJSONObject("user");
			
			// 直播状态
			if(live.has("liveStreamId")) {
				roomInfo.setLiveStatus(1);
			}else {
				roomInfo.setLiveStatus(0);
			}
			
			// 真实房间id
			roomInfo.setRoomId(shortId);


			roomInfo.setDescription(user.getString("description"));

			// 房间主id
			roomInfo.setUserId(Long.parseLong(user.getString("userId")));
			// 房间主名称
			roomInfo.setUserName(user.getString("name"));
			if(live.has("liveStreamId")) {
				roomInfo.setTitle(live.getString("caption"));
				// 清晰度
				JSONArray jArray = live.getJSONArray("playUrls");
				String[] qn = new String[jArray.length()];
				String[] qnDesc = new String[jArray.length()];
				for (int i = 0; i < jArray.length(); i++) {
					JSONObject objTemp = jArray.getJSONObject(i);
					qn[i] = "" + i;
					qnDesc[i] = objTemp.getString("quality");
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
	public String getLiveUrl(String roomId, String qn, Object...params) {
		try {
			// 获取基础信息
			String basicInfoUrl = String.format("https://live.kuaishou.com/u/%s",
					roomId);
			String html = util.getContent(basicInfoUrl, headers.getCommonHeaders("live.kuaishou.com"), null);
			
			Pattern pJson = Pattern.compile("window\\.VUE_MODEL_INIT_STATE\\['liveDetailModel'\\]=(.*);[ \r\n\t]+<\\/script>");
			Matcher matcher = pJson.matcher(html);
			matcher.find();
			JSONObject live = new JSONObject(matcher.group(1)).getJSONObject("liveStream");
			JSONArray jArray = live.getJSONArray("playUrls");
			return jArray.getJSONObject(Integer.parseInt(qn)).getString("url");
		}catch (Exception e) {
			e.fillInStackTrace();
			return null;
		}
		
	}

	@Override
	public void startRecord(String url, String fileName, String shortId) {
		util.download(url, fileName + ".flv", headers.getKuaishouLiveRecordHeaders(url, shortId));
	}

}
