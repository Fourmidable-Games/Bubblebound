package edu.cornell.gdiac.bubblebound.obstacle;

import com.badlogic.gdx.physics.box2d.BodyDef;

public class ProjEnemySensor extends BoxObstacle{

    ProjEnemy pe;

    public ProjEnemySensor(float x, float y, int w, int h, int rotation){
        super(x, y, w, h);
        this.setSensor(true);
        this.setName("projenemysensor");
        this.setBodyType(BodyDef.BodyType.StaticBody);
        pe = null;
    }

    public void setPE(ProjEnemy pe){
        this.pe = pe;
    }

    public void activate(){
        pe.activate();
    }

    public void deactivate(){
        pe.deactivate();
    }
}
