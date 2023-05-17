package edu.cornell.gdiac.bubblebound.obstacle;

import com.badlogic.gdx.graphics.Color;
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

    public ButtonPrompt(float x, float y, int level_drawn, TextureRegion buttonTexture, BitmapFont font, String buttonText) {
        super(x, y);
        this.posX = x;
        this.posY = y;
        level = level_drawn;
        this.buttonTexture = buttonTexture;
        this.font = font;
        this.buttonText = buttonText;
    }

    public int getLevel() {
        return level;
    }


    @Override
    public void draw(GameCanvas canvas){

        float sx = drawScale.x/6;
        float sy = drawScale.y/6;

        if (buttonText == null) {
            canvas.draw(texture, Color.WHITE, posX, posY, posX * drawScale.x, posY * drawScale.y, getAngle(), sx, sy);
        }

        else {
            canvas.draw(texture, Color.WHITE, posX, posY, posX * drawScale.x, posY * drawScale.y, getAngle(), sx, sy);
            canvas.drawText(buttonText, font, posX, posY);
        }

        drawtimer++;

    }


}
