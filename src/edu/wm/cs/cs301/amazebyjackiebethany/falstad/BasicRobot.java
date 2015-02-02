package edu.wm.cs.cs301.amazebyjackiebethany.falstad;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.util.Log;
import android.widget.Toast;

import edu.wm.cs.cs301.amazebyjackiebethany.falstad.Cells;
import edu.wm.cs.cs301.amazebyjackiebethany.falstad.Maze;
import edu.wm.cs.cs301.amazebyjackiebethany.ui.PlayActivity;

public class BasicRobot implements Robot {
	
	private static final String TAG = "BasicRobot";

	public Maze maze;
	
	// Set up the instantiations
	private int batteryLevel;
	public Cells mazecells;
	public int posX;
	public int posY;
	boolean crash;
	

	public Cardinal currentCardinal;


	private boolean forwardDistanceSensor;
	private boolean backwardDistanceSensor;
	private boolean leftDistanceSensor;
	private boolean rightDistanceSensor;
	private boolean junctionSensor;
	private boolean roomSensor;
	
	public int pathLength = 0;

	public BasicRobot(Maze mazeIn){
		maze = mazeIn;
		this.batteryLevel = 2500;
		mazecells = maze.mazecells;
		posX = 0;
		posY = 0;
		crash = false;
		
		this.forwardDistanceSensor = true;
		this.backwardDistanceSensor = true;
		this.leftDistanceSensor = true;
		this.rightDistanceSensor = true;
		this.junctionSensor = true;
		this.roomSensor = true;
	}
	
	/**
	 * Constructor. Takes parameters to build robot to specifications.
	 * @param batteryLevel
	 * @param junctionSensor
	 * @param roomSensor
	 * @param forwardDistanceSensor
	 * @param backwardDistanceSensor
	 * @param leftDistanceSensor
	 * @param rightDistanceSensor
	 */
	public BasicRobot (int batteryLevel, boolean junctionSensor, boolean roomSensor, boolean forwardDistanceSensor, 
			boolean backwardDistanceSensor, boolean leftDistanceSensor, boolean rightDistanceSensor) {
		
		this.batteryLevel = batteryLevel;

		// Robot's sensors
		this.forwardDistanceSensor = forwardDistanceSensor;
		this.backwardDistanceSensor = backwardDistanceSensor;
		this.leftDistanceSensor = leftDistanceSensor;
		this.rightDistanceSensor = rightDistanceSensor;
		this.junctionSensor = junctionSensor;
		this.roomSensor = roomSensor;
	} // constructor()

	/**
	 * check if hasStopped() == True, which
     * determines if the robot has run out of battery
     * if so, throw Exception
     * otherwise rotate the robot by doing the following
     * if statements related to each type of turn
     * for left, call rotate(1) and decrease energy by 3
     * for right, call rotate (-1)and decrease energy by 3
     * for around, call rotate(1) twice and decrease energy by 6
  	 * @param direction to turn to relative to current forward direction 
	 * @throws Exception if the robot stops for lack of energy. 
	 */
	@Override
	public void rotate(Turn turn) {

		
		if (this.batteryLevel <= 0) {
			return;
		}
				
		
		switch(turn) {
		
			case RIGHT: 
				rotateRight();
				//Globals.obstacleSound.start();
				break;
		
			case LEFT: 
				rotateLeft();	
				//Globals.obstacleSound.start();
				break;
		
			case AROUND: 
				rotateAround();
				//Globals.obstacleSound.start();
				break;
		}
		
		setCardinalDirection();
		
		// Delay the handler and postInvalidate the view.
		maze.panel.draw.postInvalidate();		
	} // rotate()

	/**
	 * Moves robot forward a given number of steps. A step matches a single cell.
	 * Since a robot may only have a distance sensor in its front.
	 * If the robot runs out of energy somewhere on its way, it stops, 
	 * which can be checked by hasStopped() == true and by checking the battery level. 
	 * If the robot hits an obstacle like a wall, it remains at the position in front 
	 * of the obstacle but hasStopped() == false.
	 * @param distance is the number of cells to move in the robot's current forward direction
	 * @throws Exception if robot hits an obstacle like a wall or border, 
	 * which indicates that current position is not as expected. 
	 * Also thrown if robot runs out of energy. 
	 * @precondition distance >= 0
	 */
	@Override
	public void move(int distance) {
		
		int key;
		key = '8';
		setCardinalDirection();
		
		for (int i = 0; i < distance; i++) {
			
			if (this.getBatteryLevel() <= 0) {
				Log.v(TAG, "No more battery");
				return;
			}
			
			if (distanceToObstacle(Direction.FORWARD) == 0) {
				Log.v(TAG, "Hit an obstacle");
				return;
			}
			
			this.maze.keyDown(key);
			
			
			// If the robot leaves the maze
			if (this.isSteppingOut()) {
				this.maze.state = Constants.STATE_FINISH;
				this.maze.notifyViewerRedraw() ;
			}
			
			this.setBatteryLevel(this.getBatteryLevel() - 5);
			pathLength++;
			Log.v(TAG, "Moved forward.");
			
		} 
		
		maze.panel.draw.postInvalidate();
		
	} 
	
	/**
	 * Gives the path length
	 */
	@Override
	public int getPathLength() {
		return pathLength;
	}

	/**
	 * Provides the current position as (x,y) coordinates for the maze cell as an array of length 2 with [x,y].
	 * @postcondition 0 <= x < width, 0 <= y < height of the maze. 
	 * @return array of length 2, x = array[0], y=array[1]
	 * @throws Exception if position is outside of the maze
	 */
	@Override
	public int[] getCurrentPosition() {
		
		int[] currentPosition = new int[2];
		currentPosition[0] = this.maze.px;
		currentPosition[1] = this.maze.py;

		// throw an Exception if out of maze
		if (this.maze.mazew < currentPosition[0] || this.maze.mazeh < currentPosition[1]) {
			Log.v(TAG, "Not in bounds");
		}

		return currentPosition;
	} 

	/**
	 * Provides the robot with a reference to the maze it is currently in.
	 * The robot memorizes the maze such that this method is most likely called only once
	 * and for initialization purposes. The maze serves as the main source of information
	 * about the current location, the presence of walls, the reaching of an exit.
	 * @param maze is the current maze
	 * @precondition maze != null, maze refers to a fully operational, configured maze object
	 */
	@Override
	public void setMaze(Maze maze) {
		
		if (maze != null) {
			this.maze = maze;
		}
		
	}

	/**
	 * Tells if current position is at the goal (the exit). Used to recognize termination of a search.
	 * @return true if robot is at the goal, false otherwise
	 */
	@Override
	public boolean isAtGoal() throws ArrayIndexOutOfBoundsException {
		
		try {
			if (maze.isEndPosition(maze.px, maze.py)) {
				return true;
			} 
			else {
				return false;
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			return true;
		}
		
	} 

	/**
	 * Tells if a sensor can identify the goal in given direction relative to 
	 * the robot's current forward direction from the current position.
	 * @return true if the goal (here: exit of the maze) is visible in a straight line of sight
	 * @throws UnsupportedOperationException if robot has no sensor in this direction
	 */
	@Override
	public boolean canSeeGoal(Direction direction) {
		
		// Deal with robot sensor
		if (this.hasDistanceSensor(direction) == false) {
			return false;
		}
		
		// The goal is visible
		return this.distanceToObstacle(direction) > this.maze.mazeh;
	}

	/**
	 * Tells if current position is at a junction. 
	 * A junction is a position where there is no wall to the robot's right or left. 
	 * Note that this method is not helpful when the robot is inside a room. 
	 * For most positions inside a room, the robot has no walls to it's right or left
	 * such that the method returns true.
	 * @return true if robot is at a junction, false otherwise
	 * @throws UnsupportedOperationException if not supported by robot
	 */	
	@Override
    public boolean isAtJunction() {
            if (hasJunctionSensor()==false){
            	Log.v(TAG, "Cannot use this function");
            }
            
            if((distanceToObstacle(Robot.Direction.LEFT) > 0) || (distanceToObstacle(Robot.Direction.RIGHT) > 0))
            	return true;
            return false;
    }
	/**
	 * Says whether it has a junction sensor
	 */
	@Override
	public boolean hasJunctionSensor() {
		
		// Return true if the robot has a junction sensor.
		return this.junctionSensor;
	} // hasJunctionSensor()

	/**
	 * Says whether it is inside the room
	 * @return true if robot is inside a room, false otherwise
	 * @throws UnsupportedOperationException if not supported by robot
	 */
	@Override
	public boolean isInsideRoom() {
		
		if (this.hasRoomSensor() == false) {
			return false;
		}

		int[] currentPosition;

		currentPosition = getCurrentPosition();

		// x and y are current x and y coordinates
		int x = currentPosition[0];
		int y = currentPosition[1];

		return this.maze.mazecells.isInRoom(x, y);
	} 

	/**
	 * Says whether it has a room sensor.
	 */
	@Override
	public boolean hasRoomSensor() {
		return this.roomSensor;
	} 

	/**
	 * Provides the current direction as (dx,dy) values for the robot as an array of length 2 with [dx,dy].
	 * Note that dx,dy are elements of {-1,0,1} and as in bitmasks masks in Cells.java and dirsx,dirsy in MazeBuilder.java.
	 * @return array of length 2, dx = array[0], dy=array[1]
	 */	
	@Override
	public int[] getCurrentDirection() {
		
		int [] currentDirection = new int[2];
		currentDirection[0] = this.maze.dx;
		currentDirection[1] = this.maze.dy;
		
		return currentDirection;
	} 

	/**
	 * Returns the current battery level. 
	 * The particular energy consumption is device dependent such that a call for distance2Obstacle may use less energy than a move forward operation.
	 * If battery level <= 0 then robot stops to function and hasStopped() is true.
	 * @return current battery level, level is > 0 if operational. 
	 */
	@Override
	public int getBatteryLevel() {
		return this.batteryLevel;
	} 

	/**
	 * Sets the current battery level.
	 * The particular energy consumption is device dependent such that a call for distance2Obstacle may use less energy than a move forward operation.
	 * If battery level <= 0 then robot stops to function and hasStopped() is true.
	 * @param level is the current battery level
	 * @precondition level >= 0 
	 */
	@Override
	public void setBatteryLevel(int level) {
		
		this.batteryLevel = level;
	}

	/**
	 * Gives the energy consumption for a full 360 degree rotation.
	 * Scaling by other degrees approximates the corresponding consumption. 
	 * @return energy for a full rotation
	 */
	@Override
	public int getEnergyForFullRotation() {
		return 12;
	} 

	/**
	 * Gives the energy consumption for moving forward for a distance of 1 step.
	 * For simplicity, we assume that this equals the energy necessary 
	 * to move 1 step backwards and that scaling by a larger number of moves is 
	 * approximately the corresponding multiple.
	 * @return energy for a single step forward
	 */
	@Override
	public int getEnergyForStepForward() {
		return 5;
	} 

	/**
	 * Tells if the robot has stopped for reasons like lack of energy, hitting an obstacle, etc.
	 * @return true if the robot has stopped, false otherwise
	 */
	@Override
	public boolean hasStopped() {
		boolean retBool = false;
		
		if (batteryLevel <= 0) {
			retBool = true;
		}
		
		return retBool;
        
	} 

	/**
	 * Tells the distance to an obstacle (a wall or border) for a the robot's current forward direction.
	 * Distance is measured in the number of cells towards that obstacle, 
	 * e.g. 0 if current cell has a wall in this direction
	 * @return number of steps towards obstacle if obstacle is visible 
	 * in a straight line of sight, Integer.MAX_VALUE otherwise
	 * @throws UnsupportedOperationException if not supported by robot
	 */
	@Override
	public int distanceToObstacle(Direction direction) {
		
		if (!hasDistanceSensor(direction))
			Log.v(TAG, "Unsupported operation.");
		
		int dx = maze.dx;
	    int dy = maze.dy;
	    int px = maze.px;
	    int py = maze.py;
	    int distToOb = 0;     
	    
	    
	    if (direction == Direction.BACKWARD) {
	    	dx = -dx;
	    	dy = -dy;
	    }   
	    
	    else if(direction == Direction.RIGHT){
	    	// for each six of the cases
	    	if (dy == 0 && dx == 1){
	    		dx = 0;
	    		dy = -1;
	    	}
	    	else if (dy == 0 && dx == 0){
	    		dx = 0;
	    		dy = 1;
	    	}
	    	else if (dy == 0 && dx == -1){
	    		dx = 0;
	    		dy = 1;
	    	}
	    	else if (dy == 1 && dx == 0){
	    		dx = 1;
	    		dy = 0;
	    	}
	    	else if (dy == 1 && dx == 1){
	    		dx = 1;
	    		dy = 0;
	    	}
	    	else if (dy == 1 && dx == -1){
	    		dx = 1;
	    		dy = 0;
	    	}
	    	else{
	    		//If dy == -1
	    		dx = -1;
	    		dy = 0;
	    	}                      
	    }                     
	    
	    else if(direction == Direction.LEFT){
	    	// for all six cases
	    	if (dy == 0 && dx == 1){
	    		dx = 0;
	    		dy = 1;
	    	}
	    	else if (dy == 0 && dx == 0){
	    		dx = 0;
	    		dy = -1;
	    	}
	    	else if (dy == 0 && dx == -1){
	    		dx = 0;
	    		dy = -1;
	    	}
	    	else if (dy == 1 && dx == 0){
	    		dx = -1;
	    		dy = 0;
	    	}
	    	else if (dy == 1 && dx == 1){
	    		dx = -1;
	    		dy = 0;
	    	}
	    	else if (dy == 1 && dx == -1){
	    		dx = -1;
	    		dy = 0;
	    	}
	    	else{
	    		dx = 1;
	    		dy = 0;
	    	}
	    }
	           
	    while(moveValid(px, py, dx, dy)){
	    	//while the move is valid, run through and increment distance
	    	distToOb++;
	    	
	    	px += dx;
	    	py += dy;
	           
	    	if(px < 0 || py < 0|| px >= maze.mazew || py >= maze.mazeh){
	    		return Integer.MAX_VALUE;
	    	}
	    }
	   
	    return distToOb;
    }

	private boolean moveValid(int px, int py, int dx, int dy){
	//checks if a move is valid
		
		if(dx == 1){
		    return maze.mazecells.hasNoWallOnRight(px, py);
		}
		else if (dx ==-1) {
		    return maze.mazecells.hasNoWallOnLeft(px, py);
		}
		else if (dy == 1){
		    return maze.mazecells.hasNoWallOnBottom(px,  py);
		}
		return maze.mazecells.hasNoWallOnTop(px, py);
	}

	/**
	 * Tells if the robot has a distance sensor for the given direction.
	 */
	@Override
	public boolean hasDistanceSensor(Direction direction) {
		
		// Check if the robot has a sensor the direction.
		switch(direction){
		
			case RIGHT:
				return this.rightDistanceSensor;
			case LEFT:
				return this.leftDistanceSensor;
			case FORWARD:
				return this.forwardDistanceSensor;
			case BACKWARD:
				return this.backwardDistanceSensor;
		}
		
		return false;
	}
	
	/**
	 * Says whether the robot is stepping out of bounds of the maze.
	 */
	public boolean isSteppingOut() {
		return (this.isAtGoal() && this.canSeeGoal(Direction.FORWARD));
	} 

	/**
	 * Robot has Cardinal and relative directions. This deals with the cardinal
	 */
	private void setCardinalDirection(){
		
		if (Arrays.equals(Constants.NORTH, getCurrentDirection())) {
			this.currentCardinal = Cardinal.NORTH;
		}
		
		if (Arrays.equals(Constants.SOUTH, getCurrentDirection())) {
			this.currentCardinal = Cardinal.SOUTH;
		}
		
		if (Arrays.equals(Constants.WEST, getCurrentDirection())) {
			this.currentCardinal = Cardinal.WEST;
		}
		
		if (Arrays.equals(Constants.EAST, getCurrentDirection())) {
			this.currentCardinal = Cardinal.EAST;
		}
	} // setCardinalDirection()

	/**
	 * Rotates robot left
	 **/
	private void rotateLeft(){
		
		int key;
		key ='4';
		this.maze.keyDown(key);
		
		setCardinalDirection();
		
		this.setBatteryLevel(this.getBatteryLevel() - 3);
	} // rotateLeft()

	/**
	 * Rotates robot right
	 **/
	private void rotateRight(){
		
		int key;
		key = '6';
		this.maze.keyDown(key);
		
		setCardinalDirection();
		this.setBatteryLevel(this.getBatteryLevel() - 3);
		
	} 
	/**
	 *Rotates robot around
	 */
	private void rotateAround(){
		
		int key;
		key = '6';
		this.maze.keyDown(key);
		this.maze.keyDown(key);
		
		setCardinalDirection();
		this.setBatteryLevel(this.getBatteryLevel() - 6);
	}

	/**
	 * @returnCardinal direction if the robot looks left
	 */
	public Cardinal lookLeft(){
		
		setCardinalDirection();
		
		Cardinal leftCardinal;
		
		switch(this.currentCardinal){
			case NORTH: 
				leftCardinal = Cardinal.WEST;
				break;
				
			case EAST: 
				leftCardinal = Cardinal.NORTH;
				break;
			
			case SOUTH: 
				leftCardinal = Cardinal.EAST;
				break;
			
			case WEST: 
				leftCardinal = Cardinal.SOUTH;
				break;
			
			default: 
				leftCardinal = this.currentCardinal;
		}
		
		return leftCardinal;
	} 

	/**
	 * @return Cardinal direction if the robot looks right
	 */
	public Cardinal lookRight(){
		setCardinalDirection();

		Cardinal rightCardinal;
		
		switch(this.currentCardinal){
		
			case NORTH: 
				rightCardinal = Cardinal.EAST;
				break;
			
			case EAST: 
				rightCardinal= Cardinal.SOUTH;
				break;
			
			case SOUTH: 
				rightCardinal = Cardinal.WEST;	
				break;
			
			case WEST:
				rightCardinal = Cardinal.NORTH;
				break;
			
			default: rightCardinal =  this.currentCardinal;

		}
		return rightCardinal;
	} 

	/**
	 *@return Cardinal direction if the robot looks backwards
	 */
	public Cardinal lookBackward(){
		setCardinalDirection();
		
		Cardinal backCardinal;
		
		switch(this.currentCardinal){
			case NORTH:
				backCardinal = Cardinal.SOUTH;	
				break;
			
			case EAST:
				backCardinal = Cardinal.WEST;
				break;
			
			case SOUTH:
				backCardinal =  Cardinal.NORTH;
				break;
			
			case WEST:
				backCardinal = Cardinal.EAST;
				break;
			
			default:
				backCardinal = this.currentCardinal;
		}
		
		return backCardinal;
	} 

	/**
	 * @param direction to look that is relative to the robot's current direction  
	 * @return Cardinal direction of given relative direction 
	 */
	private Cardinal relativeToCardinal(Direction direction){
		setCardinalDirection();

		Cardinal relativeCardinal;
		switch(direction) {
		case FORWARD:
			relativeCardinal = currentCardinal;
			break;
		
		case BACKWARD:
			relativeCardinal =  lookBackward();
			break;
		
		case LEFT: 
			relativeCardinal = lookLeft();
			break;
		
		case RIGHT: 
			relativeCardinal = lookRight();
			break;
			
		default:
			relativeCardinal = currentCardinal;
		}
		
		return relativeCardinal;
	} // relativeToCardinal()
	
	/**
	 * @param x coordinate
	 * @param y coordinate
	 * @return boolean if it can go north
	 */
	private boolean CanGoNorth(int x , int y){
		return this.maze.mazecells.hasNoWallOnTop(x, y);
	}

	/**
	 * @param x coordinate
	 * @param y coordinate
	 * @return distance to an obstacle in the North direction 
	 */
	private int checkNorthObstacle(int x, int y) {

		int distance = 0;

		// Keep checking for obstacle until one is found
		while(CanGoNorth(x, y)) {
			distance = distance + 1;
			y = y - 1;

			// Checks whether robot has direct line of sight, return Interger.Max_Value
			if(y >= this.maze.mazeh || y <= 0){distance = Integer.MAX_VALUE; break;}

		}
		return distance;
	} 

	/**
	 * @param x coordinate of robot in the maze 
	 * @param y coordinate of robot in maze
	 * @return boolean if it can go south or Not
	 */
	private boolean CanGoSouth(int x , int y){
		return this.maze.mazecells.hasNoWallOnBottom(x, y);
	}
	
	/**
	 * @param x coordinate of robot in the maze 
	 * @param y coordinate of robot in maze
	 * @return distance to an obstacle in the south Direction 
	 */
	private int checkSouthObstacle(int x, int y) {
		int distance = 0;

		// Keep checking for obstacle until one is found
		while(CanGoSouth(x, y)){
			distance = distance + 1;
			y = y + 1;
			
			// If Robot has direct line of sight, return Interger.Max_Value
			if(y >= this.maze.mazeh || y <= 0){distance = Integer.MAX_VALUE; break;}
		}
		return distance;
	} 
	
	/**
	 * @param x position of robot in the maze 
	 * @param y position of robot in maze
	 *@return boolean if it can go east
	 */
	private boolean CanGoEast(int x , int y){
		return this.maze.mazecells.hasNoWallOnRight(x, y);
	} // CanGoEast()

	/**
	 * @param x position of robot in the maze 
	 * @param y position of robot in maze
	 * @return distance to an obstacle in the east direction 
	 */
	private int checkEastObstacle(int x, int y) {
		
		int distance = 0;

		// Keep checking for obstacle until one is found
		while(CanGoEast(x, y)){
			
			distance = distance + 1;
			x = x + 1;

			// line of sight, return Interger.Max_Value
			if(x >= this.maze.mazew || x <= 0) {
				distance = Integer.MAX_VALUE; break;
			}
		}
		
		return distance;
	} 
	
	/**
	 * @param x coordinate of robot in the maze 
	 * @param y coordinate of robot in maze
	 * @return boolean if it can go west or not
	 */
	private boolean CanGoWest(int x , int y){
		return this.maze.mazecells.hasNoWallOnLeft(x, y);
	} 

	/**
	 * @param x coordinate of robot in the maze 
	 * @param y coordinate of robot in maze
	 * @return distance to an obstacle in the west Direction 
	 */
	private int checkWestObstacle(int x, int y) {
		int distance = 0;

		// Keep checking for obstacle until one is found
		while( CanGoWest(x, y) ){
			
			distance = distance + 1;
			x = x - 1;

			// If Robot has direct line of sight, return Interger.Max_Value
			if(x >= this.maze.mazew || x <= 0){distance = Integer.MAX_VALUE; break;}
		}		
		
		return distance;
	}
	
	/**
	 * Gets maze 
	 */
	@Override
	public Maze getMaze() {
		
		return this.maze;
	} 

	/**
	 * Checks if the robot is at a dead-end - cannot move left, right, or forward.
	 * @return boolean if robot is at a dead-end.
	 */
	public boolean isAtDeadEnd() {
		
		if (this.distanceToObstacle(Direction.FORWARD) == 0 &&
			this.distanceToObstacle(Direction.LEFT) == 0 &&
			this.distanceToObstacle(Direction.RIGHT) == 0) {
			
			return true;
		}
		else {
			return false;
		}
	} 
	
	/**
	 * Obtains array list of the directions a robot can move in by using the distance sensors.
	 * Does not check a direction if it does not have the necessary distance sensor.
	 * @return array list of available directions
	 */
	public List<Direction> getAvailableDirections() {
		
		// Initialize array list of available directions
		List<Direction> availableDirections = new ArrayList<Direction>();
		
		// If the robot has a junction sensor, check for an immediate obstacle on the left and right
		if (this.junctionSensor) {
			
			if (this.isAtJunction()) {
				availableDirections.add(Direction.LEFT);
				availableDirections.add(Direction.RIGHT);	
			}
		}
			
		// If the robot has a left distance sensor, check for an immediate obstacle.
		if (this.leftDistanceSensor) {
				
			if (this.distanceToObstacle(Direction.LEFT) == 0) {
				
				// If the robot can go left and left is not already in the list of available directions, add it
				if (!availableDirections.contains(Direction.LEFT)) {
					availableDirections.add(Direction.LEFT);
				}
			}
		} 
			
		if (this.rightDistanceSensor) {
						
			if (this.distanceToObstacle(Direction.RIGHT) == 0) {
				
				// If the robot can go right and right is not already in the list of available directions, add it
				if (!availableDirections.contains(Direction.RIGHT)) {
					availableDirections.add(Direction.RIGHT);
				}
			}
		}
		// has a forward distance sensor, check for an immediate obstacle.
		if (this.forwardDistanceSensor) {
			
			if (this.distanceToObstacle(Direction.FORWARD) == 0) {
				availableDirections.add(Direction.FORWARD);			
			}
		} 
		// has a backward distance sensor, check for an immediate obstacle.
		if (this.backwardDistanceSensor) {
			
			if (this.distanceToObstacle(Direction.BACKWARD) == 0) {
				availableDirections.add(Direction.BACKWARD);			
			}
		} 
		return availableDirections;
	}
	
	/**
	 * Obtains array list of the directions a robot can move in by using the distance sensors.
	 * Assumes the robot has forward and left distance sensors and compensates for the other directions by turning around.
	 * @return array list of available directions
	 */
	public List<Direction> getAvailableDirectionsAround() {
	
		// Initialize array list of available directions
		List<Direction> availableDirections = new ArrayList<Direction>();
		
		// If there is no immediate obstacle to the left, add left to the array of available directions
		if (this.distanceToObstacle(Direction.LEFT) == 0) {
			availableDirections.add(Direction.LEFT);			
		}
		
		// If there is no immediate obstacle ahead, add forward to the array of available directions
		if (this.distanceToObstacle(Direction.FORWARD) == 0) {
			availableDirections.add(Direction.FORWARD);			
		}
		
		// Turn robot around 180 degrees.
		this.rotate(Turn.RIGHT);
		
		// If there is no immediate obstacle to the original right, add right to the array of available directions
		if (this.distanceToObstacle(Direction.FORWARD) == 0) {
			availableDirections.add(Direction.RIGHT);			
		}
						
		// Turn robot around 180 degrees, back to the original direction
		this.rotate(Turn.LEFT);
		
		return availableDirections;
	}
	
	public void setCells(Cells c){
		mazecells = c;
	}

	public boolean canSeeGoalAhead() {
			int[] temp = { maze.px, maze.py };
			int[] exit = maze.mazedists.getExitPosition(); 
			
			// Going North
			if (maze.dx == 0) {
				if (maze.dy == 1) {
					while (temp[0] > -1
							&& temp[1] > -1
							&& temp[0] < maze.mazew
							&& temp[1] < maze.mazeh
							&& maze.mazecells.hasWallOnBottom(temp[0], temp[1]) == false) {
						if (temp[0] == exit[0] && temp[1] == exit[1]) {
							batteryLevel--;
							return true;
						} else
							temp[1]++;
					}// while
				}
			}
			// Going South
			if (maze.dx == 0) {
				if (maze.dy == -1) {
					while (temp[0] > -1
							&& temp[1] > -1
							&& temp[0] < maze.mazew
							&& temp[1] < maze.mazeh
							&& maze.mazecells.hasWallOnTop(temp[0], temp[1]) == false) {
						if (temp[0] == exit[0] && temp[1] == exit[1]) {
							batteryLevel--;
							return true;
						} else
							temp[1]--;
					}
				}
			}
			// Going East
			if (maze.dx == 1) {
				if (maze.dy == 0) {
					while (temp[0] > -1
							&& temp[1] > -1
							&& temp[0] < maze.mazew
							&& temp[1] < maze.mazeh
							&& maze.mazecells.hasWallOnRight(temp[0], temp[1]) == false) {
						if (temp[0] == exit[0] && temp[1] == exit[1]) {
							batteryLevel--;
							return true;
						} else
							temp[0]++;
					}
				}
			}
			// Going West
			if (maze.dx == -1) {
				if (maze.dy == 0) {
					while (temp[0] > -1
							&& temp[1] > -1
							&& temp[0] < maze.mazew
							&& temp[1] < maze.mazeh
							&& maze.mazecells.hasWallOnLeft(temp[0], temp[1]) == false) {
						if (temp[0] == exit[0] && temp[1] == exit[1]) {
							batteryLevel--;
							return true;
						} else
							temp[0]--;
					}// while
				}
			}
			batteryLevel--;
			return false;
		}

	public boolean canSeeGoalOnLeft() {
		int[] temp = {maze.px, maze.py}; 	
		int[] exit = maze.mazedists.getExitPosition(); 
		
		//Facing North 
		if(maze.dx == 0){
			if(maze.dy == 1){
				//System.out.println("facing north within while(basicRobot Left)");TODO:
				while(temp[0] > -1 && temp[1] > -1 && temp[0] < maze.mazew && temp[1] < maze.mazeh && maze.mazecells.hasWallOnLeft(temp[0], temp[1]) == false ){
					if (temp[0] == exit[0] && temp[1] == exit[1]) {
						batteryLevel--;
						return true;
					} else
						temp[0]--;
				}
			}
		}
		//Facing South
		if(maze.dx == 0){
			if(maze.dy == -1){
				while(temp[0] > -1 && temp[1] > -1 && temp[0] < maze.mazew && temp[1] < maze.mazeh && maze.mazecells.hasWallOnRight(temp[0], temp[1]) == false ){
					if (temp[0] == exit[0] && temp[1] == exit[1]) {
						batteryLevel--;
						return true;
					} else					
						temp[0]++;
				}
			}
		}
		//Going East
		if(maze.dx == 1){
			if(maze.dy == 0){
				while(temp[0] > -1 && temp[1] > -1 && temp[0] < maze.mazew && temp[1] < maze.mazeh && maze.mazecells.hasWallOnBottom(temp[0], temp[1]) == false ){
					if (temp[0] == exit[0] && temp[1] == exit[1]) {
						batteryLevel--;
						return true;
					} else
						temp[1]++;
				}
			}
		}
		//Going West
		if(maze.dx == -1){
			if(maze.dy == 0){
				while(temp[0] > -1 && temp[1] > -1 && temp[0] < maze.mazew && temp[1] < maze.mazeh && maze.mazecells.hasWallOnTop(temp[0], temp[1]) == false ){
					if (temp[0] == exit[0] && temp[1] == exit[1]) {
						batteryLevel--;
						return true;
					} else
						temp[1]--;
				}
			}
		}
		batteryLevel--;
		return false;
	}
} 