package com.task11.dto.table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TableGetResponse {
    private Long id;
    private Long number;
    private Long places;
    private Boolean isVip;
    private Long minOrder;

    public static TableGetResponse from(TableDbEntry dynamoDbEntry) {
        return TableGetResponse.builder()
                .id(dynamoDbEntry.getId())
                .number(dynamoDbEntry.getNumber())
                .places(dynamoDbEntry.getPlaces())
                .isVip(dynamoDbEntry.getIsVip())
                .minOrder(dynamoDbEntry.getMinOrder())
                .build();
    }
}
