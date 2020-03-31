package nicelee.bilibili.live.check;

public class TagOptions {

	
	
	public static int maxAudioHeaderSize = 10;// 根据tag的大小来判断是否是header
	public static int maxVideoHeaderSize = 60;
	
	public Integer tagSize;
	
	public Long pMeta; // 指向tag type后的字节(此时read 3 byte 为data Size)
	
//	public boolean missingAudio = false;
	public Long pAudio; // 指向tag type后的字节(此时read 3 byte 为data Size)
	
//	public boolean missingVideo = false;
	public Long pVideo; // 指向tag type后的字节(此时read 3 byte 为data Size)
}
