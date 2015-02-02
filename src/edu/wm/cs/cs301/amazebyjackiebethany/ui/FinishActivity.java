package edu.wm.cs.cs301.amazebyjackiebethany.ui;

import com.example.amazebyjackiebethany.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class FinishActivity extends Activity {
	
	private static final String TAG = "Finish Activity";

	Button finish_button;

	TextView batteryConsumption;

	TextView pathText;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_finish);
		
		Log.v(TAG, "Entered FinishActivity");
		String condition = getIntent().getStringExtra("Condition");
		Log.v(TAG, "Got condition " + condition);
		String battery = getIntent().getStringExtra("Battery");
		Log.v(TAG, "Got battery " + battery);
		String path = getIntent().getStringExtra("Path");
		Log.v(TAG, "Got path " + path);
		
		if (condition.equals("Win")) {
			((TextView) findViewById(R.id.end_reason)).setText("You won!");
		} 
		
		else if (condition.equals("Lose")) {
			((TextView)findViewById (R.id.end_reason)).setText("You lost!");
		}
		
		int batteryInt = Integer.parseInt(battery);
		
		int batteryConsumed = 2500 - batteryInt;
		Log.v(TAG, "Battery consumption calculated " + batteryConsumed);
		batteryConsumption = (TextView) findViewById(R.id.energyCon);
		batteryConsumption.setText(batteryConsumed + " out of 2500.");
		
		pathText = (TextView) findViewById(R.id.pathNumber);
		pathText.setText(path + " steps");

		finish_button = (Button) findViewById(R.id.finish_button);
		
		finish_button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent k = new Intent(getApplicationContext(), AMazeActivity.class);
				Toast.makeText(getApplicationContext(),
						"Back To Begining", Toast.LENGTH_SHORT).show();
				Log.v(TAG, "Going to AMazeActivity");
				startActivity(k);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		Log.v(TAG, "Inflate menu");
		getMenuInflater().inflate(R.menu.finish, menu);
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
	
	/**
	 * When the back button is pressed, return AMazeActivity.
	 */
	@Override
    public void onBackPressed() {
 
        super.onBackPressed();
        
		Toast.makeText(getApplicationContext(),
				"Returning to AMazeActivity", Toast.LENGTH_SHORT).show();
		Log.v(TAG, "Pressed back button. Switching to AMazeActivity");
		
		onStop();
		finish();
    }
}
