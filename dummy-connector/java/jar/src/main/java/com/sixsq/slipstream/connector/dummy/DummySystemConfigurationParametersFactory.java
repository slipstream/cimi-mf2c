package com.sixsq.slipstream.connector.dummy;

/*
 * +=================================================================+
 * SlipStream Connector Dummy
 * =====
 * Copyright (C) 2014 SixSq Sarl (sixsq.com)
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

import com.sixsq.slipstream.connector.SystemConfigurationParametersFactoryBase;
import com.sixsq.slipstream.exceptions.ValidationException;


public class DummySystemConfigurationParametersFactory extends SystemConfigurationParametersFactoryBase {

	public static final String ORCHESTRATOR_DISK_SIZE_KEY = "orchestrator.disk";

	public DummySystemConfigurationParametersFactory(String connectorInstanceName) throws ValidationException {
		super(connectorInstanceName);
	}

	@Override
	protected void initReferenceParameters() throws ValidationException {
		initConnectorParameters(DummyConnector.CLOUD_SERVICE_NAME);
	}

}