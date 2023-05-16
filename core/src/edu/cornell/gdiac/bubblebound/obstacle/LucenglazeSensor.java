package edu.cornell.gdiac.bubblebound.obstacle;

import java.util.*;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import edu.cornell.gdiac.bubblebound.GameCanvas;
import edu.cornell.gdiac.util.FilmStrip;

public class LucenglazeSensor extends BoxObstacle{

    public Lucenglaze lucen;
    public boolean triggered = false;
    public ArrayList<Vector2> poison;
    public boolean horizontal = false;
    public int direction = 1; // -1 if facing down or left
    private int x;
    private int y;
    private FilmStrip filmstrip;
    private int resettimer = 0;

    private int spawndelay = 40;
    public final int fadeawaytimer = 200; //for poison

    public int timer = -1;
    protected int ii = 0;
    protected int counter1 = 0;
    protected final int delay1 = 3; // adjust this value to change the delay
    public void initialize(FilmStrip f) {
        lucen.initialize(filmstrip);
    }
    public LucenglazeSensor(float x, float y, int lucenx, int luceny, int w, int h, int rotation){ //rotation 1 is upright, 2 is facing right, 3 is facing down, 4 is facing left
        super(x, y, w, h); //idk y 1.5
        if(rotation % 2 == 1 ){
            horizontal = true;
        }else{
            horizontal = false;
        }
        if(rotation >= 2){
            direction = -1;
        }else{
            direction = 1;
        }
        spawndelay = 40;
       this.x = lucenx;
       this.y = luceny;
       this.setSensor(true);
       triggered = false;
       poison = new ArrayList<>();
       this.setName("lucenglazesensor");
       this.setBodyType(BodyDef.BodyType.StaticBody);
    }
    @Override
    public void draw(GameCanvas canvas){
        this.lucen.draw(canvas);
    }
    public void setLucen(Lucenglaze lucen){
        this.lucen = lucen;
    }
    public void activate(){
        triggered = true;
        lucen.triggered = true;
    }
    public void deactivate(){
        triggered = false;
        lucen.triggered = false;
    }
    public ArrayList<Vector2> update(){
        lucen.fixture.filter.groupIndex = -2;
        if(resettimer > 0){
            resettimer++;
            if(resettimer > fadeawaytimer + 200){
                deactivate();
                resettimer = 0;
                timer = -1;
            }
        }
        if(triggered && timer <= spawndelay + 40){
            lucen.update();
            timer++;
            if(timer == spawndelay){
                poison.clear();
                if(horizontal){
                    poison.add(new Vector2(x,y));
                    poison.add(new Vector2(x,y + 1));
                    poison.add(new Vector2(x, y -1));
                    poison.add(new Vector2(x + (direction * 1), y));
                }else {
                    poison.add(new Vector2(x,y));
                    poison.add(new Vector2(x, y + (direction * 1)));
                    poison.add(new Vector2(x + 1, y));
                    poison.add(new Vector2(x - 1, y));
                }
                return poison;
            }else if(timer == spawndelay + 20){
                poison.clear();
                if(horizontal){
                    poison.add(new Vector2(x + (direction * 1), y + 1));
                    poison.add(new Vector2(x + (direction * 1), y - 1));
                    poison.add(new Vector2(x + (direction * 2), y));
                }else{
                    poison.add(new Vector2(x, y + (direction * 2)));
                    poison.add(new Vector2(x - 1, y + (direction * 1)));
                    poison.add(new Vector2(x + 1, y + (direction * 1)));
                }

                return poison;
            }else if(timer == spawndelay + 40){
                poison.clear();
                if(horizontal){
                    poison.add(new Vector2(x + (direction * 2), y + 1));
                    poison.add(new Vector2(x + (direction * 2), y - 1));
                    poison.add(new Vector2(x + (direction * 3), y));
                    poison.add(new Vector2(x + (direction * 3), y + 1));
                    poison.add(new Vector2(x + (direction * 3), y - 1));
                }else {
                    poison.add(new Vector2(x, y + (direction * 3)));
                    poison.add(new Vector2(x - 1, y + (direction * 2)));
                    poison.add(new Vector2(x - 1, y + (direction * 3)));
                    poison.add(new Vector2(x + 1, y + (direction * 2)));
                    poison.add(new Vector2(x + 1, y + (direction * 3)));
                }
                resettimer = 1;
                return poison;
            }
            return null;
        }else{
            return null;
        }
    }


}
