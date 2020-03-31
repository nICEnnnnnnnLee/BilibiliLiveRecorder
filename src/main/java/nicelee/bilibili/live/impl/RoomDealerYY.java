package nicelee.bilibili.live.impl;

import java.net.URLDecoder;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

import nicelee.bilibili.live.RoomDealer;
import nicelee.bilibili.live.domain.RoomInfo;
import nicelee.bilibili.util.Logger;

public class RoomDealerYY extends RoomDealer {

	final public static String liver = "yy";

	@Override
	public String getType() {
		return ".flv";
	}

	/**
	 * http://www.yy.com/{shortId}/{shortId} 根据url的shortId获取房间信息(从HTML获取)
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
			String basicInfoUrl = String.format("http://www.yy.com/%s/%s", shortId, shortId);
			String html = util.getContent(basicInfoUrl, headers.getCommonHeaders("www.yy.com"), null);
			// Logger.println(html);
			Pattern pJson = Pattern.compile("var pageInfo = *(.*?});");
			Matcher matcher = pJson.matcher(html);
			matcher.find();
			String strJSON = matcher.group(1).replaceAll("decodeURIComponent\\(\"(.*?)\"\\)", "\"$1\"")
					.replaceFirst("defaultQuality:.*?,", "");
			Logger.println(strJSON);
			JSONObject jObj = new JSONObject(strJSON);

			roomInfo.setRoomId(shortId);
			roomInfo.setUserId(Long.parseLong(jObj.getString("uid")));
			roomInfo.setTitle(URLDecoder.decode(jObj.getString("roomName")));
			// TODO
			roomInfo.setDescription("TODO 暂无");
			roomInfo.setUserName(jObj.getString("nick"));
			roomInfo.setLiveStatus(0);
			// 获取直播可提供的清晰度
			pJson = Pattern.compile("var livingStream = *(.*?});");
			matcher = pJson.matcher(html);
			if(matcher.find()) {
				roomInfo.setLiveStatus(1);
				Logger.println(matcher.group(1));
				JSONObject livingStream = new JSONObject(matcher.group(1));
				// livingStream.avp_payload.stream_names[0]
				String currentQN = livingStream.getJSONObject("avp_payload").getJSONArray("stream_names").getString(0);
				// livingStream.channel_stream_info.streams[2].stream_name
				// livingStream.channel_stream_info.streams[2].json  --> .gear_info.name
				JSONArray streams = livingStream.getJSONObject("channel_stream_info").getJSONArray("streams");
				for(int i=0; i<streams.length(); i++) {
					if(currentQN.equals(streams.getJSONObject(i).getString("stream_name"))) {
						JSONObject temp = new JSONObject(streams.getJSONObject(i).getString("json"));
						String[] qn = {"0"};
						roomInfo.setAcceptQuality(qn);
						roomInfo.setAcceptQualityDesc(
								new String[]{temp.getJSONObject("gear_info").getString("name")});
						break;
					}
				}
			}
			roomInfo.print();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return roomInfo;
	}

	@Override
	public String getLiveUrl(String shortId, String qn, Object... obj) {
		try {
			// 获取基础信息
			String basicInfoUrl = String.format("http://www.yy.com/%s/%s", shortId, shortId);
			String html = util.getContent(basicInfoUrl, headers.getCommonHeaders("www.yy.com"), null);
			// Logger.println(html);
			Pattern pJson = Pattern.compile("var livingStream = *(.*?});");
			Matcher matcher = pJson.matcher(html);
			matcher.find();
			Logger.println(matcher.group(1));
			JSONObject jObj = new JSONObject(matcher.group(1));
			
			StringBuilder sb = new StringBuilder();
			String addr = jObj.getJSONObject("avp_payload").getString("addr");
			String[] args = addr.split("\\n");
//			Logger.println(args[0]);
			
			String decodedStr = new String(Base64.getDecoder().decode(args[0]));
			sb.append(decodedStr.substring(decodedStr.indexOf("http")));
			for(int i=1; i<args.length; i++) {
				decodedStr = new String(Base64.getDecoder().decode(args[i]));
				if(decodedStr.matches("^[0-9a-zA-Z\\:\\.\\?\\/\\-=_&%]+$")) {
					sb.append(decodedStr);
				}else {
					sb.append(decodedStr.replaceFirst("[^0-9a-zA-Z\\:\\.\\?\\/\\-=_&%].*$", ""));
					//Logger.println(decodedStr  + "不符合要求");
					//Logger.println(decodedStr.replaceFirst("[^0-9a-zA-Z\\:\\.\\?\\/\\-=_&%].*$", ""));
					break;
				}
			}
			Logger.println(sb.toString());
			return sb.toString();
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
