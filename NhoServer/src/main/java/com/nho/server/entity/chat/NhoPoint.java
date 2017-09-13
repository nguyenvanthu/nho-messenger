package com.nho.server.entity.chat;

public class NhoPoint {
	private float x;
	private float y;
	private int index;
	private int action;

	public NhoPoint(float x, float y, int index, int action) {
		this.x = x;
		this.y = y;
		this.index = index;
		this.action = action;
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public int getAction() {
		return action;
	}

	public void setAction(int action) {
		this.action = action;
	}
}
