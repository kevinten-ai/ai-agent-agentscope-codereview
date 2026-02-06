@echo off
echo === JDK 21 环境验证 ===
echo.

echo 1. 检查Java版本...
java -version
if %errorlevel% neq 0 (
    echo ❌ Java未找到
    echo 请确保JDK 21已安装并添加到PATH
    goto :error
)
echo ✅ Java可用
echo.

echo 2. 检查Java版本是否为21...
java -version 2>&1 | findstr "21." >nul
if %errorlevel% neq 0 (
    echo ❌ 需要JDK 21，但发现其他版本
    echo 当前JAVA_HOME: %JAVA_HOME%
    goto :error
)
echo ✅ JDK 21已正确配置
echo.

echo 3. 检查Maven...
call mvn --version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Maven未找到
    echo 请添加Maven到PATH或使用完整路径
    goto :error
)
echo ✅ Maven可用
echo.

echo 4. 测试项目编译...
call mvn clean compile -q
if %errorlevel% neq 0 (
    echo ❌ 项目编译失败
    echo 请检查项目配置和依赖
    goto :error
)
echo ✅ 项目编译成功
echo.

echo 🎉 环境验证通过！
echo 您的JDK 21和Maven环境已正确配置
echo 项目可以正常构建
echo.
echo 下一步：配置API密钥并启动应用
echo set DASHSCOPE_API_KEY=your-api-key-here
echo mvn spring-boot:run
echo.
goto :end

:error
echo.
echo 请根据上述错误信息修复环境配置
echo 如需帮助，请查看项目文档或联系技术支持
echo.
pause
exit /b 1

:end
pause