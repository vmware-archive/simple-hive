#!/bin/bash

set -e -x

./gradlew :broker:assemble

cf push simple-hive-broker