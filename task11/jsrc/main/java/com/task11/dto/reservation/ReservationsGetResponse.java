package com.task11.dto.reservation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationsGetResponse {
    private List<ReservationGetResponse> reservations;

    public static ReservationsGetResponse from(List<ReservationDbEntry> dynamoDbEntries) {
        var reservations = dynamoDbEntries.stream()
                .map(ReservationGetResponse::from)
                .collect(Collectors.toList());

        return new ReservationsGetResponse(reservations);
    }
}