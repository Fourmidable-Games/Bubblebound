package edu.cornell.gdiac.bubblebound;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.graphics.*;

import edu.cornell.gdiac.bubblebound.obstacle.WheelObstacle;
import edu.cornell.gdiac.bubblebound.obstacle.*;
import edu.cornell.gdiac.assets.AssetDirectory;
import edu.cornell.gdiac.util.FilmStrip;

public class Bubble extends WheelObstacle {
    public static enum BubbleType{
        STATIC,
        FLOATING
    }
    private static int last_used_id=-1;
    private final int POP_TIME = 900;
    private final float bubble_speed = 1.45f;
    private BubbleType bubbleType;
   private int id;
    private boolean isGrappled;
    private boolean animate = true;
    private int blink = 0; //blinks every blink frames

    public int pop_timer;
    /** FilmStrip pointer to the texture region */

    public int hang_timer;

    private boolean popped;
    private FilmStrip filmstrip;
    /** The current animation frame of the avatar */
    private int startFrame;
    /** The rotational center of the filmstrip */
    private Vector2 center;


    public Bubble(Vector2 location, float radius, BubbleType type){

        super(location.x,location.y,radius);
        bubbleType = type;
        popped = false;
        if(type == BubbleType.FLOATING){
            pop_timer = POP_TIME;
        }else{
            pop_timer = 400;
        }
        id = last_used_id + 1;
        last_used_id++;
        hang_timer = 50;
    }
    // TODO
    protected int ii = 0;
    protected int counter1 = 0;
    protected final int delay1 = 6; // adjust this value to change the delay

    public void initialize(FilmStrip f) {
        filmstrip = f;
        if (counter1 == 0) { // execute setFrame only when counter reaches 0
            f.setFrame(ii++ % 8);
        }
        counter1 = (counter1 + 1) % delay1; // increment counter and reset to 0 when it reaches delay
        //////system.out.println("strips:" + filmstrip);
    }


    public boolean timedOut(){
        return pop_timer <=0;
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
    private int blink_time = 5;
    public void update() {
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
            if(pop_timer < 150 && pop_timer >= 10){
                blink = pop_timer / 5;
                blink_time = blink / 5;
            }
            if(pop_timer <= 0) popped = true;
        }
        hang_timer--;
        if(statc){
            setLinearVelocity(new Vector2(0, 0));
        }
        if(!statc && hang_timer < 0){
            setVY(bubble_speed * grav);
        }
//        super.update(dt);
    }
    public boolean canRopeTo = false;

    public boolean bounced;

    public void setCanRopeTo(boolean b){
        canRopeTo = b;
    }

    public void outline(GameCanvas canvas){
        canvas.end();
        canvas.shape.begin(ShapeRenderer.ShapeType.Filled);
        canvas.shape.setColor(Color.BLACK);
        canvas.shape.circle(getX()*drawScale.x,getY()*drawScale.y,getRadius()*drawScale.x*1.15f);
        canvas.shape.end();
        canvas.begin();
    }
    @Override
    public void draw(GameCanvas canvas) {
        if(blink == 0 || pop_timer % blink < blink_time) {
            if (texture != null && getD()) {
                float alpha = 1;
                if (bubbleType == BubbleType.FLOATING) {
                    alpha = 1 - (((float) POP_TIME - (float) pop_timer) / ((float) POP_TIME));
                }
                Color gold = new Color(Color.GOLD);
                //gold.a = alpha;

                Color white = new Color(Color.WHITE);
                //white.a = alpha;

                if (canRopeTo) {
                    outline(canvas);
                }
                if (grav == 1) {
                    canvas.draw(texture, gold, origin.x, origin.y, getX() * drawScale.x, getY() * drawScale.y, getAngle(), 2F * getRadius(), 2F * getRadius());
                } else {
                    canvas.draw(texture, white, origin.x, origin.y, getX() * drawScale.x, getY() * drawScale.y, getAngle(), 2F * getRadius(), 2F * getRadius());
                }

            }
        }else{
            ////system.out.println("blink");
        }

    }



}
