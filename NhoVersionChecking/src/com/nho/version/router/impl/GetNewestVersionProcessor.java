package com.nho.version.router.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.commons.io.IOUtils;

import com.nhb.common.data.MapTuple;
import com.nhb.common.data.PuArray;
import com.nhb.common.data.PuArrayList;
import com.nhb.common.data.PuElement;
import com.nhb.common.data.PuObject;
import com.nhb.common.data.PuObjectRO;
import com.nho.version.annotation.CommandProcessor;
import com.nho.version.router.VersionAbstractProcessor;

@CommandProcessor(command = { "download" })
public class GetNewestVersionProcessor extends VersionAbstractProcessor {
	private static final String LINK_APK = "/home/thunv/apk/Nho.apk";

	@Override
	public PuElement execute(PuObjectRO request) {
		PuObject response = new PuObject();
//		Path path = Paths.get(LINK_APK);
		byte[] data = null;
		try {
//			data = Files.readAllBytes(path);
			data = IOUtils.toByteArray(new FileInputStream(new File(LINK_APK)));
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (data != null) {
			response.setInteger("status", 0);
			PuArray dataArray = new PuArrayList();
			for (byte b : data) {
				dataArray.addFrom(b);
			}
			response.setPuArray("data", dataArray);
			return response;
		}
		return PuObject.fromObject(new MapTuple<>("status", 1));

	}

}
