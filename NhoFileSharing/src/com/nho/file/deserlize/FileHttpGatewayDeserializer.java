package com.nho.file.deserlize;

import java.io.InputStream;
import java.io.StringWriter;
import java.util.Collection;
import java.util.Enumeration;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;

import org.apache.commons.io.IOUtils;

import com.mario.entity.message.MessageRW;
import com.mario.entity.message.transcoder.MessageDecodingException;
import com.mario.entity.message.transcoder.http.HttpMessageDeserializer;
import com.nhb.common.data.PuObject;

public class FileHttpGatewayDeserializer extends HttpMessageDeserializer {
	public FileHttpGatewayDeserializer() {
		super();
		getLogger().debug("init deserializer");
	}
	@Override
	protected void decodeHttpRequest(ServletRequest data, MessageRW message) throws MessageDecodingException {
		HttpServletRequest request = (HttpServletRequest) data;
		if (request != null) {
			PuObject params = new PuObject();
			Enumeration<String> it = request.getParameterNames();
			while (it.hasMoreElements()) {
				String key = it.nextElement();
				String value = request.getParameter(key);
				params.set(key, value);
			}
			if (request.getMethod().equalsIgnoreCase("post")) {
				if (request.getContentType().toLowerCase().contains("multipart/form-data")) {
					getLogger().debug("Posted data is in multipart format");
					try {
						Collection<Part> parts = request.getParts();
						for (Part part : parts) {
							if (part.getSize() > 0) {
								byte[] bytes = new byte[(int) part.getSize()];
								part.getInputStream().read(bytes, 0, bytes.length);
								params.setRaw(part.getName(), bytes);
							}
						}
					} catch (Exception e) {
						getLogger().error("Error while get data from request", e);
//						try {
//							throw new Exception("Error while get data from request: " + e.getMessage(), e);
//						} catch (Exception e1) {
//							e1.printStackTrace();
//						}
						return;
					}
				} else {
					getLogger().debug("Posted data in raw format, trying to parse as json");
					try (InputStream is = request.getInputStream(); StringWriter sw = new StringWriter()) {
						IOUtils.copy(is, sw);
						params.addAll(PuObject.fromJSON(sw.toString()));
					} catch (Exception e) {
//						try {
//							throw new Exception("Unable to parse data as json", e);
//						} catch (Exception e1) {
//							e1.printStackTrace();
//						}
						return;
					}
				}
			}
			message.setData(params);
			getLogger().debug("----> parsed request: " + params);
		} else {
			throw new NullPointerException("Cannot parse null request");
		}
	}

}
