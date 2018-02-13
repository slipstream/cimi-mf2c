package com.sixsq.slipstream.persistence;

import com.sixsq.slipstream.exceptions.ValidationException;

@SuppressWarnings("serial")
public class Empty extends Metadata {

	@Override
	public String getResourceUri() {
		return "";
	}

	@Override
	public String getName() {
		return "";
	}

	@Override
	public void setName(String name) throws ValidationException {
	}

}
