#!/bin/bash

set -e -x

./deploy-service.sh
./deploy-broker.sh
./deploy-sample-client.sh

./test.sh