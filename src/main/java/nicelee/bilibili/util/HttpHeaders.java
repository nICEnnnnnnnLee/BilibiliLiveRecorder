package nicelee.bilibili.util;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpHeaders {
	HashMap<String, String> headerMap = new HashMap<String, String>();

	final private static Pattern host = Pattern.compile("^https?://([^/]+)/");

	public static String getHost(String url) {
		Matcher matcher = host.matcher(url);
		matcher.find();
		return matcher.group(1);
	}

	public void setHeader(String key, String value) {
		headerMap.put(key, value);
	}

	public String getHeader(String key) {
		return headerMap.get(key);
	}

	public HashMap<String, String> getHeaders() {
		headerMap.remove("Host");
		return headerMap;
	}

	/**
	 * 该Header配置用于FLV视频下载
	 */
	public HashMap<String, String> getBiliWwwFLVHeaders(String avId) {
		headerMap.put("X-Requested-With", "ShockwaveFlash/28.0.0.137");
		headerMap.put("Referer", "https://www.bilibili.com/video/" + avId);// need addavId
		headerMap.put("User-Agent",
				"Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36 SE 2.X MetaSr 1.0");
		return headerMap;
	}

	/**
	 * 该Header配置用于M4s视频下载
	 */
	public HashMap<String, String> getBiliWwwM4sHeaders(String avId) {
		headerMap.remove("X-Requested-With");
		headerMap.put("Referer", "https://www.bilibili.com/video/" + avId);// need addavId
		headerMap.put("User-Agent",
				"Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36 SE 2.X MetaSr 1.0");
		return headerMap;
	}

	/**
	 * 该Header配置用于直播 api 信息查询
	 */
	public HashMap<String, String> getBiliLiveJsonAPIHeaders(long shortId) {
		headerMap = new HashMap<String, String>();
		headerMap.put("Accept", "application/json, text/javascript, */*; q=0.01");
		headerMap.put("Accept-Encoding", "gzip");
		headerMap.put("Accept-Language", "zh-CN,zh;q=0.8");
		headerMap.put("Connection", "keep-alive");
		headerMap.put("Host", "api.bilibili.com");
		headerMap.put("Origin", "https://live.bilibili.com");
		headerMap.put("Referer", "https://live.bilibili.com/blanc/" + shortId);// need addavId
		headerMap.put("User-Agent",
				"Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36 SE 2.X MetaSr 1.0");
		headerMap.put("X-Requested-With", "ShockwaveFlash/28.0.0.137");
		return headerMap;
	}

	/**
	 * 该Header配置用于kuaishou直播flv下载
	 */
	public HashMap<String, String> getKuaishouLiveRecordHeaders(String url, String shortId) {
		headerMap = new HashMap<String, String>();
		headerMap.put("Accept", "*/*");
		headerMap.put("Accept-Encoding", "gzip");
		headerMap.put("Accept-Language", "zh-CN,zh;q=0.8");
		headerMap.put("Host", getHost(url));
		headerMap.put("Origin", "https://live.kuaishou.com");
		headerMap.put("Referer", "https://live.kuaishou.com/u/" + shortId);// need addavId
		headerMap.put("User-Agent",
				"Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36 SE 2.X MetaSr 1.0");
		headerMap.put("X-Requested-With", "ShockwaveFlash/28.0.0.137");
		return headerMap;
	}

	/**
	 * 该Header配置用于douyu直播 api 信息查询
	 */
	public HashMap<String, String> getDouyuJsonAPIHeaders(long shortId) {
		headerMap = new HashMap<String, String>();
		headerMap.put("Accept", "application/json, text/plain, */*");
		headerMap.put("Accept-Encoding", "gzip");
		headerMap.put("Accept-Language", "zh-CN,zh;q=0.8");
		headerMap.put("content-type", "application/x-www-form-urlencoded");
		headerMap.put("x-requested-with", "XMLHttpRequest");
		headerMap.put("Origin", "https://www.douyu.com");
		headerMap.put("Referer", "https://www.douyu.com/topic/xyb01?rid=" + shortId);// need addavId
		headerMap.put("User-Agent",
				"Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36 SE 2.X MetaSr 1.0");
		headerMap.put("X-Requested-With", "ShockwaveFlash/28.0.0.137");
		return headerMap;
	}

	/**
	 * 该Header配置用于douyu直播flv下载
	 */
	public HashMap<String, String> getDouyuLiveRecordHeaders(String url, long shortId) {
		headerMap = new HashMap<String, String>();
		headerMap.put("Accept", "*/*");
		headerMap.put("Accept-Encoding", "gzip");
		headerMap.put("Accept-Language", "zh-CN,zh;q=0.8");
		headerMap.put("Host", getHost(url));
		headerMap.put("Origin", "https://www.douyu.com");
		headerMap.put("Referer", "https://www.douyu.com/topic/xyb01?rid=" + shortId);// need addavId
		headerMap.put("User-Agent",
				"Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36 SE 2.X MetaSr 1.0");
		headerMap.put("X-Requested-With", "ShockwaveFlash/28.0.0.137");
		return headerMap;
	}

	/**
	 * 该Header配置用于bili直播flv下载
	 */
	public HashMap<String, String> getBiliLiveRecordHeaders(String url, long shortId) {
		headerMap = new HashMap<String, String>();
		headerMap.put("Accept", "*/*");
		headerMap.put("Accept-Encoding", "gzip");
		headerMap.put("Accept-Language", "zh-CN,zh;q=0.8");
		headerMap.put("Connection", "keep-alive");
		headerMap.put("Origin", "https://live.bilibili.com");
		headerMap.put("Host", getHost(url));
		headerMap.put("Referer", "https://live.bilibili.com/" + shortId);
		headerMap.put("User-Agent",
				"Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36 SE 2.X MetaSr 1.0");
		return headerMap;
	}

	/**
	 * 该Header配置用于爱奇艺ts下载
	 */
	public HashMap<String, String> getAiqiyiHeaders(String url, String sid) {
		headerMap = new HashMap<String, String>();
		headerMap.put("Accept", "*/*");
		headerMap.put("Accept-Encoding", "gzip");
		headerMap.put("Accept-Language", "zh-CN,zh;q=0.8");
		headerMap.put("Connection", "keep-alive");
		headerMap.put("Origin", "https://www.iqiyi.com");
		headerMap.put("Host", getHost(url));
		headerMap.put("Referer", String.format("https://www.iqiyi.com/%s.html", sid));
		headerMap.put("User-Agent",
				"Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36 SE 2.X MetaSr 1.0");
		return headerMap;
	}

	/**
	 * 该Header配置用于Zhanqi直播下载
	 */
	public HashMap<String, String> getZhanqiHeaders() {
		headerMap = new HashMap<String, String>();
		headerMap.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
		headerMap.put("Accept-Encoding", "gzip");
		headerMap.put("Accept-Language", "zh-CN,zh;q=0.8");
		headerMap.put("Connection", "keep-alive");
		headerMap.put("Host", "www.zhanqi.tv");
		headerMap.put("Referer", "https://www.zhanqi.tv/");
		headerMap.put("User-Agent",
				"Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36 SE 2.X MetaSr 1.0");
		return headerMap;
	}

	/**
	 * 该Header配置用于Zhanqi token获取
	 */
	public HashMap<String, String> getZhanqiTokenHeaders(String shortId, String boundary, String gid) {
		headerMap = new HashMap<String, String>();
		headerMap.put("Accept", "*/*");
		headerMap.put("Accept-Encoding", "gzip");
		headerMap.put("Accept-Language", "zh-CN,zh;q=0.8");
		headerMap.put("Connection", "keep-alive");
		headerMap.put("Host", "www.zhanqi.tv");
		headerMap.put("Origin", "https://www.zhanqi.tv");
		headerMap.put("Cookie", "gid=" + gid);
		headerMap.put("content-type", "multipart/form-data; boundary=" + boundary);
		headerMap.put("Referer", "https://www.zhanqi.tv/" + shortId);
		headerMap.put("User-Agent",
				"Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36 SE 2.X MetaSr 1.0");
		return headerMap;
	}

	/**
	 * 该Header配置用于通用PC端页面访问
	 */
	public HashMap<String, String> getCommonHeaders(String host) {
		headerMap = new HashMap<String, String>();
		headerMap.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		headerMap.put("Accept-Encoding", "gzip");
		headerMap.put("Accept-Language", "zh-CN,zh;q=0.8");
		headerMap.put("Cache-Control", "max-age=0");
		headerMap.put("Connection", "keep-alive");
		headerMap.put("Host", host);
		headerMap.put("User-Agent",
				"Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36 SE 2.X MetaSr 1.0");
		return headerMap;
	}

	/**
	 * 该Header配置用于kuaishou页面访问
	 */
	public HashMap<String, String> getKuaishouHeaders(String roomId) {
		headerMap = new HashMap<String, String>();
		headerMap.put("Accept", "*/*");
		headerMap.put("Accept-Encoding", "gzip");
		headerMap.put("Accept-Language", "zh-CN,zh;q=0.8");
		headerMap.put("Cache-Control", "max-age=0");
		headerMap.put("Connection", "keep-alive");
		headerMap.put("Origin", "https://www.kuaishou.com");
		headerMap.put("content-type", "application/json");
		headerMap.put("Referer", "https://www.kuaishou.com/profile/" + roomId);
		headerMap.put("User-Agent",
				"Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36 SE 2.X MetaSr 1.0");
		return headerMap;
	}

	/**
	 * 该Header配置用于花椒查询
	 */
	public HashMap<String, String> getHuajiaoHeaders(String shortId) {
		headerMap = new HashMap<String, String>();
		headerMap.put("Accept", "*/*");
		headerMap.put("Accept-Encoding", "gzip");
		headerMap.put("Accept-Language", "zh-CN,zh;q=0.8");
		headerMap.put("Connection", "keep-alive");
		headerMap.put("Host", "live.huajiao.com");
		headerMap.put("Referer", "http://www.huajiao.com/l/" + shortId);
		headerMap.put("User-Agent",
				"Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36 SE 2.X MetaSr 1.0");
		return headerMap;
	}

	/**
	 * 该Header配置用于FLV视频下载
	 */
	public HashMap<String, String> getFLVHeaders() {
		headerMap.put("X-Requested-With", "ShockwaveFlash/28.0.0.137");
		headerMap.put("User-Agent",
				"Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36 SE 2.X MetaSr 1.0");
		return headerMap;
	}
}
