#!/bin/bash

set -e -x

brokerHost() {
    echo `cf app simple-hive-broker | grep urls | cut -d " " -f 2`
}

serviceHost() {
    echo `cf app simple-hive-service | grep urls | cut -d " " -f 2`
}

assertBrokerAdminPasswordSet() {
    if [ "$BROKER_ADMIN_PASSWORD" = "" ]; then
        echo 'Error: Make sure BROKER_ADMIN_PASSWORD is set.'
        exit 1
    fi
}

hydrateEnv() {
    cf set-env simple-hive-broker SIMPLE_HIVE_SERVICE_HOST $(serviceHost)
    cf set-env simple-hive-broker SIMPLE_HIVE_SERVICE_PORT 80
    cf set-env simple-hive-broker SECURITY_USER_PASSWORD ${BROKER_ADMIN_PASSWORD}
}

cd $(dirname $0)

../gradlew :broker:assemble

assertBrokerAdminPasswordSet

hydrateEnv

cf push simple-hive-broker -f ../manifest.yml

cf purge-service-offering simple-hive -f

cf delete-service-broker simple-hive-broker -f

cf create-service-broker simple-hive-broker user ${BROKER_ADMIN_PASSWORD} http://$(brokerHost) --space-scoped