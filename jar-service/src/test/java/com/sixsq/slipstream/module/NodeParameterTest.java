package com.sixsq.slipstream.module;

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

import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.restlet.data.Form;

import com.sixsq.slipstream.exceptions.SlipStreamClientException;
import com.sixsq.slipstream.exceptions.ValidationException;
import com.sixsq.slipstream.persistence.ModuleCategory;
import com.sixsq.slipstream.persistence.NodeParameter;
import com.sixsq.slipstream.persistence.Parameter;
import com.sixsq.slipstream.persistence.Run;
import com.sixsq.slipstream.persistence.RuntimeParameter;
import com.sixsq.slipstream.run.RunListResource;

public class NodeParameterTest {

	@Test
	public void validateValue() throws SlipStreamClientException {
		NodeParameter.validate(new NodeParameter(RuntimeParameter.GLOBAL_STATE_KEY, "'value_with_quote'"));
		NodeParameter.validate(new NodeParameter(RuntimeParameter.GLOBAL_STATE_KEY, "\"value_with_quote\""));
		try {
			NodeParameter.validate(new NodeParameter(RuntimeParameter.GLOBAL_STATE_KEY, "value_no_quote"));
			fail();
		} catch(ValidationException ex) {
			// OK
		}

		NodeParameter.validate(new NodeParameter(RuntimeParameter.GLOBAL_STATE_KEY, Run.MACHINE_NAME_PREFIX + "a_value"));
	}

	@Test
	public void userChoicesParsingForDeployment() throws ValidationException {
		Form form = new Form();
		form.add("parameter--node--n1--p1", "'value1'");
		form.add("parameter--node--n1--p2", "'value2'");
		form.add("parameter--node--n2--pa", "'valuea'");
		form.add("parameter--node--n2--pb", "'valueb'");
		Map<String, List<Parameter<?>>> choices = RunListResource.getUserChoicesFromForm(ModuleCategory.Deployment,
				form);
		assertThat(choices.size(), is(2));
		assertThat(choices.get("n1").size(), is(2));
		assertThat(choices.get("n1").get(0).getName(), is("p1"));
		assertThat(choices.get("n1").get(0).getValue(), is("'value1'"));
	}

	@Test
	public void userChoicesParsingForImage() throws ValidationException {
		Form form = new Form();
		form.add("parameter--p1", "'value1'");
		form.add("parameter--p2", "'value2'");
		form.add("parameter--pa", "'valuea'");
		Map<String, List<Parameter<?>>> choices = RunListResource.getUserChoicesFromForm(ModuleCategory.Image, form);
		assertThat(choices.size(), is(1));
		assertThat(choices.get(Run.MACHINE_NAME).size(), is(3));
		assertThat(choices.get(Run.MACHINE_NAME).get(0).getName(), is("p1"));
		assertThat(choices.get(Run.MACHINE_NAME).get(0).getValue(), is("'value1'"));
	}

}
