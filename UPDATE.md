## 更新
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