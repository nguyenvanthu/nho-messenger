package com.nho.file.data;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.nhb.common.db.models.AbstractModel;
import com.nho.file.data.sticker.StickerMongoBean;
import com.nho.file.statics.FileField;

public class StickerMongoModel extends AbstractModel {

	public boolean insertSticker(StickerMongoBean bean) {
		boolean isSuccessful = false;
		MongoCollection<Document> stickerCollection = this.getMongoClient().getDatabase(FileField.NHO_DATABASE)
				.getCollection(FileField.STICKER_COLLECTION);

		Document document = bean.toDocument();
		if (document != null) {
			stickerCollection.insertOne(document);
			isSuccessful = true;
		}
		return isSuccessful;
	}

	public List<StickerMongoBean> getByGroup(String group) {
		List<StickerMongoBean> stickers = new ArrayList<>();
		MongoCollection<Document> stickerCollection = this.getMongoClient().getDatabase(FileField.NHO_DATABASE)
				.getCollection(FileField.STICKER_COLLECTION);
		BasicDBObject obj = new BasicDBObject();
		obj.put(FileField.GROUP, group);
		FindIterable<Document> found = stickerCollection.find(obj);
		for (Document document : found) {
			stickers.add(StickerMongoBean.fromDocument(document));
		}
		return stickers;
	}

	public List<StickerMongoBean> getAllStickers() {
		List<StickerMongoBean> stickers = new ArrayList<>();
		MongoCollection<Document> stickerCollection = this.getMongoClient().getDatabase(FileField.NHO_DATABASE)
				.getCollection(FileField.STICKER_COLLECTION);
		FindIterable<Document> found = stickerCollection.find();
		for (Document document : found) {
			stickers.add(StickerMongoBean.fromDocument(document));
		}

		return stickers;
	}

	public long deleteSticker(String name) {
		return 0;
	}
}
