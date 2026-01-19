@echo off
title Student Calendar Launcher

echo ==========================================
echo      Student Calendar - Quick Start
echo ==========================================

echo.
echo [1/3] Starting Database (Docker)...
docker-compose up -d
IF %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Docker is not running or docker-compose failed.
    echo Please make sure Docker Desktop is open.
    pause
    exit /b
)

echo.
echo [2/3] Setting Java Environment...
set "JAVA_HOME=C:\Program Files\Java\jdk-25"
echo JAVA_HOME set to: %JAVA_HOME%

echo.
echo [3/3] Launching Application...
echo (Press Ctrl+C to stop the application)
echo.
call mvnw.cmd spring-boot:run

pause
