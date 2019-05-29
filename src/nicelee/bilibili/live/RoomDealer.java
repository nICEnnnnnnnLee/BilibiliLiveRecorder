package nicelee.bilibili.live;

import nicelee.bilibili.live.domain.RoomInfo;
import nicelee.bilibili.util.HttpHeaders;
import nicelee.bilibili.util.HttpRequestUtil;

public abstract class RoomDealer {

	public HttpRequestUtil util = new HttpRequestUtil();
	protected HttpHeaders headers = new HttpHeaders();

	/**
	  *  根据url的shortId获取房间信息
	 * @param shortId
	 * @return
	 */
	public abstract RoomInfo getRoomInfo(long shortId);
	
	/**
	 * 获取直播地址的下载链接
	 * 
	 * @param shortId
	 * @param qn
	 * @return
	 */
	public abstract String getLiveUrl(long shortId, String qn, Object...obj);

	/**
	 * 开始录制
	 * 
	 * @param url
	 * @param fileName
	 * @param shortId
	 * @return
	 */
	public abstract void startRecord(String url, String fileName, long shortId);

	/**
	 * 停止录制
	 */
	public void stopRecord() {
		util.stopDownload();
	}
	
	/**
	 * 文件大小转换为字符串
	 * 
	 * @param size
	 * @return
	 */
	final static long KB = 1024L;
	final static long MB = KB * 1024L;
	final static long GB = MB * 1024L;

	public static String transToSizeStr(long size) {
		if (size == 0) {
			return "未知";
		}
		double dSize;
		if (size >= GB) {
			dSize = size * 1.0 / GB;
			return String.format("%.2f GB", dSize);
		} else if (size >= MB) {
			dSize = size * 1.0 / MB;
			return String.format("%.2f MB", dSize);
		} else if (size >= KB) {
			dSize = size * 1.0 / KB;
			return String.format("%.2f KB", dSize);
		}
		return size + " Byte";
	}

}
