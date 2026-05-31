@echo off
echo Looking for process on port 8080...
for /f "tokens=5" %%a in ('netstat -aon ^| findstr :8080') do (
    echo Killing process ID %%a...
    taskkill /F /PID %%a
)
echo.
echo Done! Port 8080 is now free. 
echo You can close this window and run start.bat again.
pause
