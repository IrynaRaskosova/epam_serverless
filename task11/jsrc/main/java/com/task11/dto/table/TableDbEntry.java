package com.task11.dto.table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamoDbBean
public class TableDbEntry {
    private Long id;
    private Long number;
    private Long places;
    private Boolean isVip;
    private Long minOrder;

    @DynamoDbPartitionKey
    public Long getId() {
        return this.id;
    }

    public static TableDbEntry from(TablePostRequest request) {
        return TableDbEntry.builder()
                .id(request.getId())
                .number(request.getNumber())
                .places(request.getPlaces())
                .isVip(request.getIsVip())
                .minOrder(request.getMinOrder())
                .build();
    }
}