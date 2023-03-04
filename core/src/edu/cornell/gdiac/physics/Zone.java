package edu.cornell.gdiac.physics;

public class Zone {

    public float xpos;
    public float ypos;

    public float width;
    public float height;

    public float grav;

    public Zone(float xp, float yp, float w, float h, float gravity){
        xpos = xp;
        ypos = yp;
        width = w;
        height = h;
        grav = gravity;
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

}
