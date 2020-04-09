package nicelee.bilibili.threads;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nicelee.bilibili.Config;
import nicelee.bilibili.live.RoomDealer;
import nicelee.bilibili.live.check.FlvCheckerWithBufferEx;
import nicelee.bilibili.live.domain.RoomInfo;
import nicelee.bilibili.plugin.Plugin;
import nicelee.bilibili.util.ZipUtil;

public class ThCheckMedia extends Thread {

	static int count = 0;
	
	RoomDealer roomDealer;
	List<String> fileList;
	Plugin plugin;
	Lock lock;
	
	public ThCheckMedia(RoomDealer roomDealer, List<String> fileList, Lock lock, Plugin plugin) {
		this.roomDealer = roomDealer;
		this.fileList = fileList;
		this.plugin = plugin;
		this.lock = lock;
		this.setName("thread-Check-" + count);
		count++;
	}
	
	@Override
	public void run() {
		
		System.out.println("处理文件中");
		lock.lock();
		if (".flv".equals(roomDealer.getType())) {
			if (Config.autoCheck) {
				try {
					for (String path : fileList) {
						System.out.println("校对时间戳开始...");
						new FlvCheckerWithBufferEx().check(path, Config.deleteOnchecked, Config.splitScriptTagsIfCheck,
								Config.splitAVHeaderTagsIfCheck, Config.saveFolderAfterCheck);
						System.out.println("校对时间戳完毕。");
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (Config.flagZip || Config.flagPlugin) {
				// 获取所有处理过的文件
				List<File> filesAll = new ArrayList<File>(); // 用于存放录制产生的flv文件
				for (String path : fileList) {
					// 如果不校正时间戳, 直接加入列表即可
					if (!Config.autoCheck) {
						filesAll.add(new File(path));
					} else {
						// 如果校正时间戳，一个个文件名进行尝试，直至不存在
						File f = null;
						Pattern fileNamePattern = Pattern.compile("[^/\\\\]+$");
						for (int count = 0;; count++) {
							if (Config.saveFolderAfterCheck != null) {
								Matcher matcher = fileNamePattern.matcher(path);
								matcher.find();
								f = new File(Config.saveFolderAfterCheck,
										matcher.group().replaceFirst(".flv$", "-checked" + count + ".flv"));
							} else {
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
//				System.out.println("正在合并...");
//				M3u8Downloader m3u8 = new M3u8Downloader();
//				m3u8.merge(file, roomDealer.currentIndex, true);
//				// 删除可能存在的part文件
//				String part = String.format("%s-%d%s.part", filename, roomDealer.currentIndex,
//						roomDealer.getType());
//				new File(file.getParent(), part).delete();
//				// 将ts文件移动到上一层文件夹
//				dstFile.renameTo(new File(dstFile.getParentFile().getParentFile(), dstFile.getName()));
//				dstFile.getParentFile().delete();
//				System.out.println("合并结束...");
		}
		
		lock.unlock();
		//System.exit(1);
	}
	
	static void record(RoomDealer roomDealer, RoomInfo roomInfo, String url, List<String> fileList) {
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
}
