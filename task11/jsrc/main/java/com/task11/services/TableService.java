package com.task11.services;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;
import com.task11.Utils;
import com.task11.dto.table.TableDbEntry;
import com.task11.dto.table.TableGetResponse;
import com.task11.dto.table.TablePostRequest;
import com.task11.dto.table.TablePostResponse;
import com.task11.dto.table.TablesGetResponse;
import com.task11.repositories.TableRepository;

public class TableService {
    private final Gson gson = new Gson();
    private final TableRepository tableRepository = new TableRepository();

    public APIGatewayProxyResponseEvent handleListTablesRequest() {
        var tableDbEntries = tableRepository.listTables();

        var response = TablesGetResponse.from(tableDbEntries);
        System.out.println("TablesGetResponse = " + response);

        return Utils.createSuccessfulResponseEvent(response);
    }

    public APIGatewayProxyResponseEvent handleGetTableRequest(Long id) {
        var tableEntry = tableRepository.getTable(id);

        var response = TableGetResponse.from(tableEntry);
        System.out.println("TableGetResponse = " + response);

        return Utils.createSuccessfulResponseEvent(response);
    }

    public APIGatewayProxyResponseEvent handleCreateTableRequest(APIGatewayProxyRequestEvent requestEvent) {
        var request = gson.fromJson(requestEvent.getBody(), TablePostRequest.class);
        System.out.println("TablePostRequest = " + request);
        var tableDbEntry = TableDbEntry.from(request);
        System.out.println("TableDbEntry = " + tableDbEntry);

        tableRepository.saveTable(tableDbEntry);

        var response = new TablePostResponse(tableDbEntry.getId());
        System.out.println("TablePostResponse = " + response);

        return Utils.createSuccessfulResponseEvent(response);
    }
}
