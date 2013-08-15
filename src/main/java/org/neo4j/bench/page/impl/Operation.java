package org.neo4j.bench.page.impl;

import org.neo4j.bench.page.QueueNotificationType;
import org.neo4j.bench.page.Record;


public class Operation
{
    private volatile boolean done = false;
    final QueueNotificationType notificationType;
    final OperationType operationType;
    private Record record;
    
    public Operation( OperationType operationType, QueueNotificationType notificationType )
    {
        this.operationType = operationType;
        this.notificationType = notificationType;
    }
    
    public void waitForCompletion()
    {
        if ( operationType == OperationType.READ )
        {
            switch( notificationType )
            {
            case NOTIFY:
                waitForNotify();
                break;
            case SPIN:
                waitUsingSpin();
                break;
            default:
                throw new RuntimeException( "" + notificationType );
            
            };
        }
    }
    
    public void notifyCompleted()
    {
        if ( operationType == OperationType.READ )
        {
            switch( notificationType )
            {
            case NOTIFY:
                notifyUsingNotify();
                break;
            case SPIN:
                notifyUsingSpin();
                break;
            default:
                throw new RuntimeException( "" + notificationType );
            
            };
        }
    }

    private void notifyUsingSpin()
    {
        done = true;
    }

    synchronized void notifyUsingNotify()
    {
        done = true;
        this.notify();
    }

    void waitUsingSpin()
    {
        while ( done == false );
    }

    synchronized void waitForNotify()
    {
        while ( done == false )
        {
            try
            {
                this.wait();
            }
            catch ( InterruptedException e )
            {
                Thread.interrupted();
            }
        }
    }
    
    void setRecord( Record record )
    {
        this.record = record;
    }
    
    Record getRecord()
    {
        return record;
    }
}