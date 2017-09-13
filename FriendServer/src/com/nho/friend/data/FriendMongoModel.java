package com.nho.friend.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.bson.Document;

import com.hazelcast.core.IMap;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import com.nhb.common.db.models.AbstractModel;
import com.nho.statics.F;
import com.nho.statics.StatusFriend;

public class FriendMongoModel extends AbstractModel {
	private static final String FRIEND_CACHE = F.PREFIX + "friends";

	public IMap<String, String> getMap() {
		return this.getHazelcast().getMap(FRIEND_CACHE);
	}

	public List<FriendMongoBean> findFriendByUserName(String senderUserName, String buddyUserName,
			boolean isBlockFriend) {
		List<FriendMongoBean> result = new ArrayList<>();
		MongoCollection<Document> friendCollection = null;
		if (isBlockFriend) {
			friendCollection = this.getMongoClient().getDatabase(F.NHO_DATABASE)
					.getCollection(F.BLOCK_FRIEND_COLLECTION);
		} else {
			friendCollection = this.getMongoClient().getDatabase(F.NHO_DATABASE).getCollection(F.FRIEND_COLLECTION);
		}

		BasicDBObject obj = new BasicDBObject();
		obj.append(F.USER, senderUserName);
		obj.append(F.BUDDY, buddyUserName);

		FindIterable<Document> found = friendCollection.find(obj);
		for (Document doc : found) {
			result.add(FriendMongoBean.fromDocument(doc));
		}

		return result;
	}

	public List<Document> getAllFriendDocument(boolean isBlock) {
		List<Document> friendDocuments = new ArrayList<>();
		MongoCollection<Document> friendCollection = null;
		if (isBlock) {
			friendCollection = this.getMongoClient().getDatabase(F.NHO_DATABASE)
					.getCollection(F.BLOCK_FRIEND_COLLECTION);
		} else {
			friendCollection = this.getMongoClient().getDatabase(F.NHO_DATABASE).getCollection(F.FRIEND_COLLECTION);
		}
		FindIterable<Document> found = friendCollection.find();
		for (Document doc : found) {
			friendDocuments.add(doc);
		}

		return friendDocuments;
	}

	public long deleteFriendDocument(Document doc, boolean isBlock) {
		long deleteCount = 0;
		MongoCollection<Document> friendCollection = null;
		if (isBlock) {
			friendCollection = this.getMongoClient().getDatabase(F.NHO_DATABASE)
					.getCollection(F.BLOCK_FRIEND_COLLECTION);
		} else {
			friendCollection = this.getMongoClient().getDatabase(F.NHO_DATABASE).getCollection(F.FRIEND_COLLECTION);
		}
		deleteCount = friendCollection.deleteOne(doc).getDeletedCount();

		return deleteCount;
	}

	public long deleteFriendOfUser(String userName) {
		long deleteCount = 0;
		MongoCollection<Document> friendCollection = this.getMongoClient().getDatabase(F.NHO_DATABASE)
				.getCollection(F.FRIEND_COLLECTION);
		Document userDocument = new Document();
		userDocument.append(F.USER, userName);

		Document buddyDocument = new Document();
		buddyDocument.append(F.BUDDY, userName);

		BasicDBList orDocument = new BasicDBList();
		orDocument.add(userDocument);
		orDocument.add(buddyDocument);

		deleteCount = friendCollection.deleteOne(new BasicDBObject("$or", orDocument)).getDeletedCount();

		return deleteCount;
	}

	public List<String> findFriendOfUser(String userName) {
		List<String> friends = new ArrayList<>();
		List<FriendMongoBean> friendByBuddyNames = findFriendByBuddyUserNameAndStatus(userName,
				StatusFriend.ACCEPT.ordinal(), false);
		for (FriendMongoBean friendBean : friendByBuddyNames) {
			friends.add(friendBean.getUser());
		}
		List<FriendMongoBean> friendByUsernames = findFriendByUserNameAndStatus(userName, StatusFriend.ACCEPT.ordinal(),
				false);
		for (FriendMongoBean friendBean : friendByUsernames) {
			friends.add(friendBean.getBuddy());
		}
		return friends;
	}

	public List<FriendMongoBean> findFriendByBuddyUserNameAndStatus(String buddyUserName, int status,
			boolean isBlockFriend) {
		List<FriendMongoBean> result = new ArrayList<>();
		MongoCollection<Document> friendCollection = null;
		if (isBlockFriend) {
			friendCollection = this.getMongoClient().getDatabase(F.NHO_DATABASE)
					.getCollection(F.BLOCK_FRIEND_COLLECTION);
		} else {
			friendCollection = this.getMongoClient().getDatabase(F.NHO_DATABASE).getCollection(F.FRIEND_COLLECTION);
		}

		BasicDBObject obj = new BasicDBObject();
		obj.append(F.STATUS, status);
		obj.append(F.BUDDY, buddyUserName);
		FindIterable<Document> found = friendCollection.find(obj);
		for (Document doc : found) {
			result.add(FriendMongoBean.fromDocument(doc));
		}
		getLogger().debug("recevie number invite friend " + result.size());

		return result;
	}

	public List<FriendMongoBean> findFriendByUserNameAndStatus(String userName, int status, boolean isBlockFriend) {
		List<FriendMongoBean> result = new ArrayList<>();
		MongoCollection<Document> friendCollection = null;
		if (isBlockFriend) {
			friendCollection = this.getMongoClient().getDatabase(F.NHO_DATABASE)
					.getCollection(F.BLOCK_FRIEND_COLLECTION);
		} else {
			friendCollection = this.getMongoClient().getDatabase(F.NHO_DATABASE).getCollection(F.FRIEND_COLLECTION);
		}

		BasicDBObject obj = new BasicDBObject();
		obj.append(F.STATUS, status);
		obj.append(F.USER, userName);

		FindIterable<Document> found = friendCollection.find(obj);
		for (Document doc : found) {
			result.add(FriendMongoBean.fromDocument(doc));
		}

		return result;
	}

	public List<FriendMongoBean> findFriendByNamesAndStatus(String userName, String buddyUserName, int status,
			boolean isBlockFriend) {
		List<FriendMongoBean> result = new ArrayList<>();
		MongoCollection<Document> friendCollection = null;
		if (isBlockFriend) {
			friendCollection = this.getMongoClient().getDatabase(F.NHO_DATABASE)
					.getCollection(F.BLOCK_FRIEND_COLLECTION);
		} else {
			friendCollection = this.getMongoClient().getDatabase(F.NHO_DATABASE).getCollection(F.FRIEND_COLLECTION);
		}

		BasicDBObject obj = new BasicDBObject();
		obj.append(F.STATUS, status);
		obj.append(F.USER, userName);
		obj.append(F.BUDDY, buddyUserName);

		FindIterable<Document> found = friendCollection.find(obj);
		for (Document doc : found) {
			result.add(FriendMongoBean.fromDocument(doc));
		}

		BasicDBObject obj2 = new BasicDBObject();
		obj.append(F.STATUS, status);
		obj.append(F.USER, buddyUserName);
		obj.append(F.BUDDY, userName);
		FindIterable<Document> found2 = friendCollection.find(obj2);
		for (Document doc : found2) {
			result.add(FriendMongoBean.fromDocument(doc));
		}
		if (status == StatusFriend.ACCEPT.ordinal()) {
			IMap<String, String> friendCache = getMap();
			for (FriendMongoBean friendBean : result) {
				if (!isAddedToCache(friendCache, friendBean)) {
					friendCache.put(friendBean.getUser(), friendBean.getBuddy());
				}
			}
		}

		return result;
	}

	private boolean isAddedToCache(IMap<String, String> friendCache, FriendMongoBean friendBean) {
		boolean isAdded = false;
		for (Entry<String, String> entry : friendCache.entrySet()) {
			if ((entry.getKey().equals(friendBean.getUser()) && entry.getValue().equals(friendBean.getBuddy()))
					|| (entry.getKey().equals(friendBean.getBuddy())
							&& entry.getValue().equals(friendBean.getUser()))) {
				isAdded = true;
			}
		}

		return isAdded;
	}

	public boolean insertFriend(FriendMongoBean friend, boolean isBlockFriend) {
		boolean isSuccessful = false;
		MongoCollection<Document> friendCollection = null;
		if (isBlockFriend) {
			friendCollection = this.getMongoClient().getDatabase(F.NHO_DATABASE)
					.getCollection(F.BLOCK_FRIEND_COLLECTION);
		} else {
			friendCollection = this.getMongoClient().getDatabase(F.NHO_DATABASE).getCollection(F.FRIEND_COLLECTION);
		}

		Document document = friend.toDocument();
		if (document != null) {
			friendCollection.insertOne(document);
			isSuccessful = true;
		}

		return isSuccessful;
	}

	public long updateFriendByUserName(String userName, String buddyName, int status) {
		MongoCollection<Document> friendCollection = this.getMongoClient().getDatabase(F.NHO_DATABASE)
				.getCollection(F.FRIEND_COLLECTION);
		BasicDBObject obj = new BasicDBObject();
		obj.append(F.USER, userName);
		obj.append(F.BUDDY, buddyName);

		BasicDBObject updateObject = new BasicDBObject(F.STATUS, new Integer(status));

		UpdateResult result = friendCollection.updateOne(obj, new BasicDBObject("$set", updateObject));
		return result.getModifiedCount(); // = 1
	}

	public long deleteFriend(String userName, String buddyName, boolean isBlockFriend) {
		MongoCollection<Document> friendCollection = null;
		if (isBlockFriend) {
			friendCollection = this.getMongoClient().getDatabase(F.NHO_DATABASE)
					.getCollection(F.BLOCK_FRIEND_COLLECTION);
		} else {
			friendCollection = this.getMongoClient().getDatabase(F.NHO_DATABASE).getCollection(F.FRIEND_COLLECTION);
		}
		BasicDBObject obj = new BasicDBObject();
		obj.append(F.USER, userName);
		obj.append(F.BUDDY, buddyName);
		DeleteResult result = friendCollection.deleteOne(obj);
		return result.getDeletedCount();
	}
}
