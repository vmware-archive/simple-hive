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

clientHost() {
    echo `cf app sample-client | grep urls | cut -d " " -f 2`
}

SAMPLE_CLIENT=$(clientHost)

PLAYER_ONE=$RANDOM
PLAYER_TWO=$RANDOM

post ${PLAYER_ONE}
post ${PLAYER_ONE}
post ${PLAYER_TWO}

assert `get ${PLAYER_ONE}` 2
assert `get ${PLAYER_TWO}` 1