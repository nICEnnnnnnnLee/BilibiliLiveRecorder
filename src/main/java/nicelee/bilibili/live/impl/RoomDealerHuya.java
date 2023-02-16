package nicelee.bilibili.live.impl;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

import nicelee.bilibili.live.RoomDealer;
import nicelee.bilibili.live.domain.RoomInfo;
import nicelee.bilibili.util.Logger;
import nicelee.bilibili.util.M3u8Downloader;

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
		System.out.println("部分清晰度不支持选择");
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
			//System.out.println(matcher.group(1));
			JSONObject obj = new JSONObject(matcher.group(1));
			if (roomInfo.getLiveStatus() == 1) {
				JSONObject streamDetail = obj.getJSONObject("stream").getJSONArray("data").getJSONObject(0)
						.getJSONArray("gameStreamInfoList").getJSONObject(0);
				String url = String.format("%s/%s.%s?%s", streamDetail.getString("sFlvUrl"),
						streamDetail.getString("sStreamName"), streamDetail.getString("sFlvUrlSuffix"),
						streamDetail.getString("sFlvAntiCode"));
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
					//qn[i] = "" + i;
					qn[i] ="" +  objTemp.getInt("iBitRate");
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
//			String stream = obj.getString("stream");
//			stream = new String(Base64.getDecoder().decode(stream), "UTF-8");
//			obj = new JSONObject(stream);
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
//			String url = String.format("%s/%s.m3u8?%s", streamDetail.getString("sHlsUrl"),
//					streamDetail.getString("sStreamName"), streamDetail.getString("sHlsAntiCode"));
			String url = String.format("%s/%s.%s?%s", streamDetail.getString("sFlvUrl"),
					streamDetail.getString("sStreamName"), 
					streamDetail.getString("sFlvUrlSuffix"), 
					streamDetail.getString("sFlvAntiCode"));
			if(!"".equals(qn)) {
				url = url + "&ratio=" + qn;
			}
			Logger.println(url);
			url = genRealUrl(url);
			Logger.println(url);
			// Logger.println(obj.getJSONObject("stream").getInt("iWebDefaultBitRate"));
			return url;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	/**
	 * https://github.com/wbt5/real-url/issues/39
	 * https://github.com/wbt5/real-url/blob/df183eee17022d558cfc2aec221dfe632e360b13/huya.py#L11-L28
	 */
	String genRealUrl(String url) {
		try {
			String[] parts =  url.split("\\?");
			String[] r = parts[0].split("/");
			String s = r[r.length -1].replace(".flv", "");
			String[] c = parts[1].split("&", 4);
			HashMap<String, String> n = new HashMap<>();
			for(String str: c) {
				String temp[] = str.split("=");
				if(temp.length > 1 && !temp[1].isEmpty()) {
					n.put(temp[0], temp[1]);
				}
			}
			String fm = URLDecoder.decode(n.get("fm"), "UTF-8");
			String u = new String(Base64.getDecoder().decode(fm), "UTF-8");
			String p = u.split("_")[0];
			String f = System.currentTimeMillis() * 10000 + (long) (Math.random() * 10000) + "";
			String ll = n.get("wsTime");
			String t = "0";
			String h = String.format("%s_%s_%s_%s_%s", p, t, s, f, ll);
			byte[] secretBytes = MessageDigest.getInstance("md5").digest(h.getBytes());
	        String md5code = new BigInteger(1, secretBytes).toString(16);
	        for (int i = 0; i < 32 - md5code.length(); i++) {
	            md5code = "0" + md5code;
	        }
	        String y = c[c.length -1];
	        
	        String realUrl = String.format("%s?wsSecret=%s&wsTime=%s&u=%s&seqid=%s&%s", parts[0], md5code, ll, t, f, y);
	        return realUrl;
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	public void startRecord(String url, String fileName, String shortId) {
		
		// flv
		util.download(url, fileName + ".flv", headers.getHeaders());
		
		// m3u8
//		// 创建文件夹，用于存放片段
//		util.setSavePath("download/tmp_huya/");
//		// 
//		// 从m3u8获取实际地址(只有3个ts，所以最好自己再构造，而不必重复再获取)
//		Logger.println(" --------- startRecord------------");
//		while(true) {
//			if(!this.downloadM3u8(url, fileName)) {
//				Logger.println(" --------- stopRecord------------");
//				break;
//			}
//		}
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
		// 使用finally块来关闭输入流
		finally {
			// System.out.println("下载Finally...");
			try {
				if (inn != null) {
					inn.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}

	}

	long lastSeqNo = 0;
	private static Pattern pSeq = Pattern.compile("[^0-9]+([0-9]+)\\.ts");
	boolean downloadM3u8(String url, String fileName) {
		String realUrl = null;
		BufferedReader buReader = null;
		Logger.println(url);
		try {
			buReader = new BufferedReader(new InputStreamReader(new URL(url).openStream()));
			while ((realUrl = buReader.readLine()) != null) {
				realUrl = realUrl.trim();
				if (!realUrl.startsWith("#") && !realUrl.isEmpty()) {
					// 检测当前m3u8 序号是否符合要求, 不符合，继续下一行
					Matcher matcher = pSeq.matcher(realUrl);
					matcher.find();
					long currentSeqNo = Long.parseLong(matcher.group(1));
					if(currentSeqNo <= lastSeqNo) {
						// 视频源更新速度比不上下载速度，再等等
						Thread.sleep(1000);
						continue;
					}
					lastSeqNo = currentSeqNo;
					
					Logger.println(realUrl);
					// 如果是相对路径，补全
					if (!realUrl.startsWith("http")) {
						realUrl = M3u8Downloader.genABUrl(realUrl, url);
					}
					// 获取真实地址
					String fname = fileName + "-" + currentIndex + ".ts";
					//Logger.println(realUrl);
					if (!util.download(realUrl, fname, headers.getHeaders())) {
						return false;
					}
					currentIndex ++;
				}
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}finally {
			try {
				buReader.close();
			} catch (Exception e) {
			}
		}
		
	}
}
