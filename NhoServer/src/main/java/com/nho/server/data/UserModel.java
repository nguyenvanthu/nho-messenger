package com.nho.server.data;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Date;

import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.exceptions.DBIException;

import com.nhb.common.db.models.AbstractModel;
import com.nhb.common.encrypt.sha.SHAEncryptor;
import com.nho.server.data.profile.ProfileBean;
import com.nho.server.data.profile.ProfileDAO;
import com.nho.server.data.user.UserBean;
import com.nho.server.data.user.UserDAO;
import com.nho.statics.Gender;

public class UserModel extends AbstractModel {

	public boolean createNewUser(String userName, String password, String displayName, Gender gender,
			String birthdayMySQLEscapeFormat) {
		if (userName != null && password != null) {
			userName = userName.trim();
			if (userName.length() > 0) {
				try (Handle handle = this.newHandle();
						UserDAO userDAO = this.openDAO(UserDAO.class, handle);
						ProfileDAO profileDAO = this.openDAO(ProfileDAO.class, handle)) {

					UserBean user = new UserBean();
					user.autoId();
					user.autoSalt();
					user.autoCreatedTime();
					user.setUserName(userName);

					ByteArrayOutputStream os = new ByteArrayOutputStream();
					try {
						os.write(password.getBytes());
						os.write(user.getSalt());
					} catch (IOException ex) {
						throw new RuntimeException(ex);
					}
					user.setPassword(SHAEncryptor.sha512(os.toByteArray()));

					ProfileBean profile = new ProfileBean();
					profile.autoId();
					profile.autoCreatedTime();
					profile.setUserId(user.getId());
					profile.setDefault(true);
					profile.setDisplayName(displayName);
					profile.setGender(gender == null ? Gender.UNKNOWN : gender);
					if (birthdayMySQLEscapeFormat != null) {
						profile.setBirthday(Date.valueOf(birthdayMySQLEscapeFormat));
					}

					handle.begin();
					userDAO.insert(user);
					profileDAO.insert(profile);

					try {
						handle.commit();
						return true;
					} catch (DBIException ex) {
						getLogger().debug("error when commit " + ex);
						handle.rollback();
						return false;
					}
				} catch (DBIException ex) {
					getLogger().debug("error dbi " + ex);
					return false;
				}
			}
		}
		return false;
	}

	public UserBean getUser(byte[] id) {
		try (UserDAO userDAO = this.openDAO(UserDAO.class)) {
			return userDAO.fetchById(id);
		}
	}

	public UserBean getUserByUserName(String userName) {
		try (UserDAO userDAO = this.openDAO(UserDAO.class)) {
			return userDAO.fetchByUsername(userName);
		}
	}

	public ProfileBean getProfile(byte[] userId) {
		try (ProfileDAO dao = this.openDAO(ProfileDAO.class)) {
			return dao.fetchDefaultByUserId(userId);
		}
	}
}
