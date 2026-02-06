# IDE运行配置指南

## IntelliJ IDEA

### 方法1：运行配置中设置环境变量

1. 打开项目，点击 `Run` -> `Edit Configurations`
2. 选择或创建 `Application` 配置
3. 在 `Environment variables` 字段中添加：
   ```
   DASHSCOPE_API_KEY=your-api-key-here
   ```
4. 点击 `OK` 保存配置

### 方法2：使用.env文件支持插件

1. 安装插件：`File` -> `Settings` -> `Plugins` -> 搜索安装 "Env File"
2. 在运行配置中启用：`Run` -> `Edit Configurations`
3. 选择配置，勾选 `Enable EnvFile`
4. 添加 `.env` 文件路径

## VS Code

### 使用launch.json配置

创建 `.vscode/launch.json`：

```json
{
    "version": "0.2.0",
    "configurations": [
        {
            "type": "java",
            "name": "Launch CodeReview Application",
            "request": "launch",
            "mainClass": "com.brag.codereview.AiCodeReviewApplication",
            "projectName": "ai-agent-agentscope-codereview",
            "env": {
                "DASHSCOPE_API_KEY": "your-api-key-here"
            }
        }
    ]
}
```

## Eclipse

1. 右键项目 -> `Run As` -> `Run Configurations`
2. 选择 `Java Application` -> `Environment` 标签
3. 点击 `Add` 添加环境变量：
   - Name: `DASHSCOPE_API_KEY`
   - Value: `your-api-key-here`
4. 点击 `Run`

## 命令行运行

### Windows
```cmd
set DASHSCOPE_API_KEY=your-api-key-here
mvn spring-boot:run
```

### Linux/Mac
```bash
export DASHSCOPE_API_KEY="your-api-key-here"
mvn spring-boot:run
```

## Docker运行

如果使用Docker，可以在docker-compose.yml中设置：

```yaml
version: '3.8'
services:
  codereview:
    build: .
    environment:
      - DASHSCOPE_API_KEY=your-api-key-here
    ports:
      - "8081:8081"
```

## 故障排除

### 常见问题

1. **环境变量不生效**
   - 检查变量名是否正确（`DASHSCOPE_API_KEY`）
   - 重启IDE或终端
   - 使用 `echo $DASHSCOPE_API_KEY` 验证

2. **API密钥无效**
   - 确认密钥来自 [DashScope官网](https://dashscope.aliyun.com/)
   - 检查密钥是否过期或失效

3. **权限问题**
   - 确保有网络访问权限
   - 检查防火墙设置

### 调试技巧

- 在 `application-development.yml` 中启用详细日志：
  ```yaml
  logging:
    level:
      com.brag.codereview: DEBUG
      io.agentscope: DEBUG
  ```

- 使用Spring Boot Actuator检查配置：
  - 访问：`http://localhost:8081/actuator/configprops`