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

		--pageType <PLAIN | MEMORY_MAPPED | FAKE> 
		--sync <NONE | ATOMIC | LOCK | QUEUE | DISRUPTOR> 
		--pageSizeInKb <size kb> 

		--printConfiguration 
		--help 
		--verbose 
		--tsvOutput 
		--includeTimeInOutput 
