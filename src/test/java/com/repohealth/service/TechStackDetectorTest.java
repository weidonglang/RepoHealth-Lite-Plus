package com.repohealth.service;

import com.repohealth.model.TechStackProfile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class TechStackDetectorTest {

    private TechStackDetector detector;

    @BeforeEach
    void setUp() {
        detector = new TechStackDetector();
    }

    @Test
    void testDetect_PomXml_JavaMaven() {
        Map<String, Boolean> fileExists = new HashMap<>();
        fileExists.put("pom.xml", true);

        TechStackProfile profile = detector.detect(
                "my-project",
                "A Java project",
                "Java",
                null,
                fileExists
        );

        assertTrue(profile.getDetectedStacks().contains("Java"));
        assertTrue(profile.getDetectedStacks().contains("Maven"));
        assertTrue(profile.getDetectedFiles().contains("pom.xml"));
    }

    @Test
    void testDetect_CargoToml_Rust() {
        Map<String, Boolean> fileExists = new HashMap<>();
        fileExists.put("Cargo.toml", true);

        TechStackProfile profile = detector.detect(
                "rust-project",
                "A Rust project",
                "Rust",
                null,
                fileExists
        );

        assertTrue(profile.getDetectedStacks().contains("Rust"));
        assertTrue(profile.getDetectedFiles().contains("Cargo.toml"));
    }

    @Test
    void testDetect_ReadmeKeywords_SpringBoot() {
        Map<String, Boolean> fileExists = new HashMap<>();

        TechStackProfile profile = detector.detect(
                "spring-app",
                "A Spring Boot REST API",
                "Java",
                "This project uses Spring Boot and Redis for caching.",
                fileExists
        );

        assertTrue(profile.getDetectedStacks().contains("Spring Boot"));
        assertTrue(profile.getDetectedKeywords().contains("Spring Boot"));
    }

    @Test
    void testDetect_NoReadmeFiles() {
        Map<String, Boolean> fileExists = new HashMap<>();
        fileExists.put("pom.xml", false);
        fileExists.put("package.json", false);

        TechStackProfile profile = detector.detect(
                "empty-project",
                "Nothing",
                "Unknown",
                null,
                fileExists
        );

        assertTrue(profile.getDetectedStacks().isEmpty());
        assertTrue(profile.getDetectedFiles().isEmpty());
        assertTrue(profile.getDetectedKeywords().isEmpty());
    }

    @Test
    void testDetect_MultipleTechStacks() {
        Map<String, Boolean> fileExists = new HashMap<>();
        fileExists.put("pom.xml", true);
        fileExists.put("Dockerfile", true);
        fileExists.put("vite.config.js", true);

        TechStackProfile profile = detector.detect(
                "fullstack-app",
                "A full stack Spring Boot app with Vue frontend",
                "Java",
                "Built with Spring Boot, Vue, and Docker.",
                fileExists
        );

        assertTrue(profile.getDetectedStacks().contains("Java"));
        assertTrue(profile.getDetectedStacks().contains("Maven"));
        assertTrue(profile.getDetectedStacks().contains("Docker"));
        assertTrue(profile.getDetectedStacks().contains("Vite"));
        assertTrue(profile.getDetectedStacks().contains("Spring Boot"));
        assertTrue(profile.getDetectedStacks().contains("Vue"));
    }
}