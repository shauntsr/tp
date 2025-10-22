@echo off
setlocal enableextensions
pushd %~dp0

cd ..
call gradlew clean shadowJar

cd build\libs

for /f "tokens=*" %%a in (
    'dir /b *.jar'
) do (
    set jarloc=%%a
)

cd ..\..\text-ui-test

REM --- Remove previous test data directory (if exists) ---
if exist data (
  echo Removing existing test data directory: .\data
  rd /s /q data
)

java -jar ..\build\libs\%jarloc% < input.txt > ACTUAL.TXT

REM Normalize IDs and dates in ACTUAL.TXT
REM - Replace 8-char lowercase hex IDs (with # prefix) with #XXXXXXXX
REM - Replace 8-char lowercase hex IDs (standalone) with XXXXXXXX
REM - Replace dates in format YYYY-MM-DD with XXXX-XX-XX
powershell -Command "(Get-Content ACTUAL.TXT) -replace '#[0-9a-f]{8}', '#XXXXXXXX' -replace '(?<![0-9a-f#])[0-9a-f]{8}(?![0-9a-f])', 'XXXXXXXX' -replace '\d{4}-\d{2}-\d{2}', 'XXXX-XX-XX' | Set-Content ACTUAL-NORMALIZED.TXT"

REM Normalize IDs and dates in EXPECTED.TXT
powershell -Command "(Get-Content EXPECTED.TXT) -replace '#[0-9a-f]{8}', '#XXXXXXXX' -replace '(?<![0-9a-f#])[0-9a-f]{8}(?![0-9a-f])', 'XXXXXXXX' -replace '\d{4}-\d{2}-\d{2}', 'XXXX-XX-XX' | Set-Content EXPECTED-NORMALIZED.TXT"

REM Compare normalized files
FC ACTUAL-NORMALIZED.TXT EXPECTED-NORMALIZED.TXT >NUL && ECHO Test passed! || ECHO Test failed!
