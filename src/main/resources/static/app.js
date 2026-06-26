// RepoHealth-Lite Plus - Frontend Application

const API_BASE = '/api';
let currentUsername = '';
let currentHealthFilter = 'all';
let allHealthResults = [];
let currentReportMarkdown = '';

// ===================== Tab Switching =====================

function switchTab(tabName) {
    document.querySelectorAll('.tab').forEach(t => t.classList.remove('active'));
    document.querySelectorAll('.tab-content').forEach(tc => tc.classList.remove('active'));

    const tabBtn = document.querySelector(`.tab[data-tab="${tabName}"]`);
    if (tabBtn) tabBtn.classList.add('active');

    const tabContent = document.getElementById(tabName + '-tab');
    if (tabContent) tabContent.classList.add('active');
}

// ===================== UI Helpers =====================

function showLoading() {
    document.getElementById('loading').style.display = 'flex';
}

function hideLoading() {
    document.getElementById('loading').style.display = 'none';
}

function showResults() {
    document.getElementById('result-section').style.display = 'block';
}

function hideResults() {
    document.getElementById('result-section').style.display = 'none';
}

function showError(message) {
    const el = document.getElementById('error-message');
    el.textContent = message;
    el.style.display = 'block';
}

function hideError() {
    document.getElementById('error-message').style.display = 'none';
}

function escapeHtml(text) {
    if (!text) return '';
    const div = document.createElement('div');
    div.textContent = String(text);
    return div.innerHTML;
}

function getScoreClass(score) {
    if (score >= 90) return 'score-excellent';
    if (score >= 75) return 'score-good';
    if (score >= 60) return 'score-fair';
    if (score >= 40) return 'score-incomplete';
    return 'score-archive';
}

function getLangClass(lang) {
    const map = {
        'Java': 'lang-java',
        'Python': 'lang-python',
        'JavaScript': 'lang-javascript',
        'TypeScript': 'lang-typescript',
        'Rust': 'lang-rust',
        'HTML': 'lang-html',
        'CSS': 'lang-css',
        'Go': 'lang-go',
        'Kotlin': 'lang-kotlin',
        'C': 'lang-c',
        'C++': 'lang-cpp'
    };
    return map[lang] || 'lang-default';
}

function formatBytes(bytes) {
    if (!bytes || bytes === 0) return '0 B';
    const units = ['B', 'KB', 'MB', 'GB'];
    let i = 0;
    let size = bytes;
    while (size >= 1024 && i < units.length - 1) {
        size /= 1024;
        i++;
    }
    return size.toFixed(1) + ' ' + units[i];
}

// ===================== API Helper =====================

async function apiGet(url) {
    const response = await fetch(API_BASE + url);
    const json = await response.json().catch(() => null);
    if (!response.ok) {
        if (json && !json.success && json.message) {
            throw new Error(json.message);
        }
        const statusMessages = {
            404: 'GitHub 用户不存在，请检查用户名是否正确',
            403: 'GitHub API 请求过于频繁，请稍后重试',
            429: '请求过于频繁，请稍后重试',
            502: 'GitHub API 暂时不可用，请稍后重试',
            504: '请求超时，请检查网络连接'
        };
        const message = statusMessages[response.status] || `请求失败 (${response.status})`;
        throw new Error(message);
    }
    if (!json || !json.success) {
        throw new Error(json && json.message ? json.message : '服务器返回数据格式不正确');
    }
    return json;
}

// ===================== Main Analysis =====================

async function startAnalysis() {
    const username = document.getElementById('username-input').value.trim();
    if (!username) {
        showError('请输入 GitHub 用户名');
        return;
    }

    currentUsername = username;
    hideError();
    showLoading();
    hideResults();

    try {
        // Fetch all data in parallel
        const [healthData, langData, techStackData, portfolioData] = await Promise.all([
            apiGet(`/analyze/${encodeURIComponent(username)}`),
            apiGet(`/languages/${encodeURIComponent(username)}`),
            apiGet(`/tech-stack/${encodeURIComponent(username)}`),
            apiGet(`/portfolio/${encodeURIComponent(username)}`)
        ]);

        if (!healthData.success) {
            showError(healthData.message || '分析失败，请检查用户名是否正确');
            hideLoading();
            return;
        }

        allHealthResults = healthData.data?.results || [];

        // Render all sections
        renderOverview(healthData.data);
        renderLanguages(langData);
        renderHealth(healthData.data);
        renderTechStack(techStackData);
        renderPortfolio(portfolioData);

        // Fetch report
        try {
            const reportData = await apiGet(`/report/${encodeURIComponent(username)}`);
            if (reportData.success) {
                currentReportMarkdown = reportData.data || '';
                document.getElementById('report-content').textContent = currentReportMarkdown;
            }
        } catch (e) {
            document.getElementById('report-content').textContent =
                '# GitHub Portfolio Report\n\nReport generation is being processed. Please try again.';
        }

        hideLoading();
        showResults();
        switchTab('overview');
    } catch (err) {
        hideLoading();
        showError('请求失败：' + err.message);
    }
}

// ===================== Overview Tab =====================

function renderOverview(data) {
    const container = document.getElementById('overview-content');
    if (!data) {
        container.innerHTML = '<div class="empty-state"><p>暂无数据</p></div>';
        return;
    }

    const summary = data.summary || {};
    const totalRepos = data.results ? data.results.length : 0;

    container.innerHTML = `
        <div class="overview-grid">
            <div class="overview-card">
                <div class="number">${totalRepos}</div>
                <div class="label">仓库总数</div>
            </div>
            <div class="overview-card card-excellent">
                <div class="number">${summary.excellentCount || 0}</div>
                <div class="label">优秀 (90-100)</div>
            </div>
            <div class="overview-card card-good">
                <div class="number">${summary.goodCount || 0}</div>
                <div class="label">良好 (75-89)</div>
            </div>
            <div class="overview-card card-fair">
                <div class="number">${summary.fairCount || 0}</div>
                <div class="label">一般 (60-74)</div>
            </div>
            <div class="overview-card card-incomplete">
                <div class="number">${summary.incompleteCount || 0}</div>
                <div class="label">不完整 (40-59)</div>
            </div>
            <div class="overview-card card-archive">
                <div class="number">${summary.archiveCandidateCount || 0}</div>
                <div class="label">建议归档 (<40)</div>
            </div>
        </div>
    `;
}

// ===================== Languages Tab =====================

function renderLanguages(data) {
    const container = document.getElementById('languages-content');
    if (!data || !data.success || !data.data) {
        container.innerHTML = '<div class="empty-state"><p>暂无语言数据</p></div>';
        return;
    }

    const langSummary = data.data;
    const percentages = langSummary.languagePercentages || {};
    const totalBytes = langSummary.totalLanguageBytes || {};
    const reposByLang = langSummary.repositoriesByLanguage || {};

    // Sort languages by percentage descending
    const sortedLangs = Object.entries(percentages)
        .sort((a, b) => b[1] - a[1]);

    if (sortedLangs.length === 0) {
        container.innerHTML = '<div class="empty-state"><p>该用户暂无语言数据</p></div>';
        return;
    }

    let html = '<div class="language-section">';

    // Progress bars
    sortedLangs.forEach(([lang, pct]) => {
        const langClass = getLangClass(lang);
        const displayPct = pct.toFixed(1);

        html += `
            <div class="language-bar">
                <span class="language-name">${escapeHtml(lang)}</span>
                <div class="language-progress">
                    <div class="language-progress-fill ${langClass}" style="width: ${Math.min(pct, 100)}%">
                        ${displayPct}%
                    </div>
                </div>
            </div>
        `;
    });

    html += '<h3 style="margin-top:1.5rem;margin-bottom:0.75rem;">语言详情</h3>';
    html += `
        <table class="language-table">
            <thead>
                <tr>
                    <th>语言</th>
                    <th>百分比</th>
                    <th>字节数</th>
                    <th>仓库数</th>
                </tr>
            </thead>
            <tbody>
    `;

    sortedLangs.forEach(([lang, pct]) => {
        const bytes = totalBytes[lang] || 0;
        const repoCount = (reposByLang[lang] || []).length;
        html += `
            <tr>
                <td><strong>${escapeHtml(lang)}</strong></td>
                <td>${pct.toFixed(1)}%</td>
                <td>${formatBytes(bytes)}</td>
                <td>${repoCount}</td>
            </tr>
        `;
    });

    html += '</tbody></table>';

    // Repositories by language
    html += '<h3 style="margin-top:1.5rem;margin-bottom:0.75rem;">语言对应仓库</h3>';
    sortedLangs.slice(0, 10).forEach(([lang]) => {
        const repoList = reposByLang[lang] || [];
        if (repoList.length === 0) return;

        html += `
            <div class="lang-repo-list">
                <strong>${escapeHtml(lang)}</strong>: ${escapeHtml(repoList.join(', '))}
            </div>
        `;
    });

    html += '</div>';
    container.innerHTML = html;
}

// ===================== Health Tab =====================

function renderHealth(data) {
    const container = document.getElementById('health-content');
    if (!data || !data.results || data.results.length === 0) {
        container.innerHTML = '<div class="empty-state"><p>暂无仓库健康数据</p></div>';
        return;
    }

    const results = data.results;
    allHealthResults = results;
    currentHealthFilter = 'all';
    renderHealthTable(results);
}

function filterHealth(filter) {
    currentHealthFilter = filter;

    // Update active button
    document.querySelectorAll('.filter-btn').forEach(btn => btn.classList.remove('active'));
    const activeBtn = document.querySelector(`.filter-btn[data-filter="${filter}"]`);
    if (activeBtn) activeBtn.classList.add('active');

    let filtered = allHealthResults;
    if (filter === 'excellent') {
        filtered = allHealthResults.filter(r => r.level === 'EXCELLENT' || r.score >= 90);
    } else if (filter === 'good') {
        filtered = allHealthResults.filter(r => r.level === 'GOOD' || (r.score >= 75 && r.score < 90));
    } else if (filter === 'needs') {
        filtered = allHealthResults.filter(r => r.level === 'FAIR' || r.level === 'INCOMPLETE' || (r.score >= 40 && r.score < 75));
    } else if (filter === 'archive') {
        filtered = allHealthResults.filter(r => r.level === 'ARCHIVE_CANDIDATE' || r.score < 40);
    }

    renderHealthTable(filtered);
}

function renderHealthTable(results) {
    const container = document.getElementById('health-content');

    if (!results || results.length === 0) {
        container.innerHTML = '<div class="empty-state"><p>没有符合条件的仓库</p></div>';
        return;
    }

    let html = `
        <table class="health-table">
            <thead>
                <tr>
                    <th>仓库名</th>
                    <th>分数</th>
                    <th>等级</th>
                    <th>问题</th>
                    <th>建议</th>
                </tr>
            </thead>
            <tbody>
    `;

    results.forEach(repo => {
        const scoreClass = getScoreClass(repo.score);
        const levelClass = 'level-' + (repo.level || '').toLowerCase();
        const problems = repo.problems && repo.problems.length > 0
            ? repo.problems.slice(0, 3).join('; ')
            : '无';
        const suggestions = repo.suggestions && repo.suggestions.length > 0
            ? repo.suggestions.slice(0, 2).join('; ')
            : '无';
        const htmlUrl = repo.htmlUrl || '#';

        html += `
            <tr>
                <td>
                    <a href="${escapeHtml(htmlUrl)}" target="_blank" rel="noopener">
                        ${escapeHtml(repo.repositoryName)}
                    </a>
                </td>
                <td class="health-score ${scoreClass}">${repo.score}</td>
                <td><span class="level-badge ${levelClass}">${escapeHtml(repo.level || 'N/A')}</span></td>
                <td class="cell-problem">${escapeHtml(problems)}</td>
                <td class="cell-suggestion">${escapeHtml(suggestions)}</td>
            </tr>
        `;
    });

    html += '</tbody></table>';
    container.innerHTML = html;
}

// ===================== Tech Stack Tab =====================

function renderTechStack(data) {
    const container = document.getElementById('techstack-content');
    if (!data || !data.success || !data.data || data.data.length === 0) {
        container.innerHTML = '<div class="empty-state"><p>暂无技术栈数据</p></div>';
        return;
    }

    const profiles = data.data;
    let html = '<div class="techstack-grid">';

    profiles.forEach(profile => {
        const stacks = profile.detectedStacks || [];

        html += `<div class="techstack-card"><h3>${escapeHtml(profile.repositoryName)}</h3>`;

        if (stacks.length > 0) {
            html += '<div class="techstack-tags">';
            stacks.forEach(s => {
                html += `<span class="techstack-tag">${escapeHtml(s)}</span>`;
            });
            html += '</div>';
        } else {
            html += '<p class="techstack-empty">未检测到特定技术栈</p>';
        }

        html += '</div>';
    });

    html += '</div>';
    container.innerHTML = html;
}

// ===================== Portfolio Tab =====================

function renderPortfolio(data) {
    const container = document.getElementById('portfolio-content');
    if (!data || !data.success || !data.data) {
        container.innerHTML = '<div class="empty-state"><p>暂无简历推荐数据</p></div>';
        return;
    }

    const portfolio = data.data;
    let html = '';

    // Priority actions
    const priorityActions = portfolio.priorityActions || [];
    if (priorityActions.length > 0) {
        html += `
            <div class="priority-actions">
                <h3>优先行动建议</h3>
                <ul>
                    ${priorityActions.map(a => `<li>${escapeHtml(a)}</li>`).join('')}
                </ul>
            </div>
        `;
    }

    html += renderPortfolioSection('重点展示项目', 'primary', portfolio.recommendedShowcase || []);
    html += renderPortfolioSection('补充展示项目', 'secondary', portfolio.secondaryShowcase || []);
    html += renderPortfolioSection('需要整理的项目', 'needs', portfolio.needsImprovement || []);
    html += renderPortfolioSection('建议归档项目', 'archive', portfolio.archiveCandidates || []);
    html += renderPortfolioSection('笔记或练习项目', 'notes', portfolio.notesOrPractice || []);

    if (!html) {
        html = '<div class="empty-state"><p>暂无数据</p></div>';
    }

    container.innerHTML = html;
}

function renderPortfolioSection(title, type, items) {
    if (!items || items.length === 0) return '';

    let typeClass = 'primary';
    if (type === 'secondary') typeClass = 'secondary';
    else if (type === 'needs') typeClass = 'needs';
    else if (type === 'archive') typeClass = 'archive';
    else if (type === 'notes') typeClass = 'notes';

    let html = `
        <div class="portfolio-section">
            <h3 class="section-title-${typeClass}">${escapeHtml(title)} (${items.length})</h3>
    `;

    items.forEach(item => {
        const htmlUrl = item.htmlUrl || '#';
        const reason = item.reason || '';
        const suggestion = item.suggestion || '';

        html += `
            <div class="portfolio-item">
                <div class="repo-name">
                    <a href="${escapeHtml(htmlUrl)}" target="_blank" rel="noopener">
                        ${escapeHtml(item.repositoryName)}
                    </a>
                </div>
                <div class="repo-meta">
                    健康度: ${item.healthScore ?? '-'} | 简历价值: ${item.showcaseScore ?? '-'}
                    ${item.healthLevel ? '| 等级: ' + escapeHtml(item.healthLevel) : ''}
                </div>
        `;

        if (reason) {
            html += `<div class="repo-reason">${escapeHtml(reason)}</div>`;
        }
        if (suggestion) {
            html += `<div class="repo-suggestion">${escapeHtml(suggestion)}</div>`;
        }

        html += '</div>';
    });

    html += '</div>';
    return html;
}

// ===================== Report Tab =====================

async function copyReport() {
    const text = currentReportMarkdown;
    if (!text) {
        alert('暂无报告内容');
        return;
    }

    try {
        await navigator.clipboard.writeText(text);
        alert('报告已复制到剪贴板');
    } catch (e) {
        // Fallback for older browsers
        const textarea = document.createElement('textarea');
        textarea.value = text;
        textarea.style.position = 'fixed';
        textarea.style.opacity = '0';
        document.body.appendChild(textarea);
        textarea.select();
        document.execCommand('copy');
        document.body.removeChild(textarea);
        alert('报告已复制到剪贴板');
    }
}

function downloadReport() {
    if (!currentUsername) {
        alert('请先进行分析');
        return;
    }

    const url = API_BASE + `/report/${encodeURIComponent(currentUsername)}/download`;
    window.open(url, '_blank');
}

// ===================== Demo Data =====================

function loadDemoData() {
    currentUsername = 'demo-user';
    hideError();
    hideResults();

    // Mock health analysis data
    const mockHealth = {
        success: true,
        message: "OK",
        data: {
            username: "demo-user",
            totalRepositories: 8,
            results: [
                {
                    repositoryName: "spring-boot-rag-service",
                    htmlUrl: "https://github.com/demo/spring-boot-rag-service",
                    score: 92,
                    level: "EXCELLENT",
                    strengths: ["README 完整", "有运行方式", "有测试说明"],
                    problems: [],
                    suggestions: [],
                    readmeQuality: {
                        exists: true,
                        charCount: 3200,
                        wordCount: 480,
                        hasOverview: true,
                        hasFeatures: true,
                        hasTechStack: true,
                        hasQuickStart: true,
                        hasRunInstructions: true,
                        hasTestInstructions: true,
                        hasScreenshots: true,
                        hasLicenseSection: true,
                        suggestions: []
                    }
                },
                {
                    repositoryName: "skygrid-uav-scheduler",
                    htmlUrl: "https://github.com/demo/skygrid-uav-scheduler",
                    score: 88,
                    level: "GOOD",
                    strengths: ["README 存在", "有运行方式", "有截图"],
                    problems: ["缺少测试说明"],
                    suggestions: ["建议补充测试命令"],
                    readmeQuality: {
                        exists: true,
                        charCount: 2400,
                        wordCount: 350,
                        hasOverview: true,
                        hasFeatures: true,
                        hasTechStack: true,
                        hasQuickStart: true,
                        hasRunInstructions: true,
                        hasTestInstructions: false,
                        hasScreenshots: true,
                        hasLicenseSection: false,
                        suggestions: ["建议补充测试说明"]
                    }
                },
                {
                    repositoryName: "dev-env-manager",
                    htmlUrl: "https://github.com/demo/dev-env-manager",
                    score: 76,
                    level: "GOOD",
                    strengths: ["README 存在", "有功能说明"],
                    problems: ["缺少运行方式", "缺少截图"],
                    suggestions: ["建议补充运行命令", "建议补充截图"],
                    readmeQuality: {
                        exists: true,
                        charCount: 1200,
                        wordCount: 180,
                        hasOverview: true,
                        hasFeatures: true,
                        hasTechStack: false,
                        hasQuickStart: true,
                        hasRunInstructions: false,
                        hasTestInstructions: false,
                        hasScreenshots: false,
                        hasLicenseSection: false,
                        suggestions: ["建议补充运行方式", "建议补充截图"]
                    }
                },
                {
                    repositoryName: "spring-boot-employee-system",
                    htmlUrl: "https://github.com/demo/spring-boot-employee-system",
                    score: 68,
                    level: "FAIR",
                    strengths: ["README 存在"],
                    problems: ["README 较短", "缺少运行方式", "缺少测试说明"],
                    suggestions: ["建议扩展项目说明", "建议补充运行命令", "建议补充测试命令"],
                    readmeQuality: {
                        exists: true,
                        charCount: 500,
                        wordCount: 80,
                        hasOverview: false,
                        hasFeatures: false,
                        hasTechStack: false,
                        hasQuickStart: false,
                        hasRunInstructions: false,
                        hasTestInstructions: false,
                        hasScreenshots: false,
                        hasLicenseSection: false,
                        suggestions: ["建议补充项目简介", "建议补充功能说明"]
                    }
                },
                {
                    repositoryName: "python-data-analysis",
                    htmlUrl: "https://github.com/demo/python-data-analysis",
                    score: 55,
                    level: "INCOMPLETE",
                    strengths: ["README 存在"],
                    problems: ["README 不完整", "缺少运行方式", "缺少测试说明"],
                    suggestions: ["建议补充运行命令", "建议补充测试命令", "建议补充项目说明"],
                    readmeQuality: {
                        exists: true,
                        charCount: 200,
                        wordCount: 30,
                        hasOverview: false,
                        hasFeatures: false,
                        hasTechStack: false,
                        hasQuickStart: false,
                        hasRunInstructions: false,
                        hasTestInstructions: false,
                        hasScreenshots: false,
                        hasLicenseSection: false,
                        suggestions: ["建议补充项目简介", "建议补充功能说明"]
                    }
                },
                {
                    repositoryName: "empty-repo-test",
                    htmlUrl: "https://github.com/demo/empty-repo-test",
                    score: 5,
                    level: "ARCHIVE_CANDIDATE",
                    strengths: [],
                    problems: ["仓库为空", "没有 README"],
                    suggestions: ["建议删除或归档"],
                    readmeQuality: {
                        exists: false,
                        charCount: 0,
                        wordCount: 0,
                        hasOverview: false,
                        hasFeatures: false,
                        hasTechStack: false,
                        hasQuickStart: false,
                        hasRunInstructions: false,
                        hasTestInstructions: false,
                        hasScreenshots: false,
                        hasLicenseSection: false,
                        suggestions: ["建议补充 README"]
                    }
                },
                {
                    repositoryName: "rust-cli-tool",
                    htmlUrl: "https://github.com/demo/rust-cli-tool",
                    score: 6,
                    level: "ARCHIVE_CANDIDATE",
                    strengths: [],
                    problems: ["没有 README"],
                    suggestions: ["建议补充 README"],
                    readmeQuality: {
                        exists: false,
                        charCount: 0,
                        wordCount: 0,
                        hasOverview: false,
                        hasFeatures: false,
                        hasTechStack: false,
                        hasQuickStart: false,
                        hasRunInstructions: false,
                        hasTestInstructions: false,
                        hasScreenshots: false,
                        hasLicenseSection: false,
                        suggestions: ["建议补充 README"]
                    }
                },
                {
                    repositoryName: "algorithm-practice",
                    htmlUrl: "https://github.com/demo/algorithm-practice",
                    score: 30,
                    level: "ARCHIVE_CANDIDATE",
                    strengths: [],
                    problems: ["README 缺失", "仓库 size 很小"],
                    suggestions: ["建议整理到统一仓库"],
                    readmeQuality: {
                        exists: false,
                        charCount: 0,
                        wordCount: 0,
                        hasOverview: false,
                        hasFeatures: false,
                        hasTechStack: false,
                        hasQuickStart: false,
                        hasRunInstructions: false,
                        hasTestInstructions: false,
                        hasScreenshots: false,
                        hasLicenseSection: false,
                        suggestions: ["建议补充 README"]
                    }
                }
            ],
            summary: {
                excellentCount: 1,
                goodCount: 2,
                fairCount: 1,
                incompleteCount: 1,
                archiveCandidateCount: 3
            }
        }
    };

    // Mock languages data
    const mockLanguages = {
        success: true,
        message: "OK",
        data: {
            username: "demo-user",
            repositoryCount: 8,
            totalBytes: 1250000,
            totalLanguageBytes: {
                "Java": 520000,
                "Python": 280000,
                "TypeScript": 180000,
                "Rust": 120000,
                "HTML": 75000,
                "CSS": 45000,
                "JavaScript": 30000
            },
            languagePercentages: {
                "Java": 41.6,
                "Python": 22.4,
                "TypeScript": 14.4,
                "Rust": 9.6,
                "HTML": 6.0,
                "CSS": 3.6,
                "JavaScript": 2.4
            },
            repositoriesByLanguage: {
                "Java": ["spring-boot-rag-service", "spring-boot-employee-system", "dev-env-manager"],
                "Python": ["python-data-analysis", "skygrid-uav-scheduler"],
                "TypeScript": ["skygrid-uav-scheduler", "dev-env-manager"],
                "Rust": ["rust-cli-tool"],
                "HTML": ["dev-env-manager"],
                "CSS": ["dev-env-manager"],
                "JavaScript": ["dev-env-manager"]
            },
            repositoryLanguageStats: []
        }
    };

    // Mock tech stack data
    const mockTechStack = {
        success: true,
        message: "OK",
        data: [
            {
                repositoryName: "spring-boot-rag-service",
                detectedStacks: ["Java", "Spring Boot", "Maven", "Docker", "Ollama", "RAG"],
                detectedFiles: ["pom.xml", "Dockerfile", "docker-compose.yml"],
                detectedKeywords: ["Spring Boot", "RAG", "Ollama", "Elasticsearch"]
            },
            {
                repositoryName: "skygrid-uav-scheduler",
                detectedStacks: ["Python", "FastAPI", "Docker"],
                detectedFiles: ["requirements.txt", "Dockerfile"],
                detectedKeywords: ["UAV", "Scheduling"]
            },
            {
                repositoryName: "dev-env-manager",
                detectedStacks: ["Java", "Spring Boot", "Maven", "TypeScript", "Vue"],
                detectedFiles: ["pom.xml", "package.json", "vite.config.js"],
                detectedKeywords: ["Manager", "Toolkit", "CLI"]
            },
            {
                repositoryName: "spring-boot-employee-system",
                detectedStacks: ["Java", "Spring Boot", "Maven"],
                detectedFiles: ["pom.xml"],
                detectedKeywords: ["Employee", "Management"]
            },
            {
                repositoryName: "python-data-analysis",
                detectedStacks: ["Python"],
                detectedFiles: ["requirements.txt"],
                detectedKeywords: []
            },
            {
                repositoryName: "rust-cli-tool",
                detectedStacks: ["Rust"],
                detectedFiles: ["Cargo.toml"],
                detectedKeywords: []
            },
            {
                repositoryName: "empty-repo-test",
                detectedStacks: [],
                detectedFiles: [],
                detectedKeywords: []
            },
            {
                repositoryName: "algorithm-practice",
                detectedStacks: [],
                detectedFiles: [],
                detectedKeywords: []
            }
        ]
    };

    // Mock portfolio data
    const mockPortfolio = {
        success: true,
        message: "OK",
        data: {
            username: "demo-user",
            totalRepositories: 8,
            recommendedShowcase: [
                {
                    repositoryName: "spring-boot-rag-service",
                    htmlUrl: "https://github.com/demo/spring-boot-rag-service",
                    healthScore: 92,
                    showcaseScore: 92,
                    healthLevel: "EXCELLENT",
                    reason: "完整项目，AI/RAG 主题，有完整 README、测试和 Docker 支持",
                    suggestion: "适合作为简历主打项目",
                    description: "基于 Spring Boot 的 RAG 检索增强生成服务"
                },
                {
                    repositoryName: "skygrid-uav-scheduler",
                    htmlUrl: "https://github.com/demo/skygrid-uav-scheduler",
                    healthScore: 88,
                    showcaseScore: 90,
                    healthLevel: "GOOD",
                    reason: "低空/UAV 调度项目，技术方向前沿，有截图和运行说明",
                    suggestion: "建议补充测试说明后作为主打项目",
                    description: "低空无人机航线调度与冲突检测系统"
                }
            ],
            secondaryShowcase: [
                {
                    repositoryName: "dev-env-manager",
                    htmlUrl: "https://github.com/demo/dev-env-manager",
                    healthScore: 76,
                    showcaseScore: 72,
                    healthLevel: "GOOD",
                    reason: "开发者工具类项目，技术栈丰富（Java + Vue），但缺少运行方式和截图",
                    suggestion: "补充运行说明和截图后可提升为重点展示"
                }
            ],
            needsImprovement: [
                {
                    repositoryName: "spring-boot-employee-system",
                    htmlUrl: "https://github.com/demo/spring-boot-employee-system",
                    healthScore: 68,
                    showcaseScore: 45,
                    healthLevel: "FAIR",
                    reason: "普通 CRUD 项目，README 不完整，缺乏工程亮点",
                    suggestion: "建议丰富 README 或添加特色功能"
                },
                {
                    repositoryName: "python-data-analysis",
                    htmlUrl: "https://github.com/demo/python-data-analysis",
                    healthScore: 55,
                    showcaseScore: 35,
                    healthLevel: "INCOMPLETE",
                    reason: "README 过于简略，缺少运行方式和数据分析说明",
                    suggestion: "补充 README 说明分析目的和使用方法"
                }
            ],
            archiveCandidates: [
                {
                    repositoryName: "empty-repo-test",
                    htmlUrl: "https://github.com/demo/empty-repo-test",
                    healthScore: 5,
                    showcaseScore: 0,
                    healthLevel: "ARCHIVE_CANDIDATE",
                    reason: "空仓库，没有内容",
                    suggestion: "建议删除或归档"
                },
                {
                    repositoryName: "rust-cli-tool",
                    htmlUrl: "https://github.com/demo/rust-cli-tool",
                    healthScore: 6,
                    showcaseScore: 5,
                    healthLevel: "ARCHIVE_CANDIDATE",
                    reason: "没有 README，无法判断用途",
                    suggestion: "建议补充 README，否则归档"
                },
                {
                    repositoryName: "algorithm-practice",
                    htmlUrl: "https://github.com/demo/algorithm-practice",
                    healthScore: 30,
                    showcaseScore: 15,
                    healthLevel: "ARCHIVE_CANDIDATE",
                    reason: "刷题笔记，缺乏结构和说明",
                    suggestion: "建议整理到统一仓库或归档"
                }
            ],
            notesOrPractice: [
                {
                    repositoryName: "algorithm-practice",
                    htmlUrl: "https://github.com/demo/algorithm-practice",
                    healthScore: 30,
                    showcaseScore: 15,
                    healthLevel: "ARCHIVE_CANDIDATE",
                    reason: "刷题仓库",
                    suggestion: "建议整理到统一仓库"
                }
            ],
            priorityActions: [
                "P0：删除或归档空仓库（empty-repo-test、rust-cli-tool）",
                "P1：给重点项目（dev-env-manager）补充运行方式",
                "P2：给重点项目（spring-boot-employee-system）补充 README",
                "P3：给重点项目（skygrid-uav-scheduler）补充测试说明",
                "P4：整理 algorithm-practice 到统一仓库"
            ]
        }
    };

    // Mock report
    const mockReport = `# GitHub Portfolio Report: demo-user

## 1. Overview

- **GitHub Username**: demo-user
- **Total Repositories**: 8
- **Primary Showcase**: 2
- **Secondary Showcase**: 1
- **Needs Improvement**: 2
- **Archive Candidates**: 3
- **Top Languages**: Java (41.6%), Python (22.4%), TypeScript (14.4%), Rust (9.6%), HTML (6.0%)
- **Top Tech Stacks**: Java, Spring Boot, Maven, Docker, Python

## 2. Language Composition

| Language | Percentage | Bytes |
|---|---:|---:|
| Java | 41.6% | 520000 |
| Python | 22.4% | 280000 |
| TypeScript | 14.4% | 180000 |
| Rust | 9.6% | 120000 |
| HTML | 6.0% | 75000 |
| CSS | 3.6% | 45000 |
| JavaScript | 2.4% | 30000 |

## 3. Tech Stack Profile

- **spring-boot-rag-service**: Java, Spring Boot, Maven, Docker, Ollama, RAG
- **skygrid-uav-scheduler**: Python, FastAPI, Docker
- **dev-env-manager**: Java, Spring Boot, Maven, TypeScript, Vue
- **spring-boot-employee-system**: Java, Spring Boot, Maven
- **python-data-analysis**: Python
- **rust-cli-tool**: Rust
- **empty-repo-test**: No specific stacks detected
- **algorithm-practice**: No specific stacks detected

## 4. Repository Health Ranking

| Repository | Health Score | Level | Main Problems |
|---|---:|---|---|
| [spring-boot-rag-service](https://github.com/demo/spring-boot-rag-service) | 92 | EXCELLENT | None |
| [skygrid-uav-scheduler](https://github.com/demo/skygrid-uav-scheduler) | 88 | GOOD | 缺少测试说明 |
| [dev-env-manager](https://github.com/demo/dev-env-manager) | 76 | GOOD | 缺少运行方式, 缺少截图 |
| [spring-boot-employee-system](https://github.com/demo/spring-boot-employee-system) | 68 | FAIR | README 较短, 缺少运行方式, 缺少测试说明 |
| [python-data-analysis](https://github.com/demo/python-data-analysis) | 55 | INCOMPLETE | README 不完整, 缺少运行方式, 缺少测试说明 |
| [algorithm-practice](https://github.com/demo/algorithm-practice) | 30 | ARCHIVE_CANDIDATE | README 缺失, 仓库 size 很小 |
| [rust-cli-tool](https://github.com/demo/rust-cli-tool) | 6 | ARCHIVE_CANDIDATE | 没有 README |
| [empty-repo-test](https://github.com/demo/empty-repo-test) | 5 | ARCHIVE_CANDIDATE | 仓库为空, 没有 README |

## 5. Resume Showcase Recommendation

| Repository | Showcase Score | Recommendation |
|---|---:|---|
| [spring-boot-rag-service](https://github.com/demo/spring-boot-rag-service) | 92 | PRIMARY_SHOWCASE |
| [skygrid-uav-scheduler](https://github.com/demo/skygrid-uav-scheduler) | 90 | PRIMARY_SHOWCASE |
| [dev-env-manager](https://github.com/demo/dev-env-manager) | 72 | SECONDARY_SHOWCASE |
| [spring-boot-employee-system](https://github.com/demo/spring-boot-employee-system) | 45 | KEEP |
| [python-data-analysis](https://github.com/demo/python-data-analysis) | 35 | NOT_FOR_RESUME |
| [algorithm-practice](https://github.com/demo/algorithm-practice) | 15 | ARCHIVE_CANDIDATE |
| [rust-cli-tool](https://github.com/demo/rust-cli-tool) | 5 | ARCHIVE_CANDIDATE |
| [empty-repo-test](https://github.com/demo/empty-repo-test) | 0 | ARCHIVE_CANDIDATE |

## 6. Archive Candidates

| Repository | Reason | Suggested Action |
|---|---|---|
| empty-repo-test | 空仓库 | 删除或归档 |
| rust-cli-tool | 没有 README | 补充 README，否则归档 |
| algorithm-practice | 刷题笔记 | 整理到统一仓库 |

## 7. Improvement Plan

- P0：删除或归档空仓库（empty-repo-test、rust-cli-tool）
- P1：给重点项目（dev-env-manager）补充运行方式
- P2：给重点项目（spring-boot-employee-system）补充 README
- P3：给重点项目（skygrid-uav-scheduler）补充测试说明
- P4：整理 algorithm-practice 到统一仓库
- P5：整理 GitHub Profile README

## 8. Resume Description Draft

- RepoHealth-Lite Plus：GitHub 作品集体检与语言构成分析工具
- 基于 Spring Boot 实现 GitHub 公开仓库分析服务，支持仓库列表拉取、README 完整度检测、语言构成统计和技术栈识别。
- 设计仓库健康度评分与简历展示价值评分规则，从文档完整度、运行说明、测试说明、更新时间、项目方向等维度识别成品项目、半成品项目和疑似烂尾项目。`;

    // Show demo data
    allHealthResults = mockHealth.data.results;
    currentReportMarkdown = mockReport;

    renderOverview(mockHealth.data);
    renderLanguages(mockLanguages);
    renderHealth(mockHealth.data);
    renderTechStack(mockTechStack);
    renderPortfolio(mockPortfolio);
    document.getElementById('report-content').textContent = mockReport;

    hideLoading();
    showResults();
    switchTab('overview');
}
