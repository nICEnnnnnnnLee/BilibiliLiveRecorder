package nicelee.bilibili.util;

public class Logger {

	
	public static boolean debug = true;
	/**
	 * 测试用
	 * @param str
	 */
	public static void print(String str) {
		if(!debug) {
			return;
		}
		System.out.print(str);
	}
	public static void print(Object str) {
		if(!debug) {
			return;
		}
		System.out.print(str);
	}
	public static void println() {
		if(!debug) {
			return;
		}
		System.out.println();
	}
	
	/**
	 * 测试用
	 * @param str
	 */
	public static void printf(String str, Object... obj) {
		if(!debug) {
			return;
		}
		StackTraceElement ele = Thread.currentThread().getStackTrace()[2];
		String file = ele.getFileName();
		file = file.substring(0, file.length() - 5);
		String method = ele.getMethodName();
		int line = ele.getLineNumber();
		String preStr = String.format(str, obj);
		String result = String.format("%s-%s/%d : %s", file, method, line, preStr);
		System.out.println(result);
	}
	
	/**
	 * 测试用
	 * @param str
	 */
	public static void printf(int tabNum, String str, Object... obj) {
		if(!debug) {
			return;
		}
		StringBuilder tab = new StringBuilder(); 
		for(int i=0; i<tabNum; i++) {
			tab.append("\t");
		}
		StackTraceElement ele = Thread.currentThread().getStackTrace()[2];
		String file = ele.getFileName();
		file = file.substring(0, file.length() - 5);
		String method = ele.getMethodName();
		int line = ele.getLineNumber();
		String preStr = String.format(str, obj);
		String result = String.format("%s-%s/%d : %s%s", file, method, line, tab.toString(), preStr);
		System.out.println(result);
	}
	
	/**
	 * 测试用
	 * @param str
	 */
	public static void println(int tabNum, String str) {
		if(!debug) {
			return;
		}
		StringBuilder tab = new StringBuilder(); 
		for(int i=0; i<tabNum; i++) {
			tab.append("\t");
		}
		StackTraceElement ele = Thread.currentThread().getStackTrace()[2];
		String file = ele.getFileName();
		file = file.substring(0, file.length() - 5);
		String method = ele.getMethodName();
		int line = ele.getLineNumber();
		String result = String.format("%s-%s/%d : %s%s", file, method, line, tab.toString(), str);
		System.out.println(result);
	}
	
	/**
	 * 测试用
	 * @param str
	 */
	public static void println(String str) {
		if(!debug) {
			return;
		}
		StackTraceElement ele = Thread.currentThread().getStackTrace()[2];
		String file = ele.getFileName();
		file = file.substring(0, file.length() - 5);
		String method = ele.getMethodName();
		int line = ele.getLineNumber();
		String result = String.format("%s-%s/%d : %s", file, method, line, str);
		System.out.println(result);
	}
	/**
	 * 测试用
	 * @param str
	 */
	public static void println(Object obj) {
		if(!debug) {
			return;
		}
		StackTraceElement ele = Thread.currentThread().getStackTrace()[2];
		String file = ele.getFileName();
		file = file.substring(0, file.length() - 5);
		String method = ele.getMethodName();
		int line = ele.getLineNumber();
		String result = String.format("%s-%s/%d : %s", file, method, line, obj.toString());
		System.out.println(result);
	}
}
