#!/usr/bin/env bash
. ./java-path.sh
./stop-twitter-input-endpoint.sh
nohup java -jar bin/fsn-twitter-input-endpoint.jar &> log/fsn-twitter-input-endpoint.nohup.out&
tail -f log/fsn-twitter-input-endpoint.nohup.out
 