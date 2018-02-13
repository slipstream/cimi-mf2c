package com.sixsq.slipstream.resource;

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

import com.sixsq.slipstream.cookie.CookieUtils;
import com.sixsq.slipstream.exceptions.ConfigurationException;
import com.sixsq.slipstream.exceptions.Util;
import com.sixsq.slipstream.exceptions.ValidationException;
import com.sixsq.slipstream.persistence.ServiceConfiguration;
import com.sixsq.slipstream.persistence.User;
import com.sixsq.slipstream.util.ConfigurationUtil;
import com.sixsq.slipstream.util.RequestUtil;
import org.restlet.Request;
import org.restlet.data.*;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import java.io.IOException;
import java.util.logging.Logger;

public abstract class BaseResource extends ServerResource {

	public static final String MODULE_RESOURCE_URI_KEY = "moduleResourceUri";
	public static final String CIMI_FILTER_KEY = "filter";
	public static final String PAGING_OFFSET_KEY = "offset";
	public static final String PAGING_LIMIT_KEY = "limit";
	public static final String PAGING_CLOUD_KEY = "cloud";
	public static final String RUN_UUID_KEY = "runUuid";
	public static final String RUN_OWNER_KEY = "runOwner";
	public static final String CHOOSER_KEY = "chooser";
	public static final String USER_KEY = "user";
	public static final String EDIT_KEY = "edit";
	public static final String NEW_KEY = "new";
	public static final String ACTIVE_ONLY_KEY = "activeOnly";

	public static final int LIMIT_DEFAULT = 20;
	public static final int LIMIT_MAX = 500;

	private User user = null;
	private ServiceConfiguration configuration = null;
	protected static final String NEW_NAME = "new";
	private boolean isEdit = false;

	@Override
	protected void doInit() throws ResourceException {
		Request request = getRequest();

		try {
			setUser(RequestUtil.getUserFromRequest(request));
		} catch (ConfigurationException e) {
			throwConfigurationException(e);
		} catch (ValidationException e) {
			throwClientValidationError(e.getMessage());
		}
		configuration = ConfigurationUtil.getServiceConfigurationFromRequest(request);

		initialize();

		authorizeMachine();

		authorize();
	}

	private void authorizeMachine() {
		Cookie cookie = CookieUtils.extractAuthnCookie(getRequest());

		if (isMachine(cookie) && !isMachineAllowedToAccessThisResource()) {
			throwClientForbiddenError();
		}
	}

	protected void authorize() {}

	protected void initialize() {}

	protected boolean isMachine(){
		Request request = getRequest();
		Cookie cookie = CookieUtils.extractAuthnCookie(request);

		return isMachine(cookie);
	}

	protected boolean isMachine(Cookie cookie){
		return cookie != null && CookieUtils.isMachine(cookie);
	}

	protected boolean isMachineAllowedToAccessThisResource(){
		return false;
	}

	protected abstract String getPageRepresentation();

	protected void setUser(User user) {
		this.user = user;
	}

	protected User getUser() {
		return user;
	}

	public ServiceConfiguration getConfiguration() {
		return configuration;
	}

	protected void throwUnauthorized() {
		Util.throwUnauthorized();
	}

	protected void throwClientError(Throwable e) {
		Util.throwClientError(e);
	}

	protected void throwClientError(String message) {
		Util.throwClientError(message);
	}

	protected void throwClientConflicError(String message) {
		Util.throwClientConflicError(message);
	}

	protected void throwClientConflicError(String message, Throwable e) {
		Util.throwClientConflicError(message, e);
	}

	protected void throwClientForbiddenError() {
		Util.throwClientForbiddenError();
	}

	protected void throwClientForbiddenError(String message) {
		Util.throwClientForbiddenError(message);
	}

	protected void throwClientForbiddenError(Throwable e) {
		Util.throwClientForbiddenError(e);
	}

	protected static void throwClientBadRequest(String message) {
		Util.throwClientBadRequest(message);
	}

	protected void throwNotFoundResource() {
		Util.throwNotFoundResource();
	}

	protected void throwClientValidationError(String message) {
		Util.throwClientValidationError(message);
	}

	protected void throwClientConflicError(Throwable e) {
		Util.throwClientConflicError(e);
	}

	protected void throwClientError(Status status, String message) {
		Util.throwClientError(status, message);
	}

	protected void throwClientError(Status status, Throwable e) {
		Util.throwClientError(status, e);
	}

	protected void throwConfigurationException(ConfigurationException e) {
		Util.throwConfigurationException(e);
	}

	protected void throwServerError(Throwable e) {
		Util.throwServerError(e);
	}

	protected static void throwServerError(String message) {
		Util.throwServerError(message);
	}

	protected void throwServerError(String message, Throwable e) {
		Util.throwServerError(message, e);
	}

	protected void setIsEdit() throws ConfigurationException,
			ValidationException {
		isEdit = isEdit || isEditFlagTrue();
	}

	protected void setIsEdit(boolean isEdit) throws ConfigurationException,
			ValidationException {
		this.isEdit = isEdit;
	}

	protected boolean isEdit() {
		return isEdit;
	}

	protected boolean isEditFlagTrue() {
		return isSetInQuery("edit");
	}

	private boolean isQueryValueSetTrue(String flag) {
		String value = getQueryValue(flag);
		return isTrue(value);
	}

	protected boolean isTrue(String value) {
		if(value == null) {
			return false;
		}
		String trimmed = value.trim().toLowerCase();
		return ("true".equals(trimmed) || "yes".equals(trimmed) || "on".equals(trimmed) || "1".equals(trimmed));
	}

	private boolean isSetInQuery(String key) {
		Reference resourceRef = getRequest().getResourceRef();
		Form form = resourceRef.getQueryAsForm();
		return isTrue(form.getFirstValue(key));
	}

	protected boolean extractNewFlagFromQuery() {
		return isQueryValueSetTrue("new");
	}

	protected Form extractFormFromEntity(Representation entity)
			throws ResourceException {

				if (entity == null) {
					throwClientBadRequest("No data provided (Entity is empty)");
				}
				Form form = null;
				try {
					form = new Form(entity.getText());
				} catch (IOException e) {
					String msg = "Failed retreiving text from entity. "
							+ e.getMessage();
					throwClientError(msg);
				}
				return form;
			}

	protected void checkIsSuper() {
		if(!isSuper()) {
			throwClientForbiddenError("Only privileged users can perform this action");
		}
	}

	protected boolean isSuper() {
		return getUser().isSuper();
	}

	protected void setEmptyEntity(MediaType mt) {
		getResponse().setEntity(null, mt);
	}

	private void logTimeDiff(String msg, long before, long after) {
		Logger.getLogger("Timing").finest("took to execute " + msg + ": " + (after - before));
	}

	protected void logTimeDiff(String msg, long before) {
		logTimeDiff(msg, before, System.currentTimeMillis());
	}

	protected int getOffset() {
		return RequestUtil.getOffset(getRequest());
	}

	protected int getLimit() {
		return RequestUtil.getLimit(getRequest());
	}

	protected int getLimit(int defaultLimit, int max) {
		return RequestUtil.getLimit(getRequest(), defaultLimit, max);
	}

	protected String getCloud() {
		return getQueryValue(PAGING_CLOUD_KEY);
	}

	protected String getModuleResourceUri() {
		return getQueryValue(MODULE_RESOURCE_URI_KEY);
	}

	protected String getRunUuid() {
		return getQueryValue(RUN_UUID_KEY);
	}

	protected String getRunOwner() {
		return getQueryValue(RUN_OWNER_KEY);
	}

	protected String getUserFilter() {
		String user = getQueryValue(USER_KEY);
		if (user != null && !getUser().isSuper()) {
			throwClientForbiddenError("You don't have the permission to use the query parameter '" + USER_KEY + "'");
		}
		return user;
	}

	protected boolean getActiveOnly() {
		return isTrue(RequestUtil.getActiveOnly(getRequest()));
	}
}
