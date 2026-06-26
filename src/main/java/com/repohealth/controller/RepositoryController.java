package com.repohealth.controller;

import com.repohealth.common.ApiResponse;
import com.repohealth.model.RepositoryInfo;
import com.repohealth.service.RepositoryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class RepositoryController {

    private final RepositoryService repositoryService;

    public RepositoryController(RepositoryService repositoryService) {
        this.repositoryService = repositoryService;
    }

    /**
     * 获取指定用户的公开仓库列表。
     */
    @GetMapping("/repos/{username}")
    public ApiResponse<List<RepositoryInfo>> getRepositories(@PathVariable String username) {
        List<RepositoryInfo> repos = repositoryService.getUserRepositories(username);
        return ApiResponse.ok(repos);
    }
}