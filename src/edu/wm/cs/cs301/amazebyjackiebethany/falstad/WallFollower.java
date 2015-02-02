package edu.wm.cs.cs301.amazebyjackiebethany.falstad;

import java.util.ArrayList;
import java.util.List;

import edu.wm.cs.cs301.amazebyjackiebethany.falstad.Robot.Direction;
import edu.wm.cs.cs301.amazebyjackiebethany.falstad.Robot.Turn;

public class WallFollower extends ManualDriver {

    private BasicRobot robot; 
    private int[] wallBotDim;
    private int wallBotDist;
    private int wallBotPathLength;
	
	
	
	public WallFollower(Maze mazeIn){
		
		maze = mazeIn;
	}

	/**
	 * This makes a robot.
	 * @param r is the robot.
	 */
	@Override
	public void setRobot(Robot r) {
		robot = (BasicRobot) r;
		if (r == null){
			return;
		}

	}

	@Override
	public boolean drive2Exit() {
		
		if (!robot.isAtGoal()) {

				if (robot.canSeeGoal(Direction.FORWARD)) {
					try {
						robot.move(1);
					} catch (Exception e) {
						e.printStackTrace();
					}
					robot.pathLength++;
				}
				
				else if (robot.canSeeGoal(Direction.LEFT)) {
					try {
						robot.rotate(Turn.LEFT);
					} catch (Exception e) {
						e.printStackTrace();
					}
					try {
						Thread.sleep(Globals.sleepInterval);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					try {
						robot.move(1);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					robot.pathLength++;
				}
				
				else if (robot.distanceToObstacle(Direction.LEFT) != 0) {
					try {
						robot.rotate(Turn.LEFT);
					} catch (Exception e) {
						e.printStackTrace();
					}
					try {
						Thread.sleep(Globals.sleepInterval);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					try {
						robot.move(1);
					} catch (Exception e) {
						e.printStackTrace();
					}
					robot.pathLength++;
				}
				
				else if (robot.distanceToObstacle(Direction.FORWARD) != 0) {
					try {
						robot.move(1);
					} catch (Exception e) {
						e.printStackTrace();
					}
					robot.pathLength++;
				}
				
				else {
					try {
						robot.rotate(Turn.AROUND);
					} catch (Exception e) {
						e.printStackTrace();
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
}
