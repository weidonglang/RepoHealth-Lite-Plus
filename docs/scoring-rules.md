# 评分规则文档

## 1. 仓库健康度评分规则

### 1.1 评分项目

满分 100 分。以下为加分项：

| 评分项 | 分值 | 说明 |
| --- | ---: | --- |
| README 存在 | +15 | 仓库根目录下存在 README 文件 |
| README 超过 300 字 | +10 | README 内容字符数超过 300 |
| 有项目简介 | +8 | README 包含"项目简介/Overview/Introduction"等关键词 |
| 有功能说明 | +8 | README 包含"功能特性/Features"等关键词 |
| 有技术栈说明 | +8 | README 包含"技术栈/Tech Stack"等关键词 |
| 有运行方式 | +12 | README 包含"运行/Run/Usage"等关键词 |
| 有测试说明 | +8 | README 包含"测试/Test/JUnit"等关键词 |
| 有截图/Demo | +8 | README 包含"截图/Screenshots/Demo"等关键词 |
| 仓库 size > 0 | +10 | 仓库不为空 |
| 最近 180 天有更新 | +8 | pushedAt 在 180 天内 |
| README 有 License 说明 | +5 | README 包含"License/MIT/Apache/GPL"等关键词 |

### 1.2 等级划分

| 分数范围 | 等级 | 说明 |
| ---: | --- | --- |
| 90-100 | EXCELLENT | 完整、高质量项目 |
| 75-89 | GOOD | 良好项目，有少量改进空间 |
| 60-74 | FAIR | 基本可用，但缺乏一些关键信息 |
| 40-59 | INCOMPLETE | 不完整，缺 README 或运行方式 |
| 0-39 | ARCHIVE_CANDIDATE | 建议归档，空仓库或缺 README |

### 1.3 限制规则

| 条件 | 限制 |
| --- | --- |
| 仓库 size == 0（空仓库） | 最高分数不超过 10 |
| README 不存在 | 最高分数不超过 50 |
| archived == true | 最高等级不超过 FAIR（即最高 74 分） |

### 1.4 建议生成规则

| 条件 | 建议 |
| --- | --- |
| README 不存在 | "建议补充 README" |
| README < 300 字 | "建议扩展项目说明" |
| 无运行方式 | "建议补充运行命令" |
| 无测试说明 | "建议补充测试命令" |
| 无截图/Demo | "建议补充截图或 GIF" |
| 仓库大小 = 0 | "建议删除或 Archive" |
| pushedAt > 180 天 | "建议确认是否继续维护" |

---

## 2. 简历展示价值评分规则

### 2.1 评分项

满分 100 分。以下为加分项：

| 评分项 | 分值 | 判断依据 |
| --- | ---: | --- |
| Java / Spring Boot 项目 | +15 | 技术栈包含 Java + Spring Boot |
| AI / RAG / 搜索相关 | +15 | 仓库名/描述/README/技术栈包含 AI、RAG、Search、Elasticsearch 等关键词 |
| 低空/UAV/调度/算法相关 | +20 | 包含 UAV、Route、Planning、Scheduling、低空、无人机、航线、路径规划等关键词 |
| 开发者工具相关 | +10 | 包含 CLI、Tool、Toolkit、Analyzer、Generator 等关键词 |
| 安全/加密/可观测性相关 | +10 | 包含 Security、Crypto、Encrypt、Log、Observability 等关键词 |
| 仓库健康度 >= 75 | +10 | 健康度评分 >= 75 |
| 有测试说明 | +10 | README 包含测试相关关键词 |
| 有前端展示或截图 | +8 | README 包含截图/Demo 关键词 |
| 项目名清晰专业 | +5 | 项目名不是 meaningless 命名 |
| 不是普通 CRUD | +7 | 不是 Employee/Banking/Student Management 等 |

### 2.2 等级划分

| 分数范围 | 等级 | 说明 |
| ---: | --- | --- |
| 80-100 | PRIMARY_SHOWCASE | 重点展示项目 |
| 60-79 | SECONDARY_SHOWCASE | 补充展示项目 |
| 40-59 | KEEP | 保留但不重点展示 |
| 20-39 | NOT_FOR_RESUME | 不建议写入简历 |
| 0-19 | ARCHIVE_CANDIDATE | 建议归档 |

### 2.3 关键词匹配规则

**AI/RAG/搜索相关**：

- AI, RAG, Search, Elasticsearch, Vector, Embedding, Recommendation, Ollama, 检索, 推荐, 向量, 智能问答

**低空/UAV/调度/算法相关**：

- LowAlt, SkyGrid, UAV, Route, Planning, Scheduling, 低空, 无人机, 航线, 调度, 冲突检测, 路径规划

**开发者工具相关**：

- DevEnv, Manager, CLI, Tool, Toolkit, Analyzer, Generator, 开发环境, 工具, 诊断

**安全/加密/可观测性相关**：

- Security, SecGuard, Crypto, Encrypt, Log, Observability, WAL, Audit, 安全, 加密, 日志, 审计, 可观测性

**普通 CRUD 判断**：

- Employee Management, Banking Management, Student Management, 后台管理系统, 员工管理, 学生管理, 银行管理

---

## 3. 作品集推荐规则

### 3.1 分组规则

| 分组 | 条件 |
| --- | --- |
| recommendedShowcase | 简历价值 >= 80 且 健康度 >= 70 且 不是 fork 且 不是 archived 且 不是空仓库 |
| secondaryShowcase | 简历价值 60-79 且 健康度 >= 60 |
| needsImprovement | 健康度 40-59 或 价值高但 README 不完整 |
| archiveCandidates | 健康度 < 40 或 空仓库 或 README 不存在且 size 很小 |
| notesOrPractice | 仓库名或 README 表明是 notes/review/practice/scripts/刷题/笔记 |

### 3.2 Priority Actions

按优先级生成建议：

| 优先级 | 说明 |
| --- | --- |
| P0 | 删除或归档空仓库 |
| P1 | 给重点项目补 README |
| P2 | 给重点项目补运行方式 |
| P3 | 给重点项目补截图或 GIF |
| P4 | 给重点项目补测试说明 |
| P5 | 整理 GitHub Profile README |