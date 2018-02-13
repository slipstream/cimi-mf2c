package com.sixsq.slipstream.exceptions;

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


import org.restlet.data.Status;

/**
 * HTTP client error wrapper.
 * @author meb
 *
 */
@SuppressWarnings("serial")
public class ClientHttpException extends SlipStreamException {

	private Status status;

	public Status getStatus() {
		return status;
	}

	public ClientHttpException(String error, Status status){
		super(error);
		this.status = status;
	}
}
