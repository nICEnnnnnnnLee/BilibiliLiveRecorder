package nicelee.bilibili.live.impl.huya;

import com.qq.taf.jce.JceDisplayer;
import com.qq.taf.jce.JceInputStream;
import com.qq.taf.jce.JceOutputStream;
import com.qq.taf.jce.JceStruct;
import com.qq.taf.jce.JceUtil;

public final class GetCdnTokenExReq extends JceStruct implements Cloneable {

	private static final long serialVersionUID = 3103522758306274500L;
	public String sFlvUrl = "";
	public String sStreamName = "";
	public int iLoopTime = 0;
	public UserId tId = null;

	static UserId cache_tId;
	public int iAppId = 66;

	public GetCdnTokenExReq(String sFlvUrl, String sStreamName) {
		this.tId = new UserId();
		this.sFlvUrl =sFlvUrl;
		this.sStreamName =sStreamName;
	}

	public String className() {
		return "HUYA.GetCdnTokenExReq";
	}

	public void display(StringBuilder var1, int var2) {
		JceDisplayer var3 = new JceDisplayer(var1, var2);
		var3.display(this.sFlvUrl, "sFlvUrl");
		var3.display(this.sStreamName, "sStreamName");
		var3.display(this.tId, "tId");
	}

	public boolean equals(Object var1) {
		if (this != var1) {
			if (var1 != null && this.getClass() == var1.getClass()) {
				GetCdnTokenExReq var2 = (GetCdnTokenExReq) var1;
				if (JceUtil.equals(this.sFlvUrl, var2.sFlvUrl) && JceUtil.equals(this.sStreamName, var2.sStreamName)
						&& JceUtil.equals(this.tId, var2.tId) && JceUtil.equals(this.iAppId, var2.iAppId)) {
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
		return "com.duowan.HUYA.GetCdnTokenExReq";
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
		this.sFlvUrl = var1.read(this.sFlvUrl, 0, false);

		this.sStreamName = var1.read(this.sStreamName, 1, false);
		this.iLoopTime = var1.read(this.iLoopTime, 2, false);
		if (cache_tId == null) {
			cache_tId = new UserId();
		}
		this.tId = (UserId) var1.read(cache_tId, 3, false);
		this.iAppId = var1.read(this.iAppId, 4, false);
	}

	public void writeTo(JceOutputStream var1) {
		var1.write(this.sFlvUrl, 0);
		var1.write(this.sStreamName, 1);
		var1.write(this.iLoopTime, 2);
		var1.write(this.tId, 3);
		var1.write(this.iAppId, 4);
	}
}
