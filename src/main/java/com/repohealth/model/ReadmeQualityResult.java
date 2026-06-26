package com.repohealth.model;

import java.util.ArrayList;
import java.util.List;

public class ReadmeQualityResult {

    private String repositoryName;
    private boolean exists;
    private int charCount;
    private int wordCount;
    private boolean hasOverview;
    private boolean hasFeatures;
    private boolean hasTechStack;
    private boolean hasQuickStart;
    private boolean hasRunInstructions;
    private boolean hasTestInstructions;
    private boolean hasScreenshots;
    private boolean hasLicenseSection;
    private List<String> suggestions;

    public ReadmeQualityResult() {
        this.suggestions = new ArrayList<>();
    }

    public ReadmeQualityResult(String repositoryName, boolean exists, int charCount, int wordCount,
                               boolean hasOverview, boolean hasFeatures, boolean hasTechStack,
                               boolean hasQuickStart, boolean hasRunInstructions, boolean hasTestInstructions,
                               boolean hasScreenshots, boolean hasLicenseSection, List<String> suggestions) {
        this.repositoryName = repositoryName;
        this.exists = exists;
        this.charCount = charCount;
        this.wordCount = wordCount;
        this.hasOverview = hasOverview;
        this.hasFeatures = hasFeatures;
        this.hasTechStack = hasTechStack;
        this.hasQuickStart = hasQuickStart;
        this.hasRunInstructions = hasRunInstructions;
        this.hasTestInstructions = hasTestInstructions;
        this.hasScreenshots = hasScreenshots;
        this.hasLicenseSection = hasLicenseSection;
        this.suggestions = suggestions != null ? suggestions : new ArrayList<>();
    }

    public String getRepositoryName() {
        return repositoryName;
    }

    public void setRepositoryName(String repositoryName) {
        this.repositoryName = repositoryName;
    }

    public boolean isExists() {
        return exists;
    }

    public void setExists(boolean exists) {
        this.exists = exists;
    }

    public int getCharCount() {
        return charCount;
    }

    public void setCharCount(int charCount) {
        this.charCount = charCount;
    }

    public int getWordCount() {
        return wordCount;
    }

    public void setWordCount(int wordCount) {
        this.wordCount = wordCount;
    }

    public boolean isHasOverview() {
        return hasOverview;
    }

    public void setHasOverview(boolean hasOverview) {
        this.hasOverview = hasOverview;
    }

    public boolean isHasFeatures() {
        return hasFeatures;
    }

    public void setHasFeatures(boolean hasFeatures) {
        this.hasFeatures = hasFeatures;
    }

    public boolean isHasTechStack() {
        return hasTechStack;
    }

    public void setHasTechStack(boolean hasTechStack) {
        this.hasTechStack = hasTechStack;
    }

    public boolean isHasQuickStart() {
        return hasQuickStart;
    }

    public void setHasQuickStart(boolean hasQuickStart) {
        this.hasQuickStart = hasQuickStart;
    }

    public boolean isHasRunInstructions() {
        return hasRunInstructions;
    }

    public void setHasRunInstructions(boolean hasRunInstructions) {
        this.hasRunInstructions = hasRunInstructions;
    }

    public boolean isHasTestInstructions() {
        return hasTestInstructions;
    }

    public void setHasTestInstructions(boolean hasTestInstructions) {
        this.hasTestInstructions = hasTestInstructions;
    }

    public boolean isHasScreenshots() {
        return hasScreenshots;
    }

    public void setHasScreenshots(boolean hasScreenshots) {
        this.hasScreenshots = hasScreenshots;
    }

    public boolean isHasLicenseSection() {
        return hasLicenseSection;
    }

    public void setHasLicenseSection(boolean hasLicenseSection) {
        this.hasLicenseSection = hasLicenseSection;
    }

    public List<String> getSuggestions() {
        return suggestions;
    }

    public void setSuggestions(List<String> suggestions) {
        this.suggestions = suggestions != null ? suggestions : new ArrayList<>();
    }
}