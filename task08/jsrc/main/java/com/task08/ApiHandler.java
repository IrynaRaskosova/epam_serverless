package com.task08;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.syndicate.deployment.annotations.lambda.LambdaLayer;
import com.syndicate.deployment.annotations.lambda.LambdaUrlConfig;
import com.syndicate.deployment.model.Architecture;
import com.syndicate.deployment.model.ArtifactExtension;
import com.syndicate.deployment.model.DeploymentRuntime;
import com.syndicate.deployment.model.RetentionSetting;
import com.syndicate.deployment.model.lambda.url.AuthType;
import com.syndicate.deployment.model.lambda.url.InvokeMode;

import java.util.HashMap;
import java.util.Map;

@LambdaHandler(lambdaName = "api_handler",
	roleName = "api_handler-role",
		layers = {"http-layer"},
		runtime = DeploymentRuntime.JAVA11,
		architecture = Architecture.ARM64,
	logsExpiration = RetentionSetting.ONE_DAY
)
@LambdaUrlConfig(authType = AuthType.NONE, invokeMode = InvokeMode.BUFFERED)
@LambdaLayer(layerName="http-layer", layerFileName = "layer", libraries = {"java/lib/httpclient-4.5.13.jar", "java/lib/httpcore-4.4.13.jar"},
		artifactExtension = ArtifactExtension.ZIP,
		architectures = {Architecture.ARM64})
public class ApiHandler implements RequestHandler<Object, Map<String, Object>> {

	private final OpenMeteoClient openMeteoClient;

	public ApiHandler() {
		this.openMeteoClient = new OpenMeteoClient();
	}

	public Map<String, Object> handleRequest(Object request, Context context) {
		/*double latitude = Double.parseDouble(request.getQueryStringParameters().get("lat"));
		double longitude = Double.parseDouble(request.getQueryStringParameters().get("lon"));*/
		double latitude = Double.parseDouble("50.4375");
		double longitude = Double.parseDouble("30.5");
		String weatherData = null;
		try {
			weatherData = openMeteoClient.getWeather(latitude, longitude);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		Map<String, Object> resultMap = new HashMap<>();
		resultMap.put("statusCode", 200);
		resultMap.put("body", weatherData);

		return resultMap;
	}
}
