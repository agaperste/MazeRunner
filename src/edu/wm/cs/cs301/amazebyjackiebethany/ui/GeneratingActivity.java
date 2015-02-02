package edu.wm.cs.cs301.amazebyjackiebethany.ui;

import com.example.amazebyjackiebethany.R;

import edu.wm.cs.cs301.amazebyjackiebethany.falstad.Constants;
import edu.wm.cs.cs301.amazebyjackiebethany.falstad.Globals;
import edu.wm.cs.cs301.amazebyjackiebethany.falstad.Maze;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

public class GeneratingActivity extends Activity implements OnClickListener, Runnable {
	
	private static final String TAG = "GeneratingActivity";
	
	Button gen_cancel;
	Button gen_continue;
	ProgressBar progressbar;
	int progressStatus = 0;
	
	String genMethod;
	String genAlgorithm;
	int genDifficulty;
	
	String buildMethod = "No";
	String driveMethod = "No";
	int skill = 999;
	
	Maze maze;
	Thread thread;
	
	GeneratingActivity gen = this;
	
	private Handler mHandler = new Handler();
	
	boolean finishScreen = false;
	
	Thread background;
	
	/**
	 * Generate maze.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_state_generating);
		Log.v(TAG, "Entered GeneratingActivity");
		
		// Set Bitmap tiles
		Globals.bitmap1 = BitmapFactory.decodeResource(this.getResources(), R.drawable.dirt_tile);  

        maze = Globals.maze;
        maze.gen = this;
        Log.v(TAG, "Set maze object.");

        String buildMethod = (String) getIntent().getStringExtra("buildMethod");

        driveMethod = (String) getIntent().getStringExtra("driveMethod");
        
        int skill = (Integer) getIntent().getSerializableExtra("skill");
        Log.v(TAG, "Retrieved extras: " + skill + " " + buildMethod + " " + driveMethod);
    
        progressbar = (ProgressBar) findViewById(R.id.progressbar);
        progressbar.setProgress(0);
        Log.v(TAG, "Found and set progress bar.");
        
        background = new Thread (new Runnable() {
			public void run() {
				while (progressbar.getProgress() < 100) {
						
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
						
					progressHandler.sendMessage(progressHandler.obtainMessage());
				}
			}
        });
    	
    	Handler handler = new Handler(){
    		public void handleMessage(Message message) {
    			Log.i(TAG, "Handler called");
    			gen.startPlay();
    		}
    	};
		
		background.start();

        if (buildMethod == null) {
        	Log.v(TAG, "Null buildMethod.");
        	return;
        }
        
        else if(buildMethod.equals("Backtracking")) {
        	Log.i(TAG, "buildMethod confirmed as Backtracking.");
        	maze.setDriver(driveMethod);
        	maze.setMethod(0);
        	Log.i(TAG, "Maze generation method changed.");
        	
        	try {
				maze.skillBuild(skill);
			} catch (Exception e) {
				e.printStackTrace();
			}
        	
        	Log.i(TAG, "Maze built.");
        }
        
        else if(buildMethod.equals("Prim")) {
        	Log.i(TAG, "buildMethod confirmed as Prim.");
        	maze.setDriver(driveMethod);
        	maze.setMethod(1);
        	Log.i(TAG, "Maze generation method changed.");
        	
        	try {
				maze.skillBuild(skill);
			} catch (Exception e) {
				e.printStackTrace();
			}
        	
        	Log.i(TAG, "Maze built.");
        }
        
        else if(buildMethod.equals("Aldous Broder")) {
        	Log.i(TAG, "buildMethod confirmed as Aldous Broder.");
        	maze.setDriver(driveMethod);
        	maze.setMethod(2);
        	Log.i(TAG, "Maze generation method changed.");
        	
        	try {
				maze.skillBuild(skill);
			} catch (Exception e) {
				e.printStackTrace();
			}
        	
        	Log.i(TAG, "Maze built.");
        }
	}
	
	public Handler handler = new Handler(){
		public void handleMessage(Message message) {
			Log.i(TAG, "Handler called");
			gen.startPlay();
		}
	};
	
	/**
	 * Moves to PLayActivity.
	 */
    public void startPlay(){
    	
    	if (progressbar.getProgress() < 80) {
    		progressbar.setProgress(80);
    		progressHandler.postDelayed(background, 100);
    	}
    	
    	if (progressbar.getProgress() < 90) {
    		progressbar.setProgress(90);
    		progressHandler.postDelayed(background, 100);
    	}
    	
    	if (progressbar.getProgress() < 100) {
    		progressbar.setProgress(100);
    		progressHandler.postDelayed(background, 100);
    	}
    	
    	Intent k = new Intent(GeneratingActivity.this, PlayActivity.class);
    	k.putExtra("driveMethod", driveMethod);
    	GeneratingActivity.this.startActivity(k);
    }
	
    /**
     * Controls progress bar.
     */
    Handler progressHandler = new Handler() {
    	public void handleMessage(Message msg) {
    		progressbar.incrementProgressBy(30);
    	}
    };
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.generating, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * Return to the main screen.
	 */
	@Override
    public void onBackPressed() {
 
        super.onBackPressed();
        
        finishScreen = true;
        
		Toast.makeText(getApplicationContext(),
				"Returning to AMazeActivity", Toast.LENGTH_SHORT).show();
		Log.v(TAG, "Pressed back button. Switching to AMazeActivity");
		
		maze = null;
		
		onStop();
		finish();
    }
}
