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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.sixsq.slipstream.exceptions.ValidationException;

public class PackageTest {
	@Test
	public void equals() throws ValidationException {
		assertTrue(new Package("name").equals(new Package("name")));
		assertFalse(new Package("name").equals(new Package("other_name")));

		Package p = new Package("name");
		p.setKey("key");

		assertFalse(p.equals(null));

		Package p2 = new Package("name");
		p.setKey("key2");

		assertFalse(p.equals(p2));

		p.setKey("repo");
		p2.setKey("repo");
		p.setRepository("repo");
		p2.setRepository("repo");

		assertTrue(p.equals(p2));

	}
}
