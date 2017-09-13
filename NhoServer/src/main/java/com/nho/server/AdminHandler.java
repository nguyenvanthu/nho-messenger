//package com.nho.server;
//
//import com.mario.entity.impl.BaseCommandRoutingHandler;
//import com.mario.entity.message.Message;
//import com.mongodb.MongoClient;
//import com.nhb.common.data.PuDataType;
//import com.nhb.common.data.PuElement;
//import com.nhb.common.data.PuObject;
//import com.nhb.common.data.PuObjectRO;
//import com.nhb.common.db.models.ModelFactory;
//import com.nho.server.helper.FriendCounter;
//import com.nho.statics.F;
//
//public class AdminHandler extends BaseCommandRoutingHandler {
//	private ModelFactory modelFactory  ;
//	private MongoClient mongoClient;
//	public ModelFactory getModelFactory(){
//		return this.modelFactory;
//	}
//	
//	public void setModelFactory(ModelFactory modelFactory){
//		this.modelFactory = modelFactory;
//	}
//	
//	@Override
//	public void init(PuObjectRO initParams) {
//		ModelFactory modelFactory = new ModelFactory();
//		modelFactory.setClassLoader(this.getClass().getClassLoader());
//		mongoClient = getApi().getMongoClient(initParams.getString(F.MONGODB));
//		modelFactory.setMongoClient(mongoClient);
//		this.modelFactory = modelFactory;
//	}
//	
//	@Override
//	public PuElement handle(Message message) {
//		PuObject dataHttp = (PuObject) message.getData();
//		getLogger().debug(dataHttp.toJSON());
//		dataHttp.setType("command", PuDataType.STRING);
//		if (dataHttp.variableExists("command")) {
//			String command = dataHttp.getString("command");
//			if(command.equals("countFriend")){
//				FriendCounter counter = new FriendCounter(this);
//				PuObject result = counter.getAverageFriend();
//				return result;
//			}
//		}
//		return null;
//	}
//}
