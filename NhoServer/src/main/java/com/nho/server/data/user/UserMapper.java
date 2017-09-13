package com.nho.server.data.user;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import com.nho.server.statics.DBF;
import com.nho.statics.F;

public class UserMapper implements ResultSetMapper<UserBean> {

	@Override
	public UserBean map(int idx, ResultSet rs, StatementContext stmtCtx) throws SQLException {
		UserBean bean = new UserBean();
		//bean.setId(rs.getBytes(F.ID));
		bean.setSalt(rs.getBytes(F.SALT));
		bean.setPassword(rs.getBytes(F.PASSWORD));
		bean.setDisabled(rs.getBoolean(F.DISABLED));
		bean.setUserName(rs.getString(DBF.USERNAME));
		bean.setCreatedTime(rs.getInt(DBF.CREATED_TIME));
		return bean;
	}

}
