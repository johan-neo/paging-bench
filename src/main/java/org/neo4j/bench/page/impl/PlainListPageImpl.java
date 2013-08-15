package org.neo4j.bench.page.impl;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.neo4j.bench.page.Record;
import org.neo4j.bench.page.api.Page;

class PlainListPageImpl extends AbstractPage
{
    private byte[] data;
    private final List<Record> recordsWritten;
    private final int shrinkIndex;
    
    PlainListPageImpl( byte[] data, long startRecord, int pageSize, int recordSize )
    {
        super( startRecord, pageSize, recordSize );
        this.data = data;
        this.recordsWritten = new CopyOnWriteArrayList<Record>();
        this.shrinkIndex = 5;
    }
    
    PlainListPageImpl( byte[] data, long startRecord, int pageSize, int recordSize, List<Record> recordsWritten )
    {
        super( startRecord, pageSize, recordSize );
        this.data = data;
        this.recordsWritten = recordsWritten;
        this.shrinkIndex = 5;
    }

    public byte[] readRecord( long record )
    {
        byte[] recordData = new byte[recordSize()];
        for ( Record writtenRecord : recordsWritten )
        {
            if ( writtenRecord.getRecordId() == record )
            {
                System.arraycopy( writtenRecord.getData(), 0, recordData, 0, recordSize() );
                return recordData;
            }
        }
        System.arraycopy( data, (int) ((record - startRecord()) * recordSize()), recordData, 0, recordSize() );
        return recordData;
    }

    public void writeRecord( long record, byte[] recordData )
    {
        for ( Record writtenRecord : recordsWritten )
        {
            if ( writtenRecord.getRecordId() == record )
            {
                System.arraycopy( recordData, 0, writtenRecord.getData(), 0, recordSize() );
                return;
            }
        }
        recordsWritten.add( new Record( record, recordData ) );
        if ( recordsWritten.size() >= shrinkIndex )
        {
            byte newData[] = new byte[data.length];
            System.arraycopy( newData, 0, data, 0, data.length );
            for ( Record writtenRecord : recordsWritten )
            {
                System.arraycopy( writtenRecord.getData(), 0, newData, (int) ((writtenRecord.getRecordId() - startRecord()) * recordSize()), recordSize() );                  
            }
            recordsWritten.clear();
            this.data = newData;
        }
    }

    public Page copy()
    {
        return new PlainListPageImpl( data, startRecord(), pageSize(), recordSize(), recordsWritten );
    }
}