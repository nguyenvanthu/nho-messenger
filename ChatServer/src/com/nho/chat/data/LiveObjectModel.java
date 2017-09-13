package com.nho.chat.data;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.nhb.common.db.models.AbstractModel;
import com.nho.chat.statics.ChannelDBField;
import com.nho.statics.F;

public class LiveObjectModel extends AbstractModel {
	public boolean insert(LiveObjectMongoBean bean) {
		try {
			getLogger().debug("insert live object to mongo db");
			MongoCollection<Document> liveObjectCollection = this.getMongoClient().getDatabase(F.NHO_DATABASE)
					.getCollection(ChannelDBField.LIVE_OBJECT_COLLECTION);
			Document document = bean.toDocument();
			if (document == null) {
				return false;
			}
			liveObjectCollection.insertOne(document);
			return true;
		} catch (Exception exception) {
			getLogger().debug("exception when isert live object " + exception);
		}
		return false;
	}

	public List<LiveObjectMongoBean> findByChannelId(String channelId) {
		List<LiveObjectMongoBean> liveObjects = new ArrayList<>();
		MongoCollection<Document> liveObjectCollection = this.getMongoClient().getDatabase(F.NHO_DATABASE)
				.getCollection(ChannelDBField.LIVE_OBJECT_COLLECTION);
		BasicDBObject obj = new BasicDBObject();
		obj.append(ChannelDBField.CHANNEL_ID, channelId);
		FindIterable<Document> found = liveObjectCollection.find(obj);
		for (Document document : found) {
			liveObjects.add(LiveObjectMongoBean.fromDocument(document));
		}
		return liveObjects;
	}

	public LiveObjectMongoBean findByObjectId(String objectId) {
		MongoCollection<Document> liveObjectCollection = this.getMongoClient().getDatabase(F.NHO_DATABASE)
				.getCollection(ChannelDBField.LIVE_OBJECT_COLLECTION);
		BasicDBObject obj = new BasicDBObject();
		obj.append(ChannelDBField.OBJ_ID, objectId);
		FindIterable<Document> found = liveObjectCollection.find(obj);
		for (Document document : found) {
			LiveObjectMongoBean bean = LiveObjectMongoBean.fromDocument(document);
			if (bean != null) {
				return bean;
			}
		}
		return null;
	}

	public void deleteLiveObject(String objectId) {
		MongoCollection<Document> liveObjectCollection = this.getMongoClient().getDatabase(F.NHO_DATABASE)
				.getCollection(ChannelDBField.LIVE_OBJECT_COLLECTION);
		BasicDBObject obj = new BasicDBObject();
		obj.append(ChannelDBField.OBJ_ID, objectId);
		liveObjectCollection.deleteOne(obj);
	}

	public void deleteliveObjectInChannel(String channelId) {
		getLogger().debug("delete live object in mongodb ");
		MongoCollection<Document> liveObjectCollection = this.getMongoClient().getDatabase(F.NHO_DATABASE)
				.getCollection(ChannelDBField.LIVE_OBJECT_COLLECTION);
		BasicDBObject obj = new BasicDBObject();
		obj.append(ChannelDBField.CHANNEL_ID, channelId);
		liveObjectCollection.deleteMany(obj);
	}
}
