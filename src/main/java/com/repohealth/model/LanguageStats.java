package com.repohealth.model;

import java.util.HashMap;
import java.util.Map;

public class LanguageStats {

    private String repositoryName;
    private Map<String, Long> languageBytes;
    private long totalBytes;
    private String primaryLanguage;

    public LanguageStats() {
        this.languageBytes = new HashMap<>();
    }

    public LanguageStats(String repositoryName, Map<String, Long> languageBytes, long totalBytes, String primaryLanguage) {
        this.repositoryName = repositoryName;
        this.languageBytes = languageBytes != null ? languageBytes : new HashMap<>();
        this.totalBytes = totalBytes;
        this.primaryLanguage = primaryLanguage;
    }

    public String getRepositoryName() {
        return repositoryName;
    }

    public void setRepositoryName(String repositoryName) {
        this.repositoryName = repositoryName;
    }

    public Map<String, Long> getLanguageBytes() {
        return languageBytes;
    }

    public void setLanguageBytes(Map<String, Long> languageBytes) {
        this.languageBytes = languageBytes != null ? languageBytes : new HashMap<>();
    }

    public long getTotalBytes() {
        return totalBytes;
    }

    public void setTotalBytes(long totalBytes) {
        this.totalBytes = totalBytes;
    }

    public String getPrimaryLanguage() {
        return primaryLanguage;
    }

    public void setPrimaryLanguage(String primaryLanguage) {
        this.primaryLanguage = primaryLanguage;
    }
}