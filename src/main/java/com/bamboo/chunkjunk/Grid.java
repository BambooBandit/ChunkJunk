package com.bamboo.chunkjunk;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public class Grid
{
    public Array<Chunk> chunks = new Array<>();
    float chunkSize;
    float halfChunkSize;
    float chunkOvershoot;
    float halfChunkOvershoot;
    int columnCount;
    int rowCount;
    private static Array<Chunk> chunkRetriever = new Array<>(9);
    private static Polygon polygon = new Polygon();

    public Grid(int columnCount, int rowCount, float chunkSize)
    {
        this.columnCount = columnCount;
        this.rowCount = rowCount;
        this.chunkSize = chunkSize;
        init();
    }

    public Grid(int columnCount, int rowCount, float chunkSize, float chunkOvershoot)
    {
        this.columnCount = columnCount;
        this.rowCount = rowCount;
        this.chunkSize = chunkSize;
        this.chunkOvershoot = chunkOvershoot;
        init();
    }

    private void init()
    {
        this.halfChunkSize = chunkSize / 2f;
        this.halfChunkOvershoot = chunkOvershoot / 2f;

        for(int y = 0; y < rowCount; y ++)
        {
            for(int x = 0; x < columnCount; x ++)
            {
                Chunk chunk = new Chunk((x * chunkSize) + halfChunkSize, (y * chunkSize) + halfChunkSize, this);
                this.chunks.add(chunk);
            }
        }
    }

    /** Add an object to the grid. Will create chunks if none were found in the location. */
    public void add(float x, float y, Object object)
    {
        Array<Chunk> chunks = getChunks(x, y);
        if(chunks.size == 0)
            chunks = createChunks(x, y);
        add(chunks, object);
    }

    /** Add an object to the grid. Will create chunks if none were found in the location. */
    public void add(float[] vertices, Object object)
    {
        createChunks(vertices);
        Array<Chunk> chunks = getChunks(vertices);
        add(chunks, object);
    }

    private void add(Array<Chunk> chunks, Object object)
    {
        for(int i = 0; i < chunks.size; i ++)
        {
            Chunk chunk = chunks.get(i);
            chunk.add(object);
        }
    }

    private Array<Chunk> createChunks(float x, float y)
    {
        chunkRetriever.clear();
        float cellX = x / chunkSize;
        float cellY = y / chunkSize;
        if(cellX >= 0)
            cellX = MathUtils.ceil(x / chunkSize);
        else
            cellX = MathUtils.floor(x / chunkSize);
        if(cellY >= 0)
            cellY = MathUtils.ceil(y / chunkSize);
        else
            cellY = MathUtils.floor(y / chunkSize);
        float chunkX = (cellX * chunkSize);
        float chunkY = (cellY * chunkSize);

        if(chunks.size == 0)
        {
            Chunk chunk = new Chunk(chunkX, chunkY, this);
            chunks.add(chunk);
            chunkRetriever.add(chunk);
            this.columnCount = 1;
            this.rowCount = 1;
            return chunkRetriever;
        }

        Chunk firstChunk = chunks.get(0);
        Chunk lastChunk = chunks.get(chunks.size - 1);
        float leftX = firstChunk.x - halfChunkSize;
        float rightX = lastChunk.x + halfChunkSize;
        float bottomY = firstChunk.y - halfChunkSize;
        float topY;

        if(x < leftX) // resize leftward
        {
            int index = 0;
            int newColumns = (int) ((leftX - chunkX) / chunkSize);
            int newColumnCount = columnCount + newColumns;
            int yIncrement = 0;
            for(float newChunkY = bottomY + halfChunkSize; newChunkY < (rowCount * chunkSize) + bottomY; newChunkY += chunkSize)
            {
                for(float newChunkX = chunkX + halfChunkSize; newChunkX < leftX; newChunkX += chunkSize)
                {
                    Chunk chunk = new Chunk(newChunkX, newChunkY, this);
                    chunkRetriever.add(chunk);
                    chunks.insert(index, chunk);
                    index ++;
                }
                yIncrement ++;
                index = newColumnCount * yIncrement;
            }
            columnCount = newColumnCount;
        }
        else if(x > rightX) // resize rightward
        {
            int index = columnCount;
            int newColumns = (int) ((chunkX - rightX) / chunkSize);
            int newColumnCount = columnCount + newColumns;
            int yIncrement = 0;
            for(float newChunkY = bottomY + halfChunkSize; newChunkY < (rowCount * chunkSize) + bottomY; newChunkY += chunkSize)
            {
                for(float newChunkX = rightX + halfChunkSize; newChunkX < chunkX; newChunkX += chunkSize)
                {
                    Chunk chunk = new Chunk(newChunkX, newChunkY, this);
                    chunkRetriever.add(chunk);
                    chunks.insert(index, chunk);
                    index ++;
                }
                yIncrement ++;
                index = ((newColumnCount * yIncrement) + columnCount);
            }
            columnCount = newColumnCount;
        }

        firstChunk = chunks.get(0);
        lastChunk = chunks.get(chunks.size - 1);
        leftX = firstChunk.x - halfChunkSize;
        bottomY = firstChunk.y - halfChunkSize;
        topY = lastChunk.y + halfChunkSize;

        if(y < bottomY) // resize downward
        {
            int index = 0;
            int newRows = (int) ((bottomY - chunkY) / chunkSize);
            int newRowCount = rowCount + newRows;
            int yIncrement = 0;
            for(float newChunkY = chunkY + halfChunkSize; newChunkY < bottomY; newChunkY += chunkSize)
            {
                for(float newChunkX = leftX + halfChunkSize; newChunkX < (columnCount * chunkSize) + leftX; newChunkX += chunkSize)
                {
                    Chunk chunk = new Chunk(newChunkX, newChunkY, this);
                    chunkRetriever.add(chunk);
                    chunks.insert(index, chunk);
                    index ++;
                }
                yIncrement ++;
                index = columnCount * yIncrement;
            }
            rowCount = newRowCount;
        }
        else if(y > topY) // resize upward
        {
            int index = columnCount * rowCount;
            int newRows = (int) ((chunkY - topY) / chunkSize);
            int newRowCount = rowCount + newRows;
            int yIncrement = 0;
            for(float newChunkY = topY + halfChunkSize; newChunkY < chunkY; newChunkY += chunkSize)
            {
                for(float newChunkX = leftX + halfChunkSize; newChunkX < (columnCount * chunkSize) + leftX; newChunkX += chunkSize)
                {
                    Chunk chunk = new Chunk(newChunkX, newChunkY, this);
                    chunkRetriever.add(chunk);
                    chunks.insert(index, chunk);
                    index ++;
                }
                yIncrement ++;
                index = (columnCount * rowCount) + (columnCount * yIncrement);
            }
            rowCount = newRowCount;
        }
        return chunkRetriever;
    }

    private void createChunks(float[] vertices)
    {
        Chunk firstChunk = chunks.get(0);
        Chunk lastChunk = chunks.get(chunks.size - 1);
        float leftX = firstChunk.x - halfChunkSize;
        float rightX = lastChunk.x + halfChunkSize;
        float bottomY = firstChunk.y - halfChunkSize;
        float topY = lastChunk.y + halfChunkSize;

        polygon.setVertices(vertices);
        Rectangle rectangle = polygon.getBoundingRectangle();

        if(rectangle.x < leftX)
        {
            createChunks(rectangle.x, topY - halfChunkSize);
            rightX = chunks.get(columnCount - 1).x + halfChunkSize;
            bottomY = chunks.get(0).y - halfChunkSize;
            topY = chunks.get(chunks.size - 1).y + halfChunkSize;
        }

        if(rectangle.x + rectangle.width > rightX)
        {
            createChunks(rectangle.x + rectangle.width, topY - halfChunkSize);
            rightX = chunks.get(columnCount - 1).x + halfChunkSize;
            bottomY = chunks.get(0).y - halfChunkSize;
            topY = chunks.get(chunks.size - 1).y + halfChunkSize;
        }

        if(rectangle.y < bottomY)
        {
            createChunks(rightX - halfChunkSize, rectangle.y);
            rightX = chunks.get(columnCount - 1).x + halfChunkSize;
            topY = chunks.get(chunks.size - 1).y + halfChunkSize;
        }

        if(rectangle.y + rectangle.height > topY)
        {
            createChunks(rightX - halfChunkSize, rectangle.y + rectangle.height);
        }
    }

    private Chunk getChunk(float x, float y)
    {
        if(chunks.size == 0)
            return null;

        Chunk firstChunk = chunks.first();

        float leftX = firstChunk.x - halfChunkSize;
        float bottomY = firstChunk.y - halfChunkSize;

        if(x < firstChunk.x - halfChunkSize || y < firstChunk.y - halfChunkSize || x > (columnCount * chunkSize) + leftX || y > (rowCount * chunkSize) + bottomY)
            return null;

        x -= leftX;
        y -= bottomY;


        int index = (int) ((int)(y / chunkSize) * columnCount + (x / chunkSize));

        if(index < 0 || index >= chunks.size)
            return null;

        Chunk chunk = chunks.get(index);
        return chunk;
    }

    /** Query the grid for chunks overlapping the point. */
    public Array<Chunk> getChunks(float x, float y)
    {
        chunkRetriever.clear();

        Chunk chunk = getChunk(x, y);
        if(chunk != null)
        {
            chunk.touched = true;
            chunkRetriever.add(chunk);
        }

        float chunkX = (MathUtils.ceil(x / chunkSize) * chunkSize);
        float chunkY = (MathUtils.ceil(y / chunkSize) * chunkSize);

        retrieveNeighborChunks(chunkX, chunkY, x, y);

        for(int i = 0; i < chunks.size; i ++)
            chunks.get(i).touched = false;

        return chunkRetriever;
    }

    /** Query the grid for chunks overlapping the polygon. */
    public Array<Chunk> getChunks(float[] vertices)
    {
        chunkRetriever.clear();

        polygon.setVertices(vertices);
        Rectangle rectangle = polygon.getBoundingRectangle();
        for(float x = rectangle.x; x < rectangle.x + rectangle.width; x += chunkSize)
        {
            for(float y = rectangle.y; y < rectangle.y + rectangle.height; y += chunkSize)
            {
                Chunk chunk = getChunk(x, y);
                if(chunk != null && chunk.contains(vertices))
                {
                    chunk.touched = true;
                    chunkRetriever.add(chunk);
                }
            }
        }

        for(int i = 0; i < chunkRetriever.size; i ++)
        {
            Chunk chunk = chunkRetriever.get(i);
            retrieveNeighborChunks(chunk, vertices);
        }

        for(int i = 0; i < chunks.size; i ++)
            chunks.get(i).touched = false;

        return chunkRetriever;
    }

    /** Checks the bounds of all neighboring chunks and adds them to chunkRetriever if collision is made. */
    private void retrieveNeighborChunks(float chunkX, float chunkY, float x, float y)
    {
        retrieveNeighborChunk(chunkX - 1, chunkY, x, y);
        retrieveNeighborChunk(chunkX - 1, chunkY + 1, x, y);
        retrieveNeighborChunk(chunkX, chunkY + 1, x, y);
        retrieveNeighborChunk(chunkX + 1, chunkY + 1, x, y);
        retrieveNeighborChunk(chunkX + 1, chunkY, x, y);
        retrieveNeighborChunk(chunkX + 1, chunkY - 1, x, y);
        retrieveNeighborChunk(chunkX, chunkY - 1, x, y);
        retrieveNeighborChunk(chunkX - 1, chunkY - 1, x, y);
    }

    /** Does not use spatial hashing. Does collision check to account for overshoot. */
    private void retrieveNeighborChunk(float chunkX, float chunkY, float x, float y)
    {
        Chunk chunk = getChunk(chunkX, chunkY);
        if(chunk != null)
        {
            if(!chunk.touched && chunk.contains(x, y))
                chunkRetriever.add(chunk);
            chunk.touched = true;
        }
    }

    /** Checks the bounds of all neighboring chunks and adds them to chunkRetriever if collision is made. */
    private void retrieveNeighborChunks(Chunk chunk, float[] vertices)
    {
        retrieveNeighborChunk(chunk.x - 1, chunk.y, vertices);
        retrieveNeighborChunk(chunk.x - 1, chunk.y + 1, vertices);
        retrieveNeighborChunk(chunk.x, chunk.y + 1, vertices);
        retrieveNeighborChunk(chunk.x + 1, chunk.y + 1, vertices);
        retrieveNeighborChunk(chunk.x + 1, chunk.y, vertices);
        retrieveNeighborChunk(chunk.x + 1, chunk.y - 1, vertices);
        retrieveNeighborChunk(chunk.x, chunk.y - 1, vertices);
        retrieveNeighborChunk(chunk.x - 1, chunk.y - 1, vertices);
    }

    /** Does not use spatial hashing. Does collision check to account for overshoot. */
    private void retrieveNeighborChunk(float chunkX, float chunkY, float[] vertices)
    {
        Chunk chunk = getChunk(chunkX, chunkY);
        if(chunk != null)
        {
            if(!chunk.touched && chunk.contains(vertices))
                chunkRetriever.add(chunk);
            chunk.touched = true;
        }
    }
}
