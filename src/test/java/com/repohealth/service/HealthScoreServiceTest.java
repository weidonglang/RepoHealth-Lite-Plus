package com.repohealth.service;

import com.repohealth.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HealthScoreServiceTest {

    private HealthScoreService service;

    @BeforeEach
    void setUp() {
        service = new HealthScoreService();
    }

    private RepositoryInfo createRepo(String name, int size, boolean archived, boolean fork, Instant pushedAt) {
        RepositoryInfo repo = new RepositoryInfo();
        repo.setName(name);
        repo.setFullName("test/" + name);
        repo.setOwner("test");
        repo.setHtmlUrl("https://github.com/test/" + name);
        repo.setSize(size);
        repo.setArchived(archived);
        repo.setFork(fork);
        repo.setPushedAt(pushedAt);
        return repo;
    }

    private ReadmeQualityResult createReadme(boolean exists, int charCount, boolean hasOverview,
                                              boolean hasFeatures, boolean hasTechStack,
                                              boolean hasQuickStart, boolean hasRunInstructions,
                                              boolean hasTestInstructions, boolean hasScreenshots,
                                              boolean hasLicenseSection) {
        ReadmeQualityResult readme = new ReadmeQualityResult();
        readme.setExists(exists);
        readme.setCharCount(charCount);
        readme.setWordCount(charCount > 0 ? charCount / 5 : 0);
        readme.setHasOverview(hasOverview);
        readme.setHasFeatures(hasFeatures);
        readme.setHasTechStack(hasTechStack);
        readme.setHasQuickStart(hasQuickStart);
        readme.setHasRunInstructions(hasRunInstructions);
        readme.setHasTestInstructions(hasTestInstructions);
        readme.setHasScreenshots(hasScreenshots);
        readme.setHasLicenseSection(hasLicenseSection);
        return readme;
    }

    @Test
    void testScore_EmptyRepo_ShouldBeMax10() {
        RepositoryInfo repo = createRepo("empty-repo", 0, false, false, Instant.now());
        ReadmeQualityResult readme = createReadme(false, 0, false, false, false, false, false, false, false, false);

        RepoHealthResult result = service.score(repo, readme);

        assertTrue(result.getScore() <= 10);
        assertEquals(HealthLevel.ARCHIVE_CANDIDATE, result.getLevel());
        assertTrue(result.getProblems().stream().anyMatch(p -> p.contains("empty")));
    }

    @Test
    void testScore_NoReadme_ShouldBeMax50() {
        RepositoryInfo repo = createRepo("no-readme", 1000, false, false, Instant.now());
        ReadmeQualityResult readme = createReadme(false, 0, false, false, false, false, false, false, false, false);

        RepoHealthResult result = service.score(repo, readme);

        assertTrue(result.getScore() <= 50);
        assertTrue(result.getProblems().stream().anyMatch(p -> p.contains("README")));
    }

    @Test
    void testScore_FullReadme_ShouldBeHighScore() {
        RepositoryInfo repo = createRepo("full-repo", 50000, false, false, Instant.now());
        ReadmeQualityResult readme = createReadme(true, 500, true, true, true, true, true, true, true, true);

        RepoHealthResult result = service.score(repo, readme);

        assertTrue(result.getScore() >= 80);
        assertEquals(HealthLevel.EXCELLENT, result.getLevel());
        assertFalse(result.getStrengths().isEmpty());
    }

    @Test
    void testScore_ArchivedRepo_ShouldBeMaxFair() {
        RepositoryInfo repo = createRepo("archived-repo", 50000, true, false, Instant.now());
        ReadmeQualityResult readme = createReadme(true, 500, true, true, true, true, true, true, true, true);

        RepoHealthResult result = service.score(repo, readme);

        assertTrue(result.getScore() <= 60);
        assertTrue(result.getLevel() == HealthLevel.ARCHIVE_CANDIDATE ||
                   result.getLevel() == HealthLevel.INCOMPLETE ||
                   result.getLevel() == HealthLevel.FAIR);
        assertTrue(result.getProblems().stream().anyMatch(p -> p.contains("archived")));
    }

    @Test
    void testScore_WellDocumentedAndRecent_ShouldBeExcellent() {
        // Full README with all sections, size > 0, recent update, has license
        RepositoryInfo repo = createRepo("excellent-repo", 100000, false, false, Instant.now());
        ReadmeQualityResult readme = createReadme(true, 800, true, true, true, true, true, true, true, true);

        RepoHealthResult result = service.score(repo, readme);

        assertTrue(result.getScore() >= 80);
    }

    @Test
    void testScore_OldRepo_ShouldDeductForNoRecentUpdate() {
        Instant oldDate = Instant.now().minus(365, ChronoUnit.DAYS);
        RepositoryInfo repo = createRepo("old-repo", 50000, false, false, oldDate);
        ReadmeQualityResult readme = createReadme(true, 500, true, true, true, true, true, true, true, true);

        RepoHealthResult result = service.score(repo, readme);

        assertTrue(result.getProblems().stream().anyMatch(p -> p.contains("updated")));
    }

    @Test
    void testScore_MinimalReadme_ShouldGenerateSuggestions() {
        RepositoryInfo repo = createRepo("minimal-repo", 50000, false, false, Instant.now());
        ReadmeQualityResult readme = createReadme(true, 50, false, false, false, false, false, false, false, false);

        RepoHealthResult result = service.score(repo, readme);

        assertFalse(result.getSuggestions().isEmpty());
        assertTrue(result.getSuggestions().size() >= 3);
    }
}