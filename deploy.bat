@echo off

if not exist .\out\artifacts goto error
if not exist .\deploy\bin goto error

del .\deploy\fsinsights-*.*
xcopy /y .\out\artifacts\rss_input_endpoint_jar\*.* .\deploy\bin
ren .\deploy\fsinsights-input-endpoints.jar rss-input-endpoint.jar
xcopy /y .\out\artifacts\twitter_input_endpoint_jar\*.* .\deploy\
ren .\deploy\fsinsights-input-endpoints.jar twitter-input-endpoint.jar

goto exit

:error

echo *** ERROR ***

:exit

pause Presiona cualquier tecla