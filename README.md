# AI Agent - CodeReview AgentScope 实现

基于AgentScope框架实现的智能代码审查AI Agent项目，提供全面的代码质量分析、安全漏洞扫描、性能优化建议等功能。

## 功能特性

- ✅ 基于ReAct推理-行动模式的智能代码审查
- ✅ 全面代码质量分析（结构、复杂度、可维护性）
- ✅ 安全漏洞检测（SQL注入、XSS、硬编码凭据等）
- ✅ 性能问题分析（算法复杂度、内存使用、数据库查询）
- ✅ 代码规范检查（命名、风格、格式化）
- ✅ 文档完整性评估
- ✅ 测试覆盖率分析和建议
- ✅ 声明式工具定义和动态注册
- ✅ 支持DashScope Qwen等多模型集成
- ✅ 异步工具调用和响应式编程
- ✅ 流式响应和实时交互

## 项目结构

```
ai-agent-agentscope-codereview/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/brag/codereview/
│   │   │       ├── agent/          # Agent实现（预留）
│   │   │       ├── tool/           # 代码审查工具
│   │   │       │   ├── CodeAnalysisTool.java       # 代码结构分析
│   │   │       │   ├── SecurityScanTool.java       # 安全漏洞扫描
│   │   │       │   ├── PerformanceAnalysisTool.java # 性能分析
│   │   │       │   ├── CodeStyleCheckTool.java     # 代码风格检查
│   │   │       │   ├── DocumentationCheckTool.java # 文档检查
│   │   │       │   └── TestCoverageTool.java       # 测试覆盖分析
│   │   │       ├── config/         # 配置类
│   │   │       │   ├── AgentScopeConfig.java
│   │   │       │   ├── ToolConfig.java
│   │   │       │   └── CodeReviewConfig.java
│   │   │       ├── service/        # 业务服务
│   │   │       │   └── CodeReviewAgentService.java
│   │   │       ├── controller/     # REST控制器（预留）
│   │   │       ├── examples/       # 示例代码
│   │   │       │   └── CodeReviewExample.java
│   │   │       └── util/           # 工具类（预留）
│   │   └── resources/
│   │       └── application.yml
│   └── test/
│       └── java/
│           └── com/brag/codereview/
├── examples/                       # 示例代码
├── docs/                          # 项目文档
├── pom.xml                        # Maven配置
└── README.md
```

## 快速开始

### 环境要求

- JDK 21+ (推荐使用LTS版本)
- Maven 3.6+
- AgentScope Java 1.0.5

### 配置环境变量

#### 方法1：设置环境变量（推荐）

**Windows系统：**
```cmd
# 命令行设置（临时）
set DASHSCOPE_API_KEY=your-api-key-here

# PowerShell设置（临时）
$env:DASHSCOPE_API_KEY="your-api-key-here"

# 永久设置：系统属性 -> 环境变量 -> 用户变量 -> 新建
# 变量名：DASHSCOPE_API_KEY
# 变量值：your-api-key-here
```

**Linux/Mac系统：**
```bash
# 临时设置
export DASHSCOPE_API_KEY="your-api-key-here"

# 永久设置：添加到 ~/.bashrc 或 ~/.zshrc
echo 'export DASHSCOPE_API_KEY="your-api-key-here"' >> ~/.bashrc
source ~/.bashrc
```

#### 方法2：使用配置文件

1. **复制环境变量模板：**
   ```bash
   cp config/env-template.txt .env
   # 编辑 .env 文件，填入您的API密钥
   ```

2. **使用开发环境配置文件：**
   - 应用已自动创建 `application-development.yml`
   - 编辑该文件，将 `your-development-api-key-here` 替换为您的真实API密钥

#### 获取DashScope API密钥

1. 访问 [DashScope官网](https://dashscope.aliyun.com/)
2. 注册/登录账号
3. 在控制台获取API Key
4. 妥善保管API密钥，不要泄露给他人

#### 方法3：IDE中配置环境变量

详细的IDE配置指南请参考：[config/ide-setup-guide.md](config/ide-setup-guide.md)

#### 验证配置

```bash
# 验证环境变量是否设置成功
echo $DASHSCOPE_API_KEY  # Linux/Mac
echo %DASHSCOPE_API_KEY%  # Windows CMD

# 测试应用启动
mvn spring-boot:run
```

### 环境验证

运行验证脚本：
```bash
# Windows
verify_jdk21.bat

# Linux/Mac
./verify_jdk21.sh
```

### 运行示例

```bash
# 克隆项目
git clone <repository-url>
cd ai-agent-agentscope-codereview

# 编译项目
mvn clean compile

# 运行示例代码审查
mvn exec:java -Dexec.mainClass="com.brag.codereview.examples.CodeReviewExample"

# 审查指定文件
mvn exec:java -Dexec.mainClass="com.brag.codereview.examples.CodeReviewExample" -Dexec.args="/path/to/your/code.java"
```

## 核心组件

### 代码审查工具

#### CodeAnalysisTool - 代码结构分析
```java
@Service
public class CodeAnalysisTool {
    @Tool(description = "分析代码文件的整体结构和质量指标")
    public String analyzeCodeStructure(@ToolParam(name = "filePath") String filePath)
}
```

#### SecurityScanTool - 安全漏洞扫描
```java
@Service
public class SecurityScanTool {
    @Tool(description = "扫描代码中的安全漏洞和风险")
    public String scanSecurityVulnerabilities(@ToolParam(name = "filePath") String filePath)
}
```

#### PerformanceAnalysisTool - 性能分析
```java
@Service
public class PerformanceAnalysisTool {
    @Tool(description = "分析代码中的性能问题")
    public String analyzePerformanceIssues(@ToolParam(name = "filePath") String filePath)
}
```

#### 其他工具
- **CodeStyleCheckTool**: 代码风格和规范检查
- **DocumentationCheckTool**: 文档完整性检查
- **TestCoverageTool**: 测试覆盖率分析

### CodeReviewAgentService - 审查服务

```java
@Service
public class CodeReviewAgentService {

    @Autowired
    private ReActAgent codeReviewAgent;

    // 全面代码审查
    public Mono<String> reviewCode(String filePath)

    // 安全审查
    public Mono<String> reviewSecurity(String filePath)

    // 性能分析
    public Mono<String> analyzePerformance(String filePath)

    // 质量检查
    public Mono<String> checkCodeQuality(String filePath)

    // 生成综合报告
    public Mono<String> generateComprehensiveReport(String filePath, String reviewType)
}
```

## 配置说明

### application.yml

```yaml
agentscope:
  model:
    provider: dashscope
    api-key: ${DASHSCOPE_API_KEY}
    model-name: qwen-plus
    stream: true
    enable-thinking: true
    temperature: 0.7
    max-tokens: 4000

  agent:
    max-iters: 10
    memory-type: in-memory
    memory-max-length: 200

  tools:
    groups:
      - name: code_analysis
        description: 代码结构和质量分析
        active: true
      - name: security_scan
        description: 安全漏洞扫描
        active: true

  codereview:
    max-file-size: 1048576  # 1MB
    supported-extensions: [java, py, js, ts]
    enable-security-scan: true
    enable-performance-analysis: true
    enable-quality-check: true
```

## 使用示例

### 1. 基本代码审查

```java
@Autowired
private CodeReviewAgentService reviewService;

// 审查Java文件
String result = reviewService.reviewCode("/path/to/JavaFile.java")
    .block();

System.out.println(result);
```

### 2. 专项安全审查

```java
// 仅进行安全漏洞扫描
String securityResult = reviewService.reviewSecurity("/path/to/JavaFile.java")
    .block();
```

### 3. 性能分析

```java
// 分析性能问题
String performanceResult = reviewService.analyzePerformance("/path/to/JavaFile.java")
    .block();
```

### 4. 生成综合报告

```java
// 生成完整审查报告
String report = reviewService.generateComprehensiveReport("/path/to/JavaFile.java", "完整审查")
    .block();
```

## 审查报告示例

```
🔍 代码审查报告
📁 文件: src/main/java/com/example/MyClass.java
⏰ 审查时间: 2024-01-06T23:30:00

📊 代码结构分析报告
==================
文件名: MyClass.java
文件大小: 2048 bytes
总行数: 156
类数量: 1
方法数量: 8
注释行数: 23
圈复杂度: 12
可维护性指数: 78.5

🚨 安全扫描报告
==================
⚠️ 潜在SQL注入风险: 检测到字符串拼接的SQL语句
🚨 安全风险: 检测到硬编码的密码

💡 修复建议:
1. 使用PreparedStatement替代字符串拼接
2. 将硬编码密码移至配置文件
```

## 开发指南

### 添加新审查工具

1. 创建工具类并继承基本功能
2. 使用`@Tool`和`@ToolParam`注解定义工具接口
3. 在`ToolConfig.java`中注册新工具
4. 更新Agent系统提示词

### 自定义审查规则

1. 修改配置文件中的审查参数
2. 扩展现有工具的检查逻辑
3. 添加新的检查模式和规则

### 集成其他语言支持

1. 在`supported-extensions`中添加新语言
2. 实现对应语言的解析器
3. 添加语言特定的检查规则

## 测试

```bash
# 运行单元测试
mvn test

# 运行集成测试
mvn verify

# 生成测试覆盖率报告
mvn test jacoco:report
```

## 文档

- [AgentScope框架调研报告](../../agentscope/AgentScope框架调研报告.md)
- [AgentScope核心组件使用指南](../../agentscope/AgentScope核心组件使用指南.md)
- [AgentScope工具系统使用指南](../../agentscope/AgentScope工具系统使用指南.md)

## 贡献

欢迎提交Issue和Pull Request来改进项目。请确保：

1. 遵循现有的代码风格
2. 添加相应的单元测试
3. 更新文档
4. 通过所有测试

## 许可证

[MIT License](LICENSE)