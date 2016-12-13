#!/bin/bash

set -e -x

./gradlew :sample-client:assemble

cf push sample-client