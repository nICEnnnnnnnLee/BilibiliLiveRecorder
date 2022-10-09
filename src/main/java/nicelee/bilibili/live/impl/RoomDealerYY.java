package nicelee.bilibili.live.impl;

import java.util.Arrays;
import java.util.HashMap;

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

	@Override
	public RoomInfo getRoomInfo(String shortId) {
		RoomInfo roomInfo = new RoomInfo();
		roomInfo.setShortId(shortId);
		try {
			// 获取基本信息
			String basicInfoUrl = String.format("https://wap.yy.com/mobileweb/play/liveinfo?sid=%s&ssid=%s", shortId,
					shortId);
			HashMap<String, String> header = new HashMap<>();
			header.put("User-Agent",
					"Mozilla/5.0 (iPhone; CPU iPhone OS 10_3_1 like Mac OS X) AppleWebKit/603.1.30 (KHTML, like Gecko) Version/10.0 Mobile/14E304 Safari/602.1");
			header.put("Referer", "https://wap.yy.com");
			String strJSON = util.getContent(basicInfoUrl, header, null);
			Logger.println(strJSON);
			JSONObject data = new JSONObject(strJSON).getJSONObject("data");

			roomInfo.setRoomId(shortId);
			roomInfo.setUserId(data.optLong("users"));
			roomInfo.setDescription(data.getString("liveDesc"));
			String liveName = data.optString("liveName");
			if (liveName.isEmpty()) {
				roomInfo.setLiveStatus(0);
			} else {
				roomInfo.setLiveStatus(1);
				roomInfo.setTitle(liveName);
				roomInfo.setUserName(data.getString("stageName"));
			}
			if (roomInfo.getLiveStatus() == 1) {
				// 获取直播源
				JSONObject livingStream = getLiveJsonObj(shortId, "1").getJSONObject("channel_stream_info");
				JSONArray streams = livingStream.getJSONArray("streams");
//				String currentQN = livingStream.getJSONObject("avp_payload").getJSONArray("stream_names").getString(0);
				HashMap<String, String> qnDesc = new HashMap<>();
				HashMap<String, Integer> qnHeight = new HashMap<>();
				for (int i = 0; i < streams.length(); i++) {
					JSONObject obj = streams.getJSONObject(i);
					if (!obj.has("stream_key"))
						continue;
					String json = obj.optString("json");
					// Logger.println(json);
					if (json.isEmpty())
						continue;
					JSONObject info = new JSONObject(json);
					JSONObject gear_info = info.optJSONObject("gear_info");
					if (gear_info == null)
						continue;
					String desc = gear_info.getString("name");
					String qn = gear_info.optString("gear");
					int height = info.optInt("height");
					int width = info.optInt("width");
					int rate = info.optInt("rate");
					Logger.printf("%s - %s: %d x %d, %d", qn, desc, width, height, rate);
					qnDesc.put(qn, desc);
					qnHeight.put(qn, height);
//					if(currentQN.equals(streams.getJSONObject(i).getString("stream_name"))) {
//						JSONObject temp = new JSONObject(streams.getJSONObject(i).getString("json"));
//						String[] qn = {"0"};
//						roomInfo.setAcceptQuality(qn);
//						roomInfo.setAcceptQualityDesc(
//								new String[]{temp.getJSONObject("gear_info").getString("name")});
//						break;
//					}
				}
				String[] qns = qnDesc.keySet().toArray(new String[qnDesc.size()]);
				Arrays.sort(qns, (qn1, qn2) -> {
					return qnHeight.get(qn2) - qnHeight.get(qn1);
				});
				String[] qnDescs = new String[qnDesc.size()];
				for (int i = 0; i < qns.length; i++) {
					qnDescs[i] = qnDesc.get(qns[i]);
				}
				roomInfo.setAcceptQuality(qns);
				roomInfo.setAcceptQualityDesc(qnDescs);
			}
			roomInfo.print();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return roomInfo;
	}

	public JSONObject getLiveJsonObj(String shortId, String qn) {
		long currentTime = System.currentTimeMillis();
		String liveDataUrl = String.format(
				"https://stream-manager.yy.com/v3/channel/streams?uid=0&cid=%s&sid=%s&appid=0&sequence=%d&encode=json",
				shortId, shortId, currentTime);
		String input = String.format("{\"head\":{\"seq\":%d,\"appidstr\":\"0\",\"bidstr\":\"121\","
				+ "\"cidstr\":\"%s\",\"sidstr\":\"%s\",\"uid64\":0,\"client_type\":108,\"client_ver\":\"5.11.0-alpha.4\","
				+ "\"stream_sys_ver\":1,\"app\":\"yylive_web\",\"playersdk_ver\":\"5.11.0-alpha.4\",\"thundersdk_ver\":\"0\","
				+ "\"streamsdk_ver\":\"5.11.0-alpha.4\"},\"client_attribute\":{\"client\":\"web\",\"model\":\"\",\"cpu\":\"\","
				+ "\"graphics_card\":\"\",\"os\":\"chrome\",\"osversion\":\"0\",\"vsdk_version\":\"\",\"app_identify\":\"\",\"app_version\":\"\","
				+ "\"business\":\"\",\"width\":\"1536\",\"height\":\"864\",\"scale\":\"\",\"client_type\":8,\"h265\":0},\"avp_parameter\":{\"version\":1,"
				+ "\"client_type\":8,\"service_type\":0,\"imsi\":0,\"send_time\":%d,\"line_seq\":-1,\"gear\":%s,\"ssl\":1,\"stream_format\":0}}",
				currentTime, shortId, shortId, currentTime / 1000, qn);
		Logger.println(input);
		HashMap<String, String> header2 = headers.getCommonHeaders("stream-manager.yy.com");
		header2.put("Referer", "https://www.yy.com/");
		String liveData = util.postContent(liveDataUrl, header2, input, null);
		Logger.println(liveData);
		return new JSONObject(liveData);
	}

	@Override
	public String getLiveUrl(String shortId, String qn, Object... obj) {
		try {
			// 得到数据
			JSONObject liveData = getLiveJsonObj(shortId, qn);
			// 查询直播源
			JSONObject stream_line_addr = liveData.getJSONObject("avp_info_res").getJSONObject("stream_line_addr");
			String name = JSONObject.getNames(stream_line_addr)[0];
			String url = stream_line_addr.getJSONObject(name).getJSONObject("cdn_info").getString("url");

			// 查询清晰度
			JSONArray streams = liveData.getJSONObject("channel_stream_info").getJSONArray("streams");
			for (int i = 0; i < streams.length(); i++) {
				JSONObject objj = streams.getJSONObject(i);
				String stream_key = objj.optString("stream_key");
				if (name.equals(stream_key)) {
					String json = objj.optString("json");
					if (json.isEmpty())
						continue;
					JSONObject info = new JSONObject(json);
					JSONObject gear_info = info.optJSONObject("gear_info");
					if (gear_info == null)
						continue;
					String gear = gear_info.optString("gear");
					System.out.printf("查询清晰度为 %s的链接， 得到清晰度为 %s的链接\r\n", qn, gear);
				}
			}
			return url;
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
