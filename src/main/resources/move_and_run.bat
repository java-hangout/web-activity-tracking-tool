@echo off
REM Ensure the destination directories exist, create them if not

set SOURCE_JAR=C:\Users\HP\Documents\WAT\artifact\web-activity-tracking-tool.jar
set DEST_JAR=C:\Users\HP\AppData\Local\WAT\deploy\artifact\web-activity-tracking-tool.jar

set SOURCE_CONFIG=C:\Users\HP\Documents\WAT\config\emailConfig.properties
set DEST_CONFIG=C:\Users\HP\AppData\Local\WAT\deploy\config\emailConfig.properties

REM Check if the destination directories exist, create if necessary
IF NOT EXIST "C:\Users\HP\AppData\Local\WAT\deploy\artifact" (
    echo Creating directory: C:\Users\HP\AppData\Local\WAT\deploy\artifact
    mkdir "C:\Users\HP\AppData\Local\WAT\deploy\artifact"
)

IF NOT EXIST "C:\Users\HP\AppData\Local\WAT\deploy\config" (
    echo Creating directory: C:\Users\HP\AppData\Local\WAT\deploy\config
    mkdir "C:\Users\HP\AppData\Local\WAT\deploy\config"
)

REM Move the JAR file
echo Moving JAR file...
move /Y "%SOURCE_JAR%" "%DEST_JAR%"
IF %ERRORLEVEL% NEQ 0 (
    echo Failed to move the JAR file.
    exit /b 1
)
echo JAR file moved successfully.

REM Move the configuration file
echo Moving config file...
move /Y "%SOURCE_CONFIG%" "%DEST_CONFIG%"
IF %ERRORLEVEL% NEQ 0 (
    echo Failed to move the config file.
    exit /b 1
)
echo Config file moved successfully.

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
