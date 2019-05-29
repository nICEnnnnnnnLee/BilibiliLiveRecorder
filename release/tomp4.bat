CD /D "%~dp0"
ffmpeg -i "%~1" -vcodec copy -acodec copy "%~dpn1-.mp4"
pause
end