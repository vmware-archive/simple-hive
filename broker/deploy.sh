#!/bin/bash

set -e -x

brokerHost() {
    echo `cf app simple-hive-broker | grep urls | cut -d " " -f 2`
}

serviceHost() {
    echo `cf app simple-hive-service | grep urls | cut -d " " -f 2`
}

hydrateEnv() {
    cf set-env simple-hive-broker SIMPLE_HIVE_SERVICE_HOST $(serviceHost)
    cf set-env simple-hive-broker SIMPLE_HIVE_SERVICE_PORT 80
}

cd $(dirname $0)

../gradlew :broker:assemble

hydrateEnv

cf push simple-hive-broker -f ../manifest.yml

cf purge-service-offering simple-hive -f

cf delete-service-broker simple-hive-broker -f

cf create-service-broker simple-hive-broker user honey http://$(brokerHost) --space-scoped