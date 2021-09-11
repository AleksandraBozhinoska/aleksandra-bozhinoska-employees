package com.example.longestperiodpair.application.service.impl;

import com.example.longestperiodpair.application.model.PairPerProject;
import com.example.longestperiodpair.application.model.ParsedLine;
import com.example.longestperiodpair.application.service.AttachmentProcessingService;
import com.example.longestperiodpair.application.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * Reads and processed the content of the files and logs information about
 * which pair of employees have worked together
 * for the longest time. Persists the processed file content in memory.
 */
@Slf4j
@Service
public class AttachmentProcessingServiceImpl
    implements AttachmentProcessingService {

    private final Map<String, List<PairPerProject>> processedFiles
        = new HashMap<>();

    /*
     * Other approach for the flexible date pattern would be defining it in
     * the configuration, in case it does not
     * need to be passed from the frontend application.
     * Note that this is not used here, it is just for demonstration purposes.
     */
    @Value("${parser.date-time-formatter-pattern:yyyy-MM-dd}")
    String dateFormatterPattern;

    /**
     * Parses the provided file content, converts the content to
     * {@link PairPerProject} models, persists them in memory
     * and then logs information about the pair of employees that have worked
     * together for the longest time. If no
     * employees have worked together (no overlap between the time intervals
     * during which they worked on same projects),
     * logs this information instead.
     *
     * @param file    the attached file
     * @param pattern the date formatting pattern used for the parsing of the
     *                dates
     */
    @Override
    public void process(MultipartFile file, String pattern) {
        try (
            InputStream is = file.getInputStream();
            InputStreamReader fis = new InputStreamReader(is,
                StandardCharsets.UTF_8);
            BufferedReader reader = new BufferedReader(fis)
        ) {
            Map<String, List<ParsedLine>> parsedModelsPerProject = reader
                .lines().map(String::trim)
                .filter(Predicate.not(String::isEmpty))
                .map((line) -> mapToParsedLine(line, pattern))
                .collect(Collectors.groupingBy(ParsedLine::getProjectId));

            List<PairPerProject> pairsPerProjects = parsedModelsPerProject
                .values().stream().flatMap(this::mapToPairsPerProject)
                .collect(Collectors.toList());
            persistParsedModels(file.getOriginalFilename(), pairsPerProjects);

            computeAndLogLongestWorkingPair(pairsPerProjects);
        } catch (IOException e) {
            log.error("An error occurred while parsing file with name '{}'.",
                      file.getOriginalFilename(), e);
        }
    }

    /**
     * Returns stream of {@link PairPerProject} models for the provided list of
     * models with a specific project id.
     * The resulting models are computed by calculating the number of days
     * during which each pair of the employees on
     * the project have worked together. If no overlap between the time
     * intervals during which two employees worked on
     * the project, this number is set to 0.
     * Package-private modifier for testing purpose.
     *
     * @param parsedModels the parsed models with a specific project id
     * @return stream of {@link PairPerProject} model for each pair of employee
     * ids present in the input
     */
    Stream<PairPerProject> mapToPairsPerProject(List<ParsedLine> parsedModels) {
        String projectId = parsedModels.stream().iterator().next()
                                       .getProjectId();
        List<PairPerProject> resultList = new ArrayList<>();

        int length = parsedModels.size();
        for (int i = 0; i < length - 1; i++) {
            for (int j = i + 1; j < length; j++) {
                ParsedLine first = parsedModels.get(i);
                ParsedLine second = parsedModels.get(j);
                if (DateUtils.checkIfIntervalsOverlap(first.getDateFrom(),
                                                      first.getDateTo(),
                                                      second.getDateFrom(),
                                                      second.getDateTo())) {
                    resultList.add(
                        PairPerProject.builder().projectId(projectId)
                                .employeeIdFirst(first.getEmployeeId())
                                .employeeIdSecond(second.getEmployeeId())
                                .daysWorked(DateUtils.findIntervalOverlapInDays(
                                        first.getDateFrom(),
                                        first.getDateTo(),
                                        second.getDateFrom(),
                                        second.getDateTo()))
                                .build());
                } else {
                    resultList.add(PairPerProject.builder().projectId(projectId)
                                                 .employeeIdFirst(
                                                     first.getEmployeeId())
                                                 .employeeIdSecond(
                                                     second.getEmployeeId())
                                                 .daysWorked(0).build());
                }
            }
        }

        return resultList.stream();
    }

    /**
     * Persists the provided {@link PairPerProject} models in
     * {@link #processedFiles} map with the provided file name as
     * key.
     *
     * @param fileName the name of the file used as key in the
     *                 {@link #processedFiles} map
     * @param models   the computed {@link PairPerProject} models
     */
    @Override
    public void persistParsedModels(String fileName,
                                    List<PairPerProject> models) {
        this.processedFiles.put(fileName, models);
    }

    /**
     * Retrieves and returns the {@link PairPerProject} models from the
     * {@link #processedFiles} map for the provided key.
     *
     * @param fileName the name of the file used as key in the
     *                 {@link #processedFiles} map
     * @return persisted {@link PairPerProject} models for the file name
     */
    @Override
    public List<PairPerProject> getParsedModels(String fileName) {
        return this.processedFiles.get(fileName);
    }

    /**
     * Sums the {@link PairPerProject#getDaysWorked()} for the models with the
     * matching employee ids and computes the
     * maximum value. Logs this information.
     *
     * @param pairsPerProjects all {@link PairPerProject} extracted from the
     *                         initial file
     */
    private void computeAndLogLongestWorkingPair(
            List<PairPerProject> pairsPerProjects) {
        Map<Set<String>, Long> durationSums = pairsPerProjects.stream().collect(
            Collectors.toMap(pair -> Set.of(pair.getEmployeeIdFirst(),
                                            pair.getEmployeeIdSecond()),
                             PairPerProject::getDaysWorked,
                             Long::sum));

        Optional<Map.Entry<Set<String>, Long>> longestWorkingPair = durationSums
            .entrySet().stream().max(Map.Entry.comparingByValue());

        log.info(
            "===== Displaying the result for the LONGEST WORKING PAIR ====="
                .concat(System.lineSeparator()));
        if (longestWorkingPair.isEmpty()) {
            log.warn(
                "The pair of employees that have worked together for the " +
                "longest time cannot be determined.");
        } else {
            if (longestWorkingPair.get().getValue() == 0L) {
                log.info("No employees have worked together.");
                return;
            }

            log.info(
                "The pair of employees that have worked together for the " +
                "longest time is '{}', {} days total.",
                longestWorkingPair.get().getKey().stream().map(
                    str -> String.format("Employee Id: %s", str))
                                  .collect(Collectors.joining(", ")),
                longestWorkingPair.get().getValue());
        }
    }

    /**
     * Maps the parsed line to a {@link ParsedLine} model using the provided
     * date formatting pattern.
     *
     * @param line                  the parsed line
     * @param dateFormattingPattern the date formatting pattern to be used
     */
    private ParsedLine mapToParsedLine(String line,
                                       String dateFormattingPattern) {
        DateTimeFormatter formatter = DateTimeFormatter
            .ofPattern(dateFormattingPattern);

        String[] parts = line.split(",");
        if (parts.length < 4) {
            throw new IllegalStateException("Unexpected line format");
        }

        String dateFirstString = parts[2].trim();
        String dateSecondString = parts[3].trim();
        LocalDate dateFirst = LocalDate.parse(dateFirstString, formatter);

        LocalDate dateSecond = LocalDate.now();
        if (!"null".equalsIgnoreCase(dateSecondString)) {
            dateSecond = LocalDate.parse(dateSecondString, formatter);
        }

        return ParsedLine.builder().employeeId(parts[0].trim())
                         .projectId(parts[1].trim()).dateFrom(dateFirst)
                         .dateTo(dateSecond).build();
    }
}
