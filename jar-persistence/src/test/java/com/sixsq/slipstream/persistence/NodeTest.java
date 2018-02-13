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

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.sixsq.slipstream.exceptions.SlipStreamClientException;
import com.sixsq.slipstream.exceptions.ValidationException;

public class NodeTest {

	@Test
	public void validateMultiplicityValue() throws SlipStreamClientException {
		Node node = new Node();
		node.setMultiplicity(RuntimeParameter.MULTIPLICITY_NODE_START_INDEX);
		node.setMultiplicity(RuntimeParameter.MULTIPLICITY_NODE_START_INDEX+1);
		try {
			node.setMultiplicity(0);
		} catch(ValidationException ex) {
			fail("0 should be valid");
		}
		try {
			node.setMultiplicity(-1);
			fail("-1 is invalid");
		} catch(ValidationException ex) {
			// OK
		}
		try {
			node.setMultiplicity("-1");
			fail("\"-1\" is invalid");
		} catch(ValidationException ex) {
			// OK
		}
		try {
			node.setMultiplicity("Not a number");
			fail("must parse to an int");
		} catch(ValidationException ex) {
			// OK
		}
	}

	@Test
	public void nodeNotes() throws SlipStreamClientException {
		ImageModule image = new ImageModule("nodeNotes");
		image.setNote("image note");
		Node node = new Node("my_node", image);
		assertThat(node.getNotes().length, is(1));
		assertThat(node.getNotes()[0], is("image note"));
	}

	@Test
	public void nodeEmptyNotes() throws SlipStreamClientException {
		ImageModule image = new ImageModule("nodeNotes");
		Node node = new Node("my_node", image);
		assertThat(node.getNotes().length, is(0));
	}
}
