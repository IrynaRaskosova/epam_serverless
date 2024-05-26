package com.task09;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ListTablesResult;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.syndicate.deployment.annotations.lambda.LambdaUrlConfig;
import com.syndicate.deployment.model.RetentionSetting;
import com.syndicate.deployment.model.TracingMode;
import com.syndicate.deployment.model.lambda.url.AuthType;
import com.syndicate.deployment.model.lambda.url.InvokeMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@LambdaHandler(lambdaName = "processor",
	roleName = "processor-role",
	logsExpiration = RetentionSetting.ONE_DAY,
		tracingMode = TracingMode.Active
)
@LambdaUrlConfig(authType = AuthType.NONE, invokeMode = InvokeMode.BUFFERED)
public class Processor implements RequestHandler<Object, Void> {
	private final OpenMeteoClient openMeteoClient;
	private AmazonDynamoDB amazonDynamoDB;
	private Regions REGION = Regions.EU_CENTRAL_1;
	private String tableName = "cmtr-7f448310-Weather";

	public Processor() {
		this.openMeteoClient = new OpenMeteoClient();
	}

	public Void handleRequest(Object request, Context context) {
		//Subsegment subsegment = AWSXRay.beginSubsegment("ProcessorLambda");
		double latitude = Double.parseDouble("50.4375");
		double longitude = Double.parseDouble("30.5");
		String weather = null;
		try {
			weather = openMeteoClient.getWeather(latitude, longitude);
			initDynamoDbClient();
			WeatherData weatherData = createWeatherData(weather);
			Map<String, AttributeValue> attributesMap = convertWeatherDataToAttributes(weatherData);
			defineTableName();
			amazonDynamoDB.putItem(tableName, attributesMap);
		} catch (Exception e) {
			//AWSXRay.getCurrentSegment().addException(e);
			throw  new RuntimeException(e);
		} finally {
			//AWSXRay.endSubsegment();
		}

		/*Map<String, Object> resultMap = new HashMap<>();
		resultMap.put("statusCode", 200);
		resultMap.put("body", "weatherData was stored to DynamoDB table");

		return resultMap;*/
		return null;
	}

	private static Map<String, AttributeValue> convertWeatherDataToAttributes1(WeatherData weatherData) {
		Map<String, AttributeValue> attributesMap = new HashMap<>();
		attributesMap.put("id", new AttributeValue().withS(UUID.randomUUID().toString()));

		Map<String, AttributeValue> forecastMap = new HashMap<>();
		AttributeValue attributeValue = new AttributeValue().withN(String.valueOf(weatherData.getElevation()));
		forecastMap.put("elevation", attributeValue);
		forecastMap.put("generationtime_ms", new AttributeValue().withN(String.valueOf(weatherData.getGenerationtimeMs())));

		Map<String, AttributeValue> hourlyMap = new HashMap<>();
		hourlyMap.put("temperature_2m", new AttributeValue().withNS(convertDoubleListToStringList(weatherData.getHourly().getTemperature2m())));
		hourlyMap.put("time", new AttributeValue().withSS(weatherData.getHourly().getTime()));
		forecastMap.put("hourly", new AttributeValue().withM(hourlyMap));

		Map<String, AttributeValue> hourlyUnitsMap = new HashMap<>();
		hourlyUnitsMap.put("temperature_2m", new AttributeValue().withS(weatherData.getHourlyUnits().getTemperature2m()));
		hourlyUnitsMap.put("time", new AttributeValue().withS(weatherData.getHourlyUnits().getTime()));
		forecastMap.put("hourly_units", new AttributeValue().withM(hourlyUnitsMap));
		
		forecastMap.put("latitude", new AttributeValue().withN(String.valueOf(weatherData.getLatitude())));
		forecastMap.put("longitude", new AttributeValue().withN(String.valueOf(weatherData.getLongitude())));
		forecastMap.put("timezone", new AttributeValue().withS(weatherData.getTimezone()));
		forecastMap.put("timezone_abbreviation", new AttributeValue().withS(weatherData.getTimezoneAbbreviation()));
		forecastMap.put("utc_offset_seconds", new AttributeValue().withN(String.valueOf(weatherData.getUtcOffsetSeconds())));

		attributesMap.put("forecast", new AttributeValue().withM(forecastMap));
		System.out.println(attributesMap);
		return attributesMap;
	}

	private static List<String> convertDoubleListToStringList(List<Double> doubleList) {
		List<String> stringList = new ArrayList<>();
		for (Double value : doubleList) {
			stringList.add(String.valueOf(value));
		}
		return stringList;
	}

	private void initDynamoDbClient() {
		this.amazonDynamoDB = AmazonDynamoDBClientBuilder.standard()
				.withRegion(REGION)
				.build();
	}

	private void defineTableName() {
		ListTablesResult tables = amazonDynamoDB.listTables();
		for (String name : tables.getTableNames()) {
			if(name.startsWith(tableName)) {
				tableName = name;
			}
		}
	}

	private WeatherData createWeatherData(String weather) {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		try {
			WeatherData weatherData = objectMapper.readValue(weather, WeatherData.class);
			System.out.println("Weather json:" + weather);
			System.out.println("Weather data:" + weatherData);
			return weatherData;
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	public Map<String, AttributeValue> convertWeatherDataToAttributes(WeatherData weatherData) {
		Map<String, AttributeValue> attributesMap = new HashMap<>();
		Map<String, AttributeValue> forecastMap = new HashMap<>();

		// Check for null values and handle them appropriately
		if (weatherData != null) {
			addAttribute(forecastMap, "latitude", weatherData.getLatitude());
			addAttribute(forecastMap, "longitude", weatherData.getLongitude());
			addAttribute(forecastMap, "elevation", weatherData.getElevation());
			addAttribute(forecastMap, "generationtime_ms", weatherData.getGenerationtimeMs());
			addAttribute(forecastMap, "utc_offset_seconds", weatherData.getUtcOffsetSeconds());
			addAttribute(forecastMap, "timezone", weatherData.getTimezone());
			addAttribute(forecastMap, "timezone_abbreviation", weatherData.getTimezoneAbbreviation());

			if (weatherData.getHourly() != null) {
				List<AttributeValue> temperature2mValues = weatherData.getHourly().getTemperature2m().stream()
						.map(temp -> new AttributeValue().withN(String.valueOf(temp)))
						.collect(Collectors.toList());
				forecastMap.put("temperature_2m", new AttributeValue().withL(temperature2mValues));

				List<AttributeValue> timeValues = weatherData.getHourly().getTime().stream()
						.map(time -> new AttributeValue().withS(time))
						.collect(Collectors.toList());
				forecastMap.put("time", new AttributeValue().withL(timeValues));
			}

			if (weatherData.getHourlyUnits() != null) {
				Map<String, AttributeValue> hourlyUnitsMap = new HashMap<>();
				addAttribute(hourlyUnitsMap, "temperature_2m", weatherData.getHourlyUnits().getTemperature2m());
				addAttribute(hourlyUnitsMap, "time", weatherData.getHourlyUnits().getTime());

				forecastMap.put("hourly_units", new AttributeValue().withM(hourlyUnitsMap));
			}

			attributesMap.put("id", new AttributeValue().withS(UUID.randomUUID().toString()));
			attributesMap.put("forecast", new AttributeValue().withM(forecastMap));
		}

		return attributesMap;
	}

	private void addAttribute(Map<String, AttributeValue> map, String key, Object value) {
		if (value != null) {
			if (value instanceof String) {
				map.put(key, new AttributeValue().withS((String) value));
			} else if (value instanceof Number) {
				map.put(key, new AttributeValue().withN(String.valueOf(value)));
			}
		}
	}
}


