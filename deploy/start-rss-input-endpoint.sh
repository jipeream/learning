#!/usr/bin/env bash
. ./java-path.sh
./stop-rss-input-endpoint.sh
nohup java -jar bin/fsn-rss-input-endpoint.jar &> log/fsn-rss-input-endpoint.nohup.out&
tail -f log/fsn-rss-input-endpoint.nohup.out
