package nicelee.bilibili.live.impl;

import java.util.Base64;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

import nicelee.bilibili.live.RoomDealer;
import nicelee.bilibili.live.domain.RoomInfo;
import nicelee.bilibili.util.HttpHeaders;
import nicelee.bilibili.util.Logger;

public class RoomDealerZhanqi extends RoomDealer{


	final public static String liver = "zhanqi";
	
	@Override
	public String getType() {
		return ".flv";
	}
	
	/**
	 * https://www.zhanqi.tv/{shortId}
	 *  根据url的shortId获取房间信息(从HTML获取)
	 * @param shortId
	 * @return
	 */
	@Override
	public RoomInfo getRoomInfo(String shortId) {
		RoomInfo roomInfo = new RoomInfo();
		roomInfo.setShortId(shortId);
		try {
			// 获取基础信息
			String basicInfoUrl = String.format("https://www.zhanqi.tv/%s", shortId);
			String html = util.getContent(basicInfoUrl, headers.getZhanqiHeaders(), null);
			//Logger.println(html);
			Pattern pJson = Pattern.compile("window.oPageConfig.oRoom =(.*?});");
			Matcher matcher = pJson.matcher(html);
			matcher.find();
			Logger.println(matcher.group(1));
			JSONObject jObj = new JSONObject(matcher.group(1));
			
			roomInfo.setRoomId(jObj.getString("id"));
			roomInfo.setUserId(Long.parseLong(jObj.getString("uid")));
			roomInfo.setTitle(jObj.getString("title"));
			if(jObj.has("starDesc")) {
				roomInfo.setDescription(jObj.getString("starDesc"));
			}
			if("4".equals(jObj.getString("status"))) {
				roomInfo.setLiveStatus(1);
			}else {
				roomInfo.setLiveStatus(0);
			}
			roomInfo.setUserName(jObj.getString("nickname"));

			roomInfo.setRemark(jObj.getString("videoId"));
			if (roomInfo.getLiveStatus() == 1) {
				// 获取直播可提供的清晰度
				String strBase64 = jObj.getJSONObject("flashvars").getString("h5Cdns");
				String streams = new String(Base64.getDecoder().decode(strBase64));
				Logger.println(streams);
				jObj = new JSONObject(streams);
				
				JSONArray descAll = jObj.getJSONArray("rate");
				JSONArray suffix = jObj.getJSONArray("suffix");
				int[][] square = qnSquare(jObj.getJSONArray("square"));
				HashMap<String, String> qnMap = new LinkedHashMap<>();
				for(int i=0; i<descAll.length(); i++) {
					// 判断202线路该清晰度是否存在
					for(int kk=0; kk<square.length; kk++) {
						if(square[kk][i] == 202) {
							qnMap.put(descAll.getString(i), suffix.getString(i));
							break;
						}
					}
				}
				if(qnMap.size() > 0) {
					String[] qn = new String[qnMap.size()];
					String[] qnDesc = new String[qnMap.size()];
					int i = 0;
					for(Entry<String, String> entry: qnMap.entrySet()) {
						qn[i] = entry.getValue();
						qnDesc[i] = entry.getKey();
						i++;
					}
					roomInfo.setAcceptQuality(qn);
					roomInfo.setAcceptQualityDesc(qnDesc);
				}
			}
			roomInfo.print();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return roomInfo;
	}
	private int[][] qnSquare(JSONArray array) {
		int xx = array.length();
		int yy = array.getJSONArray(0).length();
		int[][] square = new int[xx][yy];
		for(int i=0; i<xx; i++) {
			JSONArray line = array.getJSONArray(i);
			for(int j=0; j<yy; j++) {
				square[i][j] = line.getInt(j);
			}
		}
		return square;
	}
	
	/**
	 * 默认只采用一条线路，写死了。如果要变，搜 202 以及 alhdl-cdn.zhanqi.tv
	 */
	@Override
	public String getLiveUrl(String shortId, String qn, Object...obj) {
		// 42: "https://wshdl-cdn.zhanqi.tv/zqlive/{0}{_720p|_480p}.flv",
		// 72: "https://yfhdl-p2p-cdn.zhanqi.tv/zqlive/{0}{_720p|_480p}.flv",
	    //172: "https://yfhdl-cdn.zhanqi.tv/zqlive/{0}{_720p|_480p}.flv",
	    //192: "https://txhdl-cdn.zhanqi.tv/zqlive/{0}{_720p|_480p}.flv",
	    //202: "https://alhdl-cdn.zhanqi.tv/zqlive/{0}{_720p|_480p}.flv",
	    //232: "https://txkcardhdl-cdn.zhanqi.tv/zqlive/{0}{1}.flv",
		try {
			// 默认使用线路 alhdl-cdn.zhanqi.tv
			String format = "https://%s/alhdl-cdn.zhanqi.tv/zqlive/%s%s.flv?%s&ipFrom=1&clientIp=&fhost=h5&platform=128";
			String streamId = (String) obj[0];
			String gid = ("" + System.currentTimeMillis()).substring(3, 13); // 随机产生即可
			String cdn = getCDN(streamId, gid);
			String qnTail = qn;
			String token = getToken(shortId, streamId, qnTail, gid);
			return String.format(format, cdn, streamId, qnTail, token);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 获取token，用于FLV链接下载，无此参数无法下载
	 * @param shortId
	 * @param streamId
	 * @param qnTail
	 * @param gid
	 * @return
	 */
	private String getToken(String shortId, String streamId, String qnTail, String gid) {
		String boundary = "-----------------------" + System.currentTimeMillis();
		StringBuilder params = new StringBuilder();
		params
			// streamId
			.append("--").append(boundary).append("\r\n")
			.append("Content-Disposition: form-data; name=\"stream\"\r\n\r\n")
			.append(streamId).append(qnTail).append(".flv\r\n")
			// cdnKey
			.append("--").append(boundary).append("\r\n")
			.append("Content-Disposition: form-data; name=\"cdnKey\"\r\n\r\n")
			.append("202\r\n")
			// platform
			.append("--").append(boundary).append("\r\n")
			.append("Content-Disposition: form-data; name=\"platform\"\r\n\r\n")
			.append("128\r\n")
			// platform
			.append("--").append(boundary).append("--\r\n");
		String json = util.postContent("https://www.zhanqi.tv/api/public/burglar/chain", 
				new HttpHeaders().getZhanqiTokenHeaders(shortId, boundary, gid),
				params.toString());
		Logger.println(json);
		return new JSONObject(json).getJSONObject("data").getString("key");
	}
	
	/**
	 * 用于获取alhdl-cdn.zhanqi.tv线路的 CDN
	 * @param streamId
	 * @param gid 非必需
	 * @return
	 */
	private String getCDN(String streamId, String gid) {
		String json = util.getContent("https://umc.danuoyi.alicdn.com/dns_resolve_https?app=zqlive&host_key=alhdl-cdn.zhanqi.tv&protocol=hdl&client_ip=182.92.104.151&platform=128" + 
				"&stream=" + streamId + 
				"&gid=" + gid, new HttpHeaders().getHeaders());
		return new JSONObject(json).getJSONArray("redirect_domain").getString(0);
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
