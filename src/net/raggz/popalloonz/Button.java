package net.raggz.popalloonz;


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;
//import android.view.MotionEvent;
//import android.graphics.BitmapFactory;

public class Button {
	
	private static final String TAG = MainGamePanel.class.getSimpleName();
	
	private int x;
	private int y;
	private boolean touched = false;
	private Bitmap bitmap;
	private boolean runOnce = true;
	
	public Button(int buttonX, int buttonY, Bitmap bitmap) {
		
		this.x = buttonX;
		this.y = buttonY;
		this.bitmap = bitmap;
		
	}
	
	public void setTouched(boolean touched) {
		this.touched = touched;
	}
	
	public int getY() {
		return this.y;
	}
	
	public void setY(int y) {
		this.y = y;
	}
	
	public boolean getTouched() {
		return touched;
	}
	
	public int getHeight() {
		return bitmap.getHeight();
	}
	
	public void draw(Canvas canvas) {
		canvas.drawBitmap(bitmap, x - (bitmap.getWidth() / 2), y - (bitmap.getHeight() / 2), null);
		if (runOnce) {
			Log.d(TAG, Integer.toString(bitmap.getWidth()));
			Log.d(TAG, Integer.toString(bitmap.getHeight()));
			Log.d(TAG, (x - (bitmap.getWidth() / 2)) + " " + (y - (bitmap.getHeight() / 2)));
			runOnce = false;
		}
	}
	
	public void handleActionDown(int eventX, int eventY) {
		if (eventX >= (x - (bitmap.getWidth() / 2 + 2)) && (eventX <= (x + (bitmap.getWidth() / 2 + 2)))) {
			if (eventY >= (y - (bitmap.getHeight() / 2 + 2)) && (eventY <= (y + (bitmap.getHeight() / 2 + 2)))) {
				Log.d(TAG, "Menu button hit!");
				setTouched(true);
			} else {
				setTouched(false);
			}
		} else {
			setTouched(false);
		}

	}
	

}
