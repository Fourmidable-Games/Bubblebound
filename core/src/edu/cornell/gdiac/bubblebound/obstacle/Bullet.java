package edu.cornell.gdiac.bubblebound.obstacle;


import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.physics.box2d.BodyDef;
import edu.cornell.gdiac.bubblebound.GameCanvas;

public class Bullet extends WheelObstacle{



    public Bullet(float radius) {
        this(1, 1, radius);
    }

    public Bullet(float x, float y, float radius) {

        super(x, y, radius);

        setName("bullet");
        setBodyType(BodyDef.BodyType.DynamicBody);
        fixture.filter.groupIndex = -1;
    }

}
