package com.repohealth.service;

import com.repohealth.model.ActivityLevel;
import com.repohealth.model.RepositoryActivity;
import com.repohealth.model.RepositoryInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class RepositoryActivityAnalyzerTest {

    private RepositoryActivityAnalyzer analyzer;

    @BeforeEach
    void setUp() {
        analyzer = new RepositoryActivityAnalyzer();
    }

    @Test
    void shouldReturnActiveWhenPushedWithin30Days() {
        RepositoryInfo repo = createRepo("active-repo", Instant.now().minus(5, ChronoUnit.DAYS));
        RepositoryActivity activity = analyzer.analyze(repo);
        assertEquals(ActivityLevel.ACTIVE, activity.getActivityLevel());
        assertTrue(activity.getDaysSinceLastPush() >= 4 && activity.getDaysSinceLastPush() <= 6);
    }

    @Test
    void shouldReturnStableWhenPushedWithin90Days() {
        RepositoryInfo repo = createRepo("stable-repo", Instant.now().minus(60, ChronoUnit.DAYS));
        RepositoryActivity activity = analyzer.analyze(repo);
        assertEquals(ActivityLevel.STABLE, activity.getActivityLevel());
    }

    @Test
    void shouldReturnStaleWhenPushedWithin180Days() {
        RepositoryInfo repo = createRepo("stale-repo", Instant.now().minus(120, ChronoUnit.DAYS));
        RepositoryActivity activity = analyzer.analyze(repo);
        assertEquals(ActivityLevel.STALE, activity.getActivityLevel());
    }

    @Test
    void shouldReturnInactiveWhenPushedOver180DaysAgo() {
        RepositoryInfo repo = createRepo("inactive-repo", Instant.now().minus(365, ChronoUnit.DAYS));
        RepositoryActivity activity = analyzer.analyze(repo);
        assertEquals(ActivityLevel.INACTIVE, activity.getActivityLevel());
    }

    @Test
    void shouldReturnUnknownWhenPushedAtIsNull() {
        RepositoryInfo repo = new RepositoryInfo(
                "unknown-repo", "user/unknown-repo", "user", null,
                "https://github.com/user/unknown-repo", null,
                0, 0, 0, 0, "main",
                false, false, false, 0,
                Instant.now(), Instant.now(), null, Collections.emptyList()
        );
        RepositoryActivity activity = analyzer.analyze(repo);
        assertEquals(ActivityLevel.UNKNOWN, activity.getActivityLevel());
        assertEquals(-1, activity.getDaysSinceLastPush());
    }

    @Test
    void shouldHaveActivityNotes() {
        RepositoryInfo repo = createRepo("active-repo", Instant.now().minus(5, ChronoUnit.DAYS));
        RepositoryActivity activity = analyzer.analyze(repo);
        assertNotNull(activity.getActivityNotes());
        assertFalse(activity.getActivityNotes().isEmpty());
    }

    private RepositoryInfo createRepo(String name, Instant pushedAt) {
        return new RepositoryInfo(
                name, "user/" + name, "user", "A test repo",
                "https://github.com/user/" + name, "Java",
                0, 0, 0, 100, "main",
                false, false, false, 0,
                Instant.now().minus(365, ChronoUnit.DAYS),
                Instant.now().minus(10, ChronoUnit.DAYS),
                pushedAt, Collections.emptyList()
        );
    }
}