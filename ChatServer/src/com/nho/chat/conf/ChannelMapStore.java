package com.nho.chat.conf;

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
import com.nho.chat.data.ChannelMongoBean;
import com.nho.chat.statics.ChannelDBField;

public class ChannelMapStore extends BaseLoggable implements MapStore<String, ChannelMongoBean> {
	private MongoDatabase database;

	public ChannelMapStore(MongoDatabase database) {
		this.database = database;
	}

	private MongoCollection<Document> getUserCollection() {
		return this.database.getCollection(ChannelDBField.CHANNEL_COLLECTION);
	}

	@Override
	public ChannelMongoBean load(String channelId) {
		getLogger().debug("load channel {} from database ", channelId);
		Document document = new Document();
		document.put(ChannelDBField.CHANNEL_ID, channelId);
		FindIterable<Document> found = getUserCollection().find(document);
		if (found.first() != null) {
			return ChannelMongoBean.fromDocument(found.first());
		}
		return null;
	}

	@Override
	public Map<String, ChannelMongoBean> loadAll(Collection<String> arg0) {
		return null;
	}

	@Override
	public Iterable<String> loadAllKeys() {
		return null;
	}

	@Override
	public void delete(String channelId) {
		getLogger().debug("delete channel {} from database", channelId);
		Document document = new Document();
		document.put(ChannelDBField.CHANNEL_ID, channelId);
		getUserCollection().deleteOne(document);
	}

	@Override
	public void deleteAll(Collection<String> userNames) {
		for (String userName : userNames) {
			delete(userName);
		}
	}

	@Override
	public void store(String key, ChannelMongoBean channelBean) {
		Document document = channelBean.toDocument();
		Document where = new Document(ChannelDBField.CHANNEL_ID, key);
		try {
			getUserCollection().updateOne(where, new Document("$set", document), new UpdateOptions().upsert(true));
		} catch (Exception exception) {
			getLogger().debug("error when store channel to disk" + exception);
		}
	}

	@Override
	public void storeAll(Map<String, ChannelMongoBean> channelBeans) {
		getLogger().debug("store all channel from mapstore to disk");
		try {
			List<WriteModel<Document>> writeModels = new ArrayList<>();
			for (Entry<String, ChannelMongoBean> entry : channelBeans.entrySet()) {
				Document document = entry.getValue().toDocument();
				Document where = new Document(ChannelDBField.CHANNEL_ID, entry.getKey());
				UpdateOneModel<Document> model = new UpdateOneModel<>(where, new Document("$set", document),
						new UpdateOptions().upsert(true));
				writeModels.add(model);
			}
			if (writeModels.size() > 0) {
				com.mongodb.bulk.BulkWriteResult result = getUserCollection().bulkWrite(writeModels);
				getLogger().debug("strore {} channel to disk", result.getModifiedCount());
				List<BulkWriteUpsert> upserts = result.getUpserts();
				getLogger().debug("upserts size: {}", upserts.size());
			}
		} catch (Exception exception) {
			getLogger().debug("error when store all channel to disk " + exception);
		}
	}

}
