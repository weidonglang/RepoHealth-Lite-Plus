package com.repohealth.service;

import com.repohealth.github.GitHubClient;
import com.repohealth.model.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReportService {

    private final PortfolioService portfolioService;
    private final LanguageService languageService;
    private final TechStackService techStackService;
    private final ReportGenerator reportGenerator;

    public ReportService(PortfolioService portfolioService,
                          LanguageService languageService,
                          TechStackService techStackService,
                          ReportGenerator reportGenerator) {
        this.portfolioService = portfolioService;
        this.languageService = languageService;
        this.techStackService = techStackService;
        this.reportGenerator = reportGenerator;
    }

    public String generateReport(String username) {
        PortfolioReport portfolioReport = portfolioService.getPortfolioReport(username);
        LanguageSummary languageSummary = languageService.getLanguageSummary(username);
        List<TechStackProfile> techStacks = techStackService.getTechStackProfiles(username);

        return reportGenerator.generateMarkdown(portfolioReport, languageSummary, techStacks);
    }

    public String getReportFilename(String username) {
        return username + "-portfolio-report.md";
    }
}