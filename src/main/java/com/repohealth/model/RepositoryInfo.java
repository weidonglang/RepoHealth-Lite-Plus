package com.repohealth.model;

import java.time.Instant;
import java.util.List;

public class RepositoryInfo {

    private String name;
    private String fullName;
    private String owner;
    private String description;
    private String htmlUrl;
    private String primaryLanguage;
    private int stars;
    private int forks;
    private int watchers;
    private int size;
    private String defaultBranch;
    private boolean archived;
    private boolean fork;
    private boolean disabled;
    private int openIssuesCount;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant pushedAt;
    private List<String> topics;

    public RepositoryInfo() {
    }

    public RepositoryInfo(String name, String fullName, String owner, String description, String htmlUrl,
                          String primaryLanguage, int stars, int forks, int watchers, int size,
                          String defaultBranch, boolean archived, boolean fork, boolean disabled,
                          int openIssuesCount, Instant createdAt, Instant updatedAt, Instant pushedAt,
                          List<String> topics) {
        this.name = name;
        this.fullName = fullName;
        this.owner = owner;
        this.description = description;
        this.htmlUrl = htmlUrl;
        this.primaryLanguage = primaryLanguage;
        this.stars = stars;
        this.forks = forks;
        this.watchers = watchers;
        this.size = size;
        this.defaultBranch = defaultBranch;
        this.archived = archived;
        this.fork = fork;
        this.disabled = disabled;
        this.openIssuesCount = openIssuesCount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.pushedAt = pushedAt;
        this.topics = topics;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getHtmlUrl() {
        return htmlUrl;
    }

    public void setHtmlUrl(String htmlUrl) {
        this.htmlUrl = htmlUrl;
    }

    public String getPrimaryLanguage() {
        return primaryLanguage;
    }

    public void setPrimaryLanguage(String primaryLanguage) {
        this.primaryLanguage = primaryLanguage;
    }

    public int getStars() {
        return stars;
    }

    public void setStars(int stars) {
        this.stars = stars;
    }

    public int getForks() {
        return forks;
    }

    public void setForks(int forks) {
        this.forks = forks;
    }

    public int getWatchers() {
        return watchers;
    }

    public void setWatchers(int watchers) {
        this.watchers = watchers;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getDefaultBranch() {
        return defaultBranch;
    }

    public void setDefaultBranch(String defaultBranch) {
        this.defaultBranch = defaultBranch;
    }

    public boolean isArchived() {
        return archived;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }

    public boolean isFork() {
        return fork;
    }

    public void setFork(boolean fork) {
        this.fork = fork;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public int getOpenIssuesCount() {
        return openIssuesCount;
    }

    public void setOpenIssuesCount(int openIssuesCount) {
        this.openIssuesCount = openIssuesCount;
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

    public List<String> getTopics() {
        return topics;
    }

    public void setTopics(List<String> topics) {
        this.topics = topics;
    }
}