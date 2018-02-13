package com.sixsq.slipstream.credentials;

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

import com.sixsq.slipstream.exceptions.InvalidElementException;
import com.sixsq.slipstream.exceptions.ValidationException;
import com.sixsq.slipstream.persistence.UserParameter;
import java.util.Map;


public interface Credentials {

	public String getKey() throws InvalidElementException;
	
	public String getSecret() throws InvalidElementException;
	
	public void validate() throws ValidationException;

	public Map<String, UserParameter> setUserParametersValues(String cloudCredsJSON)
			throws ValidationException;

	/**
	 * @param connInstanceName category of the provided parameter set.
	 */
	public Object getCloudCredCreateTmpl(Map<String, UserParameter> prams, String
			connInstanceName);

	public ICloudCredential getCloudCredential(Map<String, UserParameter> params, String connInstanceName);

	public void store() throws ValidationException;
}
