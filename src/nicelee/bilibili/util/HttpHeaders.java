package nicelee.bilibili.util;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpHeaders {
	HashMap<String, String> headerMap = new HashMap<String, String>();

	final private static Pattern host = Pattern.compile("^https?://([^/]+)/");
	private String getHost(String url) {
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
		headerMap.put("Accept-Encoding", "gzip, deflate, sdch, br");
		headerMap.put("Accept-Language", "zh-CN,zh;q=0.8");
		headerMap.put("Connection", "keep-alive");
		headerMap.put("Host", "api.bilibili.com");
		headerMap.put("Origin", "https://live.bilibili.com");
		headerMap.put("Referer", "https://www.bilibili.com/video/" + shortId);// need addavId
		headerMap.put("User-Agent",
				"Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36 SE 2.X MetaSr 1.0");
		headerMap.put("X-Requested-With", "ShockwaveFlash/28.0.0.137");
		return headerMap;
	}
	/**
	 * 该Header配置用于直播 api 信息查询
	 */
	public HashMap<String, String> getBiliLiveRecordHeaders(String url, long shortId) {
		headerMap = new HashMap<String, String>();
		headerMap.put("Accept", "*/*");
		headerMap.put("Accept-Encoding", "gzip, deflate, sdch, br");
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
	 * 该Header配置用于通用PC端页面访问
	 */
	public HashMap<String, String> getCommonHeaders(String host) {
		headerMap = new HashMap<String, String>();
		headerMap.put("Accept", "text/html,application/xhtml+xm…ml;q=0.9,image/webp,*/*;q=0.8");
		headerMap.put("Accept-Encoding", "gzip, deflate, sdch, br");
		headerMap.put("Accept-Language", "zh-CN,zh;q=0.8");
		headerMap.put("Cache-Control", "max-age=0");
		headerMap.put("Connection", "keep-alive");
		headerMap.put("Host", host);
		headerMap.put("User-Agent",
				"Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36 SE 2.X MetaSr 1.0");
		return headerMap;
	}

}
