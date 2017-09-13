package com.nho.version.model;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.nhb.common.db.models.AbstractModel;
import com.nho.version.statics.Version;

public class VersionModel extends AbstractModel{
	public ObjectId insert(VersionBean versionBean) {
		Document versionDocument = versionBean.toDocument();
		if (versionDocument != null) {
			MongoCollection<Document> deviceTokenCollection = this.getMongoClient().getDatabase(Version.NHO_DATABASE)
					.getCollection(Version.NHO_VERSION_COLLECTION);
			deviceTokenCollection.insertOne(versionDocument);
			return versionDocument.getObjectId(Version._ID);
		}
		return null;
	}
	public List<Document> getVersionDocument(){
		List<Document> versionDocuments = new ArrayList<>();
		MongoCollection<Document> versionCollection = this.getMongoClient().getDatabase(Version.NHO_DATABASE)
				.getCollection(Version.NHO_VERSION_COLLECTION);
		FindIterable<Document> found = versionCollection.find();
		for(Document doc : found){
			versionDocuments.add(doc);
		}
		
		return versionDocuments;
	}
	
	public long deleteVersionDocument(Document doc){
		long deleteCount = 0;
		MongoCollection<Document> versionCollection = this.getMongoClient().getDatabase(Version.NHO_DATABASE)
				.getCollection(Version.NHO_VERSION_COLLECTION);
		deleteCount = versionCollection.deleteOne(doc).getDeletedCount();
		
		return deleteCount;
	}
	
}
