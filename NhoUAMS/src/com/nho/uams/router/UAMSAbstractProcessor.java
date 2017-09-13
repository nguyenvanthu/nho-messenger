package com.nho.uams.router;

import com.nhb.common.BaseLoggable;
import com.nhb.common.data.PuElement;
import com.nho.uams.UAMSHandler;
import com.nho.uams.data.UserActivityModel;
import com.nho.uams.message.UAMSMessage;

public abstract class UAMSAbstractProcessor<HandledType extends UAMSMessage> extends BaseLoggable
		implements UAMSProcessor {
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

	@SuppressWarnings("unchecked")
	@Override
	public PuElement execute(UAMSMessage request) {
		try {
			try {
				return this.process((HandledType) request);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (ClassCastException ex) {
			throw new RuntimeException("Request type invalid", ex);
		}
		return null;
	}

	protected abstract PuElement process(HandledType request);
}
