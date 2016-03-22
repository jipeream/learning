#!/usr/bin/env bash
. ./java-path.sh
./stop-rss-italia-input-endpoint.sh
nohup java -cp bin/fsn-input-endpoints-1.0-SNAPSHOT-jar-with-dependencies.jar com.fs.fsnews.input.endpoints.FsnInputRssEndpointMain -configDir=italia &> log/fsn-rss-italia-input-endpoint.nohup.out&
tail -f log/fsn-rss-italia-input-endpoint.nohup.out
