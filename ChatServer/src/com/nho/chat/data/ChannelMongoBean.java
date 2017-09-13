package com.nho.chat.data;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import com.mongodb.BasicDBList;
import com.nhb.common.db.beans.AbstractMongoBean;
import com.nho.chat.statics.ChannelDBField;
import com.nho.statics.ChannelType;

public class ChannelMongoBean extends AbstractMongoBean {
	private static final long serialVersionUID = 1L;

	private ChannelType type;
	private List<UserInChannelBean> users;
	private String id;
	private String lastTime = String.valueOf(System.currentTimeMillis());
	private int times = 1;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public ChannelType getType() {
		return type;
	}

	public void setType(ChannelType type) {
		this.type = type;
	}

	@Override
	public Document toDocument() {
		Document document = new Document();
		List<Object> users = new BasicDBList();
		for (UserInChannelBean user : this.getUsers()) {
			users.add(user.toDocument());
		}
		document.put(ChannelDBField.USERS, users);
		document.put(ChannelDBField.TYPE, this.type.ordinal());
		document.put(ChannelDBField.ID, this.id);
		document.put(ChannelDBField.LAST_TIME, this.getLastTime());
		document.put(ChannelDBField.TIMES, this.times);
		return document;
	}

	public static ChannelMongoBean fromDocument(Document document) {
		ChannelMongoBean channel = new ChannelMongoBean();
		channel.setObjectId(document.getObjectId(ChannelDBField._ID));

		List<UserInChannelBean> users = new ArrayList<>();
		@SuppressWarnings("unchecked")
		List<Document> userDoc = (List<Document>) document.get(ChannelDBField.USERS);
		if(userDoc != null){
			for(Document doc : userDoc){
				users.add(UserInChannelBean.fromDocument(doc));
			}
		}else {
			System.out.println("user doc null");
		}
		channel.setUsers(users);
		channel.setType(ChannelType.fromCode(document.getInteger(ChannelDBField.TYPE)));
		channel.setId(document.getString(ChannelDBField.ID));
		if (document.containsKey(ChannelDBField.LAST_TIME)) {
			channel.setLastTime(document.getString(ChannelDBField.LAST_TIME));
		} else {
			channel.setLastTime(String.valueOf(System.currentTimeMillis()));
		}
		if (document.containsKey(ChannelDBField.TIMES)) {
			channel.setTimes(document.getInteger(ChannelDBField.TIMES));
		} else {
			channel.setTimes(0);
		}
		return channel;
	}


	public int getTimes() {
		return times;
	}

	public void setTimes(int times) {
		this.times = times;
	}

	public List<UserInChannelBean> getUsers() {
		return users;
	}

	public void setUsers(List<UserInChannelBean> users) {
		this.users = users;
	}

	public String getLastTime() {
		return lastTime;
	}

	public void setLastTime(String lastTime) {
		this.lastTime = lastTime;
	}

}
