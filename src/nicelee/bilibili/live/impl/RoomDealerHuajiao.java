package nicelee.bilibili.live.impl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;

import nicelee.bilibili.live.RoomDealer;
import nicelee.bilibili.live.domain.RoomInfo;
import nicelee.bilibili.util.Logger;

public class RoomDealerHuajiao extends RoomDealer {

	final public static String liver = "huajiao";

	@Override
	public String getType() {
		return ".flv";
	}

	/**
	 * http://www.huajiao.com/l/{shortId} 根据url的shortId获取房间信息(从HTML获取)
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
			String basicInfoUrl = String.format("http://www.huajiao.com/l/%s", shortId);
			String html = util.getContent(basicInfoUrl, headers.getCommonHeaders("www.huajiao.com"), null);
			// Logger.println(html);
			Pattern pAuthor = Pattern.compile("var author = *(.*?});");
			Matcher matcher = pAuthor.matcher(html);
			if(!matcher.find()) { // 将302到首页， 目前没有开播
				roomInfo.setLiveStatus(0);
				return roomInfo;
			}
			JSONObject jAuthor = new JSONObject(matcher.group(1));

			roomInfo.setRoomId(shortId);
			roomInfo.setUserId(Long.parseLong(jAuthor.getString("uid")));
			roomInfo.setTitle(jAuthor.getString("nickname") + " 的直播");
			roomInfo.setDescription(jAuthor.getString("signature"));
			roomInfo.setUserName(jAuthor.getString("nickname"));
			
//			if(jAuthor.getInt("lives") == 0) {
				roomInfo.setLiveStatus(1);
				// 获取直播可提供的清晰度
				String[] qn = { "0" };
				String[] qnDesc = { "默认" };
				roomInfo.setAcceptQuality(qn);
				roomInfo.setAcceptQualityDesc(qnDesc);
//			}else {
//				roomInfo.setLiveStatus(0);
//			}
			
			roomInfo.print();
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("解析失败！！！");
			return roomInfo;
		}
		return roomInfo;
	}

	@Override
	public String getLiveUrl(String shortId, String qn, Object... obj) {
		try {
			// 获取基础信息
			String basicInfoUrl = String.format("http://www.huajiao.com/l/%s", shortId);
			String html = util.getContent(basicInfoUrl, headers.getCommonHeaders("www.huajiao.com"), null);
			// Logger.println(html);
			Pattern pFeed = Pattern.compile("var feed = *(.*?});");
			Matcher matcher = pFeed.matcher(html);
			matcher.find();
			JSONObject jFeed = new JSONObject(matcher.group(1));
			
			// 构造参数进行查询
			String format = "http://live.huajiao.com/live/substream?sn=%s&uid=%s&liveid=%s&encode=h265&platform=android&rand=%.16f&time=%s&version=1111&callback=%s";
			String sn = jFeed.getJSONObject("feed").getString("sn");
			String uid = jFeed.getJSONObject("author").getString("uid");
			double rand = Math.random(); //"5953748984118983"; // 16位随机数
			long time = System.currentTimeMillis();
			String callback = "jsonp_" + time + "_" + (int)(rand * 100000);
			String url = String.format(format, sn, uid, shortId, rand, time, callback);
			Logger.println(url);
			String response = util.getContent(url, headers.getHuajiaoHeaders(shortId));
			Logger.println(response);
			
			matcher = Pattern.compile(callback + "\\((.*)\\);").matcher(response);
			matcher.find();
			JSONObject json = new JSONObject(matcher.group(1));
			return json.getJSONObject("data").getString("main");
		} catch (

		Exception e) {
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
		util.download(url, fileName + ".flv", headers.getHeaders());
	}

}
