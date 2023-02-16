package nicelee.bilibili.live.impl.huya;

import com.qq.taf.jce.*;

public final class MultiStreamInfo extends JceStruct implements Cloneable {
	private static final long serialVersionUID = -7711780400080708954L;
	public int iBitRate = 0;
	public int iCodecType = 0;
	public int iCompatibleFlag = 0;
	public String sDisplayName = "";

	public MultiStreamInfo() {
	}

	public MultiStreamInfo(String var1, int var2, int var3, int var4) {
		this.sDisplayName = var1;
		this.iBitRate = var2;
		this.iCodecType = var3;
		this.iCompatibleFlag = var4;
	}

	public String className() {
		return "HUYA.MultiStreamInfo";
	}

	public void display(StringBuilder var1, int var2) {
		JceDisplayer var3 = new JceDisplayer(var1, var2);
		var3.display(this.sDisplayName, "sDisplayName");
		var3.display(this.iBitRate, "iBitRate");
		var3.display(this.iCodecType, "iCodecType");
		var3.display(this.iCompatibleFlag, "iCompatibleFlag");
	}

	public boolean equals(Object var1) {
		if (this != var1) {
			if (var1 != null && this.getClass() == var1.getClass()) {
				MultiStreamInfo var2 = (MultiStreamInfo) var1;
				if (JceUtil.equals(this.sDisplayName, var2.sDisplayName) && JceUtil.equals(this.iBitRate, var2.iBitRate)
						&& JceUtil.equals(this.iCodecType, var2.iCodecType)
						&& JceUtil.equals(this.iCompatibleFlag, var2.iCompatibleFlag)) {
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
		return "com.duowan.HUYA.MultiStreamInfo";
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
		this.sDisplayName = var1.readString(0, false);
		this.iBitRate = var1.read(this.iBitRate, 1, false);
		this.iCodecType = var1.read(this.iCodecType, 2, false);
		this.iCompatibleFlag = var1.read(this.iCompatibleFlag, 3, false);
	}

	public void writeTo(JceOutputStream var1) {
		if (this.sDisplayName != null) {
			var1.write(this.sDisplayName, 0);
		}

		var1.write(this.iBitRate, 1);
		var1.write(this.iCodecType, 2);
		var1.write(this.iCompatibleFlag, 3);
	}
}
