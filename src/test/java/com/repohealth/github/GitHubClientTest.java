package com.repohealth.github;

import com.repohealth.model.RepositoryInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GitHubClientTest {

    private RestTemplate restTemplate;
    private GitHubProperties gitHubProperties;
    private GitHubClient gitHubClient;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() {
        restTemplate = mock(RestTemplate.class);
        gitHubProperties = new GitHubProperties();
        gitHubProperties.setToken("");
        gitHubClient = new GitHubClient(restTemplate, gitHubProperties);
    }

    // ---- Task 3: getUserRepositories ----

    @Test
    @SuppressWarnings("unchecked")
    void testGetUserRepositories_ShouldReturnRepos() {
        GitHubRepositoryDto[] mockRepos = new GitHubRepositoryDto[2];
        mockRepos[0] = createMockDto("repo1", "testuser/repo1", "testuser", "First repo", 10, 3, 5, 1000, "Java");
        mockRepos[1] = createMockDto("repo2", "testuser/repo2", "testuser", "Second repo", 5, 1, 2, 500, "Python");

        ResponseEntity<GitHubRepositoryDto[]> response = ResponseEntity.ok(mockRepos);
        ResponseEntity<GitHubRepositoryDto[]> emptyResponse = ResponseEntity.ok(new GitHubRepositoryDto[0]);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(GitHubRepositoryDto[].class)))
                .thenReturn(response)
                .thenReturn(emptyResponse);

        List<RepositoryInfo> repos = gitHubClient.getUserRepositories("testuser");

        assertEquals(2, repos.size());
        assertEquals("repo1", repos.get(0).getName());
        assertEquals("testuser/repo1", repos.get(0).getFullName());
        assertEquals("testuser", repos.get(0).getOwner());
        assertEquals("Java", repos.get(0).getPrimaryLanguage());
        assertEquals(10, repos.get(0).getStars());
        assertEquals(3, repos.get(0).getForks());
    }

    @Test
    @SuppressWarnings("unchecked")
    void testGetUserRepositories_WhenUserNotFound_ShouldThrow() {
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(GitHubRepositoryDto[].class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        GitHubApiException exception = assertThrows(GitHubApiException.class, () -> {
            gitHubClient.getUserRepositories("nonexistent");
        });
        assertTrue(exception.getMessage().contains("not found"));
        assertEquals(404, exception.getStatusCode());
    }

    @Test
    @SuppressWarnings("unchecked")
    void testGetUserRepositories_WhenRateLimited_ShouldThrow() {
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(GitHubRepositoryDto[].class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.FORBIDDEN));

        GitHubApiException exception = assertThrows(GitHubApiException.class, () -> {
            gitHubClient.getUserRepositories("testuser");
        });
        assertTrue(exception.getMessage().contains("rate limit"));
        assertEquals(403, exception.getStatusCode());
    }

    // ---- Task 4: getReadmeContent ----

    @Test
    @SuppressWarnings("unchecked")
    void testGetReadmeContent_ShouldReturnDecodedContent() {
        String content = "# README\nThis is a test readme.";
        String encoded = Base64.getEncoder().encodeToString(content.getBytes(StandardCharsets.UTF_8));

        GitHubClient.GitHubContentDto dto = new GitHubClient.GitHubContentDto();
        dto.setName("README.md");
        dto.setPath("README.md");
        dto.setContent(encoded);
        dto.setEncoding("base64");

        ResponseEntity<GitHubClient.GitHubContentDto> response = ResponseEntity.ok(dto);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(GitHubClient.GitHubContentDto.class)))
                .thenReturn(response);

        Optional<String> result = gitHubClient.getReadmeContent("testuser", "repo1");

        assertTrue(result.isPresent());
        assertEquals(content, result.get());
    }

    @Test
    @SuppressWarnings("unchecked")
    void testGetReadmeContent_WhenNotFound_ShouldReturnEmpty() {
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(GitHubClient.GitHubContentDto.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        Optional<String> result = gitHubClient.getReadmeContent("testuser", "repo1");

        assertFalse(result.isPresent());
    }

    @Test
    @SuppressWarnings("unchecked")
    void testGetReadmeContent_ShouldTryNextPathIfNotFound() {
        // All attempts return 404 except the second one succeeds
        String content = "# Readme via readme.md";
        String encoded = Base64.getEncoder().encodeToString(content.getBytes(StandardCharsets.UTF_8));
        GitHubClient.GitHubContentDto dto = new GitHubClient.GitHubContentDto();
        dto.setName("readme.md");
        dto.setPath("readme.md");
        dto.setContent(encoded);
        dto.setEncoding("base64");

        // Use a counter to alternate behavior: first 3 calls throw, 4th returns the dto
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(GitHubClient.GitHubContentDto.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND))
                .thenReturn(ResponseEntity.ok(dto));

        Optional<String> result = gitHubClient.getReadmeContent("testuser", "repo1");

        assertTrue(result.isPresent());
        assertEquals(content, result.get());
    }

    // ---- Task 8: getRepositoryLanguages ----

    @Test
    @SuppressWarnings("unchecked")
    void testGetRepositoryLanguages_ShouldReturnLanguageMap() {
        Map<String, Long> mockLanguages = Map.of("Java", 1000L, "HTML", 500L);
        ResponseEntity<Map> response = ResponseEntity.ok(mockLanguages);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(Map.class)))
                .thenReturn(response);

        Map<String, Long> result = gitHubClient.getRepositoryLanguages("testuser", "repo1");

        assertEquals(2, result.size());
        assertEquals(1000L, result.get("Java"));
        assertEquals(500L, result.get("HTML"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void testGetRepositoryLanguages_WhenNotFound_ShouldReturnEmpty() {
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(Map.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        Map<String, Long> result = gitHubClient.getRepositoryLanguages("testuser", "empty-repo");

        assertTrue(result.isEmpty());
    }

    // ---- Task 11: checkFileExists ----

    @Test
    @SuppressWarnings("unchecked")
    void testCheckFileExists_WhenFileExists_ShouldReturnTrue() {
        ResponseEntity<Object> response = ResponseEntity.ok(new Object());
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(response);

        boolean exists = gitHubClient.checkFileExists("testuser", "repo1", "pom.xml");

        assertTrue(exists);
    }

    @Test
    @SuppressWarnings("unchecked")
    void testCheckFileExists_WhenFileNotFound_ShouldReturnFalse() {
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        boolean exists = gitHubClient.checkFileExists("testuser", "repo1", "pom.xml");

        assertFalse(exists);
    }

    // ---- Test Token Authentication ----

    @Test
    @SuppressWarnings("unchecked")
    void testGetUserRepositories_WithToken_ShouldPassAuthHeader() {
        gitHubProperties.setToken("test-token-123");
        gitHubClient = new GitHubClient(restTemplate, gitHubProperties);

        GitHubRepositoryDto[] mockRepos = new GitHubRepositoryDto[1];
        mockRepos[0] = createMockDto("repo1", "testuser/repo1", "testuser", "First repo", 10, 3, 5, 1000, "Java");

        ResponseEntity<GitHubRepositoryDto[]> response = ResponseEntity.ok(mockRepos);
        ResponseEntity<GitHubRepositoryDto[]> emptyResponse = ResponseEntity.ok(new GitHubRepositoryDto[0]);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(GitHubRepositoryDto[].class)))
                .thenReturn(response)
                .thenReturn(emptyResponse);

        List<RepositoryInfo> repos = gitHubClient.getUserRepositories("testuser");

        assertEquals(1, repos.size());
        assertEquals("repo1", repos.get(0).getName());
    }

    // ---- Helper ----

    private GitHubRepositoryDto createMockDto(String name, String fullName, String ownerLogin,
                                               String description, int stars, int forks,
                                               int watchers, int size, String language) {
        GitHubRepositoryDto dto = new GitHubRepositoryDto();
        dto.setName(name);
        dto.setFullName(fullName);
        GitHubRepositoryDto.Owner owner = new GitHubRepositoryDto.Owner();
        owner.setLogin(ownerLogin);
        dto.setOwner(owner);
        dto.setDescription(description);
        dto.setHtmlUrl("https://github.com/" + fullName);
        dto.setLanguage(language);
        dto.setStargazersCount(stars);
        dto.setForksCount(forks);
        dto.setWatchersCount(watchers);
        dto.setSize(size);
        dto.setDefaultBranch("main");
        dto.setArchived(false);
        dto.setFork(false);
        dto.setDisabled(false);
        dto.setOpenIssuesCount(0);
        dto.setTopics(List.of());
        return dto;
    }
}