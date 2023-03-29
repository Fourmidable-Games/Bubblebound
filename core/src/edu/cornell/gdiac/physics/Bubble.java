package edu.cornell.gdiac.physics;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.physics.box2d.*;

import com.badlogic.gdx.utils.JsonValue;
import edu.cornell.gdiac.assets.AssetDirectory;
import edu.cornell.gdiac.physics.*;
import edu.cornell.gdiac.physics.obstacle.*;
import edu.cornell.gdiac.util.FilmStrip;

public class Bubble extends WheelObstacle{
    public static enum BubbleType{
        STATIC,
        FLOATING
    }
    private static int last_used_id=-1;
    private final int POP_TIME = 400;
    private BubbleType bubbleType;
   private int id;

    private boolean isGrappled;
    private boolean animate = true;

    private int pop_timer;
    /** FilmStrip pointer to the texture region */
    private FilmStrip filmstrip;
    /** The current animation frame of the avatar */
    private int startFrame;
    /** The rotational center of the filmstrip */
    private Vector2 center;


    public Bubble(Vector2 location, float radius, BubbleType type){

        super(location.x,location.y,radius);
        bubbleType = type;
        if(type == BubbleType.FLOATING){
            pop_timer = POP_TIME;
        }else{
            pop_timer = -1;
        }
        id = last_used_id + 1;
        last_used_id++;

    }
    // TODO
    public void initialize(FilmStrip f) {
        filmstrip = f;
        f.setFrame(0);
        //System.out.println("strips:" + filmstrip);
    }
    public boolean isPopped(){
        return pop_timer == 0;
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
    protected int i;
    protected int counter = 0;
    protected final int delay = 50; // adjust this value to change the delay

    public void update(float dt) {
        if (animate) {
            if (filmstrip != null) {
                if (counter == 0) { // execute setFrame only when counter reaches 0
                    int next = (i++) % 8;
                    filmstrip.setFrame(next);
                }
                counter = (counter + 1) % delay; // increment counter and reset to 0 when it reaches delay
            }
        } else {
            if (filmstrip != null) {
                filmstrip.setFrame(0);
            }
        }
        if (bubbleType == BubbleType.FLOATING) {
            pop_timer--;
        }
        super.update(dt);
    }

    @Override
    public void draw(GameCanvas canvas) {
        if (texture != null && getD()) {
            float alpha =1;
            if(bubbleType == BubbleType.FLOATING) {
                alpha = 1 - (((float) POP_TIME - (float) pop_timer) / ((float) POP_TIME));
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
