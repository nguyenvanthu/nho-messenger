package com.nho.uams.router;

import com.nhb.common.BaseLoggable;
import com.nho.uams.UAMSHandler;
import com.nho.uams.data.UserActivityModel;

public abstract class NhoUAMSAbstractProcessor extends BaseLoggable implements UAMSProcessor {
	private UAMSHandler context;
	private UserActivityModel model;

	public UAMSHandler getContext() {
		return context;
	}

	public void setContext(UAMSHandler context) {
		this.context = context;
	}

	protected UserActivityModel getActivityModel() {
		if (model == null) {
			model = this.getContext().getModelFactory().newModel(UserActivityModel.class);
			model.createTable();
		}
		return model;
	}
}
