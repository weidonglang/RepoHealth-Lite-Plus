package com.repohealth.service;

import com.repohealth.model.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
public class HealthScoreService {

    private static final int MAX_SCORE = 100;

    public RepoHealthResult score(RepositoryInfo repo, ReadmeQualityResult readmeQuality) {
        int score = 0;
        List<String> strengths = new ArrayList<>();
        List<String> problems = new ArrayList<>();
        List<String> suggestions = new ArrayList<>();

        // If repository size is 0, max score is 10
        if (repo.getSize() == 0 || repo.getSize() <= 0) {
            score = Math.min(score + 0, 10);
            problems.add("Repository is empty (size = 0)");
            suggestions.add("Delete or archive the empty repository");
            RepoHealthResult result = new RepoHealthResult();
            result.setRepositoryName(repo.getName());
            result.setHtmlUrl(repo.getHtmlUrl());
            result.setScore(score);
            result.setLevel(determineHealthLevel(score));
            result.setStrengths(strengths);
            result.setProblems(problems);
            result.setSuggestions(suggestions);
            result.setReadmeQuality(readmeQuality);
            return result;
        }

        // README exists: +15
        if (readmeQuality.isExists()) {
            score += 15;
            strengths.add("README exists");
        } else {
            problems.add("No README file");
            suggestions.add("Add a README file to describe the project");
        }

        // If README doesn't exist, max score is 50
        if (!readmeQuality.isExists()) {
            score = Math.min(score, 50);
        }

        // README over 300 chars: +10
        if (readmeQuality.getCharCount() >= 300) {
            score += 10;
            strengths.add("README is detailed (over 300 characters)");
        }

        // Has overview: +8
        if (readmeQuality.isHasOverview()) {
            score += 8;
            strengths.add("Has project overview");
        } else {
            suggestions.add("Add a project overview section to README");
        }

        // Has features: +8
        if (readmeQuality.isHasFeatures()) {
            score += 8;
            strengths.add("Has features description");
        } else {
            suggestions.add("Add a features section describing key capabilities");
        }

        // Has tech stack: +8
        if (readmeQuality.isHasTechStack()) {
            score += 8;
            strengths.add("Has tech stack description");
        } else {
            suggestions.add("Add a tech stack section");
        }

        // Has run instructions: +12
        if (readmeQuality.isHasRunInstructions()) {
            score += 12;
            strengths.add("Has run/usage instructions");
        } else {
            suggestions.add("Add run/usage instructions");
        }

        // Has test instructions: +8
        if (readmeQuality.isHasTestInstructions()) {
            score += 8;
            strengths.add("Has testing instructions");
        } else {
            suggestions.add("Add testing instructions");
        }

        // Has screenshots/demo: +8
        if (readmeQuality.isHasScreenshots()) {
            score += 8;
            strengths.add("Has screenshots or demo");
        } else {
            suggestions.add("Add screenshots or demo GIF");
        }

        // Size > 0: +10
        if (repo.getSize() > 0) {
            score += 10;
            strengths.add("Repository has content");
        }

        // Updated in last 180 days: +8
        if (repo.getPushedAt() != null) {
            Instant now = Instant.now();
            Instant sixMonthsAgo = now.minus(180, ChronoUnit.DAYS);
            if (repo.getPushedAt().isAfter(sixMonthsAgo)) {
                score += 8;
                strengths.add("Recently updated (within 180 days)");
            } else {
                problems.add("Repository not updated in the last 180 days");
                suggestions.add("Consider confirming if the project is still maintained");
            }
        }

        // Has license: +5
        if (readmeQuality.isHasLicenseSection()) {
            score += 5;
            strengths.add("Has license information");
        } else {
            suggestions.add("Add license information");
        }

        // If archived, max level is FAIR
        if (repo.isArchived()) {
            if (score > 60) {
                score = 60;
            }
            problems.add("Repository is archived");
            suggestions.add("Consider marking as archived if no longer maintained");
        }

        // Clamp score
        score = Math.min(score, MAX_SCORE);

        RepoHealthResult result = new RepoHealthResult();
        result.setRepositoryName(repo.getName());
        result.setHtmlUrl(repo.getHtmlUrl());
        result.setScore(score);
        result.setLevel(determineHealthLevel(score));
        result.setStrengths(strengths);
        result.setProblems(problems);
        result.setSuggestions(suggestions);
        result.setReadmeQuality(readmeQuality);

        return result;
    }

    private HealthLevel determineHealthLevel(int score) {
        if (score >= 90) {
            return HealthLevel.EXCELLENT;
        } else if (score >= 75) {
            return HealthLevel.GOOD;
        } else if (score >= 60) {
            return HealthLevel.FAIR;
        } else if (score >= 40) {
            return HealthLevel.INCOMPLETE;
        } else {
            return HealthLevel.ARCHIVE_CANDIDATE;
        }
    }
}