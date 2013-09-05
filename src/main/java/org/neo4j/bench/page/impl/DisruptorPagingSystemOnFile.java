package org.neo4j.bench.page.impl;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.neo4j.bench.page.FileWithRecords;
import org.neo4j.bench.page.PageSynchronization;
import org.neo4j.bench.page.PageType;
import org.neo4j.bench.page.QueueNotificationType;
import org.neo4j.bench.page.Random;
import org.neo4j.bench.page.Record;
import org.neo4j.bench.page.api.SomethingWithRecords;

import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;

// we want multiple threads -> single thread
// no a good fit
public class DisruptorPagingSystemOnFile implements SomethingWithRecords
{
    private final QueueNotificationType notificationType; 
  
    private final ExecutorService exec = Executors.newCachedThreadPool();
    
    private final PagingSystemOnFile psof;
    private final Disruptor<OperationEvent> disruptor = new Disruptor<OperationEvent>( OperationEvent.FACTORY, 1024, exec );
    private RingBuffer<OperationEvent> ringBuffer;
    
    public DisruptorPagingSystemOnFile( FileWithRecords fwr, int targetPageSize, PageType pageType, 
            QueueNotificationType notificationType )
    {
        this.psof = new PagingSystemOnFile( fwr, targetPageSize, pageType, PageSynchronization.NONE );
        this.notificationType = notificationType;
        
        ringBuffer = disruptor.start();
    }
    
    private static class OperationEvent
    {
        private Operation operation;
        
        public Operation getOperation()
        {
            return operation;
        }
        
        public void setOperation( Operation operation )
        {
            this.operation = operation;
        }
        
        public final static EventFactory<OperationEvent> FACTORY = new EventFactory<OperationEvent>()
        {

            public OperationEvent newInstance()
            {
                return new OperationEvent();
            }
        };
    }
    
    public byte[] getRecord( long record )
    {
        Operation operation = new Operation( OperationType.READ, notificationType );
        long sequence = ringBuffer.next();
        OperationEvent opEvent = ringBuffer.get( sequence );
        opEvent.setOperation( operation );
        ringBuffer.publish( sequence );
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
        long sequence = ringBuffer.next();
        OperationEvent opEvent = ringBuffer.get( sequence );
        opEvent.setOperation( operation );
        ringBuffer.publish( sequence );
    }
    
    public void close()
    {
        ringBuffer = null;
        disruptor.shutdown();
    }
    
    public long getNrOfRecords()
    {
        return psof.getNrOfRecords();
    }
}
