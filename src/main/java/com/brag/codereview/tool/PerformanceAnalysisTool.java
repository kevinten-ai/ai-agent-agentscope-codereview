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
 * 性能分析工具
 * 检测代码中的性能问题和优化机会
 */
@Slf4j
@Service
public class PerformanceAnalysisTool {

    private static final Logger log = LoggerFactory.getLogger(PerformanceAnalysisTool.class);

    // 循环中的数据库查询
    private static final Pattern LOOP_DB_QUERY_PATTERN =
        Pattern.compile("(for|while|foreach).*\\{[^}]*\\.(find|query|select|execute)");

    // 大对象创建
    private static final Pattern LARGE_OBJECT_PATTERN =
        Pattern.compile("new\\s+(ArrayList|HashMap|HashSet)\\s*\\([^)]*1000");

    // 字符串拼接
    private static final Pattern STRING_CONCAT_PATTERN =
        Pattern.compile("\".*\"\\s*\\+\\s*\".*\"|StringBuilder|StringBuffer");

    // 内存泄漏风险
    private static final Pattern MEMORY_LEAK_PATTERN =
        Pattern.compile("(static|class).*Map|static.*List|static.*Set");

    // N+1查询问题
    private static final Pattern N_PLUS_ONE_PATTERN =
        Pattern.compile("\\.get\\(\\w+\\)\\.\\w+\\(\\)");

    @Tool(description = "分析代码中的性能问题")
    public String analyzePerformanceIssues(@ToolParam(name = "filePath", description = "要分析的代码文件路径") String filePath) {
        try {
            String content = Files.readString(Paths.get(filePath));

            List<String> issues = new ArrayList<>();

            // 检查循环中的数据库查询
            if (LOOP_DB_QUERY_PATTERN.matcher(content).find()) {
                issues.add("🚨 性能问题: 检测到循环中的数据库查询，可能导致N+1查询问题");
            }

            // 检查大对象创建
            if (LARGE_OBJECT_PATTERN.matcher(content).find()) {
                issues.add("⚠️ 性能问题: 创建了大容量集合，可能影响内存使用");
            }

            // 检查字符串拼接效率
            if (content.contains("+= \"") || content.contains("= \"") && content.contains(" + \"")) {
                issues.add("⚠️ 性能问题: 可能存在低效的字符串拼接，建议使用StringBuilder");
            }

            // 检查内存泄漏风险
            if (MEMORY_LEAK_PATTERN.matcher(content).find()) {
                issues.add("⚠️ 内存泄漏风险: 静态集合可能导致内存泄漏");
            }

            // 检查N+1查询模式
            if (N_PLUS_ONE_PATTERN.matcher(content).find()) {
                issues.add("🚨 性能问题: 可能的N+1查询模式，建议使用批量查询");
            }

            // 检查其他性能问题
            issues.addAll(checkAlgorithmComplexity(content));
            issues.addAll(checkResourceManagement(content));

            if (issues.isEmpty()) {
                return "✅ 性能分析完成: 未发现明显的性能问题";
            } else {
                StringBuilder result = new StringBuilder();
                result.append("📊 性能分析报告 - 发现 ").append(issues.size()).append(" 个潜在问题:\n\n");
                for (int i = 0; i < issues.size(); i++) {
                    result.append(i + 1).append(". ").append(issues.get(i)).append("\n");
                }
                result.append("\n💡 优化建议:\n");
                result.append("- 使用批量操作减少数据库查询次数\n");
                result.append("- 使用合适的数据结构\n");
                result.append("- 避免在循环中创建对象\n");
                result.append("- 及时释放资源\n");
                return result.toString();
            }

        } catch (Exception e) {
            log.error("性能分析失败", e);
            return "性能分析失败: " + e.getMessage();
        }
    }

    @Tool(description = "分析算法时间复杂度")
    public String analyzeTimeComplexity(@ToolParam(name = "code", description = "要分析的代码片段") String code) {
        StringBuilder analysis = new StringBuilder();
        analysis.append("=== 算法复杂度分析 ===\n");

        // 检测嵌套循环
        int nestedLoops = countNestedLoops(code);
        if (nestedLoops > 0) {
            analysis.append("检测到 ").append(nestedLoops).append(" 层嵌套循环\n");
            analysis.append("时间复杂度可能为: O(n^").append(nestedLoops + 1).append(")\n");
        }

        // 检测递归
        if (code.contains("return ") && code.contains("(") && code.contains(")") && code.contains("{")) {
            analysis.append("⚠️ 检测到递归调用，请检查递归深度和终止条件\n");
        }

        // 检测大数据处理
        if (code.contains("List") || code.contains("Array") || code.contains("Collection")) {
            analysis.append("💡 数据结构建议:\n");
            analysis.append("- 大数据量使用ArrayList\n");
            analysis.append("- 频繁查找使用HashMap\n");
            analysis.append("- 排序数据使用TreeMap/TreeSet\n");
        }

        return analysis.toString();
    }

    @Tool(description = "分析内存使用模式")
    public String analyzeMemoryUsage(@ToolParam(name = "code", description = "要分析的代码片段") String code) {
        StringBuilder analysis = new StringBuilder();
        analysis.append("=== 内存使用分析 ===\n");

        // 检查对象创建模式
        if (code.contains("new ") && code.contains("for") || code.contains("while")) {
            analysis.append("⚠️ 在循环中创建对象，可能导致频繁GC\n");
        }

        // 检查大对象
        if (code.contains("byte[") || code.contains("new byte")) {
            analysis.append("💡 检测到字节数组操作，注意内存占用\n");
        }

        // 检查集合使用
        if (code.contains("ArrayList") || code.contains("HashMap")) {
            analysis.append("💡 集合使用建议:\n");
            analysis.append("- 预估容量: new ArrayList<>(initialCapacity)\n");
            analysis.append("- 清理引用: list.clear()\n");
            analysis.append("- 使用合适的数据类型\n");
        }

        return analysis.toString();
    }

    private List<String> checkAlgorithmComplexity(String content) {
        List<String> issues = new ArrayList<>();

        // 检查三层以上的嵌套循环
        int nestedLevel = countNestedLoops(content);
        if (nestedLevel >= 3) {
            issues.add("🚨 算法复杂度问题: 检测到" + nestedLevel + "层嵌套循环，时间复杂度可能很高");
        }

        // 检查递归深度
        if (content.contains("递归") || (content.contains("return") && content.contains("方法名"))) {
            issues.add("⚠️ 递归使用: 请检查递归深度和栈溢出风险");
        }

        return issues;
    }

    private List<String> checkResourceManagement(String content) {
        List<String> issues = new ArrayList<>();

        // 检查资源释放
        if ((content.contains("File") || content.contains("Connection") || content.contains("Stream"))
            && !content.contains("try-with-resources") && !content.contains(".close()")) {
            issues.add("⚠️ 资源管理问题: 可能存在资源泄漏，未正确关闭资源");
        }

        // 检查连接池使用
        if (content.contains("Connection") && !content.contains("DataSource") && !content.contains("Pool")) {
            issues.add("⚠️ 数据库连接问题: 建议使用连接池而非直接创建连接");
        }

        return issues;
    }

    private int countNestedLoops(String code) {
        int maxDepth = 0;
        int currentDepth = 0;

        for (char c : code.toCharArray()) {
            if (c == '{') {
                currentDepth++;
                maxDepth = Math.max(maxDepth, currentDepth);
            } else if (c == '}') {
                currentDepth--;
            }
        }

        // 粗略估算循环嵌套层数
        String[] loopKeywords = {"for", "while", "foreach"};
        int loopCount = 0;
        for (String keyword : loopKeywords) {
            loopCount += countOccurrences(code, keyword);
        }

        return Math.min(loopCount, maxDepth / 2); // 保守估计
    }

    private int countOccurrences(String text, String keyword) {
        int count = 0;
        int index = 0;
        while ((index = text.indexOf(keyword, index)) != -1) {
            count++;
            index += keyword.length();
        }
        return count;
    }
}