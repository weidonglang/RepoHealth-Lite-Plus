# RepoHealth-Lite Plus

> GitHub 作品集体检与语言构成分析工具

<p align="center">
  <strong>输入 GitHub 用户名 → 分析仓库健康度 → 统计语言构成 → 识别技术栈 → 推荐简历项目 → 生成 Markdown 报告</strong>
</p>

---

## 项目简介

RepoHealth-Lite Plus 是一个基于 Java Spring Boot 的 GitHub 作品集分析工具。

它可以输入 GitHub 用户名，然后分析该用户的公开仓库：

- **仓库列表** — 获取用户所有公开仓库及其元数据
- **README 完整度** — 检测是否存在项目简介、功能说明、技术栈、运行方式、测试说明等关键信息
- **仓库健康度** — 从文档完整度、活跃度、代码量等维度综合评分
- **语言构成** — 统计 Java、Python、Rust 等语言在所有仓库中的占比
- **技术栈画像** — 基于 README 关键词和项目文件识别技术栈（Spring Boot、Vue、Docker 等）
- **简历展示价值** — 判断仓库是否适合写进简历，识别 AI/RAG、低空/UAV 等热门方向项目
- **疑似烂尾仓库** — 识别空仓库、缺 README、长期未更新等项目
- **Markdown 分析报告** — 生成包含总览、语言构成、健康排名、简历推荐、改进计划的完整报告

## 功能特性

- ✅ 输入 GitHub 用户名即可分析，无需登录
- ✅ 一键获取公开仓库列表（支持分页，最多 1000 个仓库）
- ✅ README 完整度检测（8 个维度关键词匹配）
- ✅ 仓库健康度评分（满分 100，5 个等级）
- ✅ 语言构成统计（百分比 + 字节数 + 对应仓库列表）
- ✅ 技术栈自动识别（文件规则 + 关键词规则 + 语言规则）
- ✅ 简历展示价值评分（识别 AI/RAG、低空/UAV、开发者工具等方向）
- ✅ 作品集推荐分组（重点展示 / 补充展示 / 需要整理 / 建议归档）
- ✅ Markdown 报告生成与下载
- ✅ 轻量前端展示（原生 HTML/CSS/JavaScript，CSS 进度条）
- ✅ Demo 模式（无网络也可体验）
- ✅ 本地缓存（10 分钟过期，减少重复请求）

## 技术栈

| 分类 | 技术 |
| --- | --- |
| 后端语言 | Java 17 |
| 框架 | Spring Boot 3.2 |
| 构建工具 | Maven |
| Web | Spring Web |
| 数据校验 | Spring Validation |
| JSON 处理 | Jackson |
| 前端 | 原生 HTML + CSS + JavaScript |
| 测试 | JUnit 5 + Mockito |
| 缓存 | ConcurrentHashMap（本地缓存） |

## 快速开始

### 前提条件

- JDK 17+
- Maven 3.6+

### 运行

```bash
# 克隆项目
git clone https://github.com/yourusername/RepoHealth-Lite-Plus.git
cd RepoHealth-Lite-Plus

# 编译测试
mvn test

# 启动服务
mvn spring-boot:run
```

启动后访问：http://localhost:8080

### 健康检查

```bash
curl http://localhost:8080/api/health
```

预期返回：

```json
{
  "success": true,
  "message": "OK",
  "data": {
    "name": "RepoHealth-Lite Plus",
    "version": "1.0.0",
    "status": "UP"
  }
}
```

## 接口列表

| 方法 | 接口 | 用途 |
| --- | --- | --- |
| GET | `/api/health` | 健康检查 |
| GET | `/api/repos/{username}` | 获取公开仓库列表 |
| GET | `/api/analyze/{username}` | 仓库健康度分析 |
| GET | `/api/languages/{username}` | 语言构成统计 |
| GET | `/api/tech-stack/{username}` | 技术栈识别 |
| GET | `/api/portfolio/{username}` | 简历项目推荐 |
| GET | `/api/report/{username}` | 获取 Markdown 报告内容 |
| GET | `/api/report/{username}/download` | 下载 Markdown 报告 |

### 接口示例

```bash
# 仓库健康度分析
curl http://localhost:8080/api/analyze/octocat

# 语言构成统计
curl http://localhost:8080/api/languages/octocat

# 技术栈识别
curl http://localhost:8080/api/tech-stack/octocat

# 简历推荐
curl http://localhost:8080/api/portfolio/octocat

# 下载报告
curl -O http://localhost:8080/api/report/octocat/download
```

## 评分规则

### 健康度评分（满分 100）

| 项目 | 分值 |
| --- | ---:|
| README 存在 | +15 |
| README 超过 300 字 | +10 |
| 有项目简介 | +8 |
| 有功能说明 | +8 |
| 有技术栈说明 | +8 |
| 有运行方式 | +12 |
| 有测试说明 | +8 |
| 有截图 / Demo | +8 |
| 仓库 size > 0 | +10 |
| 最近 180 天有更新 | +8 |
| README 有 License 说明 | +5 |

**等级划分**：

| 分数范围 | 等级 |
| ---: | --- |
| 90-100 | EXCELLENT |
| 75-89 | GOOD |
| 60-74 | FAIR |
| 40-59 | INCOMPLETE |
| 0-39 | ARCHIVE_CANDIDATE |

**限制规则**：

- 空仓库（size == 0）最高 10 分
- 无 README 最高 50 分
- archived 仓库最高等级 FAIR

### 简历展示价值评分（满分 100）

| 项目 | 分值 |
| --- | ---:|
| Java / Spring Boot 项目 | +15 |
| AI / RAG / 搜索相关 | +15 |
| 低空 / UAV / 调度 / 算法相关 | +20 |
| 开发者工具相关 | +10 |
| 安全 / 加密 / 可观测性相关 | +10 |
| 仓库健康度 >= 75 | +10 |
| 有测试说明 | +10 |
| 有前端展示或截图 | +8 |
| 项目名清晰专业 | +5 |
| 不是普通 CRUD | +7 |

## 页面截图

*（页面截图 - 启动后访问 http://localhost:8080）*

页面包含以下 Tab：

1. **总览** — 仓库健康度分布概览卡片
2. **语言构成** — CSS 进度条 + 语言占比表格
3. **仓库体检** — 健康度排名表格，支持按等级筛选
4. **技术栈** — 每个仓库检测到的技术栈标签展示
5. **简历推荐** — 重点展示 / 补充展示 / 需要整理 / 建议归档分组
6. **报告预览** — Markdown 原文预览 + 复制 / 下载

## 示例报告

查看完整示例报告：[reports/sample-report.md](reports/sample-report.md)

## 测试方式

```bash
# 运行全部测试
mvn test

# 运行指定测试
mvn test -Dtest=HealthScoreServiceTest
```

测试覆盖：

- `ReadmeAnalyzerTest` — README 完整度分析测试
- `HealthScoreServiceTest` — 健康度评分测试
- `LanguageAnalyzerTest` — 语言构成汇总测试
- `TechStackDetectorTest` — 技术栈识别测试
- `ResumeScoreServiceTest` — 简历展示价值评分测试
- `ReportGeneratorTest` — Markdown 报告生成测试
- `GitHubClientTest` — GitHub API 调用测试
- `AnalysisServiceTest` — 分析服务集成测试
- `RepositoryControllerTest` — 仓库控制器测试
- `HealthControllerTest` — 健康检查控制器测试

当前共 **54 个测试**，全部通过。

## 项目结构

```
RepoHealth-Lite-Plus/
├── README.md
├── pom.xml
├── .gitignore
├── docs/
│   ├── architecture.md
│   ├── api.md
│   ├── scoring-rules.md
│   └── demo-script.md
├── reports/
│   └── sample-report.md
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── repohealth/
│   │   │           ├── RepoHealthApplication.java
│   │   │           ├── common/          # 统一响应、异常处理
│   │   │           ├── controller/      # REST 控制器
│   │   │           ├── github/          # GitHub API 调用
│   │   │           ├── model/           # 数据模型
│   │   │           ├── service/         # 业务逻辑
│   │   │           └── util/            # 工具方法
│   │   └── resources/
│   │       ├── application.yml
│   │       └── static/
│   │           ├── index.html           # 前端页面
│   │           ├── app.js               # 前端逻辑
│   │           └── style.css            # 前端样式
│   └── test/
│       └── java/
│           └── com/
│               └── repohealth/
```

## 后续计划

- [ ] GitHub Token 支持（提高 API 限流上限）
- [ ] ECharts 图表展示
- [ ] HTML 格式报告导出
- [ ] 分析历史记录保存
- [ ] 多用户对比分析
- [ ] AI 简历描述生成

## License

MIT License

Copyright (c) 2024 RepoHealth