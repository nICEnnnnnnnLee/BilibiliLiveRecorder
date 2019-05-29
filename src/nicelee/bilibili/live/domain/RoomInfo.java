package nicelee.bilibili.live.domain;

import java.lang.reflect.Field;

public class RoomInfo {

	long shortId; // 直播url 里面的id， 可能与roomId相同
	long roomId;
	long userId;  // 主播id
	String userName; // 主播名称
	int liveStatus; // 1 直播; 2 轮播视频; 0 啥也没有播
	String title;
	String description;
	String[] acceptQuality;
	String[] acceptQualityDesc;
	
	String remark; // youyu: 保存加密函数
	
	public String getQualityByName(String qnName) {
		for(int i=0; i<acceptQualityDesc.length; i++ ) {
			if(qnName.equals(acceptQualityDesc[i])) {
				return acceptQuality[i];
			}
		}
		return "0";
	}
	
	public void print() {
		System.out.println("当前Room信息:");
		for(Field f :this.getClass().getDeclaredFields()) {
			try {
				if(f.getType() != String[].class && !f.getName().equals("remark")) {
					System.out.print("\t");
					System.out.println(f.getName() + "\t- " + f.get(this).toString());
				}
			} catch (NullPointerException | IllegalArgumentException | IllegalAccessException e) {
				//e.printStackTrace();
			}
		}
		if(acceptQuality != null) {
			System.out.println("可提供直播质量:");
			for(int i = 0; i < acceptQuality.length; i++) {
				System.out.print("\t");
				System.out.println(acceptQuality[i] + " : " + acceptQualityDesc[i]);
			}
		}
	}
	
	public long getShortId() {
		return shortId;
	}
	public void setShortId(long shortId) {
		this.shortId = shortId;
	}
	public long getRoomId() {
		return roomId;
	}
	public void setRoomId(long roomId) {
		this.roomId = roomId;
	}
	public int getLiveStatus() {
		return liveStatus;
	}
	public void setLiveStatus(int liveStatus) {
		this.liveStatus = liveStatus;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String[] getAcceptQuality() {
		return acceptQuality;
	}
	public void setAcceptQuality(String[] acceptQuality) {
		this.acceptQuality = acceptQuality;
	}
	public String[] getAcceptQualityDesc() {
		return acceptQualityDesc;
	}
	public void setAcceptQualityDesc(String[] acceptQualityDesc) {
		this.acceptQualityDesc = acceptQualityDesc;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public String getUserName() {
		if(userName == null) {
			return "";
		}
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
}
