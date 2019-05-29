CD /D "%~dp0"
ffmpeg -i "%~1" -vcodec copy -acodec copy "%~dpn1_flv.flv"
pause
end