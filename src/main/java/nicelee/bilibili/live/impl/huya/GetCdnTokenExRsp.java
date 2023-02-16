package nicelee.bilibili.live.impl.huya;

import com.qq.taf.jce.JceDisplayer;
import com.qq.taf.jce.JceInputStream;
import com.qq.taf.jce.JceOutputStream;
import com.qq.taf.jce.JceStruct;
import com.qq.taf.jce.JceUtil;

public final class GetCdnTokenExRsp extends JceStruct implements Cloneable {

	private static final long serialVersionUID = 2363417363415873850L;
	public String sFlvToken = "";
	public int iExpireTime = 0;

	public GetCdnTokenExRsp() {
	}

	public String className() {
		return "HUYA.GetCdnTokenExRsp";
	}

	public void display(StringBuilder var1, int var2) {
		JceDisplayer var3 = new JceDisplayer(var1, var2);
		var3.display(this.sFlvToken, "sFlvToken");
		var3.display(this.iExpireTime, "iExpireTime");
	}

	public boolean equals(Object var1) {
		if (this != var1) {
			if (var1 != null && this.getClass() == var1.getClass()) {
				GetCdnTokenExRsp var2 = (GetCdnTokenExRsp) var1;
				if (JceUtil.equals(this.sFlvToken, var2.sFlvToken)
						&& JceUtil.equals(this.iExpireTime, var2.iExpireTime)) {
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
		return "com.duowan.HUYA.GetCdnTokenExRsp";
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
		this.sFlvToken = var1.read(this.sFlvToken, 0, false);
		this.iExpireTime = var1.read(this.iExpireTime, 1, false);
	}

	public void writeTo(JceOutputStream var1) {
		var1.write(this.sFlvToken, 0);
		var1.write(this.iExpireTime, 1);
	}
}
