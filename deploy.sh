#!/bin/bash

set -e -x

./gradlew clean assemble

cf push simple-hive-service

cf push simple-hive-broker

cf purge-service-offering simple-hive -f
cf delete-service-broker simple-hive-broker -f
cf create-service-broker simple-hive-broker user honey http://simple-hive-broker.local.pcfdev.io
cf enable-service-access simple-hive

cf create-service simple-hive default hive

cf push sample-client

# A Sample Test

curl -XPOST http://sample-client.local.pcfdev.io/Kaveh
curl -XPOST http://sample-client.local.pcfdev.io/Kaveh
curl -XPOST http://sample-client.local.pcfdev.io/Max

if ! [ `curl -XGET http://sample-client.local.pcfdev.io/Kaveh` = "2" ]; then
   exit 1
fi

if ! [ `curl -XGET http://sample-client.local.pcfdev.io/Max` = "1" ]; then
   exit 1
fi
