package nicelee.test.junit;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import nicelee.bilibili.util.HttpHeaders;
import nicelee.bilibili.util.HttpRequestUtil;
import nicelee.bilibili.util.M3u8Downloader;

public class M3u8DownloadTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	public boolean download(String url, String fname, long totalSize, long partSize, int part, String id) {
		long start = partSize*part;
		long end = start + partSize > totalSize ? totalSize : start + partSize;
		long contentlength = end - start;
		url = url.replaceAll("&?(start|contentlength|end)=[0-9]*", "").replaceFirst("\\?&", "?");
		url += "&start=" + start;
		url += "&end=" + end;
		url += "&contentlength=" + contentlength;
		HttpRequestUtil util = new HttpRequestUtil();
		return util.download(url, fname + "-" + part + ".ts", new HttpHeaders().getAiqiyiHeaders(url, id));
	}
	
	//@Test
	public void testPrintParam() {
		String url = "https://cache.video.iqiyi.com/jp/dash?tvid=2091244600&bid=300&vid=5c573f5b86ba3185c5b04425c117ff78&src=01010031010000000000&vt=0&rs=1&uid=&ori=pcw&ps=0&tm=1559020255021&qd_v=1&k_uid=da4dcbf481041364b726c2fa17dbefb2&pt=0&d=0&s=&lid=&cf=&ct=&authKey=792599c2dc6b48dc083689d14f38999a&k_tag=1&ost=0&ppt=0&dfp=a084d4accac05b47209c824aa7b48ce94880dbca058d8451efc80a281b5154cbf7&locale=zh_cn&prio=%7B%22ff%22%3A%22f4v%22%2C%22code%22%3A2%7D&pck=&k_err_retries=0&k_ft1=141287244169220&k_ft4=4&bop=%7B%22version%22%3A%2210.0%22%2C%22dfp%22%3A%22a084d4accac05b47209c824aa7b48ce94880dbca058d8451efc80a281b5154cbf7%22%7D&callback=Q8714d1c738389d54de9c2d158c021f3e&ut=0&vf=7616f7bbdfca325aaa2076732a5ec2b6";
		String[] params = url.split("\\?")[1].split("&");
		for(int i=0; i<params.length; i++) {
			String[] keyValue = params[i].split("=");
			System.out.print(keyValue[0]);
			System.out.print(":");
			try {
				System.out.print(keyValue[1]);
			}catch (Exception e) {
			}
			System.out.println();
		}
	}
	//@Test
	public void testURL() {
		
		String url = "http://www.xxx.com/s1/s2/?key=value";
		Pattern hostPattern = Pattern.compile("^https?\\://[^/]+");
		Matcher m1 = hostPattern.matcher(url);
		m1.find();
		System.out.println(m1.group());
		
		Pattern rootPattern = Pattern.compile("^https?\\://.*/");
		Matcher m2 = rootPattern.matcher(url);
		m2.find();
		System.out.println(m2.group());
	}
	
	@Test
	public void testDownloadM3u8() {
		String urlM3u8 = "http://bili.meijuzuida.com/20190525/15295_9c6bc6d5/index.m3u8";
		M3u8Downloader m3u8 = new M3u8Downloader();
		try {
			URL url = new URL(urlM3u8);
			m3u8.download(new InputStreamReader(url.openStream()), "一拳超人1", new HttpHeaders().getHeaders(), urlM3u8);
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//@Test
	public void test() {
		M3u8Downloader m3u8 = new M3u8Downloader();
		StringReader reader = new StringReader("#EXTM3U\r\n" + 
				"				#EXT-X-TARGETDURATION:10\r\n" + 
				"				#EXTINF:6,\r\n" + 
				"				http://data.video.iqiyi.com/videos/vts/20190525/1d/53/ce1960f64c5ac42781b3a8f7061c0d71.ts?start=0&end=467744&contentlength=467744&sd=0&qdv=1&qd_uid=1023108576&qd_vip=0&qd_src=01010031010000000000&qd_tm=1559020252874&qd_ip=74e38847&qd_p=74e38847&qd_k=af61d1681054273a8b7df1a55b5f5b6f&sgti=14_da4dcbf481041364b726c2fa17dbefb2_1559020255021&dfp=&qd_sc=d22c130336e99ed7bbff1d692e56e4d5\r\n" + 
				"				#EXTINF:10,\r\n" + 
				"				http://data.video.iqiyi.com/videos/vts/20190525/1d/53/ce1960f64c5ac42781b3a8f7061c0d71.ts?start=467744&end=1102056&contentlength=634312&sd=6250&qdv=1&qd_uid=1023108576&qd_vip=0&qd_src=01010031010000000000&qd_tm=1559020252874&qd_ip=74e38847&qd_p=74e38847&qd_k=af61d1681054273a8b7df1a55b5f5b6f&sgti=14_da4dcbf481041364b726c2fa17dbefb2_1559020255021&dfp=&qd_sc=d22c130336e99ed7bbff1d692e56e4d5\r\n" + 
				"				#EXTINF:10,\r\n" + 
				"				http://data.video.iqiyi.com/videos/vts/20190525/1d/53/ce1960f64c5ac42781b3a8f7061c0d71.ts?start=1102056&end=1995996&contentlength=893940&sd=16250&qdv=1&qd_uid=1023108576&qd_vip=0&qd_src=01010031010000000000&qd_tm=1559020252874&qd_ip=74e38847&qd_p=74e38847&qd_k=af61d1681054273a8b7df1a55b5f5b6f&sgti=14_da4dcbf481041364b726c2fa17dbefb2_1559020255021&dfp=&qd_sc=d22c130336e99ed7bbff1d692e56e4d5\r\n" + 
				"				#EXTINF:9,\r\n" + 
				"				http://data.video.iqiyi.com/videos/vts/20190525/1d/53/ce1960f64c5ac42781b3a8f7061c0d71.ts?start=1995996&end=3116100&contentlength=1120104&sd=26250&qdv=1&qd_uid=1023108576&qd_vip=0&qd_src=01010031010000000000&qd_tm=1559020252874&qd_ip=74e38847&qd_p=74e38847&qd_k=af61d1681054273a8b7df1a55b5f5b6f&sgti=14_da4dcbf481041364b726c2fa17dbefb2_1559020255021&dfp=&qd_sc=d22c130336e99ed7bbff1d692e56e4d5\r\n" + 
				"				#EXTINF:6,\r\n" + 
				"				http://data.video.iqiyi.com/videos/vts/20190525/1d/53/ce1960f64c5ac42781b3a8f7061c0d71.ts?start=3116100&end=3440588&contentlength=324488&sd=35250&qdv=1&qd_uid=1023108576&qd_vip=0&qd_src=01010031010000000000&qd_tm=1559020252874&qd_ip=74e38847&qd_p=74e38847&qd_k=af61d1681054273a8b7df1a55b5f5b6f&sgti=14_da4dcbf481041364b726c2fa17dbefb2_1559020255021&dfp=&qd_sc=d22c130336e99ed7bbff1d692e56e4d5\r\n" + 
				"				#EXTINF:9,\r\n" + 
				"				http://data.video.iqiyi.com/videos/vts/20190525/1d/53/ce1960f64c5ac42781b3a8f7061c0d71.ts?start=3440588&end=3897052&contentlength=456464&sd=41250&qdv=1&qd_uid=1023108576&qd_vip=0&qd_src=01010031010000000000&qd_tm=1559020252874&qd_ip=74e38847&qd_p=74e38847&qd_k=af61d1681054273a8b7df1a55b5f5b6f&sgti=14_da4dcbf481041364b726c2fa17dbefb2_1559020255021&dfp=&qd_sc=d22c130336e99ed7bbff1d692e56e4d5\r\n" + 
				"				#EXTINF:3,\r\n" + 
				"				http://data.video.iqiyi.com/videos/vts/20190525/1d/53/ce1960f64c5ac42781b3a8f7061c0d71.ts?start=3897052&end=4131488&contentlength=234436&sd=50250&qdv=1&qd_uid=1023108576&qd_vip=0&qd_src=01010031010000000000&qd_tm=1559020252874&qd_ip=74e38847&qd_p=74e38847&qd_k=af61d1681054273a8b7df1a55b5f5b6f&sgti=14_da4dcbf481041364b726c2fa17dbefb2_1559020255021&dfp=&qd_sc=d22c130336e99ed7bbff1d692e56e4d5\r\n" + 
				"				#EXTINF:9,\r\n" + 
				"				http://data.video.iqiyi.com/videos/vts/20190525/1d/53/ce1960f64c5ac42781b3a8f7061c0d71.ts?start=4131488&end=4693232&contentlength=561744&sd=53375&qdv=1&qd_uid=1023108576&qd_vip=0&qd_src=01010031010000000000&qd_tm=1559020252874&qd_ip=74e38847&qd_p=74e38847&qd_k=af61d1681054273a8b7df1a55b5f5b6f&sgti=14_da4dcbf481041364b726c2fa17dbefb2_1559020255021&dfp=&qd_sc=d22c130336e99ed7bbff1d692e56e4d5\r\n" + 
				"				#EXTINF:9,\r\n" + 
				"				http://data.video.iqiyi.com/videos/vts/20190525/1d/53/ce1960f64c5ac42781b3a8f7061c0d71.ts?start=4693232&end=5081076&contentlength=387844&sd=62375&qdv=1&qd_uid=1023108576&qd_vip=0&qd_src=01010031010000000000&qd_tm=1559020252874&qd_ip=74e38847&qd_p=74e38847&qd_k=af61d1681054273a8b7df1a55b5f5b6f&sgti=14_da4dcbf481041364b726c2fa17dbefb2_1559020255021&dfp=&qd_sc=d22c130336e99ed7bbff1d692e56e4d5\r\n" + 
				"				#EXTINF:7,\r\n" + 
				"				http://data.video.iqiyi.com/videos/vts/20190525/1d/53/ce1960f64c5ac42781b3a8f7061c0d71.ts?start=5081076&end=5428312&contentlength=347236&sd=71375&qdv=1&qd_uid=1023108576&qd_vip=0&qd_src=01010031010000000000&qd_tm=1559020252874&qd_ip=74e38847&qd_p=74e38847&qd_k=af61d1681054273a8b7df1a55b5f5b6f&sgti=14_da4dcbf481041364b726c2fa17dbefb2_1559020255021&dfp=&qd_sc=d22c130336e99ed7bbff1d692e56e4d5\r\n" + 
				"				#EXTINF:10,\r\n" + 
				"				http://data.video.iqiyi.com/videos/vts/20190525/1d/53/ce1960f64c5ac42781b3a8f7061c0d71.ts?start=5428312&end=5909028&contentlength=480716&sd=78333&qdv=1&qd_uid=1023108576&qd_vip=0&qd_src=01010031010000000000&qd_tm=1559020252874&qd_ip=74e38847&qd_p=74e38847&qd_k=af61d1681054273a8b7df1a55b5f5b6f&sgti=14_da4dcbf481041364b726c2fa17dbefb2_1559020255021&dfp=&qd_sc=d22c130336e99ed7bbff1d692e56e4d5\r\n" + 
				"				#EXTINF:9,\r\n" + 
				"				http://data.video.iqiyi.com/videos/vts/20190525/1d/53/ce1960f64c5ac42781b3a8f7061c0d71.ts?start=5909028&end=6844140&contentlength=935112&sd=88125&qdv=1&qd_uid=1023108576&qd_vip=0&qd_src=01010031010000000000&qd_tm=1559020252874&qd_ip=74e38847&qd_p=74e38847&qd_k=af61d1681054273a8b7df1a55b5f5b6f&sgti=14_da4dcbf481041364b726c2fa17dbefb2_1559020255021&dfp=&qd_sc=d22c130336e99ed7bbff1d692e56e4d5\r\n" + 
				"				#EXTINF:9,\r\n" + 
				"				http://data.video.iqiyi.com/videos/vts/20190525/1d/53/ce1960f64c5ac42781b3a8f7061c0d71.ts?start=6844140&end=7770792&contentlength=926652&sd=97083&qdv=1&qd_uid=1023108576&qd_vip=0&qd_src=01010031010000000000&qd_tm=1559020252874&qd_ip=74e38847&qd_p=74e38847&qd_k=af61d1681054273a8b7df1a55b5f5b6f&sgti=14_da4dcbf481041364b726c2fa17dbefb2_1559020255021&dfp=&qd_sc=d22c130336e99ed7bbff1d692e56e4d5\r\n" + 
				"				#EXTINF:10,\r\n" + 
				"				http://data.video.iqiyi.com/videos/vts/20190525/1d/53/ce1960f64c5ac42781b3a8f7061c0d71.ts?start=7770792&end=8746888&contentlength=976096&sd=106000&qdv=1&qd_uid=1023108576&qd_vip=0&qd_src=01010031010000000000&qd_tm=1559020252874&qd_ip=74e38847&qd_p=74e38847&qd_k=af61d1681054273a8b7df1a55b5f5b6f&sgti=14_da4dcbf481041364b726c2fa17dbefb2_1559020255021&dfp=&qd_sc=d22c130336e99ed7bbff1d692e56e4d5\r\n" + 
				"				#EXTINF:10,\r\n" + 
				"				http://data.video.iqiyi.com/videos/vts/20190525/1d/53/ce1960f64c5ac42781b3a8f7061c0d71.ts?start=8746888&end=10092404&contentlength=1345516&sd=115875&qdv=1&qd_uid=1023108576&qd_vip=0&qd_src=01010031010000000000&qd_tm=1559020252874&qd_ip=74e38847&qd_p=74e38847&qd_k=af61d1681054273a8b7df1a55b5f5b6f&sgti=14_da4dcbf481041364b726c2fa17dbefb2_1559020255021&dfp=&qd_sc=d22c130336e99ed7bbff1d692e56e4d5\r\n" + 
				"				#EXTINF:9,\r\n" + 
				"				http://data.video.iqiyi.com/videos/vts/20190525/1d/53/ce1960f64c5ac42781b3a8f7061c0d71.ts?start=10092404&end=11031276&contentlength=938872&sd=125708&qdv=1&qd_uid=1023108576&qd_vip=0&qd_src=01010031010000000000&qd_tm=1559020252874&qd_ip=74e38847&qd_p=74e38847&qd_k=af61d1681054273a8b7df1a55b5f5b6f&sgti=14_da4dcbf481041364b726c2fa17dbefb2_1559020255021&dfp=&qd_sc=d22c130336e99ed7bbff1d692e56e4d5\r\n" + 
				"				#EXTINF:10,\r\n" + 
				"				http://data.video.iqiyi.com/videos/vts/20190525/1d/53/ce1960f64c5ac42781b3a8f7061c0d71.ts?start=11031276&end=12276400&contentlength=1245124&sd=134458&qdv=1&qd_uid=1023108576&qd_vip=0&qd_src=01010031010000000000&qd_tm=1559020252874&qd_ip=74e38847&qd_p=74e38847&qd_k=af61d1681054273a8b7df1a55b5f5b6f&sgti=14_da4dcbf481041364b726c2fa17dbefb2_1559020255021&dfp=&qd_sc=d22c130336e99ed7bbff1d692e56e4d5\r\n" + 
				"				#EXTINF:4,\r\n" + 
				"				http://data.video.iqiyi.com/videos/vts/20190525/1d/53/ce1960f64c5ac42781b3a8f7061c0d71.ts?start=12276400&end=12711620&contentlength=435220&sd=144458&qdv=1&qd_uid=1023108576&qd_vip=0&qd_src=01010031010000000000&qd_tm=1559020252874&qd_ip=74e38847&qd_p=74e38847&qd_k=af61d1681054273a8b7df1a55b5f5b6f&sgti=14_da4dcbf481041364b726c2fa17dbefb2_1559020255021&dfp=&qd_sc=d22c130336e99ed7bbff1d692e56e4d5\r\n" + 
				"				#EXTINF:9,\r\n" + 
				"				http://data.video.iqiyi.com/videos/vts/20190525/1d/53/ce1960f64c5ac42781b3a8f7061c0d71.ts?start=12711620&end=13391428&contentlength=679808&sd=148791&qdv=1&qd_uid=1023108576&qd_vip=0&qd_src=01010031010000000000&qd_tm=1559020252874&qd_ip=74e38847&qd_p=74e38847&qd_k=af61d1681054273a8b7df1a55b5f5b6f&sgti=14_da4dcbf481041364b726c2fa17dbefb2_1559020255021&dfp=&qd_sc=d22c130336e99ed7bbff1d692e56e4d5\r\n" + 
				"				#EXTINF:8,\r\n" + 
				"				http://data.video.iqiyi.com/videos/vts/20190525/1d/53/ce1960f64c5ac42781b3a8f7061c0d71.ts?start=13391428&end=14437836&contentlength=1046408&sd=157750&qdv=1&qd_uid=1023108576&qd_vip=0&qd_src=01010031010000000000&qd_tm=1559020252874&qd_ip=74e38847&qd_p=74e38847&qd_k=af61d1681054273a8b7df1a55b5f5b6f&sgti=14_da4dcbf481041364b726c2fa17dbefb2_1559020255021&dfp=&qd_sc=d22c130336e99ed7bbff1d692e56e4d5\r\n" + 
				"				#EXTINF:9,\r\n" + 
				"				http://data.video.iqiyi.com/videos/vts/20190525/1d/53/ce1960f64c5ac42781b3a8f7061c0d71.ts?start=14437836&end=14840532&contentlength=402696&sd=165750&qdv=1&qd_uid=1023108576&qd_vip=0&qd_src=01010031010000000000&qd_tm=1559020252874&qd_ip=74e38847&qd_p=74e38847&qd_k=af61d1681054273a8b7df1a55b5f5b6f&sgti=14_da4dcbf481041364b726c2fa17dbefb2_1559020255021&dfp=&qd_sc=d22c130336e99ed7bbff1d692e56e4d5\r\n" + 
				"				#EXTINF:9,\r\n" + 
				"				http://data.video.iqiyi.com/videos/vts/20190525/1d/53/ce1960f64c5ac42781b3a8f7061c0d71.ts?start=14840532&end=15657016&contentlength=816484&sd=174750&qdv=1&qd_uid=1023108576&qd_vip=0&qd_src=01010031010000000000&qd_tm=1559020252874&qd_ip=74e38847&qd_p=74e38847&qd_k=af61d1681054273a8b7df1a55b5f5b6f&sgti=14_da4dcbf481041364b726c2fa17dbefb2_1559020255021&dfp=&qd_sc=d22c130336e99ed7bbff1d692e56e4d5\r\n" + 
				"				#EXTINF:9,\r\n" + 
				"				http://data.video.iqiyi.com/videos/vts/20190525/1d/53/ce1960f64c5ac42781b3a8f7061c0d71.ts?start=15657016&end=16594196&contentlength=937180&sd=183458&qdv=1&qd_uid=1023108576&qd_vip=0&qd_src=01010031010000000000&qd_tm=1559020252874&qd_ip=74e38847&qd_p=74e38847&qd_k=af61d1681054273a8b7df1a55b5f5b6f&sgti=14_da4dcbf481041364b726c2fa17dbefb2_1559020255021&dfp=&qd_sc=d22c130336e99ed7bbff1d692e56e4d5\r\n" + 
				"				#EXTINF:8,\r\n" + 
				"				http://data.video.iqiyi.com/videos/vts/20190525/1d/53/ce1960f64c5ac42781b3a8f7061c0d71.ts?start=16594196&end=17785552&contentlength=1191356&sd=192500&qdv=1&qd_uid=1023108576&qd_vip=0&qd_src=01010031010000000000&qd_tm=1559020252874&qd_ip=74e38847&qd_p=74e38847&qd_k=af61d1681054273a8b7df1a55b5f5b6f&sgti=14_da4dcbf481041364b726c2fa17dbefb2_1559020255021&dfp=&qd_sc=d22c130336e99ed7bbff1d692e56e4d5\r\n" + 
				"				#EXTINF:9,\r\n" + 
				"				http://data.video.iqiyi.com/videos/vts/20190525/1d/53/ce1960f64c5ac42781b3a8f7061c0d71.ts?start=17785552&end=18637944&contentlength=852392&sd=200750&qdv=1&qd_uid=1023108576&qd_vip=0&qd_src=01010031010000000000&qd_tm=1559020252874&qd_ip=74e38847&qd_p=74e38847&qd_k=af61d1681054273a8b7df1a55b5f5b6f&sgti=14_da4dcbf481041364b726c2fa17dbefb2_1559020255021&dfp=&qd_sc=d22c130336e99ed7bbff1d692e56e4d5\r\n" + 
				"				#EXTINF:9,\r\n" + 
				"				http://data.video.iqiyi.com/videos/vts/20190525/1d/53/ce1960f64c5ac42781b3a8f7061c0d71.ts?start=18637944&end=19588848&contentlength=950904&sd=209333&qdv=1&qd_uid=1023108576&qd_vip=0&qd_src=01010031010000000000&qd_tm=1559020252874&qd_ip=74e38847&qd_p=74e38847&qd_k=af61d1681054273a8b7df1a55b5f5b6f&sgti=14_da4dcbf481041364b726c2fa17dbefb2_1559020255021&dfp=&qd_sc=d22c130336e99ed7bbff1d692e56e4d5\r\n" + 
				"				#EXTINF:7,\r\n" + 
				"				http://data.video.iqiyi.com/videos/vts/20190525/1d/53/ce1960f64c5ac42781b3a8f7061c0d71.ts?start=19588848&end=20119008&contentlength=530160&sd=218541&qdv=1&qd_uid=1023108576&qd_vip=0&qd_src=01010031010000000000&qd_tm=1559020252874&qd_ip=74e38847&qd_p=74e38847&qd_k=af61d1681054273a8b7df1a55b5f5b6f&sgti=14_da4dcbf481041364b726c2fa17dbefb2_1559020255021&dfp=&qd_sc=d22c130336e99ed7bbff1d692e56e4d5\r\n" + 
				"				#EXTINF:6,\r\n" + 
				"				http://data.video.iqiyi.com/videos/vts/20190525/1d/53/ce1960f64c5ac42781b3a8f7061c0d71.ts?start=20119008&end=20528284&contentlength=409276&sd=226000&qdv=1&qd_uid=1023108576&qd_vip=0&qd_src=01010031010000000000&qd_tm=1559020252874&qd_ip=74e38847&qd_p=74e38847&qd_k=af61d1681054273a8b7df1a55b5f5b6f&sgti=14_da4dcbf481041364b726c2fa17dbefb2_1559020255021&dfp=&qd_sc=d22c130336e99ed7bbff1d692e56e4d5\r\n" + 
				"				#EXTINF:9,\r\n" + 
				"				http://data.video.iqiyi.com/videos/vts/20190525/1d/53/ce1960f64c5ac42781b3a8f7061c0d71.ts?start=20528284&end=21035132&contentlength=506848&sd=231500&qdv=1&qd_uid=1023108576&qd_vip=0&qd_src=01010031010000000000&qd_tm=1559020252874&qd_ip=74e38847&qd_p=74e38847&qd_k=af61d1681054273a8b7df1a55b5f5b6f&sgti=14_da4dcbf481041364b726c2fa17dbefb2_1559020255021&dfp=&qd_sc=d22c130336e99ed7bbff1d692e56e4d5\r\n" + 
				"				#EXTINF:7,\r\n" + 
				"				http://data.video.iqiyi.com/videos/vts/20190525/1d/53/ce1960f64c5ac42781b3a8f7061c0d71.ts?start=21035132&end=21456440&contentlength=421308&sd=240083&qdv=1&qd_uid=1023108576&qd_vip=0&qd_src=01010031010000000000&qd_tm=1559020252874&qd_ip=74e38847&qd_p=74e38847&qd_k=af61d1681054273a8b7df1a55b5f5b6f&sgti=14_da4dcbf481041364b726c2fa17dbefb2_1559020255021&dfp=&qd_sc=d22c130336e99ed7bbff1d692e56e4d5\r\n" + 
				"				#EXTINF:8,\r\n" + 
				"				http://data.video.iqiyi.com/videos/vts/20190525/1d/53/ce1960f64c5ac42781b3a8f7061c0d71.ts?start=21456440&end=21898616&contentlength=442176&sd=247958&qdv=1&qd_uid=1023108576&qd_vip=0&qd_src=01010031010000000000&qd_tm=1559020252874&qd_ip=74e38847&qd_p=74e38847&qd_k=af61d1681054273a8b7df1a55b5f5b6f&sgti=14_da4dcbf481041364b726c2fa17dbefb2_1559020255021&dfp=&qd_sc=d22c130336e99ed7bbff1d692e56e4d5\r\n" + 
				"				#EXTINF:9,\r\n" + 
				"				http://data.video.iqiyi.com/videos/vts/20190525/1d/53/ce1960f64c5ac42781b3a8f7061c0d71.ts?start=21898616&end=22518828&contentlength=620212&sd=256125&qdv=1&qd_uid=1023108576&qd_vip=0&qd_src=01010031010000000000&qd_tm=1559020252874&qd_ip=74e38847&qd_p=74e38847&qd_k=af61d1681054273a8b7df1a55b5f5b6f&sgti=14_da4dcbf481041364b726c2fa17dbefb2_1559020255021&dfp=&qd_sc=d22c130336e99ed7bbff1d692e56e4d5\r\n" + 
				"				#EXTINF:9,\r\n" + 
				"				http://data.video.iqiyi.com/videos/vts/20190525/1d/53/ce1960f64c5ac42781b3a8f7061c0d71.ts?start=22518828&end=23089032&contentlength=570204&sd=265541&qdv=1&qd_uid=1023108576&qd_vip=0&qd_src=01010031010000000000&qd_tm=1559020252874&qd_ip=74e38847&qd_p=74e38847&qd_k=af61d1681054273a8b7df1a55b5f5b6f&sgti=14_da4dcbf481041364b726c2fa17dbefb2_1559020255021&dfp=&qd_sc=d22c130336e99ed7bbff1d692e56e4d5\r\n" + 
				"				#EXTINF:9,\r\n" + 
				"				http://data.video.iqiyi.com/videos/vts/20190525/1d/53/ce1960f64c5ac42781b3a8f7061c0d71.ts?start=23089032&end=23694392&contentlength=605360&sd=274416&qdv=1&qd_uid=1023108576&qd_vip=0&qd_src=01010031010000000000&qd_tm=1559020252874&qd_ip=74e38847&qd_p=74e38847&qd_k=af61d1681054273a8b7df1a55b5f5b6f&sgti=14_da4dcbf481041364b726c2fa17dbefb2_1559020255021&dfp=&qd_sc=d22c130336e99ed7bbff1d692e56e4d5\r\n" + 
				"				#EXTINF:4,\r\n" + 
				"				http://data.video.iqiyi.com/videos/vts/20190525/1d/53/ce1960f64c5ac42781b3a8f7061c0d71.ts?start=23694392&end=23966616&contentlength=272224&sd=283416&qdv=1&qd_uid=1023108576&qd_vip=0&qd_src=01010031010000000000&qd_tm=1559020252874&qd_ip=74e38847&qd_p=74e38847&qd_k=af61d1681054273a8b7df1a55b5f5b6f&sgti=14_da4dcbf481041364b726c2fa17dbefb2_1559020255021&dfp=&qd_sc=d22c130336e99ed7bbff1d692e56e4d5\r\n" + 
				"				#EXTINF:9,\r\n" + 
				"				http://data.video.iqiyi.com/videos/vts/20190525/1d/53/ce1960f64c5ac42781b3a8f7061c0d71.ts?start=23966616&end=24578744&contentlength=612128&sd=287208&qdv=1&qd_uid=1023108576&qd_vip=0&qd_src=01010031010000000000&qd_tm=1559020252874&qd_ip=74e38847&qd_p=74e38847&qd_k=af61d1681054273a8b7df1a55b5f5b6f&sgti=14_da4dcbf481041364b726c2fa17dbefb2_1559020255021&dfp=&qd_sc=d22c130336e99ed7bbff1d692e56e4d5\r\n" + 
				"				#EXTINF:9,\r\n" + 
				"				http://data.video.iqiyi.com/videos/vts/20190525/1d/53/ce1960f64c5ac42781b3a8f7061c0d71.ts?start=24578744&end=25046676&contentlength=467932&sd=296208&qdv=1&qd_uid=1023108576&qd_vip=0&qd_src=01010031010000000000&qd_tm=1559020252874&qd_ip=74e38847&qd_p=74e38847&qd_k=af61d1681054273a8b7df1a55b5f5b6f&sgti=14_da4dcbf481041364b726c2fa17dbefb2_1559020255021&dfp=&qd_sc=d22c130336e99ed7bbff1d692e56e4d5\r\n" + 
				"				#EXTINF:6,\r\n" + 
				"				http://data.video.iqiyi.com/videos/vts/20190525/1d/53/ce1960f64c5ac42781b3a8f7061c0d71.ts?start=25046676&end=25366088&contentlength=319412&sd=305208&qdv=1&qd_uid=1023108576&qd_vip=0&qd_src=01010031010000000000&qd_tm=1559020252874&qd_ip=74e38847&qd_p=74e38847&qd_k=af61d1681054273a8b7df1a55b5f5b6f&sgti=14_da4dcbf481041364b726c2fa17dbefb2_1559020255021&dfp=&qd_sc=d22c130336e99ed7bbff1d692e56e4d5\r\n" + 
				"				#EXTINF:5,\r\n" + 
				"				http://data.video.iqiyi.com/videos/vts/20190525/1d/53/ce1960f64c5ac42781b3a8f7061c0d71.ts?start=25366088&end=25676288&contentlength=310200&sd=311625&qdv=1&qd_uid=1023108576&qd_vip=0&qd_src=01010031010000000000&qd_tm=1559020252874&qd_ip=74e38847&qd_p=74e38847&qd_k=af61d1681054273a8b7df1a55b5f5b6f&sgti=14_da4dcbf481041364b726c2fa17dbefb2_1559020255021&dfp=&qd_sc=d22c130336e99ed7bbff1d692e56e4d5\r\n" + 
				"				#EXTINF:9,\r\n" + 
				"				http://data.video.iqiyi.com/videos/vts/20190525/1d/53/ce1960f64c5ac42781b3a8f7061c0d71.ts?start=25676288&end=26243108&contentlength=566820&sd=316458&qdv=1&qd_uid=1023108576&qd_vip=0&qd_src=01010031010000000000&qd_tm=1559020252874&qd_ip=74e38847&qd_p=74e38847&qd_k=af61d1681054273a8b7df1a55b5f5b6f&sgti=14_da4dcbf481041364b726c2fa17dbefb2_1559020255021&dfp=&qd_sc=d22c130336e99ed7bbff1d692e56e4d5\r\n" + 
				"				#EXTINF:9,\r\n" + 
				"				http://data.video.iqiyi.com/videos/vts/20190525/1d/53/ce1960f64c5ac42781b3a8f7061c0d71.ts?start=26243108&end=26853544&contentlength=610436&sd=325458&qdv=1&qd_uid=1023108576&qd_vip=0&qd_src=01010031010000000000&qd_tm=1559020252874&qd_ip=74e38847&qd_p=74e38847&qd_k=af61d1681054273a8b7df1a55b5f5b6f&sgti=14_da4dcbf481041364b726c2fa17dbefb2_1559020255021&dfp=&qd_sc=d22c130336e99ed7bbff1d692e56e4d5\r\n" + 
				"				#EXTINF:9,\r\n" + 
				"				http://data.video.iqiyi.com/videos/vts/20190525/1d/53/ce1960f64c5ac42781b3a8f7061c0d71.ts?start=26853544&end=27147952&contentlength=294408&sd=334458&qdv=1&qd_uid=1023108576&qd_vip=0&qd_src=01010031010000000000&qd_tm=1559020252874&qd_ip=74e38847&qd_p=74e38847&qd_k=af61d1681054273a8b7df1a55b5f5b6f&sgti=14_da4dcbf481041364b726c2fa17dbefb2_1559020255021&dfp=&qd_sc=d22c130336e99ed7bbff1d692e56e4d5\r\n" + 
				"				#EXTINF:9,\r\n" + 
				"				http://data.video.iqiyi.com/videos/vts/20190525/1d/53/ce1960f64c5ac42781b3a8f7061c0d71.ts?start=27147952&end=27394984&contentlength=247032&sd=343458&qdv=1&qd_uid=1023108576&qd_vip=0&qd_src=01010031010000000000&qd_tm=1559020252874&qd_ip=74e38847&qd_p=74e38847&qd_k=af61d1681054273a8b7df1a55b5f5b6f&sgti=14_da4dcbf481041364b726c2fa17dbefb2_1559020255021&dfp=&qd_sc=d22c130336e99ed7bbff1d692e56e4d5\r\n" + 
				"				#EXTINF:9,\r\n" + 
				"				http://data.video.iqiyi.com/videos/vts/20190525/1d/53/ce1960f64c5ac42781b3a8f7061c0d71.ts?start=27394984&end=27848816&contentlength=453832&sd=352458&qdv=1&qd_uid=1023108576&qd_vip=0&qd_src=01010031010000000000&qd_tm=1559020252874&qd_ip=74e38847&qd_p=74e38847&qd_k=af61d1681054273a8b7df1a55b5f5b6f&sgti=14_da4dcbf481041364b726c2fa17dbefb2_1559020255021&dfp=&qd_sc=d22c130336e99ed7bbff1d692e56e4d5\r\n" + 
				"				#EXT-X-ENDLIST");
		m3u8.download(reader, "test", new HttpHeaders().getHeaders());
	}
	

}
