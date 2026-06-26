package com.repohealth.service;

import com.repohealth.model.ReadmeQualityResult;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class ReadmeAnalyzer {

    public ReadmeQualityResult analyze(String repositoryName, Optional<String> readmeContent) {
        ReadmeQualityResult result = new ReadmeQualityResult();
        result.setRepositoryName(repositoryName);

        if (readmeContent.isEmpty()) {
            result.setExists(false);
            result.setCharCount(0);
            result.setWordCount(0);
            result.setHasOverview(false);
            result.setHasFeatures(false);
            result.setHasTechStack(false);
            result.setHasQuickStart(false);
            result.setHasRunInstructions(false);
            result.setHasTestInstructions(false);
            result.setHasScreenshots(false);
            result.setHasLicenseSection(false);
            result.setSuggestions(List.of("Add a README file to describe the project"));
            return result;
        }

        String content = readmeContent.get();
        result.setExists(true);
        result.setCharCount(content.length());
        result.setWordCount(countWords(content));

        result.setHasOverview(containsAnyKeyword(content,
                "简介", "项目简介", "Overview", "Introduction", "About", "Project Overview"));
        result.setHasFeatures(containsAnyKeyword(content,
                "功能", "功能特性", "Features", "Feature", "核心功能"));
        result.setHasTechStack(containsAnyKeyword(content,
                "技术栈", "Tech Stack", "Stack", "Technology", "技术选型"));
        result.setHasQuickStart(containsAnyKeyword(content,
                "快速开始", "Quick Start", "Getting Started", "Start"));
        result.setHasRunInstructions(containsAnyKeyword(content,
                "运行", "Run", "Usage", "启动", "Start", "mvn", "gradle", "npm", "python", "cargo"));
        result.setHasTestInstructions(containsAnyKeyword(content,
                "测试", "Test", "Testing", "JUnit", "pytest", "cargo test", "mvn test", "npm test"));
        result.setHasScreenshots(containsAnyKeyword(content,
                "截图", "Screenshots", "Screenshot", "Demo", "Preview", "GIF", "演示"));
        result.setHasLicenseSection(containsAnyKeyword(content,
                "License", "MIT", "Apache", "GPL", "许可证"));

        result.setSuggestions(generateSuggestions(result));

        return result;
    }

    private int countWords(String text) {
        if (text == null || text.isBlank()) {
            return 0;
        }
        String[] words = text.trim().split("\\s+");
        return words.length;
    }

    private boolean containsAnyKeyword(String text, String... keywords) {
        if (text == null || text.isBlank()) {
            return false;
        }
        String lowerText = text.toLowerCase();
        for (String keyword : keywords) {
            if (lowerText.contains(keyword.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    private List<String> generateSuggestions(ReadmeQualityResult result) {
        List<String> suggestions = new ArrayList<>();

        if (!result.isHasOverview()) {
            suggestions.add("Add a project overview/description section");
        }
        if (!result.isHasFeatures()) {
            suggestions.add("Add a features section listing key capabilities");
        }
        if (!result.isHasTechStack()) {
            suggestions.add("Add a tech stack section to describe technologies used");
        }
        if (!result.isHasRunInstructions()) {
            suggestions.add("Add run/usage instructions");
        }
        if (!result.isHasTestInstructions()) {
            suggestions.add("Add testing instructions");
        }
        if (!result.isHasScreenshots()) {
            suggestions.add("Add screenshots or demo GIF");
        }
        if (!result.isHasLicenseSection()) {
            suggestions.add("Add license information");
        }
        if (result.getCharCount() < 300) {
            suggestions.add("README is too short (less than 300 characters). Consider expanding with more details");
        }

        return suggestions;
    }
}