package nicelee.bilibili.live.impl;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLDecoder;
import java.util.HashMap;
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

			Pattern pJson = Pattern.compile("var TT_ROOM_DATA =(.*?);var TT_.{0,18}=");
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
			roomInfo.setUserId(Long.parseLong(room.getString("profileRoom")));
			
			pJson = Pattern.compile("var hyPlayerConfig = (.*?});");
			matcher = pJson.matcher(html);
			matcher.find();
			//System.out.println(matcher.group(1));
			JSONObject obj = new JSONObject(matcher.group(1));

			if(roomInfo.getLiveStatus() == 1) {
				// 房间主名称
				JSONObject liveInfo = obj.getJSONObject("stream").getJSONArray("data").getJSONObject(0).getJSONObject("gameLiveInfo");
				roomInfo.setUserName(liveInfo.getString("nick"));
				roomInfo.setTitle(liveInfo.getString("roomName"));
				// 清晰度
				JSONArray jArray = obj.getJSONObject("stream").getJSONArray("vMultiStreamInfo");
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
			JSONObject obj = new JSONObject(matcher.group(1));// vMultiStreamInfo 

			JSONObject streamDetail = obj.getJSONObject("stream").getJSONArray("data").getJSONObject(0)
					.getJSONArray("gameStreamInfoList").getJSONObject(0);// Integer.parseInt(qn)
//			String url = String.format("%s/%s.m3u8?%s", streamDetail.getString("sHlsUrl"),
//					streamDetail.getString("sStreamName"), streamDetail.getString("sHlsAntiCode"));
			String url = String.format("%s/%s.%s?%s", streamDetail.getString("sFlvUrl"),
					streamDetail.getString("sStreamName"), 
					streamDetail.getString("sFlvUrlSuffix"), 
					streamDetail.getString("sFlvAntiCode").replace("&amp;", "&"));
			if(!"".equals(qn) && !"0".equals(qn)) {
				url = url + "&ratio=" + qn;
			}
			Logger.println(url);
			//Logger.println(obj.getJSONObject("stream").getInt("iWebDefaultBitRate"));
			return url;
		} catch (Exception e) {
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
