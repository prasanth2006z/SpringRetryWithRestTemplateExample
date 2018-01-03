package com.mcx.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mcx.service.McxRetryService;

@RestController
public class McxController {

	@Autowired
	McxRetryService mcxRetryService; 
	
	private Message<JsonObject> getMessage(String sampleRequest){
	    JsonObject transformationMessage = new Gson().fromJson(sampleRequest, JsonObject.class);
	    Message<JsonObject> message = MessageBuilder.withPayload(transformationMessage)
	      
	      .build();

	    return message;

	  }
	
	@RequestMapping(value="/api")
	public void read() throws Exception {
		String jsonRequest="";

		mcxRetryService.retryMCXBuyService(getMessage(jsonRequest));
	}
}
