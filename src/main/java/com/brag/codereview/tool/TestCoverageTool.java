package com.brag.codereview.tool;

import io.agentscope.core.tool.Tool;
import io.agentscope.core.tool.ToolParam;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;

/**
 * 测试覆盖率工具
 * 分析代码的测试覆盖率和测试质量
 */
@Slf4j
@Service
public class TestCoverageTool {

    private static final Logger log = LoggerFactory.getLogger(TestCoverageTool.class);

    private static final Pattern METHOD_PATTERN = Pattern.compile("\\b(public|private|protected)?\\s*\\w+\\s+\\w+\\s*\\([^)]*\\)\\s*\\{");
    private static final Pattern CONDITIONAL_PATTERN = Pattern.compile("\\b(if|while|for|switch)\\s*\\(");
    private static final Pattern EXCEPTION_PATTERN = Pattern.compile("\\bthrow\\s+new\\s+\\w+");

    @Tool(description = "分析代码的测试覆盖情况")
    public String analyzeTestCoverage(@ToolParam(name = "sourcePath", description = "源代码文件路径") String sourcePath,
                                    @ToolParam(name = "testPath", description = "测试文件路径（可选）") String testPath) {
        try {
            String sourceCode = Files.readString(Paths.get(sourcePath));
            String testCode = "";

            if (testPath != null && !testPath.trim().isEmpty()) {
                try {
                    testCode = Files.readString(Paths.get(testPath));
                } catch (Exception e) {
                    // 测试文件不存在，继续分析
                }
            }

            Map<String, Object> analysis = analyzeCoverage(sourceCode, testCode);

            StringBuilder result = new StringBuilder();
            result.append("🧪 测试覆盖率分析报告\n\n");

            result.append("=== 代码复杂度分析 ===\n");
            result.append("方法数量: ").append(analysis.get("methodCount")).append("\n");
            result.append("分支数量: ").append(analysis.get("branchCount")).append("\n");
            result.append("异常处理点: ").append(analysis.get("exceptionCount")).append("\n\n");

            result.append("=== 测试覆盖分析 ===\n");
            result.append("测试文件: ").append(testPath != null ? "已提供" : "未提供").append("\n");
            result.append("测试方法数: ").append(analysis.get("testMethodCount")).append("\n");
            result.append("覆盖率估计: ").append(analysis.get("estimatedCoverage")).append("%\n\n");

            List<String> recommendations = (List<String>) analysis.get("recommendations");
            if (!recommendations.isEmpty()) {
                result.append("=== 测试建议 ===\n");
                for (int i = 0; i < recommendations.size(); i++) {
                    result.append(i + 1).append(". ").append(recommendations.get(i)).append("\n");
                }
            }

            return result.toString();

        } catch (Exception e) {
            log.error("测试覆盖率分析失败", e);
            return "测试覆盖率分析失败: " + e.getMessage();
        }
    }

    @Tool(description = "生成单元测试建议")
    public String generateUnitTestSuggestions(@ToolParam(name = "code", description = "要生成测试的代码片段") String code) {
        List<String> suggestions = new ArrayList<>();

        // 分析方法
        var methodMatcher = METHOD_PATTERN.matcher(code);
        while (methodMatcher.find()) {
            String methodSignature = extractMethodSignature(code, methodMatcher.start());
            if (methodSignature != null) {
                suggestions.add("为方法 '" + methodSignature + "' 生成单元测试");
            }
        }

        // 检查边界条件
        if (code.contains("if") || code.contains("switch")) {
            suggestions.add("测试边界条件和异常情况");
        }

        // 检查异常处理
        if (code.contains("throw") || code.contains("catch")) {
            suggestions.add("测试异常处理逻辑");
        }

        // 检查依赖注入
        if (code.contains("@Autowired") || code.contains("new ") && code.contains("(")) {
            suggestions.add("使用Mockito等框架mock依赖对象");
        }

        StringBuilder result = new StringBuilder();
        result.append("单元测试生成建议:\n\n");
        for (int i = 0; i < suggestions.size(); i++) {
            result.append(i + 1).append(". ").append(suggestions.get(i)).append("\n");
        }

        result.append("\n📝 测试模板:\n");
        result.append("""
                @Test
                public void testMethodName() {
                    // Given
                    // 准备测试数据和mock对象

                    // When
                    // 执行被测试的方法

                    // Then
                    // 验证结果
                    assertThat(result).isEqualTo(expected);
                }
                """);

        return result.toString();
    }

    @Tool(description = "分析测试质量")
    public String analyzeTestQuality(@ToolParam(name = "testCode", description = "测试代码内容") String testCode) {
        List<String> issues = new ArrayList<>();
        List<String> strengths = new ArrayList<>();

        // 检查测试结构
        if (!testCode.contains("@Test")) {
            issues.add("缺少@Test注解的测试方法");
        } else {
            strengths.add("使用了JUnit测试框架");
        }

        // 检查断言
        if (!testCode.contains("assert") && !testCode.contains("Assert.")) {
            issues.add("缺少断言语句，测试无法验证结果");
        } else {
            strengths.add("包含断言验证逻辑");
        }

        // 检查异常测试
        if (testCode.contains("expected =")) {
            strengths.add("包含异常测试");
        }

        // 检查测试命名
        if (testCode.contains("test") || testCode.contains("Test")) {
            strengths.add("测试方法命名规范");
        }

        // 检查边界值测试
        String[] boundaryValues = {"0", "-1", "null", "Integer.MAX_VALUE"};
        boolean hasBoundaryTest = false;
        for (String boundary : boundaryValues) {
            if (testCode.contains(boundary)) {
                hasBoundaryTest = true;
                break;
            }
        }
        if (hasBoundaryTest) {
            strengths.add("包含边界值测试");
        } else {
            issues.add("建议增加边界值测试");
        }

        StringBuilder result = new StringBuilder();
        result.append("🧪 测试质量分析\n\n");

        if (!strengths.isEmpty()) {
            result.append("✅ 测试优点:\n");
            for (String strength : strengths) {
                result.append("- ").append(strength).append("\n");
            }
            result.append("\n");
        }

        if (!issues.isEmpty()) {
            result.append("⚠️ 需要改进:\n");
            for (String issue : issues) {
                result.append("- ").append(issue).append("\n");
            }
        }

        return result.toString();
    }

    private Map<String, Object> analyzeCoverage(String sourceCode, String testCode) {
        Map<String, Object> result = new HashMap<>();

        // 分析源代码复杂度
        int methodCount = countMatches(sourceCode, METHOD_PATTERN);
        int branchCount = countMatches(sourceCode, CONDITIONAL_PATTERN);
        int exceptionCount = countMatches(sourceCode, EXCEPTION_PATTERN);

        // 分析测试代码
        int testMethodCount = testCode.isEmpty() ? 0 : countMatches(testCode, Pattern.compile("@Test"));

        // 估算覆盖率
        double estimatedCoverage = 0.0;
        if (methodCount > 0) {
            estimatedCoverage = Math.min(100.0, (double) testMethodCount / methodCount * 100);
        }

        // 生成建议
        List<String> recommendations = new ArrayList<>();
        if (estimatedCoverage < 70) {
            recommendations.add("建议提高单元测试覆盖率，至少达到70%");
        }
        if (branchCount > 0 && !testCode.contains("assert")) {
            recommendations.add("为分支条件编写测试用例");
        }
        if (exceptionCount > 0 && !testCode.contains("expected")) {
            recommendations.add("添加异常处理测试");
        }

        result.put("methodCount", methodCount);
        result.put("branchCount", branchCount);
        result.put("exceptionCount", exceptionCount);
        result.put("testMethodCount", testMethodCount);
        result.put("estimatedCoverage", String.format("%.1f", estimatedCoverage));
        result.put("recommendations", recommendations);

        return result;
    }

    private int countMatches(String text, Pattern pattern) {
        return (int) pattern.matcher(text).results().count();
    }

    private String extractMethodSignature(String code, int startIndex) {
        try {
            int braceIndex = code.indexOf('{', startIndex);
            if (braceIndex != -1) {
                String signature = code.substring(startIndex, braceIndex).trim();
                // 提取方法名
                String[] parts = signature.split("\\s+");
                for (int i = 0; i < parts.length; i++) {
                    if (i > 0 && parts[i].contains("(")) {
                        return parts[i].substring(0, parts[i].indexOf("("));
                    }
                }
            }
        } catch (Exception e) {
            // 忽略解析错误
        }
        return null;
    }
}