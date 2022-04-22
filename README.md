# Intro #

I wrote this libGDX library because I needed to group a bunch of game objects together in chunks in a grid to cull them faster.
If for whatever reason a quad tree datastructure doesn't suit your needs, feel free to check this library out.


# Features #

* Create a Grid of Chunks that hold whatever Object you want.
* Grid automatically resizes by creating new chunks to encompass any new Object you add to the Grid that is positioned outside of the Grid.
* Query the grid with a point or polygon to receive chunks in that region.
* Option to add overshoots to the chunks if you want them to overlap over each other.


# Installation #

Add this in your root build.gradle at the end of repositories:

`	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}`
 
Then add the dependency

`dependencies {
	        implementation 'com.github.BambooBandit:ChunkJunk:2395d5cdf4'
}`

Now you should be good to go.


# Usage #

Make a Grid with initial column, row count (can both be 0 if you want to start with 0 chunks), chunk size, and optionally, overshoot (to allow chunks to overlap)

`Grid grid = new Grid(columns, rows, chunkSize, chunkOvershoot);`

Now you can add whatever objects you want to the grid by giving it either a point or a polygon, and the grid will add them to the appropriate chunks, or create new chunks if necessary.

`grid.add(x, y, object);`

`grid.add(new float[]{0, 0, 1, 1, 1, 0}, object);`

And query the grid for the chunks by giving it either a point or polygon. Keep in mind that grid.getChunks returns a reusable array to prevent garbage creation, so work with it as soon as you call it and don't hold on to it.

`Array<Chunk> chunks = grid.getChunks(x, y);`

`Array<Chunk> chunks = grid.getChunks(new float[]{0, 0, 1, 1, 1, 0});`

And that's pretty much it!


# Performance #

Since the chunks are indexed, you can quickly access a chunk with a point in O(1) time complexity. Querying with a polygon or with overshoots (overlapping chunks) aren't as fast because once necessary chunks are accessed, neighboring chunks have their bounds checked.

# Demo #

Included in this repo is a libGDX graphical demo of this library.
Controls:
* Left mouse click to add new point to grid
* Right mouse click to plot polygon points. Enter to finish polygon and add it to grid.
* Arrow keys to move camera.
* Comma and Period to zoom in and out.
* R to reset the grid.
