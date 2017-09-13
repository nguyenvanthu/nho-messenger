package com.nho.chat.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

public class ChatManager {
	/**
	 * blockedObjByUser is map userName - liveObjectIds : store liveObject hold
	 * by user
	 */
	private Map<String, List<String>> blockedObjByUser = new ConcurrentHashMap<>();
	/**
	 * liveObjInfos is map : liveObjectId - LiveObjectInfo
	 */
	private Map<String, BasicLiveObjectInfo> liveObjInfos = new ConcurrentHashMap<>();
	/**
	 * objectInChannel map liveObjectId - channelId
	 */
	private Map<String, String> objectInChannel = new ConcurrentHashMap<>();
	/**
	 * store time chat with boot of user
	 */
	private Map<String, Long> timeUserChatWithBot = new ConcurrentHashMap<>();
	/**
	 * waitTimeOfUser store time boot wait to interact again with user
	 */
	private Map<String, Long> waitTimeOfUser = new ConcurrentHashMap<>();
	/**
	 * store strokes of live object
	 */
	private Map<String, List<Stroke>> objIdToStrokes = new ConcurrentHashMap<>();
	/**
	 * messageUserChatToBots store : userName - list message chat of user to
	 * boot
	 */
	private Map<String, List<String>> messageUserChatToBots = new ConcurrentHashMap<>();
	/**
	 * channelPingTimes store : channelId - Map<userName,count> : ping times of
	 * user to each other in channel
	 */
	private Map<String, HashMap<String, Integer>> channelPingTimes = new ConcurrentHashMap<>();

	public int updatePingTimes(String channelId, String userName) {
		synchronized (channelPingTimes) {
			if (this.channelPingTimes.containsKey(channelId)) {
				Map<String, Integer> userPingTimes = this.channelPingTimes.get(channelId);
				if (userPingTimes.containsKey(userName)) {
					int pingTimes = userPingTimes.get(userName);
					userPingTimes.put(userName, pingTimes + 1);
				} else {
					userPingTimes.put(userName, 1);
				}
			} else {
				HashMap<String, Integer> userPingTimes = new HashMap<>();
				userPingTimes.put(userName, 1);
				this.channelPingTimes.put(channelId, userPingTimes);
			}
			return this.channelPingTimes.get(channelId).get(userName);
		}
	}

	public int getPingTimesOfUser(String channelId, String userName) {
		synchronized (channelPingTimes) {
			if (this.channelPingTimes.containsKey(channelId)) {
				if (this.channelPingTimes.get(channelId).containsKey(userName)) {
					return this.channelPingTimes.get(channelId).get(userName);
				}
			}
			return 0;
		}
	}

	public void deletePingTime(String channelId) {
		synchronized (channelPingTimes) {
			if (this.channelPingTimes.containsKey(channelId)) {
				this.channelPingTimes.remove(channelId);
			}
		}
	}
	
	public void deletePingTimes(){
		this.channelPingTimes = new ConcurrentHashMap<>();
	}

	public void addMessageOfUser(String userName, String messageChat) {
		if (!messageUserChatToBots.containsKey(userName)) {
			List<String> messageChats = new ArrayList<>();
			messageChats.add(messageChat);
			this.messageUserChatToBots.put(userName, messageChats);
		} else {
			List<String> messages = this.messageUserChatToBots.get(userName);
			messages.add(messageChat);
			this.messageUserChatToBots.put(userName, messages);
		}
	}

	public List<String> getMessageChatOfUser(String userName) {
		if (this.messageUserChatToBots.containsKey(userName)) {
			return this.messageUserChatToBots.get(userName);
		}
		return new ArrayList<>();
	}

	public void removeUserChatWithBot(String userName) {
		if (this.messageUserChatToBots.containsKey(userName)) {
			this.messageUserChatToBots.remove(userName);
		}
	}

	public void addStokeOfObjectId(Stroke stroke, String objectId) {
		List<Stroke> strokes = new ArrayList<>();
		if (this.objIdToStrokes.containsKey(objectId)) {
			strokes = this.objIdToStrokes.get(objectId);
			strokes.add(stroke);
		} else {
			strokes.add(stroke);
		}
		this.objIdToStrokes.put(objectId, strokes);
	}

	public List<Stroke> getStrokesOfLiveObject(String objectId) {
		if (this.objIdToStrokes.containsKey(objectId)) {
			return this.objIdToStrokes.get(objectId);
		}
		return null;
	}

	public void removeStrokesOfObjectId(String objectId) {
		if (this.objIdToStrokes.containsKey(objectId)) {
			this.objIdToStrokes.remove(objectId);
		}
	}

	public void addNewObjInChannel(String objId, String channelId) {
		this.objectInChannel.put(objId, channelId);
	}

	public void removeObjIdInChannel(String objId) {
		if (this.objectInChannel.containsKey(objId)) {
			this.objectInChannel.remove(objId);
		}
	}

	public List<String> getListObjectInChannel(String channelId) {
		List<String> objIds = new ArrayList<>();
		for (Entry<String, String> entry : this.objectInChannel.entrySet()) {
			if (entry.getValue().equals(channelId)) {
				objIds.add(entry.getKey());
			}
		}
		return objIds;
	}

	public void addNewBlockedObj(String objId, String userName) {
		if (this.blockedObjByUser.containsKey(objId)) {
			List<String> owners = this.blockedObjByUser.get(objId);
			if (!owners.contains(userName)) {
				owners.add(userName);
			}
			this.blockedObjByUser.put(objId, owners);
		} else {
			List<String> owners = new ArrayList<>();
			owners.add(userName);
			this.blockedObjByUser.put(objId, owners);
		}
	}

	public void removeBlockedObjByUserName(String objId, String userName) {
		if (this.blockedObjByUser.containsKey(objId)) {
			List<String> owners = this.blockedObjByUser.get(objId);
			if (owners.contains(userName)) {
				owners.remove(userName);
			}
			if (owners.size() > 0) {
				this.blockedObjByUser.put(objId, owners);
			} else {
				this.blockedObjByUser.remove(objId);
			}
		}
	}

	public void removeBlockedObj(String objId) {
		if (this.blockedObjByUser.containsKey(objId)) {
			this.blockedObjByUser.remove(objId);
		}
	}

	public void removeBlockedObjOfUser(String userName) {
		for (Entry<String, List<String>> entry : this.blockedObjByUser.entrySet()) {
			List<String> owners = entry.getValue();
			if (owners.contains(userName)) {
				owners.remove(userName);
				if (owners.size() > 0) {
					this.blockedObjByUser.put(entry.getKey(), owners);
				} else {
					this.blockedObjByUser.remove(entry.getKey());
				}
			}
		}
	}

	public boolean isObjectIsBlocked(String objId) {
		boolean isBlocked = false;
		if (this.blockedObjByUser.containsKey(objId)) {
			isBlocked = true;
		}

		return isBlocked;
	}

	public boolean isObjectBlockedByUser(String objId, String userName) {
		boolean isBlocked = false;
		if (this.blockedObjByUser.containsKey(objId)) {
			if (this.blockedObjByUser.get(objId).contains(userName)) {
				isBlocked = true;
			}
		}
		return isBlocked;
	}

	public void addNewObjWithListOfIds(String objId, BasicLiveObjectInfo ids) {
		this.liveObjInfos.put(objId, ids);
	}

	public void removeObjWithListOfIds(String objId) {
		if (this.liveObjInfos.containsKey(objId)) {
			this.liveObjInfos.remove(objId);
		}
	}

	public BasicLiveObjectInfo getIdsOfObject(String objId) {
		BasicLiveObjectInfo id = new BasicLiveObjectInfo();
		if (this.liveObjInfos.containsKey(objId)) {
			id = this.liveObjInfos.get(objId);
		}
		return id;
	}

	public BasicLiveObjectInfo getStrokeOfObj(String objId) {
		if (this.liveObjInfos.containsKey(objId)) {
			return this.liveObjInfos.get(objId);
		}
		return null;
	}

	public void updatePositionOfObj(String objId, float x, float y) {
		if (this.liveObjInfos.containsKey(objId)) {
			BasicLiveObjectInfo infos = this.liveObjInfos.get(objId);
			infos.updatePosition(x, y);
			this.liveObjInfos.put(objId, infos);
		}
	}

	public void addTimeChatOfUserWithBot(String userName, long timeChat) {
		this.timeUserChatWithBot.put(userName, timeChat);
	}

	public void removeTimeChatOfUserWithBot(String userName) {
		if (this.timeUserChatWithBot.containsKey(userName)) {
			this.timeUserChatWithBot.remove(userName);
		}
	}

	public long getTimeChatOfUser(String userName) {
		if (this.timeUserChatWithBot.containsKey(userName)) {
			return this.timeUserChatWithBot.get(userName);
		}
		return 0;
	}

	public void updateWaitTimeOfUser(String userName) {
		if (!this.waitTimeOfUser.containsKey(userName)) {
			this.waitTimeOfUser.put(userName, (long) 0);
		} else {
			long waitTime = this.waitTimeOfUser.get(userName) + 1000;
			this.waitTimeOfUser.put(userName, waitTime);
		}
	}

	public void resetWaitTimeOfUser(String userName) {
		this.waitTimeOfUser.put(userName, (long) 0);
	}

	public void removeWaitTimeOfUser(String userName) {
		if (this.waitTimeOfUser.containsKey(userName)) {
			this.waitTimeOfUser.remove(userName);
		}
	}

	public long getWaitTimeOfUser(String userName) {
		if (this.waitTimeOfUser.containsKey(userName)) {
			return this.waitTimeOfUser.get(userName);
		}
		return 0;
	}

}
