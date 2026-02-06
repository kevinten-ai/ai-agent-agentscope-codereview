package com.brag.codereview.service;

import io.agentscope.core.ReActAgent;
import io.agentscope.core.message.Msg;
import io.agentscope.core.message.MsgRole;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * 代码审查Agent服务类
 * 提供代码审查的核心功能
 */
@Slf4j
@Service
public class CodeReviewAgentService {

    private static final Logger log = LoggerFactory.getLogger(CodeReviewAgentService.class);

    private final ReActAgent codeReviewAgent;

    public CodeReviewAgentService(ReActAgent codeReviewAgent) {
        this.codeReviewAgent = codeReviewAgent;
    }

    /**
     * 执行代码审查
     *
     * @param filePath 要审查的文件路径
     * @return 审查报告
     */
    public Mono<String> reviewCode(String filePath) {
        return Mono.fromCallable(() -> {
            try {
                // 读取文件内容
                String codeContent = Files.readString(Paths.get(filePath));

                // 构建审查提示
                String reviewPrompt = buildReviewPrompt(filePath, codeContent);

                // 创建消息
                Msg reviewMessage = Msg.builder()
                    .role(MsgRole.USER)
                    .textContent(reviewPrompt)
                    .build();

                // 执行审查
                Msg response = codeReviewAgent.call(reviewMessage).block();

                if (response != null) {
                    return formatReviewResult(filePath, response.getTextContent());
                } else {
                    return "代码审查失败：未收到Agent响应";
                }

            } catch (Exception e) {
                log.error("代码审查失败", e);
                return "代码审查失败: " + e.getMessage();
            }
        });
    }

    /**
     * 执行安全审查
     *
     * @param filePath 要审查的文件路径
     * @return 安全审查报告
     */
    public Mono<String> reviewSecurity(String filePath) {
        return Mono.fromCallable(() -> {
            try {
                String codeContent = Files.readString(Paths.get(filePath));

                String securityPrompt = String.format("""
                        请对以下代码进行安全审查，重点关注：

                        1. SQL注入漏洞
                        2. XSS攻击
                        3. 硬编码凭据
                        4. 不安全的随机数生成
                        5. 命令注入
                        6. 敏感数据泄露

                        代码文件: %s

                        代码内容:
                        ```java
                        %s
                        ```

                        请使用安全扫描工具进行全面检查，并提供修复建议。
                        """, filePath, codeContent);

                Msg securityMessage = Msg.builder()
                    .role(MsgRole.USER)
                    .textContent(securityPrompt)
                    .build();

                Msg response = codeReviewAgent.call(securityMessage).block();

                return response != null ? response.getTextContent() : "安全审查失败";

            } catch (Exception e) {
                log.error("安全审查失败", e);
                return "安全审查失败: " + e.getMessage();
            }
        });
    }

    /**
     * 执行性能分析
     *
     * @param filePath 要分析的文件路径
     * @return 性能分析报告
     */
    public Mono<String> analyzePerformance(String filePath) {
        return Mono.fromCallable(() -> {
            try {
                String codeContent = Files.readString(Paths.get(filePath));

                String performancePrompt = String.format("""
                        请对以下代码进行性能分析，重点关注：

                        1. 算法时间复杂度
                        2. 内存使用效率
                        3. 数据库查询优化
                        4. 资源管理
                        5. 循环和递归效率

                        代码文件: %s

                        代码内容:
                        ```java
                        %s
                        ```

                        请使用性能分析工具进行全面检查，并提供优化建议。
                        """, filePath, codeContent);

                Msg performanceMessage = Msg.builder()
                    .role(MsgRole.USER)
                    .textContent(performancePrompt)
                    .build();

                Msg response = codeReviewAgent.call(performanceMessage).block();

                return response != null ? response.getTextContent() : "性能分析失败";

            } catch (Exception e) {
                log.error("性能分析失败", e);
                return "性能分析失败: " + e.getMessage();
            }
        });
    }

    /**
     * 检查代码质量
     *
     * @param filePath 要检查的文件路径
     * @return 质量检查报告
     */
    public Mono<String> checkCodeQuality(String filePath) {
        return Mono.fromCallable(() -> {
            try {
                String codeContent = Files.readString(Paths.get(filePath));

                String qualityPrompt = String.format("""
                        请对以下代码进行质量检查，重点关注：

                        1. 代码风格和规范
                        2. 命名规范
                        3. 代码结构
                        4. 注释完整性
                        5. 测试覆盖率

                        代码文件: %s

                        代码内容:
                        ```java
                        %s
                        ```

                        请使用相关工具进行全面质量检查。
                        """, filePath, codeContent);

                Msg qualityMessage = Msg.builder()
                    .role(MsgRole.USER)
                    .textContent(qualityPrompt)
                    .build();

                Msg response = codeReviewAgent.call(qualityMessage).block();

                return response != null ? response.getTextContent() : "质量检查失败";

            } catch (Exception e) {
                log.error("质量检查失败", e);
                return "质量检查失败: " + e.getMessage();
            }
        });
    }

    /**
     * 生成审查报告
     *
     * @param filePath 文件路径
     * @param reviewType 审查类型
     * @return 综合报告
     */
    public Mono<String> generateComprehensiveReport(String filePath, String reviewType) {
        return Mono.fromCallable(() -> {
            try {
                String codeContent = Files.readString(Paths.get(filePath));

                String reportPrompt = String.format("""
                        请为以下代码生成%s审查综合报告：

                        代码文件: %s

                        代码内容:
                        ```java
                        %s
                        ```

                        请综合运用所有可用的审查工具，生成详细的审查报告，包括：
                        1. 问题发现和严重程度评估
                        2. 具体改进建议
                        3. 优先级排序
                        4. 代码质量评分

                        报告格式要求：
                        - 使用markdown格式
                        - 问题分类整理
                        - 提供可操作的修复建议
                        """, reviewType, filePath, codeContent);

                Msg reportMessage = Msg.builder()
                    .role(MsgRole.USER)
                    .textContent(reportPrompt)
                    .build();

                Msg response = codeReviewAgent.call(reportMessage).block();

                return response != null ? formatComprehensiveReport(response.getTextContent()) : "报告生成失败";

            } catch (Exception e) {
                log.error("综合报告生成失败", e);
                return "综合报告生成失败: " + e.getMessage();
            }
        });
    }

    private String buildReviewPrompt(String filePath, String codeContent) {
        return String.format("""
                请对以下Java代码进行全面的代码审查：

                文件路径: %s

                代码内容:
                ```java
                %s
                ```

                请从以下几个方面进行审查：

                1. **代码结构分析**
                   - 分析代码的整体结构
                   - 计算圈复杂度
                   - 评估可维护性

                2. **安全漏洞扫描**
                   - 检查SQL注入风险
                   - 检测XSS漏洞
                   - 查找硬编码凭据
                   - 分析命令注入风险

                3. **性能分析**
                   - 评估算法复杂度
                   - 检查内存使用
                   - 分析数据库查询效率

                4. **代码质量检查**
                   - 验证命名规范
                   - 检查代码风格
                   - 评估注释完整性

                5. **测试覆盖分析**
                   - 分析单元测试覆盖率
                   - 提供测试建议

                请使用相应的工具进行深入分析，并提供具体的改进建议。
                """, filePath, codeContent);
    }

    private String formatReviewResult(String filePath, String rawResult) {
        StringBuilder formatted = new StringBuilder();
        formatted.append("🔍 代码审查报告\n");
        formatted.append("📁 文件: ").append(filePath).append("\n");
        formatted.append("⏰ 审查时间: ").append(java.time.LocalDateTime.now()).append("\n\n");
        formatted.append(rawResult);
        return formatted.toString();
    }

    private String formatComprehensiveReport(String rawResult) {
        StringBuilder formatted = new StringBuilder();
        formatted.append("📊 代码审查综合报告\n");
        formatted.append("=".repeat(50)).append("\n\n");
        formatted.append(rawResult);
        formatted.append("\n\n");
        formatted.append("=".repeat(50)).append("\n");
        formatted.append("报告生成时间: ").append(java.time.LocalDateTime.now()).append("\n");
        return formatted.toString();
    }
}