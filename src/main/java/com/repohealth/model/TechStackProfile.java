package com.repohealth.model;

import java.util.ArrayList;
import java.util.List;

public class TechStackProfile {

    private String repositoryName;
    private List<String> detectedStacks;
    private List<String> detectedFiles;
    private List<String> detectedKeywords;

    public TechStackProfile() {
        this.detectedStacks = new ArrayList<>();
        this.detectedFiles = new ArrayList<>();
        this.detectedKeywords = new ArrayList<>();
    }

    public TechStackProfile(String repositoryName, List<String> detectedStacks,
                            List<String> detectedFiles, List<String> detectedKeywords) {
        this.repositoryName = repositoryName;
        this.detectedStacks = detectedStacks != null ? detectedStacks : new ArrayList<>();
        this.detectedFiles = detectedFiles != null ? detectedFiles : new ArrayList<>();
        this.detectedKeywords = detectedKeywords != null ? detectedKeywords : new ArrayList<>();
    }

    public String getRepositoryName() {
        return repositoryName;
    }

    public void setRepositoryName(String repositoryName) {
        this.repositoryName = repositoryName;
    }

    public List<String> getDetectedStacks() {
        return detectedStacks;
    }

    public void setDetectedStacks(List<String> detectedStacks) {
        this.detectedStacks = detectedStacks != null ? detectedStacks : new ArrayList<>();
    }

    public List<String> getDetectedFiles() {
        return detectedFiles;
    }

    public void setDetectedFiles(List<String> detectedFiles) {
        this.detectedFiles = detectedFiles != null ? detectedFiles : new ArrayList<>();
    }

    public List<String> getDetectedKeywords() {
        return detectedKeywords;
    }

    public void setDetectedKeywords(List<String> detectedKeywords) {
        this.detectedKeywords = detectedKeywords != null ? detectedKeywords : new ArrayList<>();
    }
}