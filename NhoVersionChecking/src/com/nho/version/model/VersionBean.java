package com.nho.version.model;

import org.bson.Document;

import com.nhb.common.db.beans.AbstractBean;
import com.nho.version.statics.Version;

public class VersionBean extends AbstractBean {
	private static final long serialVersionUID = 1L;

	private double version;
	private String link ;
	private boolean force_update = false;
	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public double getVersion() {
		return version;
	}

	public void setVersion(double version) {
		this.version = version;
	}

	public Document toDocument() {
		Document document = new Document();
		document.append(Version.VERSION, this.version);
		document.append(Version.LINK, this.link);
		document.append(Version.FORCE_UPDATE, this.force_update);
		
		return document;
	}

	public static VersionBean fromDocument(Document document) {
		VersionBean bean = new VersionBean();
		bean.setVersion(document.getDouble(Version.VERSION));
		bean.setLink(document.getString(Version.LINK));
		if(document.containsKey(Version.FORCE_UPDATE)){
			bean.setForce_update(document.getBoolean(Version.FORCE_UPDATE));
		}else {
			bean.setForce_update(false);
		}
		return bean;
	}

	public boolean isForce_update() {
		return force_update;
	}

	public void setForce_update(boolean force_update) {
		this.force_update = force_update;
	}
}
