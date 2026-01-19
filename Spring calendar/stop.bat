@echo off
title Stop Student Calendar Database

echo ==========================================
echo      Stopping Student Calendar DB
echo ==========================================

echo.
echo Stopping Docker containers...
docker-compose down

echo.
echo [OK] Database and pgAdmin have been stopped.
pause
