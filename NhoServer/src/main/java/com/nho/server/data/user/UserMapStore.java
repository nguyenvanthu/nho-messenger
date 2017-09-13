package com.nho.server.data.user;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bson.Document;

import com.hazelcast.core.MapStore;
import com.mongodb.bulk.BulkWriteUpsert;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.UpdateOneModel;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.WriteModel;
import com.nhb.common.BaseLoggable;
import com.nho.server.statics.DBF;

public class UserMapStore extends BaseLoggable implements MapStore<String, UserMongoBean> {
	private MongoDatabase database;

	public UserMapStore(MongoDatabase database) {
		this.database = database;
	}

	private MongoCollection<Document> getUserCollection() {
		return this.database.getCollection(DBF.USER_COLLECTION);
	}

	@Override
	public UserMongoBean load(String userName) {
		getLogger().debug("load user name {} from database ", userName);
		Document document = new Document();
		document.put(DBF.USERNAME, userName);
		FindIterable<Document> found = getUserCollection().find(document);
		if (found.first() != null) {
			return UserMongoBean.fromDocument(found.first());
		}
		return null;
	}

	@Override
	public Map<String, UserMongoBean> loadAll(Collection<String> arg0) {
		return null;
	}

	@Override
	public Iterable<String> loadAllKeys() {
		return null;
	}

	@Override
	public void delete(String userName) {
		getLogger().debug("delete userName {} from database", userName);
		Document document = new Document();
		document.put(DBF.USERNAME, userName);
		getUserCollection().deleteOne(document);
	}

	@Override
	public void deleteAll(Collection<String> userNames) {
		for (String userName : userNames) {
			delete(userName);
		}
	}

	@Override
	public void store(String key, UserMongoBean userBean) {
		Document document = userBean.toDocument();
		Document where = new Document(DBF.USERNAME, key);
		try {
			getUserCollection().updateOne(where, new Document("$set", document), new UpdateOptions().upsert(true));
		} catch (Exception exception) {
			getLogger().debug("error when store user to disk" + exception);
		}
	}

	@Override
	public void storeAll(Map<String, UserMongoBean> userBeans) {
		getLogger().debug("store all user from mapstore to disk");
		try {
			List<WriteModel<Document>> writeModels = new ArrayList<>();
			for (Entry<String, UserMongoBean> entry : userBeans.entrySet()) {
				Document document = entry.getValue().toDocument();
				Document where = new Document(DBF.USERNAME, entry.getKey());
				UpdateOneModel<Document> model = new UpdateOneModel<>(where, new Document("$set", document),
						new UpdateOptions().upsert(true));
				writeModels.add(model);
			}
			if (writeModels.size() > 0) {
				com.mongodb.bulk.BulkWriteResult result = getUserCollection().bulkWrite(writeModels);
				getLogger().debug("strore {} user to disk", result.getModifiedCount());
				List<BulkWriteUpsert> upserts = result.getUpserts();
				getLogger().debug("upserts size: {}", upserts.size());
			}
		} catch (Exception exception) {
			getLogger().debug("error when store all user to disk " + exception);
		}
	}

}
