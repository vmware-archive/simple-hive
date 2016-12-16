#!/bin/bash

set -e -x

./service/deploy.sh

./broker/deploy.sh

./sample-client/deploy.sh