#!/bin/bash

set -e -x

./gradlew :service:assemble

cf push simple-hive-service