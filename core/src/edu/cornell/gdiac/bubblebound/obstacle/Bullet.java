package edu.cornell.gdiac.bubblebound.obstacle;

import com.badlogic.gdx.physics.box2d.BodyDef;

public class Bullet extends BoxObstacle{


    public Bullet(float radius) {
        this(1, 1, radius);
    }

    public Bullet(float x, float y, float radius) {
        super(x, y, radius, radius);
        setName("bullet");
        setBodyType(BodyDef.BodyType.DynamicBody);
        fixture.filter.groupIndex = -1;
    }
}
