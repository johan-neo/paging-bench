package org.neo4j.bench.page.api;

public interface PageElement
{
    byte[] readRecord( long record );

    void writeRecord( long record, byte[] data );
}
