package com.nho.server.data.message;

import java.util.Collection;

import com.datastax.driver.core.BatchStatement;
import com.datastax.driver.core.BoundStatement;
import com.nhb.common.db.cassandra.daos.BaseCassandraDAO;

public class MessageDAO extends BaseCassandraDAO {

	public static final String CQL_INSERT_MESSAGE = "";

	public void insert(Collection<MessageBean> beans) {

		BoundStatement stmt = this.getStatement(CQL_INSERT_MESSAGE);
		BatchStatement batch = new BatchStatement();
		for (MessageBean bean : beans) {
			batch.add(stmt.bind(bean.getUuid(), bean.getFromUserUUID(), bean.getToChannelUUID(), bean.getContent(),
					bean.getTimestamp()));
		}
		this.execute(batch);
	}
}
