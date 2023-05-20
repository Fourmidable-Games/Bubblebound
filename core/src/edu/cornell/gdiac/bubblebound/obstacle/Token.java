package edu.cornell.gdiac.bubblebound.obstacle;

import com.badlogic.gdx.math.Vector2;
import edu.cornell.gdiac.util.FilmStrip;

public class Token extends WheelObstacle{
    private FilmStrip filmstrip;
    protected int ii = 0;
    protected int counter1 = 0;
    protected final int delay1 = 3; // adjust this value to change the delay
    public void initialize(FilmStrip f) {
        filmstrip = f;
        f.setFrame(0);
    }
    public void update() {
        if(filmstrip != null) {
            if(counter1 == 0) {
                int temp = ii++ / 4;
                filmstrip.setFrame(temp % 5);
            }
            counter1 = (counter1 + 1) % delay1;
        }
    }
    private int BUBBLE_LIMIT;
    public Token(Vector2 location, int bubble_limit){
        super(location.x,location.y,0.5f);
        BUBBLE_LIMIT = bubble_limit;
    }

    public int getBubbleLimitValue(){
        return BUBBLE_LIMIT;
    }




}
