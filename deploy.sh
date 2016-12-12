#!/bin/bash

set -e -x

./gradlew assemble

cf push

cf purge-service-offering simple-hive -f
cf delete-service-broker simple-hive-broker -f
cf create-service-broker simple-hive-broker user honey http://simple-hive-broker.local.pcfdev.io
cf enable-service-access simple-hive

cf create-service simple-hive default honey1
cf create-service simple-hive default honey2
cf create-service simple-hive default honey3

beeline \
    -u "jdbc:hive2://simple-hive-service.local.pcfdev.io/;transportMode=http;httpPath=simple-hive" \
    -e "show databases;"