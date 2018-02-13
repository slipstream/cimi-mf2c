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

import java.io.IOException;

import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.Put;
import org.restlet.resource.ResourceException;

import com.sixsq.slipstream.exceptions.NotFoundException;
import com.sixsq.slipstream.exceptions.ValidationException;
import com.sixsq.slipstream.persistence.CloudImageIdentifier;
import com.sixsq.slipstream.persistence.ImageModule;
import com.sixsq.slipstream.persistence.Module;
import com.sixsq.slipstream.resource.BaseResource;
import com.sixsq.slipstream.user.FormProcessor;
import com.sixsq.slipstream.util.Logger;
import com.sixsq.slipstream.util.RequestUtil;

public class CloudResourceIdentifierResource extends BaseResource {

	CloudImageIdentifier cloudImage;
	private String moduleUri;
	private String version;
	private String cloudServiceName;
	private String region;

	@Override
	public void initialize() throws ResourceException {

		extractTargetUriFromRequest();
		fetchRepresentation();
	}

	@Override
	protected boolean isMachineAllowedToAccessThisResource() {
		// TODO: LS: Check if the Run of the cookie is associated to the image.
		return true;
	}

	protected void extractTargetUriFromRequest() {
		moduleUri = extractModuleUri();
		version = extractVersion();
		cloudServiceName = extractCloudResourceId();
		region = extractRegion();
	}

	private String extractCloudResourceId() {
		return (String) getRequest().getAttributes().get("cloudservice");
	}

	private String extractVersion() {
		return getRequest().getAttributes().get("version").toString();
	}

	private String extractRegion() {
		return (String) getRequest().getAttributes().get("region");
	}

	private String extractModuleUri() {
		return getRequest().getAttributes().get("module").toString();
	}

	private void fetchRepresentation() {
		loadCloudImage();
	}

	private void loadCloudImage() {
		String moduleResourceUri = Module.constructResourceUri(moduleUri + "/" + version);
		String uri = moduleResourceUri + "/" + cloudServiceName;
		if (FormProcessor.isSet(region)) {
			uri += CloudImageIdentifier.CLOUD_SERVICE_ID_SEPARATOR + region;
		}
		cloudImage = CloudImageIdentifier.load(uri);
		if (cloudImage == null) {
			ImageModule module = ImageModule.load(moduleResourceUri);
			if (module == null) {
				throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND,
						"Unknown module " + moduleResourceUri);
			}
			cloudImage = new CloudImageIdentifier(module, cloudServiceName,
					region, null);
			setExisting(false);
		}
	}

	@Get
	public String represent() throws ResourceException, NotFoundException,
			ValidationException {

		return cloudImage.getCloudMachineIdentifer();
	}

	@Put("text")
	public void update(Representation entity) {

		checkExisting();

		setCloudImageId(entity);

		setResponseCreated(cloudImage.getResourceUri());
	}

	private void checkExisting() {
		if (isExisting()) {
			throw (new ResourceException(Status.CLIENT_ERROR_FORBIDDEN,
					"An image for this cloud service is already registered"));
		}
	}

	private void setCloudImageId(Representation entity) {

		// Entity is null if content-length in PUT request either doesn't exist or of size 0 bytes.
		if (entity == null) {
			String msg = "Image ID was not provided.";
			Logger.debug("'" + msg + "' when setting new image ID on " + cloudImage.getResourceUri());
			throwClientError(msg);
		}

		String newImageId = "";
		try {
			newImageId = entity.getText();
		} catch (IOException e) {
			String msg = "Failed to get image ID from the request.";
			Logger.debug("'" + msg + "' when setting new image ID on " + cloudImage.getResourceUri()
			        + ". Exception: " + e.getMessage());
			throwClientError(msg);
		}
		cloudImage.setCloudMachineIdentifer(newImageId);

		cloudImage.store();
	}

	protected void throwClientError(String message) {
		throwClientError(Status.CLIENT_ERROR_BAD_REQUEST, message);
	}

	protected void throwClientForbiddenError(String message) {
		throwClientError(Status.CLIENT_ERROR_FORBIDDEN, message);
	}

	protected void throwClientError(Status status, String message) {
		throw new ResourceException(status, message);
	}

	protected void setResponseCreated(String resourceUri) {
		getResponse().setStatus(Status.SUCCESS_CREATED);

		String absolutePath = RequestUtil.constructAbsolutePath(getRequest(), "/" + resourceUri);
		getResponse().setLocationRef(absolutePath);
	}

	@Override
	protected String getPageRepresentation() {
		// TODO Stub
		return null;
	}

}
