package com.nho.uams.daos;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.datastax.driver.core.BatchStatement;
import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Statement;
import com.nhb.common.db.cassandra.daos.BaseCassandraDAO;
import com.nho.uams.data.UserActivityBean;
import com.nho.uams.statics.UAMSApiField;

public class UserActivityDAO extends BaseCassandraDAO {
	private static final String CQL_INSERT_ACTIVITY = "INSERT INTO activity (id, activity_type, application_id, content, reference_id, timestamp, user_name) values (?, ?, ?, ?, ?, ?, ?)";
	private static final String CQL_FETCH_ACTIVITIES_BY_USERNAME = "SELECT * FROM activity WHERE user_name = ? ";
	private static final String CQL_GET_TOTAL_LOG_BY_USERNAME = "select count(*) from activity where user_name = ? and application_id =? ALLOW FILTERING";
	private static final String CQL_GET_TOTAL_LOG_BY_REFERENCE_ID = "select count(*) from activity where reference_id = ? and application_id = ? ALLOW FILTERING";
	private static final String CQL_GET_TOTAL_LOG_BY_ACTIVITY = "select count(*) from activity where activity_type = ? and application_id = ? ALLOW FILTERING";
	private static final String CQL_GET_TOTAL_LOG_BY_ACTIVITY_TIME = "select count(*) from activity where activity_type = ? and application_id = ? and timestamp >= ? and timestamp < ? ALLOW FILTERING";
	private static final String CQL_FETCH_LOG_BY_USERNAME = "select * from activity where user_name = ? and application_id = ? ALLOW FILTERING";
	private static final String CQL_FETCH_LOG_BY_REFERENCEID = "select * from activity where reference_id = ? and application_id = ? ALLOW FILTERING";
	private static final String CQL_FETCH_LOG_BY_ACTIVITY = "select * from activity where activity_type = ? and application_id = ? ALLOW FILTERING";
	private static final String CQL_FETCH_LOG_USER_ACTIVITY = "select * from activity where user_name = ? and activity_type = ? and application_id = ? ALLOW FILTERING";
	private static final String CQL_FETCH_LOG_BY_TIMESTAMP = "select * from activity where application_id = ? and timestamp >= ? and timestamp < ? ALLOW FILTERING";
	private static final String CQL_FETCH_LOG_BY_ACTIVITY_TIMESTAMP = "select * from activity where application_id = ? and activity_type = ? and timestamp >= ? and timestamp < ? ALLOW FILTERING";
	private static final String CQL_CREATE_TABLE = "create table if not exists activity (id text PRIMARY KEY, activity_type int, application_id text, content text, reference_id text, timestamp bigint, user_name text)";

	public void createActivityTable() {
		BoundStatement statement = this.getStatement(CQL_CREATE_TABLE);
		this.execute(statement);
	}

	public boolean insert(UserActivityBean bean) {
		if (bean != null) {
			BoundStatement stmt = this.getStatement(CQL_INSERT_ACTIVITY);
			stmt.bind(bean.getUuid(), bean.getActivityType(), bean.getApplicationId(), bean.getContent(),
					bean.getReferenceId(), bean.getTimestamp(), bean.getUserName());
			this.execute(stmt);
			return true;
		}
		return false;
	}

	public boolean insert(UserActivityBean... beans) {
		return this.insert(Arrays.asList(beans));
	}

	public boolean insert(Collection<UserActivityBean> beans) {
		if (beans != null) {
			BatchStatement batch = new BatchStatement();
			for (UserActivityBean bean : beans) {
				batch.add(this.getStatement(CQL_INSERT_ACTIVITY).bind(bean.getUuid(), bean.getActivityType(),
						bean.getApplicationId(), bean.getContent(), bean.getReferenceId(), bean.getTimestamp(),
						bean.getUserName()));
			}
			this.execute(batch);
			return true;
		}
		return false;
	}

	private Collection<UserActivityBean> processRows(Collection<Row> rows) {
		if (rows == null) {
			return null;
		}
		List<UserActivityBean> activities = new ArrayList<>();
		for (Row row : rows) {
			UserActivityBean activityBean = new UserActivityBean();
			activityBean.setId(row.getUUID(UAMSApiField.ID));
			activityBean.setApplicationId(row.getString(UAMSApiField.APPLICATION_ID));
			activityBean.setContent(row.getString(UAMSApiField.CONTENT));
			activityBean.setReferenceId(row.getString(UAMSApiField.REFERENCE_ID));
			activityBean.setTimestamp(row.getLong(UAMSApiField.TIMESTAMP));
			activityBean.setUserName(row.getString(UAMSApiField.USERNAME));
			activities.add(activityBean);
		}
		return activities;
	}

	public Collection<UserActivityBean> fetch(String userName) {
		ResultSet rs = this.execute(this.getStatement(CQL_FETCH_ACTIVITIES_BY_USERNAME).bind(userName));

		Collection<UserActivityBean> activities = processRows(rs.all());
		return activities;
	}

	public long getTotalLogByUserName(String userName, String applicationId) {
		if (userName != null) {
			BoundStatement stmt = this.getStatement(CQL_GET_TOTAL_LOG_BY_USERNAME);
			stmt.bind(userName, applicationId);
			ResultSet rows = this.execute(stmt);
			return rows.all().get(0).getLong(UAMSApiField.COUNT);
		}
		return 0;
	}

	public long getTotalLogByReferenceId(String applicationId, String referenceId) {
		if (referenceId != null) {
			BoundStatement stmt = this.getStatement(CQL_GET_TOTAL_LOG_BY_REFERENCE_ID);
			stmt.bind(referenceId, applicationId);
			ResultSet rows = this.execute(stmt);
			return rows.all().get(0).getLong(UAMSApiField.COUNT);
		}
		return 0;
	}

	public long getTotalLogByActivityType(String applicationId, int activityType) {
		BoundStatement statement = this.getStatement(CQL_GET_TOTAL_LOG_BY_ACTIVITY);
		statement.bind(activityType, applicationId);
		ResultSet rows = this.execute(statement);
		return rows.all().get(0).getLong(UAMSApiField.COUNT);
	}

	public Collection<UserActivityBean> fetchLogByActivityType(String applicationId, int acvitityType) {
		BoundStatement statement = this.getStatement(CQL_FETCH_LOG_BY_ACTIVITY);
		statement.bind(acvitityType, applicationId);
		ResultSet rows = this.execute(statement);
		return processRows(rows.all());
	}

	public Collection<UserActivityBean> fetchLogByUserName(String applicationId, String userName) {
		if (userName != null) {
			BoundStatement stm = this.getStatement(CQL_FETCH_LOG_BY_USERNAME);
			stm.bind(userName, applicationId);
			// CassandraPaging paging = new CassandraPaging(this);
			// List<Row> rows = paging.fetchRowsWithPage(stm, start, size);
			ResultSet rows = this.execute(stm);
			return processRows(rows.all());
		}
		return null;
	}

	@Override
	public ResultSet execute(Statement statement) {
		return super.execute(statement);
	}

	public Collection<UserActivityBean> fetchLogByReferenceId(String applicationId, String referenceId) {
		if (referenceId != null) {
			BoundStatement stm = this.getStatement(CQL_FETCH_LOG_BY_REFERENCEID);
			stm.bind(referenceId, applicationId);
			// CassandraPaging paging = new CassandraPaging(this);
			// List<Row> rows = paging.fetchRowsWithPage(stm, start, display);
			ResultSet rows = this.execute(stm);
			return processRows(rows.all());
		}
		return null;
	}

	public Collection<UserActivityBean> fetchLogByTimeStamp(String applicationId, long startTime, long endTime) {
		BoundStatement statement = this.getStatement(CQL_FETCH_LOG_BY_TIMESTAMP);
		statement.bind(applicationId, startTime, endTime);
		ResultSet rows = this.execute(statement);
		return processRows(rows.all());
	}

	public Collection<UserActivityBean> fetchLogByUserActivity(String userName, int activityType,
			String applicationId) {
		BoundStatement statement = this.getStatement(CQL_FETCH_LOG_USER_ACTIVITY);
		statement.bind(userName, activityType, applicationId);
		ResultSet rows = this.execute(statement);
		return processRows(rows.all());
	}

	public Collection<UserActivityBean> fetchLogByActivityAndTimeStamp(String applicationId, int activityType,
			long startTime, long endTime) {
		BoundStatement statement = this.getStatement(CQL_FETCH_LOG_BY_ACTIVITY_TIMESTAMP);
		statement.bind(applicationId, activityType, startTime, endTime);
		ResultSet rows = this.execute(statement);
		return processRows(rows.all());
	}

	public long fetchTotalLogByActivityAndTimeStamp(String applicationId, int activityType, long startTime,
			long endTime) {
		BoundStatement statement = this.getStatement(CQL_GET_TOTAL_LOG_BY_ACTIVITY_TIME);
		statement.bind( activityType,applicationId, startTime, endTime);
		ResultSet rows = this.execute(statement);
		return rows.all().get(0).getLong(UAMSApiField.COUNT);
	}
}
