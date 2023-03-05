package edu.cornell.gdiac.physics;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public class Zone {

    public float xpos;
    public float ypos;

    public float width;
    public float height;

    public Vector2 scale;
    public float grav;

    public Zone(float xp, float yp, float w, float h, float gravity, Vector2 s){
        xpos = xp;
        ypos = yp;
        width = w;
        height = h;
        grav = gravity;
        scale = s;
    }

    public float getGrav(){
        return grav;
    }

    public boolean inBounds(float x, float y){
        if(x > xpos && x < xpos + width){
            if(y > ypos && y < ypos + height){
                return true;
            }
        }
        return false;
    }

    public void sDraw(GameCanvas canvas){
        canvas.shape.rect(xpos*scale.x, ypos*scale.y, width*scale.x, height*scale.y);
    }

}
