package edu.wm.cs.cs301.amazebyjackiebethany.falstad;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaPlayer;

public class Globals { 
	public static Maze maze;
	
	public static Bitmap bitmap1;
	
	public static Bitmap bitmap2;
	
	public static MediaPlayer sound1;
	
	public static int sleepInterval = 500;
	
	public static void makeMaze(Activity a){
		maze = new Maze(a.getBaseContext());
	}
}
