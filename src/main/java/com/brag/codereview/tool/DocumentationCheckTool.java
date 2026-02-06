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
 * 文档检查工具
 * 检查代码注释、文档完整性等
 */
@Slf4j
@Service
public class DocumentationCheckTool {

    private static final Logger log = LoggerFactory.getLogger(DocumentationCheckTool.class);

    private static final Pattern JAVADOC_PATTERN = Pattern.compile("/\\*\\*.*?\\*/", Pattern.DOTALL);
    private static final Pattern SINGLE_LINE_COMMENT_PATTERN = Pattern.compile("//.*");
    private static final Pattern METHOD_PATTERN = Pattern.compile("\\b(public|private|protected)?\\s*\\w+\\s+\\w+\\s*\\([^)]*\\)\\s*\\{");

    @Tool(description = "检查代码文档完整性")
    public String checkDocumentation(@ToolParam(name = "filePath", description = "要检查的代码文件路径") String filePath) {
        try {
            String content = Files.readString(Paths.get(filePath));
            List<String> issues = new ArrayList<>();

            // 检查类文档
            issues.addAll(checkClassDocumentation(content));

            // 检查方法文档
            issues.addAll(checkMethodDocumentation(content));

            // 检查字段文档
            issues.addAll(checkFieldDocumentation(content));

            // 检查注释质量
            issues.addAll(checkCommentQuality(content));

            if (issues.isEmpty()) {
                return "✅ 文档检查完成: 文档较为完整";
            } else {
                StringBuilder result = new StringBuilder();
                result.append("📚 文档检查报告 - 发现 ").append(issues.size()).append(" 个文档问题:\n\n");
                for (int i = 0; i < issues.size(); i++) {
                    result.append(i + 1).append(". ").append(issues.get(i)).append("\n");
                }
                return result.toString();
            }

        } catch (Exception e) {
            log.error("文档检查失败", e);
            return "文档检查失败: " + e.getMessage();
        }
    }

    @Tool(description = "生成文档模板")
    public String generateDocumentationTemplate(@ToolParam(name = "elementType", description = "元素类型 (class/method/field)") String elementType) {
        switch (elementType.toLowerCase()) {
            case "class":
                return """
                        /**
                         * 类功能描述
                         *
                         * @author 作者名
                         * @version 版本号
                         * @since 起始版本
                         */
                        """;
            case "method":
                return """
                        /**
                         * 方法功能描述
                         *
                         * @param parameterName 参数描述
                         * @return 返回值描述
                         * @throws ExceptionType 异常描述
                         */
                        """;
            case "field":
                return """
                        /** 字段描述 */
                        """;
            default:
                return "不支持的元素类型: " + elementType;
        }
    }

    private List<String> checkClassDocumentation(String content) {
        List<String> issues = new ArrayList<>();

        // 检查类是否有Javadoc
        var javadocMatcher = JAVADOC_PATTERN.matcher(content);
        var classMatcher = Pattern.compile("\\bclass\\s+\\w+").matcher(content);

        if (classMatcher.find()) {
            int classIndex = classMatcher.start();
            boolean hasClassDoc = false;

            // 检查类前是否有Javadoc
            javadocMatcher.reset();
            while (javadocMatcher.find()) {
                if (javadocMatcher.end() < classIndex) {
                    hasClassDoc = true;
                    break;
                }
            }

            if (!hasClassDoc) {
                issues.add("类缺少Javadoc文档注释");
            }
        }

        return issues;
    }

    private List<String> checkMethodDocumentation(String content) {
        List<String> issues = new ArrayList<>();

        var methodMatcher = METHOD_PATTERN.matcher(content);
        var javadocMatcher = JAVADOC_PATTERN.matcher(content);

        List<Integer> methodPositions = new ArrayList<>();
        while (methodMatcher.find()) {
            methodPositions.add(methodMatcher.start());
        }

        List<Integer> javadocPositions = new ArrayList<>();
        while (javadocMatcher.find()) {
            javadocPositions.add(javadocMatcher.start());
        }

        for (int methodPos : methodPositions) {
            boolean hasDoc = false;
            for (int javadocPos : javadocPositions) {
                if (javadocPos < methodPos) {
                    // 检查Javadoc是否紧邻方法
                    String between = content.substring(javadocPos, methodPos);
                    if (!between.contains("class ") && !between.contains("}")) {
                        hasDoc = true;
                        break;
                    }
                }
            }
            if (!hasDoc) {
                issues.add("公共方法缺少Javadoc文档注释");
            }
        }

        return issues;
    }

    private List<String> checkFieldDocumentation(String content) {
        List<String> issues = new ArrayList<>();

        String[] lines = content.split("\n");
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();

            // 检查公共字段
            if (line.matches("public\\s+(?!class|interface|enum)\\w+\\s+\\w+.*;")) {
                // 检查上一行是否有注释
                boolean hasDoc = false;
                if (i > 0) {
                    String prevLine = lines[i - 1].trim();
                    if (prevLine.startsWith("//") || prevLine.startsWith("/*") || prevLine.startsWith("*")) {
                        hasDoc = true;
                    }
                }
                if (!hasDoc) {
                    issues.add("第" + (i + 1) + "行: 公共字段缺少注释");
                }
            }
        }

        return issues;
    }

    private List<String> checkCommentQuality(String content) {
        List<String> issues = new ArrayList<>();

        // 检查TODO注释
        if (content.contains("TODO") || content.contains("FIXME")) {
            issues.add("发现未完成的TODO或FIXME注释");
        }

        // 检查空注释
        String[] lines = content.split("\n");
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();
            if (line.equals("//") || line.equals("/** */") || line.equals("/* */")) {
                issues.add("第" + (i + 1) + "行: 发现空注释");
            }
        }

        // 检查注释长度
        var commentMatcher = SINGLE_LINE_COMMENT_PATTERN.matcher(content);
        while (commentMatcher.find()) {
            String comment = commentMatcher.group();
            if (comment.length() > 100) {
                issues.add("发现过长的单行注释，建议使用块注释或拆分");
                break;
            }
        }

        return issues;
    }

    @Tool(description = "分析代码注释覆盖率")
    public String analyzeCommentCoverage(@ToolParam(name = "code", description = "要分析的代码内容") String code) {
        int totalLines = code.split("\n").length;
        int commentLines = 0;

        // 计算注释行数
        String[] lines = code.split("\n");
        boolean inBlockComment = false;

        for (String line : lines) {
            String trimmed = line.trim();

            if (inBlockComment) {
                commentLines++;
                if (trimmed.contains("*/")) {
                    inBlockComment = false;
                }
            } else if (trimmed.startsWith("/*")) {
                commentLines++;
                inBlockComment = true;
                if (!trimmed.contains("*/")) {
                    inBlockComment = true;
                }
            } else if (trimmed.startsWith("//") || trimmed.startsWith("*")) {
                commentLines++;
            }
        }

        double coverage = (double) commentLines / totalLines * 100;

        return String.format("注释覆盖率分析:\n" +
                           "- 总行数: %d\n" +
                           "- 注释行数: %d\n" +
                           "- 注释覆盖率: %.1f%%\n" +
                           "- 建议覆盖率: 20-30%%",
                           totalLines, commentLines, coverage);
    }
}