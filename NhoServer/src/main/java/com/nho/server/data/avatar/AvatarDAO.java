package com.nho.server.data.avatar;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import com.nhb.common.db.sql.daos.AbstractDAO;

@RegisterMapper(AvatarMapper.class)
public abstract class AvatarDAO extends AbstractDAO{
	@SqlQuery("SELECT * FROM `avatar` WHERE `id` = :id")
	public abstract AvatarBean fetchById(@Bind("id") byte[] id); 
	
	@SqlUpdate("INSERT INTO `avatar` (`id`, `url`, `type`) VALUES (:id, :url, :type);")
	public abstract int insert(@BindBean AvatarBean bean);
	
}
