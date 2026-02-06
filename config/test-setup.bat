@echo off
REM 测试环境配置脚本 (Windows)
echo ========================================
echo AI CodeReview AgentScope 环境测试
echo ========================================

REM 检查Java版本
echo 检查Java版本...
java -version
if %errorlevel% neq 0 (
    echo ❌ Java未安装或配置不正确
    goto :error
)

REM 检查Maven版本
echo.
echo 检查Maven版本...
mvn -version
if %errorlevel% neq 0 (
    echo ❌ Maven未安装或配置不正确
    goto :error
)

REM 检查环境变量
echo.
echo 检查DASHSCOPE_API_KEY环境变量...
if "%DASHSCOPE_API_KEY%"=="" (
    echo ❌ DASHSCOPE_API_KEY环境变量未设置
    echo 请参考 config/env-template.txt 设置环境变量
    goto :error
) else (
    echo ✅ DASHSCOPE_API_KEY已设置
)

REM 编译项目
echo.
echo 编译项目...
mvn clean compile
if %errorlevel% neq 0 (
    echo ❌ 项目编译失败
    goto :error
) else (
    echo ✅ 项目编译成功
)

echo.
echo ========================================
echo ✅ 环境配置检查通过！
echo 现在可以运行: mvn spring-boot:run
echo ========================================
goto :end

:error
echo.
echo ❌ 环境配置检查失败，请解决上述问题后重试
exit /b 1

:end