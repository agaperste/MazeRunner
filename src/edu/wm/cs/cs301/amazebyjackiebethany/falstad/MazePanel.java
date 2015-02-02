package edu.wm.cs.cs301.amazebyjackiebethany.falstad;

import java.io.Serializable;

import edu.wm.cs.cs301.amazebyjackiebethany.falstad.Constants;
import edu.wm.cs.cs301.amazebyjackiebethany.ui.DrawingView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

public class MazePanel extends View {
	
	private static final String TAG = "MazePanel";
	
	public static Canvas canvas;
	public static Bitmap bufferImage;
	public Paint paint;
	public DrawingView draw;
	
	public Maze maze;
	
	static int width = 768;
	static int height = 1184;
	
	/**
	 * MazePanel constructor.
	 * @param context
	 */
	public MazePanel(Context context) {
		
		super(context);
		
		bufferImage = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		canvas = new Canvas(bufferImage);
		paint = new Paint();
		paint.setStyle(Paint.Style.FILL);
	}

	
	/**
	 * Paints canvas with bitmap
	 * @param g, the canvas
	 */
	public void paint(Canvas canvas) {
		if (bufferImage == null){
			bufferImage = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		}
		
		canvas.drawBitmap(bufferImage, 0, 0, null) ;
	}
	
	/**
	 * Initialize buffer image
	 */
	public static void initBufferImage() {
		bufferImage = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		canvas = new Canvas(bufferImage);
		
		if (null == bufferImage) {
			Log.v(TAG, "Creation of bufferImage failed.");
		}
		
	}
	
	/**
	 * Returns the canvas
	 * @return
	 */
	public static Canvas getBufferGraphics() {
		if (bufferImage == null)
			initBufferImage();
		
		return getGraphics(bufferImage);
	}
	
	/**
	 * gets the canvas.
	 * @return
	 */	
	public static Canvas getGraphics(Bitmap bufferImage){
		return canvas;
	}
	
	/**
	 * hex number
	 * @param color
	 */
	public void setColor(int color){
		if (color == 0) {
			Log.v("MazePanel", "Color is 0. Panic.");

		}
		
		paint.setColor(color);
	}
	
	/**
	 * String color
	 * @param color
	 */
	public void setColor(String color){
		if (color == null) {
			Log.v("MazePanel", "Color is 0. Panic.");

		}
	}
	
	/**
	 * Set the paint color
	 * @param color
	 */
	public void setColor(int[] color) {
		paint.setARGB(255, color[0], color[1], color[2]);	
	}

	/**
	 * Draw a rectangle
	 * @param i
	 * @param j
	 * @param viewWidth
	 * @param viewHeight
	 */
	public void fillRect(int i, int j, int viewWidth, int viewHeight) {
		Rect rectangle = new Rect(i, viewHeight + j, viewWidth + i, j);
		canvas.drawRect(rectangle, paint);
	}
	
	/**
	 * Draw a polygon 
	 * @param xps
	 * @param yps
	 */
	public void fillPolygon(int[] xps, int[] yps, int i) {
		Path path = new Path();
		path.reset(); 
		
		path.moveTo(xps[0], yps[0]);
		
		for (int j = 1; j < xps.length; j++) {
			path.lineTo(xps[j], yps[j]);
		}
		
		
		path.lineTo(xps[0], yps[0]); 
		
		
		canvas.drawPath(path, paint);
	}
	
	/** 
	 * Draw an oval
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	public void fillOval(int x, int y, int width, int height) {

		RectF rectf = new RectF(x - width, y - height, x + width, y + height);
		canvas.drawOval(rectf, paint);
	}
	
	/**
	 * Draws a line
	 * @param nx1
	 * @param ny1
	 * @param nx2
	 * @param ny12
	 */
	public void drawLine(int nx1, int ny1, int nx2, int ny12) {
		canvas.drawLine(nx1, ny1, nx2, ny12, paint);
	}
	
	/**
	 * @param canvas
	 * @param maze
	 */
	public void redrawFinish(Canvas canvas, Maze maze) {
		Log.v(TAG, "Redraw Finish.");
	}
	
	/**
	 * @param canvas
	 * @param maze
	 */
	public void redrawGenerating(Canvas canvas, Maze maze) throws Exception {
		Log.v(TAG, "Redraw Generating.");
	}
	
	/**
	 * @param canvas
	 * @param maze
	 */
	public void redrawTitle(Canvas canvas, Maze maze){
		Log.v(TAG, "Redraw Title.");
	}

	public void setShader(LinearGradient linearGradient) {
		paint.setShader(linearGradient);
	}

	public void setShader(BitmapShader fillBMPshader) {
		// TODO Auto-generated method stub
		paint.setShader(fillBMPshader);
	}

	public void clearShader(Object object) {
		// TODO Auto-generated method stub
		paint.setShader(null);
	}
}
