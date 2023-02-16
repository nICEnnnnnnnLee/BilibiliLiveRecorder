package nicelee.bilibili.live.impl.huya;

import com.qq.taf.jce.*;

public final class GetLivingInfoRsp extends JceStruct implements Cloneable {
	private static final long serialVersionUID = 911028104728966078L;
	static BeginLiveNotice cache_tNotice;
	static StreamSettingNotice cache_tStreamSettingNotice;
	public int bIsLiving = -1;
	public int bIsSelfLiving = -1;
	public BeginLiveNotice tNotice = null;
	public StreamSettingNotice tStreamSettingNotice = null;

	public GetLivingInfoRsp() {
	}

	public GetLivingInfoRsp(int var1, BeginLiveNotice var2, StreamSettingNotice var3, int var4) {
		this.bIsLiving = var1;
		this.tNotice = var2;
		this.tStreamSettingNotice = var3;
		this.bIsSelfLiving = var4;
	}

	public String className() {
		return "HUYA.GetLivingInfoRsp";
	}

	public void display(StringBuilder var1, int var2) {
		JceDisplayer var3 = new JceDisplayer(var1, var2);
		var3.display(this.bIsLiving, "bIsLiving");
		var3.display(this.tNotice, "tNotice");
		var3.display(this.tStreamSettingNotice, "tStreamSettingNotice");
		var3.display(this.bIsSelfLiving, "bIsSelfLiving");
	}

	public boolean equals(Object var1) {
		if (this != var1) {
			if (var1 != null && this.getClass() == var1.getClass()) {
				GetLivingInfoRsp var2 = (GetLivingInfoRsp) var1;
				if (JceUtil.equals(this.bIsLiving, var2.bIsLiving) && JceUtil.equals(this.tNotice, var2.tNotice)
						&& JceUtil.equals(this.tStreamSettingNotice, var2.tStreamSettingNotice)
						&& JceUtil.equals(this.bIsSelfLiving, var2.bIsSelfLiving)) {
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
		return "com.duowan.HUYA.GetLivingInfoRsp";
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
		this.bIsLiving = var1.read(this.bIsLiving, 0, false);
		if (cache_tNotice == null) {
			cache_tNotice = new BeginLiveNotice();
		}

		this.tNotice = (BeginLiveNotice) var1.read(cache_tNotice, 1, false);
		if (cache_tStreamSettingNotice == null) {
			cache_tStreamSettingNotice = new StreamSettingNotice();
		}

		this.tStreamSettingNotice = (StreamSettingNotice) var1.read(cache_tStreamSettingNotice, 2, false);
		this.bIsSelfLiving = var1.read(this.bIsSelfLiving, 3, false);
	}

	public void writeTo(JceOutputStream var1) {
		var1.write(this.bIsLiving, 0);
		if (this.tNotice != null) {
			var1.write(this.tNotice, 1);
		}

		if (this.tStreamSettingNotice != null) {
			var1.write(this.tStreamSettingNotice, 2);
		}

		var1.write(this.bIsSelfLiving, 3);
	}
}
