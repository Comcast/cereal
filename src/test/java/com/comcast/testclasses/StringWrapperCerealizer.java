package com.comcast.testclasses;

import com.comcast.cereal.CerealException;
import com.comcast.cereal.Cerealizer;
import com.comcast.cereal.ObjectCache;

public class StringWrapperCerealizer implements Cerealizer<StringWrapper, String> {

	@Override
	public StringWrapper deCerealize(String cereal, ObjectCache objectCache)
			throws CerealException {
		return new StringWrapper(cereal);
	}

	@Override
	public String cerealize(StringWrapper object, ObjectCache objectCache)
			throws CerealException {
		return object.getStr();
	}

}
