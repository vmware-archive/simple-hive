#!/usr/bin/env bash

set -e -x

beeline \
    -u "jdbc:hive2://$1/default;transportMode=http;httpPath=simple-hive" \
    -f $2