package com.nho.friend.data;

import org.bson.Document;

import com.nhb.common.db.beans.AbstractMongoBean;
import com.nho.friend.statics.FriendDbFields;
import com.nho.statics.F;

public class FriendMongoBean extends AbstractMongoBean {
	private static final long serialVersionUID = 1L;

	private String user;
	private String buddy;
	private String createdTime = String.valueOf(System.currentTimeMillis());
	private int status;


	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getBuddy() {
		return buddy;
	}

	public void setBuddy(String buddy) {
		this.buddy = buddy;
	}

	public String getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(String createdTime) {
		this.createdTime = createdTime;
	}

	/**
	 * status == 0 : accepted status == 1 : pending status == 2 : ignored status
	 * == 3 : canceled status == 4 : blocked
	 * 
	 * @return status
	 */
	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Document toDocument() {
		Document document = new Document();

		document.put(F.STATUS, this.status);
		document.put(F.CREATED_TIME, this.createdTime);
		document.put(F.USER, this.user);
		document.put(F.BUDDY, this.buddy);

		return document;
	}

	public static FriendMongoBean fromDocument(Document document) {
		FriendMongoBean bean = new FriendMongoBean();

		bean.setObjectId(document.getObjectId(FriendDbFields._ID));
		bean.setCreatedTime(document.getString(F.CREATED_TIME));
		bean.setStatus(document.getInteger(F.STATUS));
		bean.setUser(document.getString(F.USER));
		bean.setBuddy(document.getString(F.BUDDY));

		return bean;
	}
}
