package edu.cornell.gdiac.bubblebound.obstacle;

import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

public class Door extends Goal{

    public enum SpawnDirection{
        LEFT,
        RIGHT
    }
    private SpawnDirection spawnDirection;

    private Vector2 spawnLoc;

    private Vector2 loc;

    private int targetID;

    public Door(Vector2 door_location, SpawnDirection dudeSpawnDirection, int targetLevelID){
        super(door_location);
        this.isGoal = true;
        loc = door_location;
        targetID = targetLevelID;
        spawnDirection = dudeSpawnDirection;
        switch (spawnDirection){
            case LEFT:
                spawnLoc = loc.add(0,0);
                break;
            case RIGHT:
                spawnLoc = loc.add(0,0);
                break;
            default:
                //SHOULD NOT GET HERE
                spawnLoc = null;
                break;
        }
    }

    public int getTargetLevelID(){
        return targetID;
    }

    public Door.SpawnDirection getSpawnDirection(){
        return spawnDirection;
    }

    public Vector2 getPlayerSpawnLocation(){return spawnLoc;}


}
