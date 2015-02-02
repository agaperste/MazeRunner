package edu.wm.cs.cs301.amazebyjackiebethany.ui;

import edu.wm.cs.cs301.amazebyjackiebethany.falstad.Globals;
import edu.wm.cs.cs301.amazebyjackiebethany.falstad.Maze;
import edu.wm.cs.cs301.amazebyjackiebethany.falstad.MazePanel;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class DrawingView extends View {

	private static final String TAG = "DrawingView";
	
    public Context context;
    public Maze maze;
    public MazePanel mazepanel;
    public Canvas gc;
    public Canvas c;
    public Bitmap bmp;
    
    public DrawingView draw = this;
    
    public Handler handler = new Handler() {
		  @Override
		  public void handleMessage(Message message) {
				  draw.update(); 
		     }
		 };
    
    public DrawingView(Context context, AttributeSet attrs) {
        super(context,attrs);
        
        maze = Globals.maze;
        mazepanel = maze.panel;
        gc = mazepanel.canvas;
        bmp = mazepanel.bufferImage;
        mazepanel.draw = this;
    }
    
    public void onDraw(Canvas canvas) {
    	maze = Globals.maze;
        mazepanel = maze.panel;
        gc = mazepanel.canvas;
        bmp = mazepanel.bufferImage;
        mazepanel.draw = this;
    	c = canvas;
    	Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
    	canvas.drawBitmap(bmp,0,0,paint);
    	c = canvas;
    }
    
    public void update() {
    	this.invalidate();
    	Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        
        if(c == null) {
        	Log.v("DrawingView", "Null canvas."); 
        }
        
        else {
        	Log.v("DrawingView", "Canvas not null.");
        	
        	if (bmp == null) {
        		Log.v("DrawingView", "Null bitmap.");
        	}
        	
        	else {
	        	c.drawBitmap(bmp,0,0,paint);
	        	Log.v("DrawingView", "Bitmap drawn.");
	        	this.draw(c);  
        	}
        }
    }
}