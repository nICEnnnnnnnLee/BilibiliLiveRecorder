package nicelee.bilibili;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nicelee.bilibili.enums.StatusEnum;
import nicelee.bilibili.live.RoomDealer;
import nicelee.bilibili.live.check.FlvCheckerWithBufferEx;
import nicelee.bilibili.live.domain.RoomInfo;
import nicelee.bilibili.plugin.Plugin;
import nicelee.bilibili.util.Logger;
import nicelee.bilibili.util.ZipUtil;

public class Main {

	final static String version = "v2.7.0";

	/**
	 * 程序入口
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
//		 args = new String[]{"debug=false&liver=bili&id=221602&qn=10000&delete=false&check=false"};  			// 清晰度全部可选，可不需要cookie
//		args = new String[] {
//				"plugin=true&debug=false&check=true&retryAfterMinutes=0.5&retryIfLiveOff=true&liver=douyu&qnPri=高清>蓝光4M>超清>蓝光>流畅&qn=-1&id=198859&saveFolder=D:\\Workspace&fileName=测试{liver}-{name}-{startTime}-{endTime}-{seq}&saveFolderAfterCheck=D:\\Workspace\\live-test" }; // 清晰度全部可选，但部分高清需要cookie
//		args = new String[] { "debug=true&check=true&liver=kuaishou&id=mianf666&qn=0&delete=false&fileName=测试{liver}-{name}-{startTime}-{endTime}-{seq}&timeFormat=yyyyMMddHHmm" }; // 清晰度全部可选，可不需要cookie
//		args = new String[]{"debug=true&check=false&liver=huya&id=660137"}; 				// 清晰度全部可选，可不需要cookie 
//		args = new String[]{"debug=true&check=true&liver=yy&id=28581146&qn=1"}; 		// 只支持默认清晰度 54880976
//		args = new String[] { "debug=true&check=true&liver=zhanqi&id=90god" }; 			// 清晰度全部可选，可不需要cookie 90god huashan ydjs
//		args = new String[] { "debug=true&check=true&liver=huajiao&id=278581432&qn=1" }; // 只支持默认清晰度(似乎只有一种清晰度)

		final Plugin plugin = new Plugin();
		if (args != null && args[0].contains("plugin=true")) {
			try {
				plugin.compile();
				plugin.runBeforeInit(args);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		// 根据参数初始化配置
		Config.init(args);
		if (Config.flagPlugin) {
			try {
				plugin.runAfterInit();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		System.out.println(Config.liver + " 直播录制 version " + version);

		// 如果没有传入房间号，等待输入房间号
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		if (Config.shortId == null) {
			System.out.println("请输入房间号(直播网址是https://xxx.com/xxx，那么房间号就是xxx)");
			String line = reader.readLine();
			Config.shortId = line;
		}

		// 加载cookies
		String cookie = null;
		try {
			BufferedReader buReader = new BufferedReader(new FileReader(Config.liver + "-cookie.txt"));
			cookie = buReader.readLine();
			buReader.close();
			// Logger.println(cookie);
		} catch (Exception e) {
			// e.printStackTrace();
		}
		final String fcookie = cookie;

		RoomDealer roomDealer = getRoomDealer(Config.liver);
		if (cookie != null) {
			roomDealer.setCookie(cookie);
		}
		// 获取房间信息
		RoomInfo rroomInfo = roomDealer.getRoomInfo(Config.shortId);

		if (rroomInfo == null) {
			System.err.println("解析失败！！");
			System.exit(-2);
		}
		// 查看是否在线
		if (rroomInfo.getLiveStatus() != 1) {
			System.out.println("当前没有在直播");
			int retryCntLiveOff = 0;
			if (Config.retryIfLiveOff) {
				while (rroomInfo.getLiveStatus() != 1
						&& (Config.maxRetryIfLiveOff == 0 || Config.maxRetryIfLiveOff > retryCntLiveOff)) {
					retryCntLiveOff++;
					try {
						System.out.println(Config.retryAfterMinutes + "分钟左右后重试");
						Thread.sleep((long) (Config.retryAfterMinutes * 60000));
					} catch (InterruptedException e) {
					}
					rroomInfo = roomDealer.getRoomInfo(Config.shortId);
					if (rroomInfo == null) {
						System.err.println("解析失败！！");
						System.exit(-2);
					}
				}
			} else {
				System.exit(3);
			}
		}
		RoomInfo roomInfo = rroomInfo;
		// 清晰度获取
		// 先使用预设的优先级获取
		if (Config.qnPriority != null) {
			String qnDescs[] = roomInfo.getAcceptQualityDesc();
			boolean findQn = false;
			for (int i = 0; i < Config.qnPriority.length; i++) {
				// 遍历qnDescs, 如果符合要求，则设置清晰度
				for (int j = 0; j < qnDescs.length; j++) {
					if (qnDescs[j].equals(Config.qnPriority[i])) {
						Config.qn = roomInfo.getAcceptQuality()[j];
						findQn = true;
						break;
					}
				}
				if (findQn)
					break;
			}
		}
		// qn = -1, 使用最高画质
		if ("-1".equals(Config.qn)) {
			Config.qn = roomInfo.getAcceptQuality()[0];
		}
		// 没有获取到清晰度，则提示输入
		if (Config.qn == null) {
			// 输入清晰度后，获得直播视频地址
			System.out.println("请输入清晰度代号(:之前的内容，不含空格)");
			Config.qn = reader.readLine();
		}
		// 检查清晰度的合法性
		boolean qnIsValid = false;
		String validQN[] = roomInfo.getAcceptQuality();
		for (int i = 0; i < validQN.length; i++) {
			if (validQN[i].equals(Config.qn)) {
				qnIsValid = true;
				break;
			}
		}
		if (!qnIsValid) {
			System.err.println("输入的qn值不在当前可获取清晰度列表中");
			System.exit(-1);
		}
		// String url = roomDealer.getLiveUrl(roomInfo.getRoomId(),
		// roomInfo.getAcceptQuality()[0]);
		String url = roomDealer.getLiveUrl((roomInfo.getRoomId()), "" + Config.qn, roomInfo.getRemark(), cookie);
		Logger.println(url);

		// 开始录制
		new Thread(new Runnable() {
			@Override
			public void run() {
				System.out.println("开始录制，输入stop停止录制");
				List<String> fileList = new ArrayList<String>(); // 用于存放
				// 在开启录制之前，添加对退出信号的捕捉处理
				final Lock lock = new ReentrantLock();
				lock.lock();
				Runtime.getRuntime().addShutdownHook(new SignalHandler(lock, roomDealer));
				record(roomDealer, roomInfo, url, fileList);

				while (true) {
					if (roomDealer.util.getStatus() == StatusEnum.STOP && Config.flagSplit) {
						// 判断当前状态 如果文件超过配置大小，那么重命名后重新录制
						// 重置状态
						roomDealer.util.init();
						Config.flagSplit = false;
						Config.failCnt = 0;
						System.out.println("文件大小或录制时长超过阈值，重新尝试录制");
						String url = roomDealer.getLiveUrl((roomInfo.getRoomId()), "" + Config.qn, roomInfo.getRemark(),
								fcookie);
						Logger.println(url);
						record(roomDealer, roomInfo, url, fileList);
					} else if (roomDealer.util.getStatus() == StatusEnum.FAIL && Config.maxFailCnt >= Config.failCnt) {
						// 判断当前状态 如果异常连接导致失败，那么重命名后重新录制
						Config.failCnt++;
						System.out.println("连接异常，重新尝试录制");
						String url = roomDealer.getLiveUrl((roomInfo.getRoomId()), "" + Config.qn, roomInfo.getRemark(),
								fcookie);
						Logger.println(url);
						record(roomDealer, roomInfo, url, fileList);
					} else {
						break;
					}
				}

				System.out.println("下载停止");
				if (".flv".equals(roomDealer.getType())) {
					if (Config.autoCheck) {
						try {
							for (String path : fileList) {
								System.out.println("校对时间戳开始...");
								new FlvCheckerWithBufferEx().check(path, Config.deleteOnchecked,
										Config.splitScriptTagsIfCheck, 
										Config.splitAVHeaderTagsIfCheck,
										Config.saveFolderAfterCheck);
								System.out.println("校对时间戳完毕。");
							}
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					if (Config.flagZip || Config.flagPlugin) {
						// 获取所有处理过的文件
						List<File> filesAll = new ArrayList<File>(); // 用于存放
						for (String path : fileList) {
							// 如果不校正时间戳, 直接加入列表即可
							if (!Config.autoCheck) {
								filesAll.add(new File(path));
							} else {
								// 如果校正时间戳，一个个文件名进行尝试，直至不存在
								File f = null;
								Pattern fileNamePattern = Pattern.compile("[^/\\\\]+$");
								for (int count = 0;; count++) {
									if(Config.saveFolderAfterCheck != null) {
										Matcher matcher = fileNamePattern.matcher(path);
										matcher.find();
										f = new File(Config.saveFolderAfterCheck, matcher.group().replaceFirst(".flv$", "-checked" + count + ".flv"));
									}else {
										String path_i = path.replaceFirst(".flv$", "-checked" + count + ".flv");
										f = new File(path_i);
									}
									if (f.exists())
										filesAll.add(f);
									else
										break;
								}
							}
						}
						// 启用插件
						if (Config.flagPlugin) {
							try {
								plugin.runAfterComplete(filesAll);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						// 压缩文件
						if (Config.flagZip) {
							ZipUtil.zipFiles(filesAll, fileList.get(0) + ".zip");
						}
					}
				} else if (".ts".equals(roomDealer.getType())) {
//					System.out.println("正在合并...");
//					M3u8Downloader m3u8 = new M3u8Downloader();
//					m3u8.merge(file, roomDealer.currentIndex, true);
//					// 删除可能存在的part文件
//					String part = String.format("%s-%d%s.part", filename, roomDealer.currentIndex,
//							roomDealer.getType());
//					new File(file.getParent(), part).delete();
//					// 将ts文件移动到上一层文件夹
//					dstFile.renameTo(new File(dstFile.getParentFile().getParentFile(), dstFile.getName()));
//					dstFile.getParentFile().delete();
//					System.out.println("合并结束...");
				}

				lock.unlock();
				System.exit(1);
			}
		}, "thread-record").start();

		// 输出进度，超过指定大小后重新开始一次
		new Thread(new Runnable() {
			long beginTime = System.currentTimeMillis();
			long fileBeginTime = beginTime;

			@Override
			public void run() {
				while (true) {
					try {
						Thread.sleep(10000); // 每10s汇报一次情况
					} catch (InterruptedException e) {
					}
					// 查看当前文件大小, 如果超过阈值，那么重新开始新的录制
					if (Config.splitFileSize != 0 && roomDealer.util.getDownloadedFileSize() >= Config.splitFileSize) {
						fileBeginTime = System.currentTimeMillis();
						Config.flagSplit = true;
						roomDealer.stopRecord();
					}
					// 查看当前录制时长, 如果超过阈值，那么重新开始新的录制
					if (Config.splitRecordPeriod != 0
							&& System.currentTimeMillis() - fileBeginTime >= Config.splitRecordPeriod) {
						fileBeginTime = System.currentTimeMillis();
						Config.flagSplit = true;
						roomDealer.stopRecord();
					}
					if (".flv".equals(roomDealer.getType())) {
						if (roomDealer.util.getStatus() == StatusEnum.DOWNLOADING) {
							int period = (int) ((System.currentTimeMillis() - beginTime) / 1000);
							int hour = period / 3600;
							int minute = period / 60 - hour * 60;
							int second = period - minute * 60 - hour * 3600;
							if (hour == 0) {
								System.out.printf("已经录制了%dm%ds, ", minute, second);
							} else {
								System.out.printf("已经录制了%dh%dm%ds, ", hour, minute, second);
							}
							System.out.println(
									"当前进度： " + RoomDealer.transToSizeStr(roomDealer.util.getDownloadedFileSize()));
						} else {
							System.out.println("正在处理，请稍等 ");
						}
					} else {
						int period = (int) ((System.currentTimeMillis() - beginTime) / 1000);
						int hour = period / 3600;
						int minute = period / 60 - hour * 60;
						int second = period - minute * 60 - hour * 3600;
						if (hour == 0) {
							System.out.printf("已经录制了%dm%ds, ", minute, second);
						} else {
							System.out.printf("已经录制了%dh%dm%ds, ", hour, minute, second);
						}
						System.out.println("当前进度： " + RoomDealer
								.transToSizeStr(roomDealer.util.getTotalFileSize() * roomDealer.currentIndex));
					}

				}
			}
		}, "thread-monitoring").start();
		String line;
		while ((line = reader.readLine()) != null) {
			if (line.startsWith("stop") || line.startsWith("q")) {
				roomDealer.stopRecord();
				reader.close();
				break;
			} else {
				System.out.println("输入stop 或 q 停止录制");
			}
		}
	}

	private static void record(RoomDealer roomDealer, RoomInfo roomInfo, String url, List<String> fileList) {
		SimpleDateFormat sdf = new SimpleDateFormat(Config.timeFormat);
		// "{name}-{shortId} 的{liver}直播{startTime}-{seq}";
		String realName = Config.fileName.replace("{name}", roomInfo.getUserName())
				.replace("{shortId}", roomInfo.getShortId()).replace("{roomId}", roomInfo.getRoomId())
				.replace("{liver}", Config.liver).replace("{startTime}", sdf.format(new Date()))
				.replace("{seq}", "" + fileList.size()).replaceAll("[\\\\|\\/|:\\*\\?|<|>|\\||\\\"$]", ".");
		// 如果saveFolder不为空
		if (Config.saveFolder != null) {
			roomDealer.util.setSavePath(Config.saveFolder);
		}
		roomDealer.startRecord(url, realName, roomInfo.getShortId());// 此处一直堵塞， 直至停止
		File file = roomDealer.util.getFileDownload();

		File partFile = new File(file.getParent(), realName + roomDealer.getType() + ".part");
		File completeFile = new File(file.getParent(), realName + roomDealer.getType());
		realName = realName.replace("{endTime}", sdf.format(new Date()));
		File dstFile = new File(file.getParent(), realName + roomDealer.getType());

		if (partFile.exists())
			partFile.renameTo(dstFile);
		else
			completeFile.renameTo(dstFile);

		// 加入已下载列表
		fileList.add(dstFile.getAbsolutePath());
	}

	/**
	 * 获取正确的视频录制器
	 * 
	 * @param liver
	 * @return
	 */
	private static RoomDealer getRoomDealer(String liver) {
		return RoomDealer.createRoomDealer(liver);
	}

}
