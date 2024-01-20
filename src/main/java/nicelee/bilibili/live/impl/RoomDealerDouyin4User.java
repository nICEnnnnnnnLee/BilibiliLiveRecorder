package nicelee.bilibili.live.impl;

import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

import nicelee.bilibili.live.RoomDealer;
import nicelee.bilibili.live.domain.RoomInfo;
import nicelee.bilibili.util.HttpCookies;
import nicelee.bilibili.util.Logger;

public class RoomDealerDouyin4User extends RoomDealer {

	final public static String liver = "douyin";
	final static String MOBILE_USERAGENT = "Mozilla/5.0 (Linux; Android 11; SAMSUNG SM-G973U) AppleWebKit/537.36 (KHTML, like Gecko) SamsungBrowser/14.2 Chrome/87.0.4280.141 Mobile Safari/537.36";

	@Override
	public String getType() {
		return ".flv";
	}

	/**
	 * @param shortId 只能是https://live.douyin.com/后面的那一串数字
	 * @return
	 */
	@Override
	public RoomInfo getRoomInfo(String shortId) {
		RoomInfo roomInfo = new RoomInfo();
		roomInfo.setShortId(shortId);
		try {
			String roomId = shortId;

			JSONObject room = getLiveDataByShortId(roomId).getJSONObject("data").getJSONObject("room");
			JSONObject owner = room.getJSONObject("owner");
			JSONObject stream_url = room.optJSONObject("stream_url");

			roomInfo.setUserName(owner.getString("nickname"));
			roomInfo.setRoomId(roomId);
			roomInfo.setUserId(owner.optLong("id"));
			roomInfo.setTitle(room.getString("title"));
			roomInfo.setDescription(owner.getString("signature"));
			int status = room.optInt("status", 0);
			if (stream_url == null || status == 4) {
				roomInfo.setLiveStatus(0);
			} else {
				roomInfo.setLiveStatus(1);
				JSONArray flv_sources = stream_url.getJSONObject("live_core_sdk_data").getJSONObject("pull_data")
						.getJSONObject("options").getJSONArray("qualities");
				int flv_sources_len = flv_sources.length();
				String[] qn = new String[flv_sources_len];
				String[] qnDesc = new String[flv_sources_len];
				for (int i = 0; i < flv_sources_len; i++) {
					// 为了让0, 1, 2, 3 数字越小清晰度越高
					JSONObject obj = flv_sources.getJSONObject(i);
					int level = obj.getInt("level");
					qn[i] = "" + i;
					qnDesc[flv_sources_len - level] = obj.getString("name");
				}
				roomInfo.setAcceptQuality(qn);
				roomInfo.setAcceptQualityDesc(qnDesc);
			}
			roomInfo.print();
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("抖音需要cookie, 请确认cookie是否存在或失效");
			return null;
		}
		return roomInfo;
	}

	@Override
	public String getLiveUrl(String roomId, String qn, Object... obj) {
		try {
			JSONObject room = getLiveDataByShortId(roomId).getJSONObject("data").getJSONObject("room");
			JSONObject stream_url = room.optJSONObject("stream_url");
			JSONArray flv_sources = stream_url.getJSONObject("live_core_sdk_data").getJSONObject("pull_data")
					.getJSONObject("options").getJSONArray("qualities");
			int flv_sources_len = flv_sources.length();
			String sdk_key = null;
			for (int i = 0; i < flv_sources_len; i++) {
				JSONObject quality = flv_sources.getJSONObject(i);
				int level = quality.getInt("level");
				// 这里要与前面一致
				if (qn.equals("" + (flv_sources_len - level))) {
					sdk_key = quality.getString("sdk_key");
					break;
				}
			}

			String pull_data = stream_url.getJSONObject("live_core_sdk_data").getJSONObject("pull_data")
					.getString("stream_data");
			JSONObject data = new JSONObject(pull_data).getJSONObject("data");
			String link = data.getJSONObject(sdk_key).getJSONObject("main").getString("flv");
			Logger.println(link);
			return link;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	JSONObject getLiveDataByShortId(String shortId) {
		try {
			// 先访问获取 webcast_id 和 sec_user_id
			URL pcUrl = new URL("https://live.douyin.com/" + shortId);
			HttpURLConnection conn = (HttpURLConnection) pcUrl.openConnection();
			conn.setInstanceFollowRedirects(false);
			conn.setRequestProperty("User-Agent", MOBILE_USERAGENT);
			conn.connect();
			conn.disconnect();
			String location = conn.getHeaderField("Location");
			Logger.println(location);
			if (location == null) {
				conn = (HttpURLConnection) pcUrl.openConnection();
				conn.setInstanceFollowRedirects(false);
				conn.setRequestProperty("User-Agent", MOBILE_USERAGENT);
				conn.connect();
				conn.disconnect();
				location = conn.getHeaderField("Location");
				Logger.println(location);
			}
			Pattern p = Pattern
					.compile("https://webcast.amemv.com/douyin/webcast/reflow/([0-9]+)?.*sec_user_id=([^&]+)");
			Matcher m = p.matcher(location);
			if (!m.find())
				throw new Exception("未能找到对应的 webcast_id 和 sec_user_id");
			String webcast_id = m.group(1);
			String sec_user_id = m.group(2);
			
			// 随机生成 verifyFp
			long currentTime = System.currentTimeMillis();
			String verifyFp = verifyFp(currentTime);
			String msToken = ""; // TODO
			String cookie = "s_v_web_id=" + verifyFp;
			String query = "type_id=0&live_id=1&version_code=99.99.99&app_id=1128&verifyFp=%s&room_id=%s&sec_user_id=%s&msToken=%s";
			query = String.format(query, verifyFp, webcast_id, sec_user_id, msToken);
			String xBogus = xBogus(query, MOBILE_USERAGENT, currentTime, "");
			String url = "https://webcast.amemv.com/webcast/room/reflow/info/?" + query + "&X-Bogus=" + xBogus;
			Logger.println(url);
			String json_str = util.getContent(url, getMobileHeader(), HttpCookies.convertCookies(cookie));
			Logger.println(json_str);
			return new JSONObject(json_str);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private HashMap<String, String> mobileHeader;

	private HashMap<String, String> getMobileHeader() {
		if (mobileHeader == null) {
			mobileHeader = new HashMap<>();
			mobileHeader.put("User-Agent", MOBILE_USERAGENT);
			mobileHeader.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
			mobileHeader.put("Accept-Language", "zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5,en-US;q=0.3,en;q=0.2");
		}
		return mobileHeader;
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
		mobile.put("User-Agent", MOBILE_USERAGENT);
		util.download(url, fileName + ".flv", mobile);
	}

	private String md5(String data) {
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

	private String md5x2(String data) {
		try {
			MessageDigest md5 = MessageDigest.getInstance("md5");
			byte[] secretBytes = md5.digest(data.getBytes());
			byte[] secretBytesX2 = md5.digest(secretBytes);
			String md5code = new BigInteger(1, secretBytesX2).toString(16);
			for (int i = 0; i < 32 - md5code.length(); i++) {
				md5code = "0" + md5code;
			}
			return md5code;
		} catch (Exception e) {
			throw new RuntimeException();
		}
	}

	private String rc4Encrypt(String plainText, int[] key) {
		int s_box[] = new int[256];
		for (int i = 0; i < 256; i++) {
			s_box[i] = i;
		}
		int index = 0;
		for (int i = 0; i < 256; i++) {
			index = (index + s_box[i] + key[i % key.length]) % 256;
			int temp = s_box[i];
			s_box[i] = s_box[index];
			s_box[index] = temp;
		}
		index = 0;
		int i = 0;
		StringBuilder cipherText = new StringBuilder();
		for (char ch : plainText.toCharArray()) {
			i = (i + 1) % 256;
			index = (index + s_box[i]) % 256;
			int temp = s_box[i];
			s_box[i] = s_box[index];
			s_box[index] = temp;
			int keystream = s_box[(s_box[i] + s_box[index]) % 256];
			cipherText.append((char) (((int) ch) ^ keystream));
		}
		return cipherText.toString();
	}

	private String b64Encode(String plainText, String keyTable) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < plainText.length(); i = i + 3) {
			int num1 = 0, num2 = 0, num3, a1, a2, a3, a4;
			try {
				num1 = (int) plainText.charAt(i);
				num2 = (int) plainText.charAt(i + 1);
				num3 = (int) plainText.charAt(i + 2);
				a1 = num1 >> 2;
				a2 = (3 & num1) << 4 | (num2 >> 4);
				a3 = ((15 & num2) << 2) | (num3 >> 6);
				a4 = 63 & num3;
			} catch (Exception e) {
				a1 = num1 >> 2;
				a2 = (3 & num2) << 4 | 0;
				a3 = 64;
				a4 = 64;
			}
			sb.append(keyTable.charAt(a1));
			sb.append(keyTable.charAt(a2));
			sb.append(keyTable.charAt(a3));
			sb.append(keyTable.charAt(a4));
		}
		return sb.toString();
	}

	private long[] filter(List<Long> num_list) {
		int ns[] = { 3, 5, 7, 9, 11, 13, 15, 17, 19, 21, 4, 6, 8, 10, 12, 14, 16, 18, 20 };
		long[] result = new long[ns.length];
		for (int i = 0; i < ns.length; i++) {
			result[i] = num_list.get(ns[i] - 1);
		}
		return result;
	}

	private String scramble(long... inputs) {
		StringBuilder sb = new StringBuilder();
		sb.append((char) inputs[0]).append((char) inputs[10]).append((char) inputs[1]).append((char) inputs[11])
				.append((char) inputs[2]).append((char) inputs[12]).append((char) inputs[3]).append((char) inputs[13])
				.append((char) inputs[4]).append((char) inputs[14]).append((char) inputs[5]).append((char) inputs[15])
				.append((char) inputs[6]).append((char) inputs[16]).append((char) inputs[7]).append((char) inputs[17])
				.append((char) inputs[8]).append((char) inputs[18]).append((char) inputs[9]);
		return sb.toString();
	}

	private long checksum(List<Long> salt_list) {
		long checksum = 64;
		for (int i = 3; i < salt_list.size(); i++) {
			checksum = checksum ^ salt_list.get(i);
		}
		return checksum;
	}

	private String xBogus(String params, String userAgent, long timestamp, String data) {
		String md5_data = md5x2(data);
		String md5_params = md5x2(params);
		int[] key = { 0, 1, 14 };
		String md5_ua = rc4Encrypt(userAgent, key);
		md5_ua = b64Encode(md5_ua, "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=");
		md5_ua = md5(md5_ua);
		List<Long> salt_list = new ArrayList<Long>();
		salt_list.add(timestamp);
		salt_list.add(536919696L);
		salt_list.add(64L);
		salt_list.add(0L);
		salt_list.add(1L);
		salt_list.add(14L);
		salt_list.add(Long.parseLong(md5_params.substring(md5_params.length() - 4, md5_params.length() - 2), 16));
		salt_list.add(Long.parseLong(md5_params.substring(md5_params.length() - 2, md5_params.length()), 16));
		salt_list.add(Long.parseLong(md5_data.substring(md5_data.length() - 4, md5_data.length() - 2), 16));
		salt_list.add(Long.parseLong(md5_data.substring(md5_data.length() - 2, md5_data.length()), 16));
		salt_list.add(Long.parseLong(md5_ua.substring(md5_ua.length() - 4, md5_ua.length() - 2), 16));
		salt_list.add(Long.parseLong(md5_ua.substring(md5_ua.length() - 2, md5_ua.length()), 16));
		salt_list.add((timestamp >> 24) & 0xff);
		salt_list.add((timestamp >> 16) & 0xff);
		salt_list.add((timestamp >> 8) & 0xff);
		salt_list.add((timestamp) & 0xff);
		salt_list.add((salt_list.get(1) >> 24) & 0xff);
		salt_list.add((salt_list.get(1) >> 16) & 0xff);
		salt_list.add((salt_list.get(1) >> 8) & 0xff);
		salt_list.add((salt_list.get(1)) & 0xff);
		salt_list.add(checksum(salt_list));
		salt_list.add(255L);
		long[] num_list = filter(salt_list);
		StringBuilder sb = new StringBuilder().append((char) 2).append((char) 255);
		String rc4_num_list = rc4Encrypt(scramble(num_list), new int[] { 255 });
		sb.append(rc4_num_list);
		return b64Encode(sb.toString(), "Dkdpgh4ZKsQB80/Mfvw36XI1R25-WUAlEi7NLboqYTOPuzmFjJnryx9HVGcaStCe=");
	}

	private String verifyFp(long currentTime) {
		String[] t = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".split("");
		int len = t.length;
		String n = Long.toString(currentTime, 36);
		char[] r = new char[36];
		r[8] = '_';
		r[13] = '_';
		r[14] = '4';
		r[18] = '_';
		r[23] = '_';
		Random random = new Random();
		for (int o = 0, i = 0; o < 36; o++) {
			if (r[o] == (char) 0) {
				i = 0 | random.nextInt(len);
				int index = 19 == o ? (3 & i | 8) : i;
				r[o] = t[index].charAt(0);
			}
		}
		return new StringBuilder().append("verify_").append(n).append("_").append(r).toString();
	}

}
