package org.neo4j.bench.page.impl;

import org.neo4j.bench.page.api.Page;

class FakePageImpl extends AbstractPage
{
    FakePageImpl( byte[] data, long startRecord, int pageSize, int recordSize )
    {
        super( startRecord, pageSize, recordSize );
    }
    
    public byte[] readRecord( long record )
    {
        return null;
    }

    public void writeRecord( long record, byte[] data )
    {
    }

    public Page copy()
    {
        return new FakePageImpl( null, startRecord(), pageSize(), recordSize() );
    }
}