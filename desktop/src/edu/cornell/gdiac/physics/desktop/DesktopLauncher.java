/*
 * DesktopLauncher.java
 * 
 * LibGDX is a cross-platform development library. You write all of your code in 
 * the core project.  However, you still need some extra classes if you want to
 * deploy on a specific platform (e.g. PC, Android, Web).  That is the purpose
 * of this class.  It deploys your game on a PC/desktop computer.
 *
 * Author: Walker M. White
 * Based on original Optimization Lab by Don Holden, 2007
 * LibGDX version, 2/2/2015
 */
package edu.cornell.gdiac.physics.desktop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.utils.SharedLibraryLoader;
import edu.cornell.gdiac.backend.GDXApp;
import edu.cornell.gdiac.backend.GDXAppSettings;
import edu.cornell.gdiac.bubblebound.GDXRoot;
//import lwjgl3.Lwjgl3ApplicationConfiguration;

/**
 * The main class of the game.
 * 
 * This class sets the window size and launches the game.  Aside from modifying
 * the window size, you should almost never need to modify this class.
 */
public class DesktopLauncher {

	/**
	 * Classic main method that all Java programmers know.
	 * 
	 * This method simply exists to start a new Lwjgl3Application.  For desktop games,
	 * LibGDX is built on top of LWJGL3 (this is not the case for Android).
	 * 
	 * @param arg Command line arguments
	 */
	public static void main (String[] arg) {


		String os_version = System.getProperty("os.name");

		if (os_version.contains("mac")){
			System.out.println("STARTING MAC VERSION!");
			Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
			config.setForegroundFPS(60);
			config.setTitle("libgdx4m1");

			final Graphics.Monitor[] monitors = Lwjgl3ApplicationConfiguration.getMonitors();
			final Graphics.DisplayMode primaryMode = Lwjgl3ApplicationConfiguration.getDisplayMode(monitors[0]);
			config.setFullscreenMode(primaryMode);

			new Lwjgl3Application(new GDXRoot(), config);
		}
		else{
			System.out.println("STARTING NON MAC VERSION!");

			GDXAppSettings config = new GDXAppSettings();

			config.width  = 1024;
			config.height = 576;

			config.fullscreen = true;
			config.resizable = false;

			new GDXApp(new GDXRoot(), config);

		}
	}
}
