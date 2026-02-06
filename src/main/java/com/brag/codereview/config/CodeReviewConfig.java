package com.brag.codereview.config;

import io.agentscope.core.ReActAgent;
import io.agentscope.core.memory.InMemoryMemory;
import io.agentscope.core.model.DashScopeChatModel;
import io.agentscope.core.tool.Toolkit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 代码审查Agent配置类
 */
@Configuration
public class CodeReviewConfig {

    @Value("${agentscope.agent.max-iters:10}")
    private int maxIters;

    @Value("${agentscope.agent.memory-max-length:200}")
    private int memoryMaxLength;

    /**
     * 配置代码审查Agent
     */
    @Bean
    public ReActAgent codeReviewAgent(DashScopeChatModel chatModel, Toolkit toolkit) {
        String systemPrompt = """
                你是一个专业的代码审查AI助手，具备以下能力：

                ## 核心职责
                - 分析代码质量和潜在问题
                - 识别安全漏洞和风险
                - 检查代码风格和最佳实践
                - 评估性能问题
                - 验证测试覆盖率
                - 检查文档完整性

                ## 审查标准
                1. **代码质量**: 代码结构清晰度、可维护性、可读性
                2. **安全性**: SQL注入、XSS、CSRF等安全漏洞
                3. **性能**: 算法复杂度、内存泄漏、资源管理
                4. **规范性**: 命名规范、代码风格、注释规范
                5. **测试**: 单元测试覆盖率、测试质量
                6. **文档**: API文档、代码注释、README

                ## 审查流程
                1. 首先分析代码结构和功能
                2. 使用相关工具进行深入检查
                3. 生成详细的审查报告
                4. 提供具体的改进建议

                请使用工具进行全面的代码分析，并给出建设性的反馈。
                """;

        return ReActAgent.builder()
                .name("CodeReviewAgent")
                .sysPrompt(systemPrompt)
                .model(chatModel)
                .toolkit(toolkit)
                .memory(new InMemoryMemory())
                .build();
    }
}