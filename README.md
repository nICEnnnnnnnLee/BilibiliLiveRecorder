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
![CI](https://github.com/nICEnnnnnnnLee/BilibiliLiveRecorder/workflows/CI/badge.svg)
![Release 下载总量](https://img.shields.io/github/downloads/nICEnnnnnnnLee/BilibiliLiveRecorder/total.svg?style=flat-square)

#### 支持Bilibili/Acfun/Douyu/Huya/Kuaishou/Huajiao/Zhanqi/YY 直播录制  
[<h4>Instruction For Developers</h4>](/DOC.md)


## :smile:作者的话
做了个Python版本的，支持`A站/B站/斗鱼/快手`，欢迎大家添砖加瓦 --> [you-live](https://github.com/nICEnnnnnnnLee/LiveRecorder)  


## :smile:使用方法
+ 程序调用时传入参数即可(顺序可变)  
    `java -Dfile.encoding=utf-8 -jar BiliLiveRecorder.jar "debug=false&check=false&delete=false&liver=douyu&id=233233&qn=0&retry=5"`  
+ 各参数意义  

| Key  | 必选 | 释义 | 
| ------------- | ------------- | ------------- |  
| options  | 否 | json配置文件位置。支持相对/绝对路径。默认`config.json` |  
| charset  | 否 | json配置文件编码格式。默认`UTF-8` |  
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
| splitAVHeaderTags  | 否 | 校准文件时是否分割a/v header Tag时。默认与splitScriptTags一致 |  
| maxAudioHeaderSize  | 否 | 当Audio tag的data size小于该值时，认为是audio header。默认`10` | 
| maxVideoHeaderSize  | 否 | 当Video tag的data size小于该值时，认为是video header。默认`60`  | 
| fileName  | 否 | 文件命名规则，默认`{name}-{shortId} 的{liver}直播{startTime}-{seq}` | 
| timeFormat  | 否 | 文件命名中{startTime}和{endTime}的格式，默认`yyyy-MM-dd HH.mm` | 
| saveFolder  | 否 | 源文件保存路径 | 
| saveFolderAfterCheck  | 否 | FLV文件校准后的保存路径，check为true时有效。默认为空，此时与`saveFolder`等同 | 
| stopAfterOffline  | 否 | 当目标下播后，是否停止程序。为false时，需要和下面三个参数配合。默认true | 
| retryIfLiveOff  | 否 | 当目标不在直播时，是否继续重试。默认false | 
| maxRetryIfLiveOff  | 否 | 当目标不在直播时，继续重试的次数。默认0，此时会一直进行尝试，直到主播上线 | 
| retryAfterMinutes  | 否 | 当目标不在直播时，每次获取直播间信息的时间间隔，单位分钟。默认`5.0` | 
| failRetryAfterMinutes  | 否 | 当连接出现异常时，下次尝试录制的时间间隔，单位分钟。默认`1.0` | 
| plugin  | 否 | 插件功能，允许用户自定义某些操作。默认false |  

+ 各直播源解析情况  

| liver  | 最后测试时间 | 备注 | 
| ------------- | ------------- | ------------- | 
| douyu     | 2021/08/16 | `flv`清晰度可多选，但部分高清需要cookie | 
| kuaishou  | 2021/08/16 | `flv`清晰度可多选，必须要cookie(可以不登录，只需要过了拖拽验证即可) | 
| huya      | 2021/08/16 | `flv`清晰度可多选，可不需要cookie。部分视频时间戳校正后声音会变形，ffmpeg处理后正常 | 
| acfun     | 2021/08/16 | `flv`清晰度可多选，可不需要cookie | 
| bili      | 2021/08/16 | `flv`清晰度可多选，可不需要cookie | 
| zhanqi    | 2019/06/30 | `flv`清晰度可多选，可不需要cookie | 
| douyin    | 2020/09/11 | `flv`只支持默认清晰度，不咋玩，没找到多清晰度的直播间😳 | 
| yy        | 2019/06/15 | `flv`只支持默认清晰度 | 
| huajiao   | 2019/06/02 | `flv`只支持默认清晰度(似乎只有一种清晰度) | 

<details>
<summary>关于json配置</summary>


+ 如出现乱码，请尝试将charset设为不同的值，例如`charset=GBK`  
+ 支持文件配置 + 参数字符串混用，此时直接传入的参数配置拥有更高优先级，例如
```
假设传入参数为： 
    options=D:\\Workspace\\config.json&liver=huya

对应的json文件为：
{
    "debug": false,
    "check": true,
    "delete": true,
    "zip": false,
    "liver": "bili",
    "retry": 5,
    "fileSize": 0,
    "filePeriod": 0,
    "splitScriptTags": false,
    "splitAVHeaderTags": false,
    "fileName": "{name}-{shortId} 的{liver}直播{startTime}-{seq}",
    "timeFormat": "yyyy-MM-dd HH.mm",
    "retryIfLiveOff": false,
    "maxRetryIfLiveOff": 0,
    "retryAfterMinutes": 5.0,
    "plugin": false,
    "stopAfterOffline": true
}

那么，此时生效的liver应该为： huya
```
</details>

<details>
<summary>校正某FLV文件的时间戳</summary>


+ `java -Dfile.encoding=utf-8 -cp BiliLiveRecorder.jar nicelee.bilibili.live.check.FlvCheckerWithBufferEx "flv=源文件路径&debug=false&splitScripts=true&splitAVHeader=true&saveFolder=保存的文件夹路径"`

| Key  | 必选 | 释义 | 
| ------------- | ------------- | ------------- |  
| flv  | 是 | 源文件路径 |  
| debug  | 否 | debug模式,输出更多信息。默认true |  
| splitScripts  | 否 | 当出现多个Script tag时，是否分割文件。默认false |  
| splitAVHeaders  | 否 | 当出现多个a/v header时，是否分割文件。默认与splitScripts一致 |  
| saveFolder  | 否 | 校准时间戳后的保存目录。默认与源文件相同目录 |  
| deleteOnchecked  | 否 | 校准后是否删除源文件，默认false |  
| maxAudioHeaderSize  | 否 | 当Audio tag的data size小于该值时，认为是audio header。默认`10` | 
| maxVideoHeaderSize  | 否 | 当Video tag的data size小于该值时，认为是video header。默认`60`  | 

+ 旧版本的调用方法仍然兼容，但功能已经不再更新  
+ 当**主播pk/更换设备/修改推流参数/旋转画面/网络不稳定**时，可能出现许多异常情况。  
    + `splitScripts`和`splitAVHeaders`参数就是针对这些异常采取的某些处理。  
    + 当录制正常时，上面两个参数基本没有影响。  
    + 注意：这些操作**没法还原**，所以理论上原始文件最保真。  `不校验时间戳` ≈ `校验文件不分割` > `校验文件分割script/video header/audio header`  
</details>     	

<details>
<summary>加载cookies(适用于高清晰度录制)</summary>


+ 将cookie保存到同级目录的`{liver}-cookie.txt`即可，e.g. `douyu-cookie.txt`     
+ cookie内容为以下格式：  
```
dy_did=xxx; acf_did=xxx; acf_auth=xxx; ...
```
+ 如何获取cookie(以斗鱼举例)：  
    + 打开浏览器，进入斗鱼直播  
    + 登录账号  
    + 进入一个热度较高的直播间，选择清晰度： 蓝光10M(保险操作，如果清晰度不够试一试)   
    + 按F12键  
    + 过滤网址`www.douyu.com`  
    + 任意选择一条记录，复制右边的cookie，如下图  
    ![](/release/preview/cookie.png)  
</details>     

<details>
<summary>关于Plugin</summary>


+ 写死的文件位置：`plugin/CustomOperation.java`  
+ 可以新增`import`和自定义各种方法  
+ 可以调用另外的库，这时需要`java -jar`换成 `java -cp`的形式，请善用搜索  
+ 当Plugin文件发生变化时，请先删除运行时编译的class文件，否则不会生效
</details>          
    
<details>
<summary>关于文件命名规则</summary>


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

| Key  | 释义 | 支持saveFolder |    
| ------------- | ------------- | ------------- | 
| name      | 主播名称 | 是 | 
| shortId    | 直播网址id |  是 | 
| roomId     | 实际房间id，可能与shortId不同 |   是 |  
| liver     | 直播源，同传入参数 |    是 | 
| startTime     | 录制开始时间，精确到分，例如2019-11-19 20.18 |   是 | 
| endTime     | 录制开始时间，精确到分，例如2019-11-19 20.18 |  否 | 
| seq     | 录制产生的文件序号。从0开始；分段录制或异常重试均会使序号增大 |  否 | 
</details>

	

<details>
<summary>关于清晰度规则(ver>=2.5.0)</summary>


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
</details>

    
    
    
<details>
<summary>关于获取 房间id</summary>


如下图：  
![](/release/preview/id.png)  
</details>  


<details>
<summary>运行截图</summary>


如下图：  
![](/release/preview/run.png)  
</details>      


## :smile:其它  
* **支持UI的简单易操作的B站视频下载器**：[https://github.com/nICEnnnnnnnLee/BilibiliDown](https://github.com/nICEnnnnnnnLee/BilibiliDown)
* **下载地址**: [https://github.com/nICEnnnnnnnLee/BilibiliLiveRecorder/releases](https://github.com/nICEnnnnnnnLee/BilibiliLiveRecorder/releases)
* **GitHub**: [https://github.com/nICEnnnnnnnLee/BilibiliLiveRecorder](https://github.com/nICEnnnnnnnLee/BilibiliLiveRecorder)  
* **更新历史**: <https://github.com/nICEnnnnnnnLee/BilibiliLiveRecorder/blob/master/UPDATE.md>

## :smile:第三方库使用声明  
+ 使用[JSON.org](https://github.com/stleary/JSON-java)库做简单的Json解析[![](https://img.shields.io/badge/license-MIT-green.svg)](https://github.com/stleary/JSON-java/blob/master/LICENSE)
+ 使用[Crypto-js](https://github.com/brix/crypto-js)仿浏览器生成斗鱼直播录制token[![](https://img.shields.io/badge/license-MIT-green.svg)](https://github.com/brix/crypto-js/blob/develop/LICENSE) 
+ 虎牙对得到的直播流地址做进一步参数处理参考了[wbt5/real-url](https://github.com/wbt5/real-url/blob/df183eee17022d558cfc2aec221dfe632e360b13/huya.py#L11-L28)  

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
