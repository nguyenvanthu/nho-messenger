package com.nho.server.data.user;

import com.hazelcast.config.Config;
import com.hazelcast.config.MapStoreConfig;
import com.hazelcast.config.MapStoreConfig.InitialLoadMode;
import com.mario.cache.hazelcast.HazelcastInitializer;
import com.mongodb.client.MongoDatabase;
import com.nhb.common.BaseLoggable;

@SuppressWarnings("deprecation")
public class NhoHazelcastInitializer extends BaseLoggable implements HazelcastInitializer {
	private MongoDatabase database;
	private int writeBatchSize;
	private int writeDelaySeconds;
	public static final String USER_MAP_KEY = "nho:users";

	public NhoHazelcastInitializer(MongoDatabase database, int writeBatchSize, int writeDelaySeconds) {
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
		mapStoreConfig.setImplementation(new UserMapStore(database));

		config.getMapConfig(USER_MAP_KEY).setMapStoreConfig(mapStoreConfig);
	}

}
