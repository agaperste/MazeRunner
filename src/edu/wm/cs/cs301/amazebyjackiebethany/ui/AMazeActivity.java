package edu.wm.cs.cs301.amazebyjackiebethany.ui;

import com.example.amazebyjackiebethany.R;

import edu.wm.cs.cs301.amazebyjackiebethany.falstad.Globals;
import edu.wm.cs.cs301.amazebyjackiebethany.falstad.Maze;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


public class AMazeActivity extends Activity implements AdapterView.OnItemSelectedListener, OnCheckedChangeListener, OnClickListener {
	
	private static final String TAG = "AMazeActivity";
	
	Spinner spinnerGenMain;
	Spinner spinnerGenSecond;
	Spinner spinnerRobot;
	Spinner driver;
	
	int callednum1 = 0;
	int callednum2 = 0;
	int cNum = 0;
	int option = 0; 
	
	int itemGen;
	
	Button continue_button;
	
	Spinner random_gen;
	
	SeekBar complexity_bar;
	TextView difficulty_text;
	
	String buildMethod = "Null";
	String driveMethod = "Null";
	int skill = 111;
	
	private Maze maze;

	ArrayAdapter<CharSequence> adapterR;

	ArrayAdapter<CharSequence> adapterF;
	
	View save;
	
	@Override
	/**
	 * Initialize layout
	 */
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_amaze);
		
		Globals.makeMaze(this);
		maze = Globals.maze;
		Log.v(TAG, "makeMaze() called and set.");
		
		// Set up continue button.
		continue_button = (Button) findViewById(R.id.continue_button);
		continue_button.setOnClickListener(this);
		
		save = findViewById(R.id.buttonSave);
		
		// make the adapter for the random array
				//generation_random_array"
				adapterR = ArrayAdapter.createFromResource(this,
				        R.array.generation_random_array, android.R.layout.simple_spinner_item);
				// Specify the layout to use when the list of choices appears
				adapterR.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

				// make the adapter for the file array
				adapterF = ArrayAdapter.createFromResource(this,
				        R.array.generation_file_array, android.R.layout.simple_spinner_item);
				// Specify the layout to use when the list of choices appears
				adapterF.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

				// Maze Generation set up and listener -- random v file
				spinnerGenMain = (Spinner) findViewById(R.id.random_gen);
				spinnerGenMain.setOnItemSelectedListener(new OnItemSelectedListener() {
		            

					@Override
					public void onItemSelected(AdapterView<?> parentView,
		                    View selectedItemView, int position, long id) {
		                // Object item = parentView.getItemAtPosition(position);
		            		if (cNum != 0 && cNum != 1 && cNum != 2){
		        			Toast.makeText(getApplicationContext(), "You have selected " + parentView.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
		            		}
		            		cNum++;
		                // Depend on first spinner value set adapter to 2nd spinner
		            	itemGen = spinnerGenMain.getSelectedItemPosition();
		            	//if it is Random
		            	if(itemGen == 0){
		            		option = 0;
		            		spinnerGenSecond.setAdapter(adapterR);
		            		//Toast.makeText(getApplicationContext(), "You have selected Random", Toast.LENGTH_SHORT).show();
		            		Log.v("AMazeActivity", "Random spinner selected");
		                    save.setVisibility(View.GONE);
		                    //option = 0;
		           		}
		           		// if it is from the file
		           		else{
		            		//Toast.makeText(getApplicationContext(), "You have selected File", Toast.LENGTH_SHORT).show();
		           			option = 1;
		           			Log.v("AMazeActivity", "File spinner option is " + option);
		           			spinnerGenSecond.setAdapter(adapterF);
		            		Log.v("AMazeActivity", "File spinner selected");
		            		save.setVisibility(View.VISIBLE);
		            		//option = 1;
		            		//Log.v("AMazeActivity", "File spinner option is " + option);
		           		} 
		            	
		            }
		            @Override
		            public void onNothingSelected(AdapterView<?> arg0) {// do nothing
		            }
		        });
				
				spinnerGenSecond = (Spinner) findViewById(R.id.algorithm);
				// Maze Generation set up and listener -- which creation algorithm v which file
				spinnerGenSecond.setOnItemSelectedListener(new OnItemSelectedListener() {
		            @Override
					public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
		                // Object item = parentView.getItemAtPosition(position);
		            	if (cNum != 0 && cNum != 1 && cNum != 2){
		        		Toast.makeText(getApplicationContext(), "You have selected " + parentView.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
		            	}
		            	cNum++;
		            	
		            	buildMethod = spinnerGenSecond.getSelectedItem().toString(); 
		            	}
		            @Override
		            public void onNothingSelected(AdapterView<?> arg0) {// do nothing
		            }
		        });
				
				//populate and make the Robot Spinner
				ArrayAdapter<CharSequence> adapterR = ArrayAdapter.createFromResource(this,
				        R.array.robot_operation_array, android.R.layout.simple_spinner_item);
				// Specify the layout to use when the list of choices appears
				adapterR.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				// Apply the adapter to the spinner
				spinnerRobot = (Spinner) findViewById(R.id.driver);
				spinnerRobot.setAdapter(adapterR);
				
				// Robot listener and set up
				spinnerRobot.setOnItemSelectedListener(new OnItemSelectedListener() {
		            @Override
					public void onItemSelected(AdapterView<?> parentView,
		                    View selectedItemView, int position, long id) {
		                // Object item = parentView.getItemAtPosition(position);
		            	if (cNum != 0 && cNum != 1){
		        			Toast.makeText(getApplicationContext(), "You have selected " + parentView.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
		        		}
		        		cNum ++;
		                // Depend on first spinner value set adapter to 2nd spinner
		            	int itemGen = spinnerRobot.getSelectedItemPosition();
		            	driveMethod = parentView.getSelectedItem().toString();
		            }
		            @Override
		            public void onNothingSelected(AdapterView<?> arg0) {// do nothing
		            }

		        });
				Log.v("AMazeActivity", "Robot Spinner created");
				
		
		// Set up seekbar for difficulty selection.
		complexity_bar = (SeekBar) findViewById(R.id.complexity);
		difficulty_text = (TextView) findViewById(R.id.difficulty);
		
		difficulty_text.setText("Difficulty: " + complexity_bar.getProgress() + "/" + complexity_bar.getMax());
		complexity_bar.setOnSeekBarChangeListener(
				/**
				 * Create a new listener for the seekbar to change the text value to display the difficulty level.
				 */
				new OnSeekBarChangeListener() {
				
					// Initialize progress value to display as text.
			        int progress = 0;
			        
			        /**
			         * Whenever the seekbar's progress is changed, reset the progress value to the new value.
			         */
			        @Override
			        public void onProgressChanged(SeekBar seekBar, 
			                  int progressValue, boolean fromUser) {
			        	progress = progressValue;
			        	skill = progressValue;
			        }
	
			        @Override
			        public void onStartTrackingTouch(SeekBar seekBar) {
			        	difficulty_text.setText("Difficulty: " + progress + "/" + complexity_bar.getMax());
			    	  
			        }
	
			        /**
			         * When the user is no longer interacting with the seekbar, change the text to display the new progress value.
			       	*/
			        @Override
			        public void onStopTrackingTouch(SeekBar seekBar) {
			        	// Display the value in textview
			        	difficulty_text.setText("Difficulty: " + progress + "/" + complexity_bar.getMax());
			        	Log.v(TAG, "difficulty set");
			        	Toast.makeText(getApplicationContext(), "Difficulty set to " + progress, Toast.LENGTH_LONG).show();
			        	skill = progress;
			        }
				});
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		Log.v(TAG, "Inflating Menu");
		getMenuInflater().inflate(R.menu.amaze, menu);
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
	public void onNothingSelected(AdapterView<?> parent) {
		
	}
	
	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * When the button is clicked, continue to GeneratingActivity and pass on generation specifications.
	 */
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		// Log.v("AMazeActivity - Continue button pressed", null);
		Intent k = new Intent(getApplicationContext(), GeneratingActivity.class);
		
		// Push generation specifications into the intent.
		k.putExtra("itemGen", itemGen);
		k.putExtra("driveMethod", driveMethod);
		k.putExtra("buildMethod", buildMethod);
		k.putExtra("skill", complexity_bar.getProgress());
		
		Log.v(TAG, "Switching to Generating Activity");
		Toast.makeText(getApplicationContext(),
				"Continuing to Generation", Toast.LENGTH_SHORT).show();
		startActivity(k);

	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		
	}
}


