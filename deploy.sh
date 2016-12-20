#!/bin/bash

set -e -x

./gradlew clean

./service/deploy.sh

./broker/deploy.sh

./sample-client/deploy.sh