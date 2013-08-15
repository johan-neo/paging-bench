package org.neo4j.bench.page.impl;

import java.nio.ByteBuffer;

import org.neo4j.bench.page.api.Page;

class SingleThreadedMemoryMappedPage extends AbstractPage
{
    private final ByteBuffer buffer;
    
    SingleThreadedMemoryMappedPage( ByteBuffer buffer, long startRecord, int pageSize, int recordSize )
    {
        super( startRecord, pageSize, recordSize );
        this.buffer = buffer;
    }

    public byte[] readRecord( long record )
    {
        byte[] recordData = new byte[recordSize()];
        buffer.position( (int) ((record - startRecord()) * recordSize()) );
        buffer.get( recordData );
        return recordData;
    }

    public void writeRecord( long record, byte[] data )
    {
        ByteBuffer duplicate = buffer.duplicate();
        duplicate.position( (int) ((record - startRecord()) * recordSize()) );
        duplicate.put( data );
    }

    public Page copy()
    {
        return new SingleThreadedMemoryMappedPage( buffer, startRecord(), pageSize(), recordSize() );
    }
    
}