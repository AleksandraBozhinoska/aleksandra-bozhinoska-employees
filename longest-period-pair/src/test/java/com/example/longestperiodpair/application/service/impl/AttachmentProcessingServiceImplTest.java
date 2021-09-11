package com.example.longestperiodpair.application.service.impl;

import com.example.longestperiodpair.application.model.PairPerProject;
import com.example.longestperiodpair.application.model.ParsedLine;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests {@link AttachmentProcessingServiceImpl}.
 */
class AttachmentProcessingServiceImplTest {

    private final AttachmentProcessingServiceImpl service
        = new AttachmentProcessingServiceImpl();

    @Test
    void shouldMapToPairsPerProject() {
        var first = ParsedLine.builder().employeeId("1").projectId("1")
                              .dateFrom(LocalDate.of(2020, 4, 15))
                              .dateTo(LocalDate.of(2020, 4, 17)).build();
        var second = ParsedLine.builder().employeeId("2").projectId("1")
                               .dateFrom(LocalDate.of(2020, 4, 17))
                               .dateTo(LocalDate.of(2020, 4, 20)).build();
        var third = ParsedLine.builder().employeeId("3").projectId("1")
                              .dateFrom(LocalDate.of(2020, 4, 16))
                              .dateTo(LocalDate.of(2021, 4, 17)).build();

        var result = service.mapToPairsPerProject(List.of(first, second, third))
                            .collect(Collectors.toList());
        assertEquals(3, result.size());
        assertEquals(0L, result.stream().filter(
            obj -> obj.getEmployeeIdFirst().equals("1") && obj
                .getEmployeeIdSecond().equals("2")).findFirst()
                               .map(PairPerProject::getDaysWorked)
                               .orElse(100L));
        assertEquals(1L, result.stream().filter(
            obj -> obj.getEmployeeIdFirst().equals("1") && obj
                .getEmployeeIdSecond().equals("3")).findFirst()
                               .map(PairPerProject::getDaysWorked)
                               .orElse(100L));
        assertEquals(3L, result.stream().filter(
            obj -> obj.getEmployeeIdFirst().equals("2") && obj
                .getEmployeeIdSecond().equals("3")).findFirst()
                               .map(PairPerProject::getDaysWorked)
                               .orElse(100L));
    }

}
