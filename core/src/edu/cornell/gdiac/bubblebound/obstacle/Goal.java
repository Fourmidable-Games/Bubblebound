package edu.cornell.gdiac.bubblebound.obstacle;

import com.badlogic.gdx.math.Vector;
import com.badlogic.gdx.math.Vector2;


public class Goal extends BoxObstacle{

    private static final Vector2 GOAL_DIMENSIONS = new Vector2(2,3);

    public Goal(Vector2 location){
        super(location.x, location.y, GOAL_DIMENSIONS.x, GOAL_DIMENSIONS.y);
    }

}
