# API 接口文档

## 统一响应格式

所有接口返回统一格式：

### 成功响应

```json
{
  "success": true,
  "message": "OK",
  "data": {}
}
```

### 失败响应

```json
{
  "success": false,
  "message": "错误说明",
  "data": null
}
```

---

## 1. 健康检查

```
GET /api/health
```

### 响应示例

```json
{
  "success": true,
  "message": "OK",
  "data": {
    "name": "RepoHealth-Lite Plus",
    "version": "0.1.0",
    "status": "UP"
  }
}
```

---

## 2. 获取仓库列表

```
GET /api/repos/{username}
```

### 路径参数

| 参数 | 类型 | 说明 |
| --- | --- | --- |
| username | String | GitHub 用户名 |

### 响应字段

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| name | String | 仓库名 |
| fullName | String | 完整仓库名 (owner/repo) |
| owner | String | 仓库所有者 |
| description | String | 仓库描述 |
| htmlUrl | String | GitHub 页面链接 |
| primaryLanguage | String | 主要编程语言 |
| stars | int | Star 数 |
| forks | int | Fork 数 |
| size | int | 仓库大小 (KB) |
| topics | String[] | 仓库主题标签 |

### 错误示例

```json
{
  "success": false,
  "message": "GitHub 用户不存在，请检查用户名是否正确",
  "data": null
}
```

---

## 3. 仓库健康度分析

```
GET /api/analyze/{username}
```

### 路径参数

| 参数 | 类型 | 说明 |
| --- | --- | --- |
| username | String | GitHub 用户名 |

### 响应字段

```json
{
  "success": true,
  "message": "OK",
  "data": {
    "username": "octocat",
    "totalRepositories": 8,
    "results": [
      {
        "repositoryName": "repo-name",
        "htmlUrl": "https://github.com/octocat/repo-name",
        "score": 92,
        "level": "EXCELLENT",
        "strengths": ["README 完整", "有运行方式"],
        "problems": [],
        "suggestions": [],
        "readmeQuality": {
          "repositoryName": "repo-name",
          "exists": true,
          "charCount": 3200,
          "wordCount": 480,
          "hasOverview": true,
          "hasFeatures": true,
          "hasTechStack": true,
          "hasQuickStart": true,
          "hasRunInstructions": true,
          "hasTestInstructions": true,
          "hasScreenshots": true,
          "hasLicenseSection": true,
          "suggestions": []
        }
      }
    ],
    "summary": {
      "excellentCount": 1,
      "goodCount": 2,
      "fairCount": 1,
      "incompleteCount": 1,
      "archiveCandidateCount": 3
    }
  }
}
```

---

## 4. 语言构成统计

```
GET /api/languages/{username}
```

### 响应字段

```json
{
  "success": true,
  "message": "OK",
  "data": {
    "username": "octocat",
    "repositoryCount": 8,
    "totalBytes": 1250000,
    "totalLanguageBytes": {
      "Java": 520000,
      "Python": 280000
    },
    "languagePercentages": {
      "Java": 41.6,
      "Python": 22.4
    },
    "repositoriesByLanguage": {
      "Java": ["repo1", "repo2"],
      "Python": ["repo3"]
    },
    "repositoryLanguageStats": []
  }
}
```

---

## 5. 技术栈识别

```
GET /api/tech-stack/{username}
```

### 响应字段

```json
{
  "success": true,
  "message": "OK",
  "data": [
    {
      "repositoryName": "repo-name",
      "detectedStacks": ["Java", "Spring Boot", "Maven", "Docker"],
      "detectedFiles": ["pom.xml", "Dockerfile"],
      "detectedKeywords": ["Spring Boot", "RAG"]
    }
  ]
}
```

---

## 6. 简历项目推荐

```
GET /api/portfolio/{username}
```

### 响应字段

```json
{
  "success": true,
  "message": "OK",
  "data": {
    "username": "octocat",
    "totalRepositories": 8,
    "recommendedShowcase": [...],
    "secondaryShowcase": [...],
    "needsImprovement": [...],
    "archiveCandidates": [...],
    "notesOrPractice": [...],
    "priorityActions": [
      "P0：删除或归档空仓库（empty-repo）",
      "P1：给重点项目补充 README"
    ]
  }
}
```

---

## 7. 获取 Markdown 报告

```
GET /api/report/{username}
```

返回 Markdown 文本内容。

### 响应示例

```json
{
  "success": true,
  "message": "OK",
  "data": "# GitHub Portfolio Report: octocat\n\n## 1. Overview\n..."
}
```

---

## 8. 下载 Markdown 报告

```
GET /api/report/{username}/download
```

### 响应头

```
Content-Type: text/markdown;charset=UTF-8
Content-Disposition: attachment; filename="{username}-portfolio-report.md"
```

直接下载 `.md` 文件。

---

## 错误码说明

| HTTP 状态码 | 说明 |
| --- | --- |
| 200 | 请求成功 |
| 400 | 请求参数错误 |
| 404 | GitHub 用户不存在 |
| 429 | GitHub API 请求过于频繁（限流） |
| 502 | GitHub API 暂时不可用 |
| 504 | 请求超时 |
| 500 | 服务器内部错误 |