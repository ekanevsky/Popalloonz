package net.raggz.popalloonz;

import java.util.HashMap;
import java.util.Map;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;

public class Glyphs {

	private static final String TAG = Glyphs.class.getSimpleName();
	private Bitmap bitmap;
	
	private Map<Character, Bitmap> glyphs = new HashMap<Character, Bitmap>(10);
	
	private int width;
	private int height;
	
	private char[] numbers = new char[] { '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h',
			'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
	
	public int getWidth() {
		return width;
	}
	
	public Glyphs (Bitmap bitmap) {
		super();
		this.bitmap = bitmap;
		this.width = (bitmap.getWidth() / 36);
		this.height = bitmap.getHeight();
		
		for (int i = 0; i < 36; i++) {
			glyphs.put(numbers[i],  Bitmap.createBitmap(bitmap, 0 + (i * width), 0, width, height));
		}
		Log.d(TAG, "characters initialised");
	}
	
	public Bitmap getBitmap() {
		return bitmap;
	}
	
	public void drawString(Canvas canvas, String text, int x, int y) {
		if (canvas == null) {
			Log.d(TAG, "Canvas is null");
		}
		for (int i = 0; i < text.length(); i++) {
			Character ch = text.charAt(i);
			if (glyphs.get(ch) != null) {
				canvas.drawBitmap(glyphs.get(ch),  x + (i * width), y, null);
			}
		}
	}
	
	
	
}
