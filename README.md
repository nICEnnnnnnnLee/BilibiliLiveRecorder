# BilibiliLiveRecorder
![语言java](https://img.shields.io/badge/Require-java-green.svg)
![支持系统 Win/Linux/Mac](https://img.shields.io/badge/Platform-%20win%20|%20linux%20|%20mac-lightgrey.svg)
![测试版本64位Win10系统, jre 1.8.0_101](https://img.shields.io/badge/TestPass-Win10%20x64__java__1.8.0__101-green.svg)
![开源协议Apache2.0](https://img.shields.io/badge/license-apache--2.0-green.svg)  
![当前版本](https://img.shields.io/github/release/nICEnnnnnnnLee/BilibiliLiveRecorder.svg?style=flat-square)
![Release 下载总量](https://img.shields.io/github/downloads/nICEnnnnnnnLee/BilibiliLiveRecorder/total.svg?style=flat-square)

Bilibili/Douyu/Huya/Kuaishou 直播录制  
===============================  
## :smile:使用方法
+ 程序调用时传入参数即可(顺序可变)  
    `java -Dfile.encoding=utf-8 -jar BiliLiveRecorder.jar "debug=false&check=false&delete=false&liver=douyu&id=233233&qn=0&retry=5"`  
+ 各参数意义  

| Key  | 必选 | 释义 | 
| ------------- | ------------- | ------------- |  
| debug  | 否 | debug模式,输出更多信息。默认false |  
| check  | 否 | 下载完后是否校准时间戳，默认true |  
| delete  | 否 | 校准后是否删除源文件，默认true |  
| liver  | 是 | 将要录制的直播源。 详见下表 | 
| id  | 否 | 直播房间id，如未传入，后续将提示输入。 | 
| qn  | 否 | 直播视频清晰度，如未传入，后续将提示输入。不同网站数值意义不同 |   
| retry  | 否 | 异常导致录制停止后重试次数。默认5次 |   
   
+ 各直播源解析情况  

| liver  | 最后测试时间 | 备注 | 
| ------------- | ------------- | ------------- | 
| bili      | 2019/09/19 | `flv`清晰度可多选，可不需要cookie | 
| zhanqi    | 2019/06/30 | `flv`清晰度可多选，可不需要cookie | 
| douyu     | 2019/10/15 | `flv`清晰度可多选，但部分高清需要cookie | 
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
    
  获取 房间id  
![](https://raw.githubusercontent.com/nICEnnnnnnnLee/BilibiliLiveRecorder/master/release/preview/id.png)  
    
  运行截图
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
