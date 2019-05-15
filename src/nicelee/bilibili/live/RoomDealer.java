package nicelee.bilibili.live;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
	 * 根据url的shortId获取房间信息 （从网页里面爬的）
	 * 
	 * @param shortId
	 * @return
	 */
	final static Pattern userNamePattern = Pattern.compile("- ([^-]+)- 哔哩哔哩直播，二次元弹幕直播平台</title>");
	public RoomInfo getRoomInfo(long shortId) {

		RoomInfo roomInfo = new RoomInfo();
		roomInfo.setShortId(shortId);

		String url = String.format("https://live.bilibili.com/%d", shortId);
		String html;
		try {
			html = util.getContent(url, headers.getCommonHeaders("live.bilibili.com"), null);
			Matcher matcher = userNamePattern.matcher(html);
			if(matcher.find()) {
				roomInfo.setUserName(matcher.group(1));
			}
			int begin = html.indexOf("window.__NEPTUNE_IS_MY_WAIFU__={");
			int end = html.indexOf("</script>", begin);
			String jsonStr = html.substring(begin + 31, end);
			Logger.println(jsonStr);

			JSONObject jObj = new JSONObject(jsonStr).getJSONObject("baseInfoRes").getJSONObject("data");
			roomInfo.setRoomId(jObj.getLong("room_id"));
			roomInfo.setUserId(jObj.getLong("uid"));
			roomInfo.setTitle(jObj.getString("title"));
			roomInfo.setDescription(jObj.getString("description"));// .replaceAll("</?(h4|a|p|span)[^>]*>", " "));
			roomInfo.setLiveStatus(jObj.getInt("live_status"));

			if (roomInfo.getLiveStatus() == 1) {
				JSONObject jData = new JSONObject(jsonStr).getJSONObject("playUrlRes").getJSONObject("data");
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
	 * 获取直播地址的下载链接
	 * 
	 * @param shortId
	 * @param qn
	 * @return
	 */
	public String getLiveUrl(long shortId, String qn) {
		String url = String.format("https://api.live.bilibili.com/room/v1/Room/playUrl?cid=%d&quality=%s&platform=web",
				shortId, qn);
		//System.out.println(url);
		String jsonStr = util.getContent(url, headers.getBiliLiveJsonAPIHeaders(shortId), null);
		try {
			Logger.println(jsonStr);
			JSONArray jObj = new JSONObject(jsonStr).getJSONObject("data").getJSONArray("durl");
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

	// 获取房间主人信息
	// https://api.live.bilibili.com/live_user/v1/UserInfo/get_anchor_in_room?roomid=11090072
	/*
	 * {"code":0,"msg":"success","message":"success","data":{"info":{"uid":325164925
	 * ,"uname":"Fireloli","face":
	 * "https://i0.hdslb.com/bfs/face/77ce6a1a491b68f14ab2e86aec46d027bd4afdf8.jpg",
	 * "rank":"10000","identification":1,"mobile_verify":1,"platform_user_level":6,
	 * "vip_type":1,"gender":0,"official_verify":{"type":0,"desc":"bilibili直播签约主播",
	 * "role":2}},"level":{"uid":325164925,"cost":15070100,"rcost":1710549151,
	 * "user_score":"0","vip":0,"vip_time":"0000-00-0000:00:00","svip":0,
	 * "svip_time":"0000-00-00 00:00:00","update_time":"2019-05-15 14:39:32"
	 * ,"master_level":{"level":31,"current":[3730000,15613810],"next":[5000000,
	 * 20613810],"color":16746162,"anchor_score":17105491,"upgrade_score":3508319,
	 * "master_level_color":16746162,"sort":371},"user_level":20,"color":6406234,
	 * "anchor_score":17105491},"san":12}}
	 */

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
