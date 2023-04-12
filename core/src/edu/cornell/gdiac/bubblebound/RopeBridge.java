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
package edu.cornell.gdiac.bubblebound;

import com.badlogic.gdx.math.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.joints.*;
import com.badlogic.gdx.graphics.*;

import com.badlogic.gdx.utils.JsonValue;
import edu.cornell.gdiac.bubblebound.obstacle.*;

/**
 * A bridge with planks connected by revolute joints.
 *
 * Note that this class returns to static loading.  That is because there are
 * no other subclasses that we might loop through.
 */
public class RopeBridge extends ComplexObstacle {
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

	protected Vector2 anchor3;

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
	 * @param lwidth	The plank length
	 * @param lheight	The bridge thickness
	 */
	public RopeBridge(JsonValue data, float lwidth, float lheight, Body b, CapsuleObstacle a) {
		//super(data.get("pos").getFloat(0),data.get("pos").getFloat(1));
		super(b.getPosition().x, b.getPosition().y);
		// System.out.println(getPosition());
		setName("bridge");
		this.data = data;
		bubble = b;
		avatar = a.getBody();
		avatarCapsule = a;
		float x0 = a.getPosition().x;
		float y0 = a.getPosition().y;
		planksize = new Vector2(lwidth,lheight);
		linksize = planksize.x;

		anchor3 = new Vector2(0,avatarCapsule.getHeight()/2 * avatarCapsule.grav);
		dimension = new Vector2(data.getFloat("width",0),0.1f);
		// System.out.println("Dimension: " + dimension);
		float length = bubble.getPosition().dst(avatar.getPosition().add(anchor3));
		//	(float)Math.sqrt(Math.pow(bubble.getPosition().x-avatar.getPosition().x,2)+ Math.pow(bubble.getPosition().y-avatar.getPosition().y,2));
		Vector2 norm = new Vector2(bubble.getPosition().sub(avatar.getPosition().add(anchor3)));
		// System.out.println("Norm:" + norm);
		norm.nor();
		// System.out.println("Norm normed:" + norm);

		// If too small, only make one plank.;
		int nLinks = (int)(length / lwidth);
		if(nLinks > 6)	nLinks -= 3;
		System.out.println(nLinks);
		System.out.println();
		if (nLinks <= 1) {
			nLinks = 1;
			linksize = length;
			spacing = 0;
		} else {
			spacing = length - nLinks * linksize;
			spacing /= (nLinks-1);
		}

		// Create the planks
		planksize.x = linksize;

		Vector2 pos = new Vector2();

		for (int ii = 0; ii < nLinks; ii++) {
			float t = ii*(linksize+spacing) + linksize/2.0f;
			// System.out.println("Iteration " + ii);
			// System.out.println("init pos: " + pos);
			pos.set(norm);
			// System.out.println("norm pos: " + pos);
			pos.scl(t);
			// System.out.println("scale pos: " + pos);
			pos.add(avatar.getPosition().add(anchor3));
			// System.out.println("add pos: " + pos);
			BoxObstacle plank = new BoxObstacle(pos.x, pos.y, planksize.x, planksize.y);
			plank.isRope = true;
			plank.setGravityScale(0);
			plank.setName("plank"+ii);
			plank.setDensity(data.getFloat("density",0));
			bodies.add(plank);
		}
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
		Vector2 anchor2 = new Vector2(-linksize / 2, 0);
		Vector2 anchor3 = new Vector2(0,avatarCapsule.getHeight()/2 * avatarCapsule.grav);

		// Create the leftmost anchor
		// Normally, we would do this in constructor, but we have
		// reasons to not add the anchor to the bodies list.
		Vector2 pos = bodies.get(0).getPosition();
		pos.x -= linksize / 2;
		RevoluteJointDef jointDef = new RevoluteJointDef();
		Joint joint;
		jointDef.bodyA = bodies.get(0).getBody();
		jointDef.bodyB = avatar;
		// System.out.println(bubble);
		jointDef.localAnchorA.set(anchor2);
		jointDef.localAnchorB.set(anchor3);
		jointDef.collideConnected = false;
		joint = world.createJoint(jointDef);
		joints.add(joint);
		// Link the planks together
		anchor1.x = linksize / 2;
		DistanceJointDef distJointDef = new DistanceJointDef();
		for (int ii = 0; ii < bodies.size-1; ii++) {
			jointDef.bodyA = bodies.get(ii).getBody();
			jointDef.bodyB = bodies.get(ii + 1).getBody();
			jointDef.localAnchorA.set(anchor1);
			jointDef.localAnchorB.set(anchor2);
			jointDef.collideConnected = false;
			joint = world.createJoint(jointDef);
			joints.add(joint);
			//#region INSERT CODE HERE
			// Look at what we did above
			Vector2 anchor11 = new Vector2(0,0);
			Vector2 anchor22 = new Vector2(0, 0);
			distJointDef.bodyA = bodies.get(ii).getBody();
			distJointDef.bodyB = bodies.get(ii + 1).getBody();
			distJointDef.localAnchorA.set(anchor11);
			distJointDef.localAnchorB.set(anchor22);
			distJointDef.collideConnected = false;
			distJointDef.length = 0.2f;
			distJointDef.dampingRatio = 2;

			joint = world.createJoint(distJointDef);
			joints.add(joint);
			//#endregion
		}

		// Create the rightmost anchor
		Obstacle last = bodies.get(bodies.size-1);

		if(bubble != null) {
			// Final joint
			anchor2.x = 0;
			anchor2.y = 0;
			jointDef.bodyA = last.getBody();
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


//	@Override
//	public void draw(GameCanvas canvas) {
//		// Delegate to components
//		for(Obstacle obj : bodies) {
//			obj.draw(canvas);
//			canvas.draw(Rope (obj));
//		}
//	}

	public float getFirstLinkRotation(){
		Vector2 pos1 = avatar.getPosition();
		Vector2 pos2 = ((bodies.size == -1) ? bodies.get(4).getBody() : bubble).getPosition();
		float angle = (float) Math.atan((pos1.y-pos2.y)/(pos1.x-pos2.x));
		angle = (float) Math.toDegrees(angle);
		//System.out.println(angle);
		//angle -= 180;
		return (avatarCapsule.grav == 1) ? angle : angle + 180;
	}


}