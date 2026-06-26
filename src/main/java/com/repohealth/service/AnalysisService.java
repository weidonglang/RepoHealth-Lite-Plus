package com.repohealth.service;

import com.repohealth.github.GitHubClient;
import com.repohealth.model.*;
import com.repohealth.util.CacheService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AnalysisService {

    private final RepositoryService repositoryService;
    private final GitHubClient gitHubClient;
    private final ReadmeAnalyzer readmeAnalyzer;
    private final HealthScoreService healthScoreService;
    private final CacheService cacheService;

    public AnalysisService(RepositoryService repositoryService,
                           GitHubClient gitHubClient,
                           ReadmeAnalyzer readmeAnalyzer,
                           HealthScoreService healthScoreService,
                           CacheService cacheService) {
        this.repositoryService = repositoryService;
        this.gitHubClient = gitHubClient;
        this.readmeAnalyzer = readmeAnalyzer;
        this.healthScoreService = healthScoreService;
        this.cacheService = cacheService;
    }

    public AnalysisResult analyze(String username) {
        String cacheKey = "analysis:" + username;
        return cacheService.get(cacheKey, () -> doAnalyze(username));
    }

    private AnalysisResult doAnalyze(String username) {
        List<RepositoryInfo> repos = repositoryService.getUserRepositories(username);

        List<RepoHealthResult> results = new ArrayList<>();
        int excellentCount = 0;
        int goodCount = 0;
        int fairCount = 0;
        int incompleteCount = 0;
        int archiveCandidateCount = 0;

        for (RepositoryInfo repo : repos) {
            try {
                String readmeCacheKey = "readme:" + repo.getOwner() + "/" + repo.getName();
                Optional<String> readmeContent = cacheService.get(readmeCacheKey,
                        () -> gitHubClient.getReadmeContent(repo.getOwner(), repo.getName()));
                ReadmeQualityResult readmeQuality = readmeAnalyzer.analyze(repo.getName(), readmeContent);
                RepoHealthResult healthResult = healthScoreService.score(repo, readmeQuality);
                results.add(healthResult);

                switch (healthResult.getLevel()) {
                    case EXCELLENT -> excellentCount++;
                    case GOOD -> goodCount++;
                    case FAIR -> fairCount++;
                    case INCOMPLETE -> incompleteCount++;
                    case ARCHIVE_CANDIDATE -> archiveCandidateCount++;
                }
            } catch (Exception e) {
                // Skip failed repository, do not interrupt overall analysis
                RepoHealthResult fallback = new RepoHealthResult();
                fallback.setRepositoryName(repo.getName());
                fallback.setHtmlUrl(repo.getHtmlUrl());
                fallback.setScore(0);
                fallback.setLevel(HealthLevel.ARCHIVE_CANDIDATE);
                fallback.setStrengths(new ArrayList<>());
                fallback.setProblems(List.of("Analysis failed: " + e.getMessage()));
                fallback.setSuggestions(List.of("Unable to analyze this repository"));
                fallback.setReadmeQuality(null);
                results.add(fallback);
                archiveCandidateCount++;
            }
        }

        // Sort by score descending
        results.sort((a, b) -> Integer.compare(b.getScore(), a.getScore()));

        AnalysisSummary summary = new AnalysisSummary();
        summary.setExcellentCount(excellentCount);
        summary.setGoodCount(goodCount);
        summary.setFairCount(fairCount);
        summary.setIncompleteCount(incompleteCount);
        summary.setArchiveCandidateCount(archiveCandidateCount);

        AnalysisResult analysisResult = new AnalysisResult();
        analysisResult.setUsername(username);
        analysisResult.setTotalRepositories(repos.size());
        analysisResult.setResults(results);
        analysisResult.setSummary(summary);

        return analysisResult;
    }
}
