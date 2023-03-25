package edu.cornell.gdiac.physics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public class Zone {

    public float xpos;
    public float ypos;
    public float xmove;
    public float ymove;
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
        xmove = 0;
        ymove = 0;
    }

    public void move(){
        xpos += xmove;
        ypos += ymove;
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
    public void drawBackground(Texture image, GameCanvas canvas, float xcam){
        int y = image.getHeight() - (int)(ypos * scale.y) - (int)(height * scale.y); //finds y coord
        int x = canvas.wrapX(xcam, image.getWidth()) + (int)(xpos*scale.x); //find parallaxed x coord
        TextureRegion temp = new TextureRegion(image, x, y,(int)(width*scale.x), (int)(height * scale.y)); //select only needed part of image
        canvas.draw(temp, Color.WHITE, xpos * scale.x, ypos * scale.y, temp.getRegionWidth(), temp.getRegionHeight());
    }

    public void setMove(float x, float y){
        xmove = x;
        ymove = y;
    }

}
