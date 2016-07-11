#!/bin/bash

fill_file=$1
priceupdate_file=$2

if [[ -n "$fill_file" ]]; then
    echo "fill file path: $fill_file"
else
    echo "missing fill file path"
    exit 1
fi

if [[ -n "$priceupdate_file" ]]; then
    echo "price update file path: $priceupdate_file"
else
    echo "missing price update file path"
    exit 1
fi

sbt "run --fills=$fill_file --prices=$priceupdate_file"
