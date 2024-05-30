package com.task11.dto.table;

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
public class TablesGetResponse {
    private List<TableGetResponse> tables;

    public static TablesGetResponse from(List<TableDbEntry> dynamoDbEntries) {
        var tables =  dynamoDbEntries.stream()
                .map(TableGetResponse::from)
                .collect(Collectors.toList());

        return new TablesGetResponse(tables);
    }
}