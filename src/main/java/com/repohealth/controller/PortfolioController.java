package com.repohealth.controller;

import com.repohealth.common.ApiResponse;
import com.repohealth.model.PortfolioReport;
import com.repohealth.service.PortfolioService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class PortfolioController {

    private final PortfolioService portfolioService;

    public PortfolioController(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    @GetMapping("/portfolio/{username}")
    public ApiResponse<PortfolioReport> getPortfolio(@PathVariable String username) {
        try {
            PortfolioReport report = portfolioService.getPortfolioReport(username);
            return ApiResponse.ok(report);
        } catch (Exception e) {
            return ApiResponse.fail("Failed to generate portfolio report: " + e.getMessage());
        }
    }
}