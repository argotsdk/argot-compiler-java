@echo off
rem The Argot Compiler.  Converts .argot files into .dictionary files.
rem Usage ac.bat <file.argot> [-o <file.dictionary>]

set ARGOT_HOME=%~dp0..

if exist "%ARGOT_HOME%\bin\ac.bat" goto gotArgotHome
echo The ARGOT_HOME environment variable is not defined correctly
echo Set the environment variable to the base directory.

goto end
:gotArgotHome



java -DARGOT_HOME=%ARGOT_HOME% -jar %ARGOT_HOME%\lib\argot-compiler-1.2.0.jar %1 %2 %3 %4 %5

:end
