package com.brag.codereview.config;

import io.agentscope.core.model.DashScopeChatModel;
import io.agentscope.core.model.GenerateOptions;
import io.agentscope.core.tool.Toolkit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * AgentScope配置类
 */
@Configuration
public class AgentScopeConfig {

    @Value("${agentscope.model.api-key:}")
    private String apiKey;

    @Value("${agentscope.model.model-name:qwen-plus}")
    private String modelName;

    @Value("${agentscope.model.stream:true}")
    private boolean stream;

    @Value("${agentscope.model.enable-thinking:true}")
    private boolean enableThinking;

    @Value("${agentscope.model.temperature:0.7}")
    private double temperature;

    @Value("${agentscope.model.max-tokens:4000}")
    private int maxTokens;

    @Value("${agentscope.model.thinking-budget:2048}")
    private int thinkingBudget;

    /**
     * 配置DashScope模型
     */
    @Bean
    public DashScopeChatModel dashScopeChatModel() {
        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new IllegalArgumentException("DASHSCOPE_API_KEY is required. Please set it in environment variables or application.yml");
        }

        return DashScopeChatModel.builder()
                .apiKey(apiKey)
                .modelName(modelName)
                .stream(stream)
                .enableThinking(enableThinking)
                .defaultOptions(GenerateOptions.builder()
                        .thinkingBudget(thinkingBudget)
                        .build())
                .build();
    }

    /**
     * 配置工具包
     */
    @Bean
    public Toolkit toolkit() {
        return new Toolkit();
    }
}