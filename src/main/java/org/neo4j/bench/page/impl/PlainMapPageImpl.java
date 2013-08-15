package org.neo4j.bench.page.impl;

import java.util.HashMap;
import java.util.Map;

import org.neo4j.bench.page.Record;
import org.neo4j.bench.page.api.Page;

class PlainMapPageImpl extends AbstractPage
{
    private byte[] data;
    private final Map<Long,Record> recordsWritten;
    private final int writeOutMapIndex;
    
    PlainMapPageImpl( byte[] data, long startRecord, int pageSize, int recordSize )
    {
        super( startRecord, pageSize, recordSize );
        this.data = data;
        this.recordsWritten = new HashMap<Long, Record>();
        this.writeOutMapIndex = pageSize / 3;
    }
    
    PlainMapPageImpl( byte[] data, long startRecord, int pageSize, int recordSize, Map<Long, Record> recordsWritten )
    {
        super( startRecord, pageSize, recordSize );
        this.data = data;
        this.recordsWritten = recordsWritten;
        this.writeOutMapIndex = pageSize / 3;
    }

    public byte[] readRecord( long record )
    {
        byte[] recordData = new byte[recordSize()];
        Record writtenRecord = recordsWritten.get( record );
        if ( writtenRecord != null )
        {
            System.arraycopy( writtenRecord.getData(), 0, recordData, 0, recordSize() );
            return recordData;
        }
        System.arraycopy( data, (int) ((record - startRecord()) * recordSize()), recordData, 0, recordSize() );
        return recordData;
    }

    public void writeRecord( long record, byte[] recordData )
    {
        Record recordWritten = recordsWritten.get( record );
        if ( recordWritten != null )
        {
            System.arraycopy( recordData, 0, recordWritten.getData(), 0, recordSize() );
            return;
        }            
        recordsWritten.put( record, new Record( record, recordData ) );
        if ( recordsWritten.size() >= writeOutMapIndex )
        {
            byte newData[] = new byte[ data.length ];
            System.arraycopy( newData, 0, data, 0, data.length );
            for ( Record writtenRecord : recordsWritten.values() )
            {
                System.arraycopy( writtenRecord.getData(), 0, newData, (int) ((writtenRecord.getRecordId() - startRecord()) * recordSize()), recordSize() );                  
            }
            recordsWritten.clear();
            this.data = newData;
        }
    }

    public Page copy()
    {
        return new PlainMapPageImpl( data, startRecord(), pageSize(), recordSize(), recordsWritten );
    }
}