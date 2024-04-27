package com.task02;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.syndicate.deployment.annotations.lambda.LambdaUrlConfig;
import com.syndicate.deployment.model.RetentionSetting;
import com.syndicate.deployment.model.lambda.url.AuthType;
import com.syndicate.deployment.model.lambda.url.InvokeMode;

import java.util.HashMap;
import java.util.Map;

@LambdaHandler(lambdaName = "hello_world",
		roleName = "hello_world-role",
		//isPublishVersion = true,
		logsExpiration = RetentionSetting.ONE_DAY
)
@LambdaUrlConfig(authType = AuthType.NONE, invokeMode = InvokeMode.RESPONSE_STREAM)
public class HelloWorld implements RequestHandler<Object, Map<String, Object>> {

	public Map<String, Object> handleRequest(Object request, Context context) {
		System.out.println("Hello from lambda");
		Map<String, Object> result = new HashMap<>();
		result.put("statusCode", 200);
		result.put("message", "Hello from Lambda");
		String json = "{}";
		try {
			json = new ObjectMapper().writeValueAsString(result);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}

		Map<String, Object> resultMap = new HashMap<>();
		resultMap.put("statusCode", 200);
		resultMap.put("body", json);

		return resultMap;
	}
}