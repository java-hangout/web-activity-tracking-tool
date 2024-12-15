@echo off
set DEST_JAR=%USERPROFILE%\AppData\Local\WAT\deploy\artifacts\web-activity-tracking-tool.jar
REM Run the JAR file with the argument "Y"
echo Config value update starting...
echo Current date and time: %DATE% %TIME%
java -jar "%DEST_JAR%" Y
IF %ERRORLEVEL% NEQ 0 (
    echo Execution of JAR failed.
    exit /b 1
)
echo Execution ended.
exit /b 0