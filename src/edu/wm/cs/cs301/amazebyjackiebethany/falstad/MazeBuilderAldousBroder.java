package edu.wm.cs.cs301.amazebyjackiebethany.falstad;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Random;



/**
 * This class has the responsibility to create a maze of given dimensions (width, height) together with a solution based on a distance matrix.
 * The Maze class depends on it. The MazeBuilder performs its calculations within its own separate thread such that communication between 
 * Maze and MazeBuilder operates as follows. Maze calls the build() method and provides width and height. Maze has a call back method newMaze that
 * this class calls to communicate a new maze and a BSP root node and a solution.
 * 
 * The maze is built with a randomized version of Prim's algorithm. 
 * This means a spanning tree is expanded into a set of cells by removing walls from the maze.
 * 
 * This code is refactored code from Maze.java by Paul Falstad, www.falstad.com, Copyright (C) 1998, all rights reserved
 * Paul Falstad granted permission to modify and use code for teaching purposes.
 * Refactored by Peter Kemper   
 * @author Jones.Andrew
 */

public class MazeBuilderAldousBroder extends MazeBuilder implements Runnable {
	
	
	public MazeBuilderAldousBroder() {
		super();
		System.out.println("MazeBuilderAldousBroder uses Aldous-Broder's algorithm to generate maze.");
	}
	public MazeBuilderAldousBroder(boolean det) {
		super(det);
		System.out.println("MazeBuilderAldousBroder uses Aldous-Broder's algorithm to generate deterministic maze.");
	}

	/**
	 * This method generates pathways into the maze by using Aldous-Broder's algorithm to generate a spanning tree for an undirected graph.
	 * The cells are the nodes of the graph and the spanning tree. An edge represents that one can move from one cell to an adjacent cell.
	 * So an edge implies that its nodes are adjacent cells in the maze and that there is no wall separating these cells in the maze. 
	 */
	 @Override
     protected void generatePathways(){
            
             // set a random x and y variable in a maze with all cell walls up
             int x = random.nextIntWithinInterval(0, width - 1);
             int y = random.nextIntWithinInterval(0, height - 1);
             Random randomNum = new Random();
             // define a variable holding int of all cells not visited aka remaining
             int remaining = width * height - 1;
             cells.setCellAsVisited(x,y);

             // while remaining is greater than zero
             while (remaining > 0){
                     // randomly move to cell adjacent to current cell
                     int dx = 0, dy = 0;
                     int aRand = randomNum.nextInt(4);

                     // check which direction it is
                     if (y+1 < height && aRand == 0){
                             dx = 0;
                             dy = 1;
                     }
                     if (x-1 > -1 && aRand == 1){
                             dx = -1;
                             dy = 0;
                     }
                     if (x+1 < width && aRand == 2){
                             dx = 1;
                             dy = 0;
                     }
                     if (y-1 > -1 && aRand == 3){
                             dx = 0;
                             dy = -1;
                     }
                    
                     // check to see if the cell has been visited
                     if (cells.hasMaskedBitsTrue(x + dx, y + dy, Constants.CW_VISITED)){
                     		// if it was then delete the wall and set it as visited
                             cells.deleteWall(x, y, dx, dy);
                             cells.setCellAsVisited(x + dx,y + dy);
                             remaining -= 1;
                     }
                     x += dx;
                     y += dy;
             }

     }
}
