#!/bin/bash

set -e -x

cd $(dirname $0)

../gradlew :service:clean :service:assemble

cf push simple-hive-service -f ../manifest.yml