package com.example.longestperiodpair.application.service;

import com.example.longestperiodpair.application.model.PairPerProject;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


/**
 * Handles the processing of the uploaded attachments, the persisting and the
 * retrieving of the processed data.
 */
public interface AttachmentProcessingService {

    /**
     * Processes the content of the provided file with the provided date
     * formatting pattern used for parsing of dates.
     *
     * @param file    the attached file
     * @param pattern the date formatting pattern used for the parsing of the
     *                dates
     */
    void process(MultipartFile file, String pattern);

    /**
     * Persists the parsed models.
     *
     * @param fileName the name of the file
     * @param models   the parsed models
     */
    void persistParsedModels(String fileName, List<PairPerProject> models);

    /**
     * Returns the persisted models extracted from the file with the provided
     * file name.
     *
     * @param fileName the name of the file
     * @return list of {@link PairPerProject} models
     */
    List<PairPerProject> getParsedModels(String fileName);
}
