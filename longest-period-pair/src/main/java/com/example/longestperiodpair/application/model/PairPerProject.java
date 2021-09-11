package com.example.longestperiodpair.application.model;

import lombok.Builder;
import lombok.Data;


/**
 * Pair per project data.
 */
@Data
@Builder
public class PairPerProject {

    private String employeeIdFirst;
    private String employeeIdSecond;
    private String projectId;
    private long daysWorked;

}
