package com.brag.codereview.config;

import com.brag.codereview.tool.*;
import io.agentscope.core.tool.Toolkit;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * 代码审查工具配置类
 */
@Configuration
public class ToolConfig {

    private final Toolkit toolkit;
    private final CodeAnalysisTool codeAnalysisTool;
    private final SecurityScanTool securityScanTool;
    private final PerformanceAnalysisTool performanceAnalysisTool;
    private final CodeStyleCheckTool codeStyleCheckTool;
    private final DocumentationCheckTool documentationCheckTool;
    private final TestCoverageTool testCoverageTool;

    public ToolConfig(Toolkit toolkit,
                     CodeAnalysisTool codeAnalysisTool,
                     SecurityScanTool securityScanTool,
                     PerformanceAnalysisTool performanceAnalysisTool,
                     CodeStyleCheckTool codeStyleCheckTool,
                     DocumentationCheckTool documentationCheckTool,
                     TestCoverageTool testCoverageTool) {
        this.toolkit = toolkit;
        this.codeAnalysisTool = codeAnalysisTool;
        this.securityScanTool = securityScanTool;
        this.performanceAnalysisTool = performanceAnalysisTool;
        this.codeStyleCheckTool = codeStyleCheckTool;
        this.documentationCheckTool = documentationCheckTool;
        this.testCoverageTool = testCoverageTool;
    }

    /**
     * 初始化工具注册
     */
    @PostConstruct
    public void initializeTools() {
        // 注册所有代码审查工具
        toolkit.registerTool(codeAnalysisTool);
        toolkit.registerTool(securityScanTool);
        toolkit.registerTool(performanceAnalysisTool);
        toolkit.registerTool(codeStyleCheckTool);
        toolkit.registerTool(documentationCheckTool);
        toolkit.registerTool(testCoverageTool);
    }
}