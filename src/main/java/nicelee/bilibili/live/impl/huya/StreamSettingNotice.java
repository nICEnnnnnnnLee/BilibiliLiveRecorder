package nicelee.bilibili.live.impl.huya;

import com.qq.taf.jce.*;

public final class StreamSettingNotice extends JceStruct implements Cloneable {
	private static final long serialVersionUID = -4125719629250962236L;
	public int iBitRate = 0;
	public int iFrameRate = 0;
	public int iResolution = 0;
	public long lLiveId = 0L;
	public long lPresenterUid = 0L;
	public String sDisplayName = "";

	public StreamSettingNotice() {
	}

	public StreamSettingNotice(long var1, int var3, int var4, int var5, long var6, String var8) {
		this.lPresenterUid = var1;
		this.iBitRate = var3;
		this.iResolution = var4;
		this.iFrameRate = var5;
		this.lLiveId = var6;
		this.sDisplayName = var8;
	}

	public String className() {
		return "HUYA.StreamSettingNotice";
	}

	public void display(StringBuilder var1, int var2) {
		JceDisplayer var3 = new JceDisplayer(var1, var2);
		var3.display(this.lPresenterUid, "lPresenterUid");
		var3.display(this.iBitRate, "iBitRate");
		var3.display(this.iResolution, "iResolution");
		var3.display(this.iFrameRate, "iFrameRate");
		var3.display(this.lLiveId, "lLiveId");
		var3.display(this.sDisplayName, "sDisplayName");
	}

	public boolean equals(Object var1) {
		if (this != var1) {
			if (var1 != null && this.getClass() == var1.getClass()) {
				StreamSettingNotice var2 = (StreamSettingNotice) var1;
				if (JceUtil.equals(this.lPresenterUid, var2.lPresenterUid)
						&& JceUtil.equals(this.iBitRate, var2.iBitRate)
						&& JceUtil.equals(this.iResolution, var2.iResolution)
						&& JceUtil.equals(this.iFrameRate, var2.iFrameRate)
						&& JceUtil.equals(this.lLiveId, var2.lLiveId)
						&& JceUtil.equals(this.sDisplayName, var2.sDisplayName)) {
					return true;
				} else {
					return false;
				}
			} else {
				return false;
			}
		} else {
			return true;
		}
	}

	public String fullClassName() {
		return "com.duowan.HUYA.StreamSettingNotice";
	}

	public int hashCode() {
		try {
			throw new Exception("Need define key first!");
		} catch (Exception var2) {
			var2.printStackTrace();
			return 0;
		}
	}

	public void readFrom(JceInputStream var1) {
		this.lPresenterUid = var1.read(this.lPresenterUid, 0, false);
		this.iBitRate = var1.read(this.iBitRate, 1, false);
		this.iResolution = var1.read(this.iResolution, 2, false);
		this.iFrameRate = var1.read(this.iFrameRate, 3, false);
		this.lLiveId = var1.read(this.lLiveId, 4, false);
		this.sDisplayName = var1.readString(5, false);
	}

	public void writeTo(JceOutputStream var1) {
		var1.write(this.lPresenterUid, 0);
		var1.write(this.iBitRate, 1);
		var1.write(this.iResolution, 2);
		var1.write(this.iFrameRate, 3);
		var1.write(this.lLiveId, 4);
		if (this.sDisplayName != null) {
			var1.write(this.sDisplayName, 5);
		}

	}
}