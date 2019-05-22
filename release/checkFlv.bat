CD /D "%~dp0"
java -Dfile.encoding=utf-8 -cp BiliLiveRecorder.jar nicelee.bilibili.live.FlvChecker "%~1"
pause