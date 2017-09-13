package com.nho.server.data.profile;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import com.nho.server.statics.DBF;
import com.nho.statics.F;

public class ProfileMapper implements ResultSetMapper<ProfileBean> {

	@Override
	public ProfileBean map(int idx, ResultSet rs, StatementContext stmtCtx) throws SQLException {
		ProfileBean bean = new ProfileBean();
		bean.setId(rs.getBytes(F.ID));
		bean.setUserId(rs.getBytes(DBF.USER_ID));
		bean.setDefault(rs.getBoolean(DBF.IS_DEFAULT));
		bean.setDisplayName(rs.getString(DBF.DISPLAY_NAME));
		bean.setBirthday(rs.getDate(F.BIRTHDAY));
		bean.setGenderValue(rs.getInt(F.GENDER));
		bean.setCreatedTime(rs.getInt(DBF.CREATED_TIME));
		return bean;
	}

}
