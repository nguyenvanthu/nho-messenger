package com.nho.file.data;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.nhb.common.db.models.AbstractModel;
import com.nho.file.data.game.GameMongoBean;
import com.nho.file.statics.FileField;

public class GameMongoModel extends AbstractModel {

	public boolean insertGame(GameMongoBean game) {
		boolean isSuccessful = false;
		MongoCollection<Document> gameCollection = this.getMongoClient().getDatabase(FileField.NHO_DATABASE)
				.getCollection(FileField.GAME_COLLECTION);

		Document document = game.toDocument();
		if (document != null) {
			gameCollection.insertOne(document);
			isSuccessful = true;
		}
		return isSuccessful;
	}

	public List<GameMongoBean> getAllGames() {
		List<GameMongoBean> games = new ArrayList<>();
		MongoCollection<Document> gameCollection = this.getMongoClient().getDatabase(FileField.NHO_DATABASE)
				.getCollection(FileField.GAME_COLLECTION);
		FindIterable<Document> found = gameCollection.find();
		for (Document document : found) {
			games.add(GameMongoBean.fromDocument(document));
		}
		return games;
	}

	public GameMongoBean getGameByType(int type) {
		MongoCollection<Document> gameCollection = this.getMongoClient().getDatabase(FileField.NHO_DATABASE)
				.getCollection(FileField.GAME_COLLECTION);
		BasicDBObject obj = new BasicDBObject();
		obj.put(FileField.TYPE, type);
		FindIterable<Document> found = gameCollection.find(obj);
		if (found.first() != null) {
			return GameMongoBean.fromDocument(found.first());
		}
		return null;
	}
	
	public GameMongoBean getGameByName(String name){
		MongoCollection<Document> gameCollection = this.getMongoClient().getDatabase(FileField.NHO_DATABASE)
				.getCollection(FileField.GAME_COLLECTION);
		BasicDBObject obj = new BasicDBObject();
		obj.put(FileField.NAME, name);
		FindIterable<Document> found = gameCollection.find(obj);
		if (found.first() != null) {
			return GameMongoBean.fromDocument(found.first());
		}
		return null;
	}

	public long deleteGame(int type) {
		MongoCollection<Document> gameCollection = this.getMongoClient().getDatabase(FileField.NHO_DATABASE)
				.getCollection(FileField.GAME_COLLECTION);
		BasicDBObject obj = new BasicDBObject();
		obj.put(FileField.TYPE, type);
		long deleteCount = gameCollection.deleteMany(obj).getDeletedCount();
		return deleteCount;
	}
}
