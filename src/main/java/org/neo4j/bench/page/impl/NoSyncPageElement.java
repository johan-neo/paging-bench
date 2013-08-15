package org.neo4j.bench.page.impl;

import org.neo4j.bench.page.api.Page;
import org.neo4j.bench.page.api.PageElement;

class NoSyncPageElement implements PageElement
{
    final Page page;
    
    NoSyncPageElement( Page page )
    {
        this.page = page;
    }

    public byte[] readRecord( long record )
    {
        return page.readRecord( record );
    }

    public void writeRecord( long record, byte[] data )
    {
        page.writeRecord( record, data );
    }
 }