package org.neo4j.bench.page.impl;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.neo4j.bench.page.FileWithRecords;
import org.neo4j.bench.page.PageSynchronization;
import org.neo4j.bench.page.PageType;
import org.neo4j.bench.page.QueueNotificationType;
import org.neo4j.bench.page.Random;
import org.neo4j.bench.page.Record;
import org.neo4j.bench.page.api.SomethingWithRecords;

public class QueuedPagingSystemOnFile implements SomethingWithRecords
{
    private LinkedBlockingQueue<Operation> queue = new LinkedBlockingQueue<Operation>( 2000000 );
    
    private final QueueNotificationType notificationType; 
  
    // private final FileWithRecords fwr;
    
    private final PagingSystemOnFile psof;
    private final WorkerThread workerThread;
    
    public QueuedPagingSystemOnFile( FileWithRecords fwr, int targetPageSize, PageType pageType, 
            QueueNotificationType notificationType )
    {
        // this.fwr = fwr;
        this.psof = new PagingSystemOnFile( fwr, targetPageSize, pageType, PageSynchronization.NONE );
        
        this.notificationType = notificationType;
        this.workerThread = new WorkerThread( queue, psof );
        workerThread.start();
    }
    
    public byte[] getRecord( long record )
    {
        Operation operation = new Operation( OperationType.READ, notificationType );
        try
        {
            queue.offer( operation, 5, TimeUnit.SECONDS );
        }
        catch ( InterruptedException e )
        {
            Thread.interrupted();
        }
        operation.waitForCompletion();
        if ( operation.getRecord() != null )
        {
            return operation.getRecord().getData();
        }
        return null;
    }
    
    public void writeRecord( long record, byte[] data )
    {
        Operation operation = new Operation( OperationType.WRITE, notificationType );
        operation.setRecord( new Record( record, data ) );
        try
        {
            queue.put( operation);
        }
        catch ( InterruptedException e )
        {
            Thread.interrupted();
        }
    }
    
    public void close()
    {
        queue = null;
        workerThread.stopWorkingOnQueue();
    }
    
    class WorkerThread extends Thread
    {
        private volatile boolean workOnQueue = true;
        private final LinkedBlockingQueue<Operation> queue;
        private final PagingSystemOnFile psof;
        private final Random r;
        
        WorkerThread( LinkedBlockingQueue<Operation> queue, PagingSystemOnFile psof )
        {
            this.queue = queue;
            this.psof = psof;
            r = new Random( (int) psof.getNrOfRecords() );
        }
        
        @Override
        public void run()
        {
            while ( workOnQueue ) // || queue.peek() != null )
            {
                Operation operation = null;
                try
                {
                    operation = queue.poll( 5, TimeUnit.SECONDS );
                }
                catch ( InterruptedException e )
                {
                    interrupted();
                }
                if ( operation != null )
                {
                    if ( operation.operationType == OperationType.READ )
                    {
                        long id = r.next();
                        byte[] data = psof.getRecord( id );
                        operation.setRecord( new Record( id, data ) );
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

    public long getNrOfRecords()
    {
        return psof.getNrOfRecords();
    }
}
