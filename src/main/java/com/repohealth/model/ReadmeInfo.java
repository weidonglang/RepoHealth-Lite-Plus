package com.repohealth.model;

public class ReadmeInfo {

    private String repositoryName;
    private boolean exists;
    private String content;
    private int charCount;
    private int wordCount;

    public ReadmeInfo() {
    }

    public ReadmeInfo(String repositoryName, boolean exists, String content, int charCount, int wordCount) {
        this.repositoryName = repositoryName;
        this.exists = exists;
        this.content = content;
        this.charCount = charCount;
        this.wordCount = wordCount;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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
}