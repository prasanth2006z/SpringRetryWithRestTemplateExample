package com.mcx;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mcx.config.AppTestConfig;
import com.mcx.service.MCXService;
import com.mcx.service.McxRetryService;


/**
 * @author Prasanth.P
 */


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AppTestConfig.class)
@WebAppConfiguration
public class AppTest{ 
String sampleRequest="";
	
	@Autowired
	MCXService mcxService;
	
	@Autowired
	McxRetryService mcxRetryService; 
	
	@Test
	public void testMCXBuyService() throws Exception {
		Message<JsonObject> responseMessage =mcxRetryService.retryMCXBuyService(getMessage());
		Assert.assertNotNull(responseMessage);
	}
	
	
	public void testMCXSellService() throws Exception {
		System.out.println(mcxService.sell(null));
	}
	
	private Message<JsonObject> getMessage(){
	    JsonObject transformationMessage = new Gson().fromJson(sampleRequest, JsonObject.class);
	    Message<JsonObject> message = MessageBuilder.withPayload(transformationMessage)
	     
	      .build();

	    return message;

	  }
	
}
