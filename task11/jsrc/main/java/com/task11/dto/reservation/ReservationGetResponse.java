package com.task11.dto.reservation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationGetResponse {
    private Long tableNumber;
    private String clientName;
    private String phoneNumber;
    private String date;
    private String slotTimeStart;
    private String slotTimeEnd;

    public static ReservationGetResponse from(ReservationDbEntry dynamoDbEntry) {
        return ReservationGetResponse.builder()
                .tableNumber(dynamoDbEntry.getTableNumber())
                .clientName(dynamoDbEntry.getClientName())
                .phoneNumber(dynamoDbEntry.getPhoneNumber())
                .date(dynamoDbEntry.getDate())
                .slotTimeStart(dynamoDbEntry.getSlotTimeStart())
                .slotTimeEnd(dynamoDbEntry.getSlotTimeEnd())
                .build();
    }
}
