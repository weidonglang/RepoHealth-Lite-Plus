package com.repohealth.service;

import com.repohealth.github.GitHubClient;
import com.repohealth.model.RepositoryInfo;
import com.repohealth.model.TechStackProfile;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TechStackService {

    private final RepositoryService repositoryService;
    private final GitHubClient gitHubClient;
    private final TechStackDetector techStackDetector;

    private static final List<String> COMMON_FILES = Arrays.asList(
            "pom.xml", "build.gradle", "package.json", "vite.config.js",
            "requirements.txt", "pyproject.toml", "Cargo.toml",
            "Dockerfile", "docker-compose.yml"
    );

    public TechStackService(RepositoryService repositoryService,
                            GitHubClient gitHubClient,
                            TechStackDetector techStackDetector) {
        this.repositoryService = repositoryService;
        this.gitHubClient = gitHubClient;
        this.techStackDetector = techStackDetector;
    }

    public List<TechStackProfile> getTechStackProfiles(String username) {
        List<RepositoryInfo> repos = repositoryService.getUserRepositories(username);
        List<TechStackProfile> profiles = new ArrayList<>();

        for (RepositoryInfo repo : repos) {
            try {
                String readmeContent = null;
                try {
                    readmeContent = gitHubClient.getReadmeContent(repo.getOwner(), repo.getName()).orElse(null);
                } catch (Exception e) {
                    // README fetch failure - continue without it
                }

                Map<String, Boolean> fileExistsMap = new HashMap<>();
                for (String filePath : COMMON_FILES) {
                    try {
                        fileExistsMap.put(filePath, gitHubClient.checkFileExists(repo.getOwner(), repo.getName(), filePath));
                    } catch (Exception e) {
                        fileExistsMap.put(filePath, false);
                    }
                }

                TechStackProfile profile = techStackDetector.detect(
                        repo.getName(),
                        repo.getDescription(),
                        repo.getPrimaryLanguage(),
                        readmeContent,
                        fileExistsMap
                );

                profiles.add(profile);
            } catch (Exception e) {
                TechStackProfile fallback = new TechStackProfile();
                fallback.setRepositoryName(repo.getName());
                fallback.setDetectedStacks(new ArrayList<>());
                fallback.setDetectedFiles(new ArrayList<>());
                fallback.setDetectedKeywords(new ArrayList<>());
                profiles.add(fallback);
            }
        }

        return profiles;
    }
}