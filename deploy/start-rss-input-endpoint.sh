#!/usr/bin/env bash
. ./java-path.sh
nohup java -jar bin/fsn-rss-input-endpoint.jar &> log/fsn-rss-input-endpoint.nohup.out&
 