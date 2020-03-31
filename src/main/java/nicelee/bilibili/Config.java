package nicelee.bilibili;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

import nicelee.bilibili.live.check.TagOptions;
import nicelee.bilibili.util.Logger;
import nicelee.bilibili.util.TrustAllCertSSLUtil;

public class Config {

	static boolean autoCheck;
	static boolean splitScriptTagsIfCheck;
	static boolean splitAVHeaderTagsIfCheck;
	static boolean deleteOnchecked;
	static boolean flvCheckWithBuffer;
	static boolean flagZip;
	static boolean flagPlugin;
	static String liver;
	static String shortId;
	static String qn;
	static String[] qnPriority;
	static int maxFailCnt;
	static int failCnt;

	static boolean retryIfLiveOff;
	static int maxRetryIfLiveOff;
	static double retryAfterMinutes;

	static long splitFileSize;
	static long splitRecordPeriod;
	volatile static boolean flagSplit;

	static String fileName = "{name}-{shortId} 的{liver}直播{startTime}-{seq}";
	static String timeFormat = "yyyy-MM-dd HH.mm";
	static String saveFolder;
	static String saveFolderAfterCheck = null;
	
	
	public static void init(String[] args) {
		// 初始化默认值
		autoCheck = true;
		splitScriptTagsIfCheck = false;
		deleteOnchecked = true;
		Logger.debug = false;
		liver = "bili";
		maxFailCnt = 5;
		failCnt = 0;
		splitFileSize = 0;
		splitRecordPeriod = 0;
		flagSplit = false;
		flagZip = false;
		retryIfLiveOff = false;
		maxRetryIfLiveOff = 0;
		retryAfterMinutes = 5;
		flvCheckWithBuffer = true;
		flagPlugin = false;
		
		// 根据参数初始化值
		if (args != null && args.length >= 1) {
			String value = getValue(args[0], "check");
			if ("false".equals(value)) {
				autoCheck = false;
			}
			value = getValue(args[0], "plugin");
			if ("true".equals(value)) {
				flagPlugin = true;
			}
			value = getValue(args[0], "checkWithBuffer");
			if ("false".equals(value)) {
				flvCheckWithBuffer = false;
			}
			value = getValue(args[0], "splitScriptTags");
			if ("true".equals(value)) {
				splitScriptTagsIfCheck = true;
			}
			splitAVHeaderTagsIfCheck = splitScriptTagsIfCheck;
			value = getValue(args[0], "splitAVHeaderTags");
			if ("true".equals(value)) {
				splitAVHeaderTagsIfCheck = true;
			}
			value = getValue(args[0], "maxAudioHeaderSize");
			if (value != null) {
				TagOptions.maxAudioHeaderSize = Integer.parseInt(value);
			}
			value = getValue(args[0], "maxVideoHeaderSize");
			if (value != null) {
				TagOptions.maxVideoHeaderSize = Integer.parseInt(value);
			}
			value = getValue(args[0], "delete");
			if ("false".equals(value)) {
				deleteOnchecked = false;
			}
			value = getValue(args[0], "debug");
			if ("true".equals(value)) {
				Logger.debug = true;
			}
			value = getValue(args[0], "zip");
			if ("true".equals(value)) {
				flagZip = true;
			}
			value = getValue(args[0], "retryIfLiveOff");
			if ("true".equals(value)) {
				retryIfLiveOff = true;
			}
			value = getValue(args[0], "maxRetryIfLiveOff");
			if (value != null && !value.isEmpty()) {
				maxRetryIfLiveOff = Integer.parseInt(value);
			}
			value = getValue(args[0], "retryAfterMinutes");
			if (value != null && !value.isEmpty()) {
				retryAfterMinutes = Double.parseDouble(value);
			}
			value = getValue(args[0], "liver");
			if (value != null && !value.isEmpty()) {
				liver = value;
			}
			value = getValue(args[0], "id");
			if (value != null && !value.isEmpty()) {
				shortId = value;
			}
			value = getValue(args[0], "qn");
			if (value != null) {// && !value.isEmpty()
				qn = value;
			}
			value = getValue(args[0], "qnPri");
			if (value != null) {// && !value.isEmpty()
				try {
					value = URLDecoder.decode(value, "UTF-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				qnPriority = value.split(">");
			}
			value = getValue(args[0], "retry");
			if (value != null && !value.isEmpty()) {
				maxFailCnt = Integer.parseInt(value);
			}
			value = getValue(args[0], "fileSize"); // 单位： MB
			if (value != null && !value.isEmpty()) {
				splitFileSize = Long.parseLong(value) * 1024 * 1024;
			}
			value = getValue(args[0], "filePeriod"); // 单位：min
			if (value != null && !value.isEmpty()) {
				splitRecordPeriod = Long.parseLong(value) * 60 * 1000;
			}
			value = getValue(args[0], "proxy"); // http(s)代理 e.g. 127.0.0.1:8888
			if (value != null && !value.isEmpty()) {
				String argus[] = value.split(":");
				System.setProperty("proxyHost", argus[0]);
				System.setProperty("proxyPort", argus[1]);
			}
			value = getValue(args[0], "socksProxy"); // socks代理 e.g. 127.0.0.1:1080
			if (value != null && !value.isEmpty()) {
				String argus[] = value.split(":");
				System.setProperty("socksProxyHost", argus[0]);
				System.setProperty("socksProxyPort", argus[1]);
			}
			value = getValue(args[0], "trustAllCert"); // 信任所有SSL证书
			if (value != null && !value.isEmpty()) {
				if ("true".equals(value)) {
					try {
						HttpsURLConnection.setDefaultSSLSocketFactory(TrustAllCertSSLUtil.getFactory());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			value = getValue(args[0], "saveFolder");
			if (value != null && !value.isEmpty()) {
				saveFolder = value;
			}
			value = getValue(args[0], "saveFolderAfterCheck");
			if (value != null && !value.isEmpty()) {
				saveFolderAfterCheck = value;
				File f = new File(saveFolderAfterCheck);
				if (!f.exists())
					f.mkdirs();
			}
			value = getValue(args[0], "fileName");
			if (value != null && !value.isEmpty()) {
				fileName = value;
			}
			value = getValue(args[0], "timeFormat");
			if (value != null && !value.isEmpty()) {
				timeFormat = value;
			}
		}
	}
	
	/**
	 * 从参数字符串中取出值 "key1=value1&key2=value2 ..."
	 * 
	 * @param param
	 * @param key
	 * @return
	 */
	public static String getValue(String param, String key) {
		Pattern pattern = Pattern.compile(key + "=([^&]*)");
		Matcher matcher = pattern.matcher(param);
		if (matcher.find()) {
			return matcher.group(1);
		}
		return null;
	}
}
