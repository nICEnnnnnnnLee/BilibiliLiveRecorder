package nicelee.bilibili.live.impl;

import org.json.JSONArray;
import org.json.JSONObject;

import nicelee.bilibili.live.RoomDealer;
import nicelee.bilibili.live.domain.RoomInfo;
import nicelee.bilibili.util.HttpCookies;
import nicelee.bilibili.util.HttpHeaders;
import nicelee.bilibili.util.HttpRequestUtil;
import nicelee.bilibili.util.Logger;

public class RoomDealerKuaishou extends RoomDealer {

	final public static String liver = "kuaishou";

	public RoomDealerKuaishou() {
		cookie = "clientid=3; did=web_b49724cc62a3b45da08e16dddf593af4; client_key=65890b29; didv=1563932749000; kuaishou.live.bfb1s=3e261140b0cf7444a0ba411c6f227d88; needLoginToWatchHD=1";
	}

	@Override
	public String getType() {
		return ".flv";
	}

	/**
	 * https://live.kuaishou.com/u/shortId 根据url的shortId获取房间信息(从网页里面爬)
	 * 
	 * @param shortId
	 * @return
	 */
	@Override
	public RoomInfo getRoomInfo(String shortId) {
		RoomInfo roomInfo = new RoomInfo();
		roomInfo.setShortId(shortId);
		try {

			JSONObject user = getUserInfoObj(shortId);
			JSONObject live = null;
			try {
				live = getLiveInfoObj(shortId);
				// 直播状态信息
				if (live != null && live.getJSONArray("playUrls").length() > 0)
					roomInfo.setLiveStatus(1);
				else
					roomInfo.setLiveStatus(0);
			} catch (Exception e) {
				roomInfo.setLiveStatus(0);
			}

			// 真实房间id
			roomInfo.setRoomId(shortId);
			// 房间主id
			roomInfo.setUserId(user.getLong("userId"));
			// 房间主名称
			roomInfo.setUserName(user.getString("name"));

			// 直播描述
			roomInfo.setDescription(user.getString("description"));

			if (roomInfo.getLiveStatus() == 1) {
				roomInfo.setTitle(live.optString("caption", roomInfo.getDescription()));
				// 清晰度
				JSONArray jArray = live.getJSONArray("playUrls");
				String[] qn = new String[jArray.length()];
				String[] qnDesc = new String[jArray.length()];
				for (int i = 0; i < jArray.length(); i++) {
					JSONObject objTemp = jArray.getJSONObject(i);
					qn[i] = "" + i;
					qnDesc[i] = objTemp.getString("quality");
				}
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
			JSONObject obj = getLiveInfoObj(roomId);
			JSONArray array = obj.getJSONArray("playUrls");
			int order = 0;
			try {
				order = Integer.parseInt(qn);
				if (order > array.length() - 1) {
					order = array.length() - 1;
					System.out.println("没有匹配的清晰度，选取最模糊值：" + array.getJSONObject(0).getString("quality"));
				}
			} catch (Exception e) {
				System.out.println("没有匹配的清晰度，选取最清晰值：" + array.getJSONObject(0).getString("quality"));
			}
			return array.getJSONObject(order).getString("url");
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * @param roomId
	 * @return
	 */
	private JSONObject getUserInfoObj(String roomId) {

		StringBuffer param = new StringBuffer();
		param.append("{\"operationName\":\"userInfoQuery\",\"variables\":{\"principalId\":\"");
		param.append(roomId);
		param.append(
				"\"},\"query\":\"query userInfoQuery($principalId: String) {\\n  userInfo(principalId: $principalId) {\\n    id\\n    principalId\\n    kwaiId\\n    eid\\n    userId\\n    profile\\n    name\\n    description\\n    sex\\n    constellation\\n    cityName\\n    living\\n    watchingCount\\n    isNew\\n    privacy\\n    feeds {\\n      eid\\n      photoId\\n      thumbnailUrl\\n      timestamp\\n      __typename\\n    }\\n    verifiedStatus {\\n      verified\\n      description\\n      type\\n      new\\n      __typename\\n    }\\n    countsInfo {\\n      fan\\n      follow\\n      photo\\n      liked\\n      open\\n      playback\\n      private\\n      __typename\\n    }\\n    bannedStatus {\\n      banned\\n      defriend\\n      isolate\\n      socialBanned\\n      __typename\\n    }\\n    __typename\\n  }\\n}\\n\"}");

		util = new HttpRequestUtil();
		String json = util.postContent("https://live.kuaishou.com/graphql",
				new HttpHeaders().getKuaishouHeaders(roomId), param.toString(), HttpCookies.convertCookies(cookie));
		Logger.println(json);
		JSONObject obj = new JSONObject(json).getJSONObject("data").getJSONObject("userInfo");
		return obj;
	}

	/**
	 * @param roomId
	 * @return
	 */
	private JSONObject getLiveInfoObj(String roomId) {
		String graphSqlUrl = String.format("https://live.kuaishou.com/graphql");

		StringBuffer param = new StringBuffer();
		param.append("{\"operationName\":\"LiveDetail\",\"variables\":{\"principalId\":\"");
		param.append(roomId);
		param.append(
				"\"},\"query\":\"query LiveDetail($principalId: String) {\\n  liveDetail(principalId: $principalId) {\\n    liveStream\\n    feedInfo {\\n      pullCycleMillis\\n      __typename\\n    }\\n    watchingInfo {\\n      likeCount\\n      watchingCount\\n      __typename\\n    }\\n    noticeList {\\n      feed\\n      options\\n      __typename\\n    }\\n    fastComments\\n    commentColors\\n    moreRecommendList {\\n      user {\\n        id\\n        profile\\n        name\\n        __typename\\n      }\\n      watchingCount\\n      src\\n      title\\n      gameId\\n      gameName\\n      categoryId\\n      liveStreamId\\n      playUrls {\\n        quality\\n        url\\n        __typename\\n      }\\n      quality\\n      gameInfo {\\n        category\\n        name\\n        pubgSurvival\\n        type\\n        kingHero\\n        __typename\\n      }\\n      redPack\\n      liveGuess\\n      expTag\\n      __typename\\n    }\\n    __typename\\n  }\\n}\\n\"}\r\n");

		String json = util.postContent(graphSqlUrl, headers.getKuaishouHeaders(roomId), param.toString(),
				null);
		Logger.println(json);
		JSONObject obj = new JSONObject(json).getJSONObject("data").getJSONObject("liveDetail")
				.getJSONObject("liveStream");
		return obj;
	}

	@Override
	public void startRecord(String url, String fileName, String shortId) {
		util.download(url, fileName + ".flv", headers.getKuaishouLiveRecordHeaders(url, shortId));
	}

}
