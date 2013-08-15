package org.neo4j.bench.page.impl;

import org.neo4j.bench.page.api.Page;

class PlainPageImpl extends AbstractPage
{
    private final byte[] data;
    
    PlainPageImpl( byte[] data, long startRecord, int pageSize, int recordSize )
    {
        super( startRecord, pageSize, recordSize );
        this.data = data;
    }
    
    public byte[] readRecord( long record )
    {
        byte[] recordData = new byte[recordSize()];
        System.arraycopy( data, (int) ((record - startRecord()) * recordSize()), recordData, 0, recordSize() );
        return recordData;
    }

    public void writeRecord( long record, byte[] data )
    {
        System.arraycopy( data, 0, this.data, (int) ((record - startRecord()) * recordSize()), recordSize() );
    }

    public Page copy()
    {
        byte newData[] = new byte[pageSize() * recordSize() ];
        System.arraycopy( newData, 0, data, 0, newData.length );
        return new PlainPageImpl( newData, startRecord(), pageSize(), recordSize() );
    }
}