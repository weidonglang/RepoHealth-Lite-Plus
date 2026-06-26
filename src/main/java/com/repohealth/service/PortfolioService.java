package com.repohealth.service;

import com.repohealth.model.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PortfolioService {

    private final RepositoryService repositoryService;
    private final AnalysisService analysisService;
    private final TechStackService techStackService;
    private final ResumeScoreService resumeScoreService;
    private final PortfolioAdvisor portfolioAdvisor;
    private final LanguageService languageService;

    public PortfolioService(RepositoryService repositoryService,
                            AnalysisService analysisService,
                            TechStackService techStackService,
                            ResumeScoreService resumeScoreService,
                            PortfolioAdvisor portfolioAdvisor,
                            LanguageService languageService) {
        this.repositoryService = repositoryService;
        this.analysisService = analysisService;
        this.techStackService = techStackService;
        this.resumeScoreService = resumeScoreService;
        this.portfolioAdvisor = portfolioAdvisor;
        this.languageService = languageService;
    }

    public PortfolioReport getPortfolioReport(String username) {
        // Get repositories
        List<RepositoryInfo> repos = repositoryService.getUserRepositories(username);

        // Get health analysis
        AnalysisResult analysisResult = analysisService.analyze(username);
        List<RepoHealthResult> healthResults = analysisResult.getResults();

        // Get tech stacks
        List<TechStackProfile> techStacks = techStackService.getTechStackProfiles(username);

        // Get language stats
        LanguageSummary langSummary = languageService.getLanguageSummary(username);
        List<LanguageStats> languageStats = langSummary != null ? langSummary.getRepositoryLanguageStats() : List.of();

        // Compute showcase scores
        List<ResumeShowcaseResult> showcaseResults = repos.stream()
                .map(repo -> {
                    // Find matching health result
                    RepoHealthResult health = healthResults.stream()
                            .filter(h -> h.getRepositoryName().equals(repo.getName()))
                            .findFirst()
                            .orElse(null);
                    // Find matching tech stack
                    TechStackProfile techStack = techStacks.stream()
                            .filter(t -> t.getRepositoryName().equals(repo.getName()))
                            .findFirst()
                            .orElse(new TechStackProfile());
                    // Find matching language stats
                    LanguageStats langStat = languageStats.stream()
                            .filter(l -> l.getRepositoryName().equals(repo.getName()))
                            .findFirst()
                            .orElse(null);

                    return resumeScoreService.score(repo, health, techStack);
                })
                .collect(Collectors.toList());

        // Advise
        return portfolioAdvisor.advise(username, repos, healthResults, techStacks, showcaseResults, languageStats);
    }
}