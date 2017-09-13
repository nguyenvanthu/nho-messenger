package com.nho.chat.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

import org.bson.Document;

import com.hazelcast.core.IMap;
import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.UpdateResult;
import com.nhb.common.db.models.AbstractModel;
import com.nho.chat.statics.ChannelDBField;
import com.nho.statics.ChannelType;
import com.nho.statics.F;
import com.nho.statics.Personality;
import com.nho.statics.Theme;

public class ChannelMongoModel extends AbstractModel {
	public static final String CHANNEL_MAP_KEY = "nho:channels";

	private IMap<String, ChannelMongoBean> getMap() {
		return this.getHazelcast().getMap(CHANNEL_MAP_KEY);
	}

	public ChannelMongoBean getPrivateChannelFor(String userName1, String userName2,Theme user1Theme,Personality user1Personality) {
		ChannelMongoBean channel = new ChannelMongoBean();
		channel.setType(ChannelType.PRIVATE);
		channel.setId(UUID.randomUUID().toString());
		channel.setLastTime(String.valueOf(System.currentTimeMillis()));
		channel.setTimes(1);

		List<UserInChannelBean> users = new ArrayList<>();
		UserInChannelBean user1 = new UserInChannelBean();
		user1.setPersonality(user1Personality);
		user1.setTheme(user1Theme);;
		user1.setUserName(userName1);
		users.add(user1);
		users.add(getDefaulUserInChannel(userName2));
		channel.setUsers(users);

		return channel;
	}

	private UserInChannelBean getDefaulUserInChannel(String userName) {
		UserInChannelBean userBean = new UserInChannelBean();
		userBean.setUserName(userName);
		userBean.setPersonality(Personality.SERIOUS);
		userBean.setTheme(Theme.TEAL);

		return userBean;
	}

	private int getTimesUseChannel(String channelId) {
		int times = 0;
		ChannelMongoBean bean = findChannelById(channelId);
		if (bean != null) {
			times = bean.getTimes();
		}
		return times;
	}

	public ChannelMongoBean getChannelForBot(String userName) {
		ChannelMongoBean channel = new ChannelMongoBean();
		channel.setType(ChannelType.PRIVATE);
		channel.setId("bot_" + UUID.randomUUID().toString());
		List<UserInChannelBean> users = new ArrayList<>();
		users.add(getDefaulUserInChannel(userName));
		users.add(getDefaulUserInChannel("bot"));
		channel.setUsers(users);

		return channel;
	}

	public boolean insertChannel(ChannelMongoBean channel) {
		boolean isSuccessful = false;
		MongoCollection<Document> channelCollection = this.getMongoClient().getDatabase(F.NHO_DATABASE)
				.getCollection(ChannelDBField.CHANNEL_COLLECTION);

		Document document = channel.toDocument();
		if (document != null) {
			channelCollection.insertOne(document);
			isSuccessful = true;
		}
		return isSuccessful;
	}

	public List<ChannelMongoBean> findChannelByName(String channelName) {
		List<ChannelMongoBean> result = new ArrayList<>();

		MongoCollection<Document> channelCollection = this.getMongoClient().getDatabase(F.NHO_DATABASE)
				.getCollection(ChannelDBField.CHANNEL_COLLECTION);

		BasicDBObject obj = new BasicDBObject();
		obj.append(ChannelDBField.NAME, channelName);

		FindIterable<Document> found = channelCollection.find(obj);
		for (Document doc : found) {
			result.add(ChannelMongoBean.fromDocument(doc));
		}

		return result;
	}

	public ChannelMongoBean findChannelById(String channelId) {
		MongoCollection<Document> channelCollection = this.getMongoClient().getDatabase(F.NHO_DATABASE)
				.getCollection(ChannelDBField.CHANNEL_COLLECTION);
		IMap<String, ChannelMongoBean> channelCache = getMap();
		if (channelCache.containsKey(channelId)) {
			return channelCache.get(channelId);
		} else {
			BasicDBObject obj = new BasicDBObject();
			obj.append(ChannelDBField.ID, channelId);
			try {
				FindIterable<Document> found = channelCollection.find(obj);
				if (found.first() != null) {
					ChannelMongoBean result = ChannelMongoBean.fromDocument(found.first());
					channelCache.put(result.getId(), result);
					return result;
				} else {
					return null;
				}
			} catch (Exception exception) {
				getLogger().debug("error when get channel " + exception);
			}
			return null;
		}
	}

	public List<ChannelMongoBean> findChannelByListUser(List<String> users) {
		Collections.sort(users);
		List<ChannelMongoBean> result = new ArrayList<>();
		MongoCollection<Document> channelCollection = this.getMongoClient().getDatabase(F.NHO_DATABASE)
				.getCollection(ChannelDBField.CHANNEL_COLLECTION);
		IMap<String, ChannelMongoBean> channelCache = getMap();
		if (getFromChannelCache(users, getMap()).size() > 0) {
			return getFromChannelCache(users, getMap());
		} else {
			FindIterable<Document> found = channelCollection.find();
			for (Document doc : found) {
				ChannelMongoBean bean = ChannelMongoBean.fromDocument(doc);
				List<String> usersInChannel = new ArrayList<>();
				for (UserInChannelBean userBean : bean.getUsers()) {
					usersInChannel.add(userBean.getUserName());
				}
				Collections.sort(usersInChannel);
				if (usersInChannel.equals(users)) {
					result.add(bean);
					channelCache.put(bean.getId(), bean);
				}
			}
			getLogger().debug("number channel found is " + result.size());
			return result;
		}
	}

	private List<ChannelMongoBean> getFromChannelCache(List<String> users,
			IMap<String, ChannelMongoBean> channelCache) {
		List<ChannelMongoBean> result = new ArrayList<>();
		for (Entry<String, ChannelMongoBean> entry : channelCache.entrySet()) {
			List<String> usersInChannel = new ArrayList<>();
			for (UserInChannelBean userBean : entry.getValue().getUsers()) {
				usersInChannel.add(userBean.getUserName());
			}
			Collections.sort(usersInChannel);
			if (usersInChannel.equals(users)) {
				result.add(entry.getValue());
			}
		}
		return result;
	}

	public long updateBotChannelId(String currentChannelId, String newChannelId) {
		MongoCollection<Document> channelCollection = this.getMongoClient().getDatabase(F.NHO_DATABASE)
				.getCollection(ChannelDBField.CHANNEL_COLLECTION);
		BasicDBObject obj = new BasicDBObject();
		obj.append(ChannelDBField.ID, currentChannelId);

		BasicDBObject updateObject = new BasicDBObject(ChannelDBField.ID, newChannelId);

		UpdateResult result = channelCollection.updateOne(obj, new BasicDBObject("$set", updateObject));
		return result.getModifiedCount();
	}

	public long updateLastTimeChat(String channelId, long lastTime) {
		MongoCollection<Document> channelCollection = this.getMongoClient().getDatabase(F.NHO_DATABASE)
				.getCollection(ChannelDBField.CHANNEL_COLLECTION);
		BasicDBObject obj = new BasicDBObject();
		obj.append(ChannelDBField.ID, channelId);

		BasicDBObject updateObj = new BasicDBObject(ChannelDBField.LAST_TIME, String.valueOf(lastTime));
		UpdateResult result = channelCollection.updateOne(obj, new BasicDBObject("$set", updateObj));
		getLogger().debug("number updated channel when update last time chat: " + result.getModifiedCount());
		// if success -> update in hazel cast :
		if (result.getModifiedCount() > 0) {
			IMap<String, ChannelMongoBean> channelCache = getMap();
			ChannelMongoBean bean = channelCache.get(channelId);
			bean.setLastTime(String.valueOf(lastTime));
			channelCache.replace(channelId, bean);
		}
		return result.getModifiedCount();
	}

	public long updateChatTimes(String channelId) {
		MongoCollection<Document> channelCollection = this.getMongoClient().getDatabase(F.NHO_DATABASE)
				.getCollection(ChannelDBField.CHANNEL_COLLECTION);
		BasicDBObject obj = new BasicDBObject();
		obj.append(ChannelDBField.ID, channelId);

		BasicDBObject updateObj = new BasicDBObject(ChannelDBField.TIMES, getTimesUseChannel(channelId) + 1);
		UpdateResult result = channelCollection.updateOne(obj, new BasicDBObject("$set", updateObj));
		getLogger().debug("number updated channel when update times chat: " + result.getModifiedCount());
		// if success -> update in hazel cast :
		if (result.getModifiedCount() > 0) {
			IMap<String, ChannelMongoBean> channelCache = getMap();
			ChannelMongoBean bean = channelCache.get(channelId);
			bean.setTimes(bean.getTimes() + 1);
			channelCache.replace(channelId, bean);
		}
		return result.getModifiedCount();
	}

	public long updateThemeAndPersonlityUser(String channelId, String userName, Theme theme, Personality personality) {
		MongoCollection<Document> channelCollection = this.getMongoClient().getDatabase(F.NHO_DATABASE)
				.getCollection(ChannelDBField.CHANNEL_COLLECTION);
		BasicDBObject obj = new BasicDBObject();
		obj.append(ChannelDBField.ID, channelId);
		obj.append(ChannelDBField.USERS + ChannelDBField.USER_NAME, userName);

		BasicDBObject updateObj = new BasicDBObject(ChannelDBField.USERS+ChannelDBField.THEME, theme.getCode());
		updateObj.append(ChannelDBField.USERS+ChannelDBField.PERSONALITY, personality.getCode());
		UpdateResult result = channelCollection.updateOne(obj, new BasicDBObject("$set", updateObj));
		getLogger().debug("number updated channel when update times chat: " + result.getModifiedCount());
		// if success -> update in hazel cast :
		if (result.getModifiedCount() > 0) {
			IMap<String, ChannelMongoBean> channelCache = getMap();
			ChannelMongoBean bean = channelCache.get(channelId);
			for( UserInChannelBean userBean : bean.getUsers()){
				if(userBean.getUserName().equals(userName)){
					userBean.setTheme(theme);
					userBean.setPersonality(personality);
				}
			}
			channelCache.replace(channelId, bean);
		}
		return result.getModifiedCount();
	}

	public long updateChannel(String currentName, String newChannelName) {
		MongoCollection<Document> channelCollection = this.getMongoClient().getDatabase(F.NHO_DATABASE)
				.getCollection(ChannelDBField.CHANNEL_COLLECTION);
		BasicDBObject obj = new BasicDBObject();
		obj.append(ChannelDBField.NAME, currentName);

		BasicDBObject updateObject = new BasicDBObject(ChannelDBField.NAME, newChannelName);

		UpdateResult result = channelCollection.updateOne(obj, new BasicDBObject("$set", updateObject));
		return result.getModifiedCount();
	}

	public long updateListUserChannel(String channelName, List<String> newUsers) {
		MongoCollection<Document> channelCollection = this.getMongoClient().getDatabase(F.NHO_DATABASE)
				.getCollection(ChannelDBField.CHANNEL_COLLECTION);
		BasicDBObject obj = new BasicDBObject();
		obj.append(ChannelDBField.NAME, channelName);
		String userNames = "";
		for (int i = 0; i < newUsers.size(); i++) {
			userNames += "&" + newUsers.get(i);
		}
		BasicDBObject updateObject = new BasicDBObject(ChannelDBField.USERS, userNames);

		UpdateResult result = channelCollection.updateOne(obj, new BasicDBObject("$set", updateObject));
		return result.getModifiedCount();
	}
}
