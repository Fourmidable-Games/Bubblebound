/*
 * DudeModel.java
 *
 * You SHOULD NOT need to modify this file.  However, you may learn valuable lessons
 * for the rest of the lab by looking at it.
 *
 * Author: Walker M. White
 * Based on original PhysicsDemo Lab by Don Holden, 2007
 * Updated asset version, 2/6/2021
 */
package edu.cornell.gdiac.bubblebound;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.physics.box2d.*;

import com.badlogic.gdx.utils.JsonValue;
import edu.cornell.gdiac.bubblebound.*;
import edu.cornell.gdiac.bubblebound.obstacle.*;
import edu.cornell.gdiac.bubblebound.PlayerController;

/**
 * Player avatar for the plaform game.
 *
 * Note that this class returns to static loading.  That is because there are
 * no other subclasses that we might loop through.
 */
public class DudeModel extends CapsuleObstacle {
	private PlayerController playerController;

	/** The initializing data (to avoid magic numbers) */
	private final JsonValue data;

	/** The factor to multiply by the input */
	private final float force;
	/** The amount to slow the character down */
	private final float damping;
	/** The maximum character speed */
	private final float maxspeed;
	/** Identifier to allow us to track the sensor in ContactListener */
	private final String sensorName;
	/** The impulse for the character jump */
	private final float jump_force;

	/** The current horizontal movement of the character */
	private float   movement;
	/** Which direction is the character facing */
	private boolean faceRight;

	/** The physics shape of this object */

	private float acceleration;

	private int gravZone;
	private PolygonShape sensorShape;


	public static int MAX_HEALTH = 4;

	public int health;

	public boolean invincible = false;

	public int invincibletimer = 30;

	public int breath = 50;

	public boolean inGas = false;
	public int gas = 0;

	
	/** Cache for internal force calculations */
	private final Vector2 forceCache = new Vector2();


	/**
	 * Returns left/right movement of this character.
	 * 
	 * This is the result of input times dude force.
	 *
	 * @return left/right movement of this character.
	 */
	public float getMovement() {
		return movement;
	}
	
	/**
	 * Sets left/right movement of this character.
	 * 
	 * This is the result of input times dude force.
	 *
	 * @param value left/right movement of this character.
	 */
	public void setMovement(float value) {
		movement = value; 
		if(isGrappling()){
			movement *= 1.5;
		}
		// Change facing if appropriate
		if (movement < 0) {
			playerController.setFacingRight(false);
		} else if (movement > 0) {
			playerController.setFacingRight(true);
		}
	}

	/**
	 * Returns the direction of gravity of the player (1 -> down, -1 -> up).
	 *
	 * @return left/right movement of this character.
	 */
	public float getGravZone() {
		return grav;
	}

	/**
	 * Returns true if the dude is actively firing.
	 *
	 * @return true if the dude is actively firing.
	 */
	public boolean isShooting() {
		return playerController.isShooting();
	}

	public int getHealth(){
		return playerController.getHealth();
	}

	public int getMaxHealth(){
		return playerController.getMaxHealth();
	}



	public boolean isGrappling(){return playerController.isGrappling();}
	
	/**
	 * Sets whether the dude is actively firing.
	 *
	 * @param value whether the dude is actively firing.
	 */
	public void setShooting(boolean value) {
		playerController.setShooting(value);
	}

	public void setGrappling(boolean value){playerController.setGrappling(value);}

	/**
	 * Returns true if the dude is actively jumping.
	 *
	 * @return true if the dude is actively jumping.
	 */
	public boolean isJumping() {
		return playerController.isJumping();
	}

	/**
	 * Returns true if the dude just jumped.
	 *
	 * @return true if the dude just jumped.
	 */
	public boolean justJumped() { return playerController.justJumped(); }

	/**
	 * Sets whether the dude is actively jumping.
	 *
	 * @param value whether the dude is actively jumping.
	 */
	public void setJumping(boolean value) {
		playerController.setJumping(value);
	}

	/**
	 * Returns true if the dude is on the ground.
	 *
	 * @return true if the dude is on the ground.
	 */
	public boolean isGrounded() {
		return playerController.isGrounded();
	}

	/**
	 * Returns true if the dude just landed on the ground
	 *
	 * @return true if the just landed on the ground
	 */
	public boolean justGrounded() {
		return playerController.justGrounded();
	}
	
	/**
	 * Sets whether the dude is on the ground.
	 *
	 * @param value whether the dude is on the ground.
	 */
	public void setGrounded(boolean value) {
		playerController.setGrounded(value);
	}

	/**
	 * Returns how much force to apply to get the dude moving
	 *
	 * Multiply this by the input to get the movement value.
	 *
	 * @return how much force to apply to get the dude moving
	 */
	public float getForce() {
		return force;
	}

	/**
	 * Returns ow hard the brakes are applied to get a dude to stop moving
	 *
	 * @return ow hard the brakes are applied to get a dude to stop moving
	 */
	public float getDamping() {
		return damping;
	}
	
	/**
	 * Returns the upper limit on dude left-right movement.  
	 *
	 * This does NOT apply to vertical movement.
	 *
	 * @return the upper limit on dude left-right movement.  
	 */
	public float getMaxSpeed() {
		return maxspeed;
	}

	/**
	 * Returns the name of the ground sensor
	 *
	 * This is used by ContactListener
	 *
	 * @return the name of the ground sensor
	 */
	public String getSensorName() { 
		return sensorName;
	}

	/**
	 * Returns true if this character is facing right
	 *
	 * @return true if this character is facing right
	 */
	public boolean isFacingRight() {
		return playerController.isFacingRight();
	}
	public void setFacingRight(boolean value) {
		playerController.setFacingRight(value);
	}

	/**
	 * Creates a new dude avatar with the given physics data
	 *
	 * The size is expressed in physics units NOT pixels.  In order for 
	 * drawing to work properly, you MUST set the drawScale. The drawScale 
	 * converts the physics units to pixels.
	 *
	 * @param data  	The physics constants for this dude
	 * @param width		The object width in physics units
	 * @param height	The object width in physics units
	 */
	public DudeModel(JsonValue data, float width, float height, float x, float y) {
		// The shrink factors fit the image to a tigher hitbox
		super(	x,
				y,
				width*data.get("shrink").getFloat( 0 ),
				height*data.get("shrink").getFloat( 1 ));
        setDensity(data.getFloat("density", 0));
		setFriction(data.getFloat("friction", 0));  /// HE WILL STICK TO WALLS IF YOU FORGET
		setFixedRotation(true);
		maxspeed = data.getFloat("maxspeed", 0);
		damping = data.getFloat("damping", 0);
		force = data.getFloat("force", 0)*0.5f;
		gravZone = 1;
		jump_force = data.getFloat( "jump_force", 0 )*1f;
		sensorName = "DudeGroundSensor";

		this.playerController = new PlayerController(data);
		this.data = data;
		setName("dude");
	}


	/**
	 * Creates the physics Body(s) for this object, adding them to the world.
	 *
	 * This method overrides the base method to keep your ship from spinning.
	 *
	 * @param world Box2D world to store body
	 *
	 * @return true if object allocation succeeded
	 */
	public boolean activatePhysics(World world) {
		// create the box from our superclass
		if (!super.activatePhysics(world)) {
			return false;
		}

		// Ground Sensor
		// -------------
		// We only allow the dude to jump when he's on the ground. 
		// Double jumping is not allowed.
		//
		// To determine whether or not the dude is on the ground, 
		// we create a thin sensor under his feet, which reports 
		// collisions with the world but has no collision response.
		Vector2 sensorCenter = new Vector2(0, -getHeight() / 2);
		FixtureDef sensorDef = new FixtureDef();
		sensorDef.density = data.getFloat("density",0);
		sensorDef.isSensor = true;
		sensorShape = new PolygonShape();
		JsonValue sensorjv = data.get("sensor");
		sensorShape.setAsBox(sensorjv.getFloat("shrink",0)*getWidth()/2.0f,
								 sensorjv.getFloat("height",0), sensorCenter, 0.0f);
		sensorDef.shape = sensorShape;
		// Ground sensor to represent our feet
		Fixture sensorFixture = body.createFixture( sensorDef );
		sensorFixture.setUserData(getSensorName());
		Vector2 sensorCenter2 = new Vector2(0, getHeight() / 2);
		FixtureDef sensorDef2 = new FixtureDef();
		sensorDef2.density = data.getFloat("density",0);
		sensorDef2.isSensor = true;
		PolygonShape sensorShape2 = new PolygonShape();
		JsonValue sensorjv2 = data.get("sensor");
		sensorShape2.setAsBox(sensorjv2.getFloat("shrink",0)*getWidth()/2.0f,
				sensorjv2.getFloat("height",0), sensorCenter2, 0.0f);
		sensorDef2.shape = sensorShape2;

		// Ground sensor to represent our feet
		Fixture sensorFixture2 = body.createFixture( sensorDef2 );
		sensorFixture2.setUserData(getSensorName());


		return true;
	}


	public boolean damp = true;
	/**
	 * Applies the force to the body of this dude
	 *
	 * This method should be called after the force attribute is set.
	 */
	public void applyForce() {
		body.setGravityScale(grav);
		if (!isActive()) {
			return;
		}
		if(playerController.isGrappling() && !(getMovement() == 0)){
			body.setGravityScale(grav * 1.5f);
			damp = false;
		}else{
			damp = true;
		}

		// Don't want to be moving. Damp out player motion
		if ((getMovement() == 0 || getVX() * getMovement() < 0)  && (!playerController.isGrappling()) ){
			if(!damp){
				forceCache.set(-getDamping()*getVX()*0.1f,0);
			}else{
				forceCache.set(-getDamping()*getVX()*0.5f,0);
			}
			body.applyForce(forceCache,getPosition(),true);
		}
		/*float regDampFactor = 0.7f;
		float grapDampFactor = 1.3f;
		float usedFactor;*/
		// Velocity too high, clamp it
		/*if(isGrappling){
			usedFactor = grapDampFactor;
		}else{
			usedFactor = regDampFactor;
		}*/

		if (getVX() >= getMaxSpeed()*1.5f) {
			setVX(getMaxSpeed()*1.5f);
		} if (getVX() <= -getMaxSpeed()*1.5f) {
			setVX(-getMaxSpeed()*1.5f);
		} if (getVY() >= 2f*getMaxSpeed()) {
			setVY(2f * getMaxSpeed());
		}

		forceCache.set(getMovement(),0);
		body.applyForce(forceCache,getPosition(),true);



		if (isJumping()) {
			forceCache.set(0, grav * jump_force);
			forceCache.x *= 3;
			body.applyLinearImpulse(forceCache,getPosition(),true);
		}
	}


	/**
	 * Updates the object's physics state (NOT GAME LOGIC).
	 *
	 * We use this method to reset cooldowns.
	 *
	 * @param dt	Number of seconds since last animation frame
	 */
	public void update(float dt) {
		playerController.update();
		super.update(dt);
	}

	public boolean isInvincible(){
		return playerController.isInvincible();
	}

	public boolean isAlive(){
		return playerController.isAlive();
	}

	public float getLife(){
		return playerController.getHealth() / (float)playerController.getMaxHealth();
	}

	public void hurt(){
		playerController.hurt();
	}

	public void setInGas(boolean b){
		inGas = b;
	}

	public boolean displayBreath = false;

	public void breathe(){
		if(inGas){
			if(breath > 0) {
				breath--;
			}
			if(breath % 10 == 0) {
				System.out.println("Breath: " + breath);
			}
			displayBreath = true;
			if(breath == 0){
				if(!invincible){
					hurt();
				}
			}
		}else{
			if(breath < 50){
				breath++;
				displayBreath = true;
			}else{
				displayBreath = false;
			}
		}
	}


	/**
	 * Draws the physics object.
	 *
	 * @param canvas Drawing context
	 */
	public void draw(GameCanvas canvas) {

		float x = getWidth()*drawScale.x / 2;
		float y = getHeight()*drawScale.y / 2;

		float effect = faceRight ? -1.0f : 1.0f;;
		float upside = (grav == -1) ? -1.0f : 1.0f;
		if(playerController.isInvincible){

		}
		else {
			canvas.draw(texture, Color.WHITE,origin.x,origin.y,getX()*drawScale.x,getY()*drawScale.y,getAngle(),effect,upside);
		}



//		float effect = faceRight ? 1.0f : -1.0f;
//		canvas.draw(texture,Color.WHITE,origin.x,origin.y,getX()*drawScale.x,getY()*drawScale.y,getAngle(),effect,1.0f);
	}

	@Override
	public void sdraw(GameCanvas canvas){
		float x = getWidth()*drawScale.x/2;
		float y = getHeight()*drawScale.y/2;
		canvas.shape.setColor(Color.CORAL);
		if(playerController.isInvincible()){
			return;
		}else{
			canvas.shape.rect(getX()*drawScale.x-x,getY()*drawScale.y-y,
					getWidth()*drawScale.x,getHeight()*drawScale.y);
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
		super.drawDebug(canvas);
		canvas.drawPhysics(sensorShape,Color.RED,getX(),getY(),getAngle(),drawScale.x,drawScale.y);
	}
}