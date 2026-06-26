package com.repohealth.service;

import com.repohealth.model.LanguageStats;
import com.repohealth.model.LanguageSummary;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class LanguageAnalyzerTest {

    private LanguageAnalyzer languageAnalyzer;

    @BeforeEach
    void setUp() {
        languageAnalyzer = new LanguageAnalyzer();
    }

    @Test
    void testSummarize_TwoRepos_JavaAndPython() {
        LanguageStats repo1 = new LanguageStats();
        repo1.setRepositoryName("repo1");
        repo1.setTotalBytes(1000);
        repo1.setLanguageBytes(Map.of("Java", 800L, "HTML", 200L));
        repo1.setPrimaryLanguage("Java");

        LanguageStats repo2 = new LanguageStats();
        repo2.setRepositoryName("repo2");
        repo2.setTotalBytes(1000);
        repo2.setLanguageBytes(Map.of("Python", 600L, "CSS", 400L));
        repo2.setPrimaryLanguage("Python");

        LanguageSummary summary = languageAnalyzer.summarize("testuser", Arrays.asList(repo1, repo2));

        assertEquals("testuser", summary.getUsername());
        assertEquals(2, summary.getRepositoryCount());
        assertEquals(2000, summary.getTotalBytes());

        // Java: 800/2000 = 40.0%, Python: 600/2000 = 30.0%, HTML: 200/2000 = 10.0%, CSS: 400/2000 = 20.0%
        Map<String, Double> percentages = summary.getLanguagePercentages();
        assertEquals(40.0, percentages.get("Java"), 0.01);
        assertEquals(30.0, percentages.get("Python"), 0.01);
        assertEquals(10.0, percentages.get("HTML"), 0.01);
        assertEquals(20.0, percentages.get("CSS"), 0.01);
    }

    @Test
    void testSummarize_EmptyList() {
        LanguageSummary summary = languageAnalyzer.summarize("testuser", new ArrayList<>());

        assertEquals("testuser", summary.getUsername());
        assertEquals(0, summary.getRepositoryCount());
        assertEquals(0, summary.getTotalBytes());
        assertTrue(summary.getTotalLanguageBytes().isEmpty());
        assertTrue(summary.getLanguagePercentages().isEmpty());
        assertTrue(summary.getRepositoriesByLanguage().isEmpty());
    }

    @Test
    void testSummarize_AllEmptyLanguages() {
        LanguageStats repo1 = new LanguageStats();
        repo1.setRepositoryName("repo1");
        repo1.setTotalBytes(0);
        repo1.setLanguageBytes(new HashMap<>());
        repo1.setPrimaryLanguage("Unknown");

        LanguageSummary summary = languageAnalyzer.summarize("testuser", List.of(repo1));

        assertEquals(0, summary.getTotalBytes());
        assertTrue(summary.getTotalLanguageBytes().isEmpty());
        assertTrue(summary.getLanguagePercentages().isEmpty());
        assertTrue(summary.getRepositoriesByLanguage().isEmpty());
    }

    @Test
    void testSummarize_SameLanguageMultipleRepos() {
        LanguageStats repo1 = new LanguageStats();
        repo1.setRepositoryName("repo1");
        repo1.setTotalBytes(500);
        repo1.setLanguageBytes(Map.of("Java", 500L));
        repo1.setPrimaryLanguage("Java");

        LanguageStats repo2 = new LanguageStats();
        repo2.setRepositoryName("repo2");
        repo2.setTotalBytes(500);
        repo2.setLanguageBytes(Map.of("Java", 500L));
        repo2.setPrimaryLanguage("Java");

        LanguageSummary summary = languageAnalyzer.summarize("testuser", Arrays.asList(repo1, repo2));

        assertEquals(1000, summary.getTotalBytes());
        assertEquals(100.0, summary.getLanguagePercentages().get("Java"), 0.01);

        // Both repos should be listed under Java
        List<String> javaRepos = summary.getRepositoriesByLanguage().get("Java");
        assertNotNull(javaRepos);
        assertEquals(2, javaRepos.size());
        assertTrue(javaRepos.contains("repo1"));
        assertTrue(javaRepos.contains("repo2"));
    }

    @Test
    void testSummarize_PercentagesSortedDescending() {
        LanguageStats repo = new LanguageStats();
        repo.setRepositoryName("repo");
        repo.setTotalBytes(1000);
        repo.setLanguageBytes(Map.of(
                "Rust", 100L,
                "Java", 500L,
                "Python", 300L,
                "Go", 100L
        ));
        repo.setPrimaryLanguage("Java");

        LanguageSummary summary = languageAnalyzer.summarize("testuser", List.of(repo));

        List<String> languages = new ArrayList<>(summary.getLanguagePercentages().keySet());
        // Should be sorted by percentage descending: Java(50.0) -> Python(30.0) -> Rust(10.0) -> Go(10.0)
        assertEquals("Java", languages.get(0));
        assertEquals("Python", languages.get(1));
    }
}