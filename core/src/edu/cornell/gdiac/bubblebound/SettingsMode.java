package edu.cornell.gdiac.bubblebound;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.ControllerMapping;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import edu.cornell.gdiac.assets.AssetDirectory;
import edu.cornell.gdiac.util.Controllers;
import edu.cornell.gdiac.util.ScreenListener;
import edu.cornell.gdiac.util.XBoxController;

   public class SettingsMode implements Screen, InputProcessor, ControllerListener {
        // There are TWO asset managers.  One to load the loading screen.  The other to load the assets
        /** Internal assets for this loading screen */
        private AssetDirectory internal;
        /** The actual assets to be loaded */
        private AssetDirectory assets;

        /** Background texture for start-up */
        private Texture background;
        /** Play button to display when done */
        private Texture settingsButton;
        /** Texture atlas to support a progress bar */
        //private final Texture statusBar;
        /** Texture atlas to support a progress bar */
	//private Music loadingMusic;
//       private long loadingMusicId = -1;

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

        /**
         * Creates a LoadingMode with the default budget, size and position.
         *
         * @param file  	The asset directory to load in the background
         * @param canvas 	The game canvas to draw to
         */
        public SettingsMode(String file, GameCanvas canvas) {
            this(file, canvas, DEFAULT_BUDGET);
        }

        private Texture bar;
        private Texture mVolume;
        private Texture sVolume;
        private Texture soundEffects;
        private Texture control;
        private Texture backButton;
        private Texture volumeBar;
        private Texture volumeBubble;
        private Texture mControls;
        private Texture kControls;
        private Texture fullscreenButton;
        private Texture windowedButton;

        public float musicVolume = 1.0f;
        public float soundVolume = 1.0f;
        public int controls = 0; //0 is mouse, 1 is keyboard


        public float getMusicVolume(){
            return musicVolume;
        }
        public float getSoundVolume(){
            return musicVolume;
        }

        Vector2 backButtonPos;
        Vector2 soundPos;
        Vector2 musicPos;
        Vector2 soundBarPos;
        Vector2 musicBarPos;
        Vector2 controlPos;
        Vector2 mControlPos;
        Vector2 kControlPos;
        Vector2 fullscreenPos;

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
        public SettingsMode(String file, GameCanvas canvas, int millis) {
            this.canvas  = canvas;
            budget = millis;

            // Compute the dimensions from the canvas


            // We need these files loaded immediately
            internal = new AssetDirectory( "settings.json" );
            internal.loadAssets();
            internal.finishLoading();

            // Load the next two images immediately.
            background = internal.getEntry( "settingsbackground", Texture.class );
            background.setFilter( Texture.TextureFilter.Linear, Texture.TextureFilter.Linear );
            resize(canvas.getWidth(),canvas.getHeight());
            //load the loading theme immediately
//            loadingMusic = internal.getEntry("menuscreen", Sound.class);
//            loadingMusicId = loadingMusic.play(musicVolume);
//            loadingMusic.setLooping(loadingMusicId, true);
            //loadingMusic.play();
            backButton = internal.getEntry("backbutton", Texture.class);
            mVolume = internal.getEntry("mvolume", Texture.class);
            sVolume = internal.getEntry("svolume", Texture.class);
            control = internal.getEntry("control",Texture.class);
            kControls = internal.getEntry("kcontrols",Texture.class);
            mControls = internal.getEntry("mcontrols",Texture.class);
            volumeBar = internal.getEntry("volumebar", Texture.class);
            volumeBubble = internal.getEntry("volumebubble", Texture.class);
            fullscreenButton = internal.getEntry("fullscreenbutton", Texture.class);
            windowedButton = internal.getEntry("windowedbutton", Texture.class);

            backButtonPos = new Vector2(canvas.getWidth()/10, canvas.getHeight() * 0.9f);
            soundPos = new Vector2(canvas.getWidth()/5, canvas.getHeight() * 0.7f);
            musicPos = new Vector2(canvas.getWidth()/5, canvas.getHeight() * 0.8f);
            soundBarPos = new Vector2(canvas.getWidth() * 0.7f, canvas.getHeight() * 0.7f);
            musicBarPos = new Vector2(canvas.getWidth() * 0.7f, canvas.getHeight() * 0.8f);
            controlPos = new Vector2(canvas.getWidth() / 2, canvas.getHeight() * 0.6f);
            mControlPos = new Vector2(canvas.getWidth() / 3, canvas.getHeight() * 0.4f);
            kControlPos = new Vector2(canvas.getWidth() * 2f / 3f, canvas.getHeight() * 0.4f);
            fullscreenPos = new Vector2(canvas.getWidth() / 2f, canvas.getHeight() * 0.2f);

            pressState = 0;

            Gdx.input.setInputProcessor( this );

            // Let ANY connected controller start the game.
            for (XBoxController controller : Controllers.get().getXBoxControllers()) {
                controller.addListener( this );
            }

            // Start loading the real assets
        }

        /**
         * Called when this screen should release all resources.
         */
        public void dispose() {
            Gdx.input.setInputProcessor(null);
            internal.unloadAssets();
            internal.dispose();
        }

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
            float sx = ((float) canvas.getWidth()) / ((float) background.getWidth());
            float sy = ((float) canvas.getHeight()) / ((float) background.getHeight());
//		canvas.draw(background, 0, 0);

            canvas.draw(background, Color.WHITE, 0, 0, 0, 0, 0, sx, sy);
            canvas.draw(backButton, Color.WHITE, backButton.getWidth()/2f, backButton.getHeight()/2f,
                    backButtonPos.x, backButtonPos.y, 0, scale.x, scale.y);
            canvas.draw(mVolume, Color.WHITE, mVolume.getWidth()/2f, mVolume.getHeight()/2f,
                    musicPos.x, musicPos.y, 0f, scale.x, scale.y);
            canvas.draw(sVolume, Color.WHITE, sVolume.getWidth() / 2f, sVolume.getHeight()/2f,
                    soundPos.x, soundPos.y, 0f, scale.x, scale.y);

            float temp = musicBarPos.x + ((musicVolume - 0.5f) * volumeBar.getWidth() * scale.x);
            canvas.draw(volumeBar, Color.WHITE, volumeBar.getWidth() / 2f, volumeBar.getHeight()/2f,
                    musicBarPos.x, musicBarPos.y, 0, scale.x, scale.y);
            canvas.draw(volumeBubble, Color.WHITE, volumeBubble.getWidth() / 2f, volumeBubble.getHeight() /2f,
                    temp, musicBarPos.y, 0, scale.x, scale.y);
            temp = soundBarPos.x + ((soundVolume - 0.5f) * volumeBar.getWidth() * scale.x);
            canvas.draw(volumeBar, Color.WHITE, volumeBar.getWidth() / 2f, volumeBar.getHeight()/2f,
                    soundBarPos.x, soundBarPos.y, 0, scale.x, scale.y);
            canvas.draw(volumeBubble, Color.WHITE, volumeBubble.getWidth() / 2f, volumeBubble.getHeight() /2f,
                    temp, soundBarPos.y, 0, scale.x, scale.y);

            Color keyTint;
            Color mouseTint;
            if(controls == 0){
                mouseTint = Color.WHITE;
                keyTint = Color.GRAY;
            }else{
                keyTint = Color.WHITE;
                mouseTint = Color.GRAY;
            }

            canvas.draw(control, Color.WHITE, control.getWidth() /2f, control.getHeight()/2f,
                    controlPos.x, controlPos.y, 0, scale.x, scale.y);
            canvas.draw(mControls, mouseTint, mControls.getWidth()/2f, mControls.getHeight()/2f,
                    mControlPos.x, mControlPos.y, 0, scale.x * 0.5f, scale.y * 0.5f);
            canvas.draw(kControls, keyTint,kControls.getWidth()/2f, kControls.getHeight()/2f,
                    kControlPos.x, kControlPos.y, 0, scale.x * 0.33f, scale.y * 0.33f);

            if(!canvas.isFullscreen()){
                canvas.draw(fullscreenButton, Color.WHITE,windowedButton.getWidth()/2f, windowedButton.getHeight()/2f,
                        fullscreenPos.x, fullscreenPos.y, 0, scale.x, scale.y);
            }else{
                canvas.draw(windowedButton, Color.WHITE,windowedButton.getWidth()/2f, windowedButton.getHeight()/2f,
                        fullscreenPos.x, fullscreenPos.y, 0, scale.x, scale.y);
//                System.out.println("please darw");
            }

            canvas.end();
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
                    listener.exitScreen(this, 0);
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
            float sx = ((float)width)/background.getWidth();
            float sy = ((float)height)/background.getHeight();
            sx = sy;
            scale = new Vector2(sx, sy);
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
            active = true;
        }

        /**
         * Called when this screen is no longer the current screen for a Game.
         */
        public void hide() {
            // Useless if called in outside animation loop
            active = false;
        }


        public void setMusic(Sound music, long id){
            loadingMusic = music;
            loadingMusicId = id;
        }
        /**
         * Sets the ScreenListener for this mode
         *
         * The ScreenListener will respond to requests to quit.
         */
        public void setScreenListener(ScreenListener listener) {
            this.listener = listener;
        }

        public boolean pressedButton(int screenX, int screenY, Texture texture, Vector2 button_center){
            float button_w = texture.getWidth() * scale.x;
            float button_h = texture.getHeight() * scale.y;
            screenY = canvas.getHeight() - screenY;
            //System.out.println(button_center);
            if(screenX >= button_center.x - button_w/2 && screenX <= button_center.x + button_w/2){
                if(screenY >= button_center.y - button_h/2 && screenY <= button_center.y + button_h/2){
                   return true;
                }
            }
            return false;
        }
        public boolean disabled = false;
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

            if(disabled){
                //System.out.println("bad howdy");
                return true;
            }

            // Flip to match graphics coordinates
            //screenY = heightY-screenY;

            if(pressedButton(screenX, screenY, backButton, backButtonPos)){
                //System.out.println("BACKK");
                pressState = 1;
            }
            if(pressedButton(screenX, screenY, volumeBar, musicBarPos)){
                float temp = screenX - (musicBarPos.x - (volumeBar.getWidth() * scale.x / 2f));
                musicVolume = temp / (volumeBar.getWidth() * scale.x);
                loadingMusic.setVolume(loadingMusicId, musicVolume);
            }
            if(pressedButton(screenX, screenY, volumeBar, soundBarPos)){
                float temp = screenX - (soundBarPos.x - (volumeBar.getWidth() * scale.x / 2f));
                soundVolume = temp / (volumeBar.getWidth() * scale.x);
            }
            if(pressedButton2(screenX, screenY, mControls, mControlPos, 0.5f)){
                controls = 0;

//                .setVolume(loadingMusicId, musicVolume);
            }
            if(pressedButton2(screenX, screenY, kControls, kControlPos, 0.33f)){
                controls = 1;
            }

            // TODO: Fix scaling
            // Play button is a circle.


            return false;
        }
       public boolean pressedButton2(int screenX, int screenY, Texture texture, Vector2 button_center, float f){
           float button_w = texture.getWidth() * scale.x * f;
           float button_h = texture.getHeight() * scale.y * f;
           screenY = canvas.getHeight() - screenY;
           //System.out.println(button_center);
           if(screenX >= button_center.x - button_w/2 && screenX <= button_center.x + button_w/2){
               if(screenY >= button_center.y - button_h/2 && screenY <= button_center.y + button_h/2){
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
