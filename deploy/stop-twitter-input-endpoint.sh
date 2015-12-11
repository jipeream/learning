#!/usr/bin/env bash
kill $(ps -ef | grep java | grep fsn-twitter-input-endpoint | awk '{print $2}')
 