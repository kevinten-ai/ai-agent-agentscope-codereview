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
 * 代码风格检查工具
 * 检查代码规范、命名规范、格式化等
 */
@Slf4j
@Service
public class CodeStyleCheckTool {

    private static final Logger log = LoggerFactory.getLogger(CodeStyleCheckTool.class);

    // 驼峰命名检查
    private static final Pattern CAMEL_CASE_PATTERN = Pattern.compile("^[a-z][a-zA-Z0-9]*$");
    private static final Pattern PASCAL_CASE_PATTERN = Pattern.compile("^[A-Z][a-zA-Z0-9]*$");

    // 魔数检查
    private static final Pattern MAGIC_NUMBER_PATTERN = Pattern.compile("\\b[0-9]{2,}\\b");

    @Tool(description = "检查代码风格和规范")
    public String checkCodeStyle(@ToolParam(name = "filePath", description = "要检查的代码文件路径") String filePath) {
        try {
            String content = Files.readString(Paths.get(filePath));
            List<String> issues = new ArrayList<>();

            // 检查命名规范
            issues.addAll(checkNamingConventionsInternal(content));

            // 检查代码格式
            issues.addAll(checkCodeFormatting(content));

            // 检查魔数
            issues.addAll(checkMagicNumbers(content));

            // 检查代码长度
            issues.addAll(checkCodeLength(content));

            if (issues.isEmpty()) {
                return "✅ 代码风格检查完成: 符合基本代码规范";
            } else {
                StringBuilder result = new StringBuilder();
                result.append("📝 代码风格检查报告 - 发现 ").append(issues.size()).append(" 个规范问题:\n\n");
                for (int i = 0; i < issues.size(); i++) {
                    result.append(i + 1).append(". ").append(issues.get(i)).append("\n");
                }
                return result.toString();
            }

        } catch (Exception e) {
            log.error("代码风格检查失败", e);
            return "代码风格检查失败: " + e.getMessage();
        }
    }

    @Tool(description = "检查命名规范")
    public String checkNamingConventions(@ToolParam(name = "code", description = "要检查的代码内容") String code) {
        List<String> issues = new ArrayList<>();

        // 检查变量命名
        String[] lines = code.split("\n");
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();

            // 检查变量声明
            if (line.matches(".*\\b(int|long|double|String|boolean|char|float|short|byte)\\s+\\w+.*")) {
                String varName = extractVariableName(line);
                if (varName != null && !CAMEL_CASE_PATTERN.matcher(varName).matches()) {
                    issues.add("第" + (i + 1) + "行: 变量名 '" + varName + "' 不符合驼峰命名规范");
                }
            }

            // 检查常量命名
            if (line.contains("final ") && line.contains("static ")) {
                String constName = extractConstantName(line);
                if (constName != null && !constName.equals(constName.toUpperCase())) {
                    issues.add("第" + (i + 1) + "行: 常量名 '" + constName + "' 应使用大写字母和下划线");
                }
            }
        }

        return issues.isEmpty() ? "✅ 命名规范检查通过" :
            "命名规范问题:\n" + String.join("\n", issues);
    }

    private List<String> checkNamingConventionsInternal(String content) {
        List<String> issues = new ArrayList<>();

        // 检查方法名
        Pattern methodPattern = Pattern.compile("public\\s+\\w+\\s+([a-zA-Z_][a-zA-Z0-9_]*)\\s*\\(");
        var methodMatcher = methodPattern.matcher(content);
        while (methodMatcher.find()) {
            String methodName = methodMatcher.group(1);
            if (!CAMEL_CASE_PATTERN.matcher(methodName).matches()) {
                issues.add("方法名 '" + methodName + "' 不符合驼峰命名规范");
            }
        }

        // 检查类名
        Pattern classPattern = Pattern.compile("class\\s+([A-Z][a-zA-Z0-9]*)");
        var classMatcher = classPattern.matcher(content);
        while (classMatcher.find()) {
            String className = classMatcher.group(1);
            if (!PASCAL_CASE_PATTERN.matcher(className).matches()) {
                issues.add("类名 '" + className + "' 不符合帕斯卡命名规范");
            }
        }

        return issues;
    }

    private List<String> checkCodeFormatting(String content) {
        List<String> issues = new ArrayList<>();
        String[] lines = content.split("\n");

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];

            // 检查行长度
            if (line.length() > 120) {
                issues.add("第" + (i + 1) + "行: 代码行过长 (" + line.length() + " 字符)，建议不超过120字符");
            }

            // 检查空格使用
            if (line.contains("if(") || line.contains("for(") || line.contains("while(")) {
                issues.add("第" + (i + 1) + "行: 控制语句后应加空格，如 'if (' 而非 'if('");
            }
        }

        return issues;
    }

    private List<String> checkMagicNumbers(String content) {
        List<String> issues = new ArrayList<>();

        // 排除常见的非魔数
        Set<String> nonMagicNumbers = Set.of("0", "1", "-1", "2", "10", "100", "1000");

        var matcher = MAGIC_NUMBER_PATTERN.matcher(content);
        while (matcher.find()) {
            String number = matcher.group();
            if (!nonMagicNumbers.contains(number)) {
                issues.add("检测到魔数 '" + number + "'，建议定义为常量");
            }
        }

        return issues;
    }

    private List<String> checkCodeLength(String content) {
        List<String> issues = new ArrayList<>();
        String[] lines = content.split("\n");

        // 检查方法长度
        int methodStart = -1;
        int braceCount = 0;

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();

            if (line.matches(".*\\b(public|private|protected)?\\s*\\w+\\s+\\w+\\s*\\([^)]*\\)\\s*\\{.*")) {
                methodStart = i;
                braceCount = 1;
            } else if (methodStart != -1) {
                braceCount += countChar(line, '{') - countChar(line, '}');

                if (braceCount == 0 && (i - methodStart) > 50) {
                    issues.add("方法过长 (从第" + (methodStart + 1) + "行到第" + (i + 1) + "行)，建议拆分为多个小方法");
                    methodStart = -1;
                }
            }
        }

        return issues;
    }

    private String extractVariableName(String line) {
        // 简化的变量名提取
        String[] parts = line.split("\\s+");
        for (int i = 0; i < parts.length - 1; i++) {
            if (parts[i].matches("(int|long|double|String|boolean|char|float|short|byte)")) {
                return parts[i + 1].replaceAll("[^a-zA-Z0-9_]", "");
            }
        }
        return null;
    }

    private String extractConstantName(String line) {
        // 简化的常量名提取
        if (line.contains("=")) {
            String[] parts = line.split("=");
            if (parts.length > 0) {
                String before = parts[0].trim();
                String[] tokens = before.split("\\s+");
                return tokens[tokens.length - 1].replaceAll("[^a-zA-Z0-9_]", "");
            }
        }
        return null;
    }

    private int countChar(String str, char c) {
        return (int) str.chars().filter(ch -> ch == c).count();
    }
}