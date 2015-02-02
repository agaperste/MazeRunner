package edu.wm.cs.cs301.amazebyjackiebethany.ui;

import com.example.amazebyjackiebethany.R;

import edu.wm.cs.cs301.amazebyjackiebethany.falstad.BasicRobot;
import edu.wm.cs.cs301.amazebyjackiebethany.falstad.Globals;
import edu.wm.cs.cs301.amazebyjackiebethany.falstad.Maze;
import edu.wm.cs.cs301.amazebyjackiebethany.falstad.RobotDriver;
import edu.wm.cs.cs301.amazebyjackiebethany.falstad.Robot.Turn;
import edu.wm.cs.cs301.amazebyjackiebethany.ui.FinishActivity;
import edu.wm.cs.cs301.amazebyjackiebethany.ui.PlayActivity;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.OnScaleGestureListener;
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class PlayActivity extends Activity {
	
	private static final String TAG = "Play Activity";
		
	public static Handler mHandler = new Handler();
		
	ToggleButton map_toggle;
	ToggleButton walls_toggle;
	ToggleButton solution_toggle;
	ToggleButton pause_toggle;
    
	ToggleButton wallToggle;
	ToggleButton solutionToggle;
	
	TextView batteryText;
	
	Maze maze;
	
	String driveMethod;
	
	Boolean success = false;
	String condition;
	
	MediaPlayer musicHobbit;
	
	public PlayActivity play = this;
	
	public static boolean solving = true;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_play);
		
		Log.i(TAG, "Entered PlayActivity");
		
        driveMethod = (String) getIntent().getStringExtra("driveMethod");
        Log.v(TAG, driveMethod);
        
    	Button upButton = (Button) findViewById(R.id.up_button);
    	Button rightButton = (Button) findViewById(R.id.right_button);
    	Button downButton = (Button) findViewById(R.id.down_button);
    	Button leftButton = (Button) findViewById(R.id.left_button);
    	ToggleButton pauseToggle = (ToggleButton) findViewById(R.id.pause_toggle);
        
        if (!driveMethod.equals("Manual")) {
        	upButton.setVisibility(View.INVISIBLE);
        	rightButton.setVisibility(View.INVISIBLE);
        	downButton.setVisibility(View.INVISIBLE);
        	leftButton.setVisibility(View.INVISIBLE);
        }
        
        else {
        	pauseToggle.setVisibility(View.INVISIBLE);
        }
        
		// Drawing view
		RelativeLayout layout = new RelativeLayout(this);
	    DrawingView myView = new DrawingView(this,null);
	    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
	    params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
	    layout.addView(myView,params);
	    	    
	    wallToggle = (ToggleButton) findViewById(R.id.walls_toggle);
	    solutionToggle = (ToggleButton) findViewById(R.id.solution_toggle);
	    wallToggle.setVisibility(View.INVISIBLE);
	    solutionToggle.setVisibility(View.INVISIBLE);
	    		
		maze = Globals.maze;
		maze.play = this;
				
		Log.i(TAG, "Retrieved maze: " + maze);
		musicHobbit = MediaPlayer.create(getApplicationContext(), R.raw.brassbuttons);
        musicHobbit.start();
		startPlay();
	}
	
	/**
	 * Start the thread
	 */
	public void startPlay(){
		thread.start();
	}
	
	/**
	 * Calls startPLay(
	 */
	public Handler handler = new Handler() {
		@Override
		public void handleMessage(Message message) {
			Log.i(TAG, "Handler called.");
			startPlay();
			
		}
	};
	
	/**
	 * Thread drives2exit.
	 */
	Thread thread = new Thread(new Runnable() {
		public void run() {
			Log.v(TAG, "Thread running.");
			// Instantiate the battery text.
			batteryText = (TextView) findViewById(R.id.battery_value);
						
			while (!success) {
				if (solving) {
					
					try {
						success = maze.driver.drive2Exit();
					} catch ( ArrayIndexOutOfBoundsException e ) {
						success = true;
					}
					
					try {
						thread.sleep(Globals.sleepInterval);
					} catch (Exception e) {
						Log.v(TAG, "Thread exception.");
					}
					
					runOnUiThread(new Runnable() {
	                    @Override
	                    public void run() {
	                    	batteryText.setText(Integer.toString(maze.r.getBatteryLevel()));
	                    }
	                });
					
				}
			}
			
			
			onCompletion(success);
				  		
			finish();
		}
	});
	
	/**
	 * Moves to FinishActivity.
	 */
	public void onCompletion(Boolean success) {
		int batteryConsumed = (int) maze.driver.getEnergyConsumption();
		int batteryLevel = 2500 - batteryConsumed; 
		int pathLength = maze.driver.getPathLength();
		
		if (success) {
			condition = "Win";
		}
		
		else {
			condition = "Lose";
		}
		
		Intent myIntent = new Intent(PlayActivity.this, FinishActivity.class);
		
		myIntent.putExtra("Battery", Integer.toString(batteryLevel));
		myIntent.putExtra("Condition", condition);
		myIntent.putExtra("Path", Integer.toString(pathLength));
		myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		musicHobbit.release();
		PlayActivity.this.startActivity(myIntent);		
	}

	/**
	 * If robot reaches goal
	 */
	private void onReachingGoal() {
		musicHobbit.release();
		int battery = maze.r.getBatteryLevel();
		int path = maze.driver.getPathLength();
		String condition = "Win";
		
		Intent myIntent = new Intent(PlayActivity.this, FinishActivity.class);
		
		myIntent.putExtra("Battery", Integer.toString(battery));
		myIntent.putExtra("Condition", condition);
		myIntent.putExtra("Path", Integer.toString(path));
		
		PlayActivity.this.startActivity(myIntent);
	}
	
	/**
	 * If the robot's battery is empty
	 */
	private void onFailure() {
		int battery = maze.r.getBatteryLevel();
		int path = maze.driver.getPathLength();
		String condition = "Lose";
		
		Intent myIntent = new Intent(PlayActivity.this, FinishActivity.class);
		
		myIntent.putExtra("Battery", Integer.toString(battery));
		myIntent.putExtra("Condition", condition);
		myIntent.putExtra("Path", Integer.toString(path));
		
		PlayActivity.this.startActivity(myIntent);
	}
	
	/**
	 * Move rovot forward
	 * @param view
	 */
	public void onUpClick(View view) {
		
		try {
			maze.r.move(1);
			batteryText.setText(Float.toString(maze.r.getBatteryLevel()));
			
		} catch (Exception e) {
			Log.v(TAG, "Cannot move in that direction.");
			Toast.makeText(getApplicationContext(),
					"Cannot move in that direction", Toast.LENGTH_SHORT).show();
		}
		
		if (maze.r.getBatteryLevel() <= 0) {
			Log.v(TAG, "Battery is empty.");
			onFailure();
		}
		
		if (maze.r.isAtGoal()) {
			Log.v(TAG, "Reached the goal.");
			onReachingGoal();
		}
	    
	    maze.panel.draw.invalidate();
	}

	/**
	 * rotate the robot left.
	 * @param view
	 */
	public void onLeftClick(View view) {
		
		try {
			maze.r.rotate(Turn.LEFT);
			batteryText.setText(Float.toString(maze.r.getBatteryLevel()));
			
		} catch (Exception e) {
			Log.v(TAG, "Cannot turn.");
			Toast.makeText(getApplicationContext(),
					"Cannot turn", Toast.LENGTH_SHORT).show();
		}
	    
	    maze.panel.draw.invalidate();
		
	}
	
	/**
	 * rotate the robot right.
	 * @param view
	 */
	public void onRightClick(View view) {
		
		try {
			maze.r.rotate(Turn.RIGHT);
			batteryText.setText(Float.toString(maze.r.getBatteryLevel()));
			
		} catch (Exception e) {
			Log.v(TAG, "Cannot turn.");
			Toast.makeText(getApplicationContext(),
					"Cannot turn", Toast.LENGTH_SHORT).show();
		}
	    
	    maze.panel.draw.invalidate();
		
	}
	
	/**
	 * rotate the robot around.
	 * @param view
	 */
	public void onDownClick(View view) {
		
		try {
			maze.r.rotate(Turn.AROUND);
			batteryText.setText(Integer.toString(maze.r.getBatteryLevel()));
			
		} catch (Exception e) {
			Log.v(TAG, "Cannot turn.");
			Toast.makeText(getApplicationContext(),
					"Cannot turn", Toast.LENGTH_SHORT).show();
		}
	    
	    maze.panel.draw.invalidate();
		
	}
	
	/**
	 * show or hide the map.
	 * @param view
	 */
	public void onMapToggle(View view) {
	    // Is the toggle on?
		
	    if (((ToggleButton) view).isChecked()) {
	    	Log.v(TAG, "Map toggled on");
			maze.showMap();
			wallToggle.setVisibility(View.VISIBLE);
			solutionToggle.setVisibility(View.VISIBLE);
	    } 
	    
	    else {
	    	Log.v(TAG, "Map toggled off");
			maze.hideMap();
			wallToggle.setVisibility(View.INVISIBLE);
			wallToggle.setChecked(false);
			
			solutionToggle.setVisibility(View.INVISIBLE);
			solutionToggle.setChecked(false);
	    }

	    maze.panel.draw.invalidate();
	}
	
	/**
	 * show or hide the unseen walls.
	 * @param view
	 */
	public void onWallToggle(View view) {
	    
	    if (((ToggleButton) view).isChecked()) {
	    	Log.v(TAG, "Map toggled on");
			maze.showWalls();
	    } 
	    
	    else {
	    	Log.v(TAG, "Map toggled off");
			maze.showWalls();
	    }
	    
	    maze.panel.draw.invalidate();
	}
	
	/**
	 * show or hide the solution.
	 * @param view
	 */
	public void onSolutionToggle(View view) {
	    
	    if (((ToggleButton) view).isChecked()) {
			maze.showSolution();
	    	Log.v(TAG, "Solution toggled on");
	    } 
	    
	    else {
			maze.showSolution();
	    	Log.v(TAG, "Solution toggled off");
	    }
	    
	    maze.panel.draw.invalidate();
	}

	/**
	 * pause or resume the robot driver.
	 * @param view
	 */
	public void onPauseToggle(View view) {
	    
	    if (((ToggleButton) view).isChecked()) {
	    	Log.v(TAG, "Pause.");
	    	musicHobbit.pause();
	    	solving = false;
	    }
	    
	    else {
	    	Log.v(TAG, "Resume.");
	    	musicHobbit.start();
	    	solving = true;
	    }
	    
	    maze.panel.draw.invalidate();
	}
		
	/**
	 * return to the main screen.
	 */
	@Override
    public void onBackPressed() {
        // Write your code here
 
        super.onBackPressed();
        musicHobbit.release();
        maze = null;
        
		Intent k = new Intent(getApplicationContext(), AMazeActivity.class);
		Toast.makeText(getApplicationContext(),
				"Returning to AMazeActivity", Toast.LENGTH_SHORT).show();
		Log.v(TAG, "Pressed back button. Switching to A Maze Activity");
		startActivity(k);
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Log.v(TAG, "Inflate Menu");
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.amaze, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		
		int id = item.getItemId();
		Log.v(TAG, "onOptionsItemSelected " + id);
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public RobotDriver getDriver() {
		return maze.driver;
	}
	
}
