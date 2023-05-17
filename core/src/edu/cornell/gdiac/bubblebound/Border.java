package edu.cornell.gdiac.bubblebound;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import edu.cornell.gdiac.util.FilmStrip;

public class Border {
    public float x;
    public float y;
    private int borderStripNum;
    public boolean vertical;
    public TextureRegion texture;
    public Vector2 scale;
    private FilmStrip filmstrip;

    public Border(float x, float y, boolean v, int borderStripNum){
        this.x = x;
        this.y = y;
        this.borderStripNum = borderStripNum;
        vertical = v;
    }

    public int getBorderStripNum(){
        return borderStripNum;
    }


    public void initialize(FilmStrip f){
        filmstrip = f;
        f.setFrame(0);

    }

    protected int i;
    protected int counter = 0;
    protected final int delay = 10; // adjust this value to change the delay
    public void update() {
        if (filmstrip != null) {
            if (counter == 0) { // execute setFrame only when counter reaches 0
                int next = (i++) % 8;
                filmstrip.setFrame(next);
            }
            counter = (counter + 1) % delay; // increment counter and reset to 0 when it reaches delay
        }
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