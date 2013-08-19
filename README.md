paging-bench
============

Micro benchmark to evaluate different synchronization mechanisms for a paging system.


## Setup

Run ./bin/build.sh to produce a jar under target.

Run java -jar target/paging-bench-0.1.jar --help

	Usage: 
		--useFile <filename> | --useTemporaryFileWithSize <filesize> 
		--recordSize <size> 
		--nrOfThreads <nr> 
		--readWriteRatio <ratio> 
		--secondsToRun <seconds> 
		--secondsBetweenStats <seconds> 
		--doSeqRead 
		--doSeqWrite 
	
		--pageType <PLAIN | PLAIN_WITH_LIST | PLAIN_WITH_MAP | MEMORY_MAPPED | SINGLE_THREADED_MEMORY_MAPPED | SINGLE_THREADED_PLAIN> 
		--sync <NONE | ATOMIC | LOCK > 
		--pageSizeInKb <size kb> 
	
		--printConfiguration 
		--help 
		--verbose 
		--tsvOutput 
		--includeTimeInOutput'
	
Set a file to use (or use a temporary one). Typical file size to use is 1/4 of available RAM. 

For random read write set readWriteRatio (ex. 0.8 = 80% read, 20% write).

Set a page type and size (default MEMORY_MAPPED and 2kb).

## About

(Disclaimer: correctness for read visibility has not been verified)

This micro benchmark creates a memory view of data in a file dividing it into pages and then benchmarks different paging systems. 

### PLAIN, PLAIN_WITH_LIST

Based on heap arrays. For PLAIN, each time a page is written to the page will be copied then written to and finally updated using CAS. 
PLAIN_WITH_LIST keeps the first 5 writes in a list before copying the page.

### MEMORY_MAPPED

Will use FileChannel.map to memory map a region of the file corresponding to the page. ByteBuffer.duplicate is used to allow for concurrent updates.

### SINGLE_THREADED_PLAIN, SINGLE_THREADED_MEMORY_MAPPED  

Use a single thread to serve all reads and writes eating from a queue that request will be queued on. 
