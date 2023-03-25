package edu.cornell.gdiac.physics;

import com.badlogic.gdx.graphics.Color;
import edu.cornell.gdiac.physics.GameCanvas;
import edu.cornell.gdiac.physics.obstacle.CapsuleObstacle;

import javax.swing.*;

public class Enemy extends CapsuleObstacle { //capsule not working for some reasons

    private static float SPEED = 1f;
    private boolean faceRight = true;
    public float leftBound;
    public float rightBound;


    public Enemy(float x, float y, float width, float height){
        super(x, y, width, height);
        setDensity(100f);
        setFriction(0f);
        setFixedRotation(true);
        leftBound = x - 1;
        rightBound = x + 1;
        setVX(SPEED);
        setName("enemy");
    }

    public void setBounds(float leftBounds, float rightBounds){
        leftBound = leftBounds;
        rightBound = rightBounds;
    }

    public void update(){
        if(grav == -1){
            setGravityScale(-1f);
        }else{
            setGravityScale(1f);
        }
        if(faceRight){
            if(getPosition().x >= rightBound){
                setVX(-SPEED);
                faceRight = false;
            }else{
                setVX(SPEED);
            }
        }else{
            if(getPosition().x <= leftBound){
                setVX(SPEED);
                faceRight = true;
            }else{
                setVX(-SPEED);
            }
        }
    }


    /**
     * Draws the physics object.
     *
     * @param canvas Drawing context
     */
    public void draw(GameCanvas canvas) {

        float x = getWidth()*drawScale.x / 2;
        float y = getHeight()*drawScale.y / 2;

        float effect = faceRight ? 1.0f : -1.0f;;
        float upside = (grav == -1) ? -1.0f : 1.0f;
        canvas.draw(texture, Color.WHITE,origin.x,origin.y,getX()*drawScale.x,getY()*drawScale.y,getAngle(),effect,upside);




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
