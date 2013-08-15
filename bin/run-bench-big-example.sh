#!/bin/sh

## suitable for big systems, 128G RAM

VM_OPTS="-Xmx64G -XX:+UseConcMarkSweepGC"
STATIC_ARGS="-jar target/paging-bench-0.1.jar --useFile target/data/testfile-big.records --recordSize 40 --tsvOutput"
SECONDS_TO_RUN="--secondsToRun 120 --secondsBetweenStats 120" 

READ_WRITE_RATIO="--readWriteRatio 0.8"
SYNC="--sync ATOMIC"

PAGE_TYPE="--pageType PLAIN_WITH_LIST"
echo $PAGE_TYPE
PAGE_SIZE="--pageSizeInKb 1"
echo $PAGE_SIZE
java $VM_OPTS $STATIC_ARGS $READ_WRITE_RATIO $SECONDS_TO_RUN $NR_OF_THREADS $PAGE_TYPE $PAGE_SIZE $SYNC --nrOfThreads 1
java $VM_OPTS $STATIC_ARGS $READ_WRITE_RATIO $SECONDS_TO_RUN $NR_OF_THREADS $PAGE_TYPE $PAGE_SIZE $SYNC --nrOfThreads 2
java $VM_OPTS $STATIC_ARGS $READ_WRITE_RATIO $SECONDS_TO_RUN $NR_OF_THREADS $PAGE_TYPE $PAGE_SIZE $SYNC --nrOfThreads 4
java $VM_OPTS $STATIC_ARGS $READ_WRITE_RATIO $SECONDS_TO_RUN $NR_OF_THREADS $PAGE_TYPE $PAGE_SIZE $SYNC --nrOfThreads 8

PAGE_SIZE="--pageSizeInKb 2"
echo $PAGE_SIZE
java $VM_OPTS $STATIC_ARGS $READ_WRITE_RATIO $SECONDS_TO_RUN $NR_OF_THREADS $PAGE_TYPE $PAGE_SIZE $SYNC --nrOfThreads 1
java $VM_OPTS $STATIC_ARGS $READ_WRITE_RATIO $SECONDS_TO_RUN $NR_OF_THREADS $PAGE_TYPE $PAGE_SIZE $SYNC --nrOfThreads 2
java $VM_OPTS $STATIC_ARGS $READ_WRITE_RATIO $SECONDS_TO_RUN $NR_OF_THREADS $PAGE_TYPE $PAGE_SIZE $SYNC --nrOfThreads 4
java $VM_OPTS $STATIC_ARGS $READ_WRITE_RATIO $SECONDS_TO_RUN $NR_OF_THREADS $PAGE_TYPE $PAGE_SIZE $SYNC --nrOfThreads 8

PAGE_SIZE="--pageSizeInKb 8"
echo $PAGE_SIZE
java $VM_OPTS $STATIC_ARGS $READ_WRITE_RATIO $SECONDS_TO_RUN $NR_OF_THREADS $PAGE_TYPE $PAGE_SIZE $SYNC --nrOfThreads 1
java $VM_OPTS $STATIC_ARGS $READ_WRITE_RATIO $SECONDS_TO_RUN $NR_OF_THREADS $PAGE_TYPE $PAGE_SIZE $SYNC --nrOfThreads 2
java $VM_OPTS $STATIC_ARGS $READ_WRITE_RATIO $SECONDS_TO_RUN $NR_OF_THREADS $PAGE_TYPE $PAGE_SIZE $SYNC --nrOfThreads 4
java $VM_OPTS $STATIC_ARGS $READ_WRITE_RATIO $SECONDS_TO_RUN $NR_OF_THREADS $PAGE_TYPE $PAGE_SIZE $SYNC --nrOfThreads 8

PAGE_SIZE="--pageSizeInKb 64"
echo $PAGE_SIZE
java $VM_OPTS $STATIC_ARGS $READ_WRITE_RATIO $SECONDS_TO_RUN $NR_OF_THREADS $PAGE_TYPE $PAGE_SIZE $SYNC --nrOfThreads 1
java $VM_OPTS $STATIC_ARGS $READ_WRITE_RATIO $SECONDS_TO_RUN $NR_OF_THREADS $PAGE_TYPE $PAGE_SIZE $SYNC --nrOfThreads 2
java $VM_OPTS $STATIC_ARGS $READ_WRITE_RATIO $SECONDS_TO_RUN $NR_OF_THREADS $PAGE_TYPE $PAGE_SIZE $SYNC --nrOfThreads 4
java $VM_OPTS $STATIC_ARGS $READ_WRITE_RATIO $SECONDS_TO_RUN $NR_OF_THREADS $PAGE_TYPE $PAGE_SIZE $SYNC --nrOfThreads 8

VM_OPTS="-Xmx6G -XX:+UseConcMarkSweepGC"
SYNC="--sync NONE"
PAGE_TYPE="--pageType MEMORY_MAPPED"
echo $PAGE_TYPE
PAGE_SIZE="--pageSizeInKb 4096"
echo $PAGE_SIZE 
java $VM_OPTS $STATIC_ARGS $READ_WRITE_RATIO $SECONDS_TO_RUN $NR_OF_THREADS $PAGE_TYPE $PAGE_SIZE $SYNC --nrOfThreads 1
java $VM_OPTS $STATIC_ARGS $READ_WRITE_RATIO $SECONDS_TO_RUN $NR_OF_THREADS $PAGE_TYPE $PAGE_SIZE $SYNC --nrOfThreads 2
java $VM_OPTS $STATIC_ARGS $READ_WRITE_RATIO $SECONDS_TO_RUN $NR_OF_THREADS $PAGE_TYPE $PAGE_SIZE $SYNC --nrOfThreads 4
java $VM_OPTS $STATIC_ARGS $READ_WRITE_RATIO $SECONDS_TO_RUN $NR_OF_THREADS $PAGE_TYPE $PAGE_SIZE $SYNC --nrOfThreads 8

PAGE_SIZE="--pageSizeInKb 16384"
echo $PAGE_SIZE
java $VM_OPTS $STATIC_ARGS $READ_WRITE_RATIO $SECONDS_TO_RUN $NR_OF_THREADS $PAGE_TYPE $PAGE_SIZE $SYNC --nrOfThreads 1
java $VM_OPTS $STATIC_ARGS $READ_WRITE_RATIO $SECONDS_TO_RUN $NR_OF_THREADS $PAGE_TYPE $PAGE_SIZE $SYNC --nrOfThreads 2
java $VM_OPTS $STATIC_ARGS $READ_WRITE_RATIO $SECONDS_TO_RUN $NR_OF_THREADS $PAGE_TYPE $PAGE_SIZE $SYNC --nrOfThreads 4
java $VM_OPTS $STATIC_ARGS $READ_WRITE_RATIO $SECONDS_TO_RUN $NR_OF_THREADS $PAGE_TYPE $PAGE_SIZE $SYNC --nrOfThreads 8

PAGE_SIZE="--pageSizeInKb 65536"
echo $PAGE_SIZE
java $VM_OPTS $STATIC_ARGS $READ_WRITE_RATIO $SECONDS_TO_RUN $NR_OF_THREADS $PAGE_TYPE $PAGE_SIZE $SYNC --nrOfThreads 1
java $VM_OPTS $STATIC_ARGS $READ_WRITE_RATIO $SECONDS_TO_RUN $NR_OF_THREADS $PAGE_TYPE $PAGE_SIZE $SYNC --nrOfThreads 2
java $VM_OPTS $STATIC_ARGS $READ_WRITE_RATIO $SECONDS_TO_RUN $NR_OF_THREADS $PAGE_TYPE $PAGE_SIZE $SYNC --nrOfThreads 4
java $VM_OPTS $STATIC_ARGS $READ_WRITE_RATIO $SECONDS_TO_RUN $NR_OF_THREADS $PAGE_TYPE $PAGE_SIZE $SYNC --nrOfThreads 8

PAGE_SIZE="--pageSizeInKb 524288"
echo $PAGE_SIZE
java $VM_OPTS $STATIC_ARGS $READ_WRITE_RATIO $SECONDS_TO_RUN $NR_OF_THREADS $PAGE_TYPE $PAGE_SIZE $SYNC --nrOfThreads 1
java $VM_OPTS $STATIC_ARGS $READ_WRITE_RATIO $SECONDS_TO_RUN $NR_OF_THREADS $PAGE_TYPE $PAGE_SIZE $SYNC --nrOfThreads 2
java $VM_OPTS $STATIC_ARGS $READ_WRITE_RATIO $SECONDS_TO_RUN $NR_OF_THREADS $PAGE_TYPE $PAGE_SIZE $SYNC --nrOfThreads 4
java $VM_OPTS $STATIC_ARGS $READ_WRITE_RATIO $SECONDS_TO_RUN $NR_OF_THREADS $PAGE_TYPE $PAGE_SIZE $SYNC --nrOfThreads 8

PAGE_TYPE="--pageType SINGLE_THREADED_MEMORY_MAPPED"
echo $PAGE_TYPE
SYNC="--sync NONE"
PAGE_SIZE="--pageSizeInKb 64"
echo $PAGE_SIZE 
java $VM_OPTS $STATIC_ARGS $READ_WRITE_RATIO $SECONDS_TO_RUN $NR_OF_THREADS $PAGE_TYPE $PAGE_SIZE $SYNC --nrOfThreads 1
java $VM_OPTS $STATIC_ARGS $READ_WRITE_RATIO $SECONDS_TO_RUN $NR_OF_THREADS $PAGE_TYPE $PAGE_SIZE $SYNC --nrOfThreads 2
java $VM_OPTS $STATIC_ARGS $READ_WRITE_RATIO $SECONDS_TO_RUN $NR_OF_THREADS $PAGE_TYPE $PAGE_SIZE $SYNC --nrOfThreads 4
java $VM_OPTS $STATIC_ARGS $READ_WRITE_RATIO $SECONDS_TO_RUN $NR_OF_THREADS $PAGE_TYPE $PAGE_SIZE $SYNC --nrOfThreads 8

PAGE_SIZE="--pageSizeInKb 512"
echo $PAGE_SIZE
java $VM_OPTS $STATIC_ARGS $READ_WRITE_RATIO $SECONDS_TO_RUN $NR_OF_THREADS $PAGE_TYPE $PAGE_SIZE $SYNC --nrOfThreads 1
java $VM_OPTS $STATIC_ARGS $READ_WRITE_RATIO $SECONDS_TO_RUN $NR_OF_THREADS $PAGE_TYPE $PAGE_SIZE $SYNC --nrOfThreads 2
java $VM_OPTS $STATIC_ARGS $READ_WRITE_RATIO $SECONDS_TO_RUN $NR_OF_THREADS $PAGE_TYPE $PAGE_SIZE $SYNC --nrOfThreads 4
java $VM_OPTS $STATIC_ARGS $READ_WRITE_RATIO $SECONDS_TO_RUN $NR_OF_THREADS $PAGE_TYPE $PAGE_SIZE $SYNC --nrOfThreads 8

PAGE_SIZE="--pageSizeInKb 2048"
echo $PAGE_SIZE
java $VM_OPTS $STATIC_ARGS $READ_WRITE_RATIO $SECONDS_TO_RUN $NR_OF_THREADS $PAGE_TYPE $PAGE_SIZE $SYNC --nrOfThreads 1
java $VM_OPTS $STATIC_ARGS $READ_WRITE_RATIO $SECONDS_TO_RUN $NR_OF_THREADS $PAGE_TYPE $PAGE_SIZE $SYNC --nrOfThreads 2
java $VM_OPTS $STATIC_ARGS $READ_WRITE_RATIO $SECONDS_TO_RUN $NR_OF_THREADS $PAGE_TYPE $PAGE_SIZE $SYNC --nrOfThreads 4
java $VM_OPTS $STATIC_ARGS $READ_WRITE_RATIO $SECONDS_TO_RUN $NR_OF_THREADS $PAGE_TYPE $PAGE_SIZE $SYNC --nrOfThreads 8

PAGE_SIZE="--pageSizeInKb 16384"
echo $PAGE_SIZE
java $VM_OPTS $STATIC_ARGS $READ_WRITE_RATIO $SECONDS_TO_RUN $NR_OF_THREADS $PAGE_TYPE $PAGE_SIZE $SYNC --nrOfThreads 1
java $VM_OPTS $STATIC_ARGS $READ_WRITE_RATIO $SECONDS_TO_RUN $NR_OF_THREADS $PAGE_TYPE $PAGE_SIZE $SYNC --nrOfThreads 2
java $VM_OPTS $STATIC_ARGS $READ_WRITE_RATIO $SECONDS_TO_RUN $NR_OF_THREADS $PAGE_TYPE $PAGE_SIZE $SYNC --nrOfThreads 4
java $VM_OPTS $STATIC_ARGS $READ_WRITE_RATIO $SECONDS_TO_RUN $NR_OF_THREADS $PAGE_TYPE $PAGE_SIZE $SYNC --nrOfThreads 8

VM_OPTS="-Xmx64G -XX:+UseConcMarkSweepGC"
VM_OPTS="-Xmx2500M -XX:+UseConcMarkSweepGC"
PAGE_TYPE="--pageType SINGLE_THREADED_PLAIN"
SYNC="--sync NONE"
echo $PAGE_TYPE
PAGE_SIZE="--pageSizeInKb 64"
echo $PAGE_SIZE 
java $VM_OPTS $STATIC_ARGS $READ_WRITE_RATIO $SECONDS_TO_RUN $NR_OF_THREADS $PAGE_TYPE $PAGE_SIZE $SYNC --nrOfThreads 1
java $VM_OPTS $STATIC_ARGS $READ_WRITE_RATIO $SECONDS_TO_RUN $NR_OF_THREADS $PAGE_TYPE $PAGE_SIZE $SYNC --nrOfThreads 2
java $VM_OPTS $STATIC_ARGS $READ_WRITE_RATIO $SECONDS_TO_RUN $NR_OF_THREADS $PAGE_TYPE $PAGE_SIZE $SYNC --nrOfThreads 4
java $VM_OPTS $STATIC_ARGS $READ_WRITE_RATIO $SECONDS_TO_RUN $NR_OF_THREADS $PAGE_TYPE $PAGE_SIZE $SYNC --nrOfThreads 8

PAGE_SIZE="--pageSizeInKb 512"
echo $PAGE_SIZE
java $VM_OPTS $STATIC_ARGS $READ_WRITE_RATIO $SECONDS_TO_RUN $NR_OF_THREADS $PAGE_TYPE $PAGE_SIZE $SYNC --nrOfThreads 1
java $VM_OPTS $STATIC_ARGS $READ_WRITE_RATIO $SECONDS_TO_RUN $NR_OF_THREADS $PAGE_TYPE $PAGE_SIZE $SYNC --nrOfThreads 2
java $VM_OPTS $STATIC_ARGS $READ_WRITE_RATIO $SECONDS_TO_RUN $NR_OF_THREADS $PAGE_TYPE $PAGE_SIZE $SYNC --nrOfThreads 4
java $VM_OPTS $STATIC_ARGS $READ_WRITE_RATIO $SECONDS_TO_RUN $NR_OF_THREADS $PAGE_TYPE $PAGE_SIZE $SYNC --nrOfThreads 8

PAGE_SIZE="--pageSizeInKb 2048"
echo $PAGE_SIZE
java $VM_OPTS $STATIC_ARGS $READ_WRITE_RATIO $SECONDS_TO_RUN $NR_OF_THREADS $PAGE_TYPE $PAGE_SIZE $SYNC --nrOfThreads 1
java $VM_OPTS $STATIC_ARGS $READ_WRITE_RATIO $SECONDS_TO_RUN $NR_OF_THREADS $PAGE_TYPE $PAGE_SIZE $SYNC --nrOfThreads 2
java $VM_OPTS $STATIC_ARGS $READ_WRITE_RATIO $SECONDS_TO_RUN $NR_OF_THREADS $PAGE_TYPE $PAGE_SIZE $SYNC --nrOfThreads 4
java $VM_OPTS $STATIC_ARGS $READ_WRITE_RATIO $SECONDS_TO_RUN $NR_OF_THREADS $PAGE_TYPE $PAGE_SIZE $SYNC --nrOfThreads 8

PAGE_SIZE="--pageSizeInKb 16384"
echo $PAGE_SIZE
java $VM_OPTS $STATIC_ARGS $READ_WRITE_RATIO $SECONDS_TO_RUN $NR_OF_THREADS $PAGE_TYPE $PAGE_SIZE $SYNC --nrOfThreads 1
java $VM_OPTS $STATIC_ARGS $READ_WRITE_RATIO $SECONDS_TO_RUN $NR_OF_THREADS $PAGE_TYPE $PAGE_SIZE $SYNC --nrOfThreads 2
java $VM_OPTS $STATIC_ARGS $READ_WRITE_RATIO $SECONDS_TO_RUN $NR_OF_THREADS $PAGE_TYPE $PAGE_SIZE $SYNC --nrOfThreads 4
java $VM_OPTS $STATIC_ARGS $READ_WRITE_RATIO $SECONDS_TO_RUN $NR_OF_THREADS $PAGE_TYPE $PAGE_SIZE $SYNC --nrOfThreads 8
