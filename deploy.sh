#!/bin/bash

set -e -x

./gradlew assemble

cf push