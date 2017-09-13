package com.nho.notification.data;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import com.nhb.common.db.models.AbstractModel;
import com.nho.notification.statics.DeviceTokenDbField;
import com.nho.statics.F;

public class DeviceTokenModel extends AbstractModel {

	public ObjectId insert(DeviceTokenMongoBean deviceTokenBean) {
		Document deviceTokenDocument = deviceTokenBean.toDocument();
		if (deviceTokenDocument != null) {
			MongoCollection<Document> deviceTokenCollection = this.getMongoClient().getDatabase(F.NHO_DATABASE)
					.getCollection(DeviceTokenDbField.DEVICE_TOKEN_COLLECTION);
			deviceTokenCollection.insertOne(deviceTokenDocument);
			return deviceTokenDocument.getObjectId(DeviceTokenDbField._ID);
		}
		return null;
	}

	public List<DeviceTokenMongoBean> findByUserName(String userName) {
		List<DeviceTokenMongoBean> deviceTokens = new ArrayList<>();

		MongoCollection<Document> deviceTokenCollection = this.getMongoClient().getDatabase(F.NHO_DATABASE)
				.getCollection(DeviceTokenDbField.DEVICE_TOKEN_COLLECTION);
		BasicDBObject obj = new BasicDBObject();
		obj.append(F.USER , userName);
		FindIterable<Document> found = deviceTokenCollection.find(obj);
		for (Document doc : found) {
			deviceTokens.add(DeviceTokenMongoBean.fromDocument(doc));
		}
		return deviceTokens;
	}

	public List<Document> getAllDeviceTokenDocument() {
		List<Document> deviceTokenDocuments = new ArrayList<>();
		MongoCollection<Document> deviceTokenCollection = this.getMongoClient().getDatabase(F.NHO_DATABASE)
				.getCollection(DeviceTokenDbField.DEVICE_TOKEN_COLLECTION);
		FindIterable<Document> found = deviceTokenCollection.find();
		for (Document doc : found) {
			deviceTokenDocuments.add(doc);
		}

		return deviceTokenDocuments;
	}

	public long deleteDeviceTokenDocument(Document doc) {
		long deleteCount = 0;
		MongoCollection<Document> deviceTokenCollection = this.getMongoClient().getDatabase(F.NHO_DATABASE)
				.getCollection(DeviceTokenDbField.DEVICE_TOKEN_COLLECTION);
		deleteCount = deviceTokenCollection.deleteOne(doc).getDeletedCount();

		return deleteCount;
	}

	public DeviceTokenMongoBean findByToken(String token) {
		DeviceTokenMongoBean deviceTokenBean = new DeviceTokenMongoBean();
		MongoCollection<Document> deviceTokenCollection = this.getMongoClient().getDatabase(F.NHO_DATABASE)
				.getCollection(DeviceTokenDbField.DEVICE_TOKEN_COLLECTION);
		BasicDBObject obj = new BasicDBObject();
		obj.append(DeviceTokenDbField.DEVICE_TOKEN, token);
		FindIterable<Document> found = deviceTokenCollection.find(obj);
		for (Document doc : found) {
			deviceTokenBean = DeviceTokenMongoBean.fromDocument(doc);
		}

		return deviceTokenBean;
	}

	public String findUserNameByDeviceToken(String token) {
		String user = null;
		MongoCollection<Document> deviceTokenCollection = this.getMongoClient().getDatabase(F.NHO_DATABASE)
				.getCollection(DeviceTokenDbField.DEVICE_TOKEN_COLLECTION);
		BasicDBObject obj = new BasicDBObject();
		obj.append(DeviceTokenDbField.DEVICE_TOKEN, token);
		FindIterable<Document> found = deviceTokenCollection.find(obj);
		for (Document doc : found) {
			DeviceTokenMongoBean deviceToken = DeviceTokenMongoBean.fromDocument(doc);
			user = deviceToken.getUser();
		}
		return user;
	}

	public long updateUserOfDeviceToken(String token, String user) {
		MongoCollection<Document> deviceTokenCollection = this.getMongoClient().getDatabase(F.NHO_DATABASE)
				.getCollection(DeviceTokenDbField.DEVICE_TOKEN_COLLECTION);
		BasicDBObject obj = new BasicDBObject();
		obj.append(DeviceTokenDbField.DEVICE_TOKEN, token);

		BasicDBObject updateObject = new BasicDBObject(F.USER, user);

		UpdateResult result = deviceTokenCollection.updateMany(obj, new BasicDBObject("$set", updateObject));
		return result.getModifiedCount(); // = 1
	}

	public long deleteDeviceToken(String token) {
		MongoCollection<Document> deviceTokenCollection = this.getMongoClient().getDatabase(F.NHO_DATABASE)
				.getCollection(DeviceTokenDbField.DEVICE_TOKEN_COLLECTION);
		BasicDBObject obj = new BasicDBObject();
		obj.append(DeviceTokenDbField.DEVICE_TOKEN, token);
		DeleteResult result = deviceTokenCollection.deleteOne(obj);
		return result.getDeletedCount();
	}
}
