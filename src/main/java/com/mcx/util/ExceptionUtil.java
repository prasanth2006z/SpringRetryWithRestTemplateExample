package com.mcx.util;

import org.springframework.http.HttpStatus;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class ExceptionUtil {

	/**
	 * @param e
	 * @return
	 */
	public static Throwable getRootCause(final Exception e) {
		if (e == null) {
			return null;
		}
		Throwable cause = e;
		while (cause.getCause() != null) {
			cause = cause.getCause();
		}
		return cause;
	}

	private static String getErrorResponse(Exception ex) {
		String responseStr = "{ \"Exception\" :" + "\"" + getRootCause(ex) + "\" }";
		return responseStr;
	}

	public static Message<JsonObject> populateErrorResponse(Exception ex, Message<JsonObject> message) {

		String responseStr = getErrorResponse(ex);
		Message<JsonObject> responseMessage = MessageBuilder
				.withPayload(new Gson().fromJson(responseStr, JsonObject.class)).copyHeaders(message.getHeaders())
				.setHeader("status_code", HttpStatus.INTERNAL_SERVER_ERROR)
				.setHeader("Correlation-Id", message.getHeaders().get("Correlation-Id")).build();

		return responseMessage;
	}

}
