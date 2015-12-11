@echo off

if not exist .\out\artifacts goto error
if not exist .\deploy goto error

mkdir .\deploy\bin
xcopy /y .\out\artifacts\fsn_rss_input_endpoint_jar\*.* .\deploy\bin
xcopy /y .\out\artifacts\fsn_twitter_input_endpoint_jar\*.* .\deploy\bin

mkdir .\deploy\config
rem xcopy /y .\config\*.* .\deploy\config

mkdir .\deploy\log

goto exit

:error

echo *** ERROR ***

:exit

pause Presiona cualquier tecla