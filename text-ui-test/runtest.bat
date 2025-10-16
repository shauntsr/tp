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

java -jar %jarloc% < ..\..\text-ui-test\input.txt > ..\..\text-ui-test\ACTUAL.TXT

cd ..\..\text-ui-test

REM Normalize IDs and dates in ACTUAL.TXT - replace 8-char hex IDs with XXXXXXXX and dates with XXXX-XX-XX
powershell -Command "(Get-Content ACTUAL.TXT) -replace '#[a-f0-9]{8}', '#XXXXXXXX' -replace '([a-f0-9]{8})( |$)', 'XXXXXXXX$2' -replace '\d{4}-\d{2}-\d{2}', 'XXXX-XX-XX' | Set-Content ACTUAL-NORMALIZED.TXT"

REM Normalize IDs and dates in EXPECTED.TXT
powershell -Command "(Get-Content EXPECTED.TXT) -replace '#[a-f0-9]{8}', '#XXXXXXXX' -replace '([a-f0-9]{8})( |$)', 'XXXXXXXX$2' -replace '\d{4}-\d{2}-\d{2}', 'XXXX-XX-XX' | Set-Content EXPECTED-NORMALIZED.TXT"

REM Compare normalized files
FC ACTUAL-NORMALIZED.TXT EXPECTED-NORMALIZED.TXT >NUL && ECHO Test passed! || ECHO Test failed!
