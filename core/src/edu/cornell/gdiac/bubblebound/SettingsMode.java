package edu.cornell.gdiac.bubblebound;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.ControllerMapping;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import edu.cornell.gdiac.assets.AssetDirectory;
import edu.cornell.gdiac.util.Controllers;
import edu.cornell.gdiac.util.ScreenListener;
import edu.cornell.gdiac.util.XBoxController;

import java.awt.image.AreaAveragingScaleFilter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class SettingsMode implements Screen, InputProcessor, ControllerListener {
        // There are TWO asset managers.  One to load the loading screen.  The other to load the assets
        /** Internal assets for this loading screen */
        private AssetDirectory internal;
        /** The actual assets to be loaded */
        private AssetDirectory assets;

        /** Background texture for start-up */
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

        public boolean pause = false;

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

        private Cursor defaultCursor;

       public void setDefaultCursor(Cursor defaultCursor) {
           this.defaultCursor = defaultCursor;
       }

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
         * @	The asset directory to load in the background
         * @param canvas 	The game canvas to draw to
         */
        public SettingsMode(GameCanvas canvas) {
            this(canvas, DEFAULT_BUDGET);
        }

        private List<Integer> jump_with_arrows_pair = Arrays.asList(0,2);
        private List<Integer> jump_with_letters_pair = Arrays.asList(1,3);

        private List<List<Integer>> whitelisted_pairs = Arrays.asList(jump_with_arrows_pair,jump_with_letters_pair);


        public static float masterVolume = 1;
        public static float musicVolume = 0.75f;
        public static float soundVolume = 0.75f;
        public int controls = 0; //0 is mouse, 1 is keyboard
        public float getMusicVolume(){
            return InputController.getInstance().audio_levels[0];
        }
        public float getSoundVolume(){
            return InputController.getInstance().audio_levels[1];
        }


       private Texture background;
       private Texture backButton;
       private Texture resetButton;
       private Texture clickedButton;
       private Texture unclickedButton;
       private Texture leftClick;
       private Texture rightClick;
       private Texture slider;
       private Texture slidercircle;
       private Texture unclickbutton;
       private Texture greyedK;
       private Texture greyedM;

       //boolean mouse = true; //idk mouse == true means left click for place

       private Texture[] buttons = new Texture[10]; //TODO:
       private Vector2[] buttonPos = new Vector2[10]; //TODO:
       private Texture mPlace;
       private Texture mAttach;
       private Texture[] inputTextures = new Texture[40];
       public int[] inputs;
       private float music_volume = 0;
       public float[] audio_levels;
        Vector2 backButtonPos;
        Vector2 resetButtonPos;
        Vector2 masterSoundBarPos;
        Vector2 soundBarPos;
        Vector2 musicBarPos;
        Vector2 mAttachPos;
        Vector2 mPlacePos;
        Vector2 greyedKPos;
        Vector2 greyedMPos;

        private boolean masterSoundSliderActive;
        private boolean musicSliderActive;
        private boolean sfxSliderActive;


        private Vector2 createPos(int x, int y){
            return new Vector2(x * scale.x, canvas.getHeight() - (y*scale.y));
        }



        /**
         * Creates a LoadingMode with the default size and position.
         *
         * The budget is the number of milliseconds to spend loading assets each animation
         * frame.  This allows you to do something other than load assets.  An animation
         * frame is ~16 milliseconds. So if the budget is 10, you have 6 milliseconds to
         * do something else.  This is how game companies animate their loading screens.
         *
         *   	The asset directory to load in the background
         * @param canvas 	The game canvas to draw to
         * @param millis The loading budget in milliseconds
         */
        public SettingsMode(GameCanvas canvas, int millis) {
            this.canvas  = canvas;
            budget = millis;
            masterSoundSliderActive = false;
            sfxSliderActive = false;
            musicSliderActive = false;


            // Compute the dimensions from the canvas

            // We need these files loaded immediately
            internal = new AssetDirectory( "settings.json" );
            internal.loadAssets();
            internal.finishLoading();

            // Load the next two images immediately.
            background = internal.getEntry( "background", Texture.class );
            background.setFilter( Texture.TextureFilter.Linear, Texture.TextureFilter.Linear );
            resize(canvas.getWidth(),canvas.getHeight());
            //load the loading theme immediately
//            loadingMusic = internal.getEntry("menuscreen", Sound.class);
//            loadingMusicId = loadingMusic.play(musicVolume);
//            loadingMusic.setLooping(loadingMusicId, true);
            //loadingMusic.play();
            backButton = internal.getEntry("backbutton", Texture.class);
            resetButton = internal.getEntry("reset", Texture.class);
            unclickedButton = internal.getEntry("unclickedbutton", Texture.class);
            clickedButton = internal.getEntry("clickedbutton", Texture.class);
            leftClick = internal.getEntry("leftclick", Texture.class);
            rightClick = internal.getEntry("rightclick", Texture.class);
            slider = internal.getEntry("slider", Texture.class);
            slidercircle = internal.getEntry("slidercircle", Texture.class);
            greyedK = internal.getEntry("greyedK",Texture.class);
            greyedM = internal.getEntry("greyedK", Texture.class);

            inputs = InputController.getInstance().buttons;
            audio_levels = InputController.getInstance().audio_levels;
            for(int i = 0; i < buttons.length; i++){
                buttons[i] = unclickedButton;
            }
            for(int i = 0; i < inputTextures.length; i++){
                inputTextures[i] = internal.getEntry("input" + i, Texture.class);
            }
            buttonPos[0] = createPos(383, 563);
            buttonPos[1] = createPos(383, 645);
            buttonPos[2] = createPos(568, 563);; //JumpDownP
            buttonPos[3] = createPos(568, 645);
            buttonPos[4] = createPos(383, 783);
            buttonPos[5] = createPos(383, 865);
            buttonPos[6] = createPos(568, 783);
            buttonPos[7] = createPos(568, 865);
            buttonPos[8] = createPos(988, 568);
            buttonPos[9] = createPos(1390, 568);


            backButtonPos = createPos(84, 41);
            resetButtonPos = createPos(800, 41);
            masterSoundBarPos = createPos(485, 187);
            musicBarPos = createPos(485, 271);
            soundBarPos = createPos(485, 356);
            mAttachPos = createPos(878, 886);
            mPlacePos = createPos(1271, 886);
            greyedKPos = createPos(789, 527);
            greyedMPos = createPos(789, 838);




            pressState = 0;

            Gdx.input.setInputProcessor( this );

            // Let ANY connected controller start the game.
            for (XBoxController controller : Controllers.get().getXBoxControllers()) {
                controller.addListener( this );
            }

            // Start loading the real assets
        }

        public void reset(){
            InputController input = InputController.getInstance();
            int[] temp = {Input.Keys.UP, Input.Keys.W, Input.Keys.DOWN, Input.Keys.S, Input.Keys.LEFT, Input.Keys.A, Input.Keys.RIGHT, Input.Keys.D, Input.Keys.J, Input.Keys.K};
            input.buttons = temp;
            inputs = temp;
            input.times = new float[20];
            masterVolume = 1;
            musicVolume = 0.75f;
            soundVolume = 0.75f;
            float[] temp_audio_levels = {music_volume*masterVolume, soundVolume*masterVolume};
            input.audio_levels = temp_audio_levels;
            input.mouse = true;
            input.controlMapping = InputController.ControlMapping.MOUSE;
        }


        public int getInput(){
            for(int i = 19; i <= 22; i++){ //0-3 are up, down, left, right
                if(Gdx.input.isKeyPressed(i)){
                    return i;
                }
            }
            for(int i = 29; i <= 54; i++){ //4 - 29
                if(Gdx.input.isKeyPressed(i)){
                    return i;
                }
            }
            for(int i = 7; i <= 16; i++){ // 30-39
                if(Gdx.input.isKeyPressed(i)){
                    return i;
                }
            }
            return -1;
        }

        public int getTexture(int x){
            if(x <= 16){ // 0-9 are 30 - 39
                return x + 23;
            }
            if(x < 23){
                return x - 19; //up,down,left,right return 0-3
            }
            return x - 25; //alphabet return 4-29
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
            Gdx.input.setInputProcessor( this );
            float new_music_volume = InputController.getInstance().audio_levels[0];
            ////////System.out.println(new_music_volume);
            if(new_music_volume != music_volume){
                loadingMusic.setVolume(loadingMusicId,new_music_volume);
                ////////System.out.println("UPDATED");
                music_volume = new_music_volume;
            }
            if(InputController.getInstance().mouse){
                mAttach = leftClick;
                mPlace = rightClick;
            }else{
                mAttach = rightClick;
                mPlace = leftClick;
            }
            if(pressState >= 20){

                int in = getInput();
                for(int i = 0; i < inputs.length; i++){
                    if(i == pressState - 20) {
                        continue;
                    }

                    if(in == inputs[i]){
                        boolean bypass = false;
                        for(List<Integer> pair: whitelisted_pairs){
                            if (pair.contains(i) && pair.contains(pressState-20)){
                                bypass = true;
                            }
                        }
                        if(bypass){

                            continue;
                        }
                        return;
                    }
                }
                if(in != -1){
                    inputs[pressState - 20] = in;
                    buttons[pressState - 20] = unclickedButton;
                    InputController.getInstance().buttons = inputs;
                    pressState = 0;

                }
            }
            if(masterSoundSliderActive) {
                int screenX = Gdx.input.getX();
                masterSoundSliderActive = true;
                if (pressedButton(screenX, slider, masterSoundBarPos)) {
                    float temp = screenX - masterSoundBarPos.x;
                    masterVolume = temp / (slider.getWidth() * scale.x);

                }else{
                    masterVolume = (sliderOutOfBoundDirection(screenX,slider,masterSoundBarPos) < 0) ? 0 : 1;
                }
                audio_levels[0] = masterVolume * musicVolume;
            }
            if(musicSliderActive) {
                int screenX = Gdx.input.getX();
                musicSliderActive = true;
                if (pressedButton(screenX, slider, musicBarPos)) {
                    float temp = screenX - musicBarPos.x;
                    musicVolume = temp / (slider.getWidth() * scale.x);
                }else{
                    musicVolume = (sliderOutOfBoundDirection(screenX,slider,musicBarPos) < 0) ? 0 : 1;
                }
                audio_levels[0] = masterVolume * musicVolume;
            }
            if(sfxSliderActive) {
                int screenX = Gdx.input.getX();
                if (pressedButton(screenX, slider, soundBarPos)) {
                    float temp = screenX - soundBarPos.x;
                    soundVolume = temp / (slider.getWidth() * scale.x);
                }else{
                    soundVolume = (sliderOutOfBoundDirection(screenX,slider,soundBarPos) < 0) ? 0 : 1;
                }
                audio_levels[1] = soundVolume * masterVolume;
            }
            InputController.getInstance().audio_levels = audio_levels;
        }
        public void startMusic(){
            music_volume = InputController.getInstance().audio_levels[0];
            loadingMusicId = loadingMusic.loop(music_volume);
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

            Color mColor = Color.WHITE;
            Color kColor = Color.WHITE;
            if(InputController.getInstance().isMouseControlls()){
                kColor = Color.GRAY;
                canvas.draw(greyedK, Color.WHITE, 0, greyedK.getHeight(), greyedKPos.x, greyedKPos.y, 0, scale.x, scale.y);
            }else{
                mColor = Color.GRAY;
                canvas.draw(greyedM, Color.WHITE, 0, greyedM.getHeight(), greyedMPos.x, greyedMPos.y, 0, scale.x, scale.y);
            }


            canvas.draw(backButton, Color.WHITE, 0, backButton.getHeight(),
                    backButtonPos.x, backButtonPos.y, 0, scale.x, scale.y);

            canvas.draw(resetButton, Color.WHITE, 0, resetButton.getHeight(), resetButtonPos.x, resetButtonPos.y,0,scale.x,scale.y);

            float temp = masterSoundBarPos.x + (masterVolume * slider.getWidth() * scale.x) - (slidercircle.getWidth() * scale.x /  2f);
            canvas.draw(slider, Color.WHITE, 0, slider.getHeight(),
                    masterSoundBarPos.x, masterSoundBarPos.y, 0, scale.x, scale.y);
            canvas.draw(slidercircle, Color.WHITE, 0, slidercircle.getHeight() /2f,
                    temp, masterSoundBarPos.y - (slider.getHeight() * scale.y / 2f), 0, scale.x, scale.y);

            temp = soundBarPos.x + (soundVolume * slider.getWidth() * scale.x) - (slidercircle.getWidth() * scale.x /  2f);
            canvas.draw(slider, Color.WHITE, 0, slider.getHeight(),
                    soundBarPos.x, soundBarPos.y, 0, scale.x, scale.y);
            canvas.draw(slidercircle, Color.WHITE, 0, slidercircle.getHeight() /2f,
                    temp, soundBarPos.y - (slider.getHeight() * scale.y / 2f), 0, scale.x, scale.y);

            temp = musicBarPos.x + (musicVolume * slider.getWidth() * scale.x) - (slidercircle.getWidth() * scale.x /  2f);
            canvas.draw(slider, Color.WHITE, 0, slider.getHeight(),
                    musicBarPos.x, musicBarPos.y, 0, scale.x, scale.y);
            canvas.draw(slidercircle, Color.WHITE, 0, slidercircle.getHeight() /2f,
                    temp, musicBarPos.y - (slider.getHeight() * scale.y / 2f), 0, scale.x, scale.y);


            canvas.draw(mAttach, mColor, 0, mAttach.getHeight(),
                    mAttachPos.x, mAttachPos.y, 0, scale.x, scale.y);
            canvas.draw(mPlace, mColor, 0, mPlace.getHeight(),
                    mPlacePos.x, mPlacePos.y, 0, scale.x, scale.y);


            for(int i = 0; i < buttons.length; i++){

                if(i == 8 || i == 9){
                    canvas.draw(buttons[i], kColor, 0, buttons[i].getHeight(), buttonPos[i].x, buttonPos[i].y, 0, scale.x, scale.y);

                    if(buttons[i] == unclickedButton) {
                        Texture inputT = inputTextures[getTexture(inputs[i])];
                        canvas.draw(inputT, kColor, inputT.getWidth() / 2f, inputT.getHeight() / 2f,
                                buttonPos[i].x + (unclickedButton.getWidth() * scale.x / 2f ), buttonPos[i].y - (unclickedButton.getHeight() * scale.y / 2f), 0, scale.x, scale.y);
                    }
                }else {


                    canvas.draw(buttons[i], Color.WHITE, 0, buttons[i].getHeight(), buttonPos[i].x, buttonPos[i].y, 0, scale.x, scale.y);

                    if (buttons[i] == unclickedButton) {
                        Texture inputT = inputTextures[getTexture(inputs[i])];
                        canvas.draw(inputT, Color.WHITE, inputT.getWidth() / 2f, inputT.getHeight() / 2f,
                                buttonPos[i].x + (unclickedButton.getWidth() * scale.x / 2f), buttonPos[i].y - (unclickedButton.getHeight() * scale.y / 2f), 0, scale.x, scale.y);
                    }
                }
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
                    int x = (pause) ? -1 : 0;
                    listener.exitScreen(this, x);
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
            Gdx.graphics.setCursor(defaultCursor);
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

        public boolean pressedButton(int screenX, int screenY, Texture texture, Vector2 button_pos){
            float button_w = texture.getWidth() * scale.x;
            float button_h = texture.getHeight() * scale.y;
            screenY = canvas.getHeight() - screenY;
            ////////////System.out.println(button_center);
            if(screenX >= button_pos.x && screenX <= button_pos.x + button_w){
                if(screenY >= button_pos.y - button_h && screenY <= button_pos.y){
                   return true;
                }
            }
            return false;
        }

       public boolean pressedSlider(int screenX, int screenY, Texture texture, Vector2 button_pos){
           float button_w = texture.getWidth() * scale.x;
           float button_h = texture.getHeight() * scale.y;
           screenY = canvas.getHeight() - screenY;
           ////////////System.out.println(button_center);
           if(screenX >= button_pos.x - button_h && screenX <= button_pos.x + button_w + button_h){
               if(screenY >= button_pos.y - button_h -button_h/2 && screenY <= button_pos.y + button_h/2){
                   return true;
               }
           }
           return false;
       }
       public boolean pressedButton(int screenX, Texture texture, Vector2 button_pos){
           float button_w = texture.getWidth() * scale.x;
           if(screenX >= button_pos.x && screenX <= button_pos.x + button_w){
                   return true;
           }
           return false;
       }

       public int sliderOutOfBoundDirection(int screenX, Texture texture, Vector2 button_pos){
           float button_w = texture.getWidth() * scale.x;
           if(screenX < button_pos.x){
               return -1;
           }else if(screenX > button_pos.x + button_w){
               return 1;
           }
           return 0;
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
                ////////////System.out.println("bad howdy");
                return true;
            }

            // Flip to match graphics coordinates
            //screenY = heightY-screenY;

            if(pressedButton(screenX, screenY, backButton, backButtonPos)){
                ////////////System.out.println("BACKK");
                pressState = 1;
            }
            if(pressedButton(screenX, screenY, resetButton, resetButtonPos)){
                ////////////System.out.println("BACKK");
                reset();
            }
            if(pressedSlider(screenX, screenY, slider, masterSoundBarPos)){
                masterSoundSliderActive = true;
            }
            if(pressedSlider(screenX, screenY, slider, musicBarPos)){
                musicSliderActive = true;
            }
            if(pressedSlider(screenX, screenY, slider, soundBarPos)){
                sfxSliderActive = true;
            }
            if(pressedButton(screenX, screenY, greyedK, greyedKPos)){
                InputController.getInstance().controlMapping = InputController.ControlMapping.KEYBOARD;
            }
            if(pressedButton(screenX, screenY, greyedM, greyedMPos)){
                InputController.getInstance().controlMapping = InputController.ControlMapping.MOUSE;
            }

            if(pressState < 20) {
                for (int i = 0; i < buttons.length; i++) {
                    if (pressedButton(screenX, screenY, buttons[i], buttonPos[i])) {
                        buttons[i] = clickedButton;
                        pressState = i + 20;
                    }
                }
            }

            if(pressedButton(screenX, screenY, mAttach, mAttachPos) || pressedButton(screenX, screenY, mPlace, mPlacePos)){
                InputController in = InputController.getInstance();
                in.mouse = !in.mouse;
            }

            // TODO: Fix scaling
            // Play button is a circle.


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
            if(musicSliderActive){
                musicSliderActive = false;
            }
            if(sfxSliderActive){
                sfxSliderActive = false;
            }
            if(masterSoundSliderActive){
                masterSoundSliderActive = false;
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
