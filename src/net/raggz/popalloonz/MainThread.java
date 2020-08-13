/**
 * 
 */
package net.raggz.popalloonz;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.util.Log;
import android.view.SurfaceHolder;
import java.lang.System;



/**
 * @author Elysia Kanevsky
 *
 * The Main thread which contains the game loop. The thread must have access to 
 * the surface view and holder to trigger events every game tick.
 */
@SuppressLint("WrongCall")
public class MainThread extends Thread {
	
	private static final String TAG = MainThread.class.getSimpleName();

	// Surface holder that can access the physical surface
	private SurfaceHolder surfaceHolder;
	// The actual view that handles inputs
	// and draws to the surface
	private MainGamePanel gamePanel;
	private long nextBalloon;
	private int index = 0;
	private int numBalloons = 0;
	private long newTarget = 0;
	private long holdTime = 0;
	private boolean paused = false;
	private int timePassed = 0;
	private long tick = System.currentTimeMillis() + 1000;
	private long splash = System.currentTimeMillis() + 3000;
	private boolean opening = true;
	private boolean createNew = false;
	private int gameTime = 31;
	private boolean newButtonPress = false;
	private boolean startTouched = false;
	private int decreasing = 1500;
	private boolean runOnce = true;
	private boolean endTouched = false;
	
	public void setEndTouched (boolean endTouched) {
		this.endTouched = endTouched;
	}

	// flag to hold game state 
	private boolean running;
	public void setRunning(boolean running) {
		this.running = running;
	}
	
	public void setRunOnce(boolean runOnce) {
		this.runOnce = runOnce;
	}
	
	public void setTimePassed (int timePassed) {
		this.timePassed = timePassed;
	}
	
	public void setStartTouched(boolean startTouched) {
		this.startTouched = startTouched;
	}
	
	public void setNewPress(boolean newButtonPress) {
		this.newButtonPress = newButtonPress;
	}

	public MainThread(SurfaceHolder surfaceHolder, MainGamePanel gamePanel) {
		super();
		this.surfaceHolder = surfaceHolder;
		this.gamePanel = gamePanel;
		nextBalloon = System.currentTimeMillis() + 1500;
		newTarget = System.currentTimeMillis() + 10000;
	}
	
	public int getGameTime() {
		return gameTime;
	}
	
	public void setGameTime(int gameTime) {
		this.gameTime = gameTime;
	}
	
	public void pause(boolean paused) {
		this.paused = paused;
	}
	
	public void timer() {
			
		if (tick <= System.currentTimeMillis()) {
			timePassed++;
			tick = System.currentTimeMillis() + 1000;
		}
		
	}
	
	public void setCreateNew(boolean createNew) {
		this.createNew = createNew;
	}
	
	public int getTimePassed() {
		return timePassed;
	}

	public void setNewTarget() {
		newTarget = newTarget + 10000;
	}
	
	public void setOpening(boolean opening) {
		this.opening = opening;
	}
	@Override
	public void run() {
		Canvas canvas;
		Log.d(TAG, "Starting game loop");
		while (running) {
			
			if (this.gamePanel.getMissedBalloons() >= 15 && runOnce == true) {
				//Log.d(TAG, "timePassed: " + Integer.toString(timePassed));
				this.gamePanel.setGameOver(true);
				timePassed = 30;
				//Log.d(TAG, "timePassed: " + Integer.toString(timePassed));
				//Log.d(TAG, "gameTime: " + Integer.toString(gameTime));
				runOnce = false;
			}
			
			canvas = null;
			// try locking the canvas for exclusive pixel editing
			// in the surface
			
			if (opening) {
				try {
					canvas = this.surfaceHolder.lockCanvas();
					
					
					numBalloons = this.gamePanel.getNumBalloons();
					while (index <= numBalloons) {
						
						if (this.gamePanel.checkBounds(index)) {
							this.gamePanel.balloonPopped(index);
							numBalloons = this.gamePanel.getNumBalloons();
						
						} else {
							index++;
						}
					}
					index = 0;
					
					
					if (this.gamePanel.getCreditRun()) {
						if (this.gamePanel.checkCreditBounds()) {
							this.gamePanel.resetCredits();
						}
					}
					
					synchronized (surfaceHolder) {
						
						if (true == startTouched) {
							this.gamePanel.newGame();
							startTouched = false;
							this.gamePanel.setStartTouched(false);
						}
						
						if (System.currentTimeMillis() >= nextBalloon) {
							this.gamePanel.newBalloon();
							nextBalloon = System.currentTimeMillis() + 1500;
						}
						
						if (this.gamePanel.getCreditRun()) {
							this.gamePanel.updateCredits();
						}
						
						this.gamePanel.updateBalloons();
						this.gamePanel.onDraw(canvas);
						
					}
				} finally {
					if (canvas != null) {
						surfaceHolder.unlockCanvasAndPost(canvas);
					}
				}
				if (splash < System.currentTimeMillis()) {
					this.gamePanel.setShowSplash(false);
				}
			} else if (paused) { //updates surface without updating game state
				try {
					canvas = this.surfaceHolder.lockCanvas();
					
					synchronized (surfaceHolder) {
						
						if (true == newButtonPress) {
							this.gamePanel.newGame();
						}
						
						if (true == endTouched) {
							this.gamePanel.gameOver();
						}
						
						this.gamePanel.onDraw(canvas);
						
					}
				} finally {
					if (canvas != null) {
						surfaceHolder.unlockCanvasAndPost(canvas);
					}
				}
				
			} else if (!paused && timePassed < gameTime){ //updates surface and game state
				timer();
				this.gamePanel.updateTimeLeft(gameTime, timePassed);
				try {
					if (startTouched) {
						this.gamePanel.newGame();
						startTouched = false;
					}
					canvas = this.surfaceHolder.lockCanvas();
				
					//checks if a balloon is more than 50 pixels outside of the top of the screen
					//if so, removes the balloon
					numBalloons = this.gamePanel.getNumBalloons();
					while (index <= numBalloons) {
						
						if (this.gamePanel.checkBounds(index)) {
							if (this.gamePanel.getGameType() == 1) {
								if (this.gamePanel.checkType(index)) { 
									this.gamePanel.loseCriteria(true);
								}
							}
							this.gamePanel.balloonPopped(index);
							numBalloons = this.gamePanel.getNumBalloons();
						
						} else {
							index++;
						}
					}
					index = 0;
								
					synchronized (surfaceHolder) {
					
						//creates new balloons every 1 seconds
						if (System.currentTimeMillis() >= nextBalloon) {
							this.gamePanel.newBalloon();
							if (this.gamePanel.getGameType() == 0) {
								nextBalloon = System.currentTimeMillis() + 1000;
							} else if (this.gamePanel.getGameType() == 1) {
								nextBalloon = System.currentTimeMillis() + decreasing;
								if (decreasing > 750) {
									decreasing = decreasing - 60;
								}
								
							}
						}
						//changes the target balloons for 10 seconds
						if (System.currentTimeMillis() >= newTarget || createNew) {
							createNew = false;
							
							this.gamePanel.setTarget();
							//displays the new target bitmap
							this.gamePanel.setNTExist(true);
							newTarget = System.currentTimeMillis() + 10000;
							holdTime = System.currentTimeMillis() + 2000;
						}
						//keeps the new target bitmap displayed until 2 seconds is up
						if (System.currentTimeMillis() >= holdTime) {
							this.gamePanel.setNTExist(false);
							if (this.gamePanel.getRunOnce()) {
								this.gamePanel.setRunOnce(false);
							}
						}
						// update game state 
						// render state to the screen
						// draws the canvas on the panel
					
						this.gamePanel.updateBalloons();
						this.gamePanel.onDraw(canvas);	
						if (this.gamePanel.getPopIndex() >= 0) {
							this.gamePanel.balloonPopped(this.gamePanel.getPopIndex());
							this.gamePanel.setPopIndex(-1);
						}
					}
				} finally {
					// in case of an exception the surface is not left in 
					// an inconsistent state
					if (canvas != null) {
						surfaceHolder.unlockCanvasAndPost(canvas);
					}
				}	// end finally
			} else {
				timer();
				paused = false;
				try {
					canvas = this.surfaceHolder.lockCanvas();
					
					numBalloons = this.gamePanel.getNumBalloons();
					while (index <= numBalloons) {
						
						if (this.gamePanel.checkBounds(index)) {
							this.gamePanel.balloonPopped(index);
							numBalloons = this.gamePanel.getNumBalloons();
							
						} else {
							index++;
						}
					}
					index = 0;
					
					synchronized (surfaceHolder) {
						this.gamePanel.updateBalloons();
						this.gamePanel.onDraw(canvas);
						
					}
				} finally {
					if (canvas != null) {
						surfaceHolder.unlockCanvasAndPost(canvas);
					}
				}
				if (timePassed > gameTime + 5) {
					timePassed = 0;
					decreasing = 1500;
					if (this.gamePanel.getMissedBalloons() >= 15) {
						//Log.d(TAG, "Game Over");
						this.gamePanel.setGameOver(true);
						//Log.d(TAG, "gameOver = " + Boolean.toString(this.gamePanel.getGameOver()));
						this.gamePanel.gameOver();
						runOnce = true;
						this.gamePanel.setMissedBalloons(0);
					} else {
						this.gamePanel.newGame(); 
					}
				}
				
			}
		}
	}
	
}
