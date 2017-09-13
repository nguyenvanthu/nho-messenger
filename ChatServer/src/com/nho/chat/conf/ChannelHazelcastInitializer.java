package com.nho.chat.conf;

import com.hazelcast.config.Config;
import com.hazelcast.config.MapStoreConfig;
import com.hazelcast.config.MapStoreConfig.InitialLoadMode;
import com.mario.cache.hazelcast.HazelcastInitializer;
import com.mongodb.client.MongoDatabase;
import com.nhb.common.BaseLoggable;

@SuppressWarnings("deprecation")
public class ChannelHazelcastInitializer extends BaseLoggable implements HazelcastInitializer {

	private MongoDatabase database;
	private int writeBatchSize;
	private int writeDelaySeconds;
	public static final String CHANNEL_MAP_KEY = "nho:channels";

	public ChannelHazelcastInitializer(MongoDatabase database, int writeBatchSize, int writeDelaySeconds) {
		this.database = database;
		this.writeBatchSize = writeBatchSize;
		this.writeDelaySeconds = writeDelaySeconds;
	}

	@Override
	public void prepare(Config config) {
		getLogger().info("Preparing Hazelcast instance...");
		MapStoreConfig mapStoreConfig = new MapStoreConfig();
		mapStoreConfig.setWriteBatchSize(writeBatchSize);
		mapStoreConfig.setWriteDelaySeconds(writeDelaySeconds);
		mapStoreConfig.setInitialLoadMode(InitialLoadMode.LAZY);
		mapStoreConfig.setImplementation(new ChannelMapStore(database));

		config.getMapConfig(CHANNEL_MAP_KEY).setMapStoreConfig(mapStoreConfig);
	}
}
