package com.repohealth.service;

import com.repohealth.github.GitHubClient;
import com.repohealth.model.*;
import com.repohealth.util.CacheService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AnalysisServiceTest {

    @Mock
    private RepositoryService repositoryService;

    @Mock
    private GitHubClient gitHubClient;

    @Mock
    private ReadmeAnalyzer readmeAnalyzer;

    @Mock
    private HealthScoreService healthScoreService;

    @Mock
    private CacheService cacheService;

    private AnalysisService analysisService;

    @BeforeEach
    void setUp() {
        analysisService = new AnalysisService(repositoryService, gitHubClient, readmeAnalyzer, healthScoreService, cacheService);
        // Make cacheService return actual values from the supplier to simulate cache miss
        when(cacheService.get(anyString(), any())).thenAnswer(invocation -> {
            Supplier<?> supplier = invocation.getArgument(1);
            return supplier.get();
        });
    }

    private RepositoryInfo createRepo(String name, int size) {
        RepositoryInfo repo = new RepositoryInfo();
        repo.setName(name);
        repo.setFullName("test/" + name);
        repo.setOwner("testuser");
        repo.setHtmlUrl("https://github.com/testuser/" + name);
        repo.setSize(size);
        repo.setPushedAt(Instant.now());
        return repo;
    }

    private ReadmeQualityResult createReadme(boolean exists) {
        ReadmeQualityResult readme = new ReadmeQualityResult();
        readme.setRepositoryName("test-repo");
        readme.setExists(exists);
        readme.setCharCount(exists ? 500 : 0);
        readme.setWordCount(exists ? 100 : 0);
        readme.setHasOverview(exists);
        readme.setHasFeatures(exists);
        readme.setHasTechStack(exists);
        readme.setHasQuickStart(exists);
        readme.setHasRunInstructions(exists);
        readme.setHasTestInstructions(exists);
        readme.setHasScreenshots(exists);
        readme.setHasLicenseSection(exists);
        return readme;
    }

    private RepoHealthResult createHealthResult(String name, int score, HealthLevel level) {
        RepoHealthResult result = new RepoHealthResult();
        result.setRepositoryName(name);
        result.setHtmlUrl("https://github.com/testuser/" + name);
        result.setScore(score);
        result.setLevel(level);
        result.setStrengths(List.of("Good README"));
        result.setProblems(List.of());
        result.setSuggestions(List.of());
        result.setReadmeQuality(createReadme(true));
        return result;
    }

    @Test
    void testAnalyze_ShouldReturnResultsSortedByScore() {
        RepositoryInfo repo1 = createRepo("repo-1", 1000);
        RepositoryInfo repo2 = createRepo("repo-2", 50000);

        when(repositoryService.getUserRepositories("testuser")).thenReturn(List.of(repo1, repo2));
        when(gitHubClient.getReadmeContent(anyString(), anyString())).thenReturn(Optional.of("# Test README"));
        when(readmeAnalyzer.analyze(anyString(), any())).thenAnswer(invocation -> {
            String name = invocation.getArgument(0);
            ReadmeQualityResult r = createReadme(true);
            r.setRepositoryName(name);
            return r;
        });
        when(healthScoreService.score(any(), any())).thenAnswer(invocation -> {
            RepositoryInfo repo = invocation.getArgument(0);
            if (repo.getName().equals("repo-1")) {
                return createHealthResult("repo-1", 50, HealthLevel.INCOMPLETE);
            } else {
                return createHealthResult("repo-2", 95, HealthLevel.EXCELLENT);
            }
        });

        AnalysisResult result = analysisService.analyze("testuser");

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals(2, result.getTotalRepositories());
        assertEquals(2, result.getResults().size());

        // Should be sorted by score descending: repo-2 (95) first, then repo-1 (50)
        assertEquals("repo-2", result.getResults().get(0).getRepositoryName());
        assertEquals("repo-1", result.getResults().get(1).getRepositoryName());

        AnalysisSummary summary = result.getSummary();
        assertEquals(1, summary.getExcellentCount());
        assertEquals(0, summary.getGoodCount());
        assertEquals(0, summary.getFairCount());
        assertEquals(1, summary.getIncompleteCount());
        assertEquals(0, summary.getArchiveCandidateCount());
    }

    @Test
    void testAnalyze_SingleRepoFailure_ShouldNotAffectOthers() {
        RepositoryInfo repo1 = createRepo("repo-good", 1000);
        RepositoryInfo repo2 = createRepo("repo-fail", 1000);

        when(repositoryService.getUserRepositories("testuser")).thenReturn(List.of(repo1, repo2));
        when(gitHubClient.getReadmeContent(eq("testuser"), eq("repo-good"))).thenReturn(Optional.of("# Good README"));
        when(gitHubClient.getReadmeContent(eq("testuser"), eq("repo-fail"))).thenThrow(new RuntimeException("API Error"));
        when(readmeAnalyzer.analyze(eq("repo-good"), any())).thenReturn(createReadme(true));
        when(healthScoreService.score(any(), any())).thenReturn(createHealthResult("repo-good", 80, HealthLevel.GOOD));

        AnalysisResult result = analysisService.analyze("testuser");

        assertNotNull(result);
        assertEquals(2, result.getResults().size());

        // repo-good should be fine
        assertTrue(result.getResults().stream().anyMatch(r -> "repo-good".equals(r.getRepositoryName()) && r.getScore() == 80));

        // repo-fail should have fallback score 0
        assertTrue(result.getResults().stream().anyMatch(r -> "repo-fail".equals(r.getRepositoryName()) && r.getScore() == 0));
    }

    @Test
    void testAnalyze_EmptyRepo_ShouldReturnEmpty() {
        when(repositoryService.getUserRepositories("testuser")).thenReturn(List.of());

        AnalysisResult result = analysisService.analyze("testuser");

        assertNotNull(result);
        assertEquals(0, result.getTotalRepositories());
        assertTrue(result.getResults().isEmpty());

        AnalysisSummary summary = result.getSummary();
        assertEquals(0, summary.getExcellentCount());
        assertEquals(0, summary.getGoodCount());
        assertEquals(0, summary.getFairCount());
        assertEquals(0, summary.getIncompleteCount());
        assertEquals(0, summary.getArchiveCandidateCount());
    }
}