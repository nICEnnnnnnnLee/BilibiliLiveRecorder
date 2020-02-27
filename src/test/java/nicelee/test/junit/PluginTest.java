package nicelee.test.junit;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import nicelee.bilibili.plugin.Plugin;

public class PluginTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		Plugin plugin = new Plugin();
		boolean result = plugin.compile();
		System.out.println(result);
		
		List<File> files = new ArrayList<File>();
		files.add(new File("./hello.java"));
		plugin.runAfterComplete(files);
	}

}
