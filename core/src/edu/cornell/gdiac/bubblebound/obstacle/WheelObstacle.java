/*
 * WheelObstacle.java
 *
 * Sometimes you want circles instead of boxes. This class gives it to you.
 * Note that the shape must be circular, not Elliptical.  If you want to make
 * an ellipse, you will need to use the PolygonObstacle class.
 *
 * Author: Walker M. White
 * Based on original PhysicsDemo Lab by Don Holden, 2007
 * LibGDX version, 2/6/2015
 */
package edu.cornell.gdiac.bubblebound.obstacle;

import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.physics.box2d.*;

import edu.cornell.gdiac.bubblebound.GameCanvas;

/**
 * Circle-shaped model to support collisions.
 *
 * Unless otherwise specified, the center of mass is as the center.
 */
public class WheelObstacle extends SimpleObstacle {
	/** Shape information for this circle */
	protected CircleShape shape;
	/** A cache value for the fixture (for resizing) */
	private Fixture geometry;

	private boolean selected;

	public boolean statc;
	private boolean d = true;

	private String nametag = "bubble";

	/**
	 * Returns the physics object tag.
	 *
	 * A tag is a string attached to an object, in order to identify it in debugging.
	 *
	 * @return the physics object tag.
	 */
	public String getName() {
		return nametag;
	}

	/**
	 * Sets the physics object tag.
	 *
	 * A tag is a string attached to an object, in order to identify it in debugging.
	 *
	 * @param  value    the physics object tag
	 */
	public void setName(String value) {
		nametag = value;
	}


	public void setStatic(boolean b){
		statc = b;
	}

	public void setSelected(boolean b){
		selected = b;
	}

	/**
	 * Returns the radius of this circle
	 *
	 * @return the radius of this circle
	 */
	public float getRadius() {
		return shape.getRadius();
	}
	
	/**
	 * Sets the radius of this circle
	 *
	 * @param value  the radius of this circle
	 */
	public void setRadius(float value) {
		shape.setRadius(value);
		markDirty(true);
	}

	
	/**
	 * Creates a new circle at the origin.
	 *
	 * The size is expressed in physics units NOT pixels.  In order for 
	 * drawing to work properly, you MUST set the drawScale. The drawScale 
	 * converts the physics units to pixels.
	 * 
	 * @param radius	The wheel radius
	 */
	public WheelObstacle(float radius) {
		this(0, 0, radius);
	}

	/**
	 * Creates a new circle object.
	 *
	 * The size is expressed in physics units NOT pixels.  In order for 
	 * drawing to work properly, you MUST set the drawScale. The drawScale 
	 * converts the physics units to pixels.
	 *
	 * @param x 		Initial x position of the circle center
	 * @param y  		Initial y position of the circle center
	 * @param radius	The wheel radius
	 */
	public WheelObstacle(float x, float y, float radius) {
		super(x,y);
		shape = new CircleShape();
		shape.setRadius(radius);

	}
	
	/**
	 * Create new fixtures for this body, defining the shape
	 *
	 * This is the primary method to override for custom physics objects
	 */
	protected void createFixtures() {
		// System.out.println("create fixtures");
		if (body == null) {
			return;
		}
		
		releaseFixtures();
		
		// Create the fixture
		fixture.shape = shape;
		geometry = body.createFixture(fixture);
		markDirty(false);
		body.setGravityScale(0f);
	}


	/**
	 * Release the fixtures for this body, reseting the shape
	 *
	 * This is the primary method to override for custom physics objects
	 */
	protected void releaseFixtures() {
		// System.out.println("release fixtures");
		// System.out.println(geometry);
	    if (geometry != null && geometry.getBody() == body) {
	        body.destroyFixture(geometry);
	        geometry = null;
	    }
	}
	
	/**
	 * Draws the outline of the physics body.
	 *
	 * This method can be helpful for understanding issues with collisions.
	 *
	 * @param canvas Drawing context
	 */
	 public void drawDebug(GameCanvas canvas) {
		 if(!d) return;
		canvas.drawPhysics(shape,Color.YELLOW,getX(),getY(),drawScale.x,drawScale.y);
	}
	@Override
	public void sdraw(GameCanvas canvas){
		 if(!d) return;
		 if(selected){
			 canvas.shape.setColor(Color.YELLOW);
		 }else{
			 canvas.shape.setColor(Color.GREEN);
		 }
		 canvas.shape.circle(getX()*drawScale.x,getY()*drawScale.y,getRadius()*drawScale.x);
	}

	@Override
	public void draw(GameCanvas canvas) {
		if (texture != null && d) {


			if(grav == 1) {
				canvas.draw(texture,Color.GOLD,origin.x,origin.y,getX()*drawScale.x,getY()*drawScale.y,getAngle(), 1.4F*getRadius(), 1.4F*getRadius());
			}else{
				canvas.draw(texture,Color.WHITE,origin.x,origin.y,getX()*drawScale.x,getY()*drawScale.y,getAngle(), 1.4F*getRadius(), 1.4F*getRadius());
			}

		}
	}

	// Gets D
	public boolean getD(){return d;}
	public void stopDraw(){d = false;}

}