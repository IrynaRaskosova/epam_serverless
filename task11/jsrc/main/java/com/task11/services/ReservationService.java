package com.task11.services;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;
import com.task11.Utils;
import com.task11.dto.reservation.ReservationDbEntry;
import com.task11.dto.reservation.ReservationPostRequest;
import com.task11.dto.reservation.ReservationPostResponse;
import com.task11.dto.reservation.ReservationsGetResponse;
import com.task11.repositories.ReservationRepository;
import com.task11.repositories.TableRepository;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

public class ReservationService {
    private final Gson gson = new Gson();
    private final ReservationRepository reservationRepository = new ReservationRepository();
    private final TableRepository tableRepository = new TableRepository();

    public APIGatewayProxyResponseEvent handleListReservationsRequest() {
        var tableDbEntries = reservationRepository.listReservations();

        var response = ReservationsGetResponse.from(tableDbEntries);
        System.out.println("ReservationsGetResponse = " + response);

        return Utils.createSuccessfulResponseEvent(response);
    }

    public APIGatewayProxyResponseEvent handleCreateReservationRequest(APIGatewayProxyRequestEvent requestEvent) {
        var request = gson.fromJson(requestEvent.getBody(), ReservationPostRequest.class);
        System.out.println("ReservationPostRequest = " + request);

        checkIfTableExist(request.getTableNumber());
        checkIfNotOverlapsWithOtherReservation(request);

        var reservationDbEntry = ReservationDbEntry.from(request);
        System.out.println("ReservationDbEntry = " + reservationDbEntry);
        reservationRepository.saveReservation(reservationDbEntry);

        var response = new ReservationPostResponse(reservationDbEntry.getId());
        System.out.println("ReservationPostResponse = " + response);

        return Utils.createSuccessfulResponseEvent(response);
    }

    public void checkIfTableExist(Long tableNumber) {
        tableRepository.listTables().stream()
                .filter(tableDbEntry -> tableDbEntry.getNumber().equals(tableNumber))
                .findFirst()
                .orElseThrow();
    }

    public void checkIfNotOverlapsWithOtherReservation(ReservationPostRequest request) {
        var potentiallyConflictingReservations =reservationRepository.listReservations()
                .stream()
                .filter(reservationDbEntry -> reservationDbEntry.getTableNumber().equals(request.getTableNumber()))
                .filter(reservationDbEntry -> reservationDbEntry.getDate().equals(request.getDate()))
                .collect(Collectors.toList());

        for (var res : potentiallyConflictingReservations) {
            // Check if time overlaps
            var start1 = LocalTime.parse(res.getSlotTimeStart(), DateTimeFormatter.ISO_LOCAL_TIME);
            var end1 = LocalTime.parse(res.getSlotTimeEnd(), DateTimeFormatter.ISO_LOCAL_TIME);
            var start2 = LocalTime.parse(request.getSlotTimeStart(), DateTimeFormatter.ISO_LOCAL_TIME);
            var end2 = LocalTime.parse(request.getSlotTimeEnd(), DateTimeFormatter.ISO_LOCAL_TIME);

            if (isOverlap(start1, end1, start2, end2)) {
                throw new IllegalArgumentException("Reservation for this time slot already exist: " + request);
            }
        }
    }

    private boolean isOverlap(LocalTime start1, LocalTime end1, LocalTime start2, LocalTime end2) {
        return !(end1.isBefore(start2) || start1.isAfter(end2));
    }
}
