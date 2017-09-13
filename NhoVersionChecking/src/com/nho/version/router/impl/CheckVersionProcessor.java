package com.nho.version.router.impl;

import org.bson.Document;

import com.nhb.common.data.PuElement;
import com.nhb.common.data.PuObject;
import com.nhb.common.data.PuObjectRO;
import com.nho.version.annotation.CommandProcessor;
import com.nho.version.model.Announment;
import com.nho.version.model.VersionBean;
import com.nho.version.router.VersionAbstractProcessor;
import com.nho.version.statics.Version;

@CommandProcessor(command = { "checkVersion" })
public class CheckVersionProcessor extends VersionAbstractProcessor {
	@Override
	public PuElement execute(PuObjectRO request) {
		getLogger().debug("check version processor");
		PuObject data = (PuObject) request;
		double version = data.getDouble(Version.VERSION);
		getLogger().debug("version - " + version);
		return getAnnounment(version);
	}

	private VersionBean getNewestVersion() {
		Document document = this.getVersionModel().getVersionDocument().get(0);
		return VersionBean.fromDocument(document);
	}

	private PuObject getAnnounment(double version) {
		PuObject data = new PuObject();
		VersionBean newest = getNewestVersion();
		Announment announment = new Announment();
		if (version < newest.getVersion()) {
			announment.setLabel("Update");
			announment.setMessage(
					"Đã có phiên bản mới. Phiên bản này tất cả người dùng sẽ chuyển sang log in bằng Facebook, cho phép nhiều chức năng kết nối tiện lợi hơn.");
			announment.setLink(newest.getLink());
		}

		data.setDouble("version", newest.getVersion());
		data.setBoolean("force_update", newest.isForce_update());
		data.setPuObject("data", PuObject.fromObject(announment));
		data.setInteger("status", 0);
		getLogger().debug(data.toJSON());
		return data;
	}

}
