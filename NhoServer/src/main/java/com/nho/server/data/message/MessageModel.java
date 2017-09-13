package com.nho.server.data.message;

import java.util.Arrays;

import com.nhb.common.db.models.AbstractModel;

public class MessageModel extends AbstractModel {

	@Override
	protected void init() {

	}

	public void insert(MessageBean... messages) {
		try (MessageDAO dao = this.getCassandraDAOFactory().newDAOInstance(MessageDAO.class)) {
			dao.insert(Arrays.asList(messages));
		}
	}
}
