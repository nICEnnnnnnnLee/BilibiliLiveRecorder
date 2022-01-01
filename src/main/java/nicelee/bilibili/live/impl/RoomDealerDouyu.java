package nicelee.bilibili.live.impl;

import java.net.HttpCookie;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

import nicelee.bilibili.live.RoomDealer;
import nicelee.bilibili.live.domain.RoomInfo;
import nicelee.bilibili.util.HttpCookies;
import nicelee.bilibili.util.JSEngine;
import nicelee.bilibili.util.Logger;

public class RoomDealerDouyu extends RoomDealer {

	final public static String liver = "douyu";

	@Override
	public String getType() {
		return ".flv";
	}

	private final static Pattern pDyID = Pattern.compile("dy_did ?= ?([^,; ]+)");
	private final static String ttIdRandom = "2206c59057010dd04573c76400081501";
	private final static String encryptMethod = "ub98484234";
	private final static String version = "Douyu_219052705"; // ver 版本
	private final static Pattern pLiveStatus = Pattern.compile("\\$ROOM.show_status ?= ?([0-9]+);");
	private final static Pattern pRoomId = Pattern.compile("\\$ROOM.room_id ?= ?([0-9]+);");
	private final static Pattern pTitle = Pattern.compile("<h[0-9] class=\"Title-headlineH2\">([^/]*)</h[0-9]>");
	private final static Pattern pTitle2 = Pattern.compile("<title>([^/]*)</title>");
	private final static Pattern pDescript = Pattern
			.compile("<div class=\"AnchorAnnounce\"><h3><span>([^/]*)</span></h3></div>");
	private final static Pattern pUserId = Pattern.compile("\\$ROOM.owner_uid ?= ?([0-9]+);");
	private final static Pattern pUserName = Pattern.compile("<a class=\"Title-anchorName\" title=\"([^\"]+)\"");
	private final static Pattern pQuality = Pattern.compile("\\$ROOM.multirates ?= ?(\\[[^\\]]*\\]);");

	/**
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
			String basicInfoUrl = String.format("https://www.douyu.com/%s", shortId);
			List<HttpCookie> listCookie = null;
			if (cookie != null) {
				listCookie = HttpCookies.convertCookies(cookie);
				System.out.println("发现cookie配置");
			}
			String html = util.getContent(basicInfoUrl, headers.getDouyuJsonAPIHeaders(Long.parseLong(shortId)),
					listCookie);
			// System.out.println(html);
			// 直播状态
			Matcher matcher = pLiveStatus.matcher(html);
			matcher.find();
			roomInfo.setLiveStatus(Integer.parseInt(matcher.group(1)));
			// 真实房间id
			matcher = pRoomId.matcher(html);
			matcher.find();
			roomInfo.setRoomId((matcher.group(1)));
			matcher = pTitle.matcher(html);
			if (matcher.find()) {
				roomInfo.setTitle(matcher.group(1));
			} else {
				matcher = pTitle2.matcher(html);
				matcher.find();
				roomInfo.setTitle(matcher.group(1));
			}

			matcher = pDescript.matcher(html);
			if (matcher.find()) {
				roomInfo.setDescription(matcher.group(1));
			} else {
				roomInfo.setDescription("无");
			}

			// 房间主id
			matcher = pUserId.matcher(html);
			matcher.find();
			roomInfo.setUserId(Long.parseLong(matcher.group(1)));
			// 房间主名称
			matcher = pUserName.matcher(html);
			if (matcher.find()) {
				roomInfo.setUserName(matcher.group(1));
			} else {
				roomInfo.setUserName(roomInfo.getTitle());
			}
			// 以下为直播时才去获取的配置
			if (roomInfo.getLiveStatus() == 1) {
				// 加密脚本
				int begin = html.indexOf("var vdwdae325w_64we");
				int end = html.indexOf("</script>", begin);
				roomInfo.setRemark(html.substring(begin, end));
				// 清晰度
				// $ROOM.multirates
				// =[{"name":"\u84dd\u51494M","type":0},{"name":"\u9ad8\u6e05","type":2},{"name":"\u6d41\u7545","type":1}];
				// $ROOM.
				matcher = pQuality.matcher(html);
				matcher.find();
				// 网页里面的清晰度不全，改为先申请一次FLV, 在列表里找到正确的
				JSONObject liveObj = getLiveObj(roomInfo.getRoomId(), "0", roomInfo.getRemark(), cookie);
				JSONArray jArray = liveObj.getJSONObject("data").getJSONArray("multirates");
				String[] qn = new String[jArray.length()];
				String[] qnDesc = new String[jArray.length()];
				for (int i = 0; i < jArray.length(); i++) {
					JSONObject obj = jArray.getJSONObject(i);
					qn[i] = "" + obj.getInt("rate");
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

	/**
	 * 获取直播地址的下载信息
	 */

	private JSONObject getLiveObj(String roomId, String qn, String scripts, String cookieStr) {
		String ttId = ttIdRandom;
		List<HttpCookie> cookie = null;
		if (cookieStr != null) {
			Matcher matcher = pDyID.matcher(cookieStr);
			if (matcher.find()) {
				ttId = matcher.group(1);
			}
			cookie = HttpCookies.convertCookies(cookieStr);
		}
		try {
			// https://playweb.douyu.com/lapi/live/getH5Play/312212
			// //ttId 可从cookie dy_did获取
			// e.g.
			// v=220120190527&did=_did&tt=_tt&sign=_sign&cdn=ws-h5&rate=2&ver=Douyu_219052705&iar=0&ive=0&hevc=0&fa=0,0&aid=web-alone&uid=_uid从cookie获取，是用户id
			// post提交的数据里面有个&sign=xxx参数，这个调用的是js里面的名为ub98484234的方法（注释1），神tm的加密混淆，纯java太麻烦了，就直接调用js了
			// 注释1: 关键词搜索&cdn= 或 &ver=，可查到该方法附近ub98484234
			// 注释2: 主页html里，直接有ub98484234方法
			// 注释3:java调用js方法ub98484234，提示"CryptoJS" is not defined，另需要引入CryptoJS
			String param = JSEngine.run(scripts, encryptMethod, roomId, ttId, System.currentTimeMillis() / 1000L);
			String param2 = String.format("&cdn=%s&rate=%s&ver=%s&iar=0&ive=0", "", // cdn 可为空
					// [{"name":"主线路","cdn":"ws-h5"},{"name":"备用线路5","cdn":"tct-h5"},{"name":"备用线路6","cdn":"ali-h5"}]
					qn, // rate 模糊到清晰 1，2，... 0 流畅 高清 超清 蓝光4M
					version);
			Logger.println(param);
			String url = String.format("https://playweb.douyu.com/lapi/live/getH5Play/%s", roomId);
			String json = util.postContent(url, headers.getDouyuJsonAPIHeaders(Long.parseLong(roomId)), param + param2, cookie);
//			String url = String.format("https://playweb.douyu.com/lapi/live/getH5Play/%s?%s%s", roomId, param, param2);
//			String json = util.getContent(url, headers.getDouyuJsonAPIHeaders(Long.parseLong(roomId)), cookie);
			Logger.println(json);

			JSONObject jobj = new JSONObject(json);
			return jobj;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 获取直播地址的下载链接
	 * 
	 * @param roomId
	 * @param scripts
	 * @param qn
	 * @return
	 */
	@Override
	public String getLiveUrl(String roomId, String qn, Object... params) {
		String scripts = (String) params[0];
		String cookieStr = null;
		if (params.length >= 2) {
			cookieStr = (String) params[1];
		}
		try {

			JSONObject jobj = getLiveObj(roomId, qn, scripts, cookieStr);
			int realQN = jobj.getJSONObject("data").getInt("rate");
			System.out.printf("申请清晰度 %s的链接，得到清晰度 %d的链接\r\n", qn, realQN);
			String header = jobj.getJSONObject("data").getString("rtmp_url");
			String tail = jobj.getJSONObject("data").getString("rtmp_live");
			if(tail.contains("/playlist.m3u8?")) {
				Logger.println("将m3u8链接转为flv链接");
				tail = tail.replace("/playlist.m3u8?", ".flv?");
			}
			String linkURL = header + "/" + tail;
			Logger.println("链接为：" + linkURL);
			
			return linkURL;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	@Override
	public void startRecord(String url, String fileName, String shortId) {
		util.download(url, fileName + ".flv", headers.getHeaders());
	}

}
