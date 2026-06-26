package com.repohealth.service;

import com.repohealth.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ResumeScoreServiceTest {

    private ResumeScoreService service;

    @BeforeEach
    void setUp() {
        service = new ResumeScoreService();
    }

    private RepositoryInfo createRepo(String name, String description) {
        RepositoryInfo repo = new RepositoryInfo();
        repo.setName(name);
        repo.setDescription(description);
        repo.setHtmlUrl("https://github.com/test/" + name);
        return repo;
    }

    private RepoHealthResult createHealth(int score, boolean hasTest, boolean hasScreenshots) {
        RepoHealthResult health = new RepoHealthResult();
        health.setScore(score);
        ReadmeQualityResult readmeQuality = new ReadmeQualityResult();
        readmeQuality.setHasTestInstructions(hasTest);
        readmeQuality.setHasScreenshots(hasScreenshots);
        health.setReadmeQuality(readmeQuality);
        return health;
    }

    private TechStackProfile createTechStack(String... stacks) {
        TechStackProfile profile = new TechStackProfile();
        profile.setDetectedStacks(Arrays.asList(stacks));
        profile.setDetectedFiles(List.of());
        profile.setDetectedKeywords(List.of());
        return profile;
    }

    @Test
    void testAIRagProject_WithSpringBoot_ShouldBePrimary() {
        RepositoryInfo repo = createRepo("ai-search-engine", "RAG based search engine with vector embeddings");
        RepoHealthResult health = createHealth(80, true, true);
        TechStackProfile techStack = createTechStack("Java", "Spring Boot", "Elasticsearch");

        ResumeShowcaseResult result = service.score(repo, health, techStack);

        // Java/Spring Boot (+15) + AI/RAG (+15) + health>=75 (+10) + test (+10) + screenshots (+8) + prof name (+5) = 63
        assertTrue(result.getScore() >= 60, "AI/RAG + Spring Boot project should have good score");
        assertEquals(ShowcaseLevel.SECONDARY_SHOWCASE, result.getLevel());
        assertTrue(result.getResumeKeywords().contains("AI"));
    }

    @Test
    void testUAVProject_HighScore() {
        RepositoryInfo repo = createRepo("uav-route-planner", "Path planning for low altitude UAV");
        RepoHealthResult health = createHealth(75, true, false);
        TechStackProfile techStack = createTechStack("Python", "Java");

        ResumeShowcaseResult result = service.score(repo, health, techStack);

        // UAV (+20) + Java (+15) + health>=75 (+10) + test (+10) + prof name (+5) = 60
        assertTrue(result.getScore() >= 60, "UAV project should have good score");
        assertEquals(ShowcaseLevel.SECONDARY_SHOWCASE, result.getLevel());
    }

    @Test
    void testEmptyRepo_LowScore() {
        RepositoryInfo repo = createRepo("empty-repo", null);
        RepoHealthResult health = createHealth(0, false, false);
        TechStackProfile techStack = createTechStack();

        ResumeShowcaseResult result = service.score(repo, health, techStack);

        // Prof name (+5) = 5
        assertTrue(result.getScore() < 40, "Empty repo should have low score");
        assertTrue(result.getScore() < 20, "Empty repo should be archive candidate");
        assertEquals(ShowcaseLevel.ARCHIVE_CANDIDATE, result.getLevel());
    }

    @Test
    void testCrudProject_NotTooHigh() {
        RepositoryInfo repo = createRepo("employee-management-system", "Employee Management CRUD app");
        RepoHealthResult health = createHealth(80, true, true);
        TechStackProfile techStack = createTechStack("Java", "Spring Boot");

        ResumeShowcaseResult result = service.score(repo, health, techStack);

        // Java/Spring Boot (+15) + health>=75 (+10) + test (+10) + screenshots (+8) + prof name (+5) - CRUD(-7) = 41
        assertTrue(result.getScore() < 60, "CRUD project should not be too high");
        assertEquals(ShowcaseLevel.KEEP, result.getLevel());
    }

    @Test
    void testSpringBootProject_GoodScore() {
        RepositoryInfo repo = createRepo("order-service", "Microservice for order processing");
        RepoHealthResult health = createHealth(85, true, true);
        TechStackProfile techStack = createTechStack("Java", "Spring Boot");

        ResumeShowcaseResult result = service.score(repo, health, techStack);

        // Java/Spring Boot (+15) + health>=75 (+10) + test (+10) + screenshots (+8) + prof name (+5) = 48
        assertTrue(result.getScore() >= 40, "Spring Boot project with good health should have decent score");
        assertTrue(result.getResumeKeywords().contains("Java"));
        assertTrue(result.getResumeKeywords().contains("Spring Boot"));
    }

    @Test
    void testDeveloperToolProject() {
        RepositoryInfo repo = createRepo("cli-analyzer", "A CLI tool for code analysis");
        RepoHealthResult health = createHealth(70, false, false);
        TechStackProfile techStack = createTechStack("Rust");

        ResumeShowcaseResult result = service.score(repo, health, techStack);

        // Developer tool (+10) + health>=75 (false, 70 < 75) + prof name (+5) = 15
        assertTrue(result.getScore() >= 10, "Developer tool should get some points");
    }
}