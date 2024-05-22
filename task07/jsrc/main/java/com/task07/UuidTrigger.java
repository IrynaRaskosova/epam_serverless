package com.task07;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.ScheduledEvent;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.syndicate.deployment.annotations.events.RuleEventSource;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.syndicate.deployment.model.RetentionSetting;

import javax.json.Json;
import javax.json.JsonObject;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;

@LambdaHandler(lambdaName = "uuid_trigger",
	roleName = "uuid_trigger-role",
	logsExpiration = RetentionSetting.ONE_DAY
)
@RuleEventSource(targetRule = "cloudwatch-event-rule")
public class UuidTrigger implements RequestHandler<ScheduledEvent, Void> {

	private static final String BUCKET_NAME = "cmtr-7f448310-uuid-storage-test";

	public Void handleRequest(ScheduledEvent event, Context context) {
		System.out.println("Going to store file on s3");
		uploadToS3();
		System.out.println("File stored on s3");
		return null;
	}

	public void uploadToS3(){
		String S3_OBJECT_KEY = getCurrentDate();
		System.out.println("File name:" + S3_OBJECT_KEY);
		String fileContent = getFileContent();
		System.out.println("File content:" + fileContent);
		byte[] bytes = fileContent.getBytes(StandardCharsets.UTF_8);
		InputStream is = new ByteArrayInputStream(bytes);
		ObjectMetadata meta = new ObjectMetadata();
		meta.setContentLength(bytes.length);
		meta.setContentType("text/plain");
		AmazonS3 s3Client = AmazonS3ClientBuilder.standard().withRegion(Regions.EU_CENTRAL_1).build();
		s3Client.putObject(BUCKET_NAME, S3_OBJECT_KEY, is, meta);
	}

	private String getFileContent() {
		JsonObject model = Json.createObjectBuilder()
				.add("ids",
						Json.createArrayBuilder()
								.add(UUID.randomUUID().toString())
								.add(UUID.randomUUID().toString())
								.add(UUID.randomUUID().toString())
								.add(UUID.randomUUID().toString())
								.add(UUID.randomUUID().toString())
								.add(UUID.randomUUID().toString())
								.add(UUID.randomUUID().toString())
								.add(UUID.randomUUID().toString())
								.add(UUID.randomUUID().toString())
								.add(UUID.randomUUID().toString()))
				.build();

		return model.toString();
	}

	private String getCurrentDate() {
		TimeZone tz = TimeZone.getTimeZone("UTC");
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		df.setTimeZone(tz);
		return df.format(new Date());
	}

}
