package com.nho.version;

import com.mario.entity.impl.BaseCommandRoutingHandler;
import com.mario.entity.message.Message;
import com.nhb.common.data.MapTuple;
import com.nhb.common.data.PuDataType;
import com.nhb.common.data.PuElement;
import com.nhb.common.data.PuObject;
import com.nhb.common.data.PuObjectRO;
import com.nhb.common.db.models.ModelFactory;
import com.nho.version.annotation.AnnotationLoader;
import com.nho.version.router.VersionCommandRouter;
import com.nho.version.statics.Version;

public class VersionCheckingHandler extends BaseCommandRoutingHandler {
	private ModelFactory modelFactory;
	private VersionCommandRouter router;

	@Override
	public void init(PuObjectRO initParams) {
		getLogger().debug("starting nho version checking ....");
		super.init(initParams);
		ModelFactory modelFactory = new ModelFactory();
		modelFactory.setClassLoader(this.getClass().getClassLoader());
		modelFactory.setMongoClient(getApi().getMongoClient(initParams.getString(Version.MONGODB)));
		this.modelFactory = modelFactory;
		this.router = new VersionCommandRouter(this);
		try {
			router.init(AnnotationLoader.load("com.nho.version.router"));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public PuElement handle(Message message) {
		PuObject data = (PuObject) message.getData();
		getLogger().debug(data.toJSON());
		data.setType("command", PuDataType.STRING);
		if (data.variableExists("command")) {
			String command = data.getString("command");
			getLogger().debug("name of command " + command);
			try {
				return this.router.process(command, data);
			} catch (Exception e) {
				getLogger().debug("process command exception", e);
			}
		}
		return PuObject.fromObject(new MapTuple<>("status", 1));
	}

	public ModelFactory getModelFactory() {
		return modelFactory;
	}
}
