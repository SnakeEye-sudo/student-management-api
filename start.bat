@echo off
title Student Management API
color 0B
cls
echo ======================================================================
echo         STUDENT MANAGEMENT API - FINAL BOOTSTRAPPER
echo ======================================================================
echo.
echo  This script will install all dependencies, compile the project,
echo  and start the server locally. No external database needed!
echo  (Uses H2 in-memory database automatically)
echo.
echo ----------------------------------------------------------------------

echo.
echo [STEP 1/3] Verifying Java...
java -version
if %errorlevel% neq 0 (
    echo.
    echo [ERROR] Java is not installed or not in PATH!
    echo         Install JDK 17+ from: https://adoptium.net/
    pause
    exit /b 1
)
echo [OK] Java is ready.
echo.

echo [STEP 2/3] Installing all dependencies and compiling...
echo Running: mvn clean install -Dmaven.test.skip=true
echo.
call mvn clean install -Dmaven.test.skip=true
if %errorlevel% neq 0 (
    echo.
    echo [ERROR] Build failed! Check the errors above.
    echo         Make sure Maven is installed and in PATH.
    pause
    exit /b 1
)
echo.
echo [OK] Build successful! All dependencies installed.
echo.

echo [STEP 3/3] Starting application server...
echo.
echo ======================================================================
echo  Server starting on: http://localhost:8080
echo.
echo  TEST ENDPOINTS (use browser or Postman):
echo    GET  http://localhost:8080/api/v1/students
echo    POST http://localhost:8080/api/v1/students
echo    GET  http://localhost:8080/api/v1/students/1
echo    PUT  http://localhost:8080/api/v1/students/1
echo    DEL  http://localhost:8080/api/v1/students/1
echo.
echo  H2 DB Console: http://localhost:8080/h2-console
echo    JDBC URL: jdbc:h2:mem:student_db
echo    Username: sa    Password: (leave empty)
echo.
echo  Press Ctrl+C to stop the server.
echo ======================================================================
echo.
call mvn spring-boot:run

pause
