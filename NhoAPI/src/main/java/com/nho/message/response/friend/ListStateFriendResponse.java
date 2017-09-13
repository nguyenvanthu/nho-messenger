package com.nho.message.response.friend;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.nhb.common.data.PuArray;
import com.nhb.common.data.PuDataType;
import com.nhb.common.data.PuValue;
import com.nho.message.MessageType;
import com.nho.message.NhoMessage;
import com.nho.statics.Error;
import com.nho.statics.StatusUser;

public class ListStateFriendResponse extends NhoMessage{
	{
		this.setType(MessageType.LIST_STATE_FRIEND_RESPONSE);
	}
	private boolean success;
	private Error error ;
	private Set<String> userNames ;
	private List<StatusUser> statuss;
	
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public Error getError() {
		return error;
	}
	public void setError(Error error) {
		this.error = error;
	}

	
	
	public Set<String> getUserNames() {
		return userNames;
	}
	public void setUserNames(Set<String> userNames) {
		this.userNames = userNames;
	}
	public List<StatusUser> getStatuss() {
		return statuss;
	}
	public void setStatuss(List<StatusUser> statuss) {
		this.statuss = statuss;
	}
	
	private List<Integer> getCodeStatuss(){
		List<Integer> codes = new ArrayList<>();
		for(StatusUser status : this.statuss){
			codes.add(status.getCode());
		}
		return codes;
	}
	
	@Override
	protected void writePuArray(PuArray puArray) {
		puArray.addFrom(this.success);
		if(this.success){
			puArray.addFrom(this.userNames);
			puArray.addFrom(getCodeStatuss());
		}else {
			puArray.addFrom(this.getError() == null ? null : this.getError().getCode());
		}
	}
	
	@Override
	protected void readPuArray(PuArray puArray) {
		this.success = puArray.remove(0).getBoolean();
		if(this.success){
			PuArray userNameArray = puArray.remove(0).getPuArray();
			if (userNameArray != null) {
				this.userNames = new HashSet<String>();
				for (PuValue value : userNameArray) {
					this.userNames.add(value.getString());
				}
			}
			
			PuArray statusArray = puArray.remove(0).getPuArray();
			if (statusArray != null) {
				this.statuss = new ArrayList<StatusUser>();
				for (PuValue value : statusArray) {
					this.statuss.add(StatusUser.fromCode(value.getInteger()));
				}
			}
		}
		else {
			PuValue error = puArray.remove(0);
			if(error != null && error.getType() != PuDataType.NULL){
				this.setError(Error.fromCode(error.getInteger()));
			}
		}
	}
}
