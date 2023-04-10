package edu.cornell.gdiac.physics;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.physics.box2d.*;

import com.badlogic.gdx.utils.JsonValue;
import com.sun.org.apache.bcel.internal.generic.POP;
import edu.cornell.gdiac.physics.*;
import edu.cornell.gdiac.physics.obstacle.*;

public class Bubble extends WheelObstacle{
    public static enum BubbleType{
        STATIC,
        FLOATING
    }
    private static int last_used_id=-1;

    private Timer pop_timer;
    private final int POP_TIME = 400;
    private BubbleType bubbleType;
   private int id;

    private boolean isGrappled;


    public Bubble(Vector2 location, float radius, BubbleType type){

        super(location.x,location.y,radius);
        bubbleType = type;
        if(type == BubbleType.FLOATING){
            pop_timer = new Timer(POP_TIME);
        }else{
            pop_timer = new Timer(POP_TIME, true);
        }
        id = last_used_id + 1;
        last_used_id++;
    }

    public boolean isPopped(){
        return pop_timer.hasFinished();
    }

    public BubbleType getBubbleType(){
        return bubbleType;
    }

    public int getID(){
        return id;
    }

    public void setGrappled(boolean grappled){
        isGrappled = grappled;
    }

    public boolean isGrappled(){
        return isGrappled;
    }

    public void update(){
        if(bubbleType == BubbleType.FLOATING){
            pop_timer.update();
        }
    }
    @Override
    public void draw(GameCanvas canvas) {
        if (texture != null && getD()) {
            float alpha =1;
            if(bubbleType == BubbleType.FLOATING) {
                alpha = 1 - (((float) pop_timer.getMaxTime() - (float) pop_timer.getTime()) / ((float) pop_timer.getMaxTime()));
            }
            Color gold = new Color(Color.GOLD);
            gold.a =alpha;

            Color white = new Color(Color.WHITE);
            white.a = alpha;

            if(grav == 1) {
                canvas.draw(texture,gold,origin.x,origin.y,getX()*drawScale.x,getY()*drawScale.y,getAngle(), 1.4F*getRadius(), 1.4F*getRadius());
            }else{
                canvas.draw(texture,white,origin.x,origin.y,getX()*drawScale.x,getY()*drawScale.y,getAngle(), 1.4F*getRadius(), 1.4F*getRadius());
            }

        }
    }



}
