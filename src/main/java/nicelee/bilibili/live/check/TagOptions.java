package nicelee.bilibili.live.check;

public class TagOptions {

	
	
	public static int maxAudioHeaderSize = 10;// 根据tag的大小来判断是否是header
	public static int maxVideoHeaderSize = 60;
	
	public static int contentFramesToSkip = 0; // 在视频开头跳过的内容帧数，针对开头的灰屏、花屏
	public static int maxPeriodBetween2Frame = 5000; // 判断第一帧的时间戳时有用，单位ms
	
	public Integer tagSize;
	
	public Long pMeta; // 指向tag type后的字节(此时read 3 byte 为data Size)
	
//	public boolean missingAudio = false;
	public Long pAudio; // 指向tag type后的字节(此时read 3 byte 为data Size)
	
//	public boolean missingVideo = false;
	public Long pVideo; // 指向tag type后的字节(此时read 3 byte 为data Size)
}
