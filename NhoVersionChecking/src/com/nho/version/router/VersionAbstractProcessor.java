package com.nho.version.router;

import com.nhb.common.BaseLoggable;
import com.nho.version.VersionCheckingHandler;
import com.nho.version.model.VersionModel;

public abstract class VersionAbstractProcessor extends BaseLoggable implements VersionProcessor{
	private VersionCheckingHandler context ;
	private VersionModel versionModel;
	
	public VersionCheckingHandler getContext() {
		return context;
	}

	public void setContext(VersionCheckingHandler context) {
		this.context = context;
	}
	
	protected VersionModel getVersionModel(){
		if(this.versionModel == null ){
			this.versionModel = getContext().getModelFactory().newModel(VersionModel.class);
		}
		return this.versionModel;
	}
	
}
