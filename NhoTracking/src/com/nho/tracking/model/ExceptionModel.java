package com.nho.tracking.model;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.nhb.common.db.models.AbstractModel;
import com.nho.tracking.EF;

public class ExceptionModel extends AbstractModel{
	public boolean insertException(ExceptionBean exception) {
		boolean isSuccessful = false;
		MongoCollection<Document> exceptionCollection = this.getMongoClient().getDatabase(EF.NHO_DATABASE)
				.getCollection(EF.EXCEPTION_COLLECTION);

		Document document = exception.toDocument();
		if (document != null) {
			exceptionCollection.insertOne(document);
			isSuccessful = true;
		}
		return isSuccessful;
	}

	public List<ExceptionBean> findExceptionByTitle(String title) {
		List<ExceptionBean> result = new ArrayList<>();
		MongoCollection<Document> exceptionCollection = this.getMongoClient().getDatabase(EF.NHO_DATABASE)
				.getCollection(EF.EXCEPTION_COLLECTION);

		BasicDBObject obj = new BasicDBObject();
		obj.append(EF.TITLE, title);

		FindIterable<Document> found = exceptionCollection.find(obj);
		for (Document doc : found) {
			result.add(ExceptionBean.fromDocument(doc));
		}

		return result;
	}
	
	public List<ExceptionBean> findExceptionByType(int type) {
		List<ExceptionBean> result = new ArrayList<>();
		MongoCollection<Document> exceptionCollection = this.getMongoClient().getDatabase(EF.NHO_DATABASE)
				.getCollection(EF.EXCEPTION_COLLECTION);

		BasicDBObject obj = new BasicDBObject();
		obj.append(EF.TYPE, type);

		FindIterable<Document> found = exceptionCollection.find(obj);
		for (Document doc : found) {
			result.add(ExceptionBean.fromDocument(doc));
		}

		return result;
	}
}
