package org.neo4j.bench.page;

// pre-calculates random values  
public class Random
{
    private final int SIZE = 0x7FFFFF; // 8M
    private final int[] values = new int[SIZE+1];
    private final int upTo;
    private int index = 0;
    
    public Random( int upTo )
    {
        this.upTo = upTo;
        java.util.Random r = new java.util.Random( System.currentTimeMillis() );
        for ( int i = 0; i < values.length; i++ )
        {
            values[i] = r.nextInt( upTo );
        }
    }
    
    public int next()
    {
        return values[index++ & SIZE];
    }

    public float nextFloat()
    {
        float f = upTo;
        return next() / f;
    }
}
