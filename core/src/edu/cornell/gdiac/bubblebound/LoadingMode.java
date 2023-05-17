/*
 * LoadingMode.java
 *
 * Asset loading is a really tricky problem.  If you have a lot of sound or images,
 * it can take a long time to decompress them and load them into memory.  If you just
 * have code at the start to load all your assets, your game will look like it is hung
 * at the start.
 *
 * The alternative is asynchronous asset loading.  In asynchronous loading, you load a
 * little bit of the assets at a time, but still animate the game while you are loading.
 * This way the player knows the game is not hung, even though he or she cannot do 
 * anything until loading is complete. You know those loading screens with the inane tips 
 * that want to be helpful?  That is asynchronous loading.  
 *
 * This player mode provides a basic loading screen.  While you could adapt it for
 * between level loading, it is currently designed for loading all assets at the 
 * start of the game.
 *
 * Author: Walker M. White
 * Based on original PhysicsDemo Lab by Don Holden, 2007
 * Updated asset version, 2/6/2021
 */
package edu.cornell.gdiac.bubblebound;

import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.Sound;
//import com.badlogic.gdx.audio.*;
import com.badlogic.gdx.math.Vector2;

import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.ControllerMapping;

import edu.cornell.gdiac.assets.*;
import edu.cornell.gdiac.util.*;

/**
 * Class that provides a loading screen for the state of the game.
 *
 * You still DO NOT need to understand this class for this lab.  We will talk about this
 * class much later in the course.  This class provides a basic template for a loading
 * screen to be used at the start of the game or between levels.  Feel free to adopt
 * this to your needs.
 *
 * You will note that this mode has some textures that are not loaded by the AssetManager.
 * You are never required to load through the AssetManager.  But doing this will block
 * the application.  That is why we try to have as few resources as possible for this
 * loading screen.
 */
public class LoadingMode implements Screen, InputProcessor, ControllerListener {
	// There are TWO asset managers.  One to load the loading screen.  The other to load the assets
	/** Internal assets for this loading screen */
	private AssetDirectory internal;
	/** The actual assets to be loaded */
	private AssetDirectory assets;

	private AssetDirectory lvlselect;

	/** Background texture for start-up */
	private Texture [] background = new Texture [9];
	private FilmStrip backStrip;
	/** Play button to display when done */
	private Texture playButton;

	private Texture lvlselectButton;
	private Texture settingsButton;
	private Texture quitButton;
	/** Texture atlas to support a progress bar */
	private final Texture statusBar;
	/** Texture atlas to support a progress bar */
	/*private Music loadingMusic;
	private long loadingMusicId = -1;
	*/
	// statusBar is a "texture atlas." Break it up into parts.
	/** Left cap to the status background (grey region) */
	private TextureRegion statusBkgLeft;
	/** Middle portion of the status background (grey region) */
	private TextureRegion statusBkgMiddle;
	/** Right cap to the status background (grey region) */
	private TextureRegion statusBkgRight;
	/** Left cap to the status forground (colored region) */
	private TextureRegion statusFrgLeft;
	/** Middle portion of the status forground (colored region) */
	private TextureRegion statusFrgMiddle;
	/** Right cap to the status forground (colored region) */
	private TextureRegion statusFrgRight;	

	private Sound loadingMusic;
	private long loadingMusicId;

	/** Default budget for asset loader (do nothing but load 60 fps) */
	private static int DEFAULT_BUDGET = 15;
	/** Standard window size (for scaling) */
	private static int STANDARD_WIDTH  = 1800;
	/** Standard window height (for scaling) */
	private static int STANDARD_HEIGHT = 1000;
	/** Ratio of the bar width to the screen */
	private static float BAR_WIDTH_RATIO  = 0.66f;
	/** Ration of the bar height to the screen */
	private static float BAR_HEIGHT_RATIO = 0.25f;	
	/** Height of the progress bar */
	private static float BUTTON_SCALE  = 0.75f;
	
	/** Reference to GameCanvas created by the root */
	private GameCanvas canvas;
	/** Listener that will update the player mode when we are done */
	private ScreenListener listener;

	/** The width of the progress bar */
	private int width;
	/** The y-coordinate of the center of the progress bar */
	private int centerY;
	/** The x-coordinate of the center of the progress bar */
	private int centerX;
	/** The height of the canvas window (necessary since sprite origin != screen origin) */
	private int heightY;
	/** Scaling factor for when the student changes the resolution. */
	private Vector2 scale;

	/** Current progress (0 to 1) of the asset manager */
	private float progress;
	/** The current state of the play button */
	private int   pressState;
	/** The amount of time to devote to loading assets (as opposed to on screen hints, etc.) */
	private int   budget;

	/** Whether or not this player mode is still active */
	private boolean active;
	private Texture screenText;
    private FilmStrip screenStrip;
	/**
	 * Returns the budget for the asset loader.
	 *
	 * The budget is the number of milliseconds to spend loading assets each animation
	 * frame.  This allows you to do something other than load assets.  An animation 
	 * frame is ~16 milliseconds. So if the budget is 10, you have 6 milliseconds to 
	 * do something else.  This is how game companies animate their loading screens.
	 *
	 * @return the budget in milliseconds
	 */
	public int getBudget() {
		return budget;
	}

	/**
	 * Sets the budget for the asset loader.
	 *
	 * The budget is the number of milliseconds to spend loading assets each animation
	 * frame.  This allows you to do something other than load assets.  An animation 
	 * frame is ~16 milliseconds. So if the budget is 10, you have 6 milliseconds to 
	 * do something else.  This is how game companies animate their loading screens.
	 *
	 * @param millis the budget in milliseconds
	 */
	public void setBudget(int millis) {
		budget = millis;
	}
	
	/**
	 * Returns true if all assets are loaded and the player is ready to go.
	 *
	 * @return true if the player is ready to go
	 */
	public boolean isReady() {
		return pressState == 2;
	}

	/**
	 * Returns the asset directory produced by this loading screen
	 *
	 * This asset loader is NOT owned by this loading scene, so it persists even
	 * after the scene is disposed.  It is your responsbility to unload the
	 * assets in this directory.
	 *
	 * @return the asset directory produced by this loading screen
	 */
	public AssetDirectory getAssets() {
		return assets;
	}

	public AssetDirectory getLvlselect(){
		return lvlselect;
	}

	/**
	 * Creates a LoadingMode with the default budget, size and position.
	 *
	 * @param file  	The asset directory to load in the background
	 * @param canvas 	The game canvas to draw to
	 */
	public LoadingMode(String file, GameCanvas canvas) {
		this(file, canvas, DEFAULT_BUDGET);
	}

	private Vector2 playPos;
	private Vector2 lvlSelectPos;
	private Vector2 settingsPos;
	private Vector2 quitPos;

	private Texture hoveredPlayButton;
	private Texture hoveredLvlSelect;
	private Texture hoveredQuit;
	private Texture hoveredSettings;

	private int count;
	private int max_count;

	private int delay;
	private int max_delay;


	public void stopMusic() { loadingMusic.stop();}
	/**
	 * Creates a LoadingMode with the default size and position.
	 *
	 * The budget is the number of milliseconds to spend loading assets each animation
	 * frame.  This allows you to do something other than load assets.  An animation 
	 * frame is ~16 milliseconds. So if the budget is 10, you have 6 milliseconds to 
	 * do something else.  This is how game companies animate their loading screens.
	 *
	 * @param file  	The asset directory to load in the background
	 * @param canvas 	The game canvas to draw to
	 * @param millis The loading budget in milliseconds
	 */
	public LoadingMode(String file, GameCanvas canvas, int millis) {
		this.canvas  = canvas;
		budget = millis;
		count = 0;
		max_count = 9;
		delay = 0;
		max_delay = 10;
		
		// Compute the dimensions from the canvas
		resize(canvas.getWidth(),canvas.getHeight());

		// We need these files loaded immediately
		internal = new AssetDirectory( "loading.json" );
		internal.loadAssets();
		internal.finishLoading();

		screenText = internal.getEntry("screen", Texture.class);
		screenStrip = new FilmStrip(screenText, 1, 9, 9);
		// Load the next two images immediately.
		playButton = null;
		lvlselectButton = null;
		settingsButton = null;
		quitButton = null;
		for(int i = 0; i < 9; i++) {
			background[i] = internal.getEntry( "background" + i, Texture.class );
			background[i].setFilter( TextureFilter.Linear, TextureFilter.Linear );
		}

		statusBar = internal.getEntry( "progress", Texture.class );
		hoveredPlayButton = internal.getEntry("playhovered", Texture.class);
		hoveredLvlSelect = internal.getEntry("lvlselecthovered", Texture.class);
		hoveredQuit = internal.getEntry("quithovered", Texture.class);
		hoveredSettings = internal.getEntry("settingshovered",Texture.class);

		playPos = createPos(960, 90 + hoveredPlayButton.getHeight() / 2);
		lvlSelectPos = createPos(960, 241 + hoveredLvlSelect.getHeight() / 2);
		settingsPos = createPos(960, 321 + hoveredSettings.getHeight() / 2);
		quitPos = createPos(960, 401 + hoveredQuit.getHeight() / 2);

		//load the loading theme immediately
		loadingMusic = internal.getEntry("menuscreen", Sound.class);
		loadingMusicId = loadingMusic.loop(1f);
		//.play();

		// Break up the status bar texture into regions
		statusBkgLeft = internal.getEntry( "progress.backleft", TextureRegion.class );
		statusBkgRight = internal.getEntry( "progress.backright", TextureRegion.class );
		statusBkgMiddle = internal.getEntry( "progress.background", TextureRegion.class );

		statusFrgLeft = internal.getEntry( "progress.foreleft", TextureRegion.class );
		statusFrgRight = internal.getEntry( "progress.foreright", TextureRegion.class );
		statusFrgMiddle = internal.getEntry( "progress.foreground", TextureRegion.class );

		//loadingMusic = internal.getEntry( "bubbleboundsfx:level1cavetheme", Sound.class );


		// No progress so far.
		progress = 0;
		pressState = 0;

		Gdx.input.setInputProcessor( this );

		// Let ANY connected controller start the game.

		// Start loading the real assets
		assets = new AssetDirectory( file );
		assets.loadAssets();

		lvlselect = new AssetDirectory("levelselect.json");
		lvlselect.loadAssets();
		active = true;
	}


	public void poopypants(){
		pressState = 0;
		Gdx.input.setInputProcessor(this);
	}
	/**
	 * Called when this screen should release all resources.
	 */
	public void dispose() {

		internal.unloadAssets();
		internal.dispose();
	}

	private float progress2 = 0;

	private Vector2 createPos(int x, int y){
		return new Vector2(x * scale.x, canvas.getHeight() - (y*scale.y));
	}

	int hovered = -1;

	/**
	 * Update the status of this player mode.
	 *
	 * We prefer to separate update and draw from one another as separate methods, instead
	 * of using the single render() method that LibGDX does.  We will talk about why we
	 * prefer this in lecture.
	 *
	 * @param delta Number of seconds since last animation frame
	 */
	private void update(float delta) {
		Gdx.input.setInputProcessor( this );
		if (playButton == null) {
			assets.update(budget);
			this.progress = assets.getProgress();
			lvlselect.update(budget);
			this.progress2 = lvlselect.getProgress();
			if (progress + progress2 >= 2.0f) {

				this.progress = 2.0f;
				playButton = internal.getEntry("play",Texture.class);
				settingsButton = internal.getEntry("settings", Texture.class);
				lvlselectButton = internal.getEntry("lvlselect", Texture.class);
				quitButton = internal.getEntry("quit", Texture.class);

			}
		}else{
			int x = Gdx.input.getX();
			int y = Gdx.input.getY();
			hovered = 0;
			if(pressedButton(x, y, playButton, playPos)){
				hovered = 1;
			}else if(pressedButton(x, y, lvlselectButton, lvlSelectPos)){
				hovered = 2;
			}else if(pressedButton(x, y, settingsButton, settingsPos)){
				hovered = 3;
			}else if(pressedButton(x, y, quitButton, quitPos)){
				hovered = 4;
			}
		}
	}

	/**
	 * Draw the status of this player mode.
	 *
	 * We prefer to separate update and draw from one another as separate methods, instead
	 * of using the single render() method that LibGDX does.  We will talk about why we
	 * prefer this in lecture.
	 */
	private void draw() {
		canvas.begin();
		float sx = ((float) canvas.getWidth()) / ((float) background[0].getWidth());
		float sy = ((float) canvas.getHeight()) / ((float) background[0].getHeight());
//		canvas.draw(background, 0, 0);
		float x = canvas.getWidth() / 2f;
		float y = canvas.getHeight() * 0.6f;
		System.out.println(count);
		canvas.draw(background[count], Color.WHITE, 0, 0, 0, 0, 0, sx, sy);
		//canvas.draw(background, Color.WHITE, 0, 0, 0, 0, 0, sx, sy);
		if (playButton == null) {
			drawProgress(canvas);
		} else {
			Texture temp = (hovered == 1) ? hoveredPlayButton : playButton;
			canvas.draw(temp, Color.WHITE, temp.getWidth() / 2f, temp.getHeight() / 2f, playPos.x, playPos.y, 0, sx, sy);

			temp = (hovered == 2) ? hoveredLvlSelect : lvlselectButton;
			canvas.draw(temp, Color.WHITE, temp.getWidth() / 2f, temp.getHeight() / 2f, lvlSelectPos.x, lvlSelectPos.y, 0, sx, sy);

			temp = (hovered == 3) ? hoveredSettings : settingsButton;
			canvas.draw(temp, Color.WHITE, temp.getWidth() / 2f, temp.getHeight() / 2f, settingsPos.x, settingsPos.y, 0, sx, sy);

			temp = (hovered == 4) ? hoveredQuit : quitButton;
			canvas.draw(temp, Color.WHITE, temp.getWidth() / 2f, temp.getHeight() / 2f, quitPos.x, quitPos.y, 0, sx, sy);

		}
		canvas.end();
		delay++;
		if(delay == max_delay){
			delay = 0;
		}
		if(delay == 9){
			count++;
			if(count == max_count){
				count = 0;
			}
		}
	}
	
	/**
	 * Updates the progress bar according to loading progress
	 *
	 * The progress bar is composed of parts: two rounded caps on the end, 
	 * and a rectangle in a middle.  We adjust the size of the rectangle in
	 * the middle to represent the amount of progress.
	 *
	 * @param canvas The drawing context
	 */	
	private void drawProgress(GameCanvas canvas) {
		float adj = 440f *scale.y;
		canvas.draw(statusBkgLeft,   Color.WHITE, centerX-width/2, centerY-adj,
				scale.x*statusBkgLeft.getRegionWidth(), scale.y*statusBkgLeft.getRegionHeight());
		canvas.draw(statusBkgRight,  Color.WHITE,centerX+width/2-scale.x*statusBkgRight.getRegionWidth(), centerY-adj,
				scale.x*statusBkgRight.getRegionWidth(), scale.y*statusBkgRight.getRegionHeight());
		canvas.draw(statusBkgMiddle, Color.WHITE,centerX-width/2+scale.x*statusBkgLeft.getRegionWidth(), centerY-adj,
				width-scale.x*(statusBkgRight.getRegionWidth()+statusBkgLeft.getRegionWidth()),
				scale.y*statusBkgMiddle.getRegionHeight());

		canvas.draw(statusFrgLeft,   Color.WHITE,centerX-width/2, centerY-adj,
				scale.x*statusFrgLeft.getRegionWidth(), scale.y*statusFrgLeft.getRegionHeight());
		if (progress > 0) {
			float span = progress*(width-scale.x*(statusFrgLeft.getRegionWidth()+statusFrgRight.getRegionWidth()))/2.0f;
			canvas.draw(statusFrgRight,  Color.WHITE,centerX-width/2+scale.x*statusFrgLeft.getRegionWidth()+span, centerY-adj,
					scale.x*statusFrgRight.getRegionWidth(), scale.y*statusFrgRight.getRegionHeight());
			canvas.draw(statusFrgMiddle, Color.WHITE,centerX-width/2+scale.x*statusFrgLeft.getRegionWidth(), centerY-adj,
					span, scale.y*statusFrgMiddle.getRegionHeight());
		} else {
			canvas.draw(statusFrgRight,  Color.WHITE,centerX-width/2+scale.x*statusFrgLeft.getRegionWidth(), centerY-adj,
					scale.x*statusFrgRight.getRegionWidth(), scale.y*statusFrgRight.getRegionHeight());
		}
	}


	public Sound getMusic(){
		return loadingMusic;
	}
	public long getMusicId(){
		return loadingMusicId;
	}


	// ADDITIONAL SCREEN METHODS
	/**
	 * Called when the Screen should render itself.
	 *
	 * We defer to the other methods update() and draw().  However, it is VERY important
	 * that we only quit AFTER a draw.
	 *
	 * @param delta Number of seconds since last animation frame
	 */
	public void render(float delta) {
		if (active) {
			update(delta);
			draw();

			// We are are ready, notify our listener
			if (isReady() && listener != null) {
				loadingMusic.stop();
				pressState = 0;
				listener.exitScreen(this, 0);
				Gdx.input.setInputProcessor(null);


			}
			if(pressState == 3){ //lvl select
				pressState = 0;
				listener.exitScreen(this, 1);
				Gdx.input.setInputProcessor(null);


			}
			if(pressState == 4){ //settings
				pressState = 0;
				listener.exitScreen(this, 2);
				Gdx.input.setInputProcessor(null);
			}
			if(pressState == 5){ //quit
				pressState = 0;
				listener.exitScreen(this, 3);
			}
		}
	}

	/**
	 * Called when the Screen is resized. 
	 *
	 * This can happen at any point during a non-paused state but will never happen 
	 * before a call to show().
	 *
	 * @param width  The new width in pixels
	 * @param height The new height in pixels
	 */
	public void resize(int width, int height) {
		// Compute the drawing scale
		float sx = ((float)width)/1920;
		float sy = ((float)height)/1080;
		scale = new Vector2(sx, sy);

		
		this.width = (int)(BAR_WIDTH_RATIO*width);
		centerY = (int)(BAR_HEIGHT_RATIO*height);
		centerY += 200;
		centerX = width/2;
		heightY = height;
	}

	/**
	 * Called when the Screen is paused.
	 * 
	 * This is usually when it's not active or visible on screen. An Application is 
	 * also paused before it is destroyed.
	 */
	public void pause() {
		// TODO Auto-generated method stub

	}

	/**
	 * Called when the Screen is resumed from a paused state.
	 *
	 * This is usually when it regains focus.
	 */
	public void resume() {
		// TODO Auto-generated method stub

	}
	
	/**
	 * Called when this screen becomes the current screen for a Game.
	 */
	public void show() {
		// Useless if called in outside animation loop
		Gdx.input.setInputProcessor(this);

		active = true;
	}

	/**
	 * Called when this screen is no longer the current screen for a Game.
	 */
	public void hide() {
		// Useless if called in outside animation loop
		active = false;
	}
	
	/**
	 * Sets the ScreenListener for this mode
	 *
	 * The ScreenListener will respond to requests to quit.
	 */
	public void setScreenListener(ScreenListener listener) {
		this.listener = listener;
	}
	
	// PROCESSING PLAYER INPUT
	/** 
	 * Called when the screen was touched or a mouse button was pressed.
	 *
	 * This method checks to see if the play button is available and if the click
	 * is in the bounds of the play button.  If so, it signals the that the button
	 * has been pressed and is currently down. Any mouse button is accepted.
	 *
	 * @param screenX the x-coordinate of the mouse on the screen
	 * @param screenY the y-coordinate of the mouse on the screen
	 * @param pointer the button or touch finger number
	 * @return whether to hand the event to other listeners. 
	 */
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if (playButton == null || pressState == 2) {
			//System.out.println("howdy");
			return true;
		}

		//System.out.println("DO this pls");
		// Flip to match graphics coordinates
		if(pressedButton(screenX, screenY, playButton, playPos)){
			pressState = 1;
		}
		if(pressedButton(screenX, screenY, lvlselectButton, lvlSelectPos)){
			pressState = 3;
		}
		if(pressedButton(screenX, screenY, settingsButton, settingsPos)){
			pressState = 4;
		}
		if(pressedButton(screenX, screenY, quitButton, quitPos)){
			pressState = 5;
		}

		return false;
	}

	public boolean pressedButton(int screenX, int screenY, Texture texture, Vector2 button_pos){
		float button_w = texture.getWidth() * scale.x / 2f;
		float button_h = texture.getHeight() * scale.y / 2f;
		screenY = canvas.getHeight() - screenY;
		//System.out.println(button_center);
		if(screenX >= button_pos.x - button_w && screenX <= button_pos.x + button_w){
			if(screenY >= button_pos.y - button_h && screenY <= button_pos.y + button_h){
				return true;
			}
		}
		return false;
	}


	/** 
	 * Called when a finger was lifted or a mouse button was released.
	 *
	 * This method checks to see if the play button is currently pressed down. If so, 
	 * it signals the that the player is ready to go.
	 *
	 * @param screenX the x-coordinate of the mouse on the screen
	 * @param screenY the y-coordinate of the mouse on the screen
	 * @param pointer the button or touch finger number
	 * @return whether to hand the event to other listeners. 
	 */	
	public boolean touchUp(int screenX, int screenY, int pointer, int button) { 
		if (pressState == 1) {
			pressState = 2;
			return false;
		}
		return true;
	}
	
	/** 
	 * Called when a button on the Controller was pressed. 
	 *
	 * The buttonCode is controller specific. This listener only supports the start
	 * button on an X-Box controller.  This outcome of this method is identical to 
	 * pressing (but not releasing) the play button.
	 *
	 * @param controller The game controller
	 * @param buttonCode The button pressed
	 * @return whether to hand the event to other listeners. 
	 */
	public boolean buttonDown (Controller controller, int buttonCode) {
		if (pressState == 0) {
			ControllerMapping mapping = controller.getMapping();
			if (mapping != null && buttonCode == mapping.buttonStart ) {
				pressState = 1;
				return false;
			}
		}
		return true;
	}
	
	/** 
	 * Called when a button on the Controller was released. 
	 *
	 * The buttonCode is controller specific. This listener only supports the start
	 * button on an X-Box controller.  This outcome of this method is identical to 
	 * releasing the the play button after pressing it.
	 *
	 * @param controller The game controller
	 * @param buttonCode The button pressed
	 * @return whether to hand the event to other listeners. 
	 */
	public boolean buttonUp (Controller controller, int buttonCode) {
		if (pressState == 1) {
			ControllerMapping mapping = controller.getMapping();
			if (mapping != null && buttonCode == mapping.buttonStart ) {
				pressState = 2;
				return false;
			}
		}
		return true;
	}
	
	// UNSUPPORTED METHODS FROM InputProcessor

	/** 
	 * Called when a key is pressed (UNSUPPORTED)
	 *
	 * @param keycode the key pressed
	 * @return whether to hand the event to other listeners. 
	 */
	public boolean keyDown(int keycode) { 
		return true; 
	}

	/** 
	 * Called when a key is typed (UNSUPPORTED)
	 *

	 * @return whether to hand the event to other listeners. 
	 */
	public boolean keyTyped(char character) { 
		return true; 
	}

	/** 
	 * Called when a key is released (UNSUPPORTED)
	 *
	 * @param keycode the key released
	 * @return whether to hand the event to other listeners. 
	 */	
	public boolean keyUp(int keycode) { 
		return true; 
	}
	
	/** 
	 * Called when the mouse was moved without any buttons being pressed. (UNSUPPORTED)
	 *
	 * @param screenX the x-coordinate of the mouse on the screen
	 * @param screenY the y-coordinate of the mouse on the screen
	 * @return whether to hand the event to other listeners. 
	 */	
	public boolean mouseMoved(int screenX, int screenY) { 
		return true; 
	}

	/**
	 * Called when the mouse wheel was scrolled. (UNSUPPORTED)
	 *
	 * @param dx the amount of horizontal scroll
	 * @param dy the amount of vertical scroll
	 *
	 * @return whether to hand the event to other listeners.
	 */
	public boolean scrolled(float dx, float dy) {
		return true;
	}

	/** 
	 * Called when the mouse or finger was dragged. (UNSUPPORTED)
	 *
	 * @param screenX the x-coordinate of the mouse on the screen
	 * @param screenY the y-coordinate of the mouse on the screen
	 * @param pointer the button or touch finger number
	 * @return whether to hand the event to other listeners. 
	 */		
	public boolean touchDragged(int screenX, int screenY, int pointer) { 
		return true; 
	}
	
	// UNSUPPORTED METHODS FROM ControllerListener
	
	/**
	 * Called when a controller is connected. (UNSUPPORTED)
	 *
	 * @param controller The game controller
	 */
	public void connected (Controller controller) {}

	/**
	 * Called when a controller is disconnected. (UNSUPPORTED)
	 *
	 * @param controller The game controller
	 */
	public void disconnected (Controller controller) {}

	/** 
	 * Called when an axis on the Controller moved. (UNSUPPORTED) 
	 *
	 * The axisCode is controller specific. The axis value is in the range [-1, 1]. 
	 *
	 * @param controller The game controller
	 * @param axisCode 	The axis moved
	 * @param value 	The axis value, -1 to 1
	 * @return whether to hand the event to other listeners. 
	 */
	public boolean axisMoved (Controller controller, int axisCode, float value) {
		return true;
	}

}