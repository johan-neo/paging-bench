package org.neo4j.bench.page.api;

public interface SomethingWithRecords
{
    byte[] getRecord( long id );
    void writeRecord( long id, byte[] data );
    
    void close();
    
    long getNrOfRecords();
}
