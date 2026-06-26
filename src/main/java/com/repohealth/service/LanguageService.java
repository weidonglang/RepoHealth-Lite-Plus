package com.repohealth.service;

import com.repohealth.github.GitHubClient;
import com.repohealth.model.LanguageStats;
import com.repohealth.model.LanguageSummary;
import com.repohealth.model.RepositoryInfo;
import com.repohealth.util.CacheService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class LanguageService {

    private final RepositoryService repositoryService;
    private final GitHubClient gitHubClient;
    private final LanguageAnalyzer languageAnalyzer;
    private final CacheService cacheService;

    public LanguageService(RepositoryService repositoryService,
                           GitHubClient gitHubClient,
                           LanguageAnalyzer languageAnalyzer,
                           CacheService cacheService) {
        this.repositoryService = repositoryService;
        this.gitHubClient = gitHubClient;
        this.languageAnalyzer = languageAnalyzer;
        this.cacheService = cacheService;
    }

    public LanguageSummary getLanguageSummary(String username) {
        String cacheKey = "languages:" + username;
        return cacheService.get(cacheKey, () -> doGetLanguageSummary(username));
    }

    private LanguageSummary doGetLanguageSummary(String username) {
        List<RepositoryInfo> repos = repositoryService.getUserRepositories(username);
        List<LanguageStats> statsList = new ArrayList<>();

        for (RepositoryInfo repo : repos) {
            try {
                String langCacheKey = "lang:" + repo.getOwner() + "/" + repo.getName();
                Map<String, Long> languageBytes = cacheService.get(langCacheKey,
                        () -> gitHubClient.getRepositoryLanguages(repo.getOwner(), repo.getName()));
                LanguageStats stats = new LanguageStats();
                stats.setRepositoryName(repo.getName());
                stats.setLanguageBytes(languageBytes);
                stats.setTotalBytes(languageBytes.values().stream().mapToLong(Long::longValue).sum());

                // Determine primary language
                String primaryLang = "Unknown";
                if (languageBytes != null && !languageBytes.isEmpty()) {
                    primaryLang = languageBytes.entrySet().stream()
                            .max(Map.Entry.comparingByValue())
                            .map(Map.Entry::getKey)
                            .orElse("Unknown");
                }
                stats.setPrimaryLanguage(primaryLang);

                statsList.add(stats);
            } catch (Exception e) {
                // Skip failed repository language analysis
                LanguageStats stats = new LanguageStats();
                stats.setRepositoryName(repo.getName());
                stats.setLanguageBytes(Map.of());
                stats.setTotalBytes(0);
                stats.setPrimaryLanguage("Unknown");
                statsList.add(stats);
            }
        }

        return languageAnalyzer.summarize(username, statsList);
    }
}
