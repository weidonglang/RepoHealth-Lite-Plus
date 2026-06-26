package com.repohealth.model;

public class AnalysisSummary {

    private int excellentCount;
    private int goodCount;
    private int fairCount;
    private int incompleteCount;
    private int archiveCandidateCount;

    public AnalysisSummary() {
    }

    public AnalysisSummary(int excellentCount, int goodCount, int fairCount, int incompleteCount, int archiveCandidateCount) {
        this.excellentCount = excellentCount;
        this.goodCount = goodCount;
        this.fairCount = fairCount;
        this.incompleteCount = incompleteCount;
        this.archiveCandidateCount = archiveCandidateCount;
    }

    public int getExcellentCount() {
        return excellentCount;
    }

    public void setExcellentCount(int excellentCount) {
        this.excellentCount = excellentCount;
    }

    public int getGoodCount() {
        return goodCount;
    }

    public void setGoodCount(int goodCount) {
        this.goodCount = goodCount;
    }

    public int getFairCount() {
        return fairCount;
    }

    public void setFairCount(int fairCount) {
        this.fairCount = fairCount;
    }

    public int getIncompleteCount() {
        return incompleteCount;
    }

    public void setIncompleteCount(int incompleteCount) {
        this.incompleteCount = incompleteCount;
    }

    public int getArchiveCandidateCount() {
        return archiveCandidateCount;
    }

    public void setArchiveCandidateCount(int archiveCandidateCount) {
        this.archiveCandidateCount = archiveCandidateCount;
    }
}