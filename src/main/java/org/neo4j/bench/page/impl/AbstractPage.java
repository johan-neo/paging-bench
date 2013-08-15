package org.neo4j.bench.page.impl;

import org.neo4j.bench.page.api.Page;

abstract class AbstractPage implements Page
{
    private final long startRecord;
    private final int pageSize;
    private final int recordSize;
    
    AbstractPage( long startRecord, int pageSize, int recordSize )
    {
        this.startRecord = startRecord;
        this.pageSize = pageSize;
        this.recordSize = recordSize;
    }
    
    public long startRecord()
    {
        return startRecord;
    }

    public int pageSize()
    {
        return pageSize;
    }

    public int recordSize()
    {
        return recordSize;
    }
}