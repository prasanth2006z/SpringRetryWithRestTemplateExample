package com.mcx.service.impl;

import static org.springframework.http.HttpMethod.POST;

import java.net.SocketTimeoutException;
import java.util.Map;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.http.NoHttpResponseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandlingException;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.mcx.service.MCXService;
import com.mcx.util.ExceptionUtil;


public class MCXServiceImpl implements MCXService {

	@Autowired
	private RestTemplate restTemplate;
	
	private String URI=""; 

	@Override
	public Message<JsonObject> buy(Message<JsonObject> message) throws Exception {
		
		
		try {
			Gson gson = new GsonBuilder().serializeNulls().create();
			final HttpEntity<?> httpEntity = new HttpEntity<Object>(message.getPayload().toString(),
					getHttpHeaders(message.getHeaders()));

			final ResponseEntity<String> response = restTemplate.exchange(URI, POST, httpEntity, String.class, message.getHeaders());

			
			
			
			ResourceAccessException resourceAccessException = new ResourceAccessException("I/O error on POST request for <url>: Read timed out");

            resourceAccessException.initCause(new SocketTimeoutException("Read timed out"));

            Message<String> requestMessage = new GenericMessage<String>("");

            MessageHandlingException messageHandlingException = new MessageHandlingException(requestMessage,"HTTP request execution failed for URI <url>", resourceAccessException);

            throw messageHandlingException;


			
			
			
			//JsonObject responseJson = gson.fromJson(JsonSanitizer.sanitize(response.getBody()), JsonObject.class);

			//Message<JsonObject> responseMessage = MessageBuilder.withPayload(responseJson).copyHeaders(message.getHeaders()).setHeader("Correlation-Id", message.getHeaders().get("Correlation-Id")).setHeader("statusCode", response.getStatusCode().getReasonPhrase()).build();
			
			//return responseMessage;
		} catch (Exception ex) {
			
			if (ex instanceof ResourceAccessException) {
				Throwable t = ExceptionUtils.getRootCause(ex);
				if (t != null && t instanceof SocketTimeoutException) {
					ex = new SocketTimeoutException(
							"Socket Timeout Exception from processRequest-" + t.getMessage());
				}
				if (t != null && t instanceof NoHttpResponseException) {
					ex = new NoHttpResponseException(
							"NoHttpResponseException from processRequest-" + t.getMessage());
				}
			}
			return ExceptionUtil.populateErrorResponse(ex, message);
		}

		
	}

	@Override
	public Message<JsonObject> sell(Message<JsonObject> message) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	private HttpHeaders getHttpHeaders(final Map<String, Object> headers) {
		final HttpHeaders httpHeaders = new HttpHeaders();

		httpHeaders.add("Content-Type", "application/json");

		return httpHeaders;
	}
	
	

}
