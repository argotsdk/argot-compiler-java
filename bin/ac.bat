@echo off
rem The Argot Compiler.  Converts .argot files into .dictionary files.
rem Usage ac.bat <file.argot> [-o <file.dictionary>]

java -jar %ARGOT_HOME%\lib\argot-compiler-1.3.b.jar %1 %2 %3 %4 %5

