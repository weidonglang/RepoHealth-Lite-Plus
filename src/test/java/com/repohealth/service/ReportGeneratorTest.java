package com.repohealth.service;

import com.repohealth.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class ReportGeneratorTest {

    private ReportGenerator reportGenerator;

    @BeforeEach
    void setUp() {
        reportGenerator = new ReportGenerator();
    }

    @Test
    void testGenerateMarkdown_containsAllSections() {
        PortfolioReport report = createSampleReport();
        LanguageSummary langSummary = createSampleLanguageSummary();
        List<TechStackProfile> techStacks = createSampleTechStacks();

        String markdown = reportGenerator.generateMarkdown(report, langSummary, techStacks);

        assertTrue(markdown.contains("GitHub Portfolio Report: testuser"));
        assertTrue(markdown.contains("## 1. Overview"));
        assertTrue(markdown.contains("## 2. Language Composition"));
        assertTrue(markdown.contains("## 3. Tech Stack Profile"));
        assertTrue(markdown.contains("## 4. Repository Health Ranking"));
        assertTrue(markdown.contains("## 5. Resume Showcase Recommendation"));
        assertTrue(markdown.contains("## 6. Archive Candidates"));
        assertTrue(markdown.contains("## 7. Improvement Plan"));
        assertTrue(markdown.contains("## 8. Resume Description Draft"));
    }

    @Test
    void testGenerateMarkdown_containsLanguageTable() {
        PortfolioReport report = createSampleReport();
        LanguageSummary langSummary = createSampleLanguageSummary();
        List<TechStackProfile> techStacks = createSampleTechStacks();

        String markdown = reportGenerator.generateMarkdown(report, langSummary, techStacks);

        assertTrue(markdown.contains("| Language | Percentage | Bytes |"));
        assertTrue(markdown.contains("Java"));
        assertTrue(markdown.contains("42.3%"));
        assertTrue(markdown.contains("Python"));
        assertTrue(markdown.contains("18.7%"));
    }

    @Test
    void testGenerateMarkdown_containsHealthRankingTable() {
        PortfolioReport report = createSampleReport();
        LanguageSummary langSummary = createSampleLanguageSummary();
        List<TechStackProfile> techStacks = createSampleTechStacks();

        String markdown = reportGenerator.generateMarkdown(report, langSummary, techStacks);

        assertTrue(markdown.contains("| Repository | Health Score | Level | Main Problems |"));
        assertTrue(markdown.contains("excellent-repo"));
        assertTrue(markdown.contains("bad-repo"));
        assertTrue(markdown.contains("EXCELLENT"));
        assertTrue(markdown.contains("ARCHIVE_CANDIDATE"));
    }

    @Test
    void testGenerateMarkdown_containsShowcaseRecommendations() {
        PortfolioReport report = createSampleReport();
        LanguageSummary langSummary = createSampleLanguageSummary();
        List<TechStackProfile> techStacks = createSampleTechStacks();

        String markdown = reportGenerator.generateMarkdown(report, langSummary, techStacks);

        assertTrue(markdown.contains("Primary Showcase"));
        assertTrue(markdown.contains("Secondary Showcase"));
    }

    @Test
    void testGenerateMarkdown_containsArchiveCandidates() {
        PortfolioReport report = createSampleReport();
        LanguageSummary langSummary = createSampleLanguageSummary();
        List<TechStackProfile> techStacks = createSampleTechStacks();

        String markdown = reportGenerator.generateMarkdown(report, langSummary, techStacks);

        assertTrue(markdown.contains("bad-repo"));
        assertTrue(markdown.contains("Low quality or empty"));
        assertTrue(markdown.contains("Consider archiving or deleting"));
    }

    @Test
    void testGenerateMarkdown_withNullLanguageSummary() {
        PortfolioReport report = createSampleReport();
        List<TechStackProfile> techStacks = createSampleTechStacks();

        String markdown = reportGenerator.generateMarkdown(report, null, techStacks);

        assertTrue(markdown.contains("No language data available"));
        assertTrue(markdown.contains("## 1. Overview"));
    }

    @Test
    void testGenerateMarkdown_withEmptyTechStacks() {
        PortfolioReport report = createSampleReport();
        LanguageSummary langSummary = createSampleLanguageSummary();

        String markdown = reportGenerator.generateMarkdown(report, langSummary, List.of());

        assertTrue(markdown.contains("No tech stack data available"));
    }

    @Test
    void testGenerateMarkdown_containsImprovementPlan() {
        PortfolioReport report = createSampleReport();
        LanguageSummary langSummary = createSampleLanguageSummary();
        List<TechStackProfile> techStacks = createSampleTechStacks();

        String markdown = reportGenerator.generateMarkdown(report, langSummary, techStacks);

        assertTrue(markdown.contains("P0"));
        assertTrue(markdown.contains("P1"));
    }

    private PortfolioReport createSampleReport() {
        PortfolioItem excellentItem = new PortfolioItem(
                "excellent-repo", "https://github.com/testuser/excellent-repo",
                "An excellent project", 95, 90,
                HealthLevel.EXCELLENT, ShowcaseLevel.PRIMARY_SHOWCASE,
                "Java", "Great project with good README", "None"
        );

        PortfolioItem badItem = new PortfolioItem(
                "bad-repo", "https://github.com/testuser/bad-repo",
                "A bad project", 25, 15,
                HealthLevel.ARCHIVE_CANDIDATE, ShowcaseLevel.ARCHIVE_CANDIDATE,
                "Unknown", "Low quality or empty", "Consider archiving or deleting"
        );

        return new PortfolioReport(
                "testuser", 2,
                List.of(excellentItem),
                List.of(),
                List.of(),
                List.of(badItem),
                List.of(),
                List.of("P0: Delete or archive empty repositories", "P1: Add README to important projects")
        );
    }

    private LanguageSummary createSampleLanguageSummary() {
        LanguageSummary summary = new LanguageSummary();
        summary.setUsername("testuser");
        summary.setRepositoryCount(2);
        summary.setTotalBytes(2000L);

        Map<String, Long> totalBytes = new LinkedHashMap<>();
        totalBytes.put("Java", 846L);
        totalBytes.put("Python", 374L);
        totalBytes.put("HTML", 200L);
        totalBytes.put("JavaScript", 300L);
        totalBytes.put("CSS", 100L);
        totalBytes.put("Rust", 180L);
        summary.setTotalLanguageBytes(totalBytes);

        Map<String, Double> percentages = new LinkedHashMap<>();
        percentages.put("Java", 42.3);
        percentages.put("Python", 18.7);
        percentages.put("HTML", 10.0);
        percentages.put("JavaScript", 15.0);
        percentages.put("CSS", 5.0);
        percentages.put("Rust", 9.0);
        summary.setLanguagePercentages(percentages);

        return summary;
    }

    private List<TechStackProfile> createSampleTechStacks() {
        TechStackProfile profile1 = new TechStackProfile();
        profile1.setRepositoryName("excellent-repo");
        profile1.setDetectedStacks(List.of("Java", "Spring Boot", "Maven"));
        profile1.setDetectedFiles(List.of("pom.xml"));
        profile1.setDetectedKeywords(List.of("Spring Boot"));

        TechStackProfile profile2 = new TechStackProfile();
        profile2.setRepositoryName("bad-repo");
        profile2.setDetectedStacks(List.of());
        profile2.setDetectedFiles(List.of());
        profile2.setDetectedKeywords(List.of());

        return List.of(profile1, profile2);
    }
}