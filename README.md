<p align = "center">
<img alt="你指尖跃动的电光,是我此生不变的信仰" src="/release/preview/bilibili.jpg">
<br><br>
Go go go, Bilibili Pikachu!
<br><br>
</p>

# BilibiliLiveRecorder
![语言java](https://img.shields.io/badge/Require-java-green.svg)
![支持系统 Win/Linux/Mac](https://img.shields.io/badge/Platform-%20win%20|%20linux%20|%20mac-lightgrey.svg)
![测试版本64位Win10系统, jre 1.8.0_101](https://img.shields.io/badge/TestPass-Win10%20x64__java__1.8.0__101-green.svg)
![开源协议Apache2.0](https://img.shields.io/badge/license-apache--2.0-green.svg)  
![当前版本](https://img.shields.io/github/release/nICEnnnnnnnLee/BilibiliLiveRecorder.svg?style=flat-square)
![Release 下载总量](https://img.shields.io/github/downloads/nICEnnnnnnnLee/BilibiliLiveRecorder/total.svg?style=flat-square)

#### 支持Bilibili/Douyu/Huya/Kuaishou/Huajiao/Zhanqi/YY 直播录制  
[<h4>Instruction For Developers</h4>](/DOC.md)


## :smile:使用方法
+ 程序调用时传入参数即可(顺序可变)  
    `java -Dfile.encoding=utf-8 -jar BiliLiveRecorder.jar "debug=false&check=false&delete=false&liver=douyu&id=233233&qn=0&retry=5"`  
+ 各参数意义  

| Key  | 必选 | 释义 | 
| ------------- | ------------- | ------------- |  
| debug  | 否 | debug模式,输出更多信息。默认false |  
| check  | 否 | 下载完后是否校准时间戳，默认true |  
| delete  | 否 | 校准后是否删除源文件，默认true |  
| zip  | 否 | 是否压缩成zip文件，默认false |  
| liver  | 是 | 将要录制的直播源。 详见下表 | 
| id  | 否 | 直播房间id，如未传入，后续将提示输入。 | 
| qn  | 否 | 直播视频清晰度。不同网站值意义不同。-1代表最高清晰度。 |   
| qnPri  | 否 | 直播视频清晰度优先级。分隔符`>` 例: `蓝光4M>蓝光>超清` 蓝光4M优先级最高 | 
| retry  | 否 | 异常导致录制停止后的重试次数。默认5次 |   
| fileSize  | 否 | 分段录制的参考文件大小，0为不按文件大小分段，单位`MB`。默认0 |   
| filePeriod  | 否 | 分段录制的参考时长，0为不按时长分段，单位`min`。默认0 |   
| proxy  | 否 | 按需配置。http(s)代理 e.g. `127.0.0.1:8888` |   
| socksProxy  | 否 | 按需配置。socks代理 e.g. `127.0.0.1:1080` |   
| trustAllCert  | 否 | 是否无条件信任所有SSL证书。默认false |   
| splitScriptTags  | 否 | 校准文件时是否分割ScriptTag。默认false | 
| fileName  | 否 | 文件命名规则，默认`{name}-{shortId} 的{liver}直播{startTime}-{seq}` | 
| saveFolder  | 否 | 文件保存路径 | 
   
+ 各直播源解析情况  

| liver  | 最后测试时间 | 备注 | 
| ------------- | ------------- | ------------- | 
| bili      | 2019/09/19 | `flv`清晰度可多选，可不需要cookie | 
| zhanqi    | 2019/06/30 | `flv`清晰度可多选，可不需要cookie | 
| douyu     | 2019/12/28 | `flv`清晰度可多选，但部分高清需要cookie | 
| kuaishou  | 2019/09/19 | `flv`清晰度可多选，可能需要cookie(与登录无关，首次进入直播页面有反爬措施，会需要拖拽验证) | 
| huya      | 2019/08/30 | `flv`清晰度可多选，可不需要cookie | 
| yy        | 2019/06/15 | `flv`只支持默认清晰度 | 
| huajiao   | 2019/06/02 | `flv`只支持默认清晰度(似乎只有一种清晰度) | 

+ 校正某FLV文件的时间戳  
	+ `java -Dfile.encoding=utf-8 -cp BiliLiveRecorder.jar nicelee.bilibili.live.FlvChecker "文件路径"`  
	+ `java -Dfile.encoding=utf-8 -cp BiliLiveRecorder.jar nicelee.bilibili.live.FlvChecker "文件路径" true`  
		+ 第二个布尔参数的意义是**当遇到某种特定情况时，是否分割文件**  
		+ 注意：这些操作**没法还原**，所以理论上原始文件最保真。  `不校验时间戳` ≈ `校验文件不分割` > `校验文件分割scripts tag`  
		+ 如果仍旧没办法满足需求的话，建议拿着各种版本都去ffmpeg处理一下  
		
+ 加载cookies(适用于高清晰度录制)
    将cookie保存到同级目录的`{liver}-cookie.txt`即可，e.g. `douyu-cookie.txt`   


+ 关于文件命名规则
    + 请勿传入非法字符，如`&`  
	+ 建议保留`{startTime}`和`{seq}`，以确保文件名唯一，否则很可能出现未知错误  
	+ 校准时间戳这一动作将会产生若干个文件，这些文件将在原来的基础上增加-checked[0-9]+后缀  
	+ 举例：
	```
	fileName={name}-{shortId} 的{liver}直播{startTime}-{seq}&filePeriod=20&check=false
	那么，一个可能的结果是：
	英雄联盟赛事-288016 的douyu直播 2019-09-19 17.40-0.flv
	英雄联盟赛事-288016 的douyu直播 2019-09-19 18.00-1.flv

	fileName={name}-{shortId} 的{liver}直播{startTime}-{seq}&filePeriod=20&check=true
	增加时间戳校准动作。那么，一个可能的结果是：
	英雄联盟赛事-288016 的douyu直播 2019-09-19 17.40-0-checked0.flv
	英雄联盟赛事-288016 的douyu直播 2019-09-19 18.00-1-checked0.flv
	```	

	| Key  | 释义 |  
	| ------------- | ------------- | 
	| name      | 主播名称 | 
	| shortId    | 直播网址id | 
	| roomId     | 实际房间id，可能与shortId不同 |   
	| liver     | 直播源，同传入参数 |   
	| startTime     | 录制开始时间，精确到分，例如2019-11-19 20.18 |  
	| seq     | 录制产生的文件序号。从0开始；分段录制或异常重试均会使序号增大 | 

	


+ 关于清晰度规则(ver>=2.5.0)
	+ `qn`和`qnPri`可以同时存在，优先考虑`qnPri`，若匹配失败，再考虑传入的`qn` 
    ```
	可提供直播质量:
        0 : 超清
        2 : 高清
        1 : 流畅
    传入参数： qn=2&qnPri=蓝光4M>蓝光
    此时取 2 : 高清
    -------------------------------
    可提供直播质量:
        0 : 蓝光
        3 : 超清
        2 : 高清
        1 : 流畅
    传入参数： qn=2&qnPri=蓝光4M>蓝光
    此时取 0 : 蓝光
	```	
    
    + 当未传入qn，且(qnPri为空或不匹配)，程序将提示输入qn值
    ```
	可提供直播质量:
        0 : 超清
        2 : 高清
        1 : 流畅
    传入参数： 不包含qn、qnPri   
    or传入参数： qnPri=蓝光4M>蓝光
    此时程序将提示输入qn值
	```	
    
    + 当指定qn生效(指qnPri为空或不匹配)，且获取的清晰度列表不存在该清晰度值时，程序将退出
    ```
	可提供直播质量:
        0 : 超清
        2 : 高清
        1 : 流畅
    传入参数： qn=4
    此时程序将退出
    传入参数： qn=4&qnPri=蓝光4M>蓝光
    此时程序将退出
	```	
    
    
    
    
+ 获取 房间id  
![](https://raw.githubusercontent.com/nICEnnnnnnnLee/BilibiliLiveRecorder/master/release/preview/id.png)  
    
+ 运行截图
![](https://raw.githubusercontent.com/nICEnnnnnnnLee/BilibiliLiveRecorder/master/release/preview/run.png)  

## :smile:其它  
* **支持UI的简单易操作的B站视频下载器**：[https://github.com/nICEnnnnnnnLee/BilibiliDown](https://github.com/nICEnnnnnnnLee/BilibiliDown)
* **下载地址**: [https://github.com/nICEnnnnnnnLee/BilibiliLiveRecorder/releases](https://github.com/nICEnnnnnnnLee/BilibiliLiveRecorder/releases)
* **GitHub**: [https://github.com/nICEnnnnnnnLee/BilibiliLiveRecorder](https://github.com/nICEnnnnnnnLee/BilibiliLiveRecorder)  
* **更新历史**: <https://github.com/nICEnnnnnnnLee/BilibiliLiveRecorder/blob/master/UPDATE.md>

## :smile:第三方库使用声明  
* 使用[JSON.org](https://github.com/stleary/JSON-java)库做简单的Json解析[![](https://img.shields.io/badge/license-MIT-green.svg)](https://github.com/stleary/JSON-java/blob/master/LICENSE)
+ 使用[Crypto-js](https://github.com/brix/crypto-js)仿浏览器生成斗鱼直播录制token[![](https://img.shields.io/badge/license-MIT-green.svg)](https://github.com/brix/crypto-js/blob/develop/LICENSE) 


## :smile:LICENSE 
```
Copyright (C) 2019 NiceLee. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
