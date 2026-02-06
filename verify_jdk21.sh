#!/bin/bash

echo "=== JDK 21 环境验证 (Linux/Mac) ==="
echo

echo "1. 检查Java版本..."
if ! java -version 2>&1; then
    echo "❌ Java未找到"
    echo "请确保JDK 21已安装并添加到PATH"
    exit 1
fi
echo "✅ Java可用"
echo

echo "2. 检查Java版本是否为21..."
if java -version 2>&1 | grep -q "21\."; then
    echo "✅ JDK 21已正确配置"
else
    echo "❌ 需要JDK 21，但发现其他版本"
    echo "当前JAVA_HOME: $JAVA_HOME"
    exit 1
fi
echo

echo "3. 检查Maven..."
if ! mvn --version >/dev/null 2>&1; then
    echo "❌ Maven未找到"
    echo "请安装Maven并添加到PATH"
    exit 1
fi
echo "✅ Maven可用"
echo

echo "4. 测试项目编译..."
if ! mvn clean compile -q; then
    echo "❌ 项目编译失败"
    echo "请检查项目配置和依赖"
    exit 1
fi
echo "✅ 项目编译成功"
echo

echo "🎉 环境验证通过！"
echo "您的JDK 21和Maven环境已正确配置"
echo "项目可以正常构建"
echo
echo "下一步：配置API密钥并启动应用"
echo "export DASHSCOPE_API_KEY=your-api-key-here"
echo "mvn spring-boot:run"
echo