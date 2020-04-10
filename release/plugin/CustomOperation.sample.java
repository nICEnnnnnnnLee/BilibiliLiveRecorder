
import java.io.File;
import java.io.IOException;
import java.util.List;

import nicelee.bilibili.Config;

public class CustomOperation {

	public void runBeforeInit(String[] args) {
		//System.out.println("运行在初始化参数之前");
	}

	public void runAfterInit() {
		//System.out.println("运行在初始化参数之后");
	}

	public void runAfterComplete(List<File> files) throws IOException {
		for (File f : files) {
			//System.out.printf("录制文件: %s \r\n", f.getCanonicalPath());
		}
	}
}
