package com.sixsq.slipstream.persistence;

/*
 * +=================================================================+
 * SlipStream Server (WAR)
 * =====
 * Copyright (C) 2013 SixSq Sarl (sixsq.com)
 * =====
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * -=================================================================-
 */

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import com.sixsq.slipstream.exceptions.ValidationException;

import org.simpleframework.xml.Root;

@Entity
@Root(name = "parameter")
@SuppressWarnings("serial")
public class RunParameter extends Parameter<Run> {

	public static final String NODE_INCREMENT_KEY = "node.increment";
	public static final String NODE_INCREMENT_DESCRIPTION = "Current increment value for node instances ids";
	public static final String NODE_RUN_BUILD_RECIPES_KEY = "run-build-recipes";
	public static final String NODE_RUN_BUILD_RECIPES_DESCRIPTION = "Define if the SlipStream executor should run build recipes.";

	@Id
	@GeneratedValue
	Long id;

	@SuppressWarnings("unused")
	private RunParameter() {
	}

	public RunParameter(String name, String value, String description)
			throws ValidationException {
		super(name, value, description);
	}

	@Override
	public Long getId() {
		return id;
	}

	@Override
	protected void setId(Long id) {
		this.id = id;
	}

	@Override
	public RunParameter copy() throws ValidationException {
		return (RunParameter) copyTo(new RunParameter(getName(), getValue(),
				getDescription()));
	}

}
