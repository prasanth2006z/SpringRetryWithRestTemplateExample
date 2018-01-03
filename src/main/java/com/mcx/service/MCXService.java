package com.mcx.service;

import org.springframework.messaging.Message;

import com.google.gson.JsonObject;

public interface MCXService {
	
	public Message<JsonObject> buy(Message<JsonObject> message) throws Exception;
	
	public Message<JsonObject> sell(Message<JsonObject> message) throws Exception;

}
