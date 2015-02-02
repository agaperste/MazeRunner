package edu.wm.cs.cs301.amazebyjackiebethany.falstad;

import android.util.Log;
import edu.wm.cs.cs301.amazebyjackiebethany.falstad.Constants;
import edu.wm.cs.cs301.amazebyjackiebethany.falstad.DefaultViewer;
import edu.wm.cs.cs301.amazebyjackiebethany.falstad.Maze;
import edu.wm.cs.cs301.amazebyjackiebethany.falstad.MazePanel;
import edu.wm.cs.cs301.amazebyjackiebethany.falstad.RangeSet;

public class MazeView extends DefaultViewer {

	Maze maze ;
	MazePanel panel ;
	
	public MazeView(Maze m) {
		super() ;
		maze = m;
		panel = maze.panel;
	}

	@Override
	public void redraw(MazePanel mazePanel, int state, int px, int py, int view_dx,
			int view_dy, int walk_step, int view_offset, RangeSet rset, int ang) {
		
		switch (state) {
		case Constants.STATE_TITLE:
			redrawTitle(panel);
			break;
		case Constants.STATE_GENERATING:
			try {
				redrawGenerating(panel);
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case Constants.STATE_PLAY:
			break;
		case Constants.STATE_FINISH:
			redrawFinish(panel);
			break;
		}
	}
	
	private void dbg(String str) {
		System.out.println("MazeView:" + str);
	}
	
	// 
	
	/**
	 * redraw to draw the title screen, screen is hardcoded
	 * @param  gc graphics is the off screen image
	 */
	void redrawTitle(MazePanel mazePanel) {
		mazePanel.redrawTitle(mazePanel.getBufferGraphics(), maze);
	}
	/**
	 * redraw to draw final screen, screen is hard coded
	 * @param gc graphics is the off screen image
	 */
	void redrawFinish(MazePanel mazePanel) {
		mazePanel.redrawFinish(mazePanel.getBufferGraphics(), maze);
	}

	/**
	 * redraw to draw screen during phase of maze generation, screen is hard coded
	 * only attribute percentdone is dynamic
	 * @param gc graphics is the off screen image
	 * @throws Exception 
	 */
	void redrawGenerating(MazePanel mazepanel) throws Exception {
		mazepanel.redrawGenerating(mazepanel.getBufferGraphics(), maze);
		if (mazepanel.getBufferGraphics() != null) {
			Log.v("MazeView", "Buffer isn't null");
		}
		else {
			Log.v("MazeView", "Buffer's totally null.");
		}
	}



}
