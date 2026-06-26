package com.repohealth.model;

import java.util.ArrayList;
import java.util.List;

public class ResumeShowcaseResult {

    private String repositoryName;
    private String htmlUrl;
    private int score;
    private ShowcaseLevel level;
    private List<String> reasons;
    private List<String> resumeKeywords;
    private String recommendation;

    public ResumeShowcaseResult() {
        this.reasons = new ArrayList<>();
        this.resumeKeywords = new ArrayList<>();
    }

    public ResumeShowcaseResult(String repositoryName, String htmlUrl, int score, ShowcaseLevel level,
                                List<String> reasons, List<String> resumeKeywords, String recommendation) {
        this.repositoryName = repositoryName;
        this.htmlUrl = htmlUrl;
        this.score = score;
        this.level = level;
        this.reasons = reasons != null ? reasons : new ArrayList<>();
        this.resumeKeywords = resumeKeywords != null ? resumeKeywords : new ArrayList<>();
        this.recommendation = recommendation;
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

    public ShowcaseLevel getLevel() {
        return level;
    }

    public void setLevel(ShowcaseLevel level) {
        this.level = level;
    }

    public List<String> getReasons() {
        return reasons;
    }

    public void setReasons(List<String> reasons) {
        this.reasons = reasons != null ? reasons : new ArrayList<>();
    }

    public List<String> getResumeKeywords() {
        return resumeKeywords;
    }

    public void setResumeKeywords(List<String> resumeKeywords) {
        this.resumeKeywords = resumeKeywords != null ? resumeKeywords : new ArrayList<>();
    }

    public String getRecommendation() {
        return recommendation;
    }

    public void setRecommendation(String recommendation) {
        this.recommendation = recommendation;
    }
}