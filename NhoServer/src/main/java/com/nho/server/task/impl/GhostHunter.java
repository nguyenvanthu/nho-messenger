package com.nho.server.task.impl;

import java.util.Map;

import com.nho.server.NhoServer;
import com.nho.server.entity.user.User;
import com.nho.server.helper.LoggingHelper;
import com.nho.server.task.AbstractTask;
import com.nho.server.task.Timer;
import com.nho.uams.statics.ActivityType;

public class GhostHunter extends AbstractTask {

	public GhostHunter(NhoServer context) {
		super.setContext(context);
	}

	@Override
	public void run() {
		try {
			long currentTime = System.currentTimeMillis();
			Map<String, Long> lastPingTimes = this.getContext().getLastPingTimes();
			if (lastPingTimes.size() <= 0) {
				return;
			}
			for (Long lastTime : lastPingTimes.values()) {
				if (currentTime - lastTime > Timer.TIME_OUT) {
					String sessionId = this.getContext().getSessionIdByLastPingTime(lastTime);
					if (sessionId == null) {
						return;
					}
					User user = this.getContext().getUserManager().getUserBySessionId(sessionId);
					if (user == null) {
						return;
					}
					getLogger().debug("kill connection of user {}",user.getUserName());
					this.getContext().getUserManager().whenUserDisconect(user.getUserName(), sessionId);
					sendLog(user.getUserName(),sessionId);
				}
			}
		} catch (Exception exception) {
			getLogger().debug("exception when run ghost hunter: " + exception);
		}
	}

	private void sendLog(String userName,String sessionId) {
		String content = "user " + userName + " is diconnected";
		LoggingHelper helper = new LoggingHelper(this.getContext());
		helper.sendMessageLogByRabbit(content, ActivityType.DISCONECT, userName, sessionId);
	}
}
