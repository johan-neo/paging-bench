package org.neo4j.bench.page;

public class Record
{
    private final long recordId;    
    private final byte[] data;
    
    public Record( long recordId, byte[] data )
    {
        this.recordId = recordId;
        this.data = data;
    }
    
    public byte[] getData()
    {
        return data;
    }
    
    public long getRecordId()
    {
        return recordId;
    }
}
