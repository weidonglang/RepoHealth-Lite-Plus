package com.repohealth.controller;

import com.repohealth.common.ApiResponse;
import com.repohealth.model.TechStackProfile;
import com.repohealth.service.TechStackService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class TechStackController {

    private final TechStackService techStackService;

    public TechStackController(TechStackService techStackService) {
        this.techStackService = techStackService;
    }

    @GetMapping("/tech-stack/{username}")
    public ApiResponse<List<TechStackProfile>> getTechStack(@PathVariable String username) {
        List<TechStackProfile> profiles = techStackService.getTechStackProfiles(username);
        return ApiResponse.ok(profiles);
    }
}