#!/usr/bin/env bash
kill $(ps -ef | grep java | grep FsnInputRssEndpointMain | grep italia | awk '{print $2}')
