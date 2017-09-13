package com.nho.server.data.avatar;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.nhb.common.db.beans.AbstractMongoBean;
import com.nho.server.statics.DBF;
import com.nho.statics.F;

public class AvatarMongoBean extends AbstractMongoBean {
	private static final long serialVersionUID = 1L;
	private String name ;
	private String url ;
	private int type ;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
	public static AvatarMongoBean fromDocunment(Document document){
		AvatarMongoBean bean = new AvatarMongoBean();
		try{
			bean.setObjectId(document.getObjectId(DBF._ID));
			bean.setName(document.getString(F.NAME));
			bean.setUrl(document.getString(F.URL));
			bean.setType(document.getInteger(F.TYPE));
			return bean;
		}catch(Exception exception){
			return getDefault();
		}
		
	}

	@Override
	public Document toDocument() {
		Document document = new Document();
		document.put(DBF._ID, new ObjectId());
		document.put(F.NAME, this.name);
		document.put(F.URL, this.url);
		document.put(F.TYPE, this.type);
		
		return document;
	}
	
	private static AvatarMongoBean getDefault(){
		AvatarMongoBean bean = new AvatarMongoBean();
		bean.setName("icon_1");
		bean.setObjectId(new ObjectId());
		bean.setType(1);
		bean.setUrl("url_icon1");
		
		return bean;
	}

}
