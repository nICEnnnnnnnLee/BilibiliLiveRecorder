CD /D "%~dp0"
java -Dfile.encoding=utf-8 -cp BiliLiveRecorder.jar nicelee.bilibili.live.check.FlvCheckerWithBufferEx "%~1"
pause