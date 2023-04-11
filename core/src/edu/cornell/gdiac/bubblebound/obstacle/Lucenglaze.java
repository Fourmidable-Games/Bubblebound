package edu.cornell.gdiac.bubblebound.obstacle;


import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.physics.box2d.BodyDef;
import edu.cornell.gdiac.bubblebound.GameCanvas;

public class Lucenglaze extends BoxObstacle{

    public boolean triggered = false;
    public int rotation = 1;

    public Lucenglaze(float x, float y){
        super(x, y, 1, 1);
        triggered = false;
        rotation = 1;
        this.setName("lucenglaze");
        this.setBodyType(BodyDef.BodyType.StaticBody);
    }

    public void setRotation(int r){
        rotation = r;
    }
    @Override
    public void draw(GameCanvas canvas){
        if(texture != null){
            canvas.draw(texture, Color.WHITE, origin.x, origin.y, getX() * drawScale.x, getY() * drawScale.x, getAngle() * 90, 0.5F, 0.5F);
        }

    }

}
