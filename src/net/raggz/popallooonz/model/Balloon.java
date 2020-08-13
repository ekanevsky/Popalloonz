/**
 * 
 */
package net.raggz.popallooonz.model;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.MotionEvent;

/**
 * @author impaler
 *
 */
public class Balloon {

	private Bitmap bitmap;	// the actual bitmap
	private int x;			// the X coordinate
	private int y;			// the Y coordinate
	private boolean touched;	// if balloon is touched
	private int typeNumber;
	private int speed;

	public Balloon(Bitmap bitmap, int x, int y, int typeNumber, int speed) {
		this.bitmap = bitmap;
		this.x = x;
		this.y = y;
		this.typeNumber = typeNumber;
		this.speed = speed;
	}
	
	public int getTypeNumber() {
		return typeNumber;
	}
	
	public int getSpeed() {
		return speed;
	}
	
	public void setSpeed(int speed) {
		this.speed = speed;
	}
	
	public Bitmap getBitmap() {
		return bitmap;
	}
	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}

	public boolean isTouched() {
		return touched;
	}
	
	public int getHeight() {
		return bitmap.getHeight();
	}

	public void setTouched(boolean touched) {
		this.touched = touched;
	}
	
	public void draw(Canvas canvas) {
		if (typeNumber == -2) {
			canvas.drawBitmap(bitmap, x - (bitmap.getWidth() / 2), y, null);
		} else {
			canvas.drawBitmap(bitmap, x - (bitmap.getWidth() / 2), y - (bitmap.getHeight() / 2), null);
		}
	}

	/**
	 * Handles the {@link MotionEvent.ACTION_DOWN} event. If the event happens on the 
	 * bitmap surface then the touched state is set to <code>true</code> otherwise to <code>false</code>
	 * @param eventX - the event's X coordinate
	 * @param eventY - the event's Y coordinate
	 */
	public void handleActionDown(int eventX, int eventY) {
		if (eventX >= (x - (bitmap.getWidth() / 2 + 2)) && (eventX <= (x + (bitmap.getWidth() / 2 + 2)))) {
			if (eventY >= (y - (bitmap.getHeight() / 2 + 2)) && (eventY <= (y + (bitmap.getHeight() / 2 + 2)) && (eventY > 44))) {
				// balloon touched
				setTouched(true);
			} else {
				setTouched(false);
			}
		} else {
			setTouched(false);
		}

	}
	
	//just a little clean up
	public void cleanUp() {
		bitmap = null;	// the actual bitmap
		x = 0;			// the X coordinate
		y = 0;			// the Y coordinate
		touched = false;	// if balloon is touched
		typeNumber = 0;
	}
}
