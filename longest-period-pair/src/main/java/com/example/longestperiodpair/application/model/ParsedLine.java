package com.example.longestperiodpair.application.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;


/**
 * Model holding the parsed line data.
 */
@Data
@Builder
public class ParsedLine {

    private String employeeId;
    private String projectId;
    private LocalDate dateFrom;
    private LocalDate dateTo;

}
