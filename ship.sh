#!/usr/bin/env bash

set -e

git pull -r

./gradlew clean build

./licenses.sh

git push