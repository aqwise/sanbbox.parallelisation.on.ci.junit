@echo off
setlocal enabledelayedexpansion

:: Set JAVA_HOME
:: set JAVA_HOME=C:\Users\atsybuls\.jdks\azul-21.0.4

:: Update PATH
:: set PATH=%JAVA_HOME%\bin;%PATH%

:: Set Maven executable path
:: set MavenCmd=C:\Users\atsybuls\maven\apache-maven-3.9.7\bin\mvn.cmd

:: Verify JAVA_HOME and PATH
echo JAVA_HOME is set to: %JAVA_HOME%
echo PATH is set to: %PATH%
echo Maven command is set to: %MavenCmd%

:: Number of chunks
set NUM_CHUNKS=%1

:: Chunk index (1-based)
set CHUNK_INDEX=%2

:: Get the list of test classes
for /f "tokens=*" %%i in ('%MavenCmd% -q exec:java -Dexec.mainClass=com.example.TestClassSorter') do (
set TEST_CLASSES=%%i
)

:: Print the raw output for debugging
echo Raw test classes output:
echo %TEST_CLASSES%

:: Convert space-separated string to array
setlocal enabledelayedexpansion
set TEST_CLASSES_ARRAY=
for %%i in (%TEST_CLASSES%) do (
set TEST_CLASSES_ARRAY=!TEST_CLASSES_ARRAY! %%i
)
endlocal & set TEST_CLASSES_ARRAY=%TEST_CLASSES_ARRAY:~1%

:: Total number of test classes
for /f "tokens=1* delims==" %%a in ('set TEST_CLASSES_ARRAY') do (
set /a TOTAL_TESTS+=1
)

:: Number of tests per chunk
set /a TESTS_PER_CHUNK=(TOTAL_TESTS + NUM_CHUNKS - 1) / NUM_CHUNKS

:: Calculate start and end index for this chunk
set /a START_INDEX=(CHUNK_INDEX - 1) * TESTS_PER_CHUNK
set /a END_INDEX=START_INDEX + TESTS_PER_CHUNK - 1

:: Ensure end index does not exceed total number of tests
if %END_INDEX% GEQ %TOTAL_TESTS% (
set END_INDEX=%TOTAL_TESTS% - 1
)

:: Extract the test classes for this chunk
setlocal enabledelayedexpansion
set CHUNK_TESTS=
for /L %%i in (%START_INDEX%,1,%END_INDEX%) do (
for /f "tokens=1* delims==" %%a in ('set TEST_CLASSES_ARRAY') do (
if %%a GTR %%i (
set CHUNK_TESTS=!CHUNK_TESTS!,%%b
)
)
)
endlocal & set CHUNK_TESTS=%CHUNK_TESTS:~1%

:: Join test classes with comma for Maven command
set TEST_CLASSES_STR=%CHUNK_TESTS%

:: Debug messages
echo Total test classes found: %TEST_CLASSES_ARRAY%
echo Test classes for chunk %CHUNK_INDEX%: %CHUNK_TESTS%
echo Running Maven command: %MavenCmd% test -Dtest=%TEST_CLASSES_STR%

:: Run the tests for this chunk
%MavenCmd% test -Dtest=%TEST_CLASSES_STR%

endlocal
