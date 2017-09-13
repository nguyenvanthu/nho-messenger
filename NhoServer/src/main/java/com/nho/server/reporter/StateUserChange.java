package com.nho.server.reporter;

public interface StateUserChange {
	public void changeWhenUserOnline(String userName);
	public void changeWhenUserOffline(String userName);
}
