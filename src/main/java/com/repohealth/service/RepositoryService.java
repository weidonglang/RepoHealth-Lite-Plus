package com.repohealth.service;

import com.repohealth.github.GitHubClient;
import com.repohealth.model.RepositoryInfo;
import com.repohealth.util.CacheService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RepositoryService {

    private final GitHubClient gitHubClient;
    private final CacheService cacheService;

    public RepositoryService(GitHubClient gitHubClient, CacheService cacheService) {
        this.gitHubClient = gitHubClient;
        this.cacheService = cacheService;
    }

    /**
     * 获取指定用户的公开仓库列表，结果缓存 10 分钟。
     */
    public List<RepositoryInfo> getUserRepositories(String username) {
        String cacheKey = "repos:" + username;
        return cacheService.get(cacheKey, () -> gitHubClient.getUserRepositories(username));
    }
}
