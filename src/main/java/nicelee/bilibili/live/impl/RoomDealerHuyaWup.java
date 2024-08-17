package nicelee.bilibili.live.impl;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import com.qq.jce.wup.UniPacket;

import nicelee.bilibili.live.domain.RoomInfo;
import nicelee.bilibili.live.impl.huya.GetLivingInfoReq;
import nicelee.bilibili.live.impl.huya.GetLivingInfoRsp;
import nicelee.bilibili.live.impl.huya.MultiStreamInfo;
import nicelee.bilibili.live.impl.huya.StreamInfo;
import nicelee.bilibili.util.Logger;

public class RoomDealerHuyaWup extends RoomDealerHuya {

	final public static String liver = "huya2";

	@Override
	public String getType() {
		return ".flv";
	}

	/**
	 * https://www.huya.com/{shortId} shortId不是数字的需要在网页里面去找
	 * 
	 * @param shortId
	 * @return
	 */
	@Override
	public RoomInfo getRoomInfo(String shortId) {
		RoomInfo roomInfo = new RoomInfo();
		roomInfo.setShortId(shortId);
		long iRoomId = 0;
		try {
			iRoomId = Long.parseLong(shortId);
		} catch (Exception e) {
			throw new RuntimeException("id只能为数字");
		}
		try {
			GetLivingInfoRsp rsp = getRsp(iRoomId);
			roomInfo.setLiveStatus(rsp.bIsLiving);
			if (roomInfo.getLiveStatus() == 1) {
				roomInfo.setRoomId(shortId);
				roomInfo.setDescription(rsp.tNotice.sLiveDesc);
				roomInfo.setUserId(iRoomId);
				roomInfo.setUserName(rsp.tNotice.sNick);
				roomInfo.setTitle(rsp.tNotice.sLiveDesc);
				// 清晰度
				ArrayList<MultiStreamInfo> jArray = rsp.tNotice.vMultiStreamInfo;
				String[] qn = new String[jArray.size()];
				String[] qnDesc = new String[jArray.size()];
				for (int i = 0; i < jArray.size(); i++) {
					MultiStreamInfo objTemp = jArray.get(i);
					// qn[i] = "" + i;
					qn[i] = "" + objTemp.iBitRate;
					qnDesc[i] = objTemp.sDisplayName;
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
			GetLivingInfoRsp rsp = getRsp(Long.parseLong(roomId));
			StreamInfo streamDetail = null;
			ArrayList<StreamInfo> cdns = rsp.tNotice.vStreamInfo;
			for (int i = 0; i < cdns.size(); i++) {
				StreamInfo cdn = cdns.get(i);
				// ali CDN 似乎坚持不到5min就会断掉
				if (cdnType.equals(cdn.sCdnType)) {
					streamDetail = cdn;
					break;
				}
			}
			if (streamDetail == null) {
				streamDetail = cdns.get(cdns.size() - 1);
			}
//			GetCdnTokenExRsp cdnToken = getCdnToken(streamDetail.sFlvUrl, streamDetail.sStreamName);
//			Logger.println(cdnToken.sFlvToken);
			String antiCode = genFlvAntiCode(streamDetail.sStreamName, streamDetail.sFlvAntiCode, qn);
			Logger.println(streamDetail.sFlvAntiCode);
			Logger.println(antiCode);
			String url = String.format("%s/%s.%s?%s", streamDetail.sFlvUrl, streamDetail.sStreamName,
					streamDetail.sFlvUrlSuffix, antiCode);
//			if (!"".equals(qn) && !"0".equals(qn)) {
//				url = url + "&ratio=" + qn;
//			}
			// url = genRealUrl(url);
			Logger.println(url);
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
	}

	private GetLivingInfoRsp getRsp(long iRoomId) {
		UniPacket uniPacket = new UniPacket();
		uniPacket.useVersion3();
		uniPacket.setFuncName("getLivingInfo");
		uniPacket.setServantName("liveui");
		uniPacket.setEncodeName("UTF-8");
		uniPacket.setRequestId(1);
		uniPacket.put("tReq", new GetLivingInfoReq(iRoomId));
		HashMap<String, String> h = new HashMap<>();
		h.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:109.0) Gecko/20100101 Firefox/109.0");
		byte[] result = postContent("http://wup.huya.com", h, uniPacket.encode(), null);
		UniPacket var4x = new UniPacket();
		var4x.setEncodeName("UTF-8");
		var4x.decode(result);
		GetLivingInfoRsp rsp = var4x.getByClass("tRsp", new GetLivingInfoRsp());
		return rsp;
	}
	
//	private GetCdnTokenExRsp getCdnToken(String flvUrl, String streamName) {
//		UniPacket uniPacket = new UniPacket();
//		uniPacket.useVersion3();
//		uniPacket.setFuncName("getCdnTokenInfoEx");
//		uniPacket.setServantName("liveui");
//		uniPacket.setEncodeName("UTF-8");
//		uniPacket.setRequestId(1);
//		GetCdnTokenExReq tReq = new GetCdnTokenExReq(flvUrl, streamName);
//		uniPacket.put("tReq", tReq);
//		HashMap<String, String> h = new HashMap<>();
//		h.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:109.0) Gecko/20100101 Firefox/109.0");
//		byte[] result = postContent("http://wup.huya.com", h, uniPacket.encode(), null);
//		UniPacket var4x = new UniPacket();
//		var4x.setEncodeName("UTF-8");
//		var4x.decode(result);
//		GetCdnTokenExRsp rsp = var4x.getByClass("tRsp", new GetCdnTokenExRsp());
////		Logger.println(rsp.sFlvToken);
//		return rsp;
//	}

	private byte[] postContent(String url, HashMap<String, String> headers, Object param, List<HttpCookie> listCookie) {
		ByteArrayOutputStream result = new ByteArrayOutputStream();
		InputStream in = null;
		try {
			URL realUrl = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) realUrl.openConnection();
			conn.setConnectTimeout(20000);
			conn.setReadTimeout(20000);
			conn.setDoOutput(true); // 需要输出
			conn.setDoInput(true); // 需要输入
			conn.setUseCaches(false); // 不允许缓存
			conn.setRequestMethod("POST"); // 设置POST方式连接
			// 设置Headers
			for (Map.Entry<String, String> entry : headers.entrySet()) {
				conn.setRequestProperty(entry.getKey(), entry.getValue());
			}
			if (listCookie != null) {
				StringBuilder sb = new StringBuilder();
				for (HttpCookie cookie : listCookie) {
					sb.append(cookie.getName()).append("=").append(cookie.getValue()).append("; ");
				}
				String cookie = sb.toString();
				if (cookie.endsWith("; ")) {
					cookie = cookie.substring(0, cookie.length() - 2);
				}
				Logger.println(cookie);
				conn.setRequestProperty("Cookie", cookie);
			}
			conn.connect();
			// 建立输入流，向指向的URL传入参数
			DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
			if (param instanceof String) {
				dos.writeBytes((String) param);
			} else if (param instanceof byte[]) {
				dos.write((byte[]) param);
			} else {
				throw new Exception("不支持的param数据类型");
			}
			dos.flush();
			dos.close();

			String encoding = conn.getContentEncoding();
			in = conn.getInputStream();
			// 判断服务器返回的数据是否支持gzip压缩
			if (encoding != null && encoding.contains("gzip")) {
				in = new GZIPInputStream(conn.getInputStream());
			}
			byte[] buf = new byte[1024];
			int len = in.read(buf);
			while (len > -1) {
				result.write(buf, 0, len);
				len = in.read(buf);
			}
		} catch (Exception e) {
			System.out.println("发送POST请求出现异常！" + e);
			// e.printStackTrace();
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return result.toByteArray();
	}
}
