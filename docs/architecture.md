# 架构说明

## 整体架构

RepoHealth-Lite Plus 采用标准的 Spring Boot 分层架构：

```
┌─────────────────────────────────────────────────┐
│                  前端 (Static)                     │
│         index.html / app.js / style.css           │
└──────────────────┬──────────────────────────────┘
                   │ HTTP REST
┌──────────────────▼──────────────────────────────┐
│              Controller 层                        │
│   HealthController / AnalysisController / ...     │
└──────────────────┬──────────────────────────────┘
                   │
┌──────────────────▼──────────────────────────────┐
│               Service 层                         │
│   AnalysisService / HealthScoreService / ...     │
└──────────────────┬──────────────────────────────┘
                   │
┌──────────────────▼──────────────────────────────┐
│              GitHubClient 层                      │
│   RestTemplate → api.github.com                    │
└─────────────────────────────────────────────────┘
```

## 分层职责

### Controller 层

- 接收 HTTP 请求
- 参数校验
- 调用 Service 层
- 返回 ApiResponse 统一响应

### Service 层

- 业务逻辑编排
- 调用 GitHubClient 获取数据
- 调用分析引擎（ReadmeAnalyzer、HealthScoreService 等）
- 数据组装

### GitHub 层

- 封装 GitHub REST API 调用
- 支持分页
- 异常封装为 GitHubApiException

### Model 层

- 纯数据模型（POJO）
- 无业务逻辑

### Common 层

- ApiResponse — 统一响应格式
- GlobalExceptionHandler — 全局异常处理

## 核心业务流程

```
用户输入 username
        │
        ▼
获取仓库列表 ────────── 分页获取，最多 10 页
        │
        ▼
对每个仓库：
  ├── 获取 README ── 按顺序尝试多个文件名
  ├── 分析 README ── 关键词匹配 8 个维度
  ├── 健康度评分 ─── 根据 11 项规则打分
  ├── 获取语言统计 ── 调用 GitHub Languages API
  └── 技术栈识别 ─── 文件 + 关键词 + 语言规则
        │
        ▼
语言汇总 ── 累加字节数、计算百分比
        │
        ▼
简历推荐 ── 根据健康度 + 技术栈 + 关键词评分
        │
        ▼
Markdown 报告生成
        │
        ▼
前端展示
```

## 关键设计

1. **容错设计**：单个仓库 README 获取失败不影响整体分析
2. **本地缓存**：ConcurrentHashMap 实现，10 分钟过期，减少重复请求
3. **无状态**：所有分析结果不持久化，每次请求实时计算
4. **纯规则引擎**：所有分析使用关键词匹配，无需 AI/ML 模型