#!/bin/bash

set -e -x

cd $(dirname $0)

../gradlew :sample-client:assemble

cf create-service simple-hive default hive

cf push sample-client -f ../manifest.yml