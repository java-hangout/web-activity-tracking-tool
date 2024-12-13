@echo off
REM Set the current script's directory as the source location
set SCRIPT_DIR=%~dp0

REM Define the source and destination paths dynamically
set SOURCE_JAR=%SCRIPT_DIR%web-activity-tracking-tool.jar
set DEST_JAR=%USERPROFILE%\AppData\Local\WAT\deploy\artifacts\web-activity-tracking-tool.jar

set SOURCE_CONFIG=%SCRIPT_DIR%emailConfig.properties
set DEST_CONFIG=%USERPROFILE%\AppData\Local\WAT\deploy\config\emailConfig.properties

REM Ensure the destination directories exist, create them if necessary
IF NOT EXIST "%USERPROFILE%\AppData\Local\WAT\deploy\artifacts" (
    echo Creating directory: %USERPROFILE%\AppData\Local\WAT\deploy\artifacts
    mkdir "%USERPROFILE%\AppData\Local\WAT\deploy\artifacts"
)

IF NOT EXIST "%USERPROFILE%\AppData\Local\WAT\deploy\config" (
    echo Creating directory: %USERPROFILE%\AppData\Local\WAT\deploy\config
    mkdir "%USERPROFILE%\AppData\Local\WAT\deploy\config"
)

REM Copy the JAR file
echo Copying JAR file...
copy /Y "%SOURCE_JAR%" "%DEST_JAR%"
IF %ERRORLEVEL% NEQ 0 (
    echo Failed to copy the JAR file.
    exit /b 1
)
echo JAR file copied successfully.

REM Copy the configuration file
echo Copying config file...
copy /Y "%SOURCE_CONFIG%" "%DEST_CONFIG%"
IF %ERRORLEVEL% NEQ 0 (
    echo Failed to copy the config file.
    exit /b 1
)
echo Config file copied successfully.

@REM REM Run the JAR file
@REM echo Execution starting...
@REM echo Current date and time: %DATE% %TIME%
@REM java -jar "%DEST_JAR%"
@REM IF %ERRORLEVEL% NEQ 0 (
@REM     echo Execution of JAR failed.
@REM     exit /b 1
@REM )
@REM echo Execution ended.

exit /b 0
