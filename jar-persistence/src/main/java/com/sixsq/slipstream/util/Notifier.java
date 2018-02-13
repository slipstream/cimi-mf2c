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

import com.sixsq.slipstream.exceptions.SlipStreamInternalException;
import com.sixsq.slipstream.exceptions.SlipStreamRuntimeException;
import com.sixsq.slipstream.persistence.ServiceConfiguration;
import com.sixsq.slipstream.persistence.ServiceConfiguration.RequiredParameters;
import com.sixsq.slipstream.persistence.ServiceConfigurationParameter;

import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Logger;

public class Notifier {

	private static Logger logger = Logger.getLogger("org.restlet");

	private Notifier() {

	}

	public static boolean sendHTMLNotification(String email, String message)
			throws SlipStreamRuntimeException {

		ServiceConfiguration cfg = ServiceConfiguration.load();
		try {
			InternetAddress address = new InternetAddress(email);
			return sendNotification(cfg, address, message, true);
		} catch (AddressException e) {
			throw new SlipStreamRuntimeException(e.getMessage());
		}
	}

	public static boolean sendNotification(String email, String message)
			throws SlipStreamRuntimeException {

		ServiceConfiguration cfg = ServiceConfiguration.load();
		return sendNotification(cfg, email, message);
	}

	public static boolean sendNotification(ServiceConfiguration cfg,
			String email, String message) throws SlipStreamRuntimeException {

		try {
			InternetAddress address = new InternetAddress(email);
			return sendNotification(cfg, address, message);
		} catch (AddressException e) {
			throw new SlipStreamRuntimeException(e.getMessage());
		}
	}


	public static boolean sendNotification(ServiceConfiguration cfg,
										   InternetAddress email, String message) {
		return sendNotification(cfg, email, message, false);
	}

	private static void logEmailDetails(InternetAddress email, boolean isHTML, String message) {
		String notificationType = isHTML ? "HTML " : "";
		logger.info(String.format("sending %snotification to %s", notificationType, email));

		if(!isHTML) {
			logger.info("Message:");
			logger.info(message);
		}
	}

	private static boolean sendNotification(ServiceConfiguration cfg,
			InternetAddress email, String message, boolean isHTML) {

		boolean sendOk = true;

		try {

			InternetAddress admin = getRegistrationEmail(cfg);
			Session session = createSmtpSession(cfg);
			String password = getMailPassword(cfg);

			logEmailDetails(email, isHTML, message);

			Message msg = new MimeMessage(session);
			msg.setFrom(admin);

			Address[] recipients = new Address[] { email };

			msg.setRecipients(Message.RecipientType.TO, recipients);

			msg.setSubject("SlipStream Message");

			if(isHTML) {
				msg.setContent(message, "text/html; charset=utf-8");
			} else {
				msg.setText(message);
			}

			msg.setHeader("X-Mailer", "javamail");
			msg.setSentDate(new Date());

			msg.saveChanges();



			// send the thing off
			try {
				Transport t = session.getTransport();
				t.connect(null, password);
				t.sendMessage(msg, msg.getAllRecipients());
				logger.info("mail was successfully sent");
			} catch (AuthenticationFailedException afe) {
                String m = String.format("authentication failure%n%s%n", afe.getMessage());
				logger.severe(m);
				sendOk = false;
			} catch (MessagingException me) {
                String m = String.format("error sending message to %s%n%s%n", email, me.getMessage());
				logger.severe(m);
				sendOk = false;
			}

        } catch (RuntimeException re) {
            String m = String.format("error sending message to %s%n%s%n", email, re.getMessage());
            logger.severe(m);
            sendOk = false;
        } catch (Exception e) {
            String m = String.format("error sending message to %s%n%s%n", email, e.getMessage());
            logger.severe(m);
            sendOk = false;
		}
		return sendOk;
	}

	private static InternetAddress getRegistrationEmail(ServiceConfiguration cfg) {

		try {

			String key = RequiredParameters.SLIPSTREAM_REGISTRATION_EMAIL.getName();
			ServiceConfigurationParameter parameter = cfg.getParameter(key);
			String adminEmail = parameter.getValue();
			return new InternetAddress(adminEmail);

		} catch (NullPointerException e) {
			throw new SlipStreamInternalException(
					"administrator email undefined or invalid", e);
		} catch (AddressException e) {
			throw new SlipStreamInternalException(
					"administrator email undefined or invalid", e);
		}

	}

	private static String getMailPassword(ServiceConfiguration cfg) {

		String key = RequiredParameters.SLIPSTREAM_MAIL_PASSWORD.getName();
		ServiceConfigurationParameter parameter = cfg.getParameter(key);
		if (parameter != null) {
			return parameter.getValue();
		} else {
			return "";
		}

	}

	private static Session createSmtpSession(ServiceConfiguration cfg) {

		try {

			// Retrieve a copy of the system properties as a baseline for
			// setting the mail properties.
			Properties props = System.getProperties();

			// Force authentication for the SMTP server to be used.
			props.put("mail.smtp.auth", "true");

			// Turn on the use of TLS if it is available.
			props.put("mail.smtp.starttls.enable", "true");

			// Determine whether or not to use SSL. By default, SSL is not used
			// when contacting the SMTP server.
			String key = RequiredParameters.SLIPSTREAM_MAIL_SSL.getName();
			ServiceConfigurationParameter parameter = cfg.getParameter(key);
			boolean useSSL = false;
			if ("true".equals(parameter.getValue())) {
				useSSL = true;
			}

			String protocol = useSSL ? "smtps" : "smtp";

			props.put("mail.transport.protocol", protocol);

			// Set the SMTP server parameters. The host name is required; the
			// port is optional.
			key = RequiredParameters.SLIPSTREAM_MAIL_HOST.getName();
			parameter = cfg.getParameter(key);
			String mailHost = parameter.getValue();

			props.put("mail." + protocol + ".host", mailHost);

			key = RequiredParameters.SLIPSTREAM_MAIL_PORT.getName();
			parameter = cfg.getParameter(key);
			if (parameter != null) {
				String mailPort = parameter.getValue();
				props.put("mail." + protocol + ".port", mailPort);
			}

			// Set the name of the user on the SMTP server. This must be
			// specified.
			key = RequiredParameters.SLIPSTREAM_MAIL_USERNAME.getName();
			parameter = cfg.getParameter(key);
			String mailUser = parameter.getValue();
			props.put("mail." + protocol + ".user", mailUser);

			// Determine whether or not the debugging should be enabled for java
			// mail. If the option isn't given, then debugging will be off.
			key = RequiredParameters.SLIPSTREAM_MAIL_DEBUG.getName();
			parameter = cfg.getParameter(key);
			if (parameter != null) {
				String mailDebug = parameter.getValue();
				props.put("mail.debug", Boolean.parseBoolean(mailDebug));
			}

			// Create the session object for later use.
			Session session = Session.getInstance(props);
			session.setDebug(true);

			return session;

		} catch (NullPointerException e) {
			throw new SlipStreamInternalException(
					"required mail parameter is not specified", e);
		}

	}

}
