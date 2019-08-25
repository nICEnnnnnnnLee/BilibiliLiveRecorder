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

public class RoomDealerDouyu extends RoomDealer{

	final public static String liver = "douyu";

	@Override
	public String getType() {
		return ".flv";
	}
	
	private final static Pattern pDyID = Pattern.compile("dy_did ?= ?([^,; ]+)");
	private final static String ttIdRandom = "2206c59057010dd04573c76400081501"; 
	private final static String encryptMethod = "ub98484234"; 
	private final static String version = "Douyu_219052705";  //ver 版本
	private final static Pattern pLiveStatus = Pattern.compile("\\$ROOM.show_status ?= ?([0-9]+);");
	private final static Pattern pRoomId = Pattern.compile("\\$ROOM.room_id ?= ?([0-9]+);");
	private final static Pattern pTitle = Pattern.compile("<h[0-9] class=\"Title-headlineH2\">([^/]*)</h[0-9]>");
	private final static Pattern pTitle2 = Pattern.compile("<title>([^/]*)</title>");
	private final static Pattern pDescript = Pattern.compile("<div class=\"AnchorAnnounce\"><h3><span>([^/]*)</span></h3></div>");
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
			String basicInfoUrl = String.format("https://www.douyu.com/%s",
					shortId);
			String html = util.getContent(basicInfoUrl, headers.getDouyuJsonAPIHeaders(Long.parseLong(shortId)), null);
			System.out.println(html);
			// 直播状态
			Matcher matcher = pLiveStatus.matcher(html);
			matcher.find();
			roomInfo.setLiveStatus(Integer.parseInt(matcher.group(1)));
			// 真实房间id
			matcher = pRoomId.matcher(html);
			matcher.find();
			roomInfo.setRoomId((matcher.group(1)));
			matcher = pTitle.matcher(html);
			if(matcher.find()) {
				roomInfo.setTitle(matcher.group(1));
			}else {
				matcher = pTitle2.matcher(html);
				matcher.find();
				roomInfo.setTitle(matcher.group(1));
			}
			
			matcher = pDescript.matcher(html);
			if(matcher.find()) {
				roomInfo.setDescription(matcher.group(1));
			}else {
				roomInfo.setDescription("无");
			}

			// 房间主id
			matcher = pUserId.matcher(html);
			matcher.find();
			roomInfo.setUserId(Long.parseLong(matcher.group(1)));
			// 房间主名称
			matcher = pUserName.matcher(html);
			if(matcher.find()) {
				roomInfo.setUserName(matcher.group(1));
			}else{
				roomInfo.setUserName(roomInfo.getTitle());
			}
			
			// 清晰度
			//$ROOM.multirates =[{"name":"\u84dd\u51494M","type":0},{"name":"\u9ad8\u6e05","type":2},{"name":"\u6d41\u7545","type":1}]; $ROOM.
			matcher = pQuality.matcher(html);
			matcher.find();
			JSONArray jArray = new JSONArray(matcher.group(1));
			String[] qn = new String[jArray.length()];
			String[] qnDesc = new String[jArray.length()];
			for (int i = 0; i < jArray.length(); i++) {
				JSONObject obj = jArray.getJSONObject(i);
				qn[i] = "" + obj.getInt("type");
				qnDesc[i] = obj.getString("name");
			}
			roomInfo.setAcceptQuality(qn);
			roomInfo.setAcceptQualityDesc(qnDesc);
			
			// 加密脚本
			int begin = html.indexOf("var vdwdae325w_64we");
			int end = html.indexOf("</script>", begin);
			roomInfo.setRemark(html.substring(begin, end));
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
	 * @param scripts
	 * @param qn
	 * @return
	 */
	@Override
	public String getLiveUrl(String roomId, String qn, Object...params) {
		String scripts = (String) params[0];
		List<HttpCookie> cookie = null;
		String ttId = ttIdRandom; 
		if(params.length >= 2 && params[1] != null) {
			Matcher matcher = pDyID.matcher((String) params[1]);
			matcher.find();
			ttId = matcher.group(1);
			cookie = HttpCookies.convertCookies((String) params[1]);
		}
		
		try {
			String url = String.format("https://www.douyu.com/lapi/live/getH5Play/%s", roomId);
			
			// //ttId 可从cookie dy_did获取
			// e.g. v=220120190527&did=2206c59057010dd04573c76400081501&tt=1558935405&sign=79ec53bd8d789fc544697c07095fe592&cdn=ws-h5&rate=2&ver=Douyu_219052705&iar=0&ive=1
			// post提交的数据里面有个&sign=xxx参数，这个调用的是js里面的名为ub98484234的方法（注释1），神tm的加密混淆，纯java太麻烦了，就直接调用js了
			// 注释1: 关键词搜索&cdn= 或 &ver=，可查到该方法附近ub98484234
			// 注释2: 主页html里，直接有ub98484234方法
			// 注释3:java调用js方法ub98484234，提示"CryptoJS" is not defined，另需要引入CryptoJS
			String param = JSEngine.run(
					scripts, encryptMethod, roomId, ttId, 
					System.currentTimeMillis()/ 1000L);
			
			param += String.format("&cdn=%s&rate=%s&ver=%s&iar=0&ive=1", 
					"", //cdn 可为空 [{"name":"主线路","cdn":"ws-h5"},{"name":"备用线路5","cdn":"tct-h5"},{"name":"备用线路6","cdn":"ali-h5"}]
					qn, // rate 模糊到清晰 1，2，... 0     流畅  高清 超清 蓝光4M
					version
					);
			Logger.println(param);
			String json = util.postContent(url, headers.getDouyuJsonAPIHeaders(Long.parseLong(roomId)), param, cookie);
			//HttpCookies.convertCookies());
			Logger.println(json);
			
			JSONObject jobj = new JSONObject(json);
			int realQN = jobj.getJSONObject("data").getInt("rate");
			System.out.printf("申请清晰度 %s的链接，得到清晰度 %d的链接\r\n", qn, realQN);
			String header = jobj.getJSONObject("data").getString("rtmp_url");
			String tail = jobj.getJSONObject("data").getString("rtmp_live");
			String linkURL = header + "/" + tail;
			Logger.println("链接为：" + linkURL);
			return linkURL;
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}

	@Override
	public void startRecord(String url, String fileName, String shortId) {
		util.download(url, fileName + ".flv", headers.getBiliLiveRecordHeaders(url, Long.parseLong(shortId)));
	}

}
