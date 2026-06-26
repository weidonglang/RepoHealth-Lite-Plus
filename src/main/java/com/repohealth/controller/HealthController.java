package com.repohealth.controller;

import com.repohealth.common.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HealthController {

    @GetMapping("/api/health")
    public ApiResponse<Map<String, Object>> health() {
        Map<String, Object> data = Map.of(
                "name", "RepoHealth-Lite Plus",
                "version", "0.1.0",
                "status", "UP"
        );
        return ApiResponse.ok(data);
    }
}