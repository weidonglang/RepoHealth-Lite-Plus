package com.repohealth.github;

import com.repohealth.model.RepositoryInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class GitHubClient {

    private static final Logger log = LoggerFactory.getLogger(GitHubClient.class);
    private static final String GITHUB_API_BASE = "https://api.github.com";
    private static final int PER_PAGE = 100;
    private static final int MAX_PAGES = 10;

    private final RestTemplate restTemplate;
    private final GitHubProperties gitHubProperties;

    public GitHubClient(RestTemplate restTemplate, GitHubProperties gitHubProperties) {
        this.restTemplate = restTemplate;
        this.gitHubProperties = gitHubProperties;
    }

    /**
     * 获取指定用户的公开仓库列表，支持分页，最多 10 页。
     */
    public List<RepositoryInfo> getUserRepositories(String username) {
        List<RepositoryInfo> allRepos = new ArrayList<>();

        for (int page = 1; page <= MAX_PAGES; page++) {
            String url = UriComponentsBuilder.fromHttpUrl(GITHUB_API_BASE + "/users/{username}/repos")
                    .queryParam("per_page", PER_PAGE)
                    .queryParam("page", page)
                    .buildAndExpand(username)
                    .toUriString();

            try {
                HttpEntity<Void> entity = createAuthEntity();
                GitHubRepositoryDto[] repos = restTemplate.exchange(url, HttpMethod.GET, entity, GitHubRepositoryDto[].class).getBody();
                if (repos == null || repos.length == 0) {
                    break;
                }
                for (GitHubRepositoryDto dto : repos) {
                    allRepos.add(mapToRepositoryInfo(dto));
                }
            } catch (HttpClientErrorException e) {
                if (e.getStatusCode().value() == 404) {
                    throw new GitHubApiException("GitHub user not found: " + username, 404);
                } else if (e.getStatusCode().value() == 403) {
                    throw new GitHubApiException("GitHub API rate limit exceeded. Please try again later.", 403);
                } else {
                    throw new GitHubApiException("GitHub API request failed: " + e.getMessage(), e.getStatusCode().value());
                }
            } catch (Exception e) {
                throw new GitHubApiException("Failed to fetch repositories: " + e.getMessage(), 0);
            }
        }

        return allRepos;
    }

    /**
     * 获取仓库 README 内容（base64 解码）。
     * 按 README.md, README.MD, readme.md, README 顺序尝试。
     */
    public Optional<String> getReadmeContent(String owner, String repoName) {
        List<String> paths = Arrays.asList("README.md", "README.MD", "readme.md", "README");

        for (String path : paths) {
            String url = GITHUB_API_BASE + "/repos/{owner}/{repo}/contents/{path}";
            String resolvedUrl = UriComponentsBuilder.fromHttpUrl(url)
                    .buildAndExpand(owner, repoName, path)
                    .toUriString();

            try {
                HttpEntity<Void> entity = createAuthEntity();
                GitHubContentDto contentDto = restTemplate.exchange(resolvedUrl, HttpMethod.GET, entity, GitHubContentDto.class).getBody();
                if (contentDto != null && contentDto.getContent() != null) {
                    String decoded = new String(java.util.Base64.getDecoder().decode(contentDto.getContent().replaceAll("\\s", "")));
                    return Optional.of(decoded);
                }
            } catch (HttpClientErrorException e) {
                if (e.getStatusCode().value() == 404) {
                    continue;
                }
            } catch (Exception e) {
                return Optional.empty();
            }
        }

        return Optional.empty();
    }

    /**
     * 获取仓库语言统计。
     */
    @SuppressWarnings("unchecked")
    public Map<String, Long> getRepositoryLanguages(String owner, String repoName) {
        String url = GITHUB_API_BASE + "/repos/{owner}/{repo}/languages";
        String resolvedUrl = UriComponentsBuilder.fromHttpUrl(url)
                .buildAndExpand(owner, repoName)
                .toUriString();

        try {
            HttpEntity<Void> entity = createAuthEntity();
            return restTemplate.exchange(resolvedUrl, HttpMethod.GET, entity, Map.class).getBody();
        } catch (Exception e) {
            return Collections.emptyMap();
        }
    }

    /**
     * 检查仓库是否存在某个文件。
     */
    public boolean checkFileExists(String owner, String repoName, String filePath) {
        String url = GITHUB_API_BASE + "/repos/{owner}/{repo}/contents/{path}";
        String resolvedUrl = UriComponentsBuilder.fromHttpUrl(url)
                .buildAndExpand(owner, repoName, filePath)
                .toUriString();

        try {
            HttpEntity<Void> entity = createAuthEntity();
            restTemplate.exchange(resolvedUrl, HttpMethod.GET, entity, Object.class);
            return true;
        } catch (HttpClientErrorException e) {
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 创建带有 Authorization header 的 HttpEntity（如果配置了 Token）。
     */
    private HttpEntity<Void> createAuthEntity() {
        HttpHeaders headers = new HttpHeaders();
        String token = gitHubProperties.getToken();
        if (token != null && !token.isBlank()) {
            headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + token);
            log.debug("Using GitHub Token for authentication");
        }
        return new HttpEntity<>(headers);
    }

    private RepositoryInfo mapToRepositoryInfo(GitHubRepositoryDto dto) {
        RepositoryInfo info = new RepositoryInfo();
        info.setName(dto.getName());
        info.setFullName(dto.getFullName());
        info.setOwner(dto.getOwnerLogin());
        info.setDescription(dto.getDescription());
        info.setHtmlUrl(dto.getHtmlUrl());
        info.setPrimaryLanguage(dto.getLanguage());
        info.setStars(dto.getStargazersCount());
        info.setForks(dto.getForksCount());
        info.setWatchers(dto.getWatchersCount());
        info.setSize(dto.getSize());
        info.setDefaultBranch(dto.getDefaultBranch());
        info.setArchived(dto.isArchived());
        info.setFork(dto.isFork());
        info.setDisabled(dto.isDisabled());
        info.setOpenIssuesCount(dto.getOpenIssuesCount());
        info.setCreatedAt(dto.getCreatedAt() != null ? dto.getCreatedAt() : Instant.now());
        info.setUpdatedAt(dto.getUpdatedAt() != null ? dto.getUpdatedAt() : Instant.now());
        info.setPushedAt(dto.getPushedAt() != null ? dto.getPushedAt() : Instant.now());
        info.setTopics(dto.getTopics() != null ? dto.getTopics() : new ArrayList<>());
        return info;
    }

    public static class GitHubContentDto {
        private String name;
        private String path;
        private String content;
        private String encoding;

        public GitHubContentDto() {
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getEncoding() {
            return encoding;
        }

        public void setEncoding(String encoding) {
            this.encoding = encoding;
        }
    }
}