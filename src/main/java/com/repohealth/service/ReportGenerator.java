package com.repohealth.service;

import com.repohealth.model.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ReportGenerator {

    public String generateMarkdown(PortfolioReport report, LanguageSummary langSummary,
                                   List<TechStackProfile> techStacks) {
        StringBuilder sb = new StringBuilder();

        // Title
        sb.append("# GitHub Portfolio Report: ").append(report.getUsername()).append("\n\n");

        // 1. Overview
        sb.append("## 1. Overview\n\n");
        sb.append("- **GitHub Username**: ").append(report.getUsername()).append("\n");
        sb.append("- **Total Repositories**: ").append(report.getTotalRepositories()).append("\n");
        sb.append("- **Primary Showcase**: ").append(report.getRecommendedShowcase().size()).append("\n");
        sb.append("- **Secondary Showcase**: ").append(report.getSecondaryShowcase().size()).append("\n");
        sb.append("- **Needs Improvement**: ").append(report.getNeedsImprovement().size()).append("\n");
        sb.append("- **Archive Candidates**: ").append(report.getArchiveCandidates().size()).append("\n");

        // Top 5 Languages
        if (langSummary != null && langSummary.getLanguagePercentages() != null && !langSummary.getLanguagePercentages().isEmpty()) {
            List<String> top5Langs = langSummary.getLanguagePercentages().entrySet().stream()
                    .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                    .limit(5)
                    .map(e -> e.getKey() + " (" + String.format("%.1f", e.getValue()) + "%)")
                    .collect(Collectors.toList());
            sb.append("- **Top Languages**: ").append(String.join(", ", top5Langs)).append("\n");
        }

        // Top 5 Tech Stacks
        if (techStacks != null && !techStacks.isEmpty()) {
            List<String> allStacks = techStacks.stream()
                    .flatMap(t -> t.getDetectedStacks().stream())
                    .collect(Collectors.toList());
            Map<String, Long> stackCount = allStacks.stream()
                    .collect(Collectors.groupingBy(s -> s, Collectors.counting()));
            List<String> top5Stacks = stackCount.entrySet().stream()
                    .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                    .limit(5)
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());
            if (!top5Stacks.isEmpty()) {
                sb.append("- **Top Tech Stacks**: ").append(String.join(", ", top5Stacks)).append("\n");
            }
        }
        sb.append("\n");

        // 2. Language Composition
        sb.append("## 2. Language Composition\n\n");
        if (langSummary != null && langSummary.getLanguagePercentages() != null && !langSummary.getLanguagePercentages().isEmpty()) {
            sb.append("| Language | Percentage | Bytes |\n");
            sb.append("|---|---:|---:|\n");

            List<Map.Entry<String, Double>> sortedLangs = langSummary.getLanguagePercentages().entrySet().stream()
                    .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                    .collect(Collectors.toList());

            for (Map.Entry<String, Double> entry : sortedLangs) {
                String lang = entry.getKey();
                double pct = entry.getValue();
                long bytes = langSummary.getTotalLanguageBytes() != null ?
                        langSummary.getTotalLanguageBytes().getOrDefault(lang, 0L) : 0L;
                sb.append("| ").append(lang)
                        .append(" | ").append(String.format("%.1f", pct)).append("%")
                        .append(" | ").append(bytes)
                        .append(" |\n");
            }
        } else {
            sb.append("No language data available.\n");
        }
        sb.append("\n");

        // 3. Tech Stack Profile
        sb.append("## 3. Tech Stack Profile\n\n");
        if (techStacks != null && !techStacks.isEmpty()) {
            for (TechStackProfile profile : techStacks) {
                sb.append("- **").append(profile.getRepositoryName()).append("**: ");
                if (profile.getDetectedStacks() != null && !profile.getDetectedStacks().isEmpty()) {
                    sb.append(String.join(", ", profile.getDetectedStacks()));
                } else {
                    sb.append("No specific stacks detected");
                }
                sb.append("\n");
            }
        } else {
            sb.append("No tech stack data available.\n");
        }
        sb.append("\n");

        // 4. Repository Health Ranking
        sb.append("## 4. Repository Health Ranking\n\n");
        sb.append("| Repository | Health Score | Level | Main Problems |\n");
        sb.append("|---|---:|---|---|\n");

        List<PortfolioItem> allItems = new ArrayList<>();
        allItems.addAll(report.getRecommendedShowcase());
        allItems.addAll(report.getSecondaryShowcase());
        allItems.addAll(report.getNeedsImprovement());
        allItems.addAll(report.getArchiveCandidates());
        allItems.addAll(report.getNotesOrPractice());

        allItems.sort((a, b) -> Integer.compare(b.getHealthScore(), a.getHealthScore()));

        for (PortfolioItem item : allItems) {
            String problems = item.getReason() != null && !item.getReason().isEmpty()
                    ? item.getReason().substring(0, Math.min(item.getReason().length(), 100))
                    : "None";
            sb.append("| [").append(item.getRepositoryName()).append("](").append(item.getHtmlUrl()).append(")")
                    .append(" | ").append(item.getHealthScore())
                    .append(" | ").append(item.getHealthLevel())
                    .append(" | ").append(problems)
                    .append(" |\n");
        }
        sb.append("\n");

        // 5. Resume Showcase Recommendation
        sb.append("## 5. Resume Showcase Recommendation\n\n");
        sb.append("| Repository | Showcase Score | Recommendation |\n");
        sb.append("|---|---:|---|\n");

        for (PortfolioItem item : report.getRecommendedShowcase()) {
            sb.append("| [").append(item.getRepositoryName()).append("](").append(item.getHtmlUrl()).append(")")
                    .append(" | ").append(item.getShowcaseScore())
                    .append(" | Primary Showcase - ").append(item.getReason() != null ? item.getReason() : "Good project")
                    .append(" |\n");
        }
        for (PortfolioItem item : report.getSecondaryShowcase()) {
            sb.append("| [").append(item.getRepositoryName()).append("](").append(item.getHtmlUrl()).append(")")
                    .append(" | ").append(item.getShowcaseScore())
                    .append(" | Secondary Showcase - ").append(item.getReason() != null ? item.getReason() : "Decent project")
                    .append(" |\n");
        }
        sb.append("\n");

        // 6. Archive Candidates
        sb.append("## 6. Archive Candidates\n\n");
        if (!report.getArchiveCandidates().isEmpty()) {
            for (PortfolioItem item : report.getArchiveCandidates()) {
                sb.append("- **").append(item.getRepositoryName()).append("**\n");
                sb.append("  - **Reason**: ").append(item.getReason() != null ? item.getReason() : "Low quality or empty").append("\n");
                sb.append("  - **Suggestion**: ").append(item.getSuggestion() != null ? item.getSuggestion() : "Consider archiving or deleting").append("\n");
            }
        } else {
            sb.append("No archive candidates found.\n");
        }
        sb.append("\n");

        // 7. Improvement Plan
        sb.append("## 7. Improvement Plan\n\n");
        List<String> priorityActions = report.getPriorityActions();
        if (priorityActions != null && !priorityActions.isEmpty()) {
            for (String action : priorityActions) {
                sb.append("- ").append(action).append("\n");
            }
        } else {
            sb.append("- P0: Delete or archive empty repositories\n");
            sb.append("- P1: Add README to important projects\n");
            sb.append("- P2: Add run instructions\n");
            sb.append("- P3: Add screenshots or GIFs\n");
            sb.append("- P4: Add test instructions\n");
            sb.append("- P5: Organize GitHub Profile README\n");
        }
        sb.append("\n");

        // 8. Resume Description Draft
        sb.append("## 8. Resume Description Draft\n\n");
        sb.append("### English Version\n\n");
        if (!report.getRecommendedShowcase().isEmpty()) {
            sb.append("Key Projects:\n\n");
            for (PortfolioItem item : report.getRecommendedShowcase()) {
                sb.append("- **").append(item.getRepositoryName()).append("**");
                if (item.getDescription() != null && !item.getDescription().isEmpty()) {
                    sb.append(": ").append(item.getDescription());
                }
                sb.append("\n");
            }
        }
        sb.append("\n");
        sb.append("Tech Stack: ");
        if (techStacks != null && !techStacks.isEmpty()) {
            List<String> allStacks = techStacks.stream()
                    .flatMap(t -> t.getDetectedStacks().stream())
                    .distinct()
                    .collect(Collectors.toList());
            sb.append(String.join(", ", allStacks));
        } else {
            sb.append("Various");
        }
        sb.append("\n\n");

        sb.append("### Chinese Version\n\n");
        sb.append("GitHub 作品集分析报告 - 基于 Spring Boot 的 GitHub 作品集体检与语言构成分析工具。\n");
        sb.append("涵盖仓库列表拉取、README 完整度检测、语言构成统计、技术栈识别、");
        sb.append("仓库健康度评分、简历展示价值评分、烂尾项目识别和 Markdown 报告生成。\n");
        sb.append("推荐重点展示 ").append(report.getRecommendedShowcase().size()).append(" 个项目，");
        sb.append("建议归档 ").append(report.getArchiveCandidates().size()).append(" 个项目。\n");

        return sb.toString();
    }
}