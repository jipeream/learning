@echo off

if not exist .\out\artifacts goto error
if not exist .\deploy goto error

rem del .\deploy\bin\fsinsights-*.*

mkdir .\deploy\bin
xcopy /y .\out\artifacts\rss_input_endpoint_jar\*.* .\deploy\bin
ren .\deploy\bin\fsinsights-learning.jar fsn-rss-input-endpoint.jar
xcopy /y .\out\artifacts\twitter_input_endpoint_jar\*.* .\deploy\bin
ren .\deploy\bin\fsinsights-learning.jar fsn-twitter-input-endpoint.jar

mkdir .\deploy\bin\config
xcopy /y .\config\*.* .\deploy\bin\config

goto exit

:error

echo *** ERROR ***

:exit

pause Presiona cualquier tecla