package nicelee.bilibili.plugin;

import java.io.File;
import java.lang.reflect.Method;
import java.util.List;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import nicelee.bilibili.Config;

public class Plugin {

	File workingDir;
	static Class<?> clazz;
	
	public Plugin() {
		workingDir = new File("./");
	}

	public Plugin(String workingDir) {
		this.setEnv(workingDir);
	}

	public void setEnv(String workingDirectory) {
		workingDir = new File(workingDirectory);
	}

	public boolean compile() {
		File classFile = new File(workingDir, "plugin/CustomOperation.class");
		if (classFile.exists())
			return true;

		try {
			File file = new File(workingDir, "plugin/CustomOperation.java");
			JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
			int result = compiler.run(null, null, null, file.getCanonicalPath());
			return result == 0;
		} catch (Exception e) {
			return false;
		}

	}
	
	public void runBeforeInit(String[] args) {
		try {
			if(clazz == null) {
				File file = new File(workingDir, "plugin/CustomOperation.class");
				CustomClassLoader ccloader = new CustomClassLoader();
				clazz = ccloader.findClass(file.getCanonicalPath(), "CustomOperation");
			}
			Method method = clazz.getMethod("runBeforeInit", String[].class);
			method.invoke(clazz.newInstance(), new Object[]{args});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void runAfterInit() {
		try {
			if(clazz == null) {
				File file = new File(workingDir, "plugin/CustomOperation.class");
				CustomClassLoader ccloader = new CustomClassLoader();
				clazz = ccloader.findClass(file.getCanonicalPath(), "CustomOperation");
			}
			Method method = clazz.getMethod("runAfterInit");
			method.invoke(clazz.newInstance());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void runAfterComplete(List<File> files) {
		try {
			if(clazz == null) {
				File file = new File(workingDir, "plugin/CustomOperation.class");
				CustomClassLoader ccloader = new CustomClassLoader();
				clazz = ccloader.findClass(file.getCanonicalPath(), "CustomOperation");
			}
			Method method = clazz.getMethod("runAfterComplete", java.util.List.class);
			method.invoke(clazz.newInstance(), files);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
