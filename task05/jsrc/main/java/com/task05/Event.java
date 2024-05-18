package com.task05;

import java.util.Map;

/*
Event:
        {
        "id": "f356279c-9d04-45fb-9b6e-4ee331e6f4e6", //generated uuid v4
        "principalId": 1,
        "createdAt": "2023-10-20T08:51:33.123Z",
        "body": {"name": "John", "surname": "Doe"}
        }
*/
public class Event {
    private String id;
    private Integer principalId;
    private String createdAt;//date time in ISO 8601 formatted string
    private Map<String, String> body;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getPrincipalId() {
        return principalId;
    }

    public void setPrincipalId(Integer principalId) {
        this.principalId = principalId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public Map<String, String> getBody() {
        return body;
    }

    public void setBody(Map<String, String> body) {
        this.body = body;
    }
}

