package edu.cornell.gdiac.physics.obstacle;

import java.util.*;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import edu.cornell.gdiac.physics.GameCanvas;

public class LucenglazeSensor extends BoxObstacle{

    public Lucenglaze lucen;
    public boolean triggered = false;
    public ArrayList<Vector2> poison;
    private int x;
    private int y;


    public int timer = -1;

    public LucenglazeSensor(float x, float y){
       super(x, y+1.5f, 3, 4); //idk y 1.5
       this.x = (int)x;
       this.y = (int)y;
       this.setSensor(true);
       triggered = false;
       poison = new ArrayList<>();
       this.setName("lucenglazesensor");
       this.setBodyType(BodyDef.BodyType.StaticBody);
    }
    @Override
    public void draw(GameCanvas canvas){
        return; //don't draw anything
    }
    public void setLucen(Lucenglaze lucen){
        this.lucen = lucen;
    }
    public void activate(){
        triggered = true;
        lucen.triggered = true;
    }
    public ArrayList<Vector2> update(){
        if(triggered && timer <= 40){
            timer++;
            if(timer == 0){
                poison.clear();
                poison.add(new Vector2(x, y + 1));
                return poison;
            }else if(timer == 20){
                poison.clear();
                poison.add(new Vector2(x, y + 2));
                poison.add(new Vector2(x - 1, y));
                poison.add(new Vector2(x + 1, y));
                poison.add(new Vector2(x - 1, y +1));
                poison.add(new Vector2(x + 1, y + 1));
                return poison;
            }else if(timer == 40){
                poison.clear();
                poison.add(new Vector2(x, y + 3));
                poison.add(new Vector2(x-1,y+2));
                poison.add(new Vector2(x-1, y+3));
                poison.add(new Vector2(x+1,y+2));
                poison.add(new Vector2(x+1,y+3));
                return poison;
            }
            return null;
        }else{
            return null;
        }
    }


}
