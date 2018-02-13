package com.sixsq.slipstream.util;

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

import org.junit.Test;

import com.sixsq.slipstream.exceptions.SlipStreamInternalException;

public class XslUtilsTest {

	@Test
	public void compileXsl() {

		String[] xslNames = { "module-export.xsl", "module-import.xsl" };

		for (String name : xslNames) {
			try {
				XslUtils.getTransformer(name);
			} catch (SlipStreamInternalException e) {
				System.err.println("Error processing stylesheet: " + name);
				System.err.println("Message: " + e.getMessage());
				Throwable cause = e.getCause();
				if (cause != null) {
					System.err.println("Cause: " + cause.getMessage());
				}
				throw e;
			}
		}
	}

}
