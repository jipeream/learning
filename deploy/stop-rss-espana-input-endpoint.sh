#!/usr/bin/env bash
kill $(ps -ef | grep java | grep FsnInputRssEndpointMain | grep espana | awk '{print $2}')
