#!/usr/bin/env bash
kill $(ps -ef | grep java | grep fsn-rss-input-endpoint | awk '{print $2}')
 