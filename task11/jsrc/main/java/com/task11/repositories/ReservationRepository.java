package com.task11.repositories;

import com.task11.Utils;
import com.task11.dto.reservation.ReservationDbEntry;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;

import java.util.List;
import java.util.stream.Collectors;

public class ReservationRepository {

    public void saveReservation(ReservationDbEntry reservationDbEntry) {
        var dynamoDbTable = getDynamoDbTable();
        dynamoDbTable.putItem(reservationDbEntry);
    }

    public List<ReservationDbEntry> listReservations() {
        var dynamoDbTable = getDynamoDbTable();
        var reservationDbEntries = dynamoDbTable.scan()
                .items()
                .stream()
                .collect(Collectors.toList());

        System.out.println("reservationDbEntries = " + reservationDbEntries);

        return reservationDbEntries;
    }

    private DynamoDbTable<ReservationDbEntry> getDynamoDbTable() {
        return Utils.getDynamoDbTable("Reservations", ReservationDbEntry.class);
    }
}
