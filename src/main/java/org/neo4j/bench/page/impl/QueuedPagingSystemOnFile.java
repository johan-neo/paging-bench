package org.neo4j.bench.page.impl;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.neo4j.bench.page.FileWithRecords;
import org.neo4j.bench.page.PageReferenceSynchronization;
import org.neo4j.bench.page.PageType;
import org.neo4j.bench.page.QueueNotificationType;
import org.neo4j.bench.page.Random;
import org.neo4j.bench.page.Record;
import org.neo4j.bench.page.api.SomethingWithRecords;

public class QueuedPagingSystemOnFile implements SomethingWithRecords
{
    private Queue<Operation> queue = new ConcurrentLinkedQueue<Operation>();
    
    private final QueueNotificationType notificationType; 
  
    private final FileWithRecords fwr; 
    private final PagingSystemOnFile psof;
    private final WorkerThread workerThread;
    
    public QueuedPagingSystemOnFile( FileWithRecords fwr, int targetPageSize, PageType pageType,
            PageReferenceSynchronization refSync, QueueNotificationType notificationType )
    {
        this.fwr = fwr;
        this.psof = new PagingSystemOnFile( fwr, targetPageSize, pageType, refSync );
        
        this.notificationType = notificationType;
        this.workerThread = new WorkerThread( queue, psof );
        workerThread.start();
    }
    
    private Record getRecord( long record )
    {
        Operation operation = new Operation( OperationType.READ, notificationType );
        queue.add( operation );
        operation.waitForCompletion();
        return operation.getRecord();
    }
    
    private void writeRecord( long record, byte[] data )
    {
        Operation operation = new Operation( OperationType.WRITE, notificationType );
        operation.setRecord( new Record( record, data ) );
        queue.add( operation );
        // not needed: operation.waitForCompletion();
    }
    
    public void close()
    {
        queue = null;
        workerThread.stopWorkingOnQueue();
    }
    
    class WorkerThread extends Thread
    {
        private volatile boolean workOnQueue = true;
        private final Queue<Operation> queue;
        private final PagingSystemOnFile psof;
        private final Random r;
        
        WorkerThread( Queue<Operation> queue, PagingSystemOnFile psof )
        {
            this.queue = queue;
            this.psof = psof;
            r = new Random( (int) psof.getNrOfRecords() );
        }
        
        @Override
        public void run()
        {
            while ( workOnQueue || queue.peek() != null )
            {
                Operation operation = queue.poll();
                if ( operation != null )
                {
                    if ( operation.operationType == OperationType.READ )
                    {
                        Record record = psof.readRandomRecord( r );
                        operation.setRecord( record );
                        operation.notifyCompleted();
                    }
                    else
                    {
                        psof.writeRecord( operation.getRecord().getRecordId(), operation.getRecord().getData()  );
                    }
                }
            }
            psof.close();
        }
        
        public void stopWorkingOnQueue()
        {
            workOnQueue = false;
        }
    }

    public Record readRandomRecord( final Random r )
    {
        long record = r.next();
        return getRecord( record );
    }

    public void writeRandomRecord( final Random r )
    {
        long record = r.next();
        byte[] data = new byte[ fwr.getRecordSize() ];
        for ( int i = 0; i < data.length; i++ )
        {
            data[i] = (byte) (record % 256);
        }
        writeRecord( record, data );
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

    public long getNrOfRecords()
    {
        return psof.getNrOfRecords();
    }
}
