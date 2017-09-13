package com.nho.server.data;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.hazelcast.core.IMap;
import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.UpdateResult;
import com.nhb.common.db.models.AbstractModel;
import com.nho.server.data.avatar.AvatarMongoBean;
import com.nho.server.data.user.UserMongoBean;
import com.nho.server.entity.avatar.Avatar;
import com.nho.server.statics.DBF;
import com.nho.statics.F;

public class UserMongoModel extends AbstractModel {
	public static final String USER_MAP_KEY = "nho:users";

	private IMap<String, UserMongoBean> getMap() {
		return this.getHazelcast().getMap(USER_MAP_KEY);
	}

	public String insert(String userName, String faceToken, String displayName, Avatar avatar, String email) {
		UserMongoBean userBean = createNewUserBean(userName, faceToken, displayName, avatar, email);
		if (userBean != null) {
			Document userDocument = userBean.toDocument();
			MongoCollection<Document> userCollection = this.getMongoClient().getDatabase(F.NHO_DATABASE)
					.getCollection(F.USER_COLLECTION);
			userCollection.insertOne(userDocument);
			// insert to hazelcast
			IMap<String, UserMongoBean> userCache = getMap();
			UserMongoBean bean = userCache.get(userName);
			if (bean == null) {
				userCache.put(userName, userBean);
			}
			return userDocument.getObjectId(DBF._ID).toHexString();
		}
		return null;
	}

	private UserMongoBean createNewUserBean(String userName, String faceToken, String displayName, Avatar avatar,
			String email) {
		if (userName != null && faceToken != null) {
			UserMongoBean user = new UserMongoBean();
			user.setObjectId(new ObjectId());
			user.setUserName(userName);
			user.setFacebookToken(faceToken);
			user.setDisplayName(displayName);
			user.setEmail(email);

			AvatarMongoBean avatarBean = new AvatarMongoBean();
			avatarBean.setName(avatar.getName());
			avatarBean.setType(avatar.getType().getCode());
			avatarBean.setUrl(avatar.getUrl());
			user.setAvatar(avatarBean);

			return user;
		}
		return null;
	}

	public UserMongoBean findById(ObjectId id) {
		List<UserMongoBean> users = new ArrayList<>();
		MongoCollection<Document> userCollection = this.getMongoClient().getDatabase(F.NHO_DATABASE)
				.getCollection(F.USER_COLLECTION);
		Document document = new Document();
		document.append(DBF._ID, id);
		FindIterable<Document> found = userCollection.find(document);
		for (Document doc : found) {
			users.add(UserMongoBean.fromDocument(doc));
			getLogger().debug("ObjectId of document " + doc.getObjectId(DBF._ID));
		}
		if (users.size() > 0) {
			getLogger().debug("get user from mongodb ");
			return users.get(0);
		} else {
			return null;
		}
	}

	public UserMongoBean findByUserName(String faceId) {
		IMap<String, UserMongoBean> userCache = getMap();
		if (userCache.containsKey(faceId)) {
			getLogger().debug("get user from hazelcast ");
			return userCache.get(faceId);
		} else {
			List<UserMongoBean> users = new ArrayList<>();
			MongoCollection<Document> userCollection = this.getMongoClient().getDatabase(F.NHO_DATABASE)
					.getCollection(F.USER_COLLECTION);
			Document document = new Document();
			document.put(DBF.USERNAME, faceId);
			FindIterable<Document> found = userCollection.find(document);
			for (Document doc : found) {
				users.add(UserMongoBean.fromDocument(doc));
				userCache.put(doc.getString(DBF.USERNAME), UserMongoBean.fromDocument(doc));
			}
			if (users.size() > 0) {
				getLogger().debug("find done user " + users.get(0).getUserName());
				return users.get(0);
			} else {
				return null;
			}
		}
	}

	public UserMongoBean findByFacebookId(String faceId) {
		IMap<String, UserMongoBean> userCache = getMap();
		if (userCache.containsKey(faceId)) {
			getLogger().debug("get user from hazelcast ");
			return userCache.get(faceId);
		} else {
			List<UserMongoBean> users = new ArrayList<>();
			MongoCollection<Document> userCollection = this.getMongoClient().getDatabase(F.NHO_DATABASE)
					.getCollection(F.USER_COLLECTION);
			Document document = new Document();
			document.put(DBF.USERNAME, faceId);
			FindIterable<Document> found = userCollection.find(document);
			for (Document doc : found) {
				users.add(UserMongoBean.fromDocument(doc));

				userCache.put(doc.getString(DBF.USERNAME), UserMongoBean.fromDocument(doc));
			}
			if (users.size() > 0) {
				getLogger().debug("find done user " + users.get(0).getUserName());
				return users.get(0);
			}
		}
		return null;
	}

	public UserMongoBean findByFacebookToken(String facebookToken) {
		List<UserMongoBean> users = new ArrayList<>();
		MongoCollection<Document> userCollection = this.getMongoClient().getDatabase(F.NHO_DATABASE)
				.getCollection(F.USER_COLLECTION);
		Document document = new Document();
		document.put(DBF.FACE_TOKEN, facebookToken);
		FindIterable<Document> found = userCollection.find(document);
		for (Document doc : found) {
			users.add(UserMongoBean.fromDocument(doc));
		}
		if (users.size() > 0 && users.get(0) != null) {
			getLogger().debug("find done user " + users.get(0).getUserName());
			return users.get(0);
		}
		return null;
	}

	public long updateUserOnlineTime(String faceId, long lastTime) {
		MongoCollection<Document> userCollection = this.getMongoClient().getDatabase(F.NHO_DATABASE)
				.getCollection(F.USER_COLLECTION);
		BasicDBObject obj = new BasicDBObject();
		obj.append(DBF.USERNAME, faceId);

		BasicDBObject updateObj = new BasicDBObject();
		updateObj.append(DBF.TIME_ONLINE, lastTime);
		UpdateResult result = userCollection.updateOne(obj, new BasicDBObject("$set", updateObj));
		// update in hazelcast :
		IMap<String, UserMongoBean> userCache = getMap();
		UserMongoBean userBean = userCache.get(faceId);
		if (userBean != null && result.getModifiedCount() > 0) {
			userBean.setLastTimeOnline(lastTime);
			userCache.replace(faceId, userBean);
		}
		return result.getModifiedCount();
	}

	public long updateFacebookToken(String faceId, String faceToken) {
		MongoCollection<Document> userCollection = this.getMongoClient().getDatabase(F.NHO_DATABASE)
				.getCollection(F.USER_COLLECTION);
		BasicDBObject obj = new BasicDBObject();
		obj.append(DBF.USERNAME, faceId);

		BasicDBObject updateObj = new BasicDBObject();
		updateObj.append(DBF.FACE_TOKEN, faceToken);

		UpdateResult result = userCollection.updateOne(obj, new BasicDBObject("$set", updateObj));
		// update in hazelcast :
		IMap<String, UserMongoBean> userCache = getMap();
		UserMongoBean userBean = userCache.get(faceId);
		if (userBean != null && result.getModifiedCount() > 0) {
			userBean.setFacebookToken(faceToken);
			userCache.replace(faceId, userBean);
		}
		return result.getModifiedCount();
	}

	public long updateAvatar(String faceId, Avatar newAvatar) {
		MongoCollection<Document> userCollection = this.getMongoClient().getDatabase(F.NHO_DATABASE)
				.getCollection(F.USER_COLLECTION);
		BasicDBObject obj = new BasicDBObject();
		obj.append(DBF.USERNAME, faceId);
		BasicDBObject updateObj = new BasicDBObject();
		updateObj.append("avatar.name", newAvatar.getName());
		updateObj.append("avatar.url", newAvatar.getUrl());
		updateObj.append("avatar.type", newAvatar.getType().getCode());
		long updateCount = userCollection.updateMany(obj, new BasicDBObject("$set", updateObj)).getModifiedCount();
		getLogger().debug("update profile count: " + updateCount);
		// update in cache
		IMap<String, UserMongoBean> userCache = getMap();
		UserMongoBean userBean = userCache.get(faceId);
		if (userBean != null && updateCount > 0) {
			AvatarMongoBean avatarBean = new AvatarMongoBean();
			avatarBean.setName(newAvatar.getName());
			avatarBean.setType(newAvatar.getType().getCode());
			avatarBean.setUrl(newAvatar.getUrl());
			userBean.setAvatar(avatarBean);
			userCache.replace(faceId, userBean);
		}

		return updateCount;
	}

	public long deleteUser(String faceId) {
		MongoCollection<Document> userCollection = this.getMongoClient().getDatabase(F.NHO_DATABASE)
				.getCollection(F.USER_COLLECTION);
		BasicDBObject obj = new BasicDBObject();
		obj.append(DBF.USERNAME, faceId);
		long deleteCount = userCollection.deleteOne(obj).getDeletedCount();
		// delete user in cache
		IMap<String, UserMongoBean> userCache = getMap();
		UserMongoBean userBean = userCache.get(faceId);
		if (userBean != null) {
			userCache.remove(faceId);
		}
		return deleteCount;
	}

	public long updateProfile(String faceId, Avatar newAvatar, String newDisplayName) {
		MongoCollection<Document> userCollection = this.getMongoClient().getDatabase(F.NHO_DATABASE)
				.getCollection(F.USER_COLLECTION);
		BasicDBObject obj = new BasicDBObject();
		obj.append(DBF.USERNAME, faceId);

		BasicDBObject updateObj = new BasicDBObject();
		updateObj.append(DBF.DISPLAY_NAME, newDisplayName);
		updateObj.append("avatar.name", newAvatar.getName());
		updateObj.append("avatar.url", newAvatar.getUrl());
		updateObj.append("avatar.type", newAvatar.getType().getCode());
		long updateCount = userCollection.updateMany(obj, new BasicDBObject("$set", updateObj)).getModifiedCount();
		getLogger().debug("update profile count: " + updateCount);
		// update in hazelcast :
		IMap<String, UserMongoBean> userCache = getMap();
		UserMongoBean userBean = userCache.get(faceId);
		if (userBean != null && updateCount > 0) {
			AvatarMongoBean avatarBean = new AvatarMongoBean();
			avatarBean.setName(newAvatar.getName());
			avatarBean.setType(newAvatar.getType().getCode());
			avatarBean.setUrl(newAvatar.getUrl());
			userBean.setDisplayName(newDisplayName);
			userBean.setAvatar(avatarBean);
			userCache.replace(faceId, userBean);
		}
		return updateCount;
	}

	public long updateDisplayName(String faceId, String newDisplayName) {
		MongoCollection<Document> userCollection = this.getMongoClient().getDatabase(F.NHO_DATABASE)
				.getCollection(F.USER_COLLECTION);
		BasicDBObject obj = new BasicDBObject();
		obj.append(DBF.USERNAME, faceId);

		BasicDBObject updateObj = new BasicDBObject();
		updateObj.append(DBF.DISPLAY_NAME, newDisplayName);
		long updateCount = userCollection.updateMany(obj, new BasicDBObject("$set", updateObj)).getModifiedCount();
		getLogger().debug("update displayName count: " + updateCount);
		// update in hazelcast :
		IMap<String, UserMongoBean> userCache = getMap();
		UserMongoBean userBean = userCache.get(faceId);
		if (userBean != null && updateCount > 0) {
			userBean.setDisplayName(newDisplayName);
			userCache.replace(faceId, userBean);
		}
		return updateCount;
	}

	public boolean isExistUser(String faceId) {
		boolean isExist = false;
		UserMongoBean botBean = this.findByFacebookId(faceId);
		if (botBean != null) {
			isExist = true;
		}

		return isExist;
	}

	public List<UserMongoBean> findAllUser() {
		List<UserMongoBean> users = new ArrayList<>();
		MongoCollection<Document> userCollection = this.getMongoClient().getDatabase(F.NHO_DATABASE)
				.getCollection(F.USER_COLLECTION);
		FindIterable<Document> found = userCollection.find();
		for (Document doc : found) {
			users.add(UserMongoBean.fromDocument(doc));
		}
		return users;
	}
}
