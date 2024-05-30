package com.task11.dto.reservation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamoDbBean
public class ReservationDbEntry {
    private String id;
    private Long tableNumber;
    private String clientName;
    private String phoneNumber;
    private String date;
    private String slotTimeStart;
    private String slotTimeEnd;

    @DynamoDbPartitionKey
    public String getId() {
        return this.id;
    }

    public static ReservationDbEntry from(ReservationPostRequest request) {
        return ReservationDbEntry.builder()
                .id(UUID.randomUUID().toString())
                .tableNumber(request.getTableNumber())
                .clientName(request.getClientName())
                .phoneNumber(request.getPhoneNumber())
                .date(request.getDate())
                .slotTimeStart(request.getSlotTimeStart())
                .slotTimeEnd(request.getSlotTimeEnd())
                .build();
    }
}