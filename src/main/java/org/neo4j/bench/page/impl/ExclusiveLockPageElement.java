package org.neo4j.bench.page.impl;

import java.util.LinkedList;

import org.neo4j.bench.page.api.Page;
import org.neo4j.bench.page.api.PageElement;

class ExclusiveLockPageElement implements PageElement
{
    private final Page page;
    
    private Thread lockingThread = null;
    private final LinkedList<LockElement> waitingThreadList = 
        new LinkedList<LockElement>();
    private boolean locked;

    ExclusiveLockPageElement( Page page )
    {
        this.page = page;
    }
    
    public byte[] readRecord( long record )
    {
        lock();
        try
        {
            return page.readRecord( record );
        }
        finally
        {
            unLock();
        }
    }

    public void writeRecord( long record, byte[] data )
    {
        lock();
        try
        {
            page.writeRecord( record, data );
        }
        finally
        {
            unLock();
        }
    }


    private static class LockElement
    {
        private final Thread thread;
        private boolean movedOn = false;
        
        LockElement( Thread thread )
        {
            this.thread = thread;
        }
    }
    
    synchronized void lock()
    {
        Thread currentThread = Thread.currentThread();
        LockElement le = new LockElement( currentThread );
        while ( locked && lockingThread != currentThread )
        {
            waitingThreadList.addFirst( le );
            try
            {
                wait();
            }
            catch ( InterruptedException e )
            {
                Thread.interrupted();
            }
        }
        locked = true;
        lockingThread = currentThread;
        le.movedOn = true;
    }
    
    synchronized void unLock()
    {
        Thread currentThread = Thread.currentThread();
        if ( !locked )
        {
            throw new RuntimeException( "" + currentThread
                + " don't have lock on " + this );
        }
        locked = false;
        lockingThread = null;
        while ( !waitingThreadList.isEmpty() )
        {
            LockElement le = waitingThreadList.removeLast();
            if ( !le.movedOn )
            {
                le.thread.interrupt();
                break;
            }
        }
    }
}