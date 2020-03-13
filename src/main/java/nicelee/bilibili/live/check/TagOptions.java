package nicelee.bilibili.live.check;

public class TagOptions {

	
	public Integer tagSize;
	
	public Long pMeta; // 指向tag type后的字节(此时read 3 byte 为data Size)
	
//	public boolean missingAudio = false;
	public Long pAudio; // 指向tag type后的字节(此时read 3 byte 为data Size)
	
//	public boolean missingVideo = false;
	public Long pVideo; // 指向tag type后的字节(此时read 3 byte 为data Size)
}
