package com.repohealth.service;

import com.repohealth.model.RepositoryInfo;
import com.repohealth.model.TechStackProfile;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class TechStackDetector {

    // File -> Tech stack mappings
    private static final Map<String, List<String>> FILE_STACK_MAP = new LinkedHashMap<>();
    static {
        FILE_STACK_MAP.put("pom.xml", Arrays.asList("Java", "Maven"));
        FILE_STACK_MAP.put("build.gradle", Arrays.asList("Java", "Gradle"));
        FILE_STACK_MAP.put("package.json", Arrays.asList("Node.js"));
        FILE_STACK_MAP.put("vite.config.js", Arrays.asList("Vite"));
        FILE_STACK_MAP.put("requirements.txt", Arrays.asList("Python"));
        FILE_STACK_MAP.put("pyproject.toml", Arrays.asList("Python"));
        FILE_STACK_MAP.put("Cargo.toml", Arrays.asList("Rust"));
        FILE_STACK_MAP.put("Dockerfile", Arrays.asList("Docker"));
        FILE_STACK_MAP.put("docker-compose.yml", Arrays.asList("Docker Compose"));
    }

    // README keyword -> Tech stack mappings
    private static final Map<String, String> KEYWORD_STACK_MAP = new LinkedHashMap<>();
    static {
        KEYWORD_STACK_MAP.put("Spring Boot", "Spring Boot");
        KEYWORD_STACK_MAP.put("Spring Cloud", "Spring Cloud");
        KEYWORD_STACK_MAP.put("Vue", "Vue");
        KEYWORD_STACK_MAP.put("React", "React");
        KEYWORD_STACK_MAP.put("Redis", "Redis");
        KEYWORD_STACK_MAP.put("MySQL", "MySQL");
        KEYWORD_STACK_MAP.put("PostgreSQL", "PostgreSQL");
        KEYWORD_STACK_MAP.put("Elasticsearch", "Elasticsearch");
        KEYWORD_STACK_MAP.put("RabbitMQ", "RabbitMQ");
        KEYWORD_STACK_MAP.put("Ollama", "Ollama");
        KEYWORD_STACK_MAP.put("RAG", "RAG");
        KEYWORD_STACK_MAP.put("FastAPI", "FastAPI");
        KEYWORD_STACK_MAP.put("Tauri", "Tauri");
        KEYWORD_STACK_MAP.put("Rust", "Rust");
    }

    // Language -> Tech stack mappings
    private static final Map<String, String> LANGUAGE_STACK_MAP = new LinkedHashMap<>();
    static {
        LANGUAGE_STACK_MAP.put("Java", "Java");
        LANGUAGE_STACK_MAP.put("Python", "Python");
        LANGUAGE_STACK_MAP.put("Rust", "Rust");
        LANGUAGE_STACK_MAP.put("TypeScript", "TypeScript");
        LANGUAGE_STACK_MAP.put("JavaScript", "JavaScript");
        LANGUAGE_STACK_MAP.put("HTML", "HTML");
        LANGUAGE_STACK_MAP.put("CSS", "CSS");
        LANGUAGE_STACK_MAP.put("C", "C");
        LANGUAGE_STACK_MAP.put("C++", "C++");
    }

    public TechStackProfile detect(String repositoryName,
                                    String description,
                                    String primaryLanguage,
                                    String readmeContent,
                                    Map<String, Boolean> fileExistsMap) {
        Set<String> detectedStacks = new LinkedHashSet<>();
        Set<String> detectedFiles = new LinkedHashSet<>();
        Set<String> detectedKeywords = new LinkedHashSet<>();

        // 1. Check files
        for (Map.Entry<String, List<String>> entry : FILE_STACK_MAP.entrySet()) {
            Boolean exists = fileExistsMap.get(entry.getKey());
            if (Boolean.TRUE.equals(exists)) {
                detectedFiles.add(entry.getKey());
                detectedStacks.addAll(entry.getValue());
            }
        }

        // 2. Check README keywords
        if (readmeContent != null && !readmeContent.isEmpty()) {
            String lowerReadme = readmeContent.toLowerCase();
            for (Map.Entry<String, String> entry : KEYWORD_STACK_MAP.entrySet()) {
                if (lowerReadme.contains(entry.getKey().toLowerCase())) {
                    detectedKeywords.add(entry.getKey());
                    detectedStacks.add(entry.getValue());
                }
            }

            // Also check description for keywords
            if (description != null) {
                String lowerDesc = description.toLowerCase();
                for (Map.Entry<String, String> entry : KEYWORD_STACK_MAP.entrySet()) {
                    if (lowerDesc.contains(entry.getKey().toLowerCase())) {
                        detectedKeywords.add(entry.getKey());
                        detectedStacks.add(entry.getValue());
                    }
                }
            }
        }

        // 3. Check primary language
        if (primaryLanguage != null && !primaryLanguage.isEmpty()) {
            String stack = LANGUAGE_STACK_MAP.get(primaryLanguage);
            if (stack != null) {
                detectedStacks.add(stack);
            }
        }

        TechStackProfile profile = new TechStackProfile();
        profile.setRepositoryName(repositoryName);
        profile.setDetectedStacks(new ArrayList<>(detectedStacks));
        profile.setDetectedFiles(new ArrayList<>(detectedFiles));
        profile.setDetectedKeywords(new ArrayList<>(detectedKeywords));

        return profile;
    }
}