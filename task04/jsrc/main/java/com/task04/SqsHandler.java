package com.task04;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.amazonaws.services.lambda.runtime.events.SQSEvent.SQSMessage;
import com.syndicate.deployment.annotations.events.SqsTriggerEventSource;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.syndicate.deployment.model.RetentionSetting;


@LambdaHandler(lambdaName = "sqs_handler",
	roleName = "sqs_handler-role",
	//regionScope = RegionScope.ALL,
	logsExpiration = RetentionSetting.ONE_DAY,
	timeout = 20
)
@SqsTriggerEventSource(targetQueue = "async_queue", batchSize = 1)
public class SqsHandler implements RequestHandler<SQSEvent, Void> {

	@Override
	public Void handleRequest(SQSEvent sqsEvent, Context context) {
		LambdaLogger logger = context.getLogger();
		for (SQSMessage msg : sqsEvent.getRecords()) {
			logger.log("Processed message: " + msg.getBody());
		}
		return null;
	}
}
