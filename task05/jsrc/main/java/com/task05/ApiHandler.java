package com.task05;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.document.ItemUtils;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.syndicate.deployment.model.RetentionSetting;
import software.amazon.awssdk.services.dynamodb.model.ConditionalCheckFailedException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;

import static com.amazonaws.services.dynamodbv2.document.ItemUtils.toAttributeValue;

@LambdaHandler(lambdaName = "api_handler",
	roleName = "api_handler-role",
	logsExpiration = RetentionSetting.ONE_DAY
)
public class ApiHandler implements RequestHandler<Request, Map<String, Object>> {

	private AmazonDynamoDB amazonDynamoDB;
	private Regions REGION = Regions.EU_CENTRAL_1;
	private final String tableName = "cmtr-7f448310-Events";

	public Map<String, Object> handleRequest(Request request, Context context) {
		initDynamoDbClient();
		System.out.println("Lambda with DynamoDB integration");
		Map<String, Object> resultMap = new HashMap<>();
		resultMap.put("statusCode", 201);
		resultMap.put("event", persistData(request));
		return resultMap;
	}

	private Event persistData(Request request) throws ConditionalCheckFailedException {
		Event event = createEvent(request);
		Map<String, AttributeValue> attributesMap = new HashMap<>();
		attributesMap.put("id", new AttributeValue(event.getId()));
		attributesMap.put("principalId", new AttributeValue().withN(event.getPrincipalId().toString()));
		attributesMap.put("createdAt", new AttributeValue(event.getCreatedAt()));
		attributesMap.put("body", new AttributeValue().withM(fromSimpleMap(event.getBody())));
		amazonDynamoDB.putItem(tableName, attributesMap);

		return event;
	}

	private static Map<String, AttributeValue> fromSimpleMap(Map<String, String> map) {
		if (map == null) {
			return null;
		} else {
			Map<String, AttributeValue> result = new LinkedHashMap();
			Iterator var2 = map.entrySet().iterator();

			while(var2.hasNext()) {
				Map.Entry<String, String> entry = (Map.Entry)var2.next();
				result.put(entry.getKey(), toAttributeValue(entry.getValue()));
			}
			return result;
		}
	}

	private Event createEvent(Request request) {
		Event event = new Event();
		event.setCreatedAt(getCurrentDate());
		event.setBody(request.getContent());
		event.setId(UUID.randomUUID().toString());
		event.setPrincipalId(request.getPrincipalId());
		return event;
	}

	private String getCurrentDate() {
		TimeZone tz = TimeZone.getTimeZone("UTC");
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		df.setTimeZone(tz);
		return df.format(new Date());
	}

	private void initDynamoDbClient() {
		this.amazonDynamoDB = AmazonDynamoDBClientBuilder.standard()
				.withRegion(REGION)
				.build();
	}
}
