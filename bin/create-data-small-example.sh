#!/bin/sh

## creates a 1GB file

mkdir -p target/data
dd if=/dev/urandom of=target/data/testfile-small.records bs=10485760 count=100
