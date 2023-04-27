package edu.cornell.gdiac.bubblebound.obstacle;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import edu.cornell.gdiac.bubblebound.GameCanvas;

public class ProjEnemy extends BoxObstacle{

    //implies 9x9 with flower moving thing at bottom middle for upwards rotation

    int shoottimer = 0; //shoot every 100 frames;
    int shootcooldown = 200;
    boolean shooting = false;
    private int rotation;

    public int getRotation(){
        return rotation;
    }
    public ProjEnemy(float x, float y, int r) {
        this(x, y, 1, 1, r);
    }

    public ProjEnemy(float x, float y, float width, float height, int rotation) {
        super(x, y, 1, 1);
        this.setName("projenemy");
        this.setBodyType(BodyDef.BodyType.StaticBody);
        this.rotation = rotation;
        fixture.filter.groupIndex = -1;

        setSensor(true);

    }

    public void activate(){
        shooting = true;
    }

    public void deactivate(){
        shooting = false;
    }

    public boolean update(){
//        //system.out.println(shooting);
//        //system.out.println(shoottimer);
        if(shooting && shoottimer == 0){
            shoottimer++;
            return true;
        }
        if(shoottimer > 0 && shoottimer < shootcooldown){
            shoottimer++;
        }else{
            shoottimer = 0;
        }
        return false;
    }


    @Override
    public void draw(GameCanvas canvas){
        if(texture != null){
            float ox = origin.x;
            float oy = origin.y;
            float angle = 0;
            switch (rotation){
                case 0:
                    ox += 1.5;
                    oy += 0.5;
                    angle = 0;
                    break;
                case 1:
                    ox += 0.5;
                    oy += 1.5;
                    angle = -90;
                    break;
                case 2:
                    ox+=2.5;
                    oy+=1.5;
                    angle = 180;
                    break;
                case 3:
                    ox += 1.5;
                    oy += 2.5;
                    angle = 90;
                    break;
                default:
                    ////system.out.println("uh oh");
                    break;
            }
            float sx = drawScale.x / 64;
            float sy = drawScale.y / 64;

            canvas.draw(texture, Color.WHITE, ox, oy, getX() * drawScale.x, getY() * drawScale.x, (float)Math.toRadians(angle), sx, sy);
        }

    }

    public void drawDebug(GameCanvas canvas) {
        canvas.drawPhysics(shape,Color.YELLOW,getX(),getY(),getAngle(),drawScale.x,drawScale.y);

    }

}
