#!/usr/bin/env bash
kill $(ps -ef | grep java | grep FsnInputTwitterEndpointMain | grep italia | awk '{print $2}')
