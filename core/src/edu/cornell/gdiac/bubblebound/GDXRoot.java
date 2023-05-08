/*
 * GDXRoot.java
 *
 * This is the primary class file for running the game.  It is the "static main" of
 * LibGDX.  In the first lab, we extended ApplicationAdapter.  In previous lab
 * we extended Game.  This is because of a weird graphical artifact that we do not
 * understand.  Transparencies (in 3D only) is failing when we use ApplicationAdapter. 
 * There must be some undocumented OpenGL code in setScreen.
 *
 * Author: Walker M. White
 * Based on original PhysicsDemo Lab by Don Holden, 2007
 * Updated asset version, 2/6/2021
 */
 package edu.cornell.gdiac.bubblebound;

import com.badlogic.gdx.*;
import edu.cornell.gdiac.util.*;
import edu.cornell.gdiac.assets.*;

/**
 * Root class for a LibGDX.  
 * 
 * This class is technically not the ROOT CLASS. Each platform has another class above
 * this (e.g. PC games use DesktopLauncher) which serves as the true root.  However, 
 * those classes are unique to each platform, while this class is the same across all 
 * plaforms. In addition, this functions as the root class all intents and purposes, 
 * and you would draw it as a root class in an architecture specification.  
 */
public class GDXRoot extends Game implements ScreenListener {
	/** AssetManager to load game assets (textures, sounds, etc.) */
	AssetDirectory directory;
	/** Drawing context to display graphics (VIEW CLASS) */
	private GameCanvas canvas; 
	/** Player mode for the asset loading screen (CONTROLLER CLASS) */
	private LoadingMode loading;

	private LevelSelectMode levelselect;

	private SettingsMode settings;
	/** Player mode for the the game proper (CONTROLLER CLASS) */
	private int current;
	/** List of all WorldControllers */
	private PlatformController[] controllers;
	
	/**
	 * Creates a new game from the configuration settings.
	 *
	 * This method configures the asset manager, but does not load any assets
	 * or assign any screen.
	 */
	public GDXRoot() { }

	/** 
	 * Called when the Application is first created.
	 * 
	 * This is method immediately loads assets for the loading screen, and prepares
	 * the asynchronous loader for all other assets.
	 */
	public void create() {
		canvas  = new GameCanvas();
		loading = new LoadingMode("assets.json",canvas,1);


		// Initialize the three game worlds
		controllers = new PlatformController[1];
		//controllers[0] = new RocketController();
		controllers[0] = new PlatformController();
		//controllers[2] = new RagdollController();
		current = 0;
		loading.setScreenListener(this);
		setScreen(loading);
	}

	/** 
	 * Called when the Application is destroyed. 
	 *
	 * This is preceded by a call to pause().
	 */
	public void dispose() {
		// Call dispose on our children
		setScreen(null);
		for(int ii = 0; ii < controllers.length; ii++) {
			controllers[ii].dispose();
		}

		canvas.dispose();
		canvas = null;
	
		// Unload all of the resources
		// Unload all of the resources
		if (directory != null) {
			directory.unloadAssets();
			directory.dispose();
			directory = null;
		}
		super.dispose();
	}
	
	/**
	 * Called when the Application is resized. 
	 *
	 * This can happen at any point during a non-paused state but will never happen 
	 * before a call to create().
	 *
	 * @param width  The new width in pixels
	 * @param height The new height in pixels
	 */
	public void resize(int width, int height) {
		canvas.resize();
		super.resize(width,height);
	}



	/**
	 * The given screen has made a request to exit its player mode.
	 *
	 * The value exitCode can be used to implement menu options.
	 *
	 * @param screen   The screen requesting to exit
	 * @param exitCode The state of the screen upon exit
	 */
	public void exitScreen(Screen screen, int exitCode) {

		if (screen == loading) {
			for(int ii = 0; ii < controllers.length; ii++) {
				directory = loading.getAssets();
				controllers[ii].gatherAssets(directory);
				controllers[ii].setScreenListener(this);
				controllers[ii].setCanvas(canvas);
			}

			if(exitCode == 0){ //normal start
				controllers[current].reset(1);
				setScreen(controllers[current]);
			}
			else if(exitCode == 1){ //lvl select
				levelselect = new LevelSelectMode("assets.json", canvas, 1);
				levelselect.setScreenListener(this);
				setScreen(levelselect);
			}else if(exitCode == 2){// settings mode
				settings = new SettingsMode("assets.json",canvas,1);
				settings.setScreenListener(this);
				setScreen(settings);

			}

		} else if(screen == settings){
			settings.disabled = true;
			settings.dispose();
			settings = null;
			loading.poopypants();
			loading.setScreenListener(this);
			setScreen(loading);

		}else if(screen == levelselect){
			levelselect.dispose();
			levelselect = null;
			if(exitCode == -1){
				loading.poopypants();
				setScreen(loading);
			}else{
				exitCode += 1;
				System.out.println(exitCode);
				directory = loading.getAssets();
				controllers[0].gatherAssets(directory);
				controllers[0].setScreenListener(this);
				controllers[0].setCanvas(canvas);
				controllers[0].setCurrLevel(exitCode - 1);
				controllers[0].reset(exitCode);
				setScreen(controllers[0]);
			}
		}
		else if (exitCode == PlatformController.EXIT_NEXT) {
			current = (current+1) % controllers.length;
			controllers[current].reset(1);
			setScreen(controllers[current]);
		} else if (exitCode == PlatformController.EXIT_PREV) {
			current = (current+controllers.length-1) % controllers.length;
			controllers[current].reset(1);
			setScreen(controllers[current]);
		} else if (exitCode == PlatformController.EXIT_QUIT) {
			// We quit the main application
			Gdx.app.exit();
		}
	}

}
