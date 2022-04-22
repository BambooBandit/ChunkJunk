# Intro #

I wrote this libGDX library because I needed to group a bunch of game objects together in chunks in a grid to cull them faster.
If for whatever reason a quad tree datastructure doesn't suit your needs, feel free to check this library out.


# Features #

* Create a Grid of Chunks that hold whatever Object you want.
* Grid automatically resizes by creating new chunks to encompass any new Object you add to the Grid that is positioned outside of the Grid.
* Query the grid with a point or polygon to recieve chunks in that region.
* Option to add overshoots to the chunks if you want them to overlap over each other.


# Usage #

Todo


# Performance #

Since the chunks are indexed, you can quickly access a chunk with a point in O(1) time complexity. Querying with a polygon or with overshoots (overlapping chunks) aren't as fast because once necessary chunks are accessed, neighboring chunks have their bounds checked.
