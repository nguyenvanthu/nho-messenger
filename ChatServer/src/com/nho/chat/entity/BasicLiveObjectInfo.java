package com.nho.chat.entity;

public class BasicLiveObjectInfo {
	private int startId;
	private int endId;
	private Position postion;

	public BasicLiveObjectInfo() {
	}

	public BasicLiveObjectInfo(int startId, int endId, Position position) {
		this.startId = startId;
		this.endId = endId;
		this.postion = position;
	}

	public Position getPostion() {
		return postion;
	}

	public void setPostion(Position postion) {
		this.postion = postion;
	}

	public int getStartId() {
		return startId;
	}

	public void setStartId(int startId) {
		this.startId = startId;
	}

	public int getEndId() {
		return endId;
	}

	public void setEndId(int endId) {
		this.endId = endId;
	}

	public void updatePosition(float x, float y) {
		this.setPostion(new Position(x, y));
	}
}
