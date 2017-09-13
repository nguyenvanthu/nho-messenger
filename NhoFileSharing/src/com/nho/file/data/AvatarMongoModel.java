package com.nho.file.data;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.nhb.common.db.models.AbstractModel;
import com.nho.file.data.avatar.AvatarMongoBean;
import com.nho.file.statics.FileField;

public class AvatarMongoModel extends AbstractModel {

	public boolean insertAvatar(AvatarMongoBean bean) {
		boolean isSuccessful = false;
		MongoCollection<Document> avatarCollection = this.getMongoClient().getDatabase(FileField.NHO_DATABASE)
				.getCollection(FileField.AVATAR_COLLECTION);

		Document document = bean.toDocument();
		if (document != null) {
			avatarCollection.insertOne(document);
			isSuccessful = true;
		}
		return isSuccessful;
	}

	public List<AvatarMongoBean> getByType(int type) {
		List<AvatarMongoBean> avatars = new ArrayList<>();
		MongoCollection<Document> avatarCollection = this.getMongoClient().getDatabase(FileField.NHO_DATABASE)
				.getCollection(FileField.AVATAR_COLLECTION);
		BasicDBObject obj = new BasicDBObject();
		obj.put(FileField.TYPE, type);
		FindIterable<Document> found = avatarCollection.find(obj);
		for (Document document : found) {
			avatars.add(AvatarMongoBean.fromDocument(document));
		}
		return avatars;
	}

	public AvatarMongoBean getByUserId(String userId) {
		MongoCollection<Document> avatarCollection = this.getMongoClient().getDatabase(FileField.NHO_DATABASE)
				.getCollection(FileField.AVATAR_COLLECTION);
		BasicDBObject obj = new BasicDBObject();
		obj.put(FileField.USER_ID, userId);
		FindIterable<Document> found = avatarCollection.find(obj);
		if (found.first() != null) {
			return AvatarMongoBean.fromDocument(found.first());
		}
		return null;
	}

}
