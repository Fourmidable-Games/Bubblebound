/*
 * RopeBridge.java
 *
 * The class is a classic example of how to subclass ComplexPhysicsObject.
 * You have to implement the createJoints() method to stick in all of the
 * joints between objects.
 *
 * This is one of the files that you are expected to modify. Please limit changes to 
 * the regions that say INSERT CODE HERE.
 *
 * Author: Walker M. White
 * Based on original PhysicsDemo Lab by Don Holden, 2007
 * Updated asset version, 2/6/2021
 */
package edu.cornell.gdiac.physics;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.JointDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.DistanceJointDef;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.badlogic.gdx.utils.JsonValue;
import edu.cornell.gdiac.physics.obstacle.*;

/**
 * A bridge with planks connected by revolute joints.
 *
 * Note that this class returns to static loading.  That is because there are
 * no other subclasses that we might loop through.
 */
public class NewRopeBridge extends ComplexObstacle {
	/** The initializing data (to avoid magic numbers) */
	private final JsonValue data;

	// Invisible anchor objects
	/** The left side of the bridge */
	private WheelObstacle start = null;
	/** The right side of the bridge */
	private WheelObstacle finish = null;

	// Dimension information
	/** The size of the entire bridge */
	protected Vector2 dimension;
	/** The size of a single plank */
	protected Vector2 planksize;
	/* The length of each link */
	protected float linksize = 1.0f;
	/** The spacing between each link */
	protected float spacing = 0.0f;

	Body bubble;
	Body avatar;

	CapsuleObstacle avatarCapsule;

	/**
	 * Creates a new rope bridge with the given physics data
	 *
	 * This bridge is straight horizontal. The coordinates given are the
	 * position of the leftmost anchor.
	 *
	 * @param data  	The physics constants for this rope bridge
	 */
	public NewRopeBridge(JsonValue data, Body b, CapsuleObstacle a) {
		//super(data.get("pos").getFloat(0),data.get("pos").getFloat(1));
		super(b.getPosition().x, b.getPosition().y);
		// System.out.println(getPosition());
		setName("bridge");
		this.data = data;
		bubble = b;
		avatar = a.getBody();
		avatarCapsule = a;
		float x0 = avatar.getPosition().x;
		float y0 = avatar.getPosition().y + avatarCapsule.getHeight()/2;
		float xn = bubble.getPosition().x;
		float yn = bubble.getPosition().y;

		linksize = 0.125f;
		// System.out.println("linksize " +linksize);
	    // Compute the bridge length

		float length = (float)Math.sqrt(Math.pow(bubble.getPosition().x-avatar.getPosition().x,2)+ Math.pow(bubble.getPosition().y-1.0f-avatar.getPosition().y+0.5f,2));
		dimension = new Vector2(length,0.125f);
		// System.out.println("Dimension: " + dimension);
		float x_diff = bubble.getPosition().x - avatar.getPosition().x;
		float y_diff = bubble.getPosition().y -1.0f - avatar.getPosition().y + avatarCapsule.getHeight()/2;
		Vector2 norm = new Vector2(x_diff,y_diff);
		// System.out.println("Norm:" + norm);
	    norm.nor();
		Vector2 moveleft = new Vector2(-1 * norm.y, norm.x);
		moveleft.scl(dimension.y/2);
		Vector2 moveright = new Vector2(-1 * norm.y, norm.x);
		moveright.scl(dimension.y/2);
		// System.out.println("Norm normed:" + norm);
	    
	    // If too small, only make one plank.;
	    int nLinks = (int)((length -0.25f)/linksize);
		spacing = (length - 0.25f) - nLinks * linksize;
		spacing /= (nLinks);
//	    if (nLinks <= 1) {
//	        nLinks = 1;
//	        linksize = length;
//	        spacing = 0;
//	    } else {
//	        spacing = length -0.5f - nLinks * linksize;
//	        spacing /= (nLinks-1);
//	    }
	    	    
	    // Create the planks
//	    planksize.x = linksize;

		Vector2 pos = new Vector2();
		pos.add(x0,y0);
		WheelObstacle start = new WheelObstacle(pos.x,pos.y,data.getFloat("pin_radius", 1));
		start.setName("pin0");
		start.setGravityScale(0);
		start.setDensity(data.getFloat("density", 0));
		bodies.add(start);

		pos.set(norm);
		pos.scl(0.125f);
		pos.add(x0,y0);
		pos.add(moveleft);
		WheelObstacle one = new WheelObstacle(pos.x,pos.y,data.getFloat("pin_radius", 1));
		start.setName("pin1");
		start.setGravityScale(0);
		start.setDensity(data.getFloat("density", 0));
		bodies.add(one);

		pos.add(moveright);
		pos.add(moveright);
		WheelObstacle two = new WheelObstacle(pos.x,pos.y,data.getFloat("pin_radius", 1));
		start.setName("pin2");
		start.setGravityScale(0);
		start.setDensity(data.getFloat("density", 0));
		bodies.add(two);




	    for (int ii = 0; ii < nLinks; ii++) {

			float t = 0.125f + ii*(0.125f + spacing);

			pos.set(norm);
			pos.scl(t);
			pos.add(x0,y0);
			pos.add(moveleft);
			WheelObstacle w = new WheelObstacle(pos.x,pos.y,data.getFloat("pin_radius", 1));
			start.setName("pin" + 3 + (2 * ii));
			start.setGravityScale(0);
			start.setDensity(data.getFloat("density", 0));
			bodies.add(w);

			pos.add(moveright);
			pos.add(moveright);
			WheelObstacle w2 = new WheelObstacle(pos.x,pos.y,data.getFloat("pin_radius", 1));
			start.setName("pin" + 4 + + (2 * ii));
			start.setGravityScale(0);
			start.setDensity(data.getFloat("density", 0));
			bodies.add(w2);
	    }

		pos.set(norm);
		pos.scl(length);
		pos.add(x0,y0);
		WheelObstacle last = new WheelObstacle(pos.x,pos.y,data.getFloat("pin_radius", 1));
		start.setName("pin" + 3 + 2*nLinks);
		start.setGravityScale(0);
		start.setDensity(data.getFloat("density", 0));
		bodies.add(last);

	}

	/**
	 * Creates the joints for this object.
	 * 
	 * This method is executed as part of activePhysics. This is the primary method to 
	 * override for custom physics objects.
	 *
	 * @param world Box2D world to store joints
	 *
	 * @return true if object allocation succeeded
	 */
	protected boolean createJoints(World world) {
		assert bodies.size > 0;
		
		Vector2 anchor1 = new Vector2(); 
		Vector2 anchor2 = new Vector2(0,avatarCapsule.getHeight()/2);

		// Definition for a revolute joint and distance joint
		DistanceJointDef jointDefDist = new DistanceJointDef();

		// System.out.println(jointDefDist.frequencyHz);
//		jointDefDist.frequencyHz = 0.5f;
		RevoluteJointDef jointDef = new RevoluteJointDef();

		// REVOLUTE JOINT TO AVATAR
		Joint joint;
		jointDef.bodyA = bodies.get(0).getBody();
		jointDef.bodyB = avatar;
		jointDef.localAnchorA.set(anchor1);
		jointDef.localAnchorB.set(anchor2);
		jointDef.collideConnected = false;
		joint = world.createJoint(jointDef);
		joints.add(joint);
		anchor2.y = 0;

		//JOINT NEXT ONE TO FIRST
		Body nextOne = bodies.get(1).getBody();
		jointDefDist.length = .125f * (float)Math.sqrt(3);
//		jointDefDist.length = 0;
		jointDefDist.bodyA = nextOne;
		jointDefDist.bodyB = bodies.get(0).getBody();
		jointDefDist.localAnchorA.set(anchor1);
		jointDefDist.localAnchorB.set(anchor2);
		jointDefDist.collideConnected = false;
		joint = world.createJoint(jointDefDist);
		joints.add(joint);

		//JOINT NEXT TWO TO FIRST
		Body nextTwo = bodies.get(2).getBody();
		jointDefDist.length = .125f * (float)Math.sqrt(3);
		jointDefDist.bodyA = nextTwo;
		jointDefDist.bodyB = bodies.get(0).getBody();
		jointDefDist.localAnchorA.set(anchor1);
		jointDefDist.localAnchorB.set(anchor2);
		jointDefDist.collideConnected = false;
		joint = world.createJoint(jointDefDist);
		joints.add(joint);


		jointDefDist.localAnchorB.set(new Vector2());


		jointDefDist.dampingRatio = 0.3f;
		jointDefDist.frequencyHz = 10;

		//JOINT NEXT ONE TO NEXT TWO
		jointDefDist.bodyB = nextOne;
		jointDefDist.length = 0.125f;
		joint = world.createJoint(jointDefDist);
		joints.add(joint);

		Body prevOne = nextOne;
		Body prevTwo = nextTwo;

		// Link the pins together
		for (int ii = 0; ii < bodies.size-4; ii+=2) {
			prevOne = nextOne;
			prevTwo = nextTwo;
			nextOne = bodies.get(3 + ii).getBody();
			nextTwo = bodies.get(4 + ii).getBody();

			//JOINT Prev1-Next1 (.25)
			jointDefDist.length = 0.125f;
			jointDefDist.bodyA = prevOne;
			jointDefDist.bodyB = nextOne;
			joint = world.createJoint(jointDefDist);
			joints.add(joint);
			//JOINT Prev2-Next2 (.25)
			jointDefDist.length = 0.125f;
			jointDefDist.bodyA = prevTwo;
			jointDefDist.bodyB = nextTwo;
			joint = world.createJoint(jointDefDist);
			joints.add(joint);
			//JOINT Prev1-Next2 (.25 root 2)
			jointDefDist.length = 0.125f * (float)Math.sqrt(2);
			jointDefDist.bodyA = prevOne;
			jointDefDist.bodyB = nextTwo;
			joint = world.createJoint(jointDefDist);
			joints.add(joint);
			//JOINT Prev2-Next1 (.25 root 2)
			jointDefDist.length = 0.125f * (float)Math.sqrt(2);
			jointDefDist.bodyA = prevTwo;
			jointDefDist.bodyB = nextOne;
			joint = world.createJoint(jointDefDist);
			joints.add(joint);
			//JOINT Next1-Next2 (.25)
			jointDefDist.length = 0.125f;
			jointDefDist.bodyA = nextOne;
			jointDefDist.bodyB = nextTwo;
			joint = world.createJoint(jointDefDist);
			joints.add(joint);


			prevOne = nextOne;
			prevTwo = nextTwo;
		}

		//JOINT PREV ONE TO LAST
		Body last = bodies.get(bodies.size-1).getBody();
		jointDefDist.length = 0.125f * (float)Math.sqrt(3);
		jointDefDist.bodyA = prevOne;
		jointDefDist.bodyB = last;
		joint = world.createJoint(jointDefDist);
		joints.add(joint);

		//JOINT PREV TWO TO LAST
		jointDefDist.length = 0.125f * (float)Math.sqrt(3);
		jointDefDist.bodyA = prevTwo;
		jointDefDist.bodyB = last;
		joint = world.createJoint(jointDefDist);
		joints.add(joint);

		if(bubble != null) {
			// FINAL REVOLUTE JOINT FROM LAST BODY TO BUBBLE
			anchor2.x = 0;
			anchor2.y = -1.0f;
			jointDef.bodyA = last;
			jointDef.bodyB = bubble;
			// System.out.println(bubble);
			jointDef.localAnchorA.set(anchor1);
			jointDef.localAnchorB.set(anchor2);
			jointDef.collideConnected = false;
			joint = world.createJoint(jointDef);
			joints.add(joint);
		}
		return true;
	}
	
	/**
	 * Destroys the physics Body(s) of this object if applicable,
	 * removing them from the world.
	 * 
	 * @param world Box2D world that stores body
	 */
	public void deactivatePhysics(World world) {
		super.deactivatePhysics(world);
		if (start != null) {
			start.deactivatePhysics(world);
		}
		if (finish != null) {
			finish.deactivatePhysics(world);
		}
	}
	
	/**
	 * Sets the texture for the individual planks
	 *
	 * @param texture the texture for the individual planks
	 */
	public void setTexture(TextureRegion texture) {
		for(Obstacle body : bodies) {
			((SimpleObstacle)body).setTexture(texture);
		}
	}
	
	/**
	 * Returns the texture for the individual planks
	 *
	 * @return the texture for the individual planks
	 */
	public TextureRegion getTexture() {
		if (bodies.size == 0) {
			return null;
		}
		return ((SimpleObstacle)bodies.get(0)).getTexture();
	}

	// @Override
	// public void sdraw(GameCanvas canvas){
	// 	for(Obstacle obj : bodies) {
	// 		float angle = obj.getAngle();
	// 		float x = ((BoxObstacle)obj).getWidth()*drawScale.x / 2;
	// 		float y = ((BoxObstacle)obj).getHeight()*drawScale.y / 2;
	// 		canvas.shape.setColor(Color.BROWN);
	// 		canvas.shape.rect(getX()*drawScale.x-x,getY()*drawScale.y-y,((BoxObstacle)obj).getWidth()*drawScale.x,((BoxObstacle)obj).getHeight()*drawScale.y);
	// 	}
	// }


	@Override
	public void draw(GameCanvas canvas) {
		// Delegate to components
		// for(Obstacle obj : bodies) {
		// 	obj.draw(canvas);
		// 	canvas.draw(Rope (obj));
		// }
	}




}