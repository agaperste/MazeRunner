package edu.wm.cs.cs301.amazebyjackiebethany.falstad;

import java.util.Random;

import android.util.Log;
import edu.wm.cs.cs301.amazebyjackiebethany.falstad.Robot.Direction;
import edu.wm.cs.cs301.amazebyjackiebethany.falstad.Robot.Turn;

public class Tremaux implements RobotDriver {
	
	public boolean solving;
	private BasicRobot robot; 
	private int[] gamDim;
	
	public Maze maze;
	
	/**	 
	 * @param mazeIn
	 */

	public Tremaux(Maze mazeIn) {
		maze = mazeIn;
		robot = (BasicRobot) maze.r;
	}

	@Override
	public void setRobot(Robot r) {

    	robot = (BasicRobot) r;
		
	}

	/**
	 * Tremaux drives to exit
	 */
	@Override
	public boolean drive2Exit() {
		
		Random random = new Random();
		int r;
		int[] pos = new int[2];
		int[] dir = new int[2];
		
		int[][] visited = new int[maze.mazew][maze.mazeh];
		
		if (!robot.isAtGoal()) {
			
			pos = robot.getCurrentPosition();
			dir = robot.getCurrentDirection();
			r = random.nextInt(2);
			
			if(robot.getBatteryLevel() <= 0) {
				return false;
			}
			
			if (robot.distanceToObstacle(Direction.FORWARD) == 0 
					&& robot.distanceToObstacle(Direction.LEFT) == 0) {
				robot.rotate(Turn.AROUND);
			}
			
			// facing north or south
			else if (dir[0] == 0) {
				
				// facing north
				if (dir[1] == 1) {
					
					// move forward
					if (pos[0] - 1 > -1 
							&& pos[1] + 1 > -1 
							&& pos[0] - 1 < maze.mazew - 1 
							&& pos[1] + 1 < maze.mazeh - 1 
							&& visited[pos[0]][pos[1] + 1] > visited[pos[0] - 1][pos[1]]) { 
						robot.move(1);
						robot.pathLength++;
					}
					
					// move left
					else if(pos[0] - 1 > -1 
							&& pos[1] + 1 > -1 
							&& pos[0] - 1 < maze.mazew - 1 
							&& pos[1] + 1 < maze.mazeh - 1 
							&& visited[pos[0]][pos[1] + 1] < visited[pos[0] - 1][pos[1]]) {
						robot.rotate(Turn.LEFT);
						try {
							Thread.sleep(Globals.sleepInterval);
						} catch (InterruptedException e) {
						}
						robot.move(1);
						robot.pathLength++;
					}
					
					else {
						randomMove(r);
					}						
				}
				
				// facing south
				if(dir[1] == -1) {
					
					// move forward
					if(pos[0]+1 > -1 
							&& pos[1] - 1 > -1 
							&& pos[0] + 1 < maze.mazew - 1 
							&& pos[1] - 1 < maze.mazeh - 1 
							&& visited[pos[0]][pos[1] - 1] > visited[pos[0] + 1][pos[1]]) { 
						robot.move(1);
						robot.pathLength++;
					}
						
					// move left
					else if(pos[0] + 1 > -1 
							&& pos[1] - 1 > -1 
							&& pos[0] + 1 < maze.mazew - 1 
							&& pos[1] - 1 < maze.mazeh - 1 
							&& visited[pos[0]][pos[1] - 1] < visited[pos[0] + 1][pos[1]]){ //when left
						robot.rotate(Turn.LEFT);
						try {
							Thread.sleep(Globals.sleepInterval);
						} catch (InterruptedException e) {
						}						robot.move(1);
						robot.pathLength++;
					}
						
					else {
						randomMove(r);
					}						
				}				
			}
			
			// facing east or west
			else {
				
				// facing east
				if(dir[0] == 1) {
					
					// move forward
					if(pos[0]+1 > -1 
							&& pos[1] + 1 > -1 
							&& pos[0] + 1 < maze.mazew - 1 
							&& pos[1] + 1 < maze.mazeh - 1 
							&& visited[pos[0] + 1][pos[1]] > visited[pos[0]][pos[1] + 1]){ //when ahead has more
						robot.move(1);
						robot.pathLength++;
					}
						
					// move left
					else if(pos[0] + 1 > -1 
							&& pos[1] > -1 
							&& pos[0] + 1 < maze.mazew - 1 
							&& pos[1] + 1 < maze.mazeh - 1 
							&& visited[pos[0] + 1][pos[1]] < visited[pos[0]][pos[1] + 1]){ //when left
						robot.rotate(Turn.LEFT);
						try {
							Thread.sleep(Globals.sleepInterval);
						} catch (InterruptedException e) {
						}						robot.move(1);
						robot.pathLength++;
					}
						
					else {
						randomMove(r);
					}	
				}
				
				// facing west
				if (dir[0] == -1) {
					
					// move forward
					if (pos[0]-1 > -1 
							&& pos[1] - 1 > -1 
							&& pos[0] - 1 < maze.mazew - 1
							&& pos[1] - 1 < maze.mazeh - 1 
							&& visited[pos[0] - 1][pos[1]] > visited[pos[0]][pos[1] - 1]) { //when ahead has more
						robot.move(1);
						robot.pathLength++;
					}
						
					// move left
					else if (pos[0] - 1 > -1 
							&& pos[1] - 1 > -1 
							&& pos[0] - 1 < maze.mazew - 1 
							&& pos[1] - 1 < maze.mazeh - 1 
							&& visited[pos[0] - 1][pos[1]] < visited[pos[0]][pos[1] - 1]){ //when left
						robot.rotate(Turn.LEFT);
						try {
							Thread.sleep(Globals.sleepInterval);
						} catch (InterruptedException e) {
						}						robot.move(1);
						robot.pathLength++;
					}
						
					else {
						randomMove(r);
					}	
				}
			}
		}
		
		if (robot.isAtGoal()) {
			return true;
		}
		
		else {
			return false;
		}
	}
	
	/**
	 * Decides random direction.
	 * @param r
	 * @throws Exception 
	 * @throws HitObstacleException 
	 * @throws UnsupportedMethodException 
	 * @throws UnsupportedArgumentException 
	 */
	private void randomMove(int r) {

		// Move forward.
		if (r == 0) {
			if (robot.distanceToObstacle(Direction.FORWARD) != 0) {
				robot.move(1);
				robot.pathLength++;
			}
		}
		// Move left.
		if (r == 1) {
			if (robot.distanceToObstacle(Direction.LEFT) != 0) {
				robot.rotate(Turn.LEFT);
				try {
					Thread.sleep(Globals.sleepInterval);
				} catch (InterruptedException e) {
				}				robot.move(1);
				robot.pathLength++;
			}
		}
	}

	@Override
	public float getEnergyConsumption() {
		return 2500 - maze.r.getBatteryLevel();
	}

	@Override
	public int getPathLength() {
        return robot.pathLength;
	}


	@Override
	public Robot getRobot() {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public void cellCounter(int count) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void resetCellCounter() {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void latchFix() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setDimensions(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setDistance(Distance distance) {
		// TODO Auto-generated method stub
		
	}

}
