package org.neo4j.bench.page.impl;

import java.util.concurrent.atomic.AtomicReference;

import org.neo4j.bench.page.api.Page;
import org.neo4j.bench.page.api.PageElement;

class AtomicPageElement implements PageElement
{
    final AtomicReference<Page> page;

    AtomicPageElement( Page page )
    {
        this.page = new AtomicReference<Page>( page );
    }
    
    public byte[] readRecord( long record )
    {
        // no guard against concurrent writes on same record use:
        // return page.get().readRecord( record );
        
        // re-read any read that happened concurrently with a write
        byte[] data;
        Page pageReadFrom;
        do
        {
           pageReadFrom = page.get();
           data = pageReadFrom.readRecord( record );
        } while ( pageReadFrom != page.get() );
        return data;
    }

    public void writeRecord( long record, byte[] data )
    {
        Page oldPage, newPage;
        do
        {
            oldPage = page.get();
            newPage = oldPage.copy();
            newPage.writeRecord( record, data );
        } while ( !page.compareAndSet( oldPage, newPage ) );
    }
}