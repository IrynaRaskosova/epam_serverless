package com.task11;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import java.util.HashMap;

public class Utils {
    private static final DynamoDbEnhancedClient dynamoDbClient = DynamoDbEnhancedClient.create();

    public static APIGatewayProxyResponseEvent createSuccessfulResponseEvent(Object body) {
        var gson = new Gson();
        var apiGatewayResponse = new APIGatewayProxyResponseEvent();
        setDefaultHeaders(apiGatewayResponse);
        apiGatewayResponse.getHeaders().put("Content-Type", "application/json");
        apiGatewayResponse.setStatusCode(200);
        apiGatewayResponse.setBody(gson.toJson(body));
        return apiGatewayResponse;
    }

    public static APIGatewayProxyResponseEvent createSuccessfulResponseEvent() {
        var apiGatewayResponse = new APIGatewayProxyResponseEvent();
        setDefaultHeaders(apiGatewayResponse);
        apiGatewayResponse.setStatusCode(200);
        return apiGatewayResponse;
    }

    public static APIGatewayProxyResponseEvent createUnsuccessfulResponseEvent() {
        var apiGatewayResponse = new APIGatewayProxyResponseEvent();
        setDefaultHeaders(apiGatewayResponse);
        apiGatewayResponse.setStatusCode(400);
        return apiGatewayResponse;
    }

    private static void setDefaultHeaders(APIGatewayProxyResponseEvent event) {
        var headers = new HashMap<String, String>();
        headers.put("Access-Control-Allow-Headers", "Content-Type,X-Amz-Date,Authorization,X-Api-Key,X-Amz-Security-Token");
        headers.put("Access-Control-Allow-Origin", "*");
        headers.put("Access-Control-Allow-Methods", "*");
        headers.put("Accept-Version", "*");
        event.setHeaders(headers);
    }

    public static <T> DynamoDbTable<T> getDynamoDbTable(String tableName, Class<T> beanClass) {
        var fullTableName = getResourceNameWithPrefixAndSuffix(tableName);
        return dynamoDbClient.table(fullTableName, TableSchema.fromBean(beanClass));
    }

    public static String getResourceNameWithPrefixAndSuffix(String resourceName) {
        return Apihandler.FUNCTION_NAME.replace("api_handler", resourceName);
    }
}