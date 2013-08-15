package org.neo4j.bench.page;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;

public class FileWithRecords
{
    private final File file;
    private final RandomAccessFile raf;
    private final FileChannel fileChannel; 
    private long fileSize;
    private final int recordSize;
    
    private boolean deleteFileOnClose = false;
    
    public FileWithRecords( String fileName, int recordSize )
    {
        try
        {
            file = new File( fileName );
            long sizeBytes = file.length();
            if ( recordSize < 1 )
            {
                throw new IllegalArgumentException( "RecordSize: " + recordSize );
            }
            if ( sizeBytes < recordSize )
            {
                throw new IllegalArgumentException( "File SizeInBytes: " + sizeBytes );
            }
            this.recordSize = recordSize;
            this.raf = new RandomAccessFile( file, "rw" );
            this.fileSize = (sizeBytes / recordSize ) * recordSize;
            this.fileChannel = raf.getChannel();
        }
        catch ( IOException e )
        {
            throw new RuntimeException( e );
        }
    }
    
    public FileWithRecords( long sizeBytes, int recordSize )
    {
        try
        {
            if ( recordSize < 1 )
            {
                throw new IllegalArgumentException( "RecordSize: " + recordSize );
            }
            if ( sizeBytes < recordSize )
            {
                throw new IllegalArgumentException( "SizeInBytes: " + sizeBytes );
            }
            this.recordSize = recordSize;
            file = File.createTempFile( "" + System.currentTimeMillis(), "records" );
            this.raf = new RandomAccessFile( file, "rw" );
            this.fileSize = (sizeBytes / recordSize ) * recordSize;
            int bufferSize = 2*1024*1024;
            if ( bufferSize > fileSize )
            {
                bufferSize = (int) fileSize;
            }
            byte[] bytes = new byte[bufferSize];
            for ( int i = 0; i < bytes.length; i++ )
            {
                bytes[i] = (byte) (i % 256);
            }
            this.fileChannel = raf.getChannel();
            for ( int i = 0; i < (fileSize / bufferSize); i++ )
            {
                if ( fileChannel.write( ByteBuffer.wrap( bytes ) ) != bytes.length )
                {
                    throw new RuntimeException( "Unable to create tmp file" );
                }
            }
            deleteFileOnClose = true;
        }
        catch ( IOException e )
        {
            throw new RuntimeException( e );
        }
    }
    

    public long getFileSize()
    {
        return fileSize;
    }
    
    public int getRecordSize()
    {
        return recordSize;
    }
    
    public long getNrOfRecords()
    {
        return fileSize / recordSize;
    }
    
    // used to get break even between amount of records, 
    // amount of records per page and amount of pages
    public void setRecordCount( long recordCount )
    {
        try
        {
            this.fileSize = recordCount * recordSize;
            fileChannel.truncate( recordCount * recordSize );
        }
        catch ( IOException e )
        {
            throw new RuntimeException( e );
        }
    }
    
    public byte[] read( long fromRecord, int nrOfRecords )
    {
        long startPos = fromRecord * recordSize;
        int length = nrOfRecords * recordSize;
        if ( startPos < 0 || startPos >= fileSize )
        {
            throw new IllegalArgumentException( "Start record " + fromRecord );
        }
        if ( startPos + length > fileSize )
        {
            throw new IllegalArgumentException( "startPos=" + startPos + " + length=" + length + "(" + (startPos + length) + ") > fileSize=" + fileSize );
        }
        byte[] data = new byte[length];
        ByteBuffer buffer = ByteBuffer.wrap( data );  
        try
        {
            int read = fileChannel.read( buffer, startPos );
            if ( read != length )
            {
                throw new RuntimeException( "Only read " + read + " expected " + length );
            }
            return data;
        }
        catch ( IOException e )
        {
            throw new RuntimeException( e );
        }
    }
    
    public ByteBuffer map( long startRecord, int nrOfRecords )
    {
        long startPos = startRecord * recordSize;
        int length = nrOfRecords * recordSize;
        if ( startPos < 0 || startPos >= fileSize )
        {
            throw new IllegalArgumentException( "Start record " + startRecord );
        }
        if ( startPos + length > fileSize )
        {
            throw new IllegalArgumentException( "Nr of records " + nrOfRecords );
        }
        try
        {
            return fileChannel.map( MapMode.READ_WRITE, startPos, length );
        }
        catch ( IOException e )
        {
            throw new RuntimeException( e );
        }
    }
    
    public void close()
    {
        try
        {
            this.fileChannel.close();
            this.raf.close();
            if ( deleteFileOnClose )
            {
                this.file.delete();
            }
        }
        catch ( IOException e )
        {
            throw new RuntimeException( e );    
        }
    }

    public String getName()
    {
        return file.getName();
    }

    /* Not used since goal is to benchmark synchronization needed in paging system
    public void write( long startRecord, byte[] data )
    {
        long startPos = startRecord * recordSize;
        ByteBuffer buffer = ByteBuffer.wrap( data );
        int length = buffer.remaining();
        if ( startPos < 0 )
        {
            throw new IllegalArgumentException( "Start record " + startRecord );
        }
        if ( startPos + length > fileSize )
        {
            throw new IllegalArgumentException( "Buffer to large " + buffer.remaining() );
        }
        try
        {
            int written = fileChannel.write( buffer, startPos );
            if ( written != length )
            {
                throw new RuntimeException( "Only wrote " + written + " expected " + length );
            }
            buffer.flip();
        }
        catch ( IOException e )
        {
            throw new RuntimeException( e );
        }
    }*/ 
}
