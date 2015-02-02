package edu.wm.cs.cs301.amazebyjackiebethany.falstad;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.wm.cs.cs301.amazebyjackiebethany.falstad.Constants;
import edu.wm.cs.cs301.amazebyjackiebethany.falstad.Distance;
import edu.wm.cs.cs301.amazebyjackiebethany.falstad.Robot.Cardinal;
import edu.wm.cs.cs301.amazebyjackiebethany.falstad.Robot.Direction;
import edu.wm.cs.cs301.amazebyjackiebethany.falstad.Robot.Turn;

public class Wizard extends ManualDriver {

	private BasicRobot walle;

	public int cellCounter;
	
	protected SingleRandom random;
	
	public List<Direction> possibleMoves = new ArrayList<Direction>();
	
	public int[] currentPosition;
	
	int a, l, b, r;

	private long sleepInterval = 500;
	
	/** 
	 * Constructor 
	 */
	public Wizard() {
		
		walle = new BasicRobot(2500, true, true, true, false, true, false);
				
		random = SingleRandom.getRandom();
	}
	
	public Wizard(Maze mazeIn) {
		maze = mazeIn;
		walle = (BasicRobot) maze.r;
	}

	public boolean drive2Exit() {
		
		a = walle.distanceToObstacle(Direction.FORWARD);
		l = walle.distanceToObstacle(Direction.LEFT);
		b = walle.distanceToObstacle(Direction.BACKWARD);
		r = walle.distanceToObstacle(Direction.RIGHT);
		
		if (!walle.isAtGoal()){
			
			// Backtrack
			if (a == 0 
					&& l == 0
					&& r == 0
					&& b > 0){
				walle.rotate(Turn.AROUND);
				try {
					Thread.sleep(Globals.sleepInterval);
				} catch (InterruptedException e) {
				}
				walle.move(1);
			}
			
			if (walle.hasStopped()){
				return false;
			}
			
			// not backtracking
			else {
				a = walle.distanceToObstacle(Direction.FORWARD);
				l = walle.distanceToObstacle(Direction.LEFT);
				b = walle.distanceToObstacle(Direction.BACKWARD);
				r = walle.distanceToObstacle(Direction.RIGHT);
				
				int leftDist = leftDist();
				int aheadDist = aheadDist();
				int behindDist = behindDist();
				int rightDist = rightDist();
				
				if (leftDist <= rightDist && rightDist <= aheadDist) {
					
					if (l > 0) {
						
						walle.rotate(Turn.LEFT);
						try {
							Thread.sleep(Globals.sleepInterval);
						} catch (InterruptedException e) {
						}
						walle.move(1);
					}
					
					else if (r > 0) {
					
						walle.rotate(Turn.RIGHT);
						try {
							Thread.sleep(Globals.sleepInterval);
						} catch (InterruptedException e) {
						}
						walle.move(1);
					}
					
					else if (a > 0) {
						
						walle.move(1);
					}
				}
				
				else if (leftDist <= aheadDist && aheadDist <= rightDist){
					
					if (l > 0) {
						walle.rotate(Turn.LEFT);
						try {
							Thread.sleep(Globals.sleepInterval);
						} catch (InterruptedException e) {
						}
						walle.move(1);
					}
					else if (a > 0) {
						walle.move(1);
					}
					else if (r > 0) {
						walle.rotate(Turn.RIGHT);
						try {
							Thread.sleep(Globals.sleepInterval);
						} catch (InterruptedException e) {
						}
						walle.move(1);
					}
				}
				
				else if (rightDist <= leftDist && leftDist <= aheadDist){
					
					if (r > 0) {
						walle.rotate(Turn.RIGHT);
						try {
							Thread.sleep(Globals.sleepInterval);
						} catch (InterruptedException e) {
						}
						walle.move(1);
					}
					else if (l > 0) {
						walle.rotate(Turn.LEFT);
						try {
							Thread.sleep(Globals.sleepInterval);
						} catch (InterruptedException e) {
						}
						walle.move(1);
					}
					else if (a > 0) {
						walle.move(1);
					}
				}
				
				else if (rightDist <= aheadDist && aheadDist <= leftDist){
					
					if (r > 0) {
						walle.rotate(Turn.RIGHT);
						try {
							Thread.sleep(Globals.sleepInterval);
						} catch (InterruptedException e) {
						}
						walle.move(1);
					}
					else if (a > 0) {
						walle.move(1);
					}
					else if (l > 0) {
						walle.rotate(Turn.LEFT);
						try {
							Thread.sleep(Globals.sleepInterval);
						} catch (InterruptedException e) {
						}
						walle.move(1);
					}
				}
				
				else if (aheadDist <= leftDist && leftDist <= rightDist){
					
					if (a > 0) {
						walle.move(1);
					}
					else if (l > 0) {
						walle.rotate(Turn.LEFT);
						try {
							Thread.sleep(Globals.sleepInterval);
						} catch (InterruptedException e) {
						}
						walle.move(1);
					}
					else if (r > 0) {
						walle.rotate(Turn.RIGHT);
						try {
							Thread.sleep(Globals.sleepInterval);
						} catch (InterruptedException e) {
						}
						walle.move(1);
					}
				}
				
				else if (aheadDist <= rightDist && rightDist <= leftDist){
					
					if (a > 0) {
						walle.move(1);
					}
					else if (r > 0) {
						walle.rotate(Turn.RIGHT);
						try {
							Thread.sleep(Globals.sleepInterval);
						} catch (InterruptedException e) {
						}
						walle.move(1);
					}
					else if (l > 0) {
						walle.rotate(Turn.LEFT);
						try {
							Thread.sleep(Globals.sleepInterval);
						} catch (InterruptedException e) {
						}
						walle.move(1);
					}
				}
			}
		}
		
		if (walle.isAtGoal()) {
			return true;
		}
		
		else {
			return false;
		}
	}
	
	private int leftDist() {
		
		int dx = walle.getCurrentDirection()[0];
		int dy = walle.getCurrentDirection()[1];
		int px = walle.getCurrentPosition()[0];
		int py = walle.getCurrentPosition()[1];
		
		if (dx == 0 && dy == 1) {
			px = px - 1; 
			py = py;  
		}
		
		else if (dx == 0 && dy == -1) {
			px = px + 1;
			py = py;  
		}
		
		else if (dx == 1 && dy == 0) {
			px = px;
			py = py + 1;
		}
		
		else if (dx == -1 && dy == 0) {
			px = px;
			py = py - 1;
		}

		if (px >= 0 && px < maze.mazew && py >= 0 && py < maze.mazeh){
			if (maze.isEndPosition(px, py)) {
				return 0;
			}
			
			return maze.mazedists.getDistance(px,py);
		}
		
		else {
			return Integer.MAX_VALUE;
		}
	}
	
	private int rightDist() {
		
		int dx = walle.getCurrentDirection()[0];
		int dy = walle.getCurrentDirection()[1];
		int px = walle.getCurrentPosition()[0];
		int py = walle.getCurrentPosition()[1];
		
		if (dx == 0 && dy == 1) {
			px=px+1;
			py = py; 
		}
		
		else if (dx == 0 && dy == -1) {
			px = px - 1;
			py = py;
		}
		
		else if (dx == 1 && dy == 0) {
			px = px;
			py = py - 1;
		}
		
		else if (dx == -1 && dy == 0) {
			px = px;
			py = py + 1;
		}

		if (px >= 0 && px < maze.mazew && py >= 0 && py < maze.mazeh){
			if (maze.isEndPosition(px, py)) {
				return 0;
			}
			
			return maze.mazedists.getDistance(px,py);
		}
		
		else {
			return Integer.MAX_VALUE;
		}
	}
	
	private int aheadDist() {
		
		int dx = walle.getCurrentDirection()[0];
		int dy = walle.getCurrentDirection()[1];
		int px = walle.getCurrentPosition()[0];
		int py = walle.getCurrentPosition()[1];
		
		if (px + dx >= 0 && px + dx < maze.mazew && py + dy >= 0 && py + dy < maze.mazeh) {
			if (maze.isEndPosition(px+dx, py+dy)){
				return 0;
			}
			
			return maze.mazedists.getDistance(px+dx, py+dy);
		}
		
		else {
			return Integer.MAX_VALUE;
		}
	}
	
	private int behindDist() {
		
			int dx = walle.getCurrentDirection()[0];
			int dy = walle.getCurrentDirection()[1];
			int px = walle.getCurrentPosition()[0];				
			int py = walle.getCurrentPosition()[1];
			
			if (px - dx >= 0 && px - dx < maze.mazew && py - dy >= 0 && py - dy < maze.mazeh) {
				
				if (maze.isEndPosition(px-dx, py-dy)){
					return 0;
				}
				
				return maze.mazedists.getDistance(px-dx, py-dy);
			}
			
			else return Integer.MAX_VALUE;
			
	}

}
