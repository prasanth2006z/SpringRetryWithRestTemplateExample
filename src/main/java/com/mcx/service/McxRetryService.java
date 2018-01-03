package com.mcx.service;

import org.springframework.messaging.Message;

import com.google.gson.JsonObject;

public interface McxRetryService {
	
	public Message<JsonObject> retryMCXBuyService(Message<JsonObject> message) throws Exception;

}
