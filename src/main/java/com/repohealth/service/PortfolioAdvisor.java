package com.repohealth.service;

import com.repohealth.model.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PortfolioAdvisor {

    public PortfolioReport advise(String username, List<RepositoryInfo> repos,
                                  List<RepoHealthResult> healthResults,
                                  List<TechStackProfile> techStacks,
                                  List<ResumeShowcaseResult> showcaseResults,
                                  List<LanguageStats> languageStats) {
        List<PortfolioItem> recommendedShowcase = new ArrayList<>();
        List<PortfolioItem> secondaryShowcase = new ArrayList<>();
        List<PortfolioItem> needsImprovement = new ArrayList<>();
        List<PortfolioItem> archiveCandidates = new ArrayList<>();
        List<PortfolioItem> notesOrPractice = new ArrayList<>();

        for (int i = 0; i < repos.size(); i++) {
            RepositoryInfo repo = repos.get(i);
            RepoHealthResult health = i < healthResults.size() ? healthResults.get(i) : null;
            ResumeShowcaseResult showcase = i < showcaseResults.size() ? showcaseResults.get(i) : null;
            TechStackProfile techStack = i < techStacks.size() ? techStacks.get(i) : null;
            LanguageStats langStat = i < languageStats.size() ? languageStats.get(i) : null;

            PortfolioItem item = createPortfolioItem(repo, health, showcase, langStat);
            String category = categorize(repo, health, showcase);

            switch (category) {
                case "recommendedShowcase":
                    recommendedShowcase.add(item);
                    break;
                case "secondaryShowcase":
                    secondaryShowcase.add(item);
                    break;
                case "needsImprovement":
                    needsImprovement.add(item);
                    break;
                case "archiveCandidates":
                    archiveCandidates.add(item);
                    break;
                case "notesOrPractice":
                    notesOrPractice.add(item);
                    break;
                default:
                    needsImprovement.add(item);
            }
        }

        // Generate priority actions
        List<String> priorityActions = generatePriorityActions(archiveCandidates, needsImprovement);

        return new PortfolioReport(username, repos.size(),
                recommendedShowcase, secondaryShowcase, needsImprovement,
                archiveCandidates, notesOrPractice, priorityActions);
    }

    private String categorize(RepositoryInfo repo, RepoHealthResult health, ResumeShowcaseResult showcase) {
        String repoName = repo.getName() != null ? repo.getName().toLowerCase() : "";
        boolean isFork = repo.isFork();
        boolean isArchived = repo.isArchived();
        boolean isEmpty = repo.getSize() == 0;
        int healthScore = health != null ? health.getScore() : 0;
        int showcaseScore = showcase != null ? showcase.getScore() : 0;

        // Check notes or practice first
        if (isPracticeRepo(repoName, repo.getDescription())) {
            return "notesOrPractice";
        }

        // recommendedShowcase
        if (showcaseScore >= 80 && healthScore >= 70 && !isFork && !isArchived && !isEmpty) {
            return "recommendedShowcase";
        }

        // secondaryShowcase
        if (showcaseScore >= 60 && showcaseScore < 80 && healthScore >= 60) {
            return "secondaryShowcase";
        }

        // archiveCandidates
        if (healthScore < 40 || isEmpty || (health == null || repo.getSize() < 10 && (health == null || !health.getReadmeQuality().isExists()))) {
            return "archiveCandidates";
        }

        // needsImprovement
        if (healthScore >= 40 && healthScore < 60) {
            return "needsImprovement";
        }

        // If showcase is high but health is low, needs improvement
        if (showcaseScore >= 60 && healthScore < 70) {
            return "needsImprovement";
        }

        return "needsImprovement";
    }

    private boolean isPracticeRepo(String repoName, String description) {
        if (repoName == null) return false;
        String desc = description != null ? description.toLowerCase() : "";

        String[] practicePatterns = {
                "notes", "review", "practice", "scripts", "刷题", "笔记",
                "learning", "tutorial", "demo", "test", "sandbox", "playground"
        };

        for (String pattern : practicePatterns) {
            if (repoName.contains(pattern) || desc.contains(pattern)) {
                return true;
            }
        }
        return false;
    }

    private PortfolioItem createPortfolioItem(RepositoryInfo repo, RepoHealthResult health,
                                              ResumeShowcaseResult showcase, LanguageStats langStat) {
        PortfolioItem item = new PortfolioItem();
        item.setRepositoryName(repo.getName());
        item.setHtmlUrl(repo.getHtmlUrl());
        item.setDescription(repo.getDescription());
        item.setHealthScore(health != null ? health.getScore() : 0);
        item.setShowcaseScore(showcase != null ? showcase.getScore() : 0);
        item.setHealthLevel(health != null ? health.getLevel() : HealthLevel.ARCHIVE_CANDIDATE);
        item.setShowcaseLevel(showcase != null ? showcase.getLevel() : ShowcaseLevel.ARCHIVE_CANDIDATE);
        item.setPrimaryLanguage(repo.getPrimaryLanguage() != null ? repo.getPrimaryLanguage() : "Unknown");

        if (showcase != null && showcase.getReasons() != null && !showcase.getReasons().isEmpty()) {
            item.setReason(String.join("; ", showcase.getReasons()));
        } else if (health != null && health.getProblems() != null && !health.getProblems().isEmpty()) {
            item.setReason(String.join("; ", health.getProblems()));
        } else {
            item.setReason("General repository");
        }

        if (health != null && health.getSuggestions() != null && !health.getSuggestions().isEmpty()) {
            item.setSuggestion(String.join("; ", health.getSuggestions()));
        } else {
            item.setSuggestion("No specific suggestions");
        }

        return item;
    }

    private List<String> generatePriorityActions(List<PortfolioItem> archiveCandidates,
                                                  List<PortfolioItem> needsImprovement) {
        List<String> actions = new ArrayList<>();

        if (!archiveCandidates.isEmpty()) {
            actions.add("P0: Delete or archive " + archiveCandidates.size() + " empty/low-quality repositories");
        }

        long needsReadme = needsImprovement.stream()
                .filter(item -> item.getHealthScore() < 60)
                .count();
        if (needsReadme > 0) {
            actions.add("P1: Add or improve README for " + needsReadme + " important projects");
        }

        long needsRunInstructions = needsImprovement.stream()
                .filter(item -> item.getSuggestion() != null && item.getSuggestion().contains("运行"))
                .count();
        if (needsRunInstructions > 0) {
            actions.add("P2: Add run instructions for key projects");
        }

        actions.add("P3: Add screenshots or GIFs for showcase projects");
        actions.add("P4: Add test instructions for key projects");
        actions.add("P5: Organize GitHub Profile README");

        return actions;
    }
}