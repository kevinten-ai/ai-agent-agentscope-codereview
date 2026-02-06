#!/bin/bash
# 测试环境配置脚本 (Linux/Mac)

echo "========================================"
echo "AI CodeReview AgentScope 环境测试"
echo "========================================"

# 检查Java版本
echo "检查Java版本..."
if ! java -version 2>&1 | grep -q "21"; then
    echo "❌ Java 21 未安装或配置不正确"
    echo "请安装 JDK 21 并设置 JAVA_HOME"
    exit 1
else
    echo "✅ Java版本检查通过"
fi

# 检查Maven版本
echo
echo "检查Maven版本..."
if ! mvn -version >/dev/null 2>&1; then
    echo "❌ Maven 未安装或配置不正确"
    echo "请安装 Maven 3.6+ 并添加到 PATH"
    exit 1
else
    echo "✅ Maven版本检查通过"
fi

# 检查环境变量
echo
echo "检查DASHSCOPE_API_KEY环境变量..."
if [ -z "$DASHSCOPE_API_KEY" ]; then
    echo "❌ DASHSCOPE_API_KEY环境变量未设置"
    echo "请参考 config/env-template.txt 设置环境变量"
    exit 1
else
    echo "✅ DASHSCOPE_API_KEY已设置"
fi

# 编译项目
echo
echo "编译项目..."
if ! mvn clean compile >/dev/null 2>&1; then
    echo "❌ 项目编译失败"
    echo "请检查项目依赖和配置"
    exit 1
else
    echo "✅ 项目编译成功"
fi

echo
echo "========================================"
echo "✅ 环境配置检查通过！"
echo "现在可以运行: mvn spring-boot:run"
echo "========================================"