package org.neo4j.bench.page.impl;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.neo4j.bench.page.api.Page;
import org.neo4j.bench.page.api.PageElement;

class AtomicPageElement implements PageElement
{
    final AtomicInteger nextVersion = new AtomicInteger();
    final AtomicInteger currentVersion = new AtomicInteger();
    final AtomicBoolean spinLockWriterWriting = new AtomicBoolean( false );
    
    final AtomicReference<Page> page;

    AtomicPageElement( Page page )
    {
        this.page = new AtomicReference<Page>( page );
    }
    
    public byte[] readRecord( long record )
    {
        int current; 
        do
        {
            current = currentVersion.get();
            Page pageToReadFrom = page.get();
            return pageToReadFrom.readRecord( record );
        } while ( current != nextVersion.get() );
    }

    public void writeRecord( long record, byte[] data )
    {
        while ( !spinLockWriterWriting.compareAndSet( false, true ) );
        try
        {
            int versionToSet;
            versionToSet = nextVersion.incrementAndGet();
            Page realPage = page.get();
            realPage.writeRecord( record, data );
            currentVersion.set( versionToSet );
        }
        finally
        {
            spinLockWriterWriting.set( false );
        }
    }
}