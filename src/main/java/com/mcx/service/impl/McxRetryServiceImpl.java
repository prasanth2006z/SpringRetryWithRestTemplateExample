package com.mcx.service.impl;

import java.net.SocketTimeoutException;

import org.apache.commons.lang.StringUtils;
import org.apache.http.NoHttpResponseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.integration.http.HttpHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;

import com.google.gson.JsonObject;
import com.mcx.service.MCXService;
import com.mcx.service.McxRetryService;
import com.mcx.util.ExceptionUtil;

@EnableRetry 
public class McxRetryServiceImpl implements McxRetryService {

	@Autowired
	private MCXService mcxService;

	@Retryable(value = { SocketTimeoutException.class,
			NoHttpResponseException.class }, backoff = @Backoff(delay = 100, maxDelay = 101), maxAttempts = 2)
	public Message<JsonObject> retryMCXBuyService(Message<JsonObject> message) throws Exception {
		Message<?> activatorResponse = mcxService.buy(message);
		return executeRetry(message, activatorResponse);
	}

	/**
	 *
	 * @param message
	 * @param activatorResponse
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Message executeRetry(Message message, Message activatorResponse) throws Exception {
		try {
			if (activatorResponse != null && activatorResponse.getPayload() != null
					&& activatorResponse.getPayload() instanceof JsonObject) {
				JsonObject jsonObject = (JsonObject) activatorResponse.getPayload();
				if (jsonObject != null && jsonObject.has("Exception")) {
					String responseString = jsonObject.get("Exception").getAsString();
					if (responseString != null && StringUtils.contains(responseString, "SocketTimeoutException")) {

						throw (new SocketTimeoutException(responseString));
					}
					if (responseString != null && StringUtils.contains(responseString, "NoHttpResponseException")) {

						throw (new NoHttpResponseException(responseString));
					}
				}
			}
			return activatorResponse;
		} catch (SocketTimeoutException ex) {

			throw (ex);
		} catch (NoHttpResponseException ex) {

			throw (ex);
		} catch (Exception ex) {

			return ExceptionUtil.populateErrorResponse(ex, message);
		}
	}

	/**
	 *
	 * @param ex
	 * @param message
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Recover
	public Message connectionException(SocketTimeoutException ex, Message message) {

		message = MessageBuilder.withPayload(ex.getMessage() == null ? ex.getCause() : ex.getMessage())
				.copyHeaders(message.getHeaders()).setHeader(HttpHeaders.STATUS_CODE, HttpStatus.INTERNAL_SERVER_ERROR)
				.build();
		return ExceptionUtil.populateErrorResponse(ex, message);
	}

	/**
	 *
	 * @param ex
	 * @param message
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Recover
	public Message connectionException(NoHttpResponseException ex, Message message) {

		message = MessageBuilder.withPayload(ex.getMessage() == null ? ex.getCause() : ex.getMessage())
				.copyHeaders(message.getHeaders()).setHeader(HttpHeaders.STATUS_CODE, HttpStatus.INTERNAL_SERVER_ERROR)
				.build();
		return ExceptionUtil.populateErrorResponse(ex, message);
	}
}
