package edu.cornell.gdiac.bubblebound.obstacle;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import edu.cornell.gdiac.bubblebound.DudeModel;
import edu.cornell.gdiac.bubblebound.GameCanvas;
import edu.cornell.gdiac.util.FilmStrip;
import edu.cornell.gdiac.util.PooledList;

import java.util.List;

public class ProjEnemy extends BoxObstacle{

    //implies 9x9 with flower moving thing at bottom middle for upwards rotation

    int shoottimer = 1; //shoot every 100 frames;
    int shootcooldown = 200;
    private DudeModel avatar;
    private Body collidebody;
    private Vector2 collidebodyPos;
    private FilmStrip filmstrip;
    boolean shooting = false;
    private int rotation;
    private int collidedbodies;

    private Color color;

    public int getRotation(){
        return rotation;
    }
    public ProjEnemy(float x, float y, int r) {
        this(x, y, 1, 1, r);
        color = new Color(1,1,1,1);
        shoottimer = 1;
    }
    protected int i = 0;
    protected int counter = 0;
    protected final int delay = 10; // adjust this value to change the delay

    public void initialize(FilmStrip f) {
        filmstrip = f;
        f.setFrame(0);
    }
    public boolean isShooting() {
        return shooting;
    }

    public ProjEnemy(float x, float y, float width, float height, int rotation) {
        super(x, y, 1, 1);
        this.setName("projenemy");
        this.setBodyType(BodyDef.BodyType.StaticBody);
        this.rotation = rotation;
        fixture.filter.groupIndex = -1;
        color = new Color(1,1,1,1);
        shoottimer = 1;

        setSensor(true);

    }

    public void activate(){
        shooting = true;
    }

    public void deactivate(){
        shooting = false;
        shoottimer = 1;
    }

    public boolean inBounds(Obstacle obj, Rectangle bounds) {
        boolean horiz = (bounds.x <= obj.getX() && obj.getX() <= bounds.x+bounds.width);
        boolean vert  = (bounds.y <= obj.getY() && obj.getY() <= bounds.y+bounds.height);
        return horiz && vert;
    }

    public void addQueuedObject(Obstacle obj, PooledList<Obstacle> addQueue, Rectangle bounds) {
        assert inBounds(obj, bounds) : "Object is not in bounds";
        addQueue.add(obj);
    }

    public boolean update(){
            if (filmstrip != null) {
                if (counter == 0) { // execute setFrame only when counter reaches 0
                    //int next = (i++) / 5;
                    filmstrip.setFrame(i % 9);
                    i++;
                }
                counter = (counter + 1) % delay; // increment counter and reset to 0 when it reaches delay
            }

//        //////////////System.out.println(shooting);
//        //////////////System.out.println(shoottimer);
        if(shooting && shoottimer == 0){
            shoottimer++;
            color = new Color((float)255 / 255,(float)(210-shoottimer) / 210,(float)(210-shoottimer) / 210,1);
            return true;
        }
        if(shoottimer > 0 && shoottimer < shootcooldown){
            shoottimer++;
        }else{
            shoottimer = 0;
        }
        color = new Color((float)255 / 255,(float)(210-shoottimer) / 210,(float)(210-shoottimer) / 210,1);
        return false;
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
                    ////////////////System.out.println("uh oh");
                    break;
            }
            float sx = drawScale.x / 64f;
            float sy = drawScale.y / 64f;
            //////////System.out.println("DRAWING SUNFLOWER WITH COLOR LEFT OF: " + color.b);
            canvas.draw(texture, color, ox, oy, getX() * drawScale.x, getY() * drawScale.y, (float)Math.toRadians(angle), sx, sy);
        }

    }

    public void createBullet(ProjEnemy pe, DudeModel avatar, Vector2 scale, TextureRegion bulletTexture,
                             PooledList<Obstacle> addQueue, Rectangle bounds, List<Bullet> bullets){

        Vector2 dir = avatar.getPosition().sub(pe.getPosition());
        color = new Color((float)255 / 255,(float)(210-shoottimer) / 210,(float)(210-shoottimer) / 210,1);
        float radius = 0.3f;

        int[][] offsets = {{0,1}, {1,0}, {0,-1}, {-1,0}};
        int[] offset = offsets[pe.getRotation()];
        Bullet bullet = new Bullet(pe.getX() + offset[0], pe.getY() + offset[1], radius);
        bullet.setName("sundropBullet");
        bullet.setGravityScale(0f);
        bullet.setDrawScale(scale);

        bullet.setTexture(bulletTexture);
        bullet.setBullet(true);

        float speed = 5f;

        bullet.setLinearVelocity(dir.nor().scl(speed));
        addQueuedObject(bullet, addQueue, bounds);
        bullets.add(bullet);

    }

    public void addOneToCollidebodies() {
        collidedbodies = collidedbodies + 1;
    };

    public void setAvatar(DudeModel Avatar) {
        avatar = Avatar;
    }

    public DudeModel DudeModel () {
        return avatar;
    }

    public void setCollidedbody(Body body) {
        collidebody = body;
    }

    public Body getCollidedBody() {
        return collidebody;
    }

    public void setCollidedBodyPos(Vector2 pos) {
        collidebodyPos = pos;
    }

    public Vector2 getCollidedBodyPos() {
        return collidebodyPos;
    }

    public boolean canShoot(Obstacle b, Vector2 collidePos, Body collidebody, DudeModel avatar, int collidedbodies, World world) {

        setAvatar(avatar);

        RayCastCallback rcc = new RayCastCallback() {
            @Override
            public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
                setCollidedBodyPos(fixture.getBody().getPosition());
                setCollidedbody(fixture.getBody());
                if (DudeModel() != getCollidedBody().getUserData() && !fixture.isSensor()) {

                    if(!((Obstacle)fixture.getBody().getUserData()).getName().contains("plank")){
                        addOneToCollidebodies();
                    }

                }

                return 1;
            }
        };

        collidedbodies = 0;

        world.rayCast(rcc, b.getPosition(), avatar.getPosition());
        return collidedbodies < 1;
    }

    public void drawDebug(GameCanvas canvas) {
        canvas.drawPhysics(shape,Color.YELLOW,getX(),getY(),getAngle(),drawScale.x,drawScale.y);

    }

}
