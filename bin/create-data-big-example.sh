#!/bin/sh

## creates a 32GB file with random data

mkdir -p target/data
dd if=/dev/urandom of=target/data/testfile-big.records bs=1G  count=32
