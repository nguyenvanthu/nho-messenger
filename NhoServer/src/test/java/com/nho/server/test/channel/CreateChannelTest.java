package com.nho.server.test.channel;

import java.util.ArrayList;
import java.util.List;

public class CreateChannelTest {
	public static void main(String[] args) {
		String userNames = "&112720415855913&105748046554827";
		List<String> userInChannels = new ArrayList<>();String[] array = userNames.split("&");
		for (int i = 1; i < array.length; i++) {
			userInChannels.add(array[i]);
		}
		System.out.println("number users is "+userInChannels.size());
	}
}
