package com.nho.server.data.profile;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import com.nhb.common.db.sql.daos.AbstractDAO;

@RegisterMapper(ProfileMapper.class)
public abstract class ProfileDAO extends AbstractDAO {

	@SqlUpdate("INSERT INTO `profile` (`id`, `user_id`, `is_default`, `display_name`, `birthday`, `gender`, `created_time`) "
			+ "VALUES (:id, :userId, :default, :displayName, :birthday, :genderValue, :createdTime);")
	public abstract int insert(@BindBean ProfileBean bean);

	@SqlQuery("SELECT * FROM `profile` WHERE id = :id")
	public abstract ProfileBean fetchById(@Bind("id") byte[] id);

	@SqlQuery("SELECT * FROM `profile` WHERE user_id = :userId AND is_default = 1")
	public abstract ProfileBean fetchDefaultByUserId(@Bind("userId") byte[] userId);
}
