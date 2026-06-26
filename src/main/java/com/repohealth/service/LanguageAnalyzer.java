package com.repohealth.service;

import com.repohealth.model.LanguageStats;
import com.repohealth.model.LanguageSummary;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class LanguageAnalyzer {

    /**
     * 汇总所有仓库的语言构成。
     */
    public LanguageSummary summarize(String username, List<LanguageStats> statsList) {
        LanguageSummary summary = new LanguageSummary();
        summary.setUsername(username);
        summary.setRepositoryCount(statsList.size());

        if (statsList == null || statsList.isEmpty()) {
            summary.setTotalBytes(0);
            summary.setTotalLanguageBytes(new HashMap<>());
            summary.setLanguagePercentages(new HashMap<>());
            summary.setRepositoriesByLanguage(new HashMap<>());
            summary.setRepositoryLanguageStats(new ArrayList<>());
            return summary;
        }

        // Calculate total bytes across all repos
        long totalBytes = statsList.stream()
                .mapToLong(LanguageStats::getTotalBytes)
                .sum();
        summary.setTotalBytes(totalBytes);

        // Calculate total bytes per language across all repos
        Map<String, Long> totalLanguageBytes = new LinkedHashMap<>();
        for (LanguageStats stats : statsList) {
            if (stats.getLanguageBytes() != null) {
                for (Map.Entry<String, Long> entry : stats.getLanguageBytes().entrySet()) {
                    totalLanguageBytes.merge(entry.getKey(), entry.getValue(), Long::sum);
                }
            }
        }

        // Sort by byte count descending
        totalLanguageBytes = totalLanguageBytes.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));

        summary.setTotalLanguageBytes(totalLanguageBytes);

        // Calculate percentages
        Map<String, Double> languagePercentages = new LinkedHashMap<>();
        if (totalBytes > 0) {
            for (Map.Entry<String, Long> entry : totalLanguageBytes.entrySet()) {
                double percentage = (double) entry.getValue() / totalBytes * 100;
                // Round to 1 decimal place
                percentage = Math.round(percentage * 10.0) / 10.0;
                languagePercentages.put(entry.getKey(), percentage);
            }
        }
        summary.setLanguagePercentages(languagePercentages);

        // Calculate repositories by language
        Map<String, List<String>> reposByLanguage = new LinkedHashMap<>();
        for (LanguageStats stats : statsList) {
            if (stats.getLanguageBytes() != null) {
                for (String lang : stats.getLanguageBytes().keySet()) {
                    reposByLanguage.computeIfAbsent(lang, k -> new ArrayList<>()).add(stats.getRepositoryName());
                }
            }
        }
        summary.setRepositoriesByLanguage(reposByLanguage);
        summary.setRepositoryLanguageStats(statsList);

        return summary;
    }
}