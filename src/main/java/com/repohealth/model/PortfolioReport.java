package com.repohealth.model;

import java.util.List;
import java.util.Map;

public class PortfolioReport {

    private String username;
    private int totalRepositories;
    private List<PortfolioItem> recommendedShowcase;
    private List<PortfolioItem> secondaryShowcase;
    private List<PortfolioItem> needsImprovement;
    private List<PortfolioItem> archiveCandidates;
    private List<PortfolioItem> notesOrPractice;
    private List<String> priorityActions;

    public PortfolioReport() {}

    public PortfolioReport(String username, int totalRepositories,
                           List<PortfolioItem> recommendedShowcase,
                           List<PortfolioItem> secondaryShowcase,
                           List<PortfolioItem> needsImprovement,
                           List<PortfolioItem> archiveCandidates,
                           List<PortfolioItem> notesOrPractice,
                           List<String> priorityActions) {
        this.username = username;
        this.totalRepositories = totalRepositories;
        this.recommendedShowcase = recommendedShowcase;
        this.secondaryShowcase = secondaryShowcase;
        this.needsImprovement = needsImprovement;
        this.archiveCandidates = archiveCandidates;
        this.notesOrPractice = notesOrPractice;
        this.priorityActions = priorityActions;
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

    public List<PortfolioItem> getRecommendedShowcase() {
        return recommendedShowcase;
    }

    public void setRecommendedShowcase(List<PortfolioItem> recommendedShowcase) {
        this.recommendedShowcase = recommendedShowcase;
    }

    public List<PortfolioItem> getSecondaryShowcase() {
        return secondaryShowcase;
    }

    public void setSecondaryShowcase(List<PortfolioItem> secondaryShowcase) {
        this.secondaryShowcase = secondaryShowcase;
    }

    public List<PortfolioItem> getNeedsImprovement() {
        return needsImprovement;
    }

    public void setNeedsImprovement(List<PortfolioItem> needsImprovement) {
        this.needsImprovement = needsImprovement;
    }

    public List<PortfolioItem> getArchiveCandidates() {
        return archiveCandidates;
    }

    public void setArchiveCandidates(List<PortfolioItem> archiveCandidates) {
        this.archiveCandidates = archiveCandidates;
    }

    public List<PortfolioItem> getNotesOrPractice() {
        return notesOrPractice;
    }

    public void setNotesOrPractice(List<PortfolioItem> notesOrPractice) {
        this.notesOrPractice = notesOrPractice;
    }

    public List<String> getPriorityActions() {
        return priorityActions;
    }

    public void setPriorityActions(List<String> priorityActions) {
        this.priorityActions = priorityActions;
    }
}