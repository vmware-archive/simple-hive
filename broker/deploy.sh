#!/bin/bash

set -e

brokerHost() {
    echo `cf app simple-hive-broker | grep urls | cut -d " " -f 2`
}

serviceHost() {
    echo `cf app simple-hive-service | grep urls | cut -d " " -f 2`
}

setBrokerAdminPasswordIfNotSet() {
    if [ "$BROKER_ADMIN_PASSWORD" = "" ]; then
        BROKER_ADMIN_PASSWORD=$RANDOM
        echo
        echo "### Attention: BROKER_ADMIN_PASSWORD set to: $BROKER_ADMIN_PASSWORD"
        echo
    fi
}

hydrateEnv() {
    cf set-env simple-hive-broker SIMPLE_HIVE_SERVICE_HOST $(serviceHost)
    cf set-env simple-hive-broker SIMPLE_HIVE_SERVICE_PORT 80
    cf set-env simple-hive-broker SECURITY_USER_PASSWORD ${BROKER_ADMIN_PASSWORD}
}

cd $(dirname $0)

../gradlew :broker:clean :broker:assemble

setBrokerAdminPasswordIfNotSet

hydrateEnv

cf push simple-hive-broker -f ../manifest.yml

cf purge-service-offering simple-hive -f

cf delete-service-broker simple-hive-broker -f

cf create-service-broker simple-hive-broker user ${BROKER_ADMIN_PASSWORD} http://$(brokerHost) --space-scoped