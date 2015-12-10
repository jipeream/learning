#!/usr/bin/env bash
. ./java-path.sh
nohup java -jar bin/fsn-twitter-input-endpoint.jar &> log/fsn-twitter-input-endpoint.nohup.out&
 