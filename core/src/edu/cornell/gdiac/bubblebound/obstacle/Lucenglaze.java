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
    private FilmStrip filmstrip2;

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
    public void initialize(FilmStrip f, FilmStrip f2) {
        filmstrip = f;
        filmstrip2 = f2;
        if (counter1 == 0) {
            filmstrip.setFrame(0);
            filmstrip2.setFrame(0);
        }
    }
    int counter_idle = 0;
    int delay_idle = 6;
    public void update() {
        if(filmstrip != null) {
            if(counter1 == 0) {
                int temp = ii++ / 1;
                filmstrip.setFrame(temp % 18);

            }
            if (counter_idle == 0) {
                int temp = ii / 5;
                filmstrip2.setFrame(temp % 10);
            }
            counter_idle = (counter_idle + 1) % delay_idle;
            counter1 = (counter1 + 1) % delay1;
        }
    }
    public void setTexture2(TextureRegion t){
        dormant = t;
    }

    int xoffset = 0;
    int yoffest = 0;

    public void setRotation(int r){
        rotation = r;
        if(r == 0){
            xoffset = -1;
            yoffest = 0;
        }
        if(r == 2){
            xoffset = 1;
            yoffest = 0;
        }
        if(r == 1){
            yoffest = 1;
            xoffset = 0;
        }
        if(r == 3){
            yoffest = -1;
            xoffset = 0;
        }
    }

    float angle = 0;
    @Override
    public void draw(GameCanvas canvas){
        if(texture != null){

            float ox = origin.x;
            float oy = origin.y;
            switch (rotation){
                case 0:
//                    ox += 1.5;
//                    oy += 0.5;
                    angle = 0;
                    break;
                case 1:
//                    ox += 0.5;
//                    oy += 1.5;
                    angle = -90;
                    break;
                case 2:
//                    ox+=2.5;
//                    oy+=1.5;
                    angle = 180;
                    break;
                case 3:
//                    ox += 1.5;
//                    oy += 2.5;
                    angle = 90;
                    break;
                default:
                    //////////////System.out.println("uh oh");
                    break;
            }
            float sx = drawScale.x / 64f;
            float sy = drawScale.y / 64f;
            TextureRegion temp = (triggered) ? texture : dormant;
            canvas.draw(temp, Color.WHITE, ox, oy, getX() * drawScale.x, getY() * drawScale.y, (float)Math.toRadians(angle), sx, sy);
        }

    }

    public void drawPoison(GameCanvas canvas){
        if(!triggered) return;
        float sx = drawScale.x / 64f;
        float sy = drawScale.y / 64f;

        canvas.draw(filmstrip2, Color.WHITE, origin.x, origin.y, (getX() + xoffset) * drawScale.x, (getY() + yoffest) * drawScale.y, (float)Math.toRadians(angle), sx, sy);

    }

}
