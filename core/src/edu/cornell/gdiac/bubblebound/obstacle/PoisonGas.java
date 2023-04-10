package edu.cornell.gdiac.bubblebound.obstacle;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import edu.cornell.gdiac.bubblebound.GameCanvas;

public class PoisonGas extends BoxObstacle{

    public static float width = 1f;
    public static float height = 1f;
    public int timer = 0;
    public boolean faded = false;




    public PoisonGas(float x, float y) {
        super(x, y, 1, 1);
        this.setSensor(true);
        this.setName("gas");
        faded = false;
        setBodyType(BodyDef.BodyType.StaticBody);
    }

    public void setFade(boolean val){
        timer = 200;
    }

    public void update(){
        if(timer < 0){
            faded = true;
        }
        timer--;
    }

    @Override
    public void draw(GameCanvas canvas){
        if (texture != null) {
            if(grav == 1) {
                canvas.draw(texture, new Color(255,255,255,0.4f), origin.x, origin.y, getX() * drawScale.x, getY() * drawScale.x, getAngle(), 0.5F, 0.5F);
            }else{
                canvas.draw(texture, new Color(255,255,255,0.4f), origin.x, origin.y, getX() * drawScale.x, getY() * drawScale.x, getAngle(), 0.5F, 0.5F);
            }
        }
    }




}
