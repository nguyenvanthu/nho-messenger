package com.nho.server.data.avatar;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import com.nho.statics.F;

public class AvatarMapper implements ResultSetMapper<AvatarBean>{

	@Override
	public AvatarBean map(int index, ResultSet resultSet, StatementContext statementContect) throws SQLException {
		AvatarBean avatar = new AvatarBean();
		avatar.autoId();
		avatar.setUrl(resultSet.getString(F.URL));
		avatar.setType(resultSet.getInt(F.TYPE));
		avatar.setName(resultSet.getString(F.NAME));
		return avatar;
	}

}
