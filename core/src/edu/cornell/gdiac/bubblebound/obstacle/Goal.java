package edu.cornell.gdiac.bubblebound.obstacle;

import com.badlogic.gdx.math.Vector;
import com.badlogic.gdx.math.Vector2;

public class Goal extends BoxObstacle{
    public enum GoalType{
        DOOR,
        WIN
    }

    public enum SpawnDirection{
        LEFT,
        RIGHT
    }

    public GoalType goalType;
    public SpawnDirection spawnDirection;
    public Vector2 spawnLocation;

    public Goal(Vector2 location, Vector2 dimensions){
        super(location.x, location.y, dimensions.x, dimensions.y);
        goalType = GoalType.WIN;
    }

    public Goal(Vector2 location, Vector2 dimensions, SpawnDirection dudeSpawnDirection){
        super(location.x, location.y, dimensions.x, dimensions.y);
        goalType = GoalType.DOOR;
        spawnDirection = dudeSpawnDirection;
        switch (spawnDirection){
            case LEFT:
                spawnLocation = location.add(-3,0);
                break;
            case RIGHT:
                spawnLocation = location.add(3,0);
                break;
            default:
                spawnLocation = null;
                break;
        }
    }

    public void setDestination(){

    }

    public void getDestination(){

    }

}
