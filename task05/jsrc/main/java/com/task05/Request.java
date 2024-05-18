package com.task05;

import java.util.Map;

/*
        {
        "principalId": 1,
        "content": {"name": "John", "surname": "Doe"}
        }
*/
public class Request {
    private Integer principalId;
    private Map<String, String> content;

    public Integer getPrincipalId() {
        return principalId;
    }

    public void setPrincipalId(Integer principalId) {
        this.principalId = principalId;
    }

    public Map<String, String> getContent() {
        return content;
    }

    public void setContent(Map<String, String> content) {
        this.content = content;
    }
}
