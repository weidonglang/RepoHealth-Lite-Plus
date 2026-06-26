package com.repohealth.github;

import com.repohealth.model.RepositoryInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GitHubClientTest {

    private RestTemplate restTemplate;
    private GitHubClient gitHubClient;

    @BeforeEach
    void setUp() {
        restTemplate = mock(RestTemplate.class);
        gitHubClient = new GitHubClient(restTemplate);
    }

    // ---- Task 3: getUserRepositories ----

    @Test
    void testGetUserRepositories_ShouldReturnRepos() {
        GitHubRepositoryDto[] mockRepos = new GitHubRepositoryDto[2];
        mockRepos[0] = createMockDto("repo1", "testuser/repo1", "testuser", "First repo", 10, 3, 5, 1000, "Java");
        mockRepos[1] = createMockDto("repo2", "testuser/repo2", "testuser", "Second repo", 5, 1, 2, 500, "Python");

        String url = "https://api.github.com/users/testuser/repos?per_page=100&page=1";
        when(restTemplate.getForObject(url, GitHubRepositoryDto[].class)).thenReturn(mockRepos);

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
    void testGetUserRepositories_WhenUserNotFound_ShouldThrow() {
        String url = "https://api.github.com/users/nonexistent/repos?per_page=100&page=1";
        when(restTemplate.getForObject(url, GitHubRepositoryDto[].class))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        GitHubApiException exception = assertThrows(GitHubApiException.class, () -> {
            gitHubClient.getUserRepositories("nonexistent");
        });
        assertTrue(exception.getMessage().contains("not found"));
        assertEquals(404, exception.getStatusCode());
    }

    @Test
    void testGetUserRepositories_WhenRateLimited_ShouldThrow() {
        String url = "https://api.github.com/users/testuser/repos?per_page=100&page=1";
        when(restTemplate.getForObject(url, GitHubRepositoryDto[].class))
                .thenThrow(new HttpClientErrorException(HttpStatus.FORBIDDEN));

        GitHubApiException exception = assertThrows(GitHubApiException.class, () -> {
            gitHubClient.getUserRepositories("testuser");
        });
        assertTrue(exception.getMessage().contains("rate limit"));
        assertEquals(403, exception.getStatusCode());
    }

    // ---- Task 4: getReadmeContent ----

    @Test
    void testGetReadmeContent_ShouldReturnDecodedContent() {
        String content = "# README\nThis is a test readme.";
        String encoded = Base64.getEncoder().encodeToString(content.getBytes(StandardCharsets.UTF_8));

        GitHubClient.GitHubContentDto dto = new GitHubClient.GitHubContentDto();
        dto.setName("README.md");
        dto.setPath("README.md");
        dto.setContent(encoded);
        dto.setEncoding("base64");

        String url1 = "https://api.github.com/repos/testuser/repo1/contents/README.md";
        when(restTemplate.getForObject(url1, GitHubClient.GitHubContentDto.class)).thenReturn(dto);

        Optional<String> result = gitHubClient.getReadmeContent("testuser", "repo1");

        assertTrue(result.isPresent());
        assertEquals(content, result.get());
    }

    @Test
    void testGetReadmeContent_WhenNotFound_ShouldReturnEmpty() {
        String url1 = "https://api.github.com/repos/testuser/repo1/contents/README.md";
        when(restTemplate.getForObject(url1, GitHubClient.GitHubContentDto.class))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        String url2 = "https://api.github.com/repos/testuser/repo1/contents/README.MD";
        when(restTemplate.getForObject(url2, GitHubClient.GitHubContentDto.class))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        String url3 = "https://api.github.com/repos/testuser/repo1/contents/readme.md";
        when(restTemplate.getForObject(url3, GitHubClient.GitHubContentDto.class))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        String url4 = "https://api.github.com/repos/testuser/repo1/contents/README";
        when(restTemplate.getForObject(url4, GitHubClient.GitHubContentDto.class))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        Optional<String> result = gitHubClient.getReadmeContent("testuser", "repo1");

        assertFalse(result.isPresent());
    }

    @Test
    void testGetReadmeContent_ShouldTryNextPathIfNotFound() {
        // README.md 不存在
        String url1 = "https://api.github.com/repos/testuser/repo1/contents/README.md";
        when(restTemplate.getForObject(url1, GitHubClient.GitHubContentDto.class))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        // readme.md 存在
        String content = "# Readme via readme.md";
        String encoded = Base64.getEncoder().encodeToString(content.getBytes(StandardCharsets.UTF_8));
        GitHubClient.GitHubContentDto dto = new GitHubClient.GitHubContentDto();
        dto.setName("readme.md");
        dto.setPath("readme.md");
        dto.setContent(encoded);
        dto.setEncoding("base64");

        String url3 = "https://api.github.com/repos/testuser/repo1/contents/readme.md";
        when(restTemplate.getForObject(url3, GitHubClient.GitHubContentDto.class)).thenReturn(dto);

        Optional<String> result = gitHubClient.getReadmeContent("testuser", "repo1");

        assertTrue(result.isPresent());
        assertEquals(content, result.get());
    }

    // ---- Task 8: getRepositoryLanguages ----

    @Test
    void testGetRepositoryLanguages_ShouldReturnLanguageMap() {
        String url = "https://api.github.com/repos/testuser/repo1/languages";
        Map<String, Long> mockLanguages = Map.of("Java", 1000L, "HTML", 500L);
        when(restTemplate.getForObject(url, Map.class)).thenReturn(mockLanguages);

        Map<String, Long> result = gitHubClient.getRepositoryLanguages("testuser", "repo1");

        assertEquals(2, result.size());
        assertEquals(1000L, result.get("Java"));
        assertEquals(500L, result.get("HTML"));
    }

    @Test
    void testGetRepositoryLanguages_WhenNotFound_ShouldReturnEmpty() {
        String url = "https://api.github.com/repos/testuser/empty-repo/languages";
        when(restTemplate.getForObject(url, Map.class))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        Map<String, Long> result = gitHubClient.getRepositoryLanguages("testuser", "empty-repo");

        assertTrue(result.isEmpty());
    }

    // ---- Task 11: checkFileExists ----

    @Test
    void testCheckFileExists_WhenFileExists_ShouldReturnTrue() {
        String url = "https://api.github.com/repos/testuser/repo1/contents/pom.xml";
        when(restTemplate.getForObject(url, Object.class)).thenReturn(new Object());

        boolean exists = gitHubClient.checkFileExists("testuser", "repo1", "pom.xml");

        assertTrue(exists);
    }

    @Test
    void testCheckFileExists_WhenFileNotFound_ShouldReturnFalse() {
        String url = "https://api.github.com/repos/testuser/repo1/contents/pom.xml";
        when(restTemplate.getForObject(url, Object.class))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        boolean exists = gitHubClient.checkFileExists("testuser", "repo1", "pom.xml");

        assertFalse(exists);
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