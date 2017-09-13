package com.nho.server.task.impl;

import com.nho.server.NhoServer;
import com.nho.server.task.AbstractTask;

public class ChangeTokenUserTask extends AbstractTask {

	public ChangeTokenUserTask(NhoServer context) {
		super.setContext(context);
	}
	@Override
	public void run() {
//		List<UserMongoBean> userBeans = this.getUserMongoModel().findAllUser();
//		for(UserMongoBean bean : userBeans){
//			this.getUserMongoModel().updateChecksum("", bean.getObjectId());
//		}
	}
}
