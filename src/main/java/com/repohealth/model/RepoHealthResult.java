package com.repohealth.model;

import java.util.ArrayList;
import java.util.List;

public class RepoHealthResult {

    private String repositoryName;
    private String htmlUrl;
    private int score;
    private HealthLevel level;
    private List<String> strengths;
    private List<String> problems;
    private List<String> suggestions;
    private ReadmeQualityResult readmeQuality;

    public RepoHealthResult() {
        this.strengths = new ArrayList<>();
        this.problems = new ArrayList<>();
        this.suggestions = new ArrayList<>();
    }

    public RepoHealthResult(String repositoryName, String htmlUrl, int score, HealthLevel level,
                            List<String> strengths, List<String> problems, List<String> suggestions,
                            ReadmeQualityResult readmeQuality) {
        this.repositoryName = repositoryName;
        this.htmlUrl = htmlUrl;
        this.score = score;
        this.level = level;
        this.strengths = strengths != null ? strengths : new ArrayList<>();
        this.problems = problems != null ? problems : new ArrayList<>();
        this.suggestions = suggestions != null ? suggestions : new ArrayList<>();
        this.readmeQuality = readmeQuality;
    }

    public String getRepositoryName() {
        return repositoryName;
    }

    public void setRepositoryName(String repositoryName) {
        this.repositoryName = repositoryName;
    }

    public String getHtmlUrl() {
        return htmlUrl;
    }

    public void setHtmlUrl(String htmlUrl) {
        this.htmlUrl = htmlUrl;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public HealthLevel getLevel() {
        return level;
    }

    public void setLevel(HealthLevel level) {
        this.level = level;
    }

    public List<String> getStrengths() {
        return strengths;
    }

    public void setStrengths(List<String> strengths) {
        this.strengths = strengths != null ? strengths : new ArrayList<>();
    }

    public List<String> getProblems() {
        return problems;
    }

    public void setProblems(List<String> problems) {
        this.problems = problems != null ? problems : new ArrayList<>();
    }

    public List<String> getSuggestions() {
        return suggestions;
    }

    public void setSuggestions(List<String> suggestions) {
        this.suggestions = suggestions != null ? suggestions : new ArrayList<>();
    }

    public ReadmeQualityResult getReadmeQuality() {
        return readmeQuality;
    }

    public void setReadmeQuality(ReadmeQualityResult readmeQuality) {
        this.readmeQuality = readmeQuality;
    }
}