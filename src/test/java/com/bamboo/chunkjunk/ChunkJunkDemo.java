package com.bamboo.chunkjunk;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.ScreenUtils;

public class ChunkJunkDemo extends ApplicationAdapter
{
	ShapeRenderer shapeRenderer;
	Grid grid;
	OrthographicCamera camera;
	Vector3 unprojector;
	FloatArray verts;
	Array<Object> junk;

	@Override
	public void create ()
	{
		shapeRenderer = new ShapeRenderer();
		shapeRenderer.setAutoShapeType(true);
		camera = new OrthographicCamera();
		camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		unprojector = new Vector3();
		verts = new FloatArray();
		junk = new Array<>();

		grid = new Grid(3, 3, 100, 25);

		camera.position.x -= (camera.viewportWidth / 2f) - (grid.columnCount * grid.chunkSize) / 2f;
		camera.position.y -= (camera.viewportHeight / 2f) - (grid.rowCount * grid.chunkSize) / 2f;
		camera.update();
	}

	@Override
	public void render ()
	{
		Gdx.gl.glClearColor(0, 0, 0, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		unprojector.set(Gdx.input.getX(), Gdx.input.getY(), 0);
		camera.unproject(unprojector);
		if(Gdx.input.isButtonJustPressed(Input.Buttons.LEFT))
		{
			JunkPoint junkPoint = new JunkPoint(unprojector.x, unprojector.y);
			junk.add(junkPoint);
			grid.add(unprojector.x, unprojector.y, junkPoint);
		}

		if(Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT))
		{
			verts.add(unprojector.x, unprojector.y);
		}

		if(Gdx.input.isKeyJustPressed(Input.Keys.ENTER))
		{
			if(verts.size >= 6)
			{
				float[] vertices = verts.toArray();
				JunkPolygon junkPolygon = new JunkPolygon(vertices);
				junk.add(junkPolygon);
				grid.add(vertices, junkPolygon);
			}
			verts.clear();
		}

		if(Gdx.input.isKeyPressed(Input.Keys.UP))
			camera.position.y -= Gdx.graphics.getDeltaTime() * 1000;
		if(Gdx.input.isKeyPressed(Input.Keys.DOWN))
			camera.position.y += Gdx.graphics.getDeltaTime() * 1000;
		if(Gdx.input.isKeyPressed(Input.Keys.LEFT))
			camera.position.x += Gdx.graphics.getDeltaTime() * 1000;
		if(Gdx.input.isKeyPressed(Input.Keys.RIGHT))
			camera.position.x -= Gdx.graphics.getDeltaTime() * 1000;
		if(Gdx.input.isKeyPressed(Input.Keys.COMMA))
			camera.zoom += Gdx.graphics.getDeltaTime() * 10;
		if(Gdx.input.isKeyPressed(Input.Keys.PERIOD))
		{
			camera.zoom -= Gdx.graphics.getDeltaTime() * 10;
			if(camera.zoom < .2f)
				camera.zoom = .2f;
		}
		camera.update();

		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		shapeRenderer.begin();
		shapeRenderer.setProjectionMatrix(camera.combined);
		shapeRenderer.setColor(1, 0, 0, .25f);
		shapeRenderer.set(ShapeRenderer.ShapeType.Filled);
		for(int i = 0; i < grid.chunks.size; i ++)
		{
			Chunk chunk = grid.chunks.get(i);
			shapeRenderer.rect(chunk.x - grid.halfChunkSize - grid.halfChunkOvershoot, chunk.y - grid.halfChunkSize - grid.halfChunkOvershoot, grid.chunkSize + grid.chunkOvershoot, grid.chunkSize + grid.chunkOvershoot);
		}

		shapeRenderer.setColor(0, 1, 0, 1f);
		shapeRenderer.set(ShapeRenderer.ShapeType.Filled);
		Array<Chunk> chunks = grid.getChunks(unprojector.x, unprojector.y);
		for(int i = 0; i < chunks.size; i ++)
		{
			Chunk chunk = chunks.get(i);
			shapeRenderer.rect(chunk.x - grid.halfChunkSize - grid.halfChunkOvershoot, chunk.y - grid.halfChunkSize - grid.halfChunkOvershoot, grid.chunkSize + grid.chunkOvershoot, grid.chunkSize + grid.chunkOvershoot);
		}

		shapeRenderer.setColor(Color.WHITE);
		shapeRenderer.set(ShapeRenderer.ShapeType.Line);
		for(int i = 0; i < grid.chunks.size; i ++)
		{
			Chunk chunk = grid.chunks.get(i);
			shapeRenderer.rect(chunk.x - grid.halfChunkSize, chunk.y - grid.halfChunkSize, grid.chunkSize, grid.chunkSize);
		}

		shapeRenderer.setColor(Color.GRAY);
		shapeRenderer.set(ShapeRenderer.ShapeType.Line);
		for(int i = 0; i < grid.chunks.size; i ++)
		{
			Chunk chunk = grid.chunks.get(i);
			shapeRenderer.rect(chunk.x - grid.halfChunkSize - grid.halfChunkOvershoot, chunk.y - grid.halfChunkSize - grid.halfChunkOvershoot, grid.chunkSize + grid.chunkOvershoot, grid.chunkSize + grid.chunkOvershoot);
		}

		shapeRenderer.setColor(Color.BLUE);
		if(verts.size > 0)
		{
			shapeRenderer.circle(verts.get(verts.size - 2), verts.get(verts.size - 1), 10);
			int oldIndex = 0;
			for (int i = 2; i < verts.size; i += 2)
			{
				shapeRenderer.line(verts.get(oldIndex), verts.get(oldIndex + 1), verts.get(i), verts.get(i + 1));
				oldIndex += 2;
			}
		}

		chunks = null;

		shapeRenderer.setColor(Color.YELLOW);
		for(int i = 0; i < junk.size; i ++)
		{
			Object object = junk.get(i);
			// Get chunks from point
			if(object instanceof JunkPoint)
			{
				JunkPoint junkPoint = (JunkPoint) object;
				chunks = grid.getChunks(junkPoint.x, junkPoint.y);
			}
			// Get chunks from polygon
			else if(object instanceof JunkPolygon)
			{
				JunkPolygon junkPolygon = (JunkPolygon) object;
				chunks = grid.getChunks(junkPolygon.vertices);
			}

			if(chunks != null)
			{
				// Get objects from chunks
				for(int k = 0; k < chunks.size; k ++)
				{
					Chunk chunk = chunks.get(k);
					ObjectSet objects = chunk.getObjects();
					ObjectSet.ObjectSetIterator iterator = objects.iterator();
					while(iterator.hasNext)
					{
						object = iterator.next();
						if(object instanceof JunkPoint)
						{
							JunkPoint junkPoint = (JunkPoint) object;
							shapeRenderer.circle(junkPoint.x, junkPoint.y, 5);
						}
						// Get chunks from polygon
						else if(object instanceof JunkPolygon)
						{
							JunkPolygon junkPolygon = (JunkPolygon) object;
							shapeRenderer.polygon(junkPolygon.vertices);
						}
					}
				}
			}
		}

		shapeRenderer.end();
		Gdx.gl.glDisable(GL20.GL_BLEND);
	}
	
	@Override
	public void dispose ()
	{
	}
}
