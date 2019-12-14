# Overview

<p align = "center">
<img alt="你指尖跃动的电光,是我此生不变的信仰" src="/release/preview/bilibili.jpg">
<br><br>
Go go go, Bilibili Pikachu!
<br><br>
</p>

# Get it!

## Maven

Functionality of this package is contained in 
Java package `nicelee.bilibili`.

The package is (only) deployed in **Github Packages**, you may need to add this repository to maven source and config an authentication in `~/.m2/settings.xml`. 
Here's the [guide](https://help.github.com/en/github/managing-packages-with-github-packages/configuring-apache-maven-for-use-with-github-packages#installing-a-package).  

To use the package, you need to use following Maven dependency:

```xml
<dependency>
  <groupId>top.nicelee</groupId>
  <artifactId>live-record</artifactId>
  <version>{live-record.version}</version>
</dependency> 
```

or download jars from Github Packages or links [Here](https://github.com/nICEnnnnnnnLee/BilibiliLiveRecorder/packages).

Package has no external dependencies, except for `org.json`(for simple json string parse) and testing (which uses `JUnit`).

## Non-Maven

For non-Maven use cases, you download jars from [Github Packages](https://github.com/nICEnnnnnnnLee/BilibiliLiveRecorder/packages).

-----
# Use it!

## Quickstart

**Example0: record a live from <https://live.bilibili.com/6> for highest video quality, stop util offline**

<details>
<summary>Example0</summary>


```java
package nicelee.bilibili;

import java.io.File;
import java.io.IOException;

import nicelee.bilibili.enums.StatusEnum;
import nicelee.bilibili.live.FlvChecker;
import nicelee.bilibili.live.RoomDealer;
import nicelee.bilibili.live.domain.RoomInfo;
import nicelee.bilibili.live.impl.RoomDealerBilibili;

public class Example0 {

	public static void main(String[] args) {
		// some args that should be already known
		String liver = "bili";
		String roomIdFromURL = "6";
		String cookie = "xxx1=xxx; xxx2=xxxx; xxx3=xxxx"; // cookie = null is also allowed
		String fileNameWithoutSuffix = "保存文件名";
		boolean deleteOnchecked = true; // 处理完录制后，是否删除源文件
		boolean splitScriptTagsIfCheck = false; // 针对FLV的处理方式
		
		
		// 获取工具类
		// will be optimized in next version
		Class<?> clazz = PackageScanLoader.dealerClazz.get(liver);
		RoomDealer dealer = null;
		try {
			dealer = (RoomDealer) clazz.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			dealer = new RoomDealerBilibili();
		}
		//RoomDealer dealer = new RoomDealerBilibili(); // Is OK but NOT recommended
		// 获取房间信息
		dealer.setCookie(cookie); // 在某些情况下，游客和登录用户获取到的清晰度不一致
		RoomInfo roomInfo = dealer.getRoomInfo(roomIdFromURL);
		if (roomInfo == null) {
			System.err.println("解析失败！！");
			return;
		}
		// 查看当前是否在直播
		if (roomInfo.getLiveStatus() != 1) {
			System.out.println("当前没有在直播");
			return;
		}
		// 获取当前最清晰qn
		String qn = roomInfo.getAcceptQuality()[0];
		// 获取当前直播源地址
		String url = dealer.getLiveUrl(roomInfo.getRoomId(), qn, roomInfo.getRemark(), cookie);
		
		
		// 下载源文件 
		// 更改保存地址
		//dealer.util.setSavePath(new File("D:\Workspace\"));
		dealer.startRecord(url, fileNameWithoutSuffix, roomInfo.getShortId());
		// 此处一直堵塞， 直至异常 或者 主播下播 或者 人工停止。 //可以另起一个线程人工停止dealer.stopRecord();
		
		switch (dealer.util.getStatus()) {
		case NONE:
			System.out.println("下载尚未开始");
			break;
		case DOWNLOADING:
			System.out.println("正在录制");
			break;
		case FAIL:
			System.out.println("下载异常");
			break;
		case STOP:
			System.out.println("人工停止");
			break;
		case SUCCESS:
			System.out.println("主播下播");
			break;
		default:
			System.out.println("已停止录制，但原因未知");
			break;
		}
		
		// 处理源文件 
		// 下载完毕，但是源文件最后一帧以及时间戳可能存在问题
		try {
			File file = dealer.util.getFileDownload();
			// 如果不是主播下播自然停止，会有.part后缀
			if(dealer.util.getStatus() != StatusEnum.SUCCESS) {
				File partFile = new File(file.getParent(), file.getName() + ".part");
				partFile.renameTo(file);
			}
			new FlvChecker().check(file.getCanonicalPath(), deleteOnchecked, splitScriptTagsIfCheck);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
```
</details>

## Usage
### get Core Util
```java
// Get the right type of RoomDealer
Class<?> clazz = PackageScanLoader.dealerClazz.get("bili"); // liver should be support. See ReadMe for details
RoomDealer dealer = (RoomDealer) clazz.newInstance();

// The following is OK but NOT recommended
// RoomDealer dealer = new RoomDealerBilibili(); 
// RoomDealer dealer = new RoomDealerDouyu(); 
```


### get RoomInfo

```java
// Get the right type of RoomDealer
RoomDealer dealer = new RoomDealerBilibili();
// Cookie set could be skipped. But things may go different for guests/logged-in-users for Douyu
dealer.setCookie(cookie); 
RoomInfo roomInfo = dealer.getRoomInfo(roomIdFromURL);
// roomInfo may be null if sth goes wrong
```

Here's the roomInfo:  
```
String shortId; // 直播url 里面的id， 可能与roomId相同
String roomId; // 用于标识房间的真正ID
long userId;  // 主播id
String userName; // 主播名称
int liveStatus; // 1 直播; 其它 不在直播
String title; // 房间标题
String description; // 相关描述
String[] acceptQuality; // 序号越小，清晰度越高
String[] acceptQualityDesc; //清晰度描述
String remark; // 用于后续拓展。 douyu: 保存加密函数
```

### get Live Source URL

```java
// Get the right type of RoomDealer
RoomDealer dealer = new RoomDealerBilibili();
// Get quality you want
// String qn = roomInfo.getAcceptQuality()[0];
// Get Cookie(null is accepted)
String cookie = null;
// Get Live Source URL
String url = dealer.getLiveUrl(roomInfo.getRoomId(), qn, roomInfo.getRemark(), cookie);
```

### download origin files from Live Source URL

**PS:** You can download it using other tools.
```java
// Get the right type of RoomDealer
RoomDealer dealer = new RoomDealerBilibili();
// default SavePath = "./download/", you could change it before downloading
//dealer.util.setSavePath(new File("D:\Workspace\"));

// Normally, the method startRecord() will block the thread util the live streamer is offline.
// You may stop it manually by invoking `dealer.stopRecord();` in another thread.
dealer.startRecord(url, fileNameWithoutSuffix, roomInfo.getShortId());

// You can get the status of recording through dealer.util.getStatus()
// See more detail in the Quickstart example

// You can get the file this way 
File file = dealer.util.getFileDownload();
if(dealer.util.getStatus() != StatusEnum.SUCCESS) {
    File partFile = new File(file.getParent(), file.getName() + ".part");
    partFile.renameTo(file);
}		
```

#### check the origin FLV file

**The origin file may have something wrong with timestamps and its last frame.**  
**Here's the solution to handle it.**
```java
try {
    boolean deleteOriginFilesOnchecked = true;
    boolean splitScriptTags = false; // Normally, there should be only 1 ScriptTag in FLV. Two methods provided to solve the problem of ScriptTag**s**.
    
    // The method will block the thread util the work is done. Noway to stop it manually.
    new FlvChecker().check("D:\Workspace\123.flv", deleteOnchecked, splitScriptTags);
    // D:\Workspace\123.flv-checked0.flv will appear
    // D:\Workspace\123.flv-checked1.flv may appear if splitScriptTags = true, it depends on number of ScriptTag**s**
    // D:\Workspace\123.flv-checked2.flv may appear if splitScriptTags = true, it depends on number of ScriptTag**s**
    // ...
} catch (IOException e) {
    e.printStackTrace();
}	
```

