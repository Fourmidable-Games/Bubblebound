package edu.cornell.gdiac.bubblebound.obstacle;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import edu.cornell.gdiac.bubblebound.DudeModel;
import edu.cornell.gdiac.bubblebound.GameCanvas;
import edu.cornell.gdiac.util.FilmStrip;

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
    private FilmStrip filmstrip;

    public Door(Vector2 door_location, SpawnDirection dudeSpawnDirection, int targetLevelID){
        super(door_location);
        this.isGoal = true;
        loc = door_location;
        targetID = targetLevelID;
        spawnDirection = dudeSpawnDirection;
        switch (spawnDirection){
            case LEFT:
                spawnLoc = loc.add(-2,0);
                break;
            case RIGHT:
                spawnLoc = loc.add(2,0);
                break;
            default:
                //SHOULD NOT GET HERE
                spawnLoc = null;
                break;
        }
    }

    protected int ii = 0;
    protected int counter1 = 0;
    protected final int delay1 = 10;
    public void initialize(FilmStrip f){
        filmstrip = f;
        if (counter1 == 0) { // execute setFrame only when counter reaches 0
            f.setFrame(0);
        }
        counter1 = (counter1 + 1) % delay1; // increment counter and reset to 0 when it reaches delay
    }

    protected int i;
    protected int counter = 0;
    protected final int delay = 7; // adjust this value to change the delay
    public void update() {
        if (filmstrip != null) {
            if (counter == 0) { // execute setFrame only when counter reaches 0
                int next = (i++) % 8;
                filmstrip.setFrame(next);
            }
            counter = (counter + 1) % delay; // increment counter and reset to 0 when it reaches delay
        }
    }

    public int getTargetLevelID(){
        return targetID;
    }

    public Door.SpawnDirection getSpawnDirection(){
        return spawnDirection;
    }

    public Vector2 getPlayerSpawnLocation(){return spawnLoc;}

    @Override
    public void draw(GameCanvas canvas) {
        float sx = drawScale.x / 64f;
        float sy = drawScale.y / 64f;
        sx = Math.round(32 * sx) / 32f; //roudns to x.x
        sy = Math.round(32 * sy) / 32f;
//        if(targetID == 6 || targetID == 11 || targetID == 16) {
//            if (grav == 1) {
//                canvas.draw(texture, new Color(1, 1, 1, 0.95f), origin.x, origin.y, getX() * drawScale.x, getY() * drawScale.y, getAngle(), sx, sy);
//
//            } else {
//                canvas.draw(texture, new Color(1, 1, 1, 0.95f), origin.x, origin.y, getX() * drawScale.x, getY() * drawScale.y, getAngle(), sx, sy);
//
//            }
//        }else{
            if (grav == 1) {
                canvas.draw(texture, new Color(1, 1, 1, 1f), origin.x, origin.y, getX() * drawScale.x, getY() * drawScale.y, getAngle(), sx, sy);

            } else {
                canvas.draw(texture, new Color(1, 1, 1, 1f), origin.x, origin.y, getX() * drawScale.x, getY() * drawScale.y, getAngle(), sx, sy);

            }
//        }
    }
}
