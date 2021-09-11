package com.example.longestperiodpair.application.controller;

import com.example.longestperiodpair.application.model.PairPerProject;
import com.example.longestperiodpair.application.service.AttachmentProcessingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


/**
 * Controller handling the operations with attachments.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@CrossOrigin("http://localhost:3000")
@RequestMapping("/api/attachments")
public class AttachmentController {

    /**
     * Default pattern for the parsing of the date strings in the files.
     */
    private static final String DEFAULT_DATE_PATTERN = "yyyy-MM-dd";

    private final AttachmentProcessingService service;

    /**
     * Post endpoint for attachment uploads.
     *
     * @param file the attached file
     * @return response indicating whether the file upload was successful
     */
    @PostMapping(value = "/upload",
             consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
             produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<Void> uploadFile(@RequestParam MultipartFile file,
            @RequestParam(defaultValue = DEFAULT_DATE_PATTERN) String pattern) {
        log.info("Successfully loaded file with name '{}'.",
                 file.getOriginalFilename());

        service.process(file, pattern);
        return ResponseEntity.ok().build();
    }

    /**
     * Get endpoint for fetching processed attachment content.
     *
     * @param fileName the name of the file
     * @return list of {@link PairPerProject} models
     */
    @GetMapping("/{fileName}")
    public ResponseEntity<List<PairPerProject>> getParsedContent(
            @PathVariable String fileName) {
        return ResponseEntity.ok(service.getParsedModels(fileName));
    }

}
