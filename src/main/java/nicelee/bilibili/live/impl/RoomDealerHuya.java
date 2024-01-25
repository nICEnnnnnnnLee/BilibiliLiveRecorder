package nicelee.bilibili.live.impl;

import java.io.InputStream;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
			String basicInfoUrl = String.format("https://www.huya.com/%s", shortId);
			String html = util.getContent(basicInfoUrl, headers.getCommonHeaders("www.huya.com"), null);

			Pattern pJson = Pattern.compile("var TT_ROOM_DATA =(.*?); *var +TT");
			Matcher matcher = pJson.matcher(html);
			matcher.find();
			JSONObject room = new JSONObject(matcher.group(1));

			// 直播状态 ON REPLAY
			if ("ON".equals(room.getString("state"))) {
				roomInfo.setLiveStatus(1);
			} else {
				roomInfo.setLiveStatus(0);
			}

			// 真实房间id
			roomInfo.setRoomId(shortId);

			roomInfo.setDescription(room.getString("introduction"));

			// 房间主id
			roomInfo.setUserId(room.optLong("profileRoom"));

			pJson = Pattern.compile("var hyPlayerConfig = (.*?});");
			matcher = pJson.matcher(html);
			matcher.find();
			// System.out.println(matcher.group(1));
			JSONObject obj = new JSONObject(matcher.group(1));
			if (roomInfo.getLiveStatus() == 1) {
				JSONObject streamDetail = obj.getJSONObject("stream").getJSONArray("data").getJSONObject(0)
						.getJSONArray("gameStreamInfoList").getJSONObject(0);
				String url = getFlvUrlFromStreamDetail(streamDetail, "");
				boolean urlIsValid = test(url, headers.getHeaders());
				Logger.println("当前url可用性: " + urlIsValid);
				if (!urlIsValid)
					roomInfo.setLiveStatus(0);
			}
			if (roomInfo.getLiveStatus() == 1) {
//				String stream = obj.getString("stream");
//				stream = new String(Base64.getDecoder().decode(stream), "UTF-8");
//				obj = new JSONObject(stream);
				obj = obj.getJSONObject("stream");
				// 房间主名称
				JSONObject liveInfo = obj.getJSONArray("data").getJSONObject(0).getJSONObject("gameLiveInfo");
				roomInfo.setUserName(liveInfo.getString("nick"));
				roomInfo.setTitle(liveInfo.getString("roomName"));
				// 清晰度
				JSONArray jArray = obj.getJSONArray("vMultiStreamInfo");
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
			String basicInfoUrl = String.format("https://www.huya.com/%s", roomId);
			HashMap<String, String> map = headers.getCommonHeaders("www.huya.com");
			String html = util.getContent(basicInfoUrl, map, null);

			Pattern pJson = Pattern.compile("var hyPlayerConfig *= *(.*?});");
			Matcher matcher = pJson.matcher(html);
			matcher.find();
			JSONObject obj = new JSONObject(matcher.group(1)).getJSONObject("stream");
			JSONObject streamDetail = null;
			JSONArray cdns = obj.getJSONArray("data").getJSONObject(0)
					.getJSONArray("gameStreamInfoList");
			for(int i=0; i< cdns.length(); i++) {
				JSONObject cdn = cdns.getJSONObject(i);
				// ali CDN 似乎坚持不到5min就会断掉
				if("TX".equals(cdn.getString("sCdnType"))) {
					streamDetail = cdn;
					break;
				}
			}
			if(streamDetail == null) {
				streamDetail = cdns.getJSONObject(cdns.length() -1);
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
		String url = String.format("%s/%s.%s?%s", streamDetail.getString("sFlvUrl"),
				sStreamName, streamDetail.getString("sFlvUrlSuffix"), antiCode);
		return url;
	}
	
	/**
	 * key: it,
        value: function (e) {
          if ('' === this["_fm"]) return this["_sFlvAnticode"];
          var t = "web",
          i = 100;
          this["_seqid"] = Number(C.a.uid) + Date.now();
          var s = Oe(''.concat(this["_seqid"], '|').concat(this["_ctype"], '|').concat(i)),
          a = t === N.a.PLATFORM_TYPE_NAME.wap ? C.a.uid : C.a.convertUid,
          r = this["_fm"].replace("$0", a).replace("$1", this["_sStreamName"]).replace("$2", s).replace("$3", this["_wsTime"]);
          e && (r += Je);
          var n = ''.concat("wsSecret").concat("=").concat(Oe(r)).concat("&").concat("wsTime").concat("=").concat(this["_wsTime"]).concat("&")
                .concat("seqid").concat("=").concat(this["_seqid"]).concat("&").concat("ctype").concat("=").concat(this["_ctype"]).concat("&").concat("ver=1");
          return this["_params"].length > 0 && (n += "&" + this["_params"].join("&")),
          n
        }
	 *
	 */
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
			long currentTime = System.currentTimeMillis();
			String wsTime = Long.toHexString(currentTime/1000);
			long seqid = uid + currentTime + 216000000L; // 216000000 = 30*1000*60*60 = 30h
			// 生成 wsSecret, 先获取参数fm, 再逐一替换
			String fm = n.get("fm");
			fm = URLDecoder.decode(fm, "utf-8");
			fm = new String(Base64.getDecoder().decode(fm), "utf-8"); // DWq8BcJ3h6DJt6TY_$0_$1_$2_$3
			String ctype = n.getOrDefault("ctype", "huya_live"); // huya_live huya_webh5
			//String oe = JSEngine.huyaTrans(String.join("|", "" + seqid, ctype, "100"));
			String oe = md5(String.join("|", "" + seqid, ctype, "100"));
			String r = fm.replace("$0", "" + uid).replace("$1", sStreamName)
					.replace("$2", oe).replace("$3", wsTime);
			String wsSecret = md5(r);
			StringBuilder sb = new StringBuilder();
			sb.append("wsSecret=").append(wsSecret).append("&wsTime=").append(wsTime)
				.append("&seqid=").append(seqid)
				.append("&ctype=").append(ctype)
				.append("&ver=1&fs=").append(n.getOrDefault("fs", ""))
				.append("&sphdcdn=").append(n.getOrDefault("sphdcdn", ""))
				.append("&sphdDC=").append(n.getOrDefault("sphdDC", ""))
				.append("&sphd=").append(n.getOrDefault("sphd", ""))
				.append("&exsphd=").append(n.getOrDefault("exsphd", ""))
				// .append("&ratio=").append(qn)
				.append("&dMod=mseh-32&sdkPcdn=1_1&u=").append(uid)
				.append("&t=100&sv=2401190627&sdk_sid=").append(currentTime);
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
	
	public boolean test(String url, HashMap<String, String> headers) {
		InputStream inn = null;
		try {
			URL realUrl = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) realUrl.openConnection();
			conn.setConnectTimeout(20000);
			conn.setReadTimeout(120000);
			for (Map.Entry<String, String> entry : headers.entrySet()) {
				conn.setRequestProperty(entry.getKey(), entry.getValue());
				// System.out.println(entry.getKey() + ":" + entry.getValue());
			}
			conn.connect();
			// 获取所有响应头字段
//			Map<String, List<String>> map = conn.getHeaderFields();
//			// 遍历所有的响应头字段
//			for (String key : map.keySet()) {
//				System.out.println(key + "--->" + map.get(key));
//			}
			inn = conn.getInputStream();
			int rspCode = conn.getResponseCode();
			if (rspCode >= 200 && rspCode < 300)
				return true;
			else
				return false;
		} catch (Exception e) {
			System.out.println("发送GET请求出现异常！" + e);
			return false;
		}
		finally {
			try {
				if (inn != null) {
					inn.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}
}
