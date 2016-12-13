#!/bin/bash

set -e -x

./gradlew :sample-client:assemble

cf create-service simple-hive default hive

cf push sample-client