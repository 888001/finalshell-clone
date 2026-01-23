@echo off
title FinalShell Clone
cd /d "%~dp0\.."

REM Check Java installation
java -version >nul 2>&1
if errorlevel 1 (
    echo Java is not installed or not in PATH.
    echo Please install Java 8 or higher.
    pause
    exit /b 1
)

REM Set JVM options
set JAVA_OPTS=-Xms256m -Xmx1024m -Dfile.encoding=UTF-8

REM Find the JAR file
set JAR_FILE=
for %%f in (target\*-jar-with-dependencies.jar) do set JAR_FILE=%%f

if "%JAR_FILE%"=="" (
    echo JAR file not found. Please build the project first:
    echo   mvn clean package
    pause
    exit /b 1
)

REM Start application
echo Starting FinalShell Clone...
java %JAVA_OPTS% -jar "%JAR_FILE%"

if errorlevel 1 (
    echo Application exited with error.
    pause
)
