@echo off
REM Ensure the destination directories exist, create them if necessary

set SOURCE_JAR=%USERPROFILE%\Documents\WAT\artifacts\web-activity-tracking-tool.jar
set DEST_JAR=%USERPROFILE%\AppData\Local\WAT\deploy\artifacts\web-activity-tracking-tool.jar

set SOURCE_CONFIG=%USERPROFILE%\Documents\WAT\config\emailConfig.properties
set DEST_CONFIG=%USERPROFILE%\AppData\Local\WAT\deploy\config\emailConfig.properties

REM Check if the destination directories exist, create if necessary
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
