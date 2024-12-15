@echo off
set DEST_JAR=%USERPROFILE%\AppData\Local\WAT\deploy\artifacts\web-activity-tracking-tool.jar
REM Run the JAR file
echo Execution starting...
echo Current date and time: %DATE% %TIME%
java -jar "%DEST_JAR%"
IF %ERRORLEVEL% NEQ 0 (
    echo Execution of JAR failed.
    exit /b 1
)
echo Execution ended.
exit /b 0