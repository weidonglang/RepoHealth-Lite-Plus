package com.repohealth.controller;

import com.repohealth.common.ApiResponse;
import com.repohealth.model.LanguageSummary;
import com.repohealth.service.LanguageService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class LanguageController {

    private final LanguageService languageService;

    public LanguageController(LanguageService languageService) {
        this.languageService = languageService;
    }

    @GetMapping("/languages/{username}")
    public ApiResponse<LanguageSummary> getLanguages(@PathVariable String username) {
        LanguageSummary summary = languageService.getLanguageSummary(username);
        return ApiResponse.ok(summary);
    }
}