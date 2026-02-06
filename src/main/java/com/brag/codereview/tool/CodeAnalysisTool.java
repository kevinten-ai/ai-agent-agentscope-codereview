package com.brag.codereview.tool;

import io.agentscope.core.tool.Tool;
import io.agentscope.core.tool.ToolParam;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;

/**
 * 代码分析工具
 * 分析代码结构、复杂度、可维护性等
 */
@Slf4j
@Service
public class CodeAnalysisTool {

    private static final Logger log = LoggerFactory.getLogger(CodeAnalysisTool.class);

    private static final Pattern CLASS_PATTERN = Pattern.compile("\\bclass\\s+\\w+");
    private static final Pattern METHOD_PATTERN = Pattern.compile("\\b(public|private|protected)?\\s*\\w+\\s+\\w+\\s*\\([^)]*\\)\\s*\\{");
    private static final Pattern COMMENT_PATTERN = Pattern.compile("//.*|/\\*[^*]*\\*+(?:[^/*][^*]*\\*+)*/");

    @Tool(description = "分析代码文件的整体结构和质量指标")
    public String analyzeCodeStructure(@ToolParam(name = "filePath", description = "要分析的代码文件路径") String filePath) {
        try {
            File file = new File(filePath);
            if (!file.exists() || !file.isFile()) {
                return "文件不存在或不是有效文件: " + filePath;
            }

            String content = Files.readString(Paths.get(filePath));

            Map<String, Object> analysis = new HashMap<>();
            analysis.put("fileName", file.getName());
            analysis.put("fileSize", file.length());
            analysis.put("lineCount", content.split("\n").length);
            analysis.put("classCount", countMatches(content, CLASS_PATTERN));
            analysis.put("methodCount", countMatches(content, METHOD_PATTERN));
            analysis.put("commentLines", countMatches(content, COMMENT_PATTERN));
            analysis.put("codeComplexity", calculateComplexity(content));
            analysis.put("maintainabilityIndex", calculateMaintainabilityIndex(content));

            return formatAnalysisResult(analysis);

        } catch (Exception e) {
            log.error("代码分析失败", e);
            return "代码分析失败: " + e.getMessage();
        }
    }

    @Tool(description = "分析代码中的依赖关系和导入")
    public String analyzeDependencies(@ToolParam(name = "filePath", description = "要分析的代码文件路径") String filePath) {
        try {
            String content = Files.readString(Paths.get(filePath));
            List<String> imports = extractImports(content);

            Map<String, Object> result = new HashMap<>();
            result.put("totalImports", imports.size());
            result.put("javaImports", imports.stream().filter(imp -> imp.startsWith("java.")).count());
            result.put("externalImports", imports.stream().filter(imp -> !imp.startsWith("java.")).count());
            result.put("imports", imports);

            return "依赖分析结果:\n" + result.toString();

        } catch (Exception e) {
            log.error("依赖分析失败", e);
            return "依赖分析失败: " + e.getMessage();
        }
    }

    @Tool(description = "计算代码圈复杂度")
    public String calculateCyclomaticComplexity(@ToolParam(name = "code", description = "要分析的代码片段") String code) {
        int complexity = 1; // 基础复杂度

        // 统计控制流关键字
        String[] controlFlowKeywords = {"if", "else if", "for", "while", "do", "switch", "case", "catch", "&&", "||"};
        for (String keyword : controlFlowKeywords) {
            complexity += countOccurrences(code, keyword);
        }

        return "代码圈复杂度: " + complexity + " (1-10:简单, 11-20:中等, 21-50:复杂, >50:非常复杂)";
    }

    private int countMatches(String text, Pattern pattern) {
        return (int) pattern.matcher(text).results().count();
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

    private List<String> extractImports(String content) {
        List<String> imports = new ArrayList<>();
        String[] lines = content.split("\n");

        for (String line : lines) {
            line = line.trim();
            if (line.startsWith("import ")) {
                String importStmt = line.substring(7).replace(";", "").trim();
                imports.add(importStmt);
            }
        }

        return imports;
    }

    private int calculateComplexity(String content) {
        int complexity = 1;
        String[] complexityKeywords = {"if", "for", "while", "switch", "catch"};
        for (String keyword : complexityKeywords) {
            complexity += countOccurrences(content, keyword);
        }
        return complexity;
    }

    private double calculateMaintainabilityIndex(String content) {
        // 简化的可维护性指数计算
        int lines = content.split("\n").length;
        int comments = countMatches(content, COMMENT_PATTERN);
        int complexity = calculateComplexity(content);

        // MI = 171 - 5.2 * ln(Halstead Volume) - 0.23 * CC - 16.2 * ln(LOC)
        // 这里使用简化版本
        double mi = 171 - 5.2 * Math.log(lines) - 0.23 * complexity - 16.2 * Math.log(lines);
        return Math.max(0, Math.min(171, mi));
    }

    private String formatAnalysisResult(Map<String, Object> analysis) {
        StringBuilder result = new StringBuilder();
        result.append("=== 代码结构分析报告 ===\n");
        result.append("文件名: ").append(analysis.get("fileName")).append("\n");
        result.append("文件大小: ").append(analysis.get("fileSize")).append(" bytes\n");
        result.append("总行数: ").append(analysis.get("lineCount")).append("\n");
        result.append("类数量: ").append(analysis.get("classCount")).append("\n");
        result.append("方法数量: ").append(analysis.get("methodCount")).append("\n");
        result.append("注释行数: ").append(analysis.get("commentLines")).append("\n");
        result.append("圈复杂度: ").append(analysis.get("codeComplexity")).append("\n");
        result.append("可维护性指数: ").append(String.format("%.2f", analysis.get("maintainabilityIndex"))).append(" (0-171, 越高越好)\n");

        return result.toString();
    }
}