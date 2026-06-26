package com.repohealth.service;

import com.repohealth.model.RepoHealthResult;
import com.repohealth.model.RepositoryInfo;
import com.repohealth.model.ResumeShowcaseResult;
import com.repohealth.model.ShowcaseLevel;
import com.repohealth.model.TechStackProfile;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ResumeScoreService {

    public ResumeShowcaseResult score(RepositoryInfo repo, RepoHealthResult health, TechStackProfile techStack) {
        int score = 0;
        List<String> reasons = new ArrayList<>();
        List<String> resumeKeywords = new ArrayList<>();

        String repoName = repo.getName() != null ? repo.getName().toLowerCase() : "";
        String description = repo.getDescription() != null ? repo.getDescription().toLowerCase() : "";
        String readmeContent = "";
        if (health != null && health.getReadmeQuality() != null && health.getReadmeQuality().getSuggestions() != null) {
            // We don't have direct readme content here, use repo name and description
        }

        // 1. Java / Spring Boot 项目
        if (hasStack(techStack, "Spring Boot") || hasStack(techStack, "Java")) {
            score += 15;
            reasons.add("Java/Spring Boot project with enterprise value");
            resumeKeywords.add("Java");
            resumeKeywords.add("Spring Boot");
        }

        // 2. AI / RAG / 搜索相关
        if (containsAny(repoName, description, techStack,
                "ai", "rag", "search", "elasticsearch", "vector", "embedding",
                "recommendation", "ollama", "检索", "推荐", "向量", "智能问答")) {
            score += 15;
            reasons.add("AI/RAG/search related project, high technical value");
            resumeKeywords.add("AI");
        }

        // 3. 低空 / UAV / 调度 / 算法相关
        if (containsAny(repoName, description, techStack,
                "lowalt", "skygrid", "uav", "route", "planning", "scheduling",
                "低空", "无人机", "航线", "调度", "冲突检测", "路径规划")) {
            score += 20;
            reasons.add("Low-altitude/UAV/scheduling project with high industry relevance");
            resumeKeywords.add("UAV");
            resumeKeywords.add("Scheduling");
        }

        // 4. 开发者工具相关
        if (containsAny(repoName, description, techStack,
                "devenv", "manager", "cli", "tool", "toolkit", "analyzer", "generator",
                "开发环境", "工具", "诊断")) {
            score += 10;
            reasons.add("Developer tool project, demonstrates engineering ability");
            resumeKeywords.add("Developer Tools");
        }

        // 5. 安全 / 加密 / 可观测性相关
        if (containsAny(repoName, description, techStack,
                "security", "secguard", "crypto", "encrypt", "log", "observability",
                "wal", "audit", "安全", "加密", "日志", "审计", "可观测性")) {
            score += 10;
            reasons.add("Security/observability project with system design value");
            resumeKeywords.add("Security");
        }

        // 6. 仓库健康度 >= 75
        if (health != null && health.getScore() >= 75) {
            score += 10;
            reasons.add("Repository health score >= 75, well-maintained project");
        }

        // 7. 有测试说明
        if (health != null && health.getReadmeQuality() != null && health.getReadmeQuality().isHasTestInstructions()) {
            score += 10;
            reasons.add("Includes test instructions");
            resumeKeywords.add("Testing");
        }

        // 8. 有前端展示或截图
        if (health != null && health.getReadmeQuality() != null && health.getReadmeQuality().isHasScreenshots()) {
            score += 8;
            reasons.add("Includes screenshots or demo");
        }

        // 9. 项目名清晰专业
        if (isProfessionalName(repo.getName())) {
            score += 5;
            reasons.add("Professional project name");
        }

        // 10. 不是普通 CRUD
        if (isCrudProject(repoName, description)) {
            score = Math.max(score - 7, 0);
            reasons.add("Standard CRUD project, limited showcase value");
        }

        // Clamp score
        score = Math.max(0, Math.min(100, score));

        // Determine level
        ShowcaseLevel level;
        String recommendation;
        if (score >= 80) {
            level = ShowcaseLevel.PRIMARY_SHOWCASE;
            recommendation = "Highly recommended as primary showcase project on resume";
        } else if (score >= 60) {
            level = ShowcaseLevel.SECONDARY_SHOWCASE;
            recommendation = "Recommended as secondary showcase project";
        } else if (score >= 40) {
            level = ShowcaseLevel.KEEP;
            recommendation = "Can be kept on resume but not as highlight";
        } else if (score >= 20) {
            level = ShowcaseLevel.NOT_FOR_RESUME;
            recommendation = "Not recommended for resume, consider improving or archiving";
        } else {
            level = ShowcaseLevel.ARCHIVE_CANDIDATE;
            recommendation = "Recommend archiving, not suitable for resume";
        }

        ResumeShowcaseResult result = new ResumeShowcaseResult();
        result.setRepositoryName(repo.getName());
        result.setHtmlUrl(repo.getHtmlUrl());
        result.setScore(score);
        result.setLevel(level);
        result.setReasons(reasons);
        result.setResumeKeywords(resumeKeywords);
        result.setRecommendation(recommendation);

        return result;
    }

    private boolean hasStack(TechStackProfile techStack, String stackName) {
        return techStack != null && techStack.getDetectedStacks() != null
                && techStack.getDetectedStacks().stream().anyMatch(s -> s.equalsIgnoreCase(stackName));
    }

    private boolean containsAny(String repoName, String description, TechStackProfile techStack, String... keywords) {
        for (String keyword : keywords) {
            if (repoName != null && repoName.contains(keyword)) return true;
            if (description != null && description.contains(keyword)) return true;
        }
        if (techStack != null) {
            for (String stack : techStack.getDetectedStacks()) {
                String lowerStack = stack.toLowerCase();
                for (String keyword : keywords) {
                    if (lowerStack.contains(keyword)) return true;
                }
            }
            for (String keyword : techStack.getDetectedKeywords()) {
                String lowerKeyword = keyword.toLowerCase();
                for (String k : keywords) {
                    if (lowerKeyword.contains(k)) return true;
                }
            }
        }
        return false;
    }

    private boolean isProfessionalName(String name) {
        if (name == null || name.isEmpty()) return false;
        // Check if name looks like a proper project: not single words, not "my-xxx" patterns
        return name.contains("-") || name.contains("_") || name.chars().filter(c -> c >= 'A' && c <= 'Z').count() >= 2
                || name.chars().filter(Character::isUpperCase).count() >= 2;
    }

    private boolean isCrudProject(String repoName, String description) {
        String[] crudPatterns = {
                "employee management", "banking management", "student management",
                "后台管理系统", "员工管理", "学生管理", "银行管理",
                "crud", "admin panel", "management system"
        };
        for (String pattern : crudPatterns) {
            if (repoName != null && repoName.contains(pattern)) return true;
            if (description != null && description.contains(pattern)) return true;
        }
        return false;
    }
}