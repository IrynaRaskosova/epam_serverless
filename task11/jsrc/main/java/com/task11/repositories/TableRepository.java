package com.task11.repositories;

import com.task11.Utils;
import com.task11.dto.table.TableDbEntry;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;

import java.util.List;
import java.util.stream.Collectors;

public class TableRepository {

    public TableDbEntry getTable(Long id) {
        var dynamoDbTable = getDynamoDbTable();
        var key = Key.builder()
                .partitionValue(id)
                .build();
        var tableEntry = dynamoDbTable.getItem(key);
        System.out.println("tableEntry = " + tableEntry);
        return tableEntry;
    }

    public void saveTable(TableDbEntry tableDbEntry) {
        var dynamoDbTable = getDynamoDbTable();
        dynamoDbTable.putItem(tableDbEntry);
    }

    public List<TableDbEntry> listTables() {
        var dynamoDbTable = getDynamoDbTable();
        var tableDbEntries = dynamoDbTable.scan()
                .items()
                .stream()
                .collect(Collectors.toList());

        System.out.println("tableDbEntries = " + tableDbEntries);

        return tableDbEntries;
    }

    private DynamoDbTable<TableDbEntry> getDynamoDbTable() {
        return Utils.getDynamoDbTable("Tables", TableDbEntry.class);
    }
}
