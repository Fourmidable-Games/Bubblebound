package edu.cornell.gdiac.bubblebound;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import edu.cornell.gdiac.bubblebound.obstacle.SimpleObstacle;

public class Spike extends SimpleObstacle {

    /** Shape information for this box */
    protected PolygonShape shape;
    /** The width and height of the box */
    private Vector2 dimension;
    /** A cache value for when the user wants to access the dimensions */
    private Vector2 sizeCache;
    /** A cache value for the fixture (for resizing) */
    private Fixture geometry;
    /** Cache of the polygon vertices (for resizing) */
    private float[] vertices;
    private float rotation;
    private TextureRegion texture2;

    /**
     * Returns the dimensions of this box
     *
     * This method does NOT return a reference to the dimension vector. Changes to this
     * vector will not affect the shape.  However, it returns the same vector each time
     * its is called, and so cannot be used as an allocator.
     *
     * @return the dimensions of this box
     */
    public Vector2 getDimension() {
        return sizeCache.set(dimension);
    }



    /**
     * Sets the dimensions of this box
     *
     * This method does not keep a reference to the parameter.
     *
     * @param value  the dimensions of this box
     */
    public void setDimension(Vector2 value) {
        setDimension(value.x, value.y);
    }


    /**
     * Sets the dimensions of this box
     *
     * @param width   The width of this box
     * @param height  The height of this box
     */
    public void setDimension(float width, float height) {
        dimension.set(width, height);
        markDirty(true);
        resize(width, height);
    }

    /**
     * Returns the box width
     *
     * @return the box width
     */
    public float getWidth() {
        return dimension.x;
    }

    /**
     * Sets the box width
     *
     * @param value  the box width
     */
    public void setWidth(float value) {
        sizeCache.set(value,dimension.y);
        setDimension(sizeCache);
    }

    public void setTexture2(TextureRegion t){
        texture2 = t;
    }

    /**
     * Returns the box height
     *
     * @return the box height
     */
    public float getHeight() {
        return dimension.y;
    }

    /**
     * Sets the box height
     *
     * @param value  the box height
     */
    public void setHeight(float value) {
        sizeCache.set(dimension.x,value);
        setDimension(sizeCache);
    }

    public Spike(float x, float y, float width, float height, float angle) {
        super(x,y);

        rotation = angle;
//        this.origin.x +=0.5;
        this.setAngle((float)Math.toRadians(-1*angle));
        dimension = new Vector2(width,height);
        sizeCache = new Vector2();
        shape = new PolygonShape();
        vertices = new float[6];
        geometry = null;

        // Initialize
        resize(width, height);
    }



    /**
     * Reset the polygon vertices in the shape to match the dimension.
     */
    private void resize(float width, float height) {
        // Make the box with the center in the center
        vertices[0] = -width/2.0f; //0,1 = bottom left
        vertices[1] = -height/2.0f;
        vertices[2] = width/2.0f; //2,3 = bottom right
        vertices[3] = -height/2.0f;
        vertices[4] =  0; //4,5 = up
        vertices[5] =  height/2.0f;
        shape.set(vertices);
    }


    protected void createFixtures() {
        if (body == null) {
            return;
        }

        releaseFixtures();

        // Create the fixture
        fixture.shape = shape;
        geometry = body.createFixture(fixture);
        markDirty(false);
    }

    /**
     * Release the fixtures for this body, reseting the shape
     *
     * This is the primary method to override for custom physics objects
     */
    protected void releaseFixtures() {
        if (geometry != null) {
            body.destroyFixture(geometry);
            geometry = null;
        }
    }

    public void drawDebug(GameCanvas canvas) {
        canvas.drawPhysics(shape, Color.YELLOW,getX(),getY(),getAngle(),drawScale.x,drawScale.y);
    }
    @Override
    public void sdraw(GameCanvas canvas){
        float x = getWidth()*drawScale.x / 2;
        float y = getHeight()*drawScale.y / 2;
        canvas.shape.setColor(Color.DARK_GRAY);
        float angle = body.getAngle();
        canvas.shape.triangle(drawScale.x * (getX()-getWidth()/2f), drawScale.y *(getY()-getHeight()/2f), drawScale.x * (getX()+getWidth()/2f), drawScale.y * (getY()-getHeight()/2f), drawScale.x * getX(),drawScale.y * (getY()+getHeight()/2f));
        //canvas.shape.rect(getX()*drawScale.x-x,getY()*drawScale.y-y,getWidth()*drawScale.x,getHeight()*drawScale.y);
        //canvas.shape.rect(getX()*drawScale.x-x,getY()*drawScale.y-y,x,y,getWidth()*drawScale.x,getHeight()*drawScale.y,1,1,(float)Math.toDegrees(angle));
    }

    @Override
    public void draw(GameCanvas canvas) {
        float sx = drawScale.x / 64f;
        float sy = drawScale.y / 64f;
        if (texture != null) {
            if(grav == 1) {
                canvas.draw(texture, Color.WHITE, origin.x, origin.y, getX() * drawScale.x, getY() * drawScale.x, getAngle(), sx, sy);
            }
            else{
                canvas.draw(texture2, Color.WHITE, origin.x, origin.y, getX() * drawScale.x, getY() * drawScale.x, getAngle(), sx, sy);
            }
        }
    }



}
