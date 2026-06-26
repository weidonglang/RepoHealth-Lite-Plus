package com.repohealth.service;

import com.repohealth.model.ReadmeQualityResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class ReadmeAnalyzerTest {

    private ReadmeAnalyzer analyzer;

    @BeforeEach
    void setUp() {
        analyzer = new ReadmeAnalyzer();
    }

    @Test
    void testAnalyze_WhenNoReadme_ShouldReturnEmpty() {
        ReadmeQualityResult result = analyzer.analyze("test-repo", Optional.empty());

        assertFalse(result.isExists());
        assertEquals("test-repo", result.getRepositoryName());
        assertEquals(0, result.getCharCount());
        assertEquals(0, result.getWordCount());
        assertFalse(result.isHasOverview());
        assertFalse(result.isHasFeatures());
        assertFalse(result.isHasTechStack());
        assertFalse(result.isHasQuickStart());
        assertFalse(result.isHasRunInstructions());
        assertFalse(result.isHasTestInstructions());
        assertFalse(result.isHasScreenshots());
        assertFalse(result.isHasLicenseSection());
        assertFalse(result.getSuggestions().isEmpty());
    }

    @Test
    void testAnalyze_FullReadme_ShouldDetectAllSections() {
        String readme = """
                # Project Overview
                
                ## Introduction
                This is a sample project.
                
                ## Features
                - Feature 1
                - Feature 2
                
                ## Tech Stack
                - Java 17
                - Spring Boot
                
                ## Quick Start
                Follow these steps to get started.
                
                ## Usage
                Run with: mvn spring-boot:run
                
                ## Testing
                Run tests with: mvn test
                
                ## Screenshots
                ![Screenshot](screenshot.png)
                
                ## License
                MIT License
                """;

        ReadmeQualityResult result = analyzer.analyze("full-repo", Optional.of(readme));

        assertTrue(result.isExists());
        assertTrue(result.isHasOverview());
        assertTrue(result.isHasFeatures());
        assertTrue(result.isHasTechStack());
        assertTrue(result.isHasQuickStart());
        assertTrue(result.isHasRunInstructions());
        assertTrue(result.isHasTestInstructions());
        assertTrue(result.isHasScreenshots());
        assertTrue(result.isHasLicenseSection());
        assertTrue(result.getCharCount() > 0);
        assertTrue(result.getWordCount() > 0);
    }

    @Test
    void testAnalyze_MinimalReadme_ShouldSuggestImprovements() {
        String readme = "# Minimal README\nJust a quick note.";
        ReadmeQualityResult result = analyzer.analyze("minimal-repo", Optional.of(readme));

        assertTrue(result.isExists());
        assertFalse(result.isHasOverview());
        assertFalse(result.isHasFeatures());
        assertFalse(result.isHasTechStack());
        assertFalse(result.isHasQuickStart());
        assertFalse(result.isHasRunInstructions());
        assertFalse(result.isHasTestInstructions());
        assertFalse(result.isHasScreenshots());
        assertFalse(result.isHasLicenseSection());

        assertFalse(result.getSuggestions().isEmpty());
        assertTrue(result.getSuggestions().size() >= 7);
    }

    @Test
    void testAnalyze_ChineseKeywords_ShouldDetectSections() {
        String readme = """
                # 项目简介
                
                ## 功能特性
                - 功能1
                
                ## 技术栈
                Java, Spring Boot
                
                ## 快速开始
                快速启动说明
                
                ## 运行
                mvn spring-boot:run
                
                ## 测试
                mvn test
                
                ## 截图
                ![demo](demo.gif)
                
                ## 许可证
                MIT
                """;

        ReadmeQualityResult result = analyzer.analyze("chinese-repo", Optional.of(readme));

        assertTrue(result.isExists());
        assertTrue(result.isHasOverview());
        assertTrue(result.isHasFeatures());
        assertTrue(result.isHasTechStack());
        assertTrue(result.isHasQuickStart());
        assertTrue(result.isHasRunInstructions());
        assertTrue(result.isHasTestInstructions());
        assertTrue(result.isHasScreenshots());
        assertTrue(result.isHasLicenseSection());
    }

    @Test
    void testAnalyze_ShortReadme_ShouldSuggestExpanding() {
        String readme = "# Small project\nA tiny description.";
        ReadmeQualityResult result = analyzer.analyze("short-repo", Optional.of(readme));

        boolean hasExpandSuggestion = result.getSuggestions().stream()
                .anyMatch(s -> s.contains("too short") || s.contains("expanding"));
        assertTrue(hasExpandSuggestion);
    }
}