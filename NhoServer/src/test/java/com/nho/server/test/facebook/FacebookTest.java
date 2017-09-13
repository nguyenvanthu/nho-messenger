package com.nho.server.test.facebook;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.nhb.common.BaseLoggable;
import com.nhb.common.data.PuArray;
import com.nhb.common.data.PuObject;

public class FacebookTest extends BaseLoggable{
	private static final String FACEBOOK_PERMISSIONS_URL = "https://graph.facebook.com/me?fields=permissions&access_token=";
	private static final String FACEBOOK_FRIENDS_URL ="https://graph.facebook.com/v2.7/me/friends?access_token=";
	private static final String FACEBOOK_AVATAR_URL = "https://graph.facebook.com/me?fields=picture&access_token=";
	public static void main(String[] args) throws Exception {
		String facebookToken = "EAAQSBTkX0HQBAIXPbdWJPWcV6n3xBKorZA01NEJTe7dHiuuh4bvYzkutM92Tnyp4cFSILrZCjVYQgvgfJHDds9w2Vcw4aqJ74GZCdvE793aRZAa2xrrPwKqKDlCs7R8ZB3iAwOFAN0CN0nhVHlOUZBmjVBvzsDoF74CpeV69PcMeRePHBf9NtMStNCyIRzAZCXiuAjaO4RAsR60xeYSJDw2";
		FacebookTest test = new FacebookTest();
		String urlAvt = test.getUrlAvt(facebookToken);
		System.out.println(urlAvt);
	}
	
	@SuppressWarnings("unused")
	private List<String> friendsInApp(String facebookToken) throws Exception{
		List<String> friends = new ArrayList<>();
		URL url = new URL(FACEBOOK_FRIENDS_URL + facebookToken+"&fields=installed");
		URLConnection connection = url.openConnection();
		InputStream stream;
		stream = connection.getInputStream();
		String endCoding = connection.getContentEncoding();
		endCoding = endCoding == null ? "UTF-8" : endCoding;
		PuObject puObject = PuObject.fromJSON(IOUtils.toString(stream, endCoding));
		if(puObject.variableExists("error")){
			return friends;
		}
		PuArray array = puObject.getPuArray("data");
		for(int i=0;i<array.size();i++){
			PuObject data = array.getPuObject(i);
			System.out.println("friend: "+data.getString("id"));
			friends.add(data.getString("id"));
		}
		return friends;
	}
	
	private String getUrlAvt(String facebookToken) throws Exception{
		String urlAvt = "";
		URL url = new URL(FACEBOOK_AVATAR_URL + facebookToken);
		URLConnection connection = url.openConnection();
		InputStream stream = connection.getInputStream();
		String endCoding = connection.getContentEncoding();
		endCoding = endCoding == null ? "UTF-8" : endCoding;
		String content = IOUtils.toString(stream, endCoding);
		System.out.println(content);
		JSONObject json = (JSONObject) new JSONParser().parse(content);
		JSONObject picture = (JSONObject) json.get("picture");
		JSONObject data = (JSONObject) picture.get("data");
		urlAvt = (String) data.get("url");
		return urlAvt;
	}

	@SuppressWarnings("unused")
	private List<String> getUserPermissions(String facebookToken) throws Exception {
		List<String> permissions = new ArrayList<>();
		URL url = new URL(FACEBOOK_PERMISSIONS_URL + facebookToken);
		URLConnection connection = url.openConnection();
		InputStream stream;
		stream = connection.getInputStream();
		String endCoding = connection.getContentEncoding();
		endCoding = endCoding == null ? "UTF-8" : endCoding;
		PuObject puObject = PuObject.fromJSON(IOUtils.toString(stream, endCoding));
		if (puObject == null) {
			return permissions;
		}
		String facebookId = puObject.getString("id");
		permissions.add(facebookId);
		System.out.println("facebookId "+facebookId);
		PuObject pe = puObject.getPuObject("permissions");
		PuArray array = pe.getPuArray("data");
		for(int i =0;i< array.size();i++){
			PuObject data = array.getPuObject(i);
			String permission = data.getString("permission");
			permissions.add(permission);
			System.out.println(permission);
		}

		return permissions;
	}
	public class AvtFacebook {
		private String id;
		private String picture;

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getPicture() {
			return picture;
		}

		public void setPicture(String picture) {
			this.picture = picture;
		}
	}
	
	public class Data {
		private boolean is_silhouette;
		private String url;

		public boolean isIs_silhouette() {
			return is_silhouette;
		}

		public void setIs_silhouette(boolean is_silhouette) {
			this.is_silhouette = is_silhouette;
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}
	}
	public class Picture {
		private String data;

		public String getData() {
			return data;
		}

		public void setData(String data) {
			this.data = data;
		}
	}

}
