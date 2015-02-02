package edu.wm.cs.cs301.amazebyjackiebethany.falstad;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import edu.wm.cs.cs301.amazebyjackiebethany.falstad.Constants;
import edu.wm.cs.cs301.amazebyjackiebethany.falstad.Maze;
import edu.wm.cs.cs301.amazebyjackiebethany.falstad.Robot.Direction;
import edu.wm.cs.cs301.amazebyjackiebethany.falstad.Robot.Turn;

public class ManualDriver implements RobotDriver {
	
	private Robot robot;
	
	private int width;
	private int height;
	
	private float initialEnergy = 2500;
	
	private Distance distance;
	public int pathLength;
	
	protected SingleRandom random;
	
	public List<Direction> possibleMoves = new ArrayList<Direction>();
	
	Maze maze;
	
	CountDownLatch latch = new CountDownLatch(1);
	
	/** 
	 * Constructor .
	 */
	public ManualDriver() {
		random = SingleRandom.getRandom();
	}

	public ManualDriver(Maze mazeAttached){
		maze = mazeAttached;
	}

	/**
	 * Assigns a robot platform to the driver. Not all robot configurations may be suitable such that the method 
	 * will throw an exception if the robot does not match minimal configuration requirements, e.g. providing a sensor
	 * to measure the distance to an object in a particular direction. 
	 * @param r robot to operate
	 */
	@Override
	public void setRobot(Robot r) {
		this.robot = r;
		this.initialEnergy = this.robot.getBatteryLevel();
	}
	
	public int getPathLength() {
		return maze.r.getPathLength();
	}

	/**
	 * Provides the robot driver with information on the dimensions of the 2D maze
	 * measured in the number of cells in each direction.
	 * Only some drivers such as Tremaux's algorithm need this information.
	 * @param width of the maze
	 * @param height of the maze
	 * @precondition 0 <= width, 0 <= height of the maze.
	 */
	@Override
	public void setDimensions(int width, int height) {
		this.width = width;
		this.height = height;		
	}

	/**
	 * Provides the robot driver with information on the distance to the exit.
	 * Only some drivers such as the wizard rely on this information to find the exit.
	 * @param distance gives the length of path from current position to the exit.
	 * @precondition null != distance, a full functional distance object for the current maze.
	 */
	@Override
	public void setDistance(Distance distance) {
		this.distance = distance;
	}

	/**
	 * Drives the robot towards the exit given it exists and given the robot's energy supply lasts long enough. 
	 * @return true if driver successfully reaches the exit, false otherwise
	 * @throws exception if robot stopped due to some problem, e.g. lack of energy
	 */
	@Override
	public boolean drive2Exit() {
		
		try {
			latch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		if (maze.state == Constants.STATE_FINISH) {
			return true;
		}
		
		else if (robot.hasStopped()) {
			return false;
		}
		
		return false;
	}

	/**
	 * Returns the total energy consumption of the journey, i.e.,
	 * the difference between the robot's initial energy level at
	 * the starting position and its energy level at the exit position. 
	 * This is used as a measure of efficiency for a robot driver.
	 */
	@Override
	public float getEnergyConsumption() {
		return this.initialEnergy - maze.r.getBatteryLevel();
	}
	
	/**
	 * Returns robot
	 */
	public Robot getRobot() {
		return this.robot;
	}
	
	/**
	 * Find directions robot can move
	 */
	public void gatherAvailableMoves() {
		
		if (this.getRobot().distanceToObstacle(Direction.FORWARD) > 0) {
			this.possibleMoves.add(Direction.FORWARD);
		}
		
		if (this.getRobot().distanceToObstacle(Direction.LEFT) > 0) {
			this.possibleMoves.add(Direction.LEFT);
		}
		
		if (this.getRobot().distanceToObstacle(Direction.BACKWARD) > 0) {
			this.possibleMoves.add(Direction.BACKWARD);
		}
		
		if (this.getRobot().distanceToObstacle(Direction.RIGHT) > 0) {
			this.possibleMoves.add(Direction.RIGHT);
		}
	}
	
	/** 
	 * Rotates and moves robot
	 * @param chosenDirection
	 */
	public void rotateAndMove(Direction chosenDirection) throws Exception {
		if (chosenDirection == Direction.FORWARD){
			
		} else if (chosenDirection == Direction.LEFT) {
			this.getRobot().rotate(Turn.LEFT);
		} else if (chosenDirection == Direction.RIGHT) {
			this.getRobot().rotate(Turn.RIGHT);
		} else {
			this.getRobot().rotate(Turn.AROUND);
		}
		
		this.getRobot().move(1);
	}
	
	/**
	 * Picks random direction
	 * @param length
	 * @return
	 */
	public int pickRandomDirection(int length) {
		int rand;
		return rand = random.nextIntWithinInterval(0, length - 1);
	}
	
	@Override
	public void latchFix() {
		latch.countDown();
	}

	@Override
	public void cellCounter(int count) {
		
	}

	@Override
	public void resetCellCounter() {
		
	}
}