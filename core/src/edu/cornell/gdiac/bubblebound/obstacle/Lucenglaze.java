package edu.cornell.gdiac.bubblebound.obstacle;


import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.BodyDef;
import edu.cornell.gdiac.bubblebound.GameCanvas;
import edu.cornell.gdiac.util.FilmStrip;

public class Lucenglaze extends BoxObstacle{

    public boolean triggered = false;
    public int rotation = 1;
    private TextureRegion dormant;
    private FilmStrip filmstrip;


    public Lucenglaze(float x, float y){
        super(x, y, 1, 1);
        triggered = false;
        rotation = 1;
        this.setSensor(true);
        this.setName("lucenglaze");
        this.setBodyType(BodyDef.BodyType.StaticBody);
    }
    protected int ii = 0;
    protected int counter1 = 0;
    protected final int delay1 = 3; // adjust this value to change the delay
    public void initialize(FilmStrip f) {
        filmstrip = f;
        if (counter1 == 0) {
            filmstrip.setFrame(0);
        }
    }
    public void update() {
        if(filmstrip != null) {
            if(counter1 == 0) {
                int temp = ii++ / 1;
                filmstrip.setFrame(temp % 18);
            }
            counter1 = (counter1 + 1) % delay1;
        }
    }
    public void setTexture2(TextureRegion t){
        dormant = t;
    }

    public void setRotation(int r){
        rotation = r;
    }
    @Override
    public void draw(GameCanvas canvas){
        if(texture != null){

            float ox = origin.x;
            float oy = origin.y;
            float angle = 0;
            switch (rotation){
                case 0:
                    ox += 1.5;
                    oy += 0.5;
                    angle = 0;
                    break;
                case 1:
                    ox += 0.5;
                    oy += 1.5;
                    angle = -90;
                    break;
                case 2:
                    ox+=2.5;
                    oy+=1.5;
                    angle = 180;
                    break;
                case 3:
                    ox += 1.5;
                    oy += 2.5;
                    angle = 90;
                    break;
                default:
                    //////////System.out.println("uh oh");
                    break;
            }
            float sx = drawScale.x / 64f;
            float sy = drawScale.y / 64f;
            TextureRegion temp = (triggered) ? texture : dormant;
            canvas.draw(temp, Color.WHITE, ox, oy, getX() * drawScale.x, getY() * drawScale.y, (float)Math.toRadians(angle), sx, sy);
        }

    }

}
