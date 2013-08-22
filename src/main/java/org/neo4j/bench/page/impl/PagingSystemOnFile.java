package org.neo4j.bench.page.impl;

import org.neo4j.bench.page.FileWithRecords;
import org.neo4j.bench.page.PageSynchronization;
import org.neo4j.bench.page.PageType;
import org.neo4j.bench.page.Random;
import org.neo4j.bench.page.Record;
import org.neo4j.bench.page.api.Page;
import org.neo4j.bench.page.api.PageElement;
import org.neo4j.bench.page.api.SomethingWithRecords;

public class PagingSystemOnFile implements SomethingWithRecords
{
    private final FileWithRecords fwr; 
    private final int nrOfPages;
    private final int pageSizeBytes;
    private final int pageSizeRecords;    
    private final PageType pageType;
    private final PageSynchronization pageReferenceSync;
   
    private final PageElement[] pages;

    public PagingSystemOnFile( FileWithRecords fwr, int targetPageSize, PageType type, PageSynchronization refSync )
    {
        this.fwr = fwr;
        int recordSize = fwr.getRecordSize();
        if ( targetPageSize < recordSize )
        {
            throw new IllegalArgumentException( "Target page size to small " + targetPageSize );
        }
        this.pageSizeBytes = (targetPageSize / recordSize) * recordSize;
        this.pageSizeRecords = pageSizeBytes / recordSize;
        this.nrOfPages = (int) (fwr.getFileSize() / pageSizeBytes);
        fwr.setRecordCount( pageSizeRecords * nrOfPages );
        this.pageType = type;
        this.pageReferenceSync = refSync;
        this.pages = new PageElement[nrOfPages];
        setupPages();
    }
 

    public byte[] getRecord( long record )
    {
        PageElement pageElement = pages[ (int) (record / pageSizeRecords) ];
        return pageElement.readRecord( record );
    }
    
    public void writeRecord( long record, byte[] data )
    {
        int element = (int) (record / pageSizeRecords);
        PageElement pageElement = pages[ element ];
        pageElement.writeRecord( record, data );
    }

    public long readSeqAllRecords()
    {
        for ( long i = 0; i < fwr.getNrOfRecords(); i++ )
        {
            getRecord( i );
        }
        return fwr.getNrOfRecords();
    }

    public long writeSeqAllRecord()
    {
        for ( long i = 0; i < fwr.getNrOfRecords(); i++ )
        {
            writeRecord( i, new byte[ fwr.getRecordSize() ] );
        }
        return fwr.getNrOfRecords();
    }

    private void setupPages()
    {
        for ( int i = 0; i < pages.length; i++ )
        {
            long startRecord = i * pageSizeRecords;
            Page page;
            switch ( pageType )
            {
            case MEMORY_MAPPED:
                page = new MemoryMappedPage( fwr.map( startRecord, pageSizeRecords ), startRecord, pageSizeRecords, fwr.getRecordSize()  );
                break;
            case PLAIN:
                page = new PlainPageImpl( fwr.read( startRecord, pageSizeRecords ), startRecord, pageSizeRecords, fwr.getRecordSize()  );
                break;
            case FAKE:
                page = new FakePageImpl( null, startRecord, pageSizeRecords, fwr.getRecordSize()  );
                break;
            default:
                throw new RuntimeException( "Invalid enum " + pageType );
            }
            
            switch ( pageReferenceSync )
            {
            case ATOMIC:
                pages[i] = new AtomicPageElement( page ); 
                break;
            case NONE:
                pages[i] = new NoSyncPageElement( page );
                break;
            case LOCK:
                pages[i] = new ExclusiveLockPageElement( page );
                break;
            default:
                throw new RuntimeException( "Invalid enum " + pageReferenceSync );
            }
        }
    }
    
    public void close()
    {
    }
    
    public long getNrOfRecords()
    {
        return fwr.getNrOfRecords();
    }
}
