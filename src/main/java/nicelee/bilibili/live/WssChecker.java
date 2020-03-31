package nicelee.bilibili.live;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import nicelee.bilibili.util.Logger;

public class WssChecker {

	public static void main(String[] args) throws IOException {
		File file = new File("D:\\Workspace\\javaweb-springboot\\BilibiliLiveRecord\\download\\test1.flv");
		RandomAccessFile raf = new RandomAccessFile(file, "r");
		
		File fileNew = new File(file.getParentFile(), file.getName() + ".flv");
		fileNew.delete();
		RandomAccessFile rafNew = new RandomAccessFile(fileNew, "rw");
		
		int lenRead = raf.read(buffer);
		int pDuration = checkBufferForFlvHeader();
		boolean findDuration = false;
		while (lenRead > -1) {
			// 如果上一次buffer已经找到了头
			if(findDuration) {
				rafNew.write(buffer);
			}else {
				// 当前buffer找到了flv头部
				if (pDuration != -1) {
					// 标志位赋值
					findDuration = true;
					// 新文件开始写头部
					rafNew.write(flvHeader);
					// 将当前buffer剩余的写入新文件
					rafNew.write(buffer, pDuration, lenRead - pDuration);
				}else {
					// 继续寻找flv头部
					pDuration = checkBufferForFlvHeader();
				}
			}
			lenRead = raf.read(buffer);
		}
		rafNew.close();
		raf.close();
		Logger.println(pDuration);

	}

	static byte[] buffer = new byte[1024 * 1024];
	static byte[] flvHeader = { 0X46, 0X4C, 0x56, 0x01, 0x05, 0x00, 0x00, 0x00, 0x09 }; // 包含的音视频flv
	static int pFlvHeader = 0;

	/**
	 * 检查buffer 是否包含flv头部
	 * 
	 * @return flv头部末尾在 buffer中的位置
	 */
	static int checkBufferForFlvHeader() {
		for (int i = 0; i < buffer.length; i++) {
			if (buffer[i] == flvHeader[pFlvHeader]) {
				pFlvHeader++;
				if (pFlvHeader == flvHeader.length) {
					pFlvHeader = 0;
					return i;
				}
			}
		}
		return -1;
	}
}
