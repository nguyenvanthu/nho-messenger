package com.nho.tracking;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bson.Document;

import com.mario.entity.impl.BaseMessageHandler;
import com.mario.entity.message.Message;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.nhb.common.data.MapTuple;
import com.nhb.common.data.PuDataType;
import com.nhb.common.data.PuElement;
import com.nhb.common.data.PuObject;
import com.nhb.common.data.PuObjectRO;
import com.nhb.common.db.models.ModelFactory;
import com.nho.tracking.model.ExceptionBean;
import com.nho.tracking.model.ExceptionModel;
import com.nho.tracking.statics.ExceptionType;

public class NhoTrackingHandler extends BaseMessageHandler {
	private ModelFactory modelFactory;
	private MongoClient mongoClient;
	private ExceptionModel exceptionModel ;

	@Override
	public void init(PuObjectRO initParams) {
		super.init(initParams);
		getLogger().debug("NhoTracking server starting ....");
		ModelFactory modelFactory = new ModelFactory();
		modelFactory.setClassLoader(this.getClass().getClassLoader());
		mongoClient = getApi().getMongoClient(initParams.getString(EF.MONGODB));
		modelFactory.setMongoClient(mongoClient);
		this.modelFactory = modelFactory;
		initDatabase();
	}

	public ModelFactory getModelFactory() {
		return modelFactory;
	}

	@Override
	public PuElement handle(Message message) {
		return processMessage((PuObject) message.getData());
	}

	@Override
	public PuElement interop(PuElement requestParams) {
		return processMessage((PuObject) requestParams);
	}

	private PuObject processMessage(PuObject data) {
		data.setType(EF.COMMAND, PuDataType.STRING);
		if (data.variableExists(EF.COMMAND)) {
			String command = data.getString(EF.COMMAND);
			getLogger().debug("receive command " + command);
			data.setType(EF.TITLE, PuDataType.STRING);
			data.setType(EF.STACKTRACE, PuDataType.STRING);
			String title = data.getString(EF.TITLE);
			String stackTrace = data.getString(EF.STACKTRACE);
			int type = data.getInteger(EF.TYPE);
			ExceptionBean bean = new ExceptionBean();
			bean.setStackTrace(stackTrace);
			bean.setTitle(title);
			bean.setType(ExceptionType.fromCode(type));
			boolean isSuccess = this.getExceptionModel().insertException(bean);
			if(isSuccess){
				getLogger().debug("insert new exception ");
				return PuObject.fromObject(new MapTuple<>(EF.STATUS, 0));
			}
		}
		return PuObject.fromObject(new MapTuple<>(EF.STATUS, 1));
	}

	private void initDatabase() {
		createDatabaseIndexes(EF.EXCEPTION_COLLECTION,
				new ArrayList<>(Arrays.asList(new Document().append(EF.TITLE, 1), new Document().append(EF.TYPE, 1))));
	}

	private void createDatabaseIndexes(String collectionName, List<Document> tobeIndexed) {
		MongoCollection<Document> collection = this.mongoClient.getDatabase(EF.NHO_DATABASE)
				.getCollection(collectionName);
		for (Document index : collection.listIndexes()) {
			index = (Document) index.get(EF.KEY);
			List<Integer> markToRemove = new ArrayList<>();
			for (int i = 0; i < tobeIndexed.size(); i++) {
				if (tobeIndexed.get(i).equals(index)) {
					markToRemove.add(i);
				}
			}
			if (markToRemove.size() > 0) {
				while (markToRemove.size() > 0) {
					tobeIndexed.remove(markToRemove.remove(markToRemove.size() - 1).intValue());
				}
			}

			if (tobeIndexed.size() == 0) {
				break;
			}
		}
		for (Document index : tobeIndexed) {
			getLogger().debug("create index: " + index);
			collection.createIndex(index);
		}
	}
	
	private ExceptionModel getExceptionModel(){
		if(this.exceptionModel == null){
			this.exceptionModel = this.getModelFactory().newModel(ExceptionModel.class);
		}
		return this.exceptionModel;
	}
}
