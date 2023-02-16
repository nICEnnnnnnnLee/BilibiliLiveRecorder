package nicelee.bilibili.live.impl.huya;

import com.qq.taf.jce.*;

public final class StreamInfo extends JceStruct implements Cloneable {
	private static final long serialVersionUID = 1854738550457059651L;
	public int iIsMaster = 0;
	public int iIsMultiStream = 0;
	public int iLineIndex = 0;
	public int iMobilePriorityRate = 0;
	public int iPCPriorityRate = 0;
	public int iWebPriorityRate = 0;
	public long lChannelId = 0L;
	public long lPresenterUid = 0L;
	public long lSubChannelId = 0L;
	public String sCdnType = "";
	public String sFlvAntiCode = "";
	public String sFlvUrl = "";
	public String sFlvUrlSuffix = "";
	public String sHlsAntiCode = "";
	public String sHlsUrl = "";
	public String sHlsUrlSuffix = "";
	public String sStreamName = "";

	public StreamInfo() {
	}

	public StreamInfo(String var1, int var2, long var3, long var5, long var7, String var9, String var10, String var11,
			String var12, String var13, String var14, String var15, int var16, int var17, int var18, int var19,
			int var20) {
		this.sCdnType = var1;
		this.iIsMaster = var2;
		this.lChannelId = var3;
		this.lSubChannelId = var5;
		this.lPresenterUid = var7;
		this.sStreamName = var9;
		this.sFlvUrl = var10;
		this.sFlvUrlSuffix = var11;
		this.sFlvAntiCode = var12;
		this.sHlsUrl = var13;
		this.sHlsUrlSuffix = var14;
		this.sHlsAntiCode = var15;
		this.iLineIndex = var16;
		this.iIsMultiStream = var17;
		this.iPCPriorityRate = var18;
		this.iWebPriorityRate = var19;
		this.iMobilePriorityRate = var20;
	}

	public String className() {
		return "HUYA.StreamInfo";
	}

	public void display(StringBuilder var1, int var2) {
		JceDisplayer var3 = new JceDisplayer(var1, var2);
		var3.display(this.sCdnType, "sCdnType");
		var3.display(this.iIsMaster, "iIsMaster");
		var3.display(this.lChannelId, "lChannelId");
		var3.display(this.lSubChannelId, "lSubChannelId");
		var3.display(this.lPresenterUid, "lPresenterUid");
		var3.display(this.sStreamName, "sStreamName");
		var3.display(this.sFlvUrl, "sFlvUrl");
		var3.display(this.sFlvUrlSuffix, "sFlvUrlSuffix");
		var3.display(this.sFlvAntiCode, "sFlvAntiCode");
		var3.display(this.sHlsUrl, "sHlsUrl");
		var3.display(this.sHlsUrlSuffix, "sHlsUrlSuffix");
		var3.display(this.sHlsAntiCode, "sHlsAntiCode");
		var3.display(this.iLineIndex, "iLineIndex");
		var3.display(this.iIsMultiStream, "iIsMultiStream");
		var3.display(this.iPCPriorityRate, "iPCPriorityRate");
		var3.display(this.iWebPriorityRate, "iWebPriorityRate");
		var3.display(this.iMobilePriorityRate, "iMobilePriorityRate");
	}

	public boolean equals(Object var1) {
		if (this != var1) {
			if (var1 != null && this.getClass() == var1.getClass()) {
				StreamInfo var2 = (StreamInfo) var1;
				if (JceUtil.equals(this.sCdnType, var2.sCdnType) && JceUtil.equals(this.iIsMaster, var2.iIsMaster)
						&& JceUtil.equals(this.lChannelId, var2.lChannelId)
						&& JceUtil.equals(this.lSubChannelId, var2.lSubChannelId)
						&& JceUtil.equals(this.lPresenterUid, var2.lPresenterUid)
						&& JceUtil.equals(this.sStreamName, var2.sStreamName)
						&& JceUtil.equals(this.sFlvUrl, var2.sFlvUrl)
						&& JceUtil.equals(this.sFlvUrlSuffix, var2.sFlvUrlSuffix)
						&& JceUtil.equals(this.sFlvAntiCode, var2.sFlvAntiCode)
						&& JceUtil.equals(this.sHlsUrl, var2.sHlsUrl)
						&& JceUtil.equals(this.sHlsUrlSuffix, var2.sHlsUrlSuffix)
						&& JceUtil.equals(this.sHlsAntiCode, var2.sHlsAntiCode)
						&& JceUtil.equals(this.iLineIndex, var2.iLineIndex)
						&& JceUtil.equals(this.iIsMultiStream, var2.iIsMultiStream)
						&& JceUtil.equals(this.iPCPriorityRate, var2.iPCPriorityRate)
						&& JceUtil.equals(this.iWebPriorityRate, var2.iWebPriorityRate)
						&& JceUtil.equals(this.iMobilePriorityRate, var2.iMobilePriorityRate)) {
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
		return "com.duowan.HUYA.StreamInfo";
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
		this.sCdnType = var1.readString(0, false);
		this.iIsMaster = var1.read(this.iIsMaster, 1, false);
		this.lChannelId = var1.read(this.lChannelId, 2, false);
		this.lSubChannelId = var1.read(this.lSubChannelId, 3, false);
		this.lPresenterUid = var1.read(this.lPresenterUid, 4, false);
		this.sStreamName = var1.readString(5, false);
		this.sFlvUrl = var1.readString(6, false);
		this.sFlvUrlSuffix = var1.readString(7, false);
		this.sFlvAntiCode = var1.readString(8, false);
		this.sHlsUrl = var1.readString(9, false);
		this.sHlsUrlSuffix = var1.readString(10, false);
		this.sHlsAntiCode = var1.readString(11, false);
		this.iLineIndex = var1.read(this.iLineIndex, 12, false);
		this.iIsMultiStream = var1.read(this.iIsMultiStream, 13, false);
		this.iPCPriorityRate = var1.read(this.iPCPriorityRate, 14, false);
		this.iWebPriorityRate = var1.read(this.iWebPriorityRate, 15, false);
		this.iMobilePriorityRate = var1.read(this.iMobilePriorityRate, 16, false);
	}

	public void writeTo(JceOutputStream var1) {
		if (this.sCdnType != null) {
			var1.write(this.sCdnType, 0);
		}

		var1.write(this.iIsMaster, 1);
		var1.write(this.lChannelId, 2);
		var1.write(this.lSubChannelId, 3);
		var1.write(this.lPresenterUid, 4);
		if (this.sStreamName != null) {
			var1.write(this.sStreamName, 5);
		}

		if (this.sFlvUrl != null) {
			var1.write(this.sFlvUrl, 6);
		}

		if (this.sFlvUrlSuffix != null) {
			var1.write(this.sFlvUrlSuffix, 7);
		}

		if (this.sFlvAntiCode != null) {
			var1.write(this.sFlvAntiCode, 8);
		}

		if (this.sHlsUrl != null) {
			var1.write(this.sHlsUrl, 9);
		}

		if (this.sHlsUrlSuffix != null) {
			var1.write(this.sHlsUrlSuffix, 10);
		}

		if (this.sHlsAntiCode != null) {
			var1.write(this.sHlsAntiCode, 11);
		}

		var1.write(this.iLineIndex, 12);
		var1.write(this.iIsMultiStream, 13);
		var1.write(this.iPCPriorityRate, 14);
		var1.write(this.iWebPriorityRate, 15);
		var1.write(this.iMobilePriorityRate, 16);
	}
}