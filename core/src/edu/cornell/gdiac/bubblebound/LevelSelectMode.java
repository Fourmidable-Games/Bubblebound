package edu.cornell.gdiac.bubblebound;


import com.badlogic.gdx.Gdx;
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

public class LevelSelectMode implements Screen, InputProcessor, ControllerListener {
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

    private float volume = 1.0f;
    private long music;
    public void setVolume(float f){
        volume = f;
    }

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
    public void gatherAssets(AssetDirectory directory){
        System.out.println("AAAAAAAAAAAAAAAA");
        background = directory.getEntry( "phase0background", Texture.class );
        background.setFilter( Texture.TextureFilter.Linear, Texture.TextureFilter.Linear );
        for(int i = 0; i < 4; i++){
            for(int j = 0; j < 5; j++){
                phaseLevels[i][j] = directory.getEntry("level" + (i+1) + "-" + (j+1), Texture.class);
            }
        }
        for(int i = 0; i < 5; i++){
            phasebackgrounds[i] = directory.getEntry("phase" + i + "background", Texture.class);
        }
        backButton = directory.getEntry("backbutton", Texture.class);
        for(int i = 1; i <= 4; i++){
            phases[i - 1] = directory.getEntry("phase" + i, Texture.class);
        }
        for(int i = 1; i <= 4; i++){
            lightphases[i-1] = directory.getEntry("lightphase" + i, Texture.class);
        }
    }

    /**
     * Creates a LoadingMode with the default budget, size and position.
     *
     *
     * @param canvas 	The game canvas to draw to
     */
    public LevelSelectMode(GameCanvas canvas) {
        this(canvas, DEFAULT_BUDGET);
    }

    private int phase = 0; //represents which lvl select screen (0 for main)
    private int hovered = 0;
    private GameCanvas canvas2;

    private Texture backButton;
    private Texture phasebackground;
    Vector2 backButtonPos;

    private Vector2 createPos(int x, int y){
        return new Vector2(x * scale.x, canvas.getHeight() - (y*scale.y));
    }

    private Texture[] phasebackgrounds = new Texture[5];
    private Texture[][] phaseLevels = new Texture[4][5];
    private Vector2[][] phaseLevelsPos = new Vector2[4][5];
    private Texture[] phases = new Texture[4];
    private Texture[] lightphases = new Texture[4];
    private Vector2[] phasesPos = new Vector2[4];

    private Cursor defaultCursor;

    public void setDefaultCursor(Cursor defaultCursor){
        this.defaultCursor = defaultCursor;
    }

    /**
     * Creates a LoadingMode with the default size and position.
     *
     * The budget is the number of milliseconds to spend loading assets each animation
     * frame.  This allows you to do something other than load assets.  An animation
     * frame is ~16 milliseconds. So if the budget is 10, you have 6 milliseconds to
     * do something else.  This is how game companies animate their loading screens.
     *
     *
     * @param canvas 	The game canvas to draw to
     * @param millis The loading budget in milliseconds
     */
    public LevelSelectMode(GameCanvas canvas, int millis) {
        canvas2 = canvas;
        this.canvas  = canvas;
        budget = millis;
        resize(canvas.getWidth(), canvas.getHeight());
        // Compute the dimensions from the canvas


        // We need these files loaded immediately


        //load the loading theme immediately


        //int xoffset =  phases[0].getWidth()/ 2;
        int xoffset = 406/2;
        //int yoffset = phases[0].getHeight() /2;
        int yoffset = 504/2;
        phasesPos[0] = createPos(71 + xoffset, 83 + yoffset);//represents the center
        phasesPos[1] = createPos(529 + xoffset, 490 + yoffset);
        phasesPos[2] = createPos(986 + xoffset, 83 + yoffset);
        phasesPos[3] = createPos(1443 + xoffset, 490 + yoffset);

        //xoffset = phaseLevels[0][0].getWidth() / 2;
        //yoffset = phaseLevels[0][0].getHeight() / 2;
        xoffset = 194/2;
        yoffset = 194/2;

        phaseLevelsPos[0][0] = createPos(174 +xoffset, 131 +yoffset);
        phaseLevelsPos[0][1] = createPos(332 +xoffset, 518 +yoffset);
        phaseLevelsPos[0][2] = createPos(580 +xoffset, 780 +yoffset);
        phaseLevelsPos[0][3] = createPos(1098 +xoffset, 535 +yoffset);
        phaseLevelsPos[0][4] = createPos(1543 +xoffset, 194 +yoffset);

        phaseLevelsPos[1][0] = createPos(124 +xoffset, 126 +yoffset);
        phaseLevelsPos[1][1] = createPos(148 +xoffset, 692 +yoffset);
        phaseLevelsPos[1][2] = createPos(733 +xoffset, 540 +yoffset);
        phaseLevelsPos[1][3] = createPos(1094 +xoffset, 228 +yoffset);
        phaseLevelsPos[1][4] = createPos(1615 +xoffset, 34 +yoffset);

        phaseLevelsPos[2][0] = createPos(1484 +xoffset, 500 +yoffset);
        phaseLevelsPos[2][1] = createPos(1308 +xoffset, 160 +yoffset);
        phaseLevelsPos[2][2] = createPos(742 +xoffset, 443 +yoffset);
        phaseLevelsPos[2][3] = createPos(281 +xoffset, 165 +yoffset);
        phaseLevelsPos[2][4] = createPos(160 +xoffset, 702 +yoffset);

        phaseLevelsPos[3][0] = createPos(1634 +xoffset, 89 +yoffset);
        phaseLevelsPos[3][1] = createPos(1441 +xoffset, 575 +yoffset);
        phaseLevelsPos[3][2] = createPos(908 +xoffset, 854 +yoffset);
        phaseLevelsPos[3][3] = createPos(480 +xoffset, 157 +yoffset);
        phaseLevelsPos[3][4] = createPos(160 +xoffset, 702 +yoffset);


        backButtonPos = createPos(96, 84);

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
        int x = Gdx.input.getX();
        int y = Gdx.input.getY();
        hovered = -1;
        for(int i = 0; i < phasesPos.length; i++){
            if(pressedCircle(x, y, phases[i], phasesPos[i])){
                hovered = i;
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
        float sx = ((float) canvas.getWidth()) / ((float) background.getWidth());
        float sy = ((float) canvas.getHeight()) / ((float) background.getHeight());
        canvas.draw(phasebackgrounds[phase], Color.WHITE, 0, 0, 0, 0, 0, sx, sy);
        canvas.draw(backButton, Color.WHITE, 0, backButton.getHeight(),
                backButtonPos.x, backButtonPos.y, 0, scale.x, scale.y);

        if(phase == 0) {
            for (int i = 0; i < phasesPos.length; i++) {
                Texture text = (i == hovered) ? lightphases[i] : phases[i];
                canvas.draw(text, Color.WHITE, text.getWidth() / 2f, text.getHeight() / 2f, phasesPos[i].x, phasesPos[i].y, 0, scale.x, scale.y);
            }
        }else if(phase >= 1){
            int p = phase - 1;
            Texture[] texts = phaseLevels[p];
            Vector2[] pos = phaseLevelsPos[p];
            for(int i = 0; i < texts.length; i++){
                canvas.draw(texts[i], Color.WHITE, texts[i].getWidth() / 2f, texts[i].getHeight() / 2f, pos[i].x, pos[i].y, 0, scale.x, scale.y);
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
                listener.exitScreen(this, -1);
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

    /**
     * Sets the ScreenListener for this mode
     *
     * The ScreenListener will respond to requests to quit.
     */
    public void setScreenListener(ScreenListener listener) {
        this.listener = listener;
    }

    public int chosenlevel = -1;

    public boolean pressedButton(int screenX, int screenY, Texture texture, Vector2 button_pos){
        float button_w = texture.getWidth() * scale.x;
        float button_h = texture.getHeight() * scale.y;
        screenY = canvas.getHeight() - screenY;
        //System.out.println(button_center);
        if(screenX >= button_pos.x && screenX <= button_pos.x + button_w){
            if(screenY >= button_pos.y - button_h && screenY <= button_pos.y){
                return true;
            }
        }
        return false;
    }

    public boolean pressedCircle(int x, int y, Texture texture, Vector2 pos){
        y = canvas.getHeight() - y;
        return (pos.dst(x, y) <= texture.getWidth() * scale.x / 2f);
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
        if(pressedButton(screenX, screenY, backButton, backButtonPos)){
            if(phase == 0) {
                pressState = 1;
            }else{
                phase = 0;
            }
        }
        if(phase == 0){
            for(int i = 0; i < phasesPos.length; i++){
                if(pressedCircle(screenX, screenY, phases[i], phasesPos[i])){
                    phase = i + 1;
                }
            }
        }else{
            int p = phase - 1;
            Texture[] texts = phaseLevels[p];
            Vector2[] pos = phaseLevelsPos[p];
            for(int i = 0; i < texts.length; i++){
                if(pressedCircle(screenX, screenY, texts[i], pos[i])){
                   listener.exitScreen(this, 5 * (phase - 1) + i + 1); //which lvl
                }
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
