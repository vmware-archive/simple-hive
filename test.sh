#!/usr/bin/env bash

set -e

fail() {
    echo failure
    exit 1
}

post() {
    curl -s -XPOST ${SAMPLE_CLIENT}/$1
}

get() {
    curl -s -XGET ${SAMPLE_CLIENT}/$1
}

assert() {
    if ! [ $1 = $2 ]; then
        fail
    fi
}

SAMPLE_CLIENT=sample-client.local.pcfdev.io

PLAYER_ONE=$RANDOM
PLAYER_TWO=$RANDOM

post ${PLAYER_ONE}
post ${PLAYER_ONE}
post ${PLAYER_TWO}

assert `get ${PLAYER_ONE}` 2
assert `get ${PLAYER_TWO}` 1