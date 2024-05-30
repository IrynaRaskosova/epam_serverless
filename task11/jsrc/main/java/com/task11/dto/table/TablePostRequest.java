package com.task11.dto.table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TablePostRequest {
    private Long id;
    private Long number;
    private Long places;
    private Boolean isVip;
    private Long minOrder;
}