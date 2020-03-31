package nicelee.bilibili.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

public class JSEngine {

	public static void main(String[] a) {
		run(null, "ub98484234", "2040718","2206c59057010dd04573c76400081501","1558952335");
	}
	
	/**
	 * 初始化CryptoJS
	 */
	private static String cryptoJS = null;
	private static String initCryptoJS() {
		if(cryptoJS == null) {
			StringBuilder sb = new StringBuilder();
			try {
				BufferedReader buReader = new BufferedReader(new InputStreamReader(JSEngine.class.getClassLoader().getResource("crypto-js.min.js").openStream()));
				String line = null;
				while( (line = buReader.readLine()) != null) {
					sb.append(line);
				}
				buReader.close();
			} catch (IOException e) {
			}
			cryptoJS = sb.toString();
		}
		return cryptoJS;
	}
	
	
	public static String run(String scripts, String method, Object... params) {
//		for(Object obj: params) {
//			System.out.println(obj);
//		}
        try {
        	//1.得到脚本引擎
        	ScriptEngineManager m = new ScriptEngineManager();
        	ScriptEngine engine = m.getEngineByName("JavaScript");
            //2.引擎读取 脚本字符串
        	engine.eval(scripts);
            engine.eval(initCryptoJS());
            //3.将引擎转换为Invocable，这样才可以掉用js的方法
            Invocable invocable = (Invocable) engine;
            //4.使用 invocable.invokeFunction掉用js脚本里的方法，第一個参数为方法名，后面的参数为被调用的js方法的入参
            String scriptResult = (String) invocable.invokeFunction(method, params);
            //System.out.print(scriptResult);
            return scriptResult;
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
