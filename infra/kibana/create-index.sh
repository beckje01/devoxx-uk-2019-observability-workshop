#!/bin/sh

set -xeuo pipefail

if ! curl --retry 5 --retry-connrefused --retry-delay 0 -sf http://kibana:5601/api/saved_objects/_find?type=index-pattern; then
    curl -XPOST -D- 'http://kibana:5601/api/saved_objects/index-pattern' \
			-H 'Content-Type: application/json' \
			-H 'kbn-version: 6.6.1' \
			-d '{"attributes":{"title":"logstash-*","timeFieldName":"@timestamp"}}'
fi

echo 'Created Index'