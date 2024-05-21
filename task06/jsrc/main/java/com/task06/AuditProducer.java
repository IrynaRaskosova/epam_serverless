package com.task06;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ListTablesResult;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.syndicate.deployment.annotations.events.DynamoDbTriggerEventSource;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.syndicate.deployment.model.RetentionSetting;
import software.amazon.awssdk.services.dynamodb.model.ConditionalCheckFailedException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;

@LambdaHandler(lambdaName = "audit_producer",
	roleName = "audit_producer-role",
	logsExpiration = RetentionSetting.ONE_DAY
)
@DynamoDbTriggerEventSource(targetTable = "Configuration", batchSize = 1)
public class AuditProducer implements RequestHandler<DynamodbEvent, Void> {

	private AmazonDynamoDB amazonDynamoDB;
	private Regions REGION = Regions.EU_CENTRAL_1;
	private String tableName = "cmtr-7f448310-Audit";

	public Void handleRequest(DynamodbEvent event, Context context) {
		System.out.println("Before init DB client");
		initDynamoDbClient();
		System.out.println("After init DB client");
		for (DynamodbEvent.DynamodbStreamRecord record : event.getRecords()) {
			if (record == null) {
				continue;
			}
			System.out.println("Record:" + record);
			if ("INSERT".equals(record.getEventName())) {
				System.out.println("In insert event:");
				insertEventHandler(record);
			} else if ("MODIFY".equals(record.getEventName())) {
				System.out.println("In update event:");
				updateEventHandler(record);
			}
		}
		return null;
	}

	private void insertEventHandler(DynamodbEvent.DynamodbStreamRecord record) {
		Audit audit = createAuditForInsert(record);
		System.out.println("InsertAudit:" + audit);
		persistData(audit, false);
	}

	private void updateEventHandler(DynamodbEvent.DynamodbStreamRecord record) {
		Audit audit = createAuditForUpdate(record);
		System.out.println("UpdateAudit:" + audit);
		persistData(audit, true);
	}

	private void persistData(Audit audit, boolean update) throws ConditionalCheckFailedException {
		/*Map<String, AttributeValue> attributesMap = new HashMap<>();
		attributesMap.put("id", new AttributeValue(audit.getId()));
		attributesMap.put("itemKey", new AttributeValue(audit.getItemKey()));
		attributesMap.put("modificationTime", new AttributeValue(audit.getModificationTime()));

		if (!update) {
			Configuration conf = (Configuration) audit.getNewValue();
			Map<String, Object> confParams = new HashMap<>();
			confParams.put("key", conf.getKey());
			confParams.put("value", conf.getValue());
			//attributesMap.put("newValue", new AttributeValue(confParams));
		}
		if (update) {
			attributesMap.put("newValue", new AttributeValue().withN(audit.getNewValue().toString()));
			attributesMap.put("oldValue", new AttributeValue().withN(audit.getOldValue().toString()));
			attributesMap.put("updatedAttribute", new AttributeValue(audit.getUpdatedAttribute()));
		}
		System.out.println("Result map:" + attributesMap);
		defineTableName();
		System.out.println("Table name:" + tableName);
		amazonDynamoDB.putItem(tableName, attributesMap);*/
//////////////////////////////////////////////////////////

		DynamoDB dynamodb = new DynamoDB(amazonDynamoDB);
		defineTableName();
		Table table = dynamodb.getTable(tableName);

	    Item item = new Item()
				.withString("id", audit.getId())
				.withString("itemKey", audit.getItemKey())
				.withString("modificationTime", audit.getModificationTime());

		if (!update) {
			Configuration conf = (Configuration) audit.getNewValue();
			item.withJSON("newValue", toJson(conf));
		}
		if (update) {
				item.withInt("newValue", Integer.valueOf(audit.getNewValue().toString()))
					.withInt("oldValue", Integer.valueOf(audit.getOldValue().toString()))
					.withString("updatedAttribute", audit.getUpdatedAttribute());
		}

		table.putItem(item);
	}

	public String toJson(Configuration configuration) {
		ObjectMapper mapper = new ObjectMapper();

		try {
			return mapper.writeValueAsString(configuration);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static AttributeValue fromMap(Map<? extends Object, ? extends Object> m) {
		var vals = new HashMap<String, AttributeValue>();
		m.forEach((k, v) ->
				vals.put(k.toString(), marshal(v))
		);
		return new AttributeValue().withM(vals);
	}

	public static AttributeValue marshal(Object obj) {
		if (obj instanceof Integer) {
			return new AttributeValue().withN(obj.toString());
		} else {
			return new AttributeValue(obj.toString());
		}
	}


	private Audit createAuditForInsert(DynamodbEvent.DynamodbStreamRecord record) {
		Configuration conf = createNewValue(record.getDynamodb().getNewImage());
		Audit event = new Audit();
		event.setModificationTime(getCurrentDate());
		event.setId(UUID.randomUUID().toString());
		event.setItemKey(conf.getKey());
		event.setNewValue(conf);
		return event;
	}

	private Audit createAuditForUpdate(DynamodbEvent.DynamodbStreamRecord record) {
		Configuration conf = createNewValue(record.getDynamodb().getNewImage());
		Audit event = new Audit();
		event.setModificationTime(getCurrentDate());
		event.setId(UUID.randomUUID().toString());
		event.setItemKey(conf.getKey());
		event.setNewValue(getValue(record.getDynamodb().getNewImage()));
		event.setOldValue(getValue(record.getDynamodb().getOldImage()));
		event.setUpdatedAttribute("value");
		return event;
	}

	private Configuration createNewValue(Map<String, AttributeValue> newValue) {
		Iterator var2 = newValue.entrySet().iterator();
		Configuration conf = new Configuration();
		while(var2.hasNext()) {
			Map.Entry<String, AttributeValue> entry = (Map.Entry)var2.next();
			if("key".equals(entry.getKey())) {
				conf.setKey(entry.getValue().getS());
				System.out.println("Key:" + conf.getKey());
			}
			if("value".equals(entry.getKey())) {
				conf.setValue(Integer.valueOf(entry.getValue().getN()));
				System.out.println("Value:" + conf.getValue());
			}
		}
		return conf;
	}

	private String getValue(Map<String, AttributeValue> values) {
		Iterator var2 = values.entrySet().iterator();
		while(var2.hasNext()) {
			Map.Entry<String, AttributeValue> entry = (Map.Entry)var2.next();
			if("value".equals(entry.getKey())) {
				return entry.getValue().getN();
			}
		}
		return "0";
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

	private void defineTableName() {
		ListTablesResult tables = amazonDynamoDB.listTables();
		for (String name : tables.getTableNames()) {
			if(name.startsWith(tableName)) {
				tableName = name;
			}
		}
	}
}
