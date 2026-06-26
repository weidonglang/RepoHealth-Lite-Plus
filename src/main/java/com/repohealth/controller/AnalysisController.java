package com.repohealth.controller;

import com.repohealth.common.ApiResponse;
import com.repohealth.model.AnalysisResult;
import com.repohealth.service.AnalysisService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AnalysisController {

    private final AnalysisService analysisService;

    public AnalysisController(AnalysisService analysisService) {
        this.analysisService = analysisService;
    }

    @GetMapping("/analyze/{username}")
    public ApiResponse<AnalysisResult> analyze(@PathVariable String username) {
        AnalysisResult result = analysisService.analyze(username);
        return ApiResponse.ok(result);
    }
}