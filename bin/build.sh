mvn package
cd target && rm -rf package-build && mkdir package-build && cd package-build
unzip ../../lib/disruptor-2.10.4.jar && rm -rf META-INF
unzip ../paging-bench-0.1.jar
rm ../paging-bench-0.1.jar
jar cfM ../paging-bench-0.1.jar *
