package com.nho.vo;

import java.io.Serializable;
import java.util.UUID;

public class ChannelInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	private final String id;
	private String name;

	public ChannelInfo(String id) {
		this.id = id;
	}

	public ChannelInfo() {
		this(UUID.randomUUID().toString());
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

}
