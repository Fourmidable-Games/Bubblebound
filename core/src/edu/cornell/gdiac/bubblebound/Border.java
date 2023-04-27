package edu.cornell.gdiac.bubblebound;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class Border {
    public float x;
    public float y;
    public boolean vertical;
    public TextureRegion texture;
    public Vector2 scale;

    public Border(float x, float y, boolean v){
        this.x = x;
        this.y = y;
        vertical = v;
    }

    public void setTexture(TextureRegion texture) {
        this.texture = texture;
    }

    public void setDrawScale(Vector2 v){
        scale = v;
    }

    public void draw(GameCanvas canvas){
        float sx = scale.x / 64;
        float sy = scale.y / 64;
        float angle = (vertical) ? 0 : -1.57f; //-90 deg in radians
        float xoffset = (vertical) ? 0 : texture.getRegionWidth() / 2f; //idk how this works but it fixes when it is horizontal
        float offset = (vertical) ? -(texture.getRegionWidth() * sx) / 2f : 0;  //moves to the left half the width
        //idk y this offset works but it centers it onto the grid lines my comments r just guesses
        canvas.draw(texture, Color.WHITE, xoffset, 0, x  * scale.x + offset, y * scale.y, angle, sx, sy);
    }

    public boolean compare(float x, float y, boolean v){
        return this.x == x && this.y == y && vertical == v;
    }

}
