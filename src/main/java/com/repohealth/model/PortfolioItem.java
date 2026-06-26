package com.repohealth.model;

public class PortfolioItem {

    private String repositoryName;
    private String htmlUrl;
    private String description;
    private int healthScore;
    private int showcaseScore;
    private HealthLevel healthLevel;
    private ShowcaseLevel showcaseLevel;
    private String primaryLanguage;
    private String reason;
    private String suggestion;

    public PortfolioItem() {}

    public PortfolioItem(String repositoryName, String htmlUrl, String description,
                         int healthScore, int showcaseScore,
                         HealthLevel healthLevel, ShowcaseLevel showcaseLevel,
                         String primaryLanguage, String reason, String suggestion) {
        this.repositoryName = repositoryName;
        this.htmlUrl = htmlUrl;
        this.description = description;
        this.healthScore = healthScore;
        this.showcaseScore = showcaseScore;
        this.healthLevel = healthLevel;
        this.showcaseLevel = showcaseLevel;
        this.primaryLanguage = primaryLanguage;
        this.reason = reason;
        this.suggestion = suggestion;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getHealthScore() {
        return healthScore;
    }

    public void setHealthScore(int healthScore) {
        this.healthScore = healthScore;
    }

    public int getShowcaseScore() {
        return showcaseScore;
    }

    public void setShowcaseScore(int showcaseScore) {
        this.showcaseScore = showcaseScore;
    }

    public HealthLevel getHealthLevel() {
        return healthLevel;
    }

    public void setHealthLevel(HealthLevel healthLevel) {
        this.healthLevel = healthLevel;
    }

    public ShowcaseLevel getShowcaseLevel() {
        return showcaseLevel;
    }

    public void setShowcaseLevel(ShowcaseLevel showcaseLevel) {
        this.showcaseLevel = showcaseLevel;
    }

    public String getPrimaryLanguage() {
        return primaryLanguage;
    }

    public void setPrimaryLanguage(String primaryLanguage) {
        this.primaryLanguage = primaryLanguage;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getSuggestion() {
        return suggestion;
    }

    public void setSuggestion(String suggestion) {
        this.suggestion = suggestion;
    }
}