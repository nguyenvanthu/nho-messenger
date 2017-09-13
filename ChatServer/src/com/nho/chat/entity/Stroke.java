package com.nho.chat.entity;

import com.nho.chat.data.StrokeMongoBean;

public class Stroke {
	private String uuid;
	private int index;
	private int action;
	private float x;
	private float y;

	public Stroke() {
	}

	public Stroke(String uuid, int index, int action, float x, float y) {
		this.uuid = uuid;
		this.index = index;
		this.action = action;
		this.x = x;
		this.y = y;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
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

	public StrokeMongoBean toStrokeMongoBean() {
		StrokeMongoBean bean = new StrokeMongoBean();
		bean.setUuid(this.uuid);
		bean.setAction(this.action);
		bean.setIndex(this.index);
		bean.setX(this.x);
		bean.setY(this.y);

		return bean;
	}
}
