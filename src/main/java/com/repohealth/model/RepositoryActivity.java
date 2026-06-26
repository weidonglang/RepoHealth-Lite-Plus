package com.repohealth.model;

import java.time.Instant;
import java.util.List;

public class RepositoryActivity {

    private String repositoryName;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant pushedAt;
    private long daysSinceLastPush;
    private long daysSinceLastUpdate;
    private ActivityLevel activityLevel;
    private List<String> activityNotes;

    public RepositoryActivity() {
    }

    public RepositoryActivity(String repositoryName, Instant createdAt, Instant updatedAt, Instant pushedAt,
                              long daysSinceLastPush, long daysSinceLastUpdate, ActivityLevel activityLevel,
                              List<String> activityNotes) {
        this.repositoryName = repositoryName;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.pushedAt = pushedAt;
        this.daysSinceLastPush = daysSinceLastPush;
        this.daysSinceLastUpdate = daysSinceLastUpdate;
        this.activityLevel = activityLevel;
        this.activityNotes = activityNotes;
    }

    public String getRepositoryName() {
        return repositoryName;
    }

    public void setRepositoryName(String repositoryName) {
        this.repositoryName = repositoryName;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Instant getPushedAt() {
        return pushedAt;
    }

    public void setPushedAt(Instant pushedAt) {
        this.pushedAt = pushedAt;
    }

    public long getDaysSinceLastPush() {
        return daysSinceLastPush;
    }

    public void setDaysSinceLastPush(long daysSinceLastPush) {
        this.daysSinceLastPush = daysSinceLastPush;
    }

    public long getDaysSinceLastUpdate() {
        return daysSinceLastUpdate;
    }

    public void setDaysSinceLastUpdate(long daysSinceLastUpdate) {
        this.daysSinceLastUpdate = daysSinceLastUpdate;
    }

    public ActivityLevel getActivityLevel() {
        return activityLevel;
    }

    public void setActivityLevel(ActivityLevel activityLevel) {
        this.activityLevel = activityLevel;
    }

    public List<String> getActivityNotes() {
        return activityNotes;
    }

    public void setActivityNotes(List<String> activityNotes) {
        this.activityNotes = activityNotes;
    }
}