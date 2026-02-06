package com.brag.codereview.examples;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * 代码审查示例
 * 演示如何使用代码审查Agent进行全面的代码分析
 */
@Slf4j
public class CodeReviewExample {

    private static final Logger log = LoggerFactory.getLogger(CodeReviewExample.class);

    public static void main(String[] args) {
        CodeReviewExample example = new CodeReviewExample();

        try {
            if (args.length == 0) {
                log.info("未提供文件路径参数，运行示例代码审查");
                example.runExampleReview();
            } else {
                String filePath = args[0];
                if (Files.exists(Paths.get(filePath))) {
                    log.info("开始审查文件: {}", filePath);
                    example.performFullReview(filePath);
                } else {
                    log.error("文件不存在: {}", filePath);
                }
            }
        } catch (Exception e) {
            log.error("运行示例时发生错误", e);
        }
    }

    /**
     * 运行示例代码审查
     */
    private void runExampleReview() {
        try {
            // 创建示例Java文件
            String exampleCode = createExampleJavaFile();

            log.info("=== AI Agent CodeReview 示例 ===\n");

            // 由于Spring依赖问题，这里先展示代码结构
            log.info("示例Java代码已创建: example/ExampleClass.java");
            log.info("代码包含以下问题用于演示:");
            log.info("1. SQL注入漏洞 (字符串拼接)");
            log.info("2. XSS风险 (innerHTML直接赋值)");
            log.info("3. 硬编码密码");
            log.info("4. 不安全的随机数生成");
            log.info("5. 性能问题 (循环中创建对象)");

            log.info("\n注意: 完整的Agent功能需要Spring上下文和API密钥");
            log.info("请运行完整的Spring Boot应用来体验所有功能");

        } catch (Exception e) {
            log.error("示例运行失败", e);
        }
    }

    /**
     * 执行完整审查流程
     */
    private void performFullReview(String filePath) {
        try {
            log.info("开始执行完整代码审查流程...");
            log.info("审查文件: {}", filePath);

            if (Files.exists(Paths.get(filePath))) {
                log.info("✅ 文件存在，大小: {} bytes", Files.size(Paths.get(filePath)));
                log.info("\n注意: 完整的Agent功能需要Spring上下文和API密钥");
                log.info("请运行完整的Spring Boot应用来体验所有功能");
            } else {
                log.error("❌ 文件不存在: {}", filePath);
            }

        } catch (Exception e) {
            log.error("完整审查流程执行失败", e);
        }
    }

    /**
     * 创建示例Java文件用于演示
     */
    private String createExampleJavaFile() {
        String exampleCode = """
                package example;

                import java.sql.Connection;
                import java.sql.PreparedStatement;
                import java.sql.ResultSet;
                import java.util.ArrayList;
                import java.util.List;
                import java.util.Random;

                /**
                 * 示例类 - 包含多种代码问题用于演示审查功能
                 */
                public class ExampleClass {

                    private static final String PASSWORD = "admin123"; // 硬编码密码
                    private List<String> data = new ArrayList<>();

                    /**
                     * 查询用户信息 - 存在SQL注入风险
                     */
                    public List<String> getUsers(String username) {
                        List<String> users = new ArrayList<>();

                        try {
                            Connection conn = getConnection();
                            // SQL注入漏洞：字符串拼接
                            String query = "SELECT * FROM users WHERE username = '" + username + "'";
                            PreparedStatement stmt = conn.prepareStatement(query);
                            ResultSet rs = stmt.executeQuery();

                            while (rs.next()) {
                                users.add(rs.getString("username"));
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        return users;
                    }

                    /**
                     * 处理大量数据 - 性能问题示例
                     */
                    public void processLargeData(List<String> inputData) {
                        // 在循环中创建对象，影响性能
                        for (String item : inputData) {
                            String processed = item + "_processed";
                            data.add(processed);
                        }
                    }

                    /**
                     * 生成随机数 - 使用不安全的随机数生成器
                     */
                    public int generateRandomNumber() {
                        Random random = new Random(); // 不安全
                        return random.nextInt(100);
                    }

                    /**
                     * 显示用户信息 - XSS风险
                     */
                    public String displayUserInfo(String userInput) {
                        // XSS漏洞：直接设置innerHTML
                        return "<div>" + userInput + "</div>";
                    }

                    private Connection getConnection() {
                        // 模拟获取数据库连接
                        return null;
                    }
                }
                """;

        try {
            // 创建目录
            Files.createDirectories(Paths.get("example"));
            // 写入文件
            Files.writeString(Paths.get("example/ExampleClass.java"), exampleCode);
        } catch (Exception e) {
            log.error("创建示例文件失败", e);
        }

        return exampleCode;
    }
}