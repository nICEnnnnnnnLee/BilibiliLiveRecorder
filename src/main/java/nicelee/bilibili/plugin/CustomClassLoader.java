package nicelee.bilibili.plugin;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;

public class CustomClassLoader extends ClassLoader {

	protected Class<?> findClass(String classPath, String className) {
		try {
			FileInputStream in = new FileInputStream(new File(classPath));
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			for (int len = 0; (len = in.read(buffer)) != -1;) {
				out.write(buffer, 0, len);
			}
			in.close();
			byte[] bytes = out.toByteArray();
			return this.defineClass(className, bytes, 0, bytes.length);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
