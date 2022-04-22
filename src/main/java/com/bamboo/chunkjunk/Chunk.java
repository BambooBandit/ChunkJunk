package com.bamboo.chunkjunk;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.utils.ObjectSet;

public class Chunk
{
    float x, y; // middle of chunk
    boolean touched; // ignore as a neighbor if true
    private ObjectSet objects;
    private Grid grid;
    private float[] vertices;

    public Chunk(float x, float y, Grid grid)
    {
        objects = new ObjectSet();
        this.grid = grid;
        this.x = x;
        this.y = y;
        float halfSize = grid.halfChunkSize + grid.halfChunkOvershoot;
        this.vertices = new float[]{x - halfSize, y - halfSize, x - halfSize, y + halfSize, x + halfSize, y + halfSize, x + halfSize, y - halfSize};
    }

    public ObjectSet getObjects()
    {
        return objects;
    }

    public void add(Object object)
    {
        objects.add(object);
    }

    public boolean contains(float x, float y)
    {
        float halfSize = grid.halfChunkSize + grid.halfChunkOvershoot;
        return x >= this.x - halfSize && x <= this.x + halfSize && y >= this.y - halfSize && y <= this.y + halfSize;
    }

    public boolean contains(float[] vertices)
    {
        return Intersector.overlapConvexPolygons(this.vertices, vertices, null);
    }
}
