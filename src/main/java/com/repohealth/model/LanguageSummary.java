package com.repohealth.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LanguageSummary {

    private String username;
    private int repositoryCount;
    private long totalBytes;
    private Map<String, Long> totalLanguageBytes;
    private Map<String, Double> languagePercentages;
    private Map<String, List<String>> repositoriesByLanguage;
    private List<LanguageStats> repositoryLanguageStats;

    public LanguageSummary() {
        this.totalLanguageBytes = new HashMap<>();
        this.languagePercentages = new HashMap<>();
        this.repositoriesByLanguage = new HashMap<>();
        this.repositoryLanguageStats = new ArrayList<>();
    }

    public LanguageSummary(String username, int repositoryCount, long totalBytes,
                           Map<String, Long> totalLanguageBytes, Map<String, Double> languagePercentages,
                           Map<String, List<String>> repositoriesByLanguage,
                           List<LanguageStats> repositoryLanguageStats) {
        this.username = username;
        this.repositoryCount = repositoryCount;
        this.totalBytes = totalBytes;
        this.totalLanguageBytes = totalLanguageBytes != null ? totalLanguageBytes : new HashMap<>();
        this.languagePercentages = languagePercentages != null ? languagePercentages : new HashMap<>();
        this.repositoriesByLanguage = repositoriesByLanguage != null ? repositoriesByLanguage : new HashMap<>();
        this.repositoryLanguageStats = repositoryLanguageStats != null ? repositoryLanguageStats : new ArrayList<>();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getRepositoryCount() {
        return repositoryCount;
    }

    public void setRepositoryCount(int repositoryCount) {
        this.repositoryCount = repositoryCount;
    }

    public long getTotalBytes() {
        return totalBytes;
    }

    public void setTotalBytes(long totalBytes) {
        this.totalBytes = totalBytes;
    }

    public Map<String, Long> getTotalLanguageBytes() {
        return totalLanguageBytes;
    }

    public void setTotalLanguageBytes(Map<String, Long> totalLanguageBytes) {
        this.totalLanguageBytes = totalLanguageBytes != null ? totalLanguageBytes : new HashMap<>();
    }

    public Map<String, Double> getLanguagePercentages() {
        return languagePercentages;
    }

    public void setLanguagePercentages(Map<String, Double> languagePercentages) {
        this.languagePercentages = languagePercentages != null ? languagePercentages : new HashMap<>();
    }

    public Map<String, List<String>> getRepositoriesByLanguage() {
        return repositoriesByLanguage;
    }

    public void setRepositoriesByLanguage(Map<String, List<String>> repositoriesByLanguage) {
        this.repositoriesByLanguage = repositoriesByLanguage != null ? repositoriesByLanguage : new HashMap<>();
    }

    public List<LanguageStats> getRepositoryLanguageStats() {
        return repositoryLanguageStats;
    }

    public void setRepositoryLanguageStats(List<LanguageStats> repositoryLanguageStats) {
        this.repositoryLanguageStats = repositoryLanguageStats != null ? repositoryLanguageStats : new ArrayList<>();
    }
}