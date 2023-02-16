package nicelee.bilibili.live.impl.huya;

import com.qq.taf.jce.*;

import java.util.ArrayList;

public final class BeginLiveNotice extends JceStruct implements Cloneable {
	private static final long serialVersionUID = -7996246683515055902L;
	static ArrayList<String> cache_vCdnList;
	static ArrayList<MultiStreamInfo> cache_vMultiStreamInfo;
	static ArrayList<StreamInfo> cache_vStreamInfo;
	public int iCdnPolicyLevel = 0;
	public int iCodecType = 0;
	public int iGameId = 0;
	public int iMobileDefaultBitRate = 0;
	public int iPCDefaultBitRate = 0;
	public int iRandomRange = 0;
	public int iScreenType = 0;
	public int iSourceType = 0;
	public int iStartTime = 0;
	public int iStreamType = 0;
	public int iWebDefaultBitRate = 0;
	public long lAttendeeCount = 0L;
	public long lChannelId = 0L;
	public long lLiveCompatibleFlag = 0L;
	public long lLiveId = 0L;
	public long lMultiStreamFlag = 0L;
	public long lPresenterUid = 0L;
	public long lSubChannelId = 0L;
	public long lYYId = 0L;
	public String sAvatarUrl = "";
	public String sGameName = "";
	public String sLiveDesc = "";
	public String sLocation = "";
	public String sNick = "";
	public String sSubchannelName = "";
	public String sVideoCaptureUrl = "";
	public ArrayList<String> vCdnList = null;
	public ArrayList<MultiStreamInfo> vMultiStreamInfo = null;
	public ArrayList<StreamInfo> vStreamInfo = null;

	public BeginLiveNotice() {
	}

	public BeginLiveNotice(long var1, int var3, String var4, int var5, int var6, ArrayList<StreamInfo> var7,
			ArrayList<String> var8, long var9, int var11, int var12, int var13, long var14, String var16, long var17,
			long var19, int var21, int var22, ArrayList<MultiStreamInfo> var23, String var24, long var25, String var27,
			int var28, String var29, String var30, int var31, long var32, long var34, String var36, int var37) {
		this.lPresenterUid = var1;
		this.iGameId = var3;
		this.sGameName = var4;
		this.iRandomRange = var5;
		this.iStreamType = var6;
		this.vStreamInfo = var7;
		this.vCdnList = var8;
		this.lLiveId = var9;
		this.iPCDefaultBitRate = var11;
		this.iWebDefaultBitRate = var12;
		this.iMobileDefaultBitRate = var13;
		this.lMultiStreamFlag = var14;
		this.sNick = var16;
		this.lYYId = var17;
		this.lAttendeeCount = var19;
		this.iCodecType = var21;
		this.iScreenType = var22;
		this.vMultiStreamInfo = var23;
		this.sLiveDesc = var24;
		this.lLiveCompatibleFlag = var25;
		this.sAvatarUrl = var27;
		this.iSourceType = var28;
		this.sSubchannelName = var29;
		this.sVideoCaptureUrl = var30;
		this.iStartTime = var31;
		this.lChannelId = var32;
		this.lSubChannelId = var34;
		this.sLocation = var36;
		this.iCdnPolicyLevel = var37;
	}

	public String className() {
		return "HUYA.BeginLiveNotice";
	}

	public void display(StringBuilder var1, int var2) {
		JceDisplayer var3 = new JceDisplayer(var1, var2);
		var3.display(this.lPresenterUid, "lPresenterUid");
		var3.display(this.iGameId, "iGameId");
		var3.display(this.sGameName, "sGameName");
		var3.display(this.iRandomRange, "iRandomRange");
		var3.display(this.iStreamType, "iStreamType");
		var3.display(this.vStreamInfo, "vStreamInfo");
		var3.display(this.vCdnList, "vCdnList");
		var3.display(this.lLiveId, "lLiveId");
		var3.display(this.iPCDefaultBitRate, "iPCDefaultBitRate");
		var3.display(this.iWebDefaultBitRate, "iWebDefaultBitRate");
		var3.display(this.iMobileDefaultBitRate, "iMobileDefaultBitRate");
		var3.display(this.lMultiStreamFlag, "lMultiStreamFlag");
		var3.display(this.sNick, "sNick");
		var3.display(this.lYYId, "lYYId");
		var3.display(this.lAttendeeCount, "lAttendeeCount");
		var3.display(this.iCodecType, "iCodecType");
		var3.display(this.iScreenType, "iScreenType");
		var3.display(this.vMultiStreamInfo, "vMultiStreamInfo");
		var3.display(this.sLiveDesc, "sLiveDesc");
		var3.display(this.lLiveCompatibleFlag, "lLiveCompatibleFlag");
		var3.display(this.sAvatarUrl, "sAvatarUrl");
		var3.display(this.iSourceType, "iSourceType");
		var3.display(this.sSubchannelName, "sSubchannelName");
		var3.display(this.sVideoCaptureUrl, "sVideoCaptureUrl");
		var3.display(this.iStartTime, "iStartTime");
		var3.display(this.lChannelId, "lChannelId");
		var3.display(this.lSubChannelId, "lSubChannelId");
		var3.display(this.sLocation, "sLocation");
		var3.display(this.iCdnPolicyLevel, "iCdnPolicyLevel");
	}

	public boolean equals(Object var1) {
		if (this != var1) {
			if (var1 != null && this.getClass() == var1.getClass()) {
				BeginLiveNotice var2 = (BeginLiveNotice) var1;
				if (JceUtil.equals(this.lPresenterUid, var2.lPresenterUid) && JceUtil.equals(this.iGameId, var2.iGameId)
						&& JceUtil.equals(this.sGameName, var2.sGameName)
						&& JceUtil.equals(this.iRandomRange, var2.iRandomRange)
						&& JceUtil.equals(this.iStreamType, var2.iStreamType)
						&& JceUtil.equals(this.vStreamInfo, var2.vStreamInfo)
						&& JceUtil.equals(this.vCdnList, var2.vCdnList) && JceUtil.equals(this.lLiveId, var2.lLiveId)
						&& JceUtil.equals(this.iPCDefaultBitRate, var2.iPCDefaultBitRate)
						&& JceUtil.equals(this.iWebDefaultBitRate, var2.iWebDefaultBitRate)
						&& JceUtil.equals(this.iMobileDefaultBitRate, var2.iMobileDefaultBitRate)
						&& JceUtil.equals(this.lMultiStreamFlag, var2.lMultiStreamFlag)
						&& JceUtil.equals(this.sNick, var2.sNick) && JceUtil.equals(this.lYYId, var2.lYYId)
						&& JceUtil.equals(this.lAttendeeCount, var2.lAttendeeCount)
						&& JceUtil.equals(this.iCodecType, var2.iCodecType)
						&& JceUtil.equals(this.iScreenType, var2.iScreenType)
						&& JceUtil.equals(this.vMultiStreamInfo, var2.vMultiStreamInfo)
						&& JceUtil.equals(this.sLiveDesc, var2.sLiveDesc)
						&& JceUtil.equals(this.lLiveCompatibleFlag, var2.lLiveCompatibleFlag)
						&& JceUtil.equals(this.sAvatarUrl, var2.sAvatarUrl)
						&& JceUtil.equals(this.iSourceType, var2.iSourceType)
						&& JceUtil.equals(this.sSubchannelName, var2.sSubchannelName)
						&& JceUtil.equals(this.sVideoCaptureUrl, var2.sVideoCaptureUrl)
						&& JceUtil.equals(this.iStartTime, var2.iStartTime)
						&& JceUtil.equals(this.lChannelId, var2.lChannelId)
						&& JceUtil.equals(this.lSubChannelId, var2.lSubChannelId)
						&& JceUtil.equals(this.sLocation, var2.sLocation)
						&& JceUtil.equals(this.iCdnPolicyLevel, var2.iCdnPolicyLevel)) {
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
		return "com.duowan.HUYA.BeginLiveNotice";
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
		this.iGameId = var1.read(this.iGameId, 1, false);
		this.sGameName = var1.readString(2, false);
		this.iRandomRange = var1.read(this.iRandomRange, 3, false);
		this.iStreamType = var1.read(this.iStreamType, 4, false);
		if (cache_vStreamInfo == null) {
			cache_vStreamInfo = new ArrayList<StreamInfo>();
			StreamInfo var2 = new StreamInfo();
			cache_vStreamInfo.add(var2);
		}

		this.vStreamInfo = (ArrayList<StreamInfo>) var1.read(cache_vStreamInfo, 5, false);
		if (cache_vCdnList == null) {
			cache_vCdnList = new ArrayList<String>();
			cache_vCdnList.add("");
		}

		this.vCdnList = (ArrayList<String>) var1.read(cache_vCdnList, 6, false);
		this.lLiveId = var1.read(this.lLiveId, 7, false);
		this.iPCDefaultBitRate = var1.read(this.iPCDefaultBitRate, 8, false);
		this.iWebDefaultBitRate = var1.read(this.iWebDefaultBitRate, 9, false);
		this.iMobileDefaultBitRate = var1.read(this.iMobileDefaultBitRate, 10, false);
		this.lMultiStreamFlag = var1.read(this.lMultiStreamFlag, 11, false);
		this.sNick = var1.readString(12, false);
		this.lYYId = var1.read(this.lYYId, 13, false);
		this.lAttendeeCount = var1.read(this.lAttendeeCount, 14, false);
		this.iCodecType = var1.read(this.iCodecType, 15, false);
		this.iScreenType = var1.read(this.iScreenType, 16, false);
		if (cache_vMultiStreamInfo == null) {
			cache_vMultiStreamInfo = new ArrayList<MultiStreamInfo>();
			MultiStreamInfo var3 = new MultiStreamInfo();
			cache_vMultiStreamInfo.add(var3);
		}

		this.vMultiStreamInfo = (ArrayList<MultiStreamInfo>) var1.read(cache_vMultiStreamInfo, 17, false);
		this.sLiveDesc = var1.readString(18, false);
		this.lLiveCompatibleFlag = var1.read(this.lLiveCompatibleFlag, 19, false);
		this.sAvatarUrl = var1.readString(20, false);
		this.iSourceType = var1.read(this.iSourceType, 21, false);
		this.sSubchannelName = var1.readString(22, false);
		this.sVideoCaptureUrl = var1.readString(23, false);
		this.iStartTime = var1.read(this.iStartTime, 24, false);
		this.lChannelId = var1.read(this.lChannelId, 25, false);
		this.lSubChannelId = var1.read(this.lSubChannelId, 26, false);
		this.sLocation = var1.readString(27, false);
		this.iCdnPolicyLevel = var1.read(this.iCdnPolicyLevel, 28, false);
	}

	public void writeTo(JceOutputStream var1) {
		var1.write(this.lPresenterUid, 0);
		var1.write(this.iGameId, 1);
		if (this.sGameName != null) {
			var1.write(this.sGameName, 2);
		}

		var1.write(this.iRandomRange, 3);
		var1.write(this.iStreamType, 4);
		if (this.vStreamInfo != null) {
			var1.write(this.vStreamInfo, 5);
		}

		if (this.vCdnList != null) {
			var1.write(this.vCdnList, 6);
		}

		var1.write(this.lLiveId, 7);
		var1.write(this.iPCDefaultBitRate, 8);
		var1.write(this.iWebDefaultBitRate, 9);
		var1.write(this.iMobileDefaultBitRate, 10);
		var1.write(this.lMultiStreamFlag, 11);
		if (this.sNick != null) {
			var1.write(this.sNick, 12);
		}

		var1.write(this.lYYId, 13);
		var1.write(this.lAttendeeCount, 14);
		var1.write(this.iCodecType, 15);
		var1.write(this.iScreenType, 16);
		if (this.vMultiStreamInfo != null) {
			var1.write(this.vMultiStreamInfo, 17);
		}

		if (this.sLiveDesc != null) {
			var1.write(this.sLiveDesc, 18);
		}

		var1.write(this.lLiveCompatibleFlag, 19);
		if (this.sAvatarUrl != null) {
			var1.write(this.sAvatarUrl, 20);
		}

		var1.write(this.iSourceType, 21);
		if (this.sSubchannelName != null) {
			var1.write(this.sSubchannelName, 22);
		}

		if (this.sVideoCaptureUrl != null) {
			var1.write(this.sVideoCaptureUrl, 23);
		}

		var1.write(this.iStartTime, 24);
		var1.write(this.lChannelId, 25);
		var1.write(this.lSubChannelId, 26);
		if (this.sLocation != null) {
			var1.write(this.sLocation, 27);
		}

		var1.write(this.iCdnPolicyLevel, 28);
	}
}
