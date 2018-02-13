package com.sixsq.slipstream.run;

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
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;

import com.sixsq.slipstream.exceptions.AbortException;
import com.sixsq.slipstream.exceptions.ConfigurationException;
import com.sixsq.slipstream.exceptions.NotFoundException;
import com.sixsq.slipstream.exceptions.SlipStreamException;
import com.sixsq.slipstream.exceptions.ValidationException;
import com.sixsq.slipstream.persistence.PersistenceUtil;
import com.sixsq.slipstream.persistence.Run;
import com.sixsq.slipstream.persistence.RuntimeParameter;

public class RuntimeParameterResourceTest extends RuntimeParameterResourceTestBase {

	@BeforeClass
	public static void classSetup() throws ValidationException {
		RuntimeParameterResourceTestBase.classSetup();
	}

	@AfterClass
	public static void classTearDown() throws ValidationException {
		RuntimeParameterResourceTestBase.classTearDown();
	}

	@After
	public void tearDown() {
		super.tearDown();
	}

	@Test
	public void runtimeParameterResourceGetUnknownUuid() throws ConfigurationException, ValidationException {

		Request request = createGetRequest("unknownUuid", "aKey");

		Response response = executeRequest(request);

		assertEquals(Status.CLIENT_ERROR_NOT_FOUND, response.getStatus());
	}

	@Test
	public void runtimeParameterResourceGetUnknownKey() throws IOException, SlipStreamException {

		Run run = createAndStoreRunImage("RuntimeParameterResourceGetUnknownKey");

		Request request = createGetRequest(run.getUuid(), "unknownKey");

		Response response = executeRequest(request);

		assertEquals(Status.CLIENT_ERROR_NOT_FOUND, response.getStatus());

		run = Run.loadFromUuid(run.getUuid());
		assertAbortSet(run);

		run.remove();
	}

	@Test
	public void runtimeParameterResourceGetNotYetSetValue() throws IOException,
			SlipStreamException {

		Run run = createAndStoreRunImage("runtimeParameterResourceGetNotYetSetValue");

		Request request = createGetRequest(run.getUuid(), "ss:abort");

		Response response = executeRequest(request);

		assertEquals(Status.CLIENT_ERROR_PRECONDITION_FAILED, response.getStatus());

		assertAbortNotSet(run);

		run.remove();
	}

	@Test
	public void runtimeParameterResourceGet() throws IOException, SlipStreamException {

		Run run = createAndStoreRunImage("RuntimeParameterResourceGet");

		String key = "node.1:key";
		String value = "value of key";
		storeRuntimeParameter(key, value, run);

		executeGetRequestAndAssertValue(run, key, value);

		run.remove();
	}

	private void storeRuntimeParameter(String key, String value, Run run) throws ValidationException, NotFoundException {
		run.assignRuntimeParameter(key, value, "");
		run.store();
	}

	@Test
	public void runtimeParameterResourcePutExisting() throws IOException, SlipStreamException {

		Run run = createAndStoreRunImage("runtimeParameterResourcePutExisting");

		String key = "node.1:key";
		String value = "value of key";

		run.assignRuntimeParameter(key, value, "");
		run.store();

		Request request = createPutRequest(run.getUuid(), key, new StringRepresentation(value));

		executeRequest(request);

		RuntimeParameter runtimeParameter = RuntimeParameter.loadFromUuidAndKey(run.getUuid(), key);

		run.remove();

		assertNotNull(runtimeParameter);
		assertEquals("value of key", runtimeParameter.getValue());

		assertAbortNotSet(run);
	}

	private void assertAbortNotSet(Run run) {
		assertThat(run.getRuntimeParameters().get("ss:abort").isSet(), is(false));
	}

	private void assertAbortSet(Run run) {
		EntityManager em = PersistenceUtil.createEntityManager();
		EntityTransaction transaction = em.getTransaction();
		transaction.begin();
		run = em.merge(run);

		assertThat(run.getRuntimeParameters().get("ss:abort").isSet(), is(true));

		transaction.commit();
		em.close();
	}

	@Test
	public void runtimeParameterResourcePutNotExisting() throws IOException, SlipStreamException {

		Run run = createAndStoreRunImage("runtimeParameterResourcePutNotExisting");

		String key = "node.1:key";
		Form form = new Form();
		String value = "value of key";
		form.add("value", value);

		Representation entity = form.getWebRepresentation();
		Request request = createPutRequest(run.getUuid(), key, entity);

		Response response = executeRequest(request);

		run.remove();

		assertThat(response.getStatus(), is(Status.CLIENT_ERROR_NOT_FOUND));
	}

	@Test
	public void runtimeParameterResourcePutTooLong() throws IOException, SlipStreamException {

		Run run = createAndStoreRunImage("runtimeParameterResourcePutTooLong");

		String key = "node.1:key";
		String value = new String(new char[RuntimeParameter.VALUE_MAX_LENGTH+1]).replace('\0', 'x');

		run.assignRuntimeParameter(key, "", "");
		run.store();

		Request request = createPutRequest(run.getUuid(), key, new StringRepresentation(value));
		Response response = executeRequest(request);

		run.remove();

		assertThat(response.getStatus().getCode(), is(Status.CLIENT_ERROR_BAD_REQUEST.getCode()));
	}

	@Test
	public void runtimeParameterResourcePutAbortTooLong() throws IOException, SlipStreamException {

		Run run = createAndStoreRunImage("runtimeParameterResourcePutAbortTooLong");

		String key = "node.1:abort";
		String value = new String(new char[RuntimeParameter.VALUE_MAX_LENGTH+1]).replace('\0', 'x');

		run.assignRuntimeParameter(key, "", "");
		run.store();

		Request request = createPutRequest(run.getUuid(), key, new StringRepresentation(value));
		Response response = executeRequest(request);
		RuntimeParameter runtimeParameter = RuntimeParameter.loadFromUuidAndKey(run.getUuid(), key);

		run.remove();

		assertNotNull(runtimeParameter);
		assertThat(runtimeParameter.getValue().length(), lessThanOrEqualTo(RuntimeParameter.VALUE_MAX_LENGTH));
		assertThat(response.getStatus(), is(Status.SUCCESS_OK));
	}

	@Test
	public void runtimeParameterRetrieveFromContainerRun() throws IOException,
			SlipStreamException {

		Run run = createAndStoreRunImage("RuntimeParameterRetrieveFromContainerRun");

		String key = "node.1:key";
		String value = "value of key";
		storeRuntimeParameter(key, value, run);

		EntityManager em = PersistenceUtil.createEntityManager();

		run = Run.loadFromUuid(run.getUuid(), em);

		assertNotNull(run);
		assertEquals("value of key", run.getRuntimeParameterValue(key));

		em.close();

		run.remove();
	}

	@Test
	public void runtimeParameterReset() throws SlipStreamException, IOException {

		String key = "node.1:key";
		String value = "value of key";
		Run run = createAndStoreRunWithRuntimeParameter("runtimeParameterReset", key, value);

		assertNotNull(run);
		assertEquals(value, run.getRuntimeParameterValue(key));

		Request request = createDeleteRequest(run.getUuid(), key);
		Response response = executeRequest(request);

		assertEquals(Status.SUCCESS_NO_CONTENT, response.getStatus());

		assertRuntimeParameterWasReset(run, key);

		run.remove();
	}

	private void assertRuntimeParameterWasReset(Run run, String key) throws AbortException {
		RuntimeParameter rtp = RuntimeParameter.loadFromUuidAndKey(run.getUuid(), key);
		assertNotNull(rtp);
		assertThat(rtp.isSet(), is(false));
	}

	@Test
	public void wrongNodeTriggersAbort() throws SlipStreamException, IOException {

		String key = "wrong.1:key";
		Run run = createAndStoreRunImage("wrongNodeTriggersAbort");

		Request request = createGetRequest(run.getUuid(), key);
		Response response = executeRequest(request);

		assertEquals(Status.CLIENT_ERROR_NOT_FOUND, response.getStatus());

		run = Run.loadFromUuid(run.getUuid());
		assertAbortSet(run);

		run.remove();
	}

	@Test
	public void wrongKeyTriggersAbort() throws SlipStreamException, IOException {

		String key = "ss:wrong";
		Run run = createAndStoreRunImage("wrongKeyTriggersAbort");

		Request request = createGetRequest(run.getUuid(), key);
		Response response = executeRequest(request);

		assertEquals(Status.CLIENT_ERROR_NOT_FOUND, response.getStatus());

		run = Run.loadFromUuid(run.getUuid());
		assertAbortSet(run);

		run.remove();
	}

	@Test
	public void cantSetAbortTwice() throws SlipStreamException, IOException {

		String key = "ss:abort";
		Run run = createAndStoreRunImage("cantSetAbortTwice");

		Request request = createPutRequest(run.getUuid(), key, new StringRepresentation("first abort"));
		Response response = executeRequest(request);

		assertEquals(Status.SUCCESS_OK, response.getStatus());

		RuntimeParameter abort = RuntimeParameter.loadFromUuidAndKey(run.getUuid(), "ss:abort");
		assertThat(abort.getValue(), is("first abort"));

		request = createPutRequest(run.getUuid(), key, new StringRepresentation("second abort"));
		response = executeRequest(request);

		assertEquals(Status.CLIENT_ERROR_CONFLICT, response.getStatus());

		abort = RuntimeParameter.loadFromUuidAndKey(run.getUuid(), "ss:abort");
		assertThat(abort.getValue(), is("first abort"));

		run.remove();
	}

	@Test
	public void errorSetsNodeAndGlobalAbort() throws IOException, SlipStreamException {

		String machineAbortKey = Run.MACHINE_NAME_PREFIX.toLowerCase() + RuntimeParameter.ABORT_KEY;
		String globalAbortKey = RuntimeParameter.GLOBAL_ABORT_KEY;
		String abortMessage = "machine abort";

		Run run = createAndStoreRunImage("errorSetsNodeAndGlobalAbort");

		Request request = createPutRequest(run.getUuid(), machineAbortKey, new StringRepresentation(abortMessage));
		Response response = executeRequest(request);

		assertEquals(Status.SUCCESS_OK, response.getStatus());

		RuntimeParameter nodeAbort = RuntimeParameter.loadFromUuidAndKey(run.getUuid(), machineAbortKey);
		assertThat(nodeAbort.getValue(), is(abortMessage));

		RuntimeParameter globalAbort = RuntimeParameter.loadFromUuidAndKey(run.getUuid(), globalAbortKey);
		assertThat(globalAbort.getValue(), is(abortMessage));
	}

	@Test
	public void cancelAbort() throws IOException, SlipStreamException {

		String machineAbortKey = Run.MACHINE_NAME_PREFIX.toLowerCase() + RuntimeParameter.ABORT_KEY;
		String globalAbortKey = RuntimeParameter.GLOBAL_ABORT_KEY;
		String abortMessage = "machine abort";

		Run run = createAndStoreRunImage("errorSetsNodeAndGlobalAbort");

		Request request = createPutRequest(run.getUuid(), machineAbortKey, new StringRepresentation(abortMessage));
		Response response = executeRequest(request);

		assertEquals(Status.SUCCESS_OK, response.getStatus());

		RuntimeParameter nodeAbort = RuntimeParameter.loadFromUuidAndKey(run.getUuid(), machineAbortKey);
		assertThat(nodeAbort.getValue(), is(abortMessage));
		assertThat(nodeAbort.isSet(), is(true));

		RuntimeParameter globalAbort = RuntimeParameter.loadFromUuidAndKey(run.getUuid(), globalAbortKey);
		assertThat(globalAbort.getValue(), is(abortMessage));
		assertThat(globalAbort.isSet(), is(true));

		Map<String, Object> attributes = createRequestAttributes(run.getUuid(), "");
		attributes.put(RunListResource.IGNORE_ABORT_QUERY, "true");

		attributes.put("key", machineAbortKey);
		request = createDeleteRequest(attributes);
		response = executeRequest(request);
		assertEquals(Status.SUCCESS_NO_CONTENT, response.getStatus());

		attributes.put("key", globalAbortKey);
		request = createDeleteRequest(attributes);
		response = executeRequest(request);
		assertEquals(Status.SUCCESS_NO_CONTENT, response.getStatus());

		nodeAbort = RuntimeParameter.loadFromUuidAndKey(run.getUuid(), machineAbortKey);
		assertThat(nodeAbort.getValue(), is(""));
		assertThat(nodeAbort.isSet(), is(false));

		globalAbort = RuntimeParameter.loadFromUuidAndKey(run.getUuid(), globalAbortKey);
		assertThat(globalAbort.getValue(), is(""));
		assertThat(globalAbort.isSet(), is(false));
	}

	@Test
	public void runtimeParameterResourceGetStress() throws IOException, SlipStreamException {

		Run run = createAndStoreRunImage("runtimeParameterResourceGetStress");

		String key = "node.1:key";
		String value = "value of key";
		storeRuntimeParameter(key, value, run);

		Long before = System.currentTimeMillis();
		for (int i = 0; i < 1000; i++) {
			executeGetRequestAndAssertValue(run, key, value);
		}
		logTimeDiff("parseRequest", before);

		run.remove();
	}

	@Test
	public void isAbort() throws IOException, SlipStreamException {

		Run run = createAndStoreRunImage("isAbort");

		boolean isAbort = RuntimeParameter.isAbort(run.getUuid());
		assertThat(isAbort, is(false));

		RuntimeParameter abort = RuntimeParameter.loadFromUuidAndKey(run.getUuid(), "ss:abort");
		abort.setValue("abort!");
		abort.store();

		isAbort = RuntimeParameter.isAbort(run.getUuid());
		assertThat(isAbort, is(true));

		run.remove();
	}

	@Test
	public void getValueAndSet() throws IOException, SlipStreamException {

		Run run = createAndStoreRunImage("getValueAndSet");

		String key = "node.1:key";
		storeRuntimeParameter(key, "", run);

		RuntimeParameter rp = RuntimeParameter.loadFromUuidAndKey(run.getUuid(), key);

		Properties p = RuntimeParameter.getValueAndSet(run.getUuid(), key);
		String value = (String) p.get("value");
		boolean isSet = (Boolean) p.get("isSet");
		assertThat(value, is(""));
		assertFalse(isSet);

		rp = RuntimeParameter.loadFromUuidAndKey(run.getUuid(), key);
		value = "value of key";
		rp.setValue(value);
		rp.store();

		p = RuntimeParameter.getValueAndSet(run.getUuid(), key);
		value = (String) p.get("value");
		isSet = (Boolean) p.get("isSet");
		assertThat(value, is("value of key"));
		assertTrue(isSet);
		
		run.remove();
	}

	@Test
	public void runtimeParameterResourceGetSsGroups() throws IOException, SlipStreamException {

		Run run = createAndStoreRunImage("runtimeParameterResourceGetSsGroups");

		String key = "ss:groups";
		String value = "local:machine";

		executeGetRequestAndAssertValue(run, key, value);

		run.remove();
	}

	private void logTimeDiff(String msg, long before, long after) {
		Logger.getLogger("Timing").severe("took to execute " + msg + ": " + (after - before));
	}

	protected void logTimeDiff(String msg, long before) {
		logTimeDiff(msg, before, System.currentTimeMillis());
	}

}
