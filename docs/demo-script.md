# Demo 脚本

## 前提条件

1. JDK 17+ 已安装
2. Maven 3.6+ 已安装
3. 网络可以访问 GitHub API

## 演示流程

### 1. 启动项目

```bash
cd RepoHealth-Lite-Plus
mvn spring-boot:run
```

预期输出：
```
Started RepoHealthApplication in X.XXX seconds
```

### 2. 健康检查

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

### 3. 打开前端页面

浏览器访问：http://localhost:8080

页面包含：
- 项目标题 "RepoHealth-Lite Plus"
- GitHub 用户名输入框
- "开始分析"按钮
- "加载示例数据"按钮

### 4. 加载示例数据（无需网络）

点击"加载示例数据"按钮，前端会加载预置的示例 JSON 数据并展示。

展示内容：
- **总览**：仓库数量统计、健康度分布
- **语言构成**：CSS 进度条展示各语言百分比
- **仓库体检**：表格展示每个仓库的健康度分数和等级
- **技术栈**：每个仓库检测到的技术栈标签
- **简历推荐**：重点展示 / 补充展示 / 需要整理 / 建议归档
- **报告预览**：Markdown 报告原文 + 复制 / 下载

### 5. 真实的 GitHub 用户分析

在输入框中输入 GitHub 用户名（如 `octocat` 或真实用户名），点击"开始分析"。

等待后端完成分析（取决于仓库数量，通常 3-10 秒）。

### 6. 直接调用 API

```bash
# 获取仓库列表
curl http://localhost:8080/api/repos/octocat | jq .

# 健康度分析
curl http://localhost:8080/api/analyze/octocat | jq .

# 语言构成
curl http://localhost:8080/api/languages/octocat | jq .

# 技术栈
curl http://localhost:8080/api/tech-stack/octocat | jq .

# 简历推荐
curl http://localhost:8080/api/portfolio/octocat | jq .

# 获取报告
curl http://localhost:8080/api/report/octocat

# 下载报告
curl -o report.md http://localhost:8080/api/report/octocat/download
```

### 7. 查看测试结果

```bash
mvn test
```

预期输出：
```
Tests run: XX, Failures: 0, Errors: 0, Skipped: 0
```

### 8. 演示要点

| 场景 | 说明 |
| --- | --- |
| 正常用户 | 展示完整分析流程 |
| Demo 模式 | 无网络时展示前端 |
| 健康度差异 | 空仓库 vs 完整项目 |
| 语言百分比 | 多语言仓库的占比 |
| 报告下载 | Markdown 文件下载 |

### 9. 常见问题

**Q: 启动时端口被占用？**
A: 修改 `application.yml` 中的 `server.port`

**Q: GitHub API 限流？**
A: 未认证的 GitHub API 每小时 60 次请求，等待一小时后自动恢复

**Q: 中文乱码？**
A: 确保终端和浏览器使用 UTF-8 编码

**Q: 用户不存在？**
A: 后端返回 "GitHub 用户不存在" 错误提示

## 演示时长

| 步骤 | 预计时间 |
| --- | ---: |
| 启动项目 | 30 秒 |
| 健康检查 | 5 秒 |
| Demo 模式展示 | 1 分钟 |
| 真实用户分析 | 2 分钟 |
| API 调用展示 | 2 分钟 |
| 报告下载 | 30 秒 |
| 测试展示 | 30 秒 |
| **总计** | **约 6 分钟** |