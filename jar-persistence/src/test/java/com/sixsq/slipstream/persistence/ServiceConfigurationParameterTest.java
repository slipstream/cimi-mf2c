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

import com.sixsq.slipstream.exceptions.SlipStreamClientException;
import com.sixsq.slipstream.exceptions.ValidationException;
import com.sixsq.slipstream.util.SerializationUtil;
import org.junit.Test;
import org.w3c.dom.Document;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

public class ServiceConfigurationParameterTest {

	@Test(expected = ValidationException.class)
	public void nullKeyNotAllowed() throws SlipStreamClientException {

		new ServiceConfigurationParameter(null, "ok", "ok");
	}

	@Test(expected = ValidationException.class)
	public void emptyKeyNotAllowed() throws SlipStreamClientException {

		new ServiceConfigurationParameter("", "ok", "ok");
	}

	@Test(expected = ValidationException.class)
	public void keyCantStartWithDot() throws SlipStreamClientException {

		new ServiceConfigurationParameter(".123", "ok", "ok");
	}

	@Test(expected = ValidationException.class)
	public void keyCantStartWithDigit() throws SlipStreamClientException {
		new ServiceConfigurationParameter("1abc", "ok", "ok");
	}

	@Test(expected = ValidationException.class)
	public void keyCantStartWithSpace() throws SlipStreamClientException {
		new ServiceConfigurationParameter(" abc", "ok", "ok");
	}

	@Test
	public void serializationWorks() throws ValidationException {
		ServiceConfigurationParameter parameter = new ServiceConfigurationParameter(
				"dummy", "ok", "ok");

		String serialization = SerializationUtil.toXmlString(parameter);

		assertNotNull(serialization);
		assertFalse("".equals(serialization));

		Document document = SerializationUtil.toXmlDocument(parameter);

		assertNotNull(document);
	}

}
