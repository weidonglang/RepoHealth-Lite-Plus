package com.repohealth.controller;

import com.repohealth.common.ApiResponse;
import com.repohealth.service.ReportService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/report/{username}")
    public ApiResponse<String> getReport(@PathVariable String username) {
        String markdown = reportService.generateReport(username);
        return ApiResponse.ok(markdown);
    }

    @GetMapping("/report/{username}/download")
    public ResponseEntity<byte[]> downloadReport(@PathVariable String username) {
        String markdown = reportService.generateReport(username);
        String filename = reportService.getReportFilename(username);

        byte[] bytes = markdown.getBytes(java.nio.charset.StandardCharsets.UTF_8);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/markdown;charset=UTF-8"));
        headers.setContentDispositionFormData("attachment", filename);

        return ResponseEntity.ok()
                .headers(headers)
                .body(bytes);
    }
}