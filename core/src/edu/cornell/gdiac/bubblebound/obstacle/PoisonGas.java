package edu.cornell.gdiac.physics.obstacle;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import edu.cornell.gdiac.physics.GameCanvas;

public class PoisonGas extends BoxObstacle{





    public PoisonGas(float x, float y) {
        super(x, y, 1, 1);
        this.setSensor(true);
        this.setName("gas");
        setBodyType(BodyDef.BodyType.StaticBody);
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
