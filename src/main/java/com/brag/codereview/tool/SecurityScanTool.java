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
 * 安全扫描工具
 * 检测代码中的安全漏洞和风险
 */
@Slf4j
@Service
public class SecurityScanTool {

    private static final Logger log = LoggerFactory.getLogger(SecurityScanTool.class);

    // SQL注入风险模式
    private static final Pattern SQL_INJECTION_PATTERN =
        Pattern.compile(".*(SELECT|INSERT|UPDATE|DELETE).*\\+.*|.*Statement\\.execute.*\\+.*");

    // XSS风险模式
    private static final Pattern XSS_PATTERN =
        Pattern.compile(".*innerHTML.*=.*|.*outerHTML.*=.*|.*document\\.write.*");

    // 硬编码密码模式
    private static final Pattern HARDCODED_PASSWORD_PATTERN =
        Pattern.compile("password.*=.*[\"'][^\"']*[\"']|PASSWORD.*=.*[\"'][^\"']*[\"']");

    // 不安全的随机数生成
    private static final Pattern INSECURE_RANDOM_PATTERN =
        Pattern.compile("new Random\\(\\)|Math\\.random\\(\\)");

    // 命令注入风险
    private static final Pattern COMMAND_INJECTION_PATTERN =
        Pattern.compile("Runtime\\.getRuntime\\(\\)\\.exec\\(.*\\+.*\\)|ProcessBuilder.*\\+.*");

    @Tool(description = "扫描代码中的安全漏洞和风险")
    public String scanSecurityVulnerabilities(@ToolParam(name = "filePath", description = "要扫描的代码文件路径") String filePath) {
        try {
            String content = Files.readString(Paths.get(filePath));

            List<String> vulnerabilities = new ArrayList<>();

            // SQL注入检查
            if (SQL_INJECTION_PATTERN.matcher(content).find()) {
                vulnerabilities.add("⚠️ 潜在SQL注入风险: 检测到字符串拼接的SQL语句");
            }

            // XSS检查
            if (XSS_PATTERN.matcher(content).find()) {
                vulnerabilities.add("⚠️ 潜在XSS风险: 检测到直接设置innerHTML或outerHTML");
            }

            // 硬编码密码检查
            if (HARDCODED_PASSWORD_PATTERN.matcher(content).find()) {
                vulnerabilities.add("🚨 安全风险: 检测到硬编码的密码");
            }

            // 不安全随机数检查
            if (INSECURE_RANDOM_PATTERN.matcher(content).find()) {
                vulnerabilities.add("⚠️ 安全风险: 使用不安全的随机数生成器");
            }

            // 命令注入检查
            if (COMMAND_INJECTION_PATTERN.matcher(content).find()) {
                vulnerabilities.add("🚨 严重安全风险: 潜在命令注入漏洞");
            }

            // 检查敏感信息泄露
            vulnerabilities.addAll(checkSensitiveDataLeakageInternal(content));

            // 检查访问控制
            vulnerabilities.addAll(checkAccessControl(content));

            if (vulnerabilities.isEmpty()) {
                return "✅ 安全扫描完成: 未发现明显的安全漏洞";
            } else {
                StringBuilder result = new StringBuilder();
                result.append("🚨 安全扫描报告 - 发现 ").append(vulnerabilities.size()).append(" 个潜在问题:\n\n");
                for (int i = 0; i < vulnerabilities.size(); i++) {
                    result.append(i + 1).append(". ").append(vulnerabilities.get(i)).append("\n");
                }
                return result.toString();
            }

        } catch (Exception e) {
            log.error("安全扫描失败", e);
            return "安全扫描失败: " + e.getMessage();
        }
    }

    @Tool(description = "检查敏感数据泄露风险")
    public String checkSensitiveDataLeakage(@ToolParam(name = "code", description = "要检查的代码内容") String code) {
        List<String> issues = new ArrayList<>();

        // 检查日志中的敏感信息
        if (code.contains("log.") && (code.contains("password") || code.contains("token") || code.contains("secret"))) {
            issues.add("⚠️ 日志安全风险: 可能在日志中记录敏感信息");
        }

        // 检查硬编码的API密钥
        if (code.contains("api_key") || code.contains("API_KEY") || code.contains("apikey")) {
            issues.add("🚨 敏感数据风险: 检测到API密钥相关代码");
        }

        return issues.isEmpty() ? "✅ 未发现敏感数据泄露风险" :
            "敏感数据检查结果:\n" + String.join("\n", issues);
    }

    private List<String> checkSensitiveDataLeakageInternal(String content) {
        List<String> issues = new ArrayList<>();

        // 检查日志记录敏感信息
        Pattern logPattern = Pattern.compile("log\\.(info|debug|warn|error).*password|log.*token|log.*secret",
                                           Pattern.CASE_INSENSITIVE);
        if (logPattern.matcher(content).find()) {
            issues.add("⚠️ 日志安全风险: 可能在日志中记录敏感信息(password/token/secret)");
        }

        // 检查硬编码的凭据
        Pattern credentialPattern = Pattern.compile("(api_key|apikey|secret|token).*=.*[\"'][^\"']*[\"']",
                                                  Pattern.CASE_INSENSITIVE);
        if (credentialPattern.matcher(content).find()) {
            issues.add("🚨 凭据安全风险: 检测到硬编码的API密钥或令牌");
        }

        return issues;
    }

    private List<String> checkAccessControl(String content) {
        List<String> issues = new ArrayList<>();

        // 检查权限绕过风险
        if (content.contains("@PreAuthorize") && content.contains("permitAll")) {
            issues.add("⚠️ 权限控制风险: 使用permitAll可能过于宽松");
        }

        // 检查SQL权限
        if (content.contains("Statement") && !content.contains("PreparedStatement")) {
            issues.add("⚠️ SQL安全风险: 使用Statement而非PreparedStatement");
        }

        return issues;
    }

    @Tool(description = "生成安全修复建议")
    public String generateSecurityFixSuggestions(@ToolParam(name = "vulnerabilityType", description = "漏洞类型") String vulnerabilityType) {
        Map<String, String> suggestions = new HashMap<>();
        suggestions.put("SQL注入", "1. 使用PreparedStatement替代Statement\n2. 使用参数化查询\n3. 输入验证和转义\n4. 使用ORM框架如MyBatis");
        suggestions.put("XSS", "1. 对用户输入进行HTML编码\n2. 使用模板引擎的自动转义\n3. 设置Content Security Policy (CSP)\n4. 使用安全的DOM操作方法");
        suggestions.put("硬编码密码", "1. 使用环境变量或配置文件\n2. 使用密码管理服务\n3. 实现密码轮换机制\n4. 使用密钥管理系统");
        suggestions.put("命令注入", "1. 避免直接执行用户输入的命令\n2. 使用白名单验证命令参数\n3. 使用安全的方法执行系统命令\n4. 最小权限原则");
        suggestions.put("不安全随机数", "1. 使用SecureRandom替代Random\n2. 使用java.security.SecureRandom\n3. 考虑使用专门的加密库");

        String suggestion = suggestions.get(vulnerabilityType);
        if (suggestion != null) {
            return "针对 '" + vulnerabilityType + "' 的修复建议:\n" + suggestion;
        } else {
            return "未找到针对 '" + vulnerabilityType + "' 的特定修复建议。请提供更多详细信息。";
        }
    }
}