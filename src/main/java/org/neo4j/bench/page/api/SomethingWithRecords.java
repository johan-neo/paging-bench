package org.neo4j.bench.page.api;

import org.neo4j.bench.page.Random;
import org.neo4j.bench.page.Record;

public interface SomethingWithRecords
{
    Record readRandomRecord( Random r );
    void writeRandomRecord( Random r );
    
    long readSeqAllRecords();
    long writeSeqAllRecord();
    
    void close();
    
    long getNrOfRecords();
}
