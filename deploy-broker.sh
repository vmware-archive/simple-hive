#!/bin/bash

set -e -x

./gradlew :broker:assemble

cf push simple-hive-broker

cf purge-service-offering simple-hive -f

cf delete-service-broker simple-hive-broker -f

cf create-service-broker simple-hive-broker user honey http://simple-hive-broker.local.pcfdev.io

cf enable-service-access simple-hive