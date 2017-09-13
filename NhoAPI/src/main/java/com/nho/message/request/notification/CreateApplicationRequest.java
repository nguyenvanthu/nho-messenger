package com.nho.message.request.notification;

import com.nhb.common.data.PuArray;
import com.nho.message.MessageType;
import com.nho.message.NhoMessage;
import com.nho.message.request.Request;

public class CreateApplicationRequest extends NhoMessage implements Request {
	{
		this.setType(MessageType.CREATE_APPLICATION);
	}

	private String bundleId;
	private String key;
	private String filePath;
	private String password;
	private String name ;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getBundleId() {
		return bundleId;
	}

	public void setBundleId(String bundleId) {
		this.bundleId = bundleId;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	protected void writePuArray(PuArray puArray) {
		puArray.addFrom(this.bundleId);
		puArray.addFrom(this.key);
		puArray.addFrom(this.filePath);
		puArray.addFrom(this.password);
		puArray.addFrom(this.name);
	}

	@Override
	protected void readPuArray(PuArray puArray) {
		this.bundleId = puArray.remove(0).getString();
		this.key = puArray.remove(0).getString();
		this.filePath = puArray.remove(0).getString();
		this.password = puArray.remove(0).getString();
		this.name = puArray.remove(0).getString();
	}

}
