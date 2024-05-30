package com.task11.services;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;
import com.task11.Utils;
import com.task11.dto.user.UserSignInRequest;
import com.task11.dto.user.UserSignInResponse;
import com.task11.dto.user.UserSignupRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminCreateUserRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminInitiateAuthRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminSetUserPasswordRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AuthFlowType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ListUserPoolClientsRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ListUserPoolsRequest;

import java.util.Map;

public class UserService {
    private final Gson gson = new Gson();

    public APIGatewayProxyResponseEvent handleSignupRequest(APIGatewayProxyRequestEvent requestEvent) {
        var request = gson.fromJson(requestEvent.getBody(), UserSignupRequest.class);
        System.out.println("UserSignupRequest = " + request);

        var cognitoClient = CognitoIdentityProviderClient.create();
        var userPoolId = getUserPoolId(cognitoClient);
        var clientId = getClientId(cognitoClient, userPoolId);
        try {
            var createUserRequest = AdminCreateUserRequest.builder()
                    .userPoolId(userPoolId)
                    .temporaryPassword(request.getPassword())
                    .username(request.getEmail())
                    .messageAction("SUPPRESS")
                    .build();

            var setUserPasswordRequest = AdminSetUserPasswordRequest.builder()
                    .userPoolId(userPoolId)
                    .username(request.getEmail())
                    .password(request.getPassword())
                    .permanent(true)
                    .build();

            var createUserResponse = cognitoClient.adminCreateUser(createUserRequest);
            var setUserPasswordResponse = cognitoClient.adminSetUserPassword(setUserPasswordRequest);
            System.out.println("createUserResponse = " + createUserResponse);
            System.out.println("setUserPasswordResponse = " + setUserPasswordResponse);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return Utils.createSuccessfulResponseEvent();
    }

    public APIGatewayProxyResponseEvent handleSigninRequest(APIGatewayProxyRequestEvent requestEvent) {
        var request = gson.fromJson(requestEvent.getBody(), UserSignInRequest.class);
        System.out.println("UserSigninRequest = " + request);

        var cognitoClient = CognitoIdentityProviderClient.create();
        var userPoolId = getUserPoolId(cognitoClient);
        var clientId = getClientId(cognitoClient, userPoolId);

        var authParams = Map.of(
                "USERNAME", request.getEmail(),
                "PASSWORD", request.getPassword()
        );

        var authRequest = AdminInitiateAuthRequest.builder()
                .authFlow(AuthFlowType.ADMIN_NO_SRP_AUTH)
                .userPoolId(userPoolId)
                .clientId(clientId)
                .authParameters(authParams)
                .build();

        var authResponse = cognitoClient.adminInitiateAuth(authRequest);
        System.out.println("Auth response: " + authResponse);
        System.out.println("ID Token: " + authResponse.authenticationResult().idToken());
        System.out.println("Access Token: " + authResponse.authenticationResult().accessToken());
        System.out.println("Refresh Token: " + authResponse.authenticationResult().refreshToken());

        var response = new UserSignInResponse(authResponse.authenticationResult().idToken());

        return Utils.createSuccessfulResponseEvent(response);
    }

    private String getUserPoolId(CognitoIdentityProviderClient cognitoClient) {
        var userPoolName = Utils.getResourceNameWithPrefixAndSuffix("simple-booking-userpool");
        String userPoolId = "";
        try {
            var request = ListUserPoolsRequest.builder()
                    .maxResults(60)
                    .build();

            var response = cognitoClient.listUserPools(request);
            response.userPools().forEach(userpool -> {
                System.out.println("User pool " + userpool.name() + ", User ID " + userpool.id());
            });
            userPoolId = response.userPools().stream()
                    .filter(userPool -> userPoolName.equals(userPool.name()))
                    .findFirst()
                    .orElseThrow()
                    .id();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        System.out.println("UserPoolId = " + userPoolId);

        return userPoolId;
    }

    private String getClientId(CognitoIdentityProviderClient cognitoClient, String userPoolId) {
        String clientId = "";
        try {
            var request = ListUserPoolClientsRequest.builder()
                    .userPoolId(userPoolId)
                    .build();

            var response = cognitoClient.listUserPoolClients(request);
            response.userPoolClients().forEach(userPoolClient -> {
                System.out.println("User pool client " + userPoolClient.clientName() + ", Pool ID "
                        + userPoolClient.userPoolId() + ", Client ID " + userPoolClient.clientId());
            });

            clientId = response.userPoolClients().get(0).clientId();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        System.out.println("Client ID = " + clientId);

        return clientId;
    }
}