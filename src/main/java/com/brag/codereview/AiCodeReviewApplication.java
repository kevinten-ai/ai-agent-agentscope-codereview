package com.brag.codereview;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * AI 代码审查 Agent 主应用类
 *
 * 基于AgentScope框架实现的智能代码审查系统
 */
@SpringBootApplication
public class AiCodeReviewApplication {

    public static void main(String[] args) {
        SpringApplication.run(AiCodeReviewApplication.class, args);
    }
}