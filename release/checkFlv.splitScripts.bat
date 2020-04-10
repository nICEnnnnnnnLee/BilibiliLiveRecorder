CD /D "%~dp0"
java -Dfile.encoding=utf-8 -cp BiliLiveRecorder.jar nicelee.bilibili.live.check.FlvCheckerWithBufferEx "flv=%~1&debug=false&splitScripts=true&splitAVHeader=true"
pause