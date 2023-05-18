package edu.cornell.gdiac.bubblebound.obstacle;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Shape2D;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Shape;
import edu.cornell.gdiac.bubblebound.GameCanvas;



public class ButtonPrompt extends BoxObstacle {

    float posX;

    float posY;

    String buttonText = null;

    TextureRegion buttonChar = null;

    float canvasWidth;
    float canvasHeight;

    BitmapFont font = null;

    int level;
    TextureRegion buttonTexture;

    public ButtonPrompt(Vector2 location, int level_drawn, TextureRegion buttonTexture){
        super(location.x, location.y);
        this.posX = location.x;
        this.posY = location.y;
        level = level_drawn;
        this.buttonTexture = buttonTexture;
    }

    public ButtonPrompt(float x, float y, int level_drawn, TextureRegion buttonTexture){
        super(x, y);
        this.posX = x;
        this.posY = y;
        level = level_drawn;
        this.buttonTexture = buttonTexture;
    }

    public ButtonPrompt(float x, float y, int level_drawn, TextureRegion buttonTexture, TextureRegion ButtonChar, float canvasWidth, float canvasHeight) {
        super(x, y);
        this.posX = x;
        this.posY = y;
        level = level_drawn;
        this.buttonTexture = buttonTexture;
        this.buttonChar = ButtonChar;
        this.canvasWidth = canvasWidth;
        this.canvasHeight = canvasHeight;
    }

    public int getLevel() {
        return level;
    }


    @Override
    public void draw(GameCanvas canvas){

        float sx = drawScale.x/6;
        float sy = drawScale.y/6;
        //System.out.println("fuck:" + (posY) * drawScale.y);
        if (buttonChar == null) {
            canvas.draw(texture, Color.WHITE, posX, posY, (posX * drawScale.x), (posY * drawScale.y), getAngle(), sx, sy);
        }

        else {
            canvas.draw(texture, Color.WHITE, posX, posY, posX * drawScale.x, posY * drawScale.y, getAngle(), sx, sy);
            int ratio = 60;
            canvas.draw(buttonChar, Color.WHITE, (canvasWidth/1920) + posX-11, (canvasHeight/1080)+posY-51, (400/ratio), ((posY) * drawScale.y)/ratio);
            ////System.out.println("fuck:" + (posX) * drawScale.x);
        }

        drawtimer++;

    }


}
