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

    public final int fadeawaytimer = 200;




    public PoisonGas(float x, float y) {
        super(x, y, 1, 1);
        this.setSensor(true);
        this.setName("gas");
        faded = false;
        setBodyType(BodyDef.BodyType.StaticBody);
    }

    public void setFade(boolean val){
        timer = fadeawaytimer;
    }

    public void update(){
        if(timer < 0){
            faded = true;
        }
        timer--;
    }

    @Override
    public void draw(GameCanvas canvas){
        Color c = new Color(Color.LIME);
        c.a = 0.6f;
        float sx = drawScale.x / 64f;
        float sy = drawScale.y / 64f;
        if (texture != null) {
            if(grav == 1) {
                canvas.draw(texture, c, origin.x, origin.y, getX() * drawScale.x, getY() * drawScale.y, getAngle(), sx, sy);
            }else{
                canvas.draw(texture, c, origin.x, origin.y, getX() * drawScale.x, getY() * drawScale.y, getAngle(), sx, sy  );
            }
        }
    }




}
