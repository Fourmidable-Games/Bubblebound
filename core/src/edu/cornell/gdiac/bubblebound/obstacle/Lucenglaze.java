package edu.cornell.gdiac.bubblebound.obstacle;


import com.badlogic.gdx.physics.box2d.BodyDef;

public class Lucenglaze extends BoxObstacle{

    public boolean triggered = false;

    public Lucenglaze(float x, float y){
        super(x, y, 1, 1);
        triggered = false;
        this.setName("lucenglaze");
        this.setBodyType(BodyDef.BodyType.StaticBody);
    }


}
