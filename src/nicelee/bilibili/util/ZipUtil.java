package nicelee.bilibili.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipUtil {

	public static boolean zipFiles(List<File> fileListToZip, String dstPath) {
		System.out.println("压缩中...");
		byte[] buffer = new byte[1024 * 1024];
		ZipEntry zipEntry = null;
		int readLength = 0;// 每次读出来的长度
		try {
			ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(dstPath));
			for (File file : fileListToZip) {
				zipEntry = new ZipEntry(file.getName());
				zipEntry.setSize(file.length());
				zipEntry.setTime(file.lastModified());
				zipOutputStream.putNextEntry(zipEntry);

				InputStream is = new FileInputStream(file);
				while ((readLength = is.read(buffer)) != -1) {
					zipOutputStream.write(buffer, 0, readLength);
				}
				is.close();
			}
			zipOutputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("压缩失败");
			return false;
		}
		System.out.println("压缩成功");
		for (File file : fileListToZip) {
			file.delete();
		}
		return true;
	}

	public static boolean zipFile(File fileToZip, String dstPath) {
		System.out.println("压缩中...");
		byte[] buffer = new byte[1024 * 1024];
		ZipEntry zipEntry = null;
		int readLength = 0;// 每次读出来的长度
		try {
			ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(dstPath));
			zipEntry = new ZipEntry(fileToZip.getName());
			zipEntry.setSize(fileToZip.length());
			zipEntry.setTime(fileToZip.lastModified());
			zipOutputStream.putNextEntry(zipEntry);

			InputStream is = new FileInputStream(fileToZip);
			while ((readLength = is.read(buffer)) != -1) {
				zipOutputStream.write(buffer, 0, readLength);
			}
			is.close();
			zipOutputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("压缩失败");
			return false;
		}
		System.out.println("压缩成功");
		fileToZip.delete();
		return true;
	}
}
