package org.neo4j.bench.page;

import java.io.File;
import java.util.concurrent.atomic.AtomicLong;

import org.neo4j.bench.page.api.SomethingWithRecords;
import org.neo4j.bench.page.impl.PagingSystemOnFile;
import org.neo4j.bench.page.impl.QueuedPagingSystemOnFile;

public class PagingBenchmark
{
    private static final AtomicLong readCount = new AtomicLong();
    private static final AtomicLong writeCount = new AtomicLong();
    
    private static PageType pageType = PageType.MEMORY_MAPPED;
    private static PageSynchronization pageSync = PageSynchronization.NONE;
    private static PagingType pagingType = PagingType.DIRECT;
    private static int pageSizeKb = 2;
    
    private static boolean doSeqRead = false;
    private static boolean doSeqWrite = false;
    private static float readWriteRatio = -1.0f;
    private static int nrOfThreads = 1;
    private static int recordSize = 40;
    private static String fileName = null; 
    private static long tempFileSize = -1;
    
    private static int secondsToRun = 60;
    private static int secondsStatsTimePrint = 10;
    
    private static boolean printConfiguration = false;
    private static boolean verbose = false;
    private static boolean tsvOutput = false;
    private static boolean includeTime = false;
    
    private static void usage()
    {
        System.out.println( "\nUsage: \n" + 
                "\t--useFile <filename> | --useTemporaryFileWithSize <filesize> \n" +
                "\t--recordSize <size> \n" + 
                "\t--nrOfThreads <nr> \n" + 
                "\t--readWriteRatio <ratio> \n" +
                "\t--secondsToRun <seconds> \n" +
                "\t--secondsBetweenStats <seconds> \n" +
                "\t--doSeqRead \n" +
                "\t--doSeqWrite \n" +
                "\n" +
                "\t--pageType <PLAIN | PLAIN_WITH_LIST | PLAIN_WITH_MAP | MEMORY_MAPPED | SINGLE_THREADED_MEMORY_MAPPED | SINGLE_THREADED_PLAIN> \n" +
                "\t--sync <NONE | ATOMIC | LOCK> \n" +
                "\t--pageSizeInKb <size kb> \n" +
                "\n" +
                "\t--printConfiguration \n" +
                "\t--help \n" +
                "\t--verbose \n" +
                "\t--tsvOutput \n" + 
                "\t--includeTimeInOutput \n");
    }
    
    public static void main( String[] args )
    {
        parseConfiguration( args );
        FileWithRecords fwr;
        if ( fileName != null )
        {
            fwr = new FileWithRecords( fileName, recordSize );
        }
        else
        {
            fwr = new FileWithRecords( tempFileSize, recordSize );
        }
        
        SomethingWithRecords swr;
        switch ( pagingType )
        {
        case DIRECT:
            swr = new PagingSystemOnFile( fwr, pageSizeKb*1024, pageType, pageSync );
            break;
        case QUEUED:
            swr = new QueuedPagingSystemOnFile( fwr, pageSizeKb*1024, pageType, pageSync, QueueNotificationType.SPIN );
            break;
        default:
            throw new RuntimeException( "" + pagingType );
        
        };
                
        if ( printConfiguration )
        {
            printConfiguraiton( fwr );
        }
        long startTime, endTime;
        if ( doSeqRead )
        {
            startTime = System.currentTimeMillis();
            long count = swr.readSeqAllRecords();
            endTime = System.currentTimeMillis();
            System.out.println( "SeqRead (records/s): " + count * 1000.0f / ( endTime - startTime) );
        }
        if ( doSeqWrite )
        {
            startTime = System.currentTimeMillis();
            long count = swr.writeSeqAllRecord();
            endTime = System.currentTimeMillis();
            System.out.println( "SeqWrite (records/s): " + count * 1000.0f / ( endTime - startTime) );
        }
        if ( readWriteRatio != -1.0f )
        {
            doRandomReadWriteLoad( swr );
        }
        if ( !doSeqRead && !doSeqWrite && readWriteRatio == -1.0f )
        {
            usage();
        }
        swr.close();
        fwr.close();
    }

    private static void doRandomReadWriteLoad( SomethingWithRecords swr )
    {
        long startTime;
        long endTime;
        RrwThread[] workingThreads = new RrwThread[nrOfThreads];
        for ( int i = 0; i < workingThreads.length; i++ )
        {
            workingThreads[i] = new RrwThread( readWriteRatio, secondsToRun, swr );
            workingThreads[i].start();
        }
        String verboseString = "";
        if ( verbose )
        {
            verboseString = "(" + nrOfThreads + " " + readWriteRatio + " " +  pageType + " " + pageSync + ") ";
        }
        startTime = System.currentTimeMillis();
        endTime = startTime;
        int nrOfPrints = 0;
        do
        {
            long interTime = endTime;
            try
            {
                Thread.sleep( secondsStatsTimePrint * 1000 );
            }
            catch ( InterruptedException e )
            {
                Thread.interrupted();
            }
            long writes = writeCount.getAndSet( 0 );
            long reads = readCount.getAndSet( 0 );
            endTime = System.currentTimeMillis();
            double seconds = (endTime - interTime ) / 1000.0d;
            int writesPerSecond = (int) (writes / seconds);
            int readsPerSecond = (int) (reads / seconds);
            String timeString = "";
            if ( includeTime )
            {
                if ( !tsvOutput )
                {
                    timeString = "after " + (secondsStatsTimePrint * ++nrOfPrints) + "s ";
                }
                else
                {
                    timeString = (secondsStatsTimePrint * ++nrOfPrints) + "\t";
                }
            }
            if ( !tsvOutput )
            {
                
                System.out.println( timeString + "using " + nrOfThreads + " threads reading " + verboseString + "at " + readsPerSecond + " records/s using page size " + pageSizeKb + "kb" );
                System.out.println( timeString + "using " + nrOfThreads + " threads writing " + verboseString + "at " + writesPerSecond + " records/s using page size " + pageSizeKb + "kb" );
            }
            else
            {
                
                System.out.println( timeString + nrOfThreads + "\t" + readsPerSecond + "\t" + writesPerSecond + "\t" + pageSizeKb );
            }
        } while ( (endTime - startTime) < secondsToRun * 1000 );
    }
    
    static void incrementReadRecordCount( long count )
    {
        readCount.addAndGet( count );
    }
    
    static void incrementWriteRecordCount( long count )
    {
        writeCount.addAndGet( count );        
    }
    
    static class RrwThread extends Thread
    {
        private final int secondsToRun;
        private final float readWriteRatio;
        private final SomethingWithRecords swr;
        
        private final Random r;
        
        RrwThread( float readWriteRatio, int secondsToRun, SomethingWithRecords swr )
        {
            this.readWriteRatio = readWriteRatio;
            this.secondsToRun = secondsToRun;
            this.swr = swr;
            r = new Random( (int) swr.getNrOfRecords() );
        }
        
        @Override
        public void run()
        {
            final int READ_WRITE_ITERATION_COUNT = 1000;
            final int READ_OR_WRITE_COUNT = 100;
            long startTime = System.currentTimeMillis();
            long endTime;
            try
            {
            do
            {
                int readCount = 0, writeCount = 0;
                
                for ( int j = 0; j < READ_WRITE_ITERATION_COUNT; j++ )
                {
                    float f = r.nextFloat();
                    if ( f <= readWriteRatio )
                    {
                        for ( int i = 0; i < READ_OR_WRITE_COUNT; i++ )
                        {
                            swr.readRandomRecord( r );
                        }
                        readCount += READ_OR_WRITE_COUNT;
                    }
                    else
                    {
                        for ( int i = 0; i < READ_OR_WRITE_COUNT; i++ )
                        {
                            swr.writeRandomRecord( r );
                        }
                        writeCount += READ_OR_WRITE_COUNT;
                    }
                }
                incrementReadRecordCount( readCount );
                incrementWriteRecordCount( writeCount );
                endTime = System.currentTimeMillis();
            } while ( (endTime - startTime) < (secondsToRun * 1000) );
            }
            catch ( NullPointerException e )
            {
                // way to terminitate 
            }
        }
    }
    
    private static void parseConfiguration( String[] args )
    {
        for ( int i = 0; i < args.length; i++ )
        {
            try
            {
                String arg = args[i].toLowerCase();
                if ( arg.equals( "--usefile" ) )
                {
                    fileName = args[++i];
                    if ( !new File( fileName ).exists() )
                    {
                        throw new IllegalArgumentException( "No such file " + fileName );
                    }
                }
                else if ( arg.equals( "--recordsize" ) )
                {
                    recordSize = Integer.parseInt( args[++i] );
                }
                else if ( arg.equals( "--nrofthreads" ) )
                {
                    nrOfThreads = Integer.parseInt( args[++i] );
                    if ( nrOfThreads < 1 )
                    {
                        throw new IllegalArgumentException( "Number of threads: " + nrOfThreads );
                    }
                }
                else if ( arg.equals( "--readwriteratio" ) )
                {
                    readWriteRatio = Float.parseFloat( args[++i] );
                    if ( readWriteRatio < 0 || readWriteRatio > 1 )
                    {
                        throw new IllegalArgumentException( "ReadWriteRatio: " + readWriteRatio );
                    }
                }
                else if ( arg.equals( "--secondstorun" ) )
                {
                    secondsToRun = Integer.parseInt( args[++i] );
                    if ( secondsToRun < 1 )
                    {
                        throw new IllegalArgumentException( "SecondsToRun: " + secondsToRun );
                    }
                }
                else if ( arg.equals( "--secondsbetweenstats" ) )
                {
                    secondsStatsTimePrint = Integer.parseInt( args[++i] );
                    if ( secondsStatsTimePrint < 1 )
                    {
                        throw new IllegalArgumentException( "SecondsBetweenStats: " + secondsStatsTimePrint );
                    }
                }
                else if ( arg.equals( "--usetemporaryfilewithsize" ) )
                {
                    tempFileSize = Integer.parseInt( args[++i] );
                }
                else if ( arg.equals( "--doseqread" ) )
                {
                    doSeqRead = true;
                }
                else if ( arg.equals( "--doseqwrite" ) )
                {
                    doSeqWrite = true; 
                }
                else if ( arg.equals( "--pagetype" ) )
                {
                    pagingType = PagingType.DIRECT;
                    String pageTypeString = args[++i].toUpperCase(); 
                    if ( pageTypeString.equals( PageType.PLAIN.name() ) )
                    {
                        pageType = PageType.PLAIN;
                    }
                    else if ( pageTypeString.equals( PageType.PLAIN_WITH_LIST.name() ) )
                    {
                        pageType = PageType.PLAIN_WITH_LIST;
                    }
                    else if ( pageTypeString.equals( PageType.PLAIN_WITH_MAP.name() ) )
                    {
                        pageType = PageType.PLAIN_WITH_MAP;
                    }
                    else if ( pageTypeString.equals( PageType.MEMORY_MAPPED.name() ) )
                    {
                        pageType = PageType.MEMORY_MAPPED;
                    }
                    else if ( pageTypeString.equals( PageType.SINGLE_THREADED_MEMORY_MAPPED.name() ) )
                    {
                        pageType = PageType.SINGLE_THREADED_MEMORY_MAPPED;
                        pagingType = PagingType.QUEUED;
                        pageSync = PageSynchronization.NONE;
                    }
                    else if ( pageTypeString.equals( PageType.SINGLE_THREADED_PLAIN.name() ) )
                    {
                        pageType = PageType.SINGLE_THREADED_PLAIN;
                        pagingType = PagingType.QUEUED;
                        pageSync = PageSynchronization.NONE;
                    }
                    else
                    {
                        throw new IllegalArgumentException( "PageType: " + pageTypeString );
                    }
                }
                else if ( arg.equals( "--sync" ) )
                {
                    String syncString = args[++i].toUpperCase();
                    if ( syncString.equals( PageSynchronization.ATOMIC.name() ) )
                    {
                        if ( pagingType == PagingType.QUEUED )
                        {
                            System.out.println( "Ignoring ATOMIC on SINGLE THREADED page type" );
                        }
                        else
                        {
                            pageSync = PageSynchronization.ATOMIC;
                        }
                    }
                    else if ( syncString.equals( PageSynchronization.NONE.name() ) )
                    { 
                        pageSync = PageSynchronization.NONE;
                    }
                    else if ( syncString.equals(  PageSynchronization.LOCK.name() ) )
                    {
                        pageSync = PageSynchronization.LOCK;
                    }
                    else
                    {
                        throw new IllegalArgumentException( "Sync: " + syncString );
                    }
                }
                else if ( arg.equals( "--pagesizeinkb" ) )
                {
                    pageSizeKb = Integer.parseInt(args[++i] );
                    if ( pageSizeKb < 1 )
                    {
                        throw new IllegalArgumentException( "PageSizeInKb: " + pageSizeKb );
                    }
                }
                else if ( arg.equals( "--printconfiguration" ) )
                {
                    printConfiguration = true; 
                }
                else if ( arg.equals( "--verbose" ) )
                {
                    verbose = true; 
                }
                else if ( arg.equals(  "--includetimeinoutput" ) )
                {
                    includeTime = true;
                }
                else if ( arg.equals( "--tsvoutput" ) )
                {
                    tsvOutput = true;
                }
                else if ( arg.equals( "--help" ) )
                {
                    usage();
                    System.exit( 0 );
                }
                else
                {
                    usage();
                    throw new IllegalArgumentException( "Unkown argument " + arg );
                }
            }
            catch ( IndexOutOfBoundsException e )
            {
                System.out.println( "Index out of bounds after " + args[i-1] );
                throw e;
            }
        }
    }

    private static void printConfiguraiton(FileWithRecords fwr)
    {
        System.out.println( "\nConfiguration{" +
                " file=" + fwr.getName() + 
                " size=" + fwr.getFileSize() + 
                " recordSize=" + fwr.getRecordSize() +
                " numberOfRecords=" + fwr.getNrOfRecords() + 
                " nrOfThreads=" + nrOfThreads + 
                " readWriteRatio=" + readWriteRatio + 
                " secondsToRun=" + secondsToRun +
                " secondsBetweenStats=" + secondsStatsTimePrint +
                " doSeqRead=" + doSeqRead + 
                " doSeqWrite=" + doSeqWrite +
                " pageType=" + pageType + 
                " sync=" + pageSync +
                " pagingType=" + pagingType +
                " pageSizeInKb=" + pageSizeKb + 
                " verbose=" + verbose + 
                " tsvFormat=" + tsvOutput + 
                " includeTime=" + includeTime + " }" );
        if ( tsvOutput )
        {
            String timeString = "";
            if ( includeTime )
            {
                timeString = "TIME\t";
            }
            System.out.println( timeString + "NR_OF_THREADS\t" + "READ\t" + "WRITE\t" + "PAGE_SIZE_KB" );
        }
    }
}
