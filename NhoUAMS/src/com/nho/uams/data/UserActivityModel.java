package com.nho.uams.data;

import java.util.Arrays;
import java.util.Collection;

import com.nhb.common.db.models.AbstractModel;
import com.nho.uams.daos.UserActivityDAO;

public class UserActivityModel extends AbstractModel {

	public void createTable() {
		try (UserActivityDAO dao = this.getCassandraDAOFactory().newDAOInstance(UserActivityDAO.class)) {
			dao.createActivityTable();
		} catch (Exception e) {
			getLogger().error("error when insert activity", e);
		}
	}

	public boolean insert(UserActivityBean activityBean) {
		try (UserActivityDAO dao = this.getCassandraDAOFactory().newDAOInstance(UserActivityDAO.class)) {
			return dao.insert(activityBean);
		} catch (Exception e) {
			getLogger().error("error when insert activity", e);
		}
		return false;
	}

	public boolean insert(UserActivityBean... beans) {
		return this.insert(Arrays.asList(beans));
	}

	public boolean insert(Collection<UserActivityBean> beans) {
		try (UserActivityDAO dao = this.getCassandraDAOFactory().newDAOInstance(UserActivityDAO.class)) {
			return dao.insert(beans);
		} catch (Exception e) {
			getLogger().error("error when insert activity", e);
		}
		return false;
	}

	public Collection<UserActivityBean> fetchLogByUserName(String applicationId, String userName) {
		try (UserActivityDAO dao = this.getCassandraDAOFactory().newDAOInstance(UserActivityDAO.class)) {
			return dao.fetchLogByUserName(applicationId, userName);
		} catch (Exception e) {
			getLogger().error("error when fetch log by userName: " + userName, e);
		}
		return null;
	}

	public Collection<UserActivityBean> fetchLogByActivity(String applicationId, int activityType) {
		try (UserActivityDAO dao = this.getCassandraDAOFactory().newDAOInstance(UserActivityDAO.class)) {
			return dao.fetchLogByActivityType(applicationId, activityType);
		} catch (Exception e) {
			getLogger().error("error when fetch log by activity: " + activityType, e);
		}
		return null;
	}

	public long getTotalLogByUserName(String userName, String applicationId) {
		try (UserActivityDAO dao = this.getCassandraDAOFactory().newDAOInstance(UserActivityDAO.class)) {
			return dao.getTotalLogByUserName(userName, applicationId);
		} catch (Exception e) {
			getLogger().error("error when get total log by userName: " + userName, e);
		}
		return 0;
	}

	public long getTotalLogByReferenceId(String referenceId, String applicationId) {
		try (UserActivityDAO dao = this.getCassandraDAOFactory().newDAOInstance(UserActivityDAO.class)) {
			return dao.getTotalLogByReferenceId(applicationId, referenceId);
		} catch (Exception e) {
			getLogger().error("error when get total log by referenceId: " + referenceId, e);
		}
		return 0;
	}

	public long getTotalLogByActivityType(int activityType, String applicationId) {
		try (UserActivityDAO dao = this.getCassandraDAOFactory().newDAOInstance(UserActivityDAO.class)) {
			return dao.getTotalLogByActivityType(applicationId, activityType);
		} catch (Exception e) {
			getLogger().error("error when get total log by activityType: " + activityType, e);
		}
		return 0;
	}

	public Collection<UserActivityBean> fetchLogByReferenceId(String referenceId, String applicationId) {
		try (UserActivityDAO dao = this.getCassandraDAOFactory().newDAOInstance(UserActivityDAO.class)) {
			return dao.fetchLogByReferenceId(applicationId, referenceId);
		} catch (Exception e) {
			getLogger().error("error when get log by referenceId: " + referenceId, e);
		}
		return null;
	}

	public Collection<UserActivityBean> fetchLogByTimeStamp(long startTime, long endTime, String applicationId) {
		try (UserActivityDAO dao = this.getCassandraDAOFactory().newDAOInstance(UserActivityDAO.class)) {
			return dao.fetchLogByTimeStamp(applicationId, startTime, endTime);
		} catch (Exception e) {
			getLogger().error("error when get log by timeStamp: ", e);
		}
		return null;
	}

	public Collection<UserActivityBean> fetchLogByUserActivity(String userName, int activityType,
			String applicationId) {
		try (UserActivityDAO dao = this.getCassandraDAOFactory().newDAOInstance(UserActivityDAO.class)) {
			return dao.fetchLogByUserActivity(userName, activityType, applicationId);
		} catch (Exception e) {
			getLogger().error("error when get log by user activity: " + userName, e);
		}
		return null;
	}

	public Collection<UserActivityBean> fetchLogByActivityAndTimestamp(int activityType, long startTime, long endTime,
			String applicationId) {
		try (UserActivityDAO dao = this.getCassandraDAOFactory().newDAOInstance(UserActivityDAO.class)) {
			return dao.fetchLogByActivityAndTimeStamp(applicationId, activityType, startTime, endTime);
		} catch (Exception e) {
			getLogger().error("error when get log by  activity: " + activityType, e);
		}
		return null;
	}

	public long fetchTotalLogByActivityAndTimestamp(int activityType, long startTime, long endTime,
			String applicationId) {
		try (UserActivityDAO dao = this.getCassandraDAOFactory().newDAOInstance(UserActivityDAO.class)) {
			return dao.fetchTotalLogByActivityAndTimeStamp(applicationId, activityType, startTime, endTime);
		} catch (Exception e) {
			getLogger().error("error when get log by  activity: " + activityType, e);
		}
		return 0;
	}
}
