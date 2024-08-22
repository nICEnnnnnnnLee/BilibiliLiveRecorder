## 更新  
+ V2.29.0
    * 修复：虎牙：参考[biliup/biliup](https://github.com/biliup/biliup/blob/7c703b936b7a79c134a7af45331d71c32de976a7/biliup/plugins/huya.py#L169)，使用微信小程序的参数。 #134 
+ V2.28.0
    * 优化：淘宝：兼容可能的json解析错误。
    * 优化：虎牙：通过java代码来计算md5，而不是js。
    * 修复：修改虎牙`ctype`、`t`参数，使之不在2分钟时就断开。 #132
+ V2.27.0
    * 修复：解决JDK15及以上斗鱼不能录制的问题 [#122](https://github.com/nICEnnnnnnnLee/BilibiliLiveRecorder/issues/122)
    * 修复：修复`huya`开始下载后直接停止的问题 [#124](https://github.com/nICEnnnnnnnLee/BilibiliLiveRecorder/issues/124)
    * 优化：对`huya2`得到的原始直播流地址换一种处理方式
    * 修复：修复`douyin`无法解析的问题, 使用移动端页面解析 [#125](https://github.com/nICEnnnnnnnLee/BilibiliLiveRecorder/issues/125)
    * 优化: 在未配置cookie使用`douyin2`时，尝试打印建议的cookie值

+ V2.26.0
    * 修复：快手直播`liver=kuaishou`
    * 新增: 淘宝`taobao`解析，并支持输入回放链接提取m3u8下载链接
    
+ V2.25.0
    * 修复： 抖音直播`liver=douyin2`
    * 修复[#113](https://github.com/nICEnnnnnnnLee/BilibiliLiveRecorder/issues/113)： 虎牙从网页里面抠的内容有延迟，主播下播后可能误认为仍在直播。为此，获取直播链接后增加有效性检查。  
    * 新增[#5](https://github.com/nICEnnnnnnnLee/BilibiliLiveRecorder/issues/5) ： 尝试使用另一种方式获取虎牙直播信息，该方式没有延迟。  `liver=huya2`
+ V2.24.0
    * 修复[#105](https://github.com/nICEnnnnnnnLee/BilibiliLiveRecorder/issues/105),[#102](https://github.com/nICEnnnnnnnLee/BilibiliLiveRecorder/issues/102) `douyin`正则匹配修复
    * 修复快手录制
    * 优化：去除解析器里面的`System.exit`使用
    
+ V2.23.0
    * 新增：提供工具实现合并FLV功能，目的是解决分离视频时出现`只有声音`、`分辨率不对`的问题
    * 新增: [#91](https://github.com/nICEnnnnnnnLee/BilibiliLiveRecorder/issues/91),[#102](https://github.com/nICEnnnnnnnLee/BilibiliLiveRecorder/issues/102), 针对花屏，新增在校验时去掉前`contentFramesToSkip`个内容帧的功能
    * 优化：首帧时间戳间隔由`30`s固定值改为`maxPeriodBetween2Frame` ms  
        + 原理：在前10帧里，初始为0，后续如果某帧相比前一帧间隔过大，则选取该帧时间戳作为初始时间戳。
        
+ V2.22.0
    * 修复[#93](https://github.com/nICEnnnnnnnLee/BilibiliLiveRecorder/issues/93)、[#96](https://github.com/nICEnnnnnnnLee/BilibiliLiveRecorder/issues/96), 修复一个cookie转换问题，该问题导致cookie中存在字符`|`时会出现错误
+ V2.21.0
    * 修复[#94](https://github.com/nICEnnnnnnnLee/BilibiliLiveRecorder/issues/94), 修复YY直播
    * 修复[#93](https://github.com/nICEnnnnnnnLee/BilibiliLiveRecorder/issues/93), 抖音直播新增另一种备用解析方式, 注意`liver=douyin2`, cookie文件名为`douyin2-cookie.txt`
    * 修复抖音直播传入短链接`https://v.douyin.com/xxxx`的解析方式
    
+ V2.20.0
    * 将POST方式查询视频源时的HTTP连接超时时间改为20s
+ V2.19.0
    * 修复[#87](https://github.com/nICEnnnnnnnLee/BilibiliLiveRecorder/issues/87)：修复虎牙录制
+ V2.18.1
    * 修复[#86](https://github.com/nICEnnnnnnnLee/BilibiliLiveRecorder/issues/86)：适配最新抖音版本 
+ V2.18.0
    * 修复[#72](https://github.com/nICEnnnnnnnLee/BilibiliLiveRecorder/issues/72)：修复斗鱼部分区域获取链接为m3u8的问题  
+ V2.17.0
    * 修复[#69](https://github.com/nICEnnnnnnnLee/BilibiliLiveRecorder/issues/69)：修复斗鱼部分直播间不能录播的问题  
+ V2.16.0
    * 修复[#67](https://github.com/nICEnnnnnnnLee/BilibiliLiveRecorder/issues/67)：使抖音直播录制支持cookie(尚未测试仅移动端限定直播)
+ V2.15.0
    * 优化抖音直播录制逻辑，支持PC/移动端直播、支持清晰度选择、理论上短链接不再是一次性录制
+ V2.14.0
    * 修复[#60](https://github.com/nICEnnnnnnnLee/BilibiliLiveRecorder/issues/60)：修复bilibili概率性出错的问题  
    * 修复： 更新斗鱼API，解决得不到高清晰度的问题  
    * 修复： 解决快手在合法cookie下得不到直播源的问题  
+ V2.13.0
    * 新增[#52](https://github.com/nICEnnnnnnnLee/BilibiliLiveRecorder/issues/52)：新增抖音录制   
+ V2.12.0
    * 优化[#48](https://github.com/nICEnnnnnnnLee/BilibiliLiveRecorder/issues/48)：新增录制异常重试间隔配置项`failRetryAfterMinutes`   
    * 修复[#53](https://github.com/nICEnnnnnnnLee/BilibiliLiveRecorder/issues/53)：对得到的直播流链接做进一步处理，解决虎牙一起看录制失败问题  
+ V2.11.0
    * 修复[#47](https://github.com/nICEnnnnnnnLee/BilibiliLiveRecorder/issues/47)：尝试修复虎牙录制一段时间后自动断开的问题   
+ V2.10.1
    * 优化[#45](https://github.com/nICEnnnnnnnLee/BilibiliLiveRecorder/issues/45)：将命令交互放入Daemon线程 
    * 增加一个异常处理  
+ V2.10.0
    * 修复[#44](https://github.com/nICEnnnnnnnLee/BilibiliLiveRecorder/issues/44)：修复虎牙直播录制  
    * 优化[#41](https://github.com/nICEnnnnnnnLee/BilibiliLiveRecorder/issues/41)：抛出UnknownHost异常时自动重试  
+ V2.9.0
    * 修复快手不能录制的问题(graph查询返回字段更改+Accept头部修改)
+ V2.8.0
    * 新增功能[#38](https://github.com/nICEnnnnnnnLee/BilibiliLiveRecorder/issues/38)：`saveFolder`配置支持部分约定的变量
    * 新增功能[#40](https://github.com/nICEnnnnnnnLee/BilibiliLiveRecorder/issues/40)：增加acfun直播录制，房间id即up主id
    * 新增功能[#39](https://github.com/nICEnnnnnnnLee/BilibiliLiveRecorder/issues/39)：延长直播信息查询超时时间；当哔哩哔哩查询超时时，视为主播下播情况处理。
	
+ V2.7.1
    * 新增功能：从json文件读取配置
    * 新增功能[#36](https://github.com/nICEnnnnnnnLee/BilibiliLiveRecorder/issues/36)：在主播下播后可尝试继续查询/轮询房间信息
    * 重构配置读取方式，方便后续拓展
    * Main主类行数过多，逻辑趋于复杂，进行重构

+ V2.7.0
    * 增加了针对异常FLV文件的处理情况  
    * 更改了独立调用校准时间戳功能的参数传入方式(兼容旧版本，但后续新特性不再进行维护)

+ V2.6.6
    * Plugin增加内部类/匿名类支持  
    * 增加Plugin用例，能调用ffmpeg将flv转换为mp4  
    * 修复一个bug，该bug导致额外指定校正时间戳保存路径时，压缩功能失效  
    
+ V2.6.5
    * 增加Plugin功能
    * 初始化配置从主类中分离

+ V2.6.4
    *  #29 增加对退出信号的捕捉处理 
    *  #27 尽可能保持时间戳的相对大小，不再分音视频类别进行额外处理
    
+ V2.6.3
    * 优化 #26 ，校正时间戳时可以使用缓存
    * 该版本使用scripts tag分割文件功能异常
+ V2.6.2
    * 优化 #25 ，以FlvChecker的Main方法运行时，接受debug布尔开关
    * 修复一个bug #22 ，以FlvChecker的Main方法运行时，解析传入的文件保存路径；
+ V2.6.1
    * 修复一个bug #20 ，该bug导致主播正常下播时无法自动重命名{endTime}参数；
    * 优化 #22 ，如果FLV自动校准，且传入了自定义参数saveFolderAfterCheck，校准后的文件将保存在参数对应目录中
    * 优化 #23 ，如果未开播就等待几分钟后重试
+ V2.6.0
    * 优化[issue #19](https://github.com/nICEnnnnnnnLee/BilibiliLiveRecorder/issues/19) 当快手cookie失效无法获取用户信息时，以默认值代替。此时，直播录制仍可进行
    * 优化[issue #20](https://github.com/nICEnnnnnnnLee/BilibiliLiveRecorder/issues/20) 文件名支持结束时间{endTime}，且支持自定义日期格式，参见参数`timeFormat`
	* 优化[issue #22](https://github.com/nICEnnnnnnnLee/BilibiliLiveRecorder/issues/22) FlvChecker支持自定义输出目录
+ V2.5.0
    * RoomDealer封装了创建方法，可以根据liver直接创建对象
    * 优化[issue #17](https://github.com/nICEnnnnnnnLee/BilibiliLiveRecorder/issues/17) 获取的清晰度列表不存在指定清晰度值时，程序将退出
    * 优化[issue #18](https://github.com/nICEnnnnnnnLee/BilibiliLiveRecorder/issues/18) 当设置qn=-1时，使用最高的清晰度进行录制
    * 新增`qnPri`字段，e.g. `蓝光4M>蓝光>超清` 根据清晰度优先级描述获取清晰度设置
+ V2.4
	* 新增特性 [issue #15](https://github.com/nICEnnnnnnnLee/BilibiliLiveRecorder/issues/15) 支持zip压缩录制的flv文件
+ V2.3
	* 新增特性 [issue #12](https://github.com/nICEnnnnnnnLee/BilibiliLiveRecorder/issues/12) 可在调用时传入参数，使得校准时间戳时分割scripts tag
	* 新增特性 [issue #13](https://github.com/nICEnnnnnnnLee/BilibiliLiveRecorder/issues/13) 支持自定义输出路径或文件名
+ V2.2
	* 新增特性 [issue #11](https://github.com/nICEnnnnnnnLee/BilibiliLiveRecorder/issues/11) 可配置代理
+ V2.1
	* fix [issue #10](https://github.com/nICEnnnnnnnLee/BilibiliLiveRecorder/issues/10)修复一个斗鱼录制bug，该bug导致网址id与房间实际id不同时录制失败
+ V2.0
    * 新增特性 [issue #9](https://github.com/nICEnnnnnnnLee/BilibiliLiveRecorder/issues/9) 可按照文件大小或录制时间分段
+ V1.9
    * fix [issue #8](https://github.com/nICEnnnnnnnLee/BilibiliLiveRecorder/issues/8)
	* 将script Tag前一个tag size置为零
    * 提供选项，在碰到第二个script脚本时，可以选择是否分割文件
+ V1.8 
    * 修复快手更新导致的解析问题  
    * 修正斗鱼清晰度获取不全的问题(旧版本的清晰度提示有缺失，可能会有误导)  
    * `FLV`时间戳处理进一步优化  
        * 优化个别倒序时间戳的处理  
        * 音视频时间戳分开处理  
+ V1.7
    * 修复`FLV`可能出现的时间戳异常问题
    * 修复切流后的录制问题 - 当`FLV`存在多个`scripts`类型时，将视频分割处理
    （一个典型的例子是LOL比赛直播间回放，一轮播完重新开始时，视频出现问题）
    * 将链接超时时长由`12s`增加到`120s`，防止由网络问题引起的录制断开。
    （B站/虎牙/斗鱼测试了几次，似乎1h没有问题）
    * 增加录播异常断开后，自动重试功能(丢失画面应可忽略不计)
+ V1.6
    * 增加斗鱼特殊活动直播间(如LOL比赛)的针对解析
    * 修复虎牙更新导致的解析问题
+ V1.5
    * 增加虎牙直播录制
    * 增加战旗直播录制
    * 增加快手直播录制
    * 增加YY直播录制
    * 增加花椒直播录制
+ V1.4
    * 增加斗鱼直播录制
+ V1.3
    * 将从html网页里面爬取，改为通过api获取(防止小部分网页正则抠不出来)
    * 将去掉`.part`后缀的动作由 `下载完`之后 改为 `处理完时间戳`之后，防止文件被占用而修改失败
    * 增加主播下拨作为结束条件的情况处理

+ V1.2  
    * 改写FLV文件duration字段
    * 修复Non-monotonous DTS in output stream问题
    * 修改userName的正则匹配
    
+ V1.1      
    * 增加了对FLV文件的时间戳的校正
    * 去掉了FLV文件可能不完整的末尾帧