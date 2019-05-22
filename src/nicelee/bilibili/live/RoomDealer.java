package nicelee.bilibili.live;

import org.json.JSONArray;
import org.json.JSONObject;

import nicelee.bilibili.live.domain.RoomInfo;
import nicelee.bilibili.util.HttpHeaders;
import nicelee.bilibili.util.HttpRequestUtil;
import nicelee.bilibili.util.Logger;

public class RoomDealer {

	public HttpRequestUtil util = new HttpRequestUtil();
	HttpHeaders headers = new HttpHeaders();

	/**
	  *  根据url的shortId获取房间信息(从api获取，查询了 3 次)
	 * @param shortId
	 * @return
	 */
	public RoomInfo getRoomInfo(long shortId) {
		RoomInfo roomInfo = new RoomInfo();
		roomInfo.setShortId(shortId);
		try {
			// 获取基础信息
			String basicInfoUrl = String.format("https://api.live.bilibili.com/room/v1/Room/get_info?id=%d&from=room", shortId);
			String jsonStr = util.getContent(basicInfoUrl, headers.getBiliLiveJsonAPIHeaders(shortId), null);
			Logger.println(jsonStr);

			JSONObject jObj = new JSONObject(jsonStr).getJSONObject("data");
			roomInfo.setRoomId(jObj.getLong("room_id"));
			roomInfo.setUserId(jObj.getLong("uid"));
			roomInfo.setTitle(jObj.getString("title"));
			roomInfo.setDescription(jObj.getString("description"));// .replaceAll("</?(h4|a|p|span)[^>]*>", " "));
			roomInfo.setLiveStatus(jObj.getInt("live_status"));

			if (roomInfo.getLiveStatus() == 1) {
				// 获取该房间的主播信息 - 名称等
				String liverInfoUrl = String.format("https://api.live.bilibili.com/live_user/v1/UserInfo/get_anchor_in_room?roomid=%d",
						roomInfo.getRoomId());
				String jsonLiverStr = util.getContent(liverInfoUrl, headers.getBiliLiveJsonAPIHeaders(shortId), null);
				String uname = new JSONObject(jsonLiverStr).getJSONObject("data").getJSONObject("info").getString("uname");
				roomInfo.setUserName(uname);
				
				// 获取直播可提供的清晰度
				JSONObject jData = getLiveJson(roomInfo.getRoomId(), "4").getJSONObject("data");
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
	private JSONObject getLiveJson(long roomId, String qn) {
		String url = String.format("https://api.live.bilibili.com/room/v1/Room/playUrl?cid=%d&quality=%s&platform=web",
				roomId, qn);
		String jsonStr = util.getContent(url, headers.getBiliLiveJsonAPIHeaders(roomId), null);
		Logger.println(url);
		Logger.println(jsonStr);
		JSONObject obj = new JSONObject(jsonStr);
		return obj;
	}
	
	/**
	 * 获取直播地址的下载链接
	 * 
	 * @param shortId
	 * @param qn
	 * @return
	 */
	public String getLiveUrl(long shortId, String qn) {
		try {
			JSONObject json = getLiveJson(shortId, qn);
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
	public void startRecord(String url, String fileName, long shortId) {
		util.download(url, fileName, headers.getBiliLiveRecordHeaders(url, shortId));
	}

	/**
	 * 停止录制
	 */
	public void stopRecord() {
		util.stopDownload();
	}

	// 根据主播id 查找房间
	// https://api.live.bilibili.com/room/v1/Room/getRoomInfoOld?mid=393403683
	// {"code":0,"msg":"ok","message":"ok","data":{"roomStatus":1,"roundStatus":0,"liveStatus":1,"url":"https://live.bilibili.com/21154133","title":"今天超热呐","cover":"https://i0.hdslb.com/bfs/live/user_cover/8c27287513c3e8a96d0737fe0c8de9a4fe7a7587.jpg","online":1773,"roomid":21154133,"broadcast_type":1}}
	/*
	 * GET /room/v1/Room/getRoomInfoOld?mid=393403683 HTTP/1.1 Host:
	 * api.live.bilibili.com Connection: keep-alive Accept: application/json,
	 * text/plain, Origin: https://space.bilibili.com User-Agent: Mozilla/5.0
	 * (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko)
	 * Chrome/58.0.3029.110 Safari/537.36 SE 2.X MetaSr 1.0 Referer:
	 * https://space.bilibili.com/393403683/ Accept-Encoding: gzip, deflate, sdch,
	 * br Accept-Language: zh-CN,zh;q=0.8
	 */

	/**
	 * 文件大小转换为字符串
	 * 
	 * @param size
	 * @return
	 */
	final static long KB = 1024L;
	final static long MB = KB * 1024L;
	final static long GB = MB * 1024L;

	public static String transToSizeStr(long size) {
		if (size == 0) {
			return "未知";
		}
		double dSize;
		if (size >= GB) {
			dSize = size * 1.0 / GB;
			return String.format("%.2f GB", dSize);
		} else if (size >= MB) {
			dSize = size * 1.0 / MB;
			return String.format("%.2f MB", dSize);
		} else if (size >= KB) {
			dSize = size * 1.0 / KB;
			return String.format("%.2f KB", dSize);
		}
		return size + " Byte";
	}

}
