/**
 * 
 */
package net.raggz.popalloonz;

import com.webs.kanevskyproductions.R;
import net.raggz.popallooonz.model.Balloon;
import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.util.DisplayMetrics;

//import java.io.File;
import java.util.ArrayList;
import java.util.Random;
import android.graphics.Bitmap;
import android.media.MediaPlayer;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.File;
//import android.widget.ImageView;

/**
 * @author Elysia Kanevsky
 * Main panel for the Popalloonz game.
 * Extends a framework built by Obviam
 * http://www.javacodegeeks.com/2011/06/android-game-development-tutorials.html
 * 
 */
public class MainGamePanel extends SurfaceView implements
		SurfaceHolder.Callback {

	private static final String TAG = MainGamePanel.class.getSimpleName();
	
	private MainThread thread;
	private int xDrop;
	private ArrayList<Droid> balloons;
	private int objectIndex = 0;
	private DisplayMetrics metrics;
	private Random randomGenerator;
	private int width;
	private int height;
	private int numBalloons;
	private ArrayList<Droid> balloonPop;
	private Droid credits;
	private boolean paused = false;
	private int speed;
	private Bitmap currentTarget;
	private int targetType = 0;
	private int score = 0;
	private boolean ntExist = false;
	private Button menuBar;
	private Button exitButton;
	private Button endButton;
	private Button newButton;
	private Button startButton;
	private Button exitTitleButton;
	private Button creditButton;
	private Button paperButton;
	private Paint paint = new Paint();
	private Bitmap[][] balloonPic;
	private Glyphs glyphs;
	private MediaPlayer mp;
	private MediaPlayer bgMusic;
	private Context context;
	private boolean opening = true;
	private int alphaRedux = 255;
	private int gameType = 0;
	private boolean runOnce = true;
	private int timeLeft = 0;
	private int prevType = 4;
	private int balloonSize = 0;
	private boolean newButtonPress = false;
	private boolean showSplash = true;
	private boolean startTouched = false;
	private int missedBalloons = 0;
	private boolean gameOver = false;
	private FileOutputStream fileOut;
	private FileInputStream fileIn;
	private File exists;
	private String scoreString;
	private int popIndex = -1;
	private boolean creditRun = false;
	private boolean paper = false;
	private boolean paperTouch = false;
	
	
	//constant bitmap declarations
	private final Bitmap MENUBUTTON = BitmapFactory.decodeResource(getResources(), R.drawable.menu_button);
	private final Bitmap EXITBUTTON = BitmapFactory.decodeResource(getResources(), R.drawable.exit_button);
	private final Bitmap NEWBUTTON = BitmapFactory.decodeResource(getResources(), R.drawable.new_button);
	private final Bitmap ENDBUTTON = BitmapFactory.decodeResource(getResources(), R.drawable.end_button);
	private final Bitmap NEW_TARGET = BitmapFactory.decodeResource(getResources(), R.drawable.new_target);
	private final Bitmap ARROW = BitmapFactory.decodeResource(getResources(), R.drawable.arrow);
	private final Bitmap POP1 = BitmapFactory.decodeResource(getResources(), R.drawable.pop1);
	private final Bitmap POP2 = BitmapFactory.decodeResource(getResources(), R.drawable.pop2);
	private Bitmap MENUBAR = BitmapFactory.decodeResource(getResources(), R.drawable.top_bar);
	private Bitmap PAUSE_MENU = BitmapFactory.decodeResource(getResources(), R.drawable.pause_menu);
	private final Bitmap TIME_UP = BitmapFactory.decodeResource(getResources(), R.drawable.time_up);
	private final Bitmap POP_ALL = BitmapFactory.decodeResource(getResources(), R.drawable.pop_all);
	private final Bitmap START_BUTTON = BitmapFactory.decodeResource(getResources(), R.drawable.start_game);
	private final Bitmap START_BUTTON1 = BitmapFactory.decodeResource(getResources(), R.drawable.start_game1);
	private final Bitmap TITLE_BAR = BitmapFactory.decodeResource(getResources(), R.drawable.title_bar);
	private final Bitmap TITLE_BAR1 = BitmapFactory.decodeResource(getResources(), R.drawable.title_bar1);
	private final Bitmap GAME_OVER = BitmapFactory.decodeResource(getResources(), R.drawable.game_over);
	private final Bitmap CREDIT_BUTTON = BitmapFactory.decodeResource(getResources(), R.drawable.credit_button);
	private final Bitmap EXIT_MAIN = BitmapFactory.decodeResource(getResources(), R.drawable.exit_main);
	private final Bitmap BLUE_NUMBER = BitmapFactory.decodeResource(getResources(), R.drawable.blue_numbers);
	private final Bitmap BLUE_NUMBER1 = BitmapFactory.decodeResource(getResources(), R.drawable.blue_numbers1);
	private final Bitmap PAPER = BitmapFactory.decodeResource(getResources(), R.drawable.paper_main);
	private Bitmap background;
	private Bitmap splash;
	private Bitmap title_bar;
	private Bitmap exit_main;
	private Bitmap credit_button;
	private Bitmap creds;
	private Bitmap paper_button;
	
	public MainGamePanel(Context context) {
		super(context);
		this.context = context;
		// adding the callback (this) to the surface holder to intercept events
		getHolder().addCallback(this);
		glyphs = new Glyphs(BLUE_NUMBER);
		scoreString = Integer.toString(score);
		
		exists = getContext().getFileStreamPath("score");
		if (exists.exists() == false) {
			//Log.d(TAG, "creating a new file");
			try {
				fileOut = context.openFileOutput("score", Context.MODE_PRIVATE);
				fileOut.write(scoreString.getBytes());
				fileOut.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			fileIn = context.openFileInput("score");
			
			int ch;
			StringBuffer fileContent = new StringBuffer("");
			
			while( (ch = fileIn.read()) != -1)
			  fileContent.append((char)ch);
			scoreString = new String(fileContent);
			fileIn.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//Log.d(TAG, "Opening high score: " + scoreString);
		//create a random number generator
		randomGenerator = new Random();
		
		//creates the menu button
		menuBar = new Button((MENUBUTTON.getWidth() / 2) + 2, (MENUBUTTON.getHeight() / 2) + 2, MENUBUTTON);
		
		//get window width and height
		
		metrics = context.getResources().getDisplayMetrics();
		width = metrics.widthPixels;
		height = metrics.heightPixels - 75;
		
		
		splash = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.kp_splash), width, height, true);
		
		if (TITLE_BAR.getWidth() > width) {
			title_bar = Bitmap.createScaledBitmap(TITLE_BAR, width, height / 3, true);
		} else {
			title_bar = TITLE_BAR;
		}
		
		exit_main = Bitmap.createScaledBitmap(EXIT_MAIN, width / 2, EXIT_MAIN.getHeight(), true);
		credit_button = Bitmap.createScaledBitmap(CREDIT_BUTTON, width / 2, CREDIT_BUTTON.getHeight(), true);
		paper_button = Bitmap.createScaledBitmap(PAPER, width / 2, PAPER.getHeight(), true);
		
		exitTitleButton = new Button(((width / 4) * 3), height - (exit_main.getHeight() / 2), exit_main);
		creditButton = new Button((width / 4), height - (credit_button.getHeight() / 2), credit_button);
		paperButton = new Button((width / 2), height - 3 * (paper_button.getHeight() / 2), paper_button);
		//creates menu button objects
		exitButton = new Button((width / 2 ) , (height / 2) + (EXITBUTTON.getHeight() + 15), EXITBUTTON);
		newButton = new Button((width / 2 ) , (height / 2) - (NEWBUTTON.getHeight() + 15), NEWBUTTON);
		endButton = new Button((width / 2), (height / 2), ENDBUTTON);
		startButton = new Button((width / 2), (height / 2), START_BUTTON);
		
		
		balloonPic = new Bitmap[4][3];
		balloonPopulate();
		
		
		background = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.clouds), width, height, true);
		creds = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.whole_creds), width, (int) (height * 1.756), true);
		
		credits = new Droid(creds, width / 2, height, -2, 2);
		
		//used for displaying pop animation
		balloonPop = new ArrayList<Droid>(0);
		
		//create an array of droid objects
		balloons = new ArrayList<Droid>();
		balloons.add(new Droid(BitmapFactory.decodeResource(getResources(), R.drawable.balloon_blue), width/2, height, 0, 5));
		numBalloons = 0;
		setTarget();
		//plays background music
		bgMusic = MediaPlayer.create(context,  R.raw.drops);
		bgMusic.setLooping(true);
		bgMusic.start();
		// create the game loop thread
		thread = new MainThread(getHolder(), this);
		
		// make the GamePanel focusable so it can handle events
		setFocusable(true);
	}
	
	public void setGameOver (boolean gameOver) {
		this.gameOver = gameOver;
	}
	
	public boolean getGameOver () {
		return gameOver;
	}
	
	public void setMissedBalloons (int missedBalloons) {
		this.missedBalloons = missedBalloons;
	}
	
	public void setStartTouched (boolean startTouched) {
		this.startTouched = startTouched;
	}
	
	public int getMissedBalloons() {
		return missedBalloons;
	}
	
	public void gameOver() {
		thread.setEndTouched(false);
		paused = false;
		thread.pause(paused);
		startButton.setY(height / 2);
		numBalloons = 0;
		balloons.clear();
		balloons.add(new Droid(BitmapFactory.decodeResource(getResources(), R.drawable.balloon_yellow_two), width/2, height, -1, 3));
		opening = true;
		//Log.d(TAG, "Current high score " + scoreString);
		if (score > Integer.parseInt(scoreString)) {
			//Log.d(TAG, "new high score");
			scoreString = new String(Integer.toString(score));
		}
		try {
			fileOut = context.openFileOutput("score", Context.MODE_PRIVATE);
			fileOut.write(scoreString.getBytes());
			fileOut.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		score = 0;
		thread.setOpening(true);
		missedBalloons = 0;
		bgMusic.release();
		bgMusic = MediaPlayer.create(context,  R.raw.drops);
		bgMusic.setLooping(true);
		bgMusic.start();
	}
	
	public void pauseThread(boolean paused) {
		//pauses the game
		thread.pause(paused);
		//Log.d(TAG, width + " " + height);
	}
	
	public void setPause(boolean paused) {
		this.paused = paused;
	}
	
	public void balloonPopulate() {
		//populates the balloons, 3 different sizes each
		
		//yellow balloons
		balloonPic[0][0] = BitmapFactory.decodeResource(getResources(), R.drawable.balloon_yellow);
		balloonPic[0][1] = BitmapFactory.decodeResource(getResources(), R.drawable.balloon_yellow_one);
		balloonPic[0][2] = BitmapFactory.decodeResource(getResources(), R.drawable.balloon_yellow_two);
		//red balloons
		balloonPic[1][0] = BitmapFactory.decodeResource(getResources(), R.drawable.balloon_red);
		balloonPic[1][1] = BitmapFactory.decodeResource(getResources(), R.drawable.balloon_red_one);
		balloonPic[1][2] = BitmapFactory.decodeResource(getResources(), R.drawable.balloon_red_two);
		//green balloons
		balloonPic[2][0] = BitmapFactory.decodeResource(getResources(), R.drawable.balloon_green);
		balloonPic[2][1] = BitmapFactory.decodeResource(getResources(), R.drawable.balloon_green_one);
		balloonPic[2][2] = BitmapFactory.decodeResource(getResources(), R.drawable.balloon_green_two);
		//blue balloons
		balloonPic[3][0] = BitmapFactory.decodeResource(getResources(), R.drawable.balloon_blue);
		balloonPic[3][1] = BitmapFactory.decodeResource(getResources(), R.drawable.balloon_blue_one);
		balloonPic[3][2] = BitmapFactory.decodeResource(getResources(), R.drawable.balloon_blue_two);
		
	}
	
	public void balloonPopulatePaper() {
		//populates the balloons, 3 different sizes each using the paper art workx
		
		//yellow balloons
		balloonPic[0][0] = BitmapFactory.decodeResource(getResources(), R.drawable.balloon_yellow1);
		balloonPic[0][1] = BitmapFactory.decodeResource(getResources(), R.drawable.balloon_yellow_one1);
		balloonPic[0][2] = BitmapFactory.decodeResource(getResources(), R.drawable.balloon_yellow_two1);
		//red balloons
		balloonPic[1][0] = BitmapFactory.decodeResource(getResources(), R.drawable.balloon_red1);
		balloonPic[1][1] = BitmapFactory.decodeResource(getResources(), R.drawable.balloon_red_one1);
		balloonPic[1][2] = BitmapFactory.decodeResource(getResources(), R.drawable.balloon_red_two1);
		//green balloons
		balloonPic[2][0] = BitmapFactory.decodeResource(getResources(), R.drawable.balloon_green1);
		balloonPic[2][1] = BitmapFactory.decodeResource(getResources(), R.drawable.balloon_green_one1);
		balloonPic[2][2] = BitmapFactory.decodeResource(getResources(), R.drawable.balloon_green_two1);
		//blue balloons
		balloonPic[3][0] = BitmapFactory.decodeResource(getResources(), R.drawable.balloon_blue1);
		balloonPic[3][1] = BitmapFactory.decodeResource(getResources(), R.drawable.balloon_blue_one1);
		balloonPic[3][2] = BitmapFactory.decodeResource(getResources(), R.drawable.balloon_blue_two1);
		
	}
	
	public void soundPopulate() {
		
	}
	
	public int getGameType() {
		return gameType;
	}
	
	public void setShowSplash(boolean showSplash) {
		this.showSplash = showSplash;
	}
	
	@SuppressWarnings({ "static-access"})
	public void playSound() {
		//plays popping sounds
		
		int rand = randomGenerator.nextInt(8);
		
		
		switch (rand) {
		case 0: mp = MediaPlayer.create(context,  R.raw.pop_one);break;
		case 1: mp = MediaPlayer.create(context,  R.raw.pop_two);break;
		case 2: mp = MediaPlayer.create(context,  R.raw.pop_three);break;
		case 3: mp = MediaPlayer.create(context,  R.raw.pop_four);break;
		case 4: mp = MediaPlayer.create(context,  R.raw.pop_five);break;
		case 5: mp = MediaPlayer.create(context,  R.raw.pop_six);break;
		case 6: mp = MediaPlayer.create(context,  R.raw.pop_seven);break;
		case 7: mp = MediaPlayer.create(context,  R.raw.pop_eight);break;
		}
		
		
		mp.start();
		Log.d(TAG, "Playing a sound");
		//sleeps the thread long enough for sound to play then releases the mediaplayer resources
		try {
			Thread.currentThread().sleep(100);
		} catch (InterruptedException e) {
			
			e.printStackTrace();
		}
		mp.release();
		
		
	}
	
	//clears all objects and score and creates a new game
	public void newGame() {
		this.gameOver = false;
		numBalloons = 0;
		balloons.clear();
		balloons.add(new Droid(BitmapFactory.decodeResource(getResources(), R.drawable.new_game), width/2, height, -1, 5));
		changeGameType();
		thread.setCreateNew(true);
		thread.setGameTime(31);
		thread.setTimePassed(0);
		runOnce = true;
		paused = false;
		thread.pause(paused);
		newButtonPress = false;
		thread.setNewPress(newButtonPress);
		thread.setRunOnce(true);
		resetCredits();
		
		
		
	}
	
	
	
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		
	}
	
	
	//changes game types
	public void changeGameType() {
		if (gameType == 0) {
			gameType = 1;
		} else if (gameType == 1) {
			gameType = 0;
		}
	}
	
	
	public int getNumBalloons() {
		return numBalloons;
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// at this point the surface is created and
		// we can safely start the game loop
		thread.setRunning(true);
		thread.start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.d(TAG, "Surface is being destroyed");
		// tell the thread to shut down and wait for it to finish
		// this is a clean shutdown
		boolean retry = true;
		while (retry) {
			try {
				thread.setRunning(false);
				((Activity)getContext()).finish();
				bgMusic.release();
				retry = false;
			} finally {
				//Log.d(TAG, "Something?");
			}
		}
		
	}
	//sets the new target exists variable
	public void setNTExist(boolean ntExist) {
		this.ntExist = ntExist;
	}
	
	//sets the target balloon
	public void setTarget() {
		if (gameType == 0) {
			int randSetter = randomGenerator.nextInt(8);
			while (prevType == randSetter) {
				randSetter = randomGenerator.nextInt(8);
			}
			if (paper == true) {
				switch (randSetter) {
				case 0: currentTarget = BitmapFactory.decodeResource(getResources(), R.drawable.balloon_yellow_pop1);
				targetType = 0;
				break;
				case 1: currentTarget = BitmapFactory.decodeResource(getResources(), R.drawable.balloon_red_pop1);
				targetType = 1;
				break;
				case 2: currentTarget = BitmapFactory.decodeResource(getResources(), R.drawable.balloon_green_pop1);
				targetType = 2;
				break;
				case 3: currentTarget = BitmapFactory.decodeResource(getResources(), R.drawable.balloon_blue_pop1);
				targetType = 3;
				break;
				case 4: currentTarget = BitmapFactory.decodeResource(getResources(), R.drawable.balloon_yellow_wrong1);
				targetType = 4;
				break;
				case 5: currentTarget = BitmapFactory.decodeResource(getResources(), R.drawable.balloon_red_wrong1);
				targetType = 5;
				break;
				case 6: currentTarget = BitmapFactory.decodeResource(getResources(), R.drawable.balloon_green_wrong1);
				targetType = 6;
				break;
				case 7: currentTarget = BitmapFactory.decodeResource(getResources(), R.drawable.balloon_blue_wrong1);
				targetType = 7;
				break;
				}
				
			} else {
				switch (randSetter) {
				case 0: currentTarget = BitmapFactory.decodeResource(getResources(), R.drawable.balloon_yellow_pop);
				targetType = 0;
				break;
				case 1: currentTarget = BitmapFactory.decodeResource(getResources(), R.drawable.balloon_red_pop);
				targetType = 1;
				break;
				case 2: currentTarget = BitmapFactory.decodeResource(getResources(), R.drawable.balloon_green_pop);
				targetType = 2;
				break;
				case 3: currentTarget = BitmapFactory.decodeResource(getResources(), R.drawable.balloon_blue_pop);
				targetType = 3;
				break;
				case 4: currentTarget = BitmapFactory.decodeResource(getResources(), R.drawable.balloon_yellow_wrong);
				targetType = 4;
				break;
				case 5: currentTarget = BitmapFactory.decodeResource(getResources(), R.drawable.balloon_red_wrong);
				targetType = 5;
				break;
				case 6: currentTarget = BitmapFactory.decodeResource(getResources(), R.drawable.balloon_green_wrong);
				targetType = 6;
				break;
				case 7: currentTarget = BitmapFactory.decodeResource(getResources(), R.drawable.balloon_blue_wrong);
				targetType = 7;
				break;
				}
			}
			prevType = randSetter;
		
			ntExist = true;
		} else {
			currentTarget = BitmapFactory.decodeResource(getResources(), R.drawable.all_pop);
		}
		balloonSize = currentTarget.getWidth();
		
	}
	
	//creates new balloons
	public void newBalloon() {
		
		//Log.d(TAG, "Creating a new balloon");
		int numNew = 0;
		if (gameType == 0) {
			numNew = randomGenerator.nextInt(4) + 1;
		} else if (gameType == 1) {
			numNew = randomGenerator.nextInt(3) + 1;
		}
		int counter = 0;
		
		//create up to 3 new balloons
		while(counter <= numNew) {
		//generates a random balloon type
		
			speed = randomGenerator.nextInt(4);
			while (speed == 0) {
				speed = randomGenerator.nextInt(4);
			}
			int typeNumber = randomGenerator.nextInt(4);
			
			int xLoc = randomGenerator.nextInt(width);
			while (xLoc >= width - 49 && xLoc <= 25) {
				xLoc = randomGenerator.nextInt(width);
			}
			//creates a new balloon based on the color chosen
			//yellow = 0, red = 1, green = 2, blue = 3
			switch (typeNumber) {
				case 0: balloons.add(new Droid(balloonPic[typeNumber][speed - 1], xLoc, height, 0, speed));
				numBalloons++;
				break;
				case 1: balloons.add(new Droid(balloonPic[typeNumber][speed - 1], xLoc, height, 1, speed));
				numBalloons++;
				break;
				case 2: balloons.add(new Droid(balloonPic[typeNumber][speed - 1], xLoc, height, 2, speed));
				numBalloons++;
				break;
				case 3: balloons.add(new Droid(balloonPic[typeNumber][speed - 1], xLoc, height, 3, speed));
				numBalloons++;
				break;
				default: break;
			}
			counter++;
		}
		
	}
	
	public void gameMusic() {
		bgMusic = MediaPlayer.create(context,  R.raw.ditto);
		bgMusic.setLooping(true);
		bgMusic.start();
	}
	
	public int getPopIndex() {
		return popIndex;
	}
	
	public void setPopIndex(int popIndex) {
		this.popIndex = popIndex;
	}
	

	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			objectIndex = 0;
			Log.d(TAG, "Coords: x=" + event.getX() + ",y=" + event.getY());
			
			menuBar.handleActionDown((int)event.getX(), (int)event.getY());
			if (menuBar.getTouched()){
				//Log.d(TAG, Boolean.toString(paused));
				paused = !paused;//toggles paused/unpaused
				pauseThread(paused);//if menu was touched, turns on a loop in the thread, effectively pausing it without killing it
				//Log.d(TAG, Boolean.toString(paused));
				
				
				
				
				
			}
			
			
			if (paused == false && thread.getTimePassed() < thread.getGameTime() && false == opening) {
				
				objectIndex = numBalloons;
				
			// delegating event handling to the balloon
				while (0 <= objectIndex) {
					balloons.get(objectIndex).handleActionDown((int)event.getX(), (int)event.getY());	
				
					if (balloons.get(objectIndex).isTouched()) {
						//displays a pop animation
						popIndex = objectIndex;
						balloonPop.add(new Droid(POP1, balloons.get(objectIndex).getX(), balloons.get(objectIndex).getY(), 0, 0));
						//if the target balloon is popped, adds 10
						if (targetType <= 3 && balloons.get(objectIndex).getTypeNumber() == targetType && gameType == 0 && balloons.get(objectIndex).getTypeNumber() > -1) {
							score = score + 10;
							//int gameTime = thread.getGameTime();
							//thread.setGameTime(gameTime++);
						} else if (targetType <= 3 && gameType == 0 && balloons.get(objectIndex).getTypeNumber() > -1){
							//if wrong balloon is popped, takes off 5 points only if it won't go below zero
							if (score >= 5) {
								score = score - 5;
							}
							this.missedBalloons++;
							//Log.d(TAG, "Missed " + Integer.toString(missedBalloons) + " balloons - isTouched1");
						} else if (gameType == 1 && balloons.get(objectIndex).getTypeNumber() > -1) {
							//all balloons are the target in game type 1
							score = score + 10;
						} else if (targetType > 3 && balloons.get(objectIndex).getTypeNumber() != (targetType - 4) && gameType == 0 && balloons.get(objectIndex).getTypeNumber() > -1) {
							score = score + 10;
						} else if (targetType > 3 && gameType == 0 && balloons.get(objectIndex).getTypeNumber() > -1) {
							if (score >= 5) {
								score = score - 5;
							}
							this.missedBalloons++;
							//Log.d(TAG, "Missed " + Integer.toString(missedBalloons) + " balloons - isTouched2");
						}
					
						//balloonPopped(objectIndex);
						objectIndex = 0;
						playSound();
						
						break;
					}
				
					objectIndex--;
				}
				objectIndex = 0;
			
				while (objectIndex <= numBalloons) {
					balloons.get(objectIndex).setTouched(false);
					objectIndex++;
				}
				objectIndex = 0;
			
			}//end Paused if
			
			if (paused) {
				
				//checks if a menu button has been touched
				newButton.handleActionDown((int)event.getX(), (int)event.getY());
				exitButton.handleActionDown((int)event.getX(), (int)event.getY());
				endButton.handleActionDown((int)event.getX(), (int)event.getY());
				
				if (exitButton.getTouched()) {
					//closes the thread and ends the context
					thread.setRunning(false);
					((Activity)getContext()).finish();
				}
				
				if (newButton.getTouched()) {
					//resets the score and starts a new game
					score = 0;
					newButtonPress = true;
					thread.setNewPress(newButtonPress);
					this.missedBalloons = 0;
					
				}
				
				if (endButton.getTouched()) {
					thread.setEndTouched(true);
				}
				
			}
			
			if (opening) {
				startButton.handleActionDown((int)event.getX(), (int)event.getY());
				if (false == startTouched) {
					if (startButton.getTouched()) {
						Log.d(TAG, "start button touched");
						startTouched = true;
						
					}
				}
				
				exitTitleButton.handleActionDown((int)event.getX(), (int)event.getY());
				if (exitTitleButton.getTouched()) {
					thread.setRunning(false);
					((Activity)getContext()).finish();
				}
				
				creditButton.handleActionDown((int)event.getX(), (int)event.getY());
				if (creditButton.getTouched()) {
					creditRun = true;
				}
				
				paperButton.handleActionDown((int)event.getX(), (int)event.getY());
				if (paperButton.getTouched()) {
					paperTouch = !paperTouch;
					if (true == paperTouch) {
						goPaper();	
					} else {
						goReg();
					}
					
					
				}
			}
			
			
			
			
		}
		
		return true;
	}
	
	public void goPaper() {
		
		paper = true;
		balloonPopulatePaper();
		background = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.clouds1), width, height, true);
		if (TITLE_BAR.getWidth() > width) {
			title_bar = Bitmap.createScaledBitmap(TITLE_BAR1, width, height / 3, true);
		} else {
			title_bar = TITLE_BAR1;
		}
		glyphs = new Glyphs(BLUE_NUMBER1);
		startButton = new Button((width / 2), (height / 2), START_BUTTON1);
		MENUBAR = BitmapFactory.decodeResource(getResources(), R.drawable.top_bar1);
		PAUSE_MENU = BitmapFactory.decodeResource(getResources(), R.drawable.pause_menu1);
		
	}
	
	public void goReg() {
		
		paper = false;
		balloonPopulate();
		background = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.clouds), width, height, true);
		if (TITLE_BAR.getWidth() > width) {
			title_bar = Bitmap.createScaledBitmap(TITLE_BAR, width, height / 3, true);
		} else {
			title_bar = TITLE_BAR;
		}
		glyphs = new Glyphs(BLUE_NUMBER);
		startButton = new Button((width / 2), (height / 2), START_BUTTON);
		MENUBAR = BitmapFactory.decodeResource(getResources(), R.drawable.top_bar);
		PAUSE_MENU = BitmapFactory.decodeResource(getResources(), R.drawable.pause_menu);
		
		
	}
	
	//checks if the balloon is outside of the top of the screen
	public boolean checkBounds (int index) {
		
		boolean outOfBounds = false;
		
		if (index <= balloons.size() - 1 && balloons.get(index).getY() <= 0 - balloons.get(index).getHeight()) {
			outOfBounds = true;
		}
		return outOfBounds;
	}
	
	public boolean checkCreditBounds() {
		boolean outOfBounds = false;
		if (credits.getY() <= 0 - credits.getHeight()) {
			outOfBounds = true;
		}
		return outOfBounds;
	}
	
	public boolean checkType (int index) {
		if (balloons.size() - 1 >= index){
			if (balloons.get(index).getTypeNumber() > -1) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	
	public void loseCriteria(boolean oob) {
		if (gameType == 1 && !(thread.getTimePassed() >= thread.getGameTime()) && oob) {
			this.missedBalloons++;
			//Log.d(TAG, "Missed " + Integer.toString(missedBalloons) + " balloons");
		}
	}
	
	//deletes a balloon object
	public void balloonPopped (int index) {
		
		//Log.d(TAG, "Deleting a balloon");
		
		balloons.get(index).cleanUp();
		balloons.remove(index);
		numBalloons--;
		popIndex = -1;
		
	}
	
	public void resetCredits () {
		
		credits.setY(height);
		creditRun = false;
	}
	
	//removes a pop animation
	public void removePop (int index) {
		
		Log.d(TAG, "Removing pop animation");
		
		balloonPop.get(index).cleanUp();
		balloonPop.remove(index);
	}
	
	public void updateBalloons() {
		objectIndex = 0;
		//cycles through the balloons to update them all
		while (objectIndex <= numBalloons) {
		// moves the balloon up the screen
			if (balloons.size() > objectIndex && balloons.get(objectIndex).isTouched() == false) {
				speed = balloons.get(objectIndex).getSpeed();
				
				xDrop = balloons.get(objectIndex).getY();
				balloons.get(objectIndex).setY(xDrop - speed); 
			}
			objectIndex++;
		
		}
		objectIndex = 0;
	}
	
	public void updateCredits() { 

		speed = credits.getSpeed();
				
		xDrop = credits.getY();
		credits.setY(xDrop - speed); 
		

	}
	
	public boolean getCreditRun() {
		return creditRun;
	}
	
	public void setOpening(boolean opening) {
		this.opening = opening;
	}
	
	public boolean getOpening() {
		return opening;
	}
	
	//reduces the alpha of the splash screen
	public void alpha() {
		if (alphaRedux > 0) {
			alphaRedux = alphaRedux - 3;
			if (alphaRedux < 0) {
				alphaRedux = 0;
			}
		}
		
	}
	
	public void setRunOnce(boolean runOnce) {
		this.runOnce = runOnce;
	}
	
	public boolean getRunOnce() {
		return runOnce;
	}
	
	public void updateTimeLeft(int gameTime, int timePassed) {
		timeLeft = gameTime - timePassed;
	}
	
	public void updateStart() {
		int thisY;
		thisY = startButton.getY();
		startButton.setY(thisY - 3);
		if (startButton.getY() < (0 - startButton.getHeight())){
			thread.setStartTouched(startTouched);
			startTouched = false;
			opening = false;
			thread.setOpening(opening);
			bgMusic.release();
			gameMusic();
			
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		
		
		
		if (true == opening) {
		//draws the splash screen	
			
			if (false == showSplash) {
				alpha();
				paint.setAlpha(alphaRedux);
			}
			
			canvas.drawBitmap(background, 0, 0, null);
			
			
			if (startTouched) {
				updateStart();
			}
			
			while (objectIndex <= numBalloons) {
				balloons.get(objectIndex).draw(canvas);
				objectIndex++;
			}
			objectIndex = 0;
			
			
		
			
			glyphs.drawString(canvas, "high score", (width / 2) - ((10 * glyphs.getWidth() / 2)), (height /2) + (startButton.getHeight() / 2));
			glyphs.drawString(canvas, scoreString, (width / 2) - ((scoreString.length() * glyphs.getWidth() / 2)), (height /2) + (startButton.getHeight()));
			exitTitleButton.draw(canvas);
			creditButton.draw(canvas);
			paperButton.draw(canvas);
			
			canvas.drawBitmap(title_bar, (width / 2) - (title_bar.getWidth() / 2), 50, null);
			
			startButton.draw(canvas);
			canvas.drawBitmap(splash, 0,  0, paint);
			if (creditRun) {
				credits.draw(canvas);
			}
		} else {
		//normal drawing
		paint.setARGB(255, 0, 0, 0);
		String s = Integer.toString(score);

		
		// fills the canvas with background
		canvas.drawBitmap(background, 0, 0, null);
		
		
		//canvas.drawText(s, 10, 10, paint);
		//draws the balloons
		while (objectIndex <= numBalloons) {
			balloons.get(objectIndex).draw(canvas);
			objectIndex++;
		}
		objectIndex = 0;
		
		
		//draws the pops (if any)
		if (balloonPop.size() > 0) {
			while (objectIndex <= balloonPop.size() - 1) {
				balloonPop.get(objectIndex).draw(canvas);
				//cycles the pop animation
				if (balloonPop.get(objectIndex).getBitmap() == POP2) {
					removePop(objectIndex);
				} else {
					balloonPop.get(objectIndex).setBitmap(POP2);
				}
				objectIndex++;
			}
			objectIndex = 0;
		}
		//draws the menu bar, if it doesn't fill with one it draws a second next to it
		canvas.drawBitmap(MENUBAR, 0, 0, null);
		if (MENUBAR.getWidth() < width) {
			canvas.drawBitmap(MENUBAR, MENUBAR.getWidth(), 0, null);
		}
		menuBar.draw(canvas);
		
		if (gameOver == true) {
			Log.d(TAG, "GAME_OVER");
			int yCoord = (height / 2) - (GAME_OVER.getHeight() / 2);
			int xCoord = (width / 2) - (GAME_OVER.getWidth() / 2);
			canvas.drawBitmap(GAME_OVER, xCoord, yCoord, null);
			xCoord = (width / 2 ) - ((s.length() / 2) * (BLUE_NUMBER.getWidth() / 36));
			
			
			ntExist = false;
		} else if (thread.getTimePassed() >= thread.getGameTime()) {
			Log.d(TAG, "TIME_UP");
			Log.d(TAG, Boolean.toString(gameOver));
			int yCoord = (height / 2) - (TIME_UP.getHeight() / 2);
			int xCoord = (width / 2) - (TIME_UP.getWidth() / 2);
			canvas.drawBitmap(TIME_UP, xCoord, yCoord, null);
			xCoord = (width / 2 ) - ((s.length() / 2) * (BLUE_NUMBER.getWidth() / 36));
			
			
			ntExist = false;
		}
		
		//draws the arrow and current target bitmaps
		canvas.drawBitmap(ARROW, width - balloonSize - ARROW.getWidth() - 5, 0, null);
		canvas.drawBitmap(currentTarget, width - currentTarget.getWidth() - 1, 0, null);	
		if (ntExist && gameType == 0) {
			//displays the new target indicator
			canvas.drawBitmap(NEW_TARGET, (width / 2) - (NEW_TARGET.getWidth() / 2), (height / 2) - (NEW_TARGET.getHeight() / 2), null);
		} else if (ntExist && gameType == 1 && runOnce == true) {
			//displays the pop them all message
			canvas.drawBitmap(POP_ALL, (width / 2) - (POP_ALL.getWidth() / 2), (height / 2) - (POP_ALL.getHeight() / 2), null);
		}
		//displays the remaining time
		glyphs.drawString(canvas, Integer.toString(timeLeft), width / 2, 0);
		int balloonsLeft =  15 - missedBalloons;
		if (balloonsLeft < 0)
			balloonsLeft = 0;
		glyphs.drawString(canvas, Integer.toString(balloonsLeft) + " left!", 0, menuBar.getHeight() + 20);
		glyphs.drawString(canvas, s, width - ((s.length()) * (BLUE_NUMBER.getWidth() / 36)), height - BLUE_NUMBER.getHeight());
		
		if (paused) {
			//shows the pause menu
			canvas.drawBitmap(PAUSE_MENU, (width / 2) - (PAUSE_MENU.getWidth() / 2), (height / 2) - (PAUSE_MENU.getHeight() / 2), null);
			newButton.draw(canvas);
			endButton.draw(canvas);
			exitButton.draw(canvas);
		}
	}
	}

}
