package com.repohealth.controller;

import com.repohealth.common.ApiResponse;
import com.repohealth.github.GitHubApiException;
import com.repohealth.model.RepositoryInfo;
import com.repohealth.service.RepositoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class RepositoryControllerTest {

    private RepositoryService repositoryService;
    private RepositoryController repositoryController;

    @BeforeEach
    void setUp() {
        repositoryService = Mockito.mock(RepositoryService.class);
        repositoryController = new RepositoryController(repositoryService);
    }

    @Test
    void testGetRepositories_ShouldReturnRepositoryList() {
        List<RepositoryInfo> mockRepos = new ArrayList<>();
        RepositoryInfo repo1 = new RepositoryInfo();
        repo1.setName("repo1");
        repo1.setFullName("testuser/repo1");
        repo1.setOwner("testuser");
        repo1.setHtmlUrl("https://github.com/testuser/repo1");
        repo1.setStars(10);
        repo1.setForks(3);
        repo1.setSize(1000);
        repo1.setCreatedAt(Instant.now());
        repo1.setUpdatedAt(Instant.now());
        repo1.setPushedAt(Instant.now());
        mockRepos.add(repo1);

        RepositoryInfo repo2 = new RepositoryInfo();
        repo2.setName("repo2");
        repo2.setFullName("testuser/repo2");
        repo2.setOwner("testuser");
        repo2.setHtmlUrl("https://github.com/testuser/repo2");
        repo2.setStars(5);
        repo2.setForks(1);
        repo2.setSize(500);
        repo2.setCreatedAt(Instant.now());
        repo2.setUpdatedAt(Instant.now());
        repo2.setPushedAt(Instant.now());
        mockRepos.add(repo2);

        Mockito.when(repositoryService.getUserRepositories("testuser")).thenReturn(mockRepos);

        ApiResponse<List<RepositoryInfo>> response = repositoryController.getRepositories("testuser");

        assertTrue(response.isSuccess());
        assertEquals("OK", response.getMessage());
        assertNotNull(response.getData());
        assertEquals(2, response.getData().size());
        assertEquals("repo1", response.getData().get(0).getName());
        assertEquals("repo2", response.getData().get(1).getName());
    }

    @Test
    void testGetRepositories_ShouldReturnEmptyList() {
        Mockito.when(repositoryService.getUserRepositories("emptyuser")).thenReturn(new ArrayList<>());

        ApiResponse<List<RepositoryInfo>> response = repositoryController.getRepositories("emptyuser");

        assertTrue(response.isSuccess());
        assertNotNull(response.getData());
        assertTrue(response.getData().isEmpty());
    }

    @Test
    void testGetRepositories_WhenUserNotFound_ShouldThrowException() {
        Mockito.when(repositoryService.getUserRepositories("nonexistent"))
                .thenThrow(new GitHubApiException("GitHub user not found: nonexistent", 404));

        GitHubApiException exception = assertThrows(GitHubApiException.class, () -> {
            repositoryController.getRepositories("nonexistent");
        });

        assertEquals(404, exception.getStatusCode());
        assertTrue(exception.getMessage().contains("not found"));
    }

    @Test
    void testGetRepositories_WhenRateLimited_ShouldThrowException() {
        Mockito.when(repositoryService.getUserRepositories("testuser"))
                .thenThrow(new GitHubApiException("GitHub API rate limit exceeded. Please try again later.", 403));

        GitHubApiException exception = assertThrows(GitHubApiException.class, () -> {
            repositoryController.getRepositories("testuser");
        });

        assertEquals(403, exception.getStatusCode());
        assertTrue(exception.getMessage().contains("rate limit"));
    }
}