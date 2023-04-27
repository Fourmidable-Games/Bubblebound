package edu.cornell.gdiac.bubblebound;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import edu.cornell.gdiac.bubblebound.obstacle.CapsuleObstacle;
import edu.cornell.gdiac.util.FilmStrip;

public class Enemy extends CapsuleObstacle { //capsule not working for some reasons

    private static float SPEED = 2.5f;
    private boolean faceRight = true;
    public float leftBound;
    public float rightBound;
    private boolean animate = true;
    private FilmStrip filmstrip;
    private int start;

    public Enemy(float x, float y, float width, float height){
        super(x, y, 1, 1.5f);
        start = (int)x;
        setDensity(100f);
        setFriction(0f);
        setFixedRotation(true);
        leftBound = x - 1;
        rightBound = x + 1;
        setVX(SPEED);
        setName("enemy");
    }

    protected int ii = 0;
    protected int counter1 = 0;
    protected final int delay1 = 6; // adjust this value to change the delay
    public void initialize(FilmStrip f) {
        filmstrip = f;
        if (counter1 == 0) { // execute setFrame only when counter reaches 0
            f.setFrame(ii++ % 9);
        }
        counter1 = (counter1 + 1) % delay1; // increment counter and reset to 0 when it reaches delay
    }

    public void setBounds(float leftDisplacement, float rightDisplacement){
        leftBound = start - leftDisplacement;
        rightBound = start + rightDisplacement;
    }

    public boolean checkSpeedUp(DudeModel avatar){
        Vector2 diff = avatar.getPosition().sub(getPosition());
        if(faceRight && diff.x > 0 && Math.abs(diff.y) < 3 && diff.x < 10){
            return true;
        }else if(!faceRight && diff.x < 0 && Math.abs(diff.y) < 3 && diff.x > -10){
            return true;
        }

        return false;
    }

    protected int i;
    protected int counter = 0;
    protected final int delay = 50; // adjust this value to change the delay
    public void update(DudeModel avatar){
        if (animate) {
            if (filmstrip != null) {
                if (counter == 0) { // execute setFrame only when counter reaches 0
                    int next = (i++) % 9;
                    filmstrip.setFrame(next);
                }
                counter = (counter + 1) % delay; // increment counter and reset to 0 when it reaches delay
            }
        } else {
            if (filmstrip != null) {
                filmstrip.setFrame(0);
            }
        }

        float multiplier = (checkSpeedUp(avatar)) ? 2f : 1f;
        if(grav == -1){
            setGravityScale(-1f);
        }else{
            setGravityScale(1f);
        }
        if(faceRight){
            if(getPosition().x >= rightBound){
                setVX(-SPEED * multiplier);
                faceRight = false;
            }else{
                setVX(SPEED * multiplier);
            }
        }else{
            if(getPosition().x <= leftBound){
                setVX(SPEED * multiplier);
                faceRight = true;
            }else{
                setVX(-SPEED * multiplier);
            }
        }
    }


    /**
     * Draws the physics object.
     *
     * @param canvas Drawing context
     */
    public void draw(GameCanvas canvas) {

        float sx = drawScale.x / 64;
        float sy = drawScale.y / 64;


        float effect = faceRight ? 1.0f : -1.0f;;
        float upside = (grav == -1) ? -1.5f : 1.5f;
        //////system.out.println(texture);

        canvas.draw(texture, Color.WHITE,origin.x,origin.y,getX()*drawScale.x,getY()*drawScale.y,getAngle(),sx * effect,sy * upside);




//		float effect = faceRight ? 1.0f : -1.0f;
//		canvas.draw(texture,Color.WHITE,origin.x,origin.y,getX()*drawScale.x,getY()*drawScale.y,getAngle(),effect,1.0f);
    }

    @Override
    public void sdraw(GameCanvas canvas){
        float x = getWidth()*drawScale.x/2;
        float y = getHeight()*drawScale.y/2;
        canvas.shape.setColor(Color.RED);
            canvas.shape.rect(getX()*drawScale.x-x,getY()*drawScale.y-y,
                    getWidth()*drawScale.x,getHeight()*drawScale.y);




    }

    /**
     * Draws the outline of the physics body.
     *
     * This method can be helpful for understanding issues with collisions.
     *
     * @param canvas Drawing context
     */
    public void drawDebug(GameCanvas canvas) {
        super.drawDebug(canvas);
        canvas.drawPhysics(shape,Color.RED,getX(),getY(),getAngle(),drawScale.x,drawScale.y);
    }







}
