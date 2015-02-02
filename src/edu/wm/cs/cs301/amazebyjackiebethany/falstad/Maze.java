package edu.wm.cs.cs301.amazebyjackiebethany.falstad;

//import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

import edu.wm.cs.cs301.amazebyjackiebethany.falstad.BasicRobot;
import edu.wm.cs.cs301.amazebyjackiebethany.falstad.Constants;
import edu.wm.cs.cs301.amazebyjackiebethany.falstad.FirstPersonDrawer;
import edu.wm.cs.cs301.amazebyjackiebethany.falstad.Gambler;
import edu.wm.cs.cs301.amazebyjackiebethany.falstad.ManualDriver;
import edu.wm.cs.cs301.amazebyjackiebethany.falstad.MazePanel;
import edu.wm.cs.cs301.amazebyjackiebethany.falstad.Robot;
import edu.wm.cs.cs301.amazebyjackiebethany.falstad.RobotDriver;
import edu.wm.cs.cs301.amazebyjackiebethany.falstad.Viewer;
import edu.wm.cs.cs301.amazebyjackiebethany.falstad.WallFollower;
import edu.wm.cs.cs301.amazebyjackiebethany.falstad.Wizard;
import edu.wm.cs.cs301.amazebyjackiebethany.falstad.Robot.Turn;
import edu.wm.cs.cs301.amazebyjackiebethany.ui.GeneratingActivity;
import edu.wm.cs.cs301.amazebyjackiebethany.ui.PlayActivity;

import android.content.Context;
import android.os.Message;
import android.util.Log;
import android.widget.ProgressBar;

/**
 * Class handles the user interaction for the maze. 
 * It implements a state-dependent behavior that controls the display and reacts to key board input from a user. 
 * After refactoring the original code from an applet into a panel, it is wrapped by a MazeApplication to be a java application 
 * and a MazeApp to be an applet for a web browser. At this point user keyboard input is first dealt with a key listener
 * and then handed over to a Maze object by way of the keyDown method.
 *
 * This code is refactored code from Maze.java by Paul Falstad, www.falstad.com, Copyright (C) 1998, all rights reserved
 * Paul Falstad granted permission to modify and use code for teaching purposes.
 * Refactored by Peter Kemper
 */
// MEMO: original code: public class Maze extends Applet {
//public class Maze extends Panel {
public class Maze implements Serializable {
	
	private static final String TAG = "Maze";
	
	static int width = 768;
	static int height = 1184;

	// Model View Controller pattern, the model needs to know the viewers
	// however, all viewers share the same graphics to draw on, such that the share graphics
	// are administered by the Maze object
	final private ArrayList<Viewer> views = new ArrayList<Viewer>() ; 
	public MazePanel panel ; // graphics to draw on, shared by all views
		
	protected int state;			// keeps track of the current GUI state, one of STATE_TITLE,...,STATE_FINISH, mainly used in redraw()
	// possible values are defined in Constants
	// user can navigate 
	// title -> generating -(escape) -> title
	// title -> generation -> play -(escape)-> title
	// title -> generation -> play -> finish -> title
	// STATE_PLAY is the main state where the user can navigate through the maze in a first person view

	public int percentdone = 0; // describes progress during generation phase
	protected boolean showMaze;		 	// toggle switch to show overall maze on screen
	protected boolean showSolution;		// toggle switch to show solution in overall maze on screen
	protected boolean solving;			// toggle switch 
	protected boolean mapMode; // true: display map of maze, false: do not display map of maze
	// map_mode is toggled by user keyboard input, causes a call to draw_map during play mode

	//static final int viewz = 50;    
	int viewx, viewy, angle;
	int dx, dy;  // current direction
	int px, py ; // current position on maze grid (x,y)
	int walkStep;
	int viewdx, viewdy; // current view direction


	// debug stuff
	boolean deepdebug = false;
	boolean allVisible = false;
	boolean newGame = false;

	// properties of the current maze
	int mazew; // width of maze
	int mazeh; // height of maze
	protected Cells mazecells ; // maze as a matrix of cells which keep track of the location of walls
	Distance mazedists ; // a matrix with distance values for each cell towards the exit
	Cells seencells ; // a matrix with cells to memorize which cells are visible from the current point of view
	// the FirstPersonDrawer obtains this information and the MapDrawer uses it for highlighting currently visible walls on the map
	BSPNode rootnode ; // a binary tree type search data structure to quickly locate a subset of segments
	// a segment is a continuous sequence of walls in vertical or horizontal direction
	// a subset of segments need to be quickly identified for drawing
	// the BSP tree partitions the set of all segments and provides a binary search tree for the partitions
	

	// Mazebuilder is used to calculate a new maze together with a solution
	// The maze is computed in a separate thread. It is started in the local Build method.
	// The calculation communicates back by calling the local newMaze() method.
	public MazeBuilder mazebuilder;

	
	// fixing a value matching the escape key
	final int ESCAPE = 27;
	final int LEFT = 37;
	final int UP = 38;
	final int RIGHT = 39;
	final int DOWN = 40;

	// generation method used to compute a maze
	int method = 0 ; // 0 : default method, Falstad's original code
	// method == 1: Prim's algorithm
	//method == 2:Aldous Broder's Algortithm

	int zscale = Constants.VIEW_HEIGHT/2;
	
	public PlayActivity play;
	public GeneratingActivity gen;
	ProgressBar progressbar;

	private RangeSet rset;
	public MazeView MazeView;
	Context context;
	
	private String driveType = "Manual"; 
	private int skill;
	
	public Robot r;
	public static RobotDriver driver;
	
	/**
	 * Constructor
	 */
	public Maze(Context context) {
		super() ;
		panel = new MazePanel(context) ;
		this.context = context;
		progressbar = new ProgressBar(context);
	}
	
	/**
	 * Constructor that also selects a particular generation method
	 */
	public Maze(int method)
	{
		super() ;
		// 0 is default, do not accept other settings but 0 and 1
		if (1 == method)
			this.method = 1 ;
		if (2 == method)
			this.method = 2;
		panel = new MazePanel(context) ;
	}
	
	public void setProgessBar(ProgressBar p){
		progressbar = p;
	}
		
	/**
	 * Method to initialize internal attributes. Called separately from the constructor. 
	 */
	public void init() {
		state = Constants.STATE_TITLE;
		rset = new RangeSet();
		panel.initBufferImage() ;
		MazeView = new MazeView(this);
		addView(MazeView) ;
		notifyViewerRedraw() ;
	}
	
	/**
	 * Method obtains a new Mazebuilder and has it compute new maze, 
	 * it is only used in keyDown()
	 * @param skill level determines the width, height and number of rooms for the new maze
	 */
	public void build(int skill) {
		this.skill = skill;
		Log.v("GeneratingActivity", "Entered maze build");
		// switch screen
		state = Constants.STATE_GENERATING;
		percentdone = 0;
		notifyViewerRedraw() ;
		// select generation method
		switch(method){
		case 1 : mazebuilder = new MazeBuilderPrim(); // generate with Prim's algorithm
		break ;
		case 2: mazebuilder = new MazeBuilderAldousBroder(); //generate with Aldous Broder's algorithm
		break;
		case 0: // generate with Falstad's original algorithm (0 and default), note the missing break statement
		default : mazebuilder = new MazeBuilder(); 
		break ;
		}
		// adjust settings and launch generation in a separate thread
		mazew = Constants.SKILL_X[skill];
		mazeh = Constants.SKILL_Y[skill];
		mazebuilder.build(this, mazew, mazeh, Constants.SKILL_ROOMS[skill], Constants.SKILL_PARTCT[skill]);
		// mazebuilder performs in a separate thread and calls back by calling newMaze() to return newly generated maze
	}
	
	/**
	 * Call back method for MazeBuilder to communicate newly generated maze as reaction to a call to build()
	 * @param root node for traversals, used for the first person perspective
	 * @param cells encodes the maze with its walls and border
	 * @param dists encodes the solution by providing distances to the exit for each position in the maze
	 * @param startx current position, x coordinate
	 * @param starty current position, y coordinate
	 */
	public void newMaze(BSPNode root, Cells c, Distance dists, int startx, int starty) {
		Log.v(TAG, "Entered newMaze().");
		if (Cells.deepdebugWall)
		{   // for debugging: dump the sequence of all deleted walls to a log file
			// This reveals how the maze was generated
			c.saveLogFile(Cells.deepedebugWallFileName);
		}
		
		// adjust internal state of maze model
		showMaze = showSolution = solving = false;
		setMazecells(c) ;
		setMazedists(dists);
		seencells = new Cells(mazew+1,mazeh+1) ;
		rootnode = root ;
		setCurrentDirection(1, 0) ;
		setCurrentPosition(startx,starty) ;
		walkStep = 0;
		viewdx = dx<<16; 
		viewdy = dy<<16;
		angle = 0;
		mapMode = false;
		// set the current state for the state-dependent behavior
		state = Constants.STATE_PLAY;
		
		if(gen != null){
			Message m = new Message();
			gen.handler.sendMessage(m);
		}
		
		cleanViews() ;
		// register views for the new maze
		// mazew and mazeh have been set in build() method before mazebuider was called to generate a new maze.
		// reset map_scale in mapdrawer to a value of 10
		addView(new FirstPersonDrawer(Constants.VIEW_WIDTH,Constants.VIEW_HEIGHT,
				Constants.MAP_UNIT,Constants.STEP_SIZE, mazecells, seencells, 10, mazedists.getDists(), mazew, mazeh, root, this)) ;
		// order of registration matters, code executed in order of appearance!
		addView(new MapDrawer(Constants.VIEW_WIDTH,Constants.VIEW_HEIGHT,Constants.MAP_UNIT,Constants.STEP_SIZE, getMazecells(), seencells, 10, getMazedists().getDists(), mazew, mazeh, this)) ;
		// notify viewers
		notifyViewerRedraw();
		
		
		if (driveType.equals("Manual")){
			Log.v(TAG, "Driver set to Manual.");
			r = new BasicRobot(this);
			//r.setCells(c);
			driver = new ManualDriver(this);
			driver.setRobot(r);
		}
		
		else if (driveType.equals("Gambler")){
			Log.v(TAG, "Driver set to Gambler");
			r = new BasicRobot(this);
			//r.setCells(c);
			driver = new Gambler(this);
			driver.setRobot(r);
		}
		
		else if (driveType.equals("Wall Follower")){
			Log.v(TAG, "Driver set to Wall Follower");
			r = new BasicRobot(this);
			//r.setCells(c);
			driver = new WallFollower(this);
			driver.setRobot(r);

		}
		
		else if (driveType.equals("Wizard")){
			Log.v(TAG, "Driver set to Wizard");
			r = new BasicRobot(this);
			r.setCells(c);
			driver = new Wizard(this);
			driver.setRobot(r);
		}
		
		else if (driveType.equals("Tremaux")){
			Log.v(TAG, "Driver set to Tremaux");
			r = new BasicRobot(this);
			r.setCells(c);
			driver = new Tremaux(this);
			driver.setRobot(r);
		}
	}

	/////////////////////////////// Methods for the Model-View-Controller Pattern /////////////////////////////
	/**
	 * Register a view
	 */
	public void addView(Viewer view) {
		views.add(view) ;
	}
	
	/**
	 * Unregister a view
	 */
	public void removeView(Viewer view) {
		views.remove(view) ;
	}
	
	/**
	 * Remove obsolete FirstPersonDrawer and MapDrawer
	 */
	private void cleanViews() {
		// go through views and notify each one
		Iterator<Viewer> it = views.iterator() ;
		while (it.hasNext())
		{
			Viewer v = it.next() ;
			if ((v instanceof FirstPersonDrawer)||(v instanceof MapDrawer))
			{
				//System.out.println("Removing " + v);
				it.remove() ;
			}
		}
	}
	
	/**
	 * Notify all registered viewers to redraw their graphics
	 */
	protected void notifyViewerRedraw() {
		// go through views and notify each one
		Iterator<Viewer> it = views.iterator() ;
		while (it.hasNext())
		{
			//panel.setBufferGraphics();2
			Viewer v = it.next() ;
			// viewers draw on the buffer graphics
			v.redraw(panel, state, px, py, viewdx, viewdy, walkStep, Constants.VIEW_OFFSET, rset, angle) ;
		}
	}
	
	/** 
	 * Notify all registered viewers to increment the map scale
	 */
	public void notifyViewerIncrementMapScale() {
		// go through views and notify each one
		Iterator<Viewer> it = views.iterator() ;
		while (it.hasNext())
		{
			Viewer v = it.next() ;
			v.incrementMapScale() ;
		}
	}
	
	/** 
	 * Notify all registered viewers to decrement the map scale
	 */
	public void notifyViewerDecrementMapScale() {
		// go through views and notify each one
		Iterator<Viewer> it = views.iterator() ;
		while (it.hasNext())
		{
			Viewer v = it.next() ;
			v.decrementMapScale() ;
		}
	}
	
	////////////////////////////// get methods ///////////////////////////////////////////////////////////////
	boolean isInMapMode() { 
		return mapMode ; 
	} 
	
	boolean isInShowMazeMode() { 
		return showMaze ; 
	} 
	
	boolean isInShowSolutionMode() { 
		return showSolution ; 
	} 
	
	public String getPercentDone(){
		return String.valueOf(percentdone) ;
	}
	
	public MazePanel getPanel() {
		return panel ;
	}
	
	////////////////////////////// set methods ///////////////////////////////////////////////////////////////
	////////////////////////////// Actions that can be performed on the maze model ///////////////////////////
	/**
	 * Sets current position of robot.
	 * @param x
	 * @param y
	 */
	public void setCurrentPosition(int x, int y) {
		px = x ;
		py = y ;
	}
	
	/**
	 * Sets current direction of robot.
	 * @param x
	 * @param y
	 */
	public void setCurrentDirection(int x, int y) {
		dx = x ;
		dy = y ;
	}
	
	/**
	 * Interrupts maze building.
	 */
	void buildInterrupted() {
		state = Constants.STATE_TITLE;
		notifyViewerRedraw() ;
		mazebuilder = null;
	}

	/**
	 * Radify.
	 * @param x
	 * @return
	 */
	final double radify(int x) {
		return x * Math.PI / 180;
	}

	/**
	 * Allows external increase to percentage in generating mode with subsequence graphics update
	 * @param pc gives the new percentage on a range [0,100]
	 * @return true if percentage was updated, false otherwise
	 */
	public boolean increasePercentage(int pc) {
		if (percentdone < pc && pc < 100) {
			percentdone = pc;
			if (state == Constants.STATE_GENERATING)
			{
				gen.setProgress(pc);
			}
			else
				dbg("Warning: Receiving update request for increasePercentage while not in generating state, skip redraw.") ;
			return true ;
		}
		return false ;
	}

	/////////////////////// Methods for debugging ////////////////////////////////
	
	/**
	 * Prints debug string.
	 * @param str
	 */
	private void dbg(String str) {
		//System.out.println(str);
	}

	/**
	 * Passes current position to dbg().
	 */
	private void logPosition() {
		if (!deepdebug)
			return;
		dbg("x="+viewx/Constants.MAP_UNIT+" ("+
				viewx+") y="+viewy/Constants.MAP_UNIT+" ("+viewy+") ang="+
				angle+" dx="+dx+" dy="+dy+" "+viewdx+" "+viewdy);
	}
	
	///////////////////////////////////////////////////////////////////////////////

	/**
	 * Helper method for walk()
	 * @param dir
	 * @return true if there is no wall in this direction
	 */
	boolean checkMove(int dir) {
		// obtain appropriate index for direction (CW_BOT, CW_TOP ...) 
		// for given direction parameter
		int a = angle/90;
		if (dir == -1)
			a = (a+2) & 3; // TODO: check why this works
		// check if cell has walls in this direction
		// returns true if there are no walls in this direction
		return getMazecells().hasMaskedBitsFalse(px, py, Constants.MASKS[a]) ;
	}

	/**
	 * Helper method for rotate().
	 */
	private void rotateStep() {
		angle = (angle+1800) % 360;
		viewdx = (int) (Math.cos(radify(angle))*(1<<16));
		viewdy = (int) (Math.sin(radify(angle))*(1<<16));
		moveStep();
	}

	/**
	 * Helper method for walk().
	 */
	private void moveStep() {
		notifyViewerRedraw() ;
		try {
			Thread.currentThread().sleep(25);
		} catch (Exception e) { }
	}

	/**
	 * Helper method for rotate().
	 */
	private void rotateFinish() {
		setCurrentDirection((int) Math.cos(radify(angle)), (int) Math.sin(radify(angle))) ;
		logPosition();
	}

	/**
	 * Helper method for walk().
	 * @param dir
	 */
	private void walkFinish(int dir) {
		setCurrentPosition(px + dir*dx, py + dir*dy) ;
		
		if (isEndPosition(px,py)) {
			state = Constants.STATE_FINISH;
			notifyViewerRedraw() ;
		}
		walkStep = 0;
		logPosition();
	}

	/**
	 * Checks if the given position is outside the maze
	 * @param x
	 * @param y
	 * @return true if position is outside, false otherwise
	 */
	boolean isEndPosition(int x, int y) {
		return x < 0 || y < 0 || x >= mazew || y >= mazeh;
	}

	/**
	 * Tells the robot to walk in a direction.
	 * @param dir
	 */
	synchronized private void walk(int dir) {
		if (!checkMove(dir))
			return;
		for (int step = 0; step != 4; step++) {
			walkStep += dir;
			moveStep();
		}
		walkFinish(dir);
	}

	/**
	 * Tells the robot to rotate in a direction.
	 * @param dir
	 */
	synchronized private void rotate(int dir) {
		final int originalAngle = angle;
		final int steps = 4;

		for (int i = 0; i != steps; i++) {
			angle = originalAngle + dir*(90*(i+1))/steps;
			rotateStep();
		}
		rotateFinish();
	}

	/**
	 * Method incorporates all reactions to keyboard input in original code, 
	 * after refactoring, Java Applet and Java Application wrapper call this method to communicate input.
	 */
	public boolean keyDown(int key) {
		switch (state) {
		// if screen shows title page, keys describe level of expertise
		// create a maze according to the user's selected level
		case Constants.STATE_TITLE:
			
			if (key >= '0' && key <= '9') {
				build(key - '0');
				break;
			}
			if (key >= 'a' && key <= 'f') {
				build(key - 'a' + 10);
				break;
			}
			break;
		// if we are currently generating a maze, recognize interrupt signal (ESCAPE key)
		// to stop generation of current maze
		case Constants.STATE_GENERATING:
			if (key == ESCAPE) {
				mazebuilder.interrupt();
				buildInterrupted();
			}
			break;
		// if user explores maze, 
		// react to input for directions and interrupt signal (ESCAPE key)	
		// react to input for displaying a map of the current path or of the overall maze (on/off toggle switch)
		// react to input to display solution (on/off toggle switch)
		// react to input to increase/reduce map scale
		case Constants.STATE_PLAY:
			switch (key) {
			case UP: case 'k': case '8':
				walk(1);
				break;
			case LEFT: case 'h': case '4':
				rotate(1);
				break;
			case RIGHT: case 'l': case '6':
				rotate(-1);
				break;
			case DOWN: case 'j': case '2':
				walk(-1);
				break;
			case ESCAPE: case 65385:
				if (solving)
					solving = false;
				else
					state = Constants.STATE_TITLE;
				notifyViewerRedraw() ;
				break;
			case ('w' & 0x1f): 
			{ 
				setCurrentPosition(px + dx, py + dy) ;
				notifyViewerRedraw() ;
				break;
			}
			case '\t': case 'm':
				mapMode = !mapMode; 		
				notifyViewerRedraw() ; 
				break;
			case 'z':
				showMaze = !showMaze; 		
				notifyViewerRedraw() ; 
				break;
			case 's':
				showSolution = !showSolution; 		
				notifyViewerRedraw() ;
				break;
			case ('s' & 0x1f):
				if (solving)
					solving = false;
				else {
					solving = true;
				}
			break;
			case '+': case '=':
			{
				notifyViewerIncrementMapScale() ;
				notifyViewerRedraw() ; // seems useless but it is necessary to make the screen update
				break ;
			}
			case '-':
				notifyViewerDecrementMapScale() ;
				notifyViewerRedraw() ; // seems useless but it is necessary to make the screen update
				break ;
			}
			break;
		// if we are finished, return to initial state with title screen	
		case Constants.STATE_FINISH:
			state = Constants.STATE_TITLE;
			notifyViewerRedraw() ;
			break;
		} 
		return true;
	}
	
	/**
	 * Getter for mazedists.
	 * @return mazedists
	 */
	public Distance getMazedists() {
		return mazedists;
	}
	
	/**
	 * Setter for mazedists.
	 * @param mazedists
	 */
	public void setMazedists(Distance mazedists) {
		this.mazedists = mazedists;
	}
	
	/**
	 * Getter for mazecells.
	 * @return mazecells
	 */
	public Cells getMazecells() {
		return mazecells;
	}
	
	/**
	 * Setter for mazecells.
	 * @param mazecells
	 */
	public void setMazecells(Cells mazecells) {
		this.mazecells = mazecells;
	}
	
	/**
	 * Setter for generation method.
	 * @param method
	 */
	public void setGenerationMethod(int method) {
		this.method = method;
		
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Helper method for Android port.
	 * Builds maze with a specified skill level. 
	 * @param s
	 * @return
	 * @throws Exception
	 */
	public void skillBuild(int s) throws Exception{
		Log.v("Maze", "Building with skill " + s);
		init();
		
		if (s < 0 || s > 9){
			return;
		}

		build(s);
	}

	/**
	 * Toggles the map on.
	 */
	public void showMap(){
		if(showMaze) {
			showMaze = !showMaze;
		}
		
		if (mapMode) {
			return;
		}
		
		mapMode = !mapMode; 
	    panel.draw.postInvalidate();	
	}

	/**
	 * Toggles the map off.
	 */
	public void hideMap() {
		if (mapMode) {
			mapMode = !mapMode;
		}	
		
		notifyViewerRedraw() ;
	}	
	
	/**
	 * Toggles the full map on and off.
	 */
	public void showWalls(){
		showMaze = !showMaze; 		
		notifyViewerRedraw() ;
	}
	
	/**
	 * Toggles the solution on and off.
	 */
	public void showSolution(){
		showSolution = !showSolution; 		
		notifyViewerRedraw() ;
	}
	
	/**
	 * Zooms the maze in by one unit.
	 * @return true if successful.
	 */
	public void zoomIn(){
		notifyViewerIncrementMapScale() ;
		notifyViewerRedraw() ;
	}
	
	/**
	 * Zooms the maze out by one unit.
	 * @return true if successful.
	 */
	public void zoomOut(){
		notifyViewerDecrementMapScale() ;
		notifyViewerRedraw() ;
	}
	
	/**
	 * Setter for generation method.
	 * @param meth
	 */
	public void setMethod(int meth){
		Log.v(TAG, "Method changed to " + meth);
		method = meth;
	}
	
	/**
	 * Setter for robot driver. Attaches a robot driver to a maze.
	 * @param driver
	 */
	public void setDriver(String driver){
		Log.v(TAG, "Driver changed to " + driver);
		driveType = driver;
	}

	public RobotDriver getDriver() {
		// TODO Auto-generated method stub
		return driver;
	}

	
}
