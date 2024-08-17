package nicelee.bilibili.live.impl;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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

public class RoomDealerTaobao extends RoomDealer {

	final public static String liver = "taobao";
	final static String appKey = "12574478";

	final static Pattern pToken = Pattern.compile("_m_h5_tk=([^ ;_]+)");

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
		RoomInfo roomInfo = new RoomInfo();
		roomInfo.setShortId(shortId);
		// 根据shortId的类型来获取userId
		String userId = null, roomId = null;
		if (shortId.startsWith("https://m.tb.cn/")) {
			String html = util.getContent(shortId, getPCHeader(), HttpCookies.convertCookies(cookie));
			Pattern p = Pattern.compile("var url = '.*(live|video)\\.html\\?id=([^'&]+)");
			Matcher m = p.matcher(html);
			if (!m.find()) {
				System.err.println("只支持淘宝直播分享链接");
				return null;
			}
			roomId = m.group(2);
		} else {
			if (shortId.matches("^\\d+$")) {
				roomId = shortId;
			} else {
				userId = shortId;
			}
		}
		try {
			if (userId == null) { // 此时roomId不为空
				JSONObject data = getLiveData(roomId);
				String accountInfoUrl = data.getJSONObject("broadCaster").getString("accountInfoUrl");
				Pattern p = Pattern.compile("userId=([^&]+)");
				Matcher m = p.matcher(accountInfoUrl);
				m.find();
				userId = m.group(1);
				userId = URLDecoder.decode(userId, "UTF-8");
				System.out.println("以后请使用此id来进行录制：" + userId);
				String replayUrl = data.optString("replayUrl");
				if(replayUrl != null) {
					System.out.println("直播回放的地址如下，如有需要请使用m3u8下载器下载：");
					System.out.println(replayUrl);
				}
			}
			// 获取用户直播的在线状态
			JSONObject rUserData = getUserData(userId).getJSONObject("result").getJSONObject("data");
			roomInfo.setDescription(rUserData.optString("signature", "-"));
			roomInfo.setUserName(rUserData.getString("userNick"));
			if ("0".equals(rUserData.optString("livingStatus"))) {
				roomInfo.setLiveStatus(0);
			} else {
				// 获取直播间id
				String livingRoomUrl = rUserData.getString("livingRoomUrl");
				Pattern plivingRoomUrl = Pattern.compile("id=([0-9]+)");
				Matcher m = plivingRoomUrl.matcher(livingRoomUrl);
				m.find();
				roomId = m.group(1);
				roomInfo.setRoomId(roomId);
				// 获取直播信息
				JSONObject rData = getLiveData(roomId);
				JSONObject broadCaster = rData.getJSONObject("broadCaster");
				// roomInfo.setUserName(broadCaster.getString("accountName"));
				roomInfo.setUserId(broadCaster.optLong("accountId"));
				roomInfo.setTitle(rData.getString("descInfo"));

				JSONArray liveUrlList = rData.optJSONArray("liveUrlList");
				roomInfo.setLiveStatus(1);
				int flv_sources_len = liveUrlList.length();
				String[] qn = new String[flv_sources_len];
				String[] qnDesc = new String[flv_sources_len];
				for (int i = 0; i < flv_sources_len; i++) {
					// 为了让0, 1, 2, 3 数字越小清晰度越高
					JSONObject obj = liveUrlList.getJSONObject(i);
					int level = obj.optInt("codeLevel");
					int idx = flv_sources_len - level - 1;
					qn[idx] = "" + idx;
					qnDesc[idx] = obj.getString("name");
				}
				roomInfo.setAcceptQuality(qn);
				roomInfo.setAcceptQualityDesc(qnDesc);
			}

			roomInfo.print();
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("淘宝需要cookie, 请确认cookie是否存在或失效");
			return null;
		}
		return roomInfo;
	}

	private JSONObject getUserData(String userId)
			throws NoSuchAlgorithmException, UnsupportedEncodingException, URISyntaxException {
		long timestamp = System.currentTimeMillis();
		String data = String.format(
				"{\"source\":\"taolive\",\"type\":\"h5\",\"userId\":\"%s\",\"userIdString\":\"%s\",\"keyName\":\"kc_lightspeed\"}",
				userId, userId);
		String md5code = sign(timestamp, data);
		data = data.replace("+", "%2B");
		String url = "https://h5api.m.taobao.com/h5/mtop.taobao.maserati.guangguang.getpersonalheader/1.0/?jsv=2.6.1&appKey=%s&t=%d&sign=%s&v=1.0&api=mtop.taobao.maserati.guangguang.getPersonalHeader&type=json&dataType=json&smToken=token&queryToken=sm&sm=sm&data=%s";
		url = String.format(url, appKey, timestamp, md5code, data);
		String res = util.getContent(url, getPCHeader(), HttpCookies.convertCookies(cookie));
		Logger.println(url);
		Logger.println(res);

		JSONObject rAll = new JSONObject(res);
		JSONObject rData = rAll.getJSONObject("data");
		if (rAll.getJSONArray("ret").getString(0).equals("FAIL_SYS_TOKEN_EXOIRED::令牌过期")) {
			Logger.println("令牌过期,cookie进行刷新");
			CookieStore cookieStore = HttpRequestUtil.DefaultCookieManager().getCookieStore();
			URI taobaoUri = new URI("https://taobao.com");
			List<HttpCookie> cookies = cookieStore.get(taobaoUri);
			for (HttpCookie c : cookies) {
				String pattern = c.getName() + "=[^;]+";
				String value = c.getName() + "=" + c.getValue();
				cookie = cookie.replaceFirst(pattern, value);
				cookieStore.remove(taobaoUri, c);
			}
			Logger.println(cookie);
			return getUserData(userId);
		}
		return rData;
	}

	private JSONObject getLiveData(String roomId)
			throws NoSuchAlgorithmException, UnsupportedEncodingException, URISyntaxException {
		long timestamp = System.currentTimeMillis();
		String data = String.format("{\"liveId\":\"%s\",\"creatorId\":null}", roomId);
		String md5code = sign(timestamp, data);
		String url = "https://h5api.m.taobao.com/h5/mtop.mediaplatform.live.livedetail/4.0/?jsv=2.7.0&appKey=%s&t=%d&sign=%s&api=mtop.mediaplatform.live.livedetail&v=4.0&type=json&dataType=json&data=%s";
		url = String.format(url, appKey, timestamp, md5code, data);
		String res = util.getContent(url, getPCHeader(), HttpCookies.convertCookies(cookie));
		Logger.println(url);
		Logger.println(res);

		JSONObject rAll = new JSONObject(res);
		JSONObject rData = rAll.getJSONObject("data");
		if (rAll.getJSONArray("ret").getString(0).equals("FAIL_SYS_TOKEN_EXOIRED::令牌过期")) {
			Logger.println("令牌过期,cookie进行刷新");
			CookieStore cookieStore = HttpRequestUtil.DefaultCookieManager().getCookieStore();
			URI taobaoUri = new URI("https://taobao.com");
			List<HttpCookie> cookies = cookieStore.get(taobaoUri);
			for (HttpCookie c : cookies) {
				String pattern = c.getName() + "=[^;]+";
				String value = c.getName() + "=" + c.getValue();
				cookie = cookie.replaceFirst(pattern, value);
				cookieStore.remove(taobaoUri, c);
			}
			Logger.println(cookie);
			return getLiveData(roomId);
		}
		return rData;
	}

	private String sign(long timestamp, String data) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		Matcher mToken = pToken.matcher(cookie);
		if (!mToken.find()) {
			System.err.println("cookie需要_m_h5_tk值");
			throw new RuntimeException("cookie需要_m_h5_tk值");
		}
		String token = mToken.group(1);
		String sign = new StringBuilder(token).append("&").append(timestamp).append("&").append(appKey).append("&")
				.append(data).toString();
		byte[] secretBytes = MessageDigest.getInstance("md5").digest(sign.getBytes("UTF-8"));
		String md5code = new BigInteger(1, secretBytes).toString(16);
		for (int i = 0; i < 32 - md5code.length(); i++) {
			md5code = "0" + md5code;
		}
		return md5code;
	}

	@Override
	public String getLiveUrl(String roomId, String qn, Object... obj) {
		try {
			JSONObject rData = getLiveData(roomId);
			JSONArray liveUrlList = rData.optJSONArray("liveUrlList");
			int flv_sources_len = liveUrlList.length();
			String flvUrl = null;
			for (int i = 0; i < flv_sources_len; i++) {
				JSONObject quality = liveUrlList.getJSONObject(i);
				int level = quality.optInt("codeLevel");
				int idxTrans = flv_sources_len - level - 1;
				// 这里要与前面一致
				if (qn.equals("" + idxTrans)) {
					Logger.println("qn: " + qn + ", codeLevel: " + level);
					flvUrl = quality.getString("flvUrl");
					break;
				}
			}
			Logger.println(flvUrl);
			return flvUrl;
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
		mobile.put("User-Agent", "Mozilla/5.0 (Android 9.0; Mobile; rv:68.0) Gecko/68.0 Firefox/86.0");
		util.download(url, fileName + ".flv", mobile);
	}

	private HashMap<String, String> pcHeader;

	private HashMap<String, String> getPCHeader() {
		if (pcHeader == null) {
			pcHeader = new HashMap<>();
			pcHeader.put("User-Agent",
					"Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:86.0) Gecko/20100101 Firefox/86.0");
//			pcHeader.put("Referer", "https://h5.m.taobao.com/");
			pcHeader.put("Referer", "https://market.m.taobao.com/");
			pcHeader.put("Accept", "*/*");
			pcHeader.put("Accept-Language", "zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5,en-US;q=0.3,en;q=0.2");
		}
		return pcHeader;
	}

}
