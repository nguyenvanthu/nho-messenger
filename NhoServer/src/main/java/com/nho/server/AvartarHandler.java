package com.nho.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.exception.ExceptionUtils;

import com.mario.entity.impl.BaseCommandRoutingHandler;
import com.mario.entity.message.Message;
import com.nhb.common.data.PuElement;
import com.nhb.common.data.PuObject;
import com.nhb.common.data.PuObjectRO;
import com.nhb.common.data.PuValue;
import com.nhb.common.db.models.ModelFactory;
import com.nho.server.data.AvatarModel;
import com.nho.server.data.avatar.AvatarBean;
import com.nho.server.entity.avatar.AvatarManager;
import com.nho.statics.AvatarType;
import com.nho.statics.F;

public class AvartarHandler extends BaseCommandRoutingHandler {
	private ModelFactory modelFactory  ;
	private AvatarManager avatarManager ;
	private Map<String, String> avatars = new HashMap<>();
	
	public ModelFactory getModelFactory(){
		return this.modelFactory;
	}
	
	public void setModelFactory(ModelFactory modelFactory){
		this.modelFactory = modelFactory;
	}
	
	private AvatarModel avatarModel;
	
	public AvatarModel getAvatarModel(){
		if(this.avatarModel == null ){
			this.avatarModel = new AvatarModel();
		}
		return this.avatarModel;
	}
	public void setAvatarModel(AvatarModel avatarModel){
		this.avatarModel = avatarModel;
	}
	
	public AvatarManager getAvatarManager(){
		return this.avatarManager;
	}
	@Override
	public void init(PuObjectRO initParams) {
		this.modelFactory = new ModelFactory(getApi().getDatabaseAdapter(initParams.getString("mysql")));
		modelFactory.setClassLoader(this.getClass().getClassLoader());
		avatarManager = new AvatarManager();
		if(initParams.variableExists(F.AVATARS)){
			try{
				this.avatars = getAvatars(initParams);
			}catch(Exception exception){
				exception.printStackTrace();
			}
		}
		
		if(initParams.variableExists(F.COMMANDS)){
			try {
				this.initCommandController(initParams.getPuObject(F.COMMANDS));
				this.getCommandController().setEnviroiment("handler", this);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private Map<String,String> getAvatars(PuObjectRO initParams){
		Map<String,String> urls = new HashMap<>();
		PuObject avatars = initParams.getPuObject(F.AVATARS);
		for (Entry<String, PuValue> entry : avatars) {
			if (entry.getValue() != null) {
				urls.put(entry.getKey(), entry.getValue().getString());
				this.avatarManager.registerAvatar(AvatarType.ICON, entry.getKey(), entry.getValue().getString());
			}
		}
		return urls;
 	}
	
	@Override
	public PuElement handle(Message message) {
		PuObject data = (PuObject)message.getData();
		Object result = null;
		int status = 1 ;
		
		try{
			String command = data.getString(F.COMMAND);
			if(command!=null){
				switch (command) {
				case F.GET_AVATARS:
					List<AvatarBean> avatars = new ArrayList<>();
					for(Entry<String, String> entry : this.avatars.entrySet()){
						AvatarBean avatar = new AvatarBean();
						avatar.autoId();
						avatar.setType(1);
						avatar.setName(entry.getKey());
						avatar.setUrl(entry.getValue());
						avatars.add(avatar);
					}
					status = 0;
					result = avatars;
					break;

				default:
					break;
				}
			}
		}catch(Exception exception){
			getLogger().debug("error", exception);
			result = exception;
		}
		PuObject response = new PuObject();
		response.setInteger(F.STATUS, status);
		if (result != null) {
			if (result instanceof Throwable) {
				result = ExceptionUtils.getFullStackTrace((Throwable) result);
			}
			response.set(F.DATA, result);
		}
		return response;
	}
	
	@Override
	public PuElement interop(PuElement requestParams) {
		return super.interop(requestParams);
	}
	
}

