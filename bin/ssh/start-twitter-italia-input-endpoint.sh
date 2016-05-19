#!/usr/bin/env bash
. ./java-path.sh
./stop-twitter-italia-input-endpoint.sh
nohup java -cp bin/fsn-input-endpoints-1.0-SNAPSHOT-jar-with-dependencies.jar com.fs.fsnews.input.endpoints.FsnInputTwitterEndpointMain -configDir=italia &> log/fsn-twitter-italia-input-endpoint.nohup.out&
tail -f log/fsn-twitter-italia-input-endpoint.nohup.out
 