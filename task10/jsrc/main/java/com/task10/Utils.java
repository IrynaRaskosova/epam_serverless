package com.task10;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import java.util.Map;

public class Utils {
    private static final DynamoDbEnhancedClient dynamoDbClient = DynamoDbEnhancedClient.create();

    public static APIGatewayProxyResponseEvent createSuccessfulResponseEvent(Object body) {
        var gson = new Gson();
        var apiGatewayResponse = new APIGatewayProxyResponseEvent();
        apiGatewayResponse.setStatusCode(200);
        apiGatewayResponse.setBody(gson.toJson(body));
        apiGatewayResponse.setHeaders(Map.of("Content-Type", "application/json"));
        return apiGatewayResponse;
    }

    public static APIGatewayProxyResponseEvent createSuccessfulResponseEvent() {
        var apiGatewayResponse = new APIGatewayProxyResponseEvent();
        apiGatewayResponse.setStatusCode(200);
        return apiGatewayResponse;
    }

    public static APIGatewayProxyResponseEvent createUnsuccessfulResponseEvent() {
        var apiGatewayResponse = new APIGatewayProxyResponseEvent();
        apiGatewayResponse.setStatusCode(400);
        return apiGatewayResponse;
    }

    public static <T> DynamoDbTable<T> getDynamoDbTable(String tableName, Class<T> beanClass) {
        var fullTableName = getResourceNameWithPrefixAndSuffix(tableName);
        return dynamoDbClient.table(fullTableName, TableSchema.fromBean(beanClass));
    }

    public static String getResourceNameWithPrefixAndSuffix(String resourceName) {
        return Apihandler.FUNCTION_NAME.replace("api_handler", resourceName);
    }
}