package nicelee.bilibili.live.impl;

import java.math.BigInteger;
import java.net.URLDecoder;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.HashMap;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONObject;

import nicelee.bilibili.live.RoomDealer;
import nicelee.bilibili.live.domain.RoomInfo;
import nicelee.bilibili.util.Logger;

public class RoomDealerHuya extends RoomDealer {

	final public static String liver = "huya";

	@Override
	public String getType() {
//		return ".ts";
		return ".flv";
	}

	/**
	 * https://www.huya.com/{shortId} 根据url的shortId获取房间信息(从网页里面爬)
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
			JSONObject all = getLiveObj(shortId);
			JSONObject liveData = all.getJSONObject("liveData");
			JSONObject profileInfo = all.getJSONObject("profileInfo");

			// 直播状态 ON REPLAY
			if ("ON".equals(all.getString("liveStatus"))) {
				roomInfo.setLiveStatus(1);
			} else {
				roomInfo.setLiveStatus(0);
			}

			roomInfo.setRoomId(shortId);
			roomInfo.setDescription(liveData.getString("introduction"));
			roomInfo.setUserId(profileInfo.optLong("profileRoom"));
			roomInfo.setUserName(profileInfo.getString("nick"));
			roomInfo.setTitle(liveData.getString("roomName"));
			
			
			if (roomInfo.getLiveStatus() == 1) {
				JSONObject obj = all.getJSONObject("stream").getJSONObject("flv");
				// 清晰度
				JSONArray jArray = obj.getJSONArray("rateArray");
				String[] qn = new String[jArray.length()];
				String[] qnDesc = new String[jArray.length()];
				for (int i = 0; i < jArray.length(); i++) {
					JSONObject objTemp = jArray.getJSONObject(i);
					// qn[i] = "" + i;
					qn[i] = "" + objTemp.getInt("iBitRate");
					qnDesc[i] = objTemp.getString("sDisplayName");
				}

//				JSONArray jArray = obj.getJSONObject("stream").getJSONArray("data").getJSONObject(0)
//						.getJSONArray("gameStreamInfoList");

//				String[] qn = new String[jArray.length()];
//				String[] qnDesc = new String[jArray.length()];
//				Pattern pQuality = Pattern.compile("&exsphd=([^&]+)");
//				for (int i = 0; i < jArray.length(); i++) {
//					qn[i] = "" + i;
//					
//					JSONObject objTemp = jArray.getJSONObject(i);
//					matcher = pQuality.matcher(objTemp.getString("sHlsAntiCode"));
//					if(matcher.find()) {
//						qnDesc[i] = matcher.group(1);
//					}else {
//						Logger.println(objTemp.getString("sHlsAntiCode"));
//						qnDesc[i] = "实际默认";
//					}
//				}
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

	public JSONObject getLiveObj(String roomId) {
		String basicInfoUrl = "https://mp.huya.com/cache.php?m=Live&do=profileRoom&roomid=" + roomId;
		HashMap<String, String> map = headers.getCommonHeaders("www.huya.com");
		String j = util.getContent(basicInfoUrl, map, null);
		Logger.println(j);
		return new JSONObject(j).getJSONObject("data");
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
			// 获取基础信息
			JSONObject streamDetail = null;
			JSONArray cdns = getLiveObj(roomId).getJSONObject("stream").getJSONArray("baseSteamInfoList");
			for (int i = 0; i < cdns.length(); i++) {
				JSONObject cdn = cdns.getJSONObject(i);
				// ali CDN 似乎坚持不到5min就会断掉
				if (cdnType.equals(cdn.getString("sCdnType"))) {
					streamDetail = cdn;
					break;
				}
			}
			if (streamDetail == null) {
				streamDetail = cdns.getJSONObject(cdns.length() - 1);
			}
			String url = getFlvUrlFromStreamDetail(streamDetail, qn);
			Logger.println(url);
			// Logger.println(obj.getJSONObject("stream").getInt("iWebDefaultBitRate"));
			return url;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	String getFlvUrlFromStreamDetail(JSONObject streamDetail, String qn) {
		String sStreamName = streamDetail.getString("sStreamName");
		String antiCode = genFlvAntiCode(sStreamName, streamDetail.getString("sFlvAntiCode"), qn);
		Logger.println(antiCode);
		String url = String.format("%s/%s.%s?%s", streamDetail.getString("sFlvUrl"), sStreamName,
				streamDetail.getString("sFlvUrlSuffix"), antiCode);
		return url;
	}

//	static String sv, platform, ctype, t, uaSuffix, ua, cdnType;
	static String sv, ctype, t, cdnType;
	static {
		cdnType = System.getProperty("huya.cdn", "TX");
		sv = System.getProperty("huya.sv", "2408161057");
//		platform = System.getProperty("huya.platform", "adr");
//		uaSuffix = System.getProperty("huya.uaSuffix", "&huya"); // websocket minigame signalsd
//		switch (platform) {
//		case "adr":
//			t = "2";
//			break;
//		case "ios":
//			t = "3";
//			break;
//		case "mini_app":
//			t = "102";
//			break;
//		case "wap":
//			t = "103";
//			sv = "1.0.0";
//			break;
//		case "huya_liveshareh5":
//			platform = "liveshareh5";
//			t = "104";
//			break;
//		case "web":
//			t = "100";
//			break;
//		default:
//			t = System.getProperty("huya.plType", "2");
//			break;
//		}
//		ctype = "huya_" + platform;
//		ua = platform + "&" + sv + uaSuffix;
		ctype = System.getProperty("huya.force_ctype", "tars_mp");;
		t = System.getProperty("huya.force_t", "102");
	}

	String genFlvAntiCode(String sStreamName, String sFlvAntiCode, String qn) {
		try {
			// 根据sFlvAntiCode生成key value表
			String[] c = sFlvAntiCode.split("&");
			HashMap<String, String> n = new HashMap<>();
			for (String str : c) {
				String temp[] = str.split("=");
				if (temp.length > 1 && !temp[1].isEmpty()) {
					n.put(temp[0], temp[1]);
				}
			}
			// 随机生成uid
			long uid = 1462220000000L + new Random().nextInt(1145142333);
			long convertUid = (uid << 8 | uid >> (32 - 8)) & 0xFFFFFFFF;
			long currentTime = System.currentTimeMillis();
			String wsTime = Long.toHexString(currentTime / 1000);
			long seqid = uid + currentTime + 216000000L; // 216000000 = 30*1000*60*60 = 30h
			// 生成 wsSecret, 先获取参数fm, 再逐一替换
			String fm = n.get("fm");
			fm = URLDecoder.decode(fm, "utf-8");
			fm = new String(Base64.getDecoder().decode(fm), "utf-8"); // DWq8BcJ3h6DJt6TY_$0_$1_$2_$3
			Logger.println(n.getOrDefault("ctype", "ctype:none"));
			Logger.println(ctype);
			// String oe = JSEngine.huyaTrans(String.join("|", "" + seqid, ctype, t));
			String oe = md5(String.join("|", "" + seqid, ctype, t));
			String r = fm.replace("$0", "" + convertUid).replace("$1", sStreamName).replace("$2", oe).replace("$3",
					wsTime);
			String wsSecret = md5(r);
			StringBuilder sb = new StringBuilder();
			sb.append("wsSecret=").append(wsSecret)
					.append("&wsTime=").append(wsTime)
					.append("&seqid=").append(seqid)
					.append("&ctype=").append(ctype)
					.append("&t=").append(t)
					.append("&ver=1&fs=").append(n.getOrDefault("fs", ""))
//				.append("&sphdcdn=").append(n.getOrDefault("sphdcdn", "")).append("&sphdDC=")
//					.append(n.getOrDefault("sphdDC", "")).append("&sphd=").append(n.getOrDefault("sphd", ""))
//					.append("&exsphd=").append(n.getOrDefault("exsphd", ""))
//					.append("&t=").append(t)
//					.append("&dMod=mseh-32&sdkPcdn=1_1")
					.append("&sv=").append(sv) // .append("&ratio=").append(qn)
					.append("&uuid=").append(uid / 100)  // 随便填的
					.append("&codec=264")
					.append("&u=").append(convertUid)
					.append("&sdk_sid=").append(currentTime);
			if (!"".equals(qn) && !"0".equals(qn)) {
				sb.append("&ratio=").append(qn);
			}
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private static String md5(String data) {
		try {
			byte[] secretBytes = MessageDigest.getInstance("md5").digest(data.getBytes("utf-8"));
			String md5code = new BigInteger(1, secretBytes).toString(16);
			for (int i = 0; i < 32 - md5code.length(); i++) {
				md5code = "0" + md5code;
			}
			return md5code;
		} catch (Exception e) {
			throw new RuntimeException();
		}
	}

	@Override
	public void startRecord(String url, String fileName, String shortId) {

		HashMap<String, String> mobile = new HashMap<>();
		mobile.put("User-Agent", "Mozilla/5.0 (Android 9.0; Mobile; rv:68.0) Gecko/68.0 Firefox/68.0");
		mobile.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		mobile.put("Accept-Language", "zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5,en-US;q=0.3,en;q=0.2");
		// flv
		util.download(url, fileName + ".flv", mobile);
	}

}
