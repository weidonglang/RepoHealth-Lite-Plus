package com.repohealth.model;

import java.util.List;

public class AnalysisResult {

    private String username;
    private int totalRepositories;
    private List<RepoHealthResult> results;
    private AnalysisSummary summary;

    public AnalysisResult() {
    }

    public AnalysisResult(String username, int totalRepositories, List<RepoHealthResult> results, AnalysisSummary summary) {
        this.username = username;
        this.totalRepositories = totalRepositories;
        this.results = results;
        this.summary = summary;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getTotalRepositories() {
        return totalRepositories;
    }

    public void setTotalRepositories(int totalRepositories) {
        this.totalRepositories = totalRepositories;
    }

    public List<RepoHealthResult> getResults() {
        return results;
    }

    public void setResults(List<RepoHealthResult> results) {
        this.results = results;
    }

    public AnalysisSummary getSummary() {
        return summary;
    }

    public void setSummary(AnalysisSummary summary) {
        this.summary = summary;
    }
}