package com.nho.server.data.user;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import com.nhb.common.db.sql.daos.AbstractDAO;

@RegisterMapper(UserMapper.class)
public abstract class UserDAO extends AbstractDAO {

	@SqlUpdate("INSERT INTO `user` (`id`, `username`, `password`, `salt`, `disabled`, `created_time`) VALUES (:id, :userName, :password, :salt, :disabled, :createdTime);")
	public abstract int insert(@BindBean UserBean bean);

	@SqlQuery("SELECT * FROM `user` WHERE `id` = :id")
	public abstract UserBean fetchById(@Bind("id") byte[] id);

	@SqlQuery("SELECT * FROM `user` WHERE `username` = :userName")
	public abstract UserBean fetchByUsername(@Bind("userName") String userName);

	@SqlUpdate("UPDATE `user` SET `password` = :password WHERE `id` = :id;")
	public abstract int updatePassword(@Bind("id") byte[] id, @Bind("password") byte[] password);
}
