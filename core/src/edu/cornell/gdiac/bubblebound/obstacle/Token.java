package edu.cornell.gdiac.bubblebound.obstacle;

import com.badlogic.gdx.math.Vector2;

public class Token extends WheelObstacle{
    private int BUBBLE_LIMIT;
    public Token(Vector2 location, int bubble_limit){
        super(location.x,location.y,0.5f);
        BUBBLE_LIMIT = bubble_limit;
    }

    public int getBubbleLimitValue(){
        return BUBBLE_LIMIT;
    }




}
