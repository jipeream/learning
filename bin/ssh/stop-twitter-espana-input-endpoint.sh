#!/usr/bin/env bash
kill $(ps -ef | grep java | grep FsnInputTwitterEndpointMain | grep espana | awk '{print $2}')
