package edu.cornell.gdiac.bubblebound.obstacle;

import com.badlogic.gdx.math.Vector2;

public class Door{

    public enum SpawnDirection{
        LEFT,
        RIGHT
    }
    SpawnDirection spawnDirection;

    private Vector2 location;
    private Vector2 avatarSpawnLocation;

    private static int last_used_id = 0;

    private Goal goal;

    private int levelID;
    private int doorID;

    private int targetLevelID;

    private int targetDoorID;


    public Door(Vector2 door_location, SpawnDirection dudeSpawnDirection){
        goal = new Goal(door_location);
        targetDoorID = last_used_id + 1;
        last_used_id ++;
        spawnDirection = dudeSpawnDirection;
        location = door_location;
        switch (spawnDirection){
            case LEFT:
                avatarSpawnLocation = location.add(-3,0);
                break;
            case RIGHT:
                avatarSpawnLocation = location.add(3,0);
                break;
            default:
                //SHOULD NOT GET HERE
                avatarSpawnLocation = null;
                break;
        }
    }

    public int getId(){
        return doorID;
    }

    public boolean setTargetID(int t_id){
        if(t_id <= last_used_id){
            targetDoorID = t_id;
            return true;
        }else{
            return false;
        }
    }


    public Goal getGoal(){
        return goal;
    }
    public void respawnGoal(){
        if
    }
}
