package com.brag.codereview.tool;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CodeAnalysisToolTest {

    @Test
    public void testAnalyzeCodeStructure() {
        CodeAnalysisTool codeAnalysisTool = new CodeAnalysisTool();

        // 创建一个简单的测试Java代码
        String testCode = """
                package test;

                import java.util.List;
                import java.util.ArrayList;

                /**
                 * 测试类
                 */
                public class TestClass {
                    private String name;
                    private int age;

                    /**
                     * 构造函数
                     */
                    public TestClass(String name, int age) {
                        this.name = name;
                        this.age = age;
                    }

                    /**
                     * 获取姓名
                     */
                    public String getName() {
                        return name;
                    }

                    /**
                     * 设置姓名
                     */
                    public void setName(String name) {
                        this.name = name;
                    }

                    /**
                     * 获取年龄
                     */
                    public int getAge() {
                        return age;
                    }

                    /**
                     * 设置年龄
                     */
                    public void setAge(int age) {
                        this.age = age;
                    }
                }
                """;

        try {
            // 创建临时文件进行测试
            java.nio.file.Path tempFile = java.nio.file.Files.createTempFile("test", ".java");
            java.nio.file.Files.writeString(tempFile, testCode);

            // 测试代码结构分析
            String result = codeAnalysisTool.analyzeCodeStructure(tempFile.toString());

            assertNotNull(result, "分析结果不应为空");
            assertTrue(result.contains("类数量"), "应包含类数量信息");
            assertTrue(result.contains("方法数量"), "应包含方法数量信息");

            // 清理临时文件
            java.nio.file.Files.deleteIfExists(tempFile);

        } catch (Exception e) {
            throw new RuntimeException("测试失败", e);
        }
    }
}