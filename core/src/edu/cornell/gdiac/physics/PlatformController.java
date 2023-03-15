/*
 * PlatformController.java
 *
 * You SHOULD NOT need to modify this file.  However, you may learn valuable lessons
 * for the rest of the lab by looking at it.
 *
 * Author: Walker M. White
 * Based on original PhysicsDemo Lab by Don Holden, 2007
 * Updated asset version, 2/6/2021
 */
package edu.cornell.gdiac.physics;

import com.badlogic.gdx.math.*;
import com.badlogic.gdx.utils.*;
import com.badlogic.gdx.audio.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.physics.box2d.*;

import edu.cornell.gdiac.assets.AssetDirectory;
import edu.cornell.gdiac.physics.obstacle.*;
import java.util.*;
import java.util.logging.Level;

/**
 * Gameplay specific controller for the platformer game.  
 *
 * You will notice that asset loading is not done with static methods this time.  
 * Instance asset loading makes it easier to process our game modes in a loop, which 
 * is much more scalable. However, we still want the assets themselves to be static.
 * This is the purpose of our AssetState variable; it ensures that multiple instances
 * place nicely with the static assets.
 */
public class PlatformController extends WorldController implements ContactListener {
	/** Texture asset for character avatar */
	private TextureRegion avatarTexture;
	/** Texture asset for the bullet */
	private TextureRegion bulletTexture;
	/** Texture asset for the bridge plank */
	private TextureRegion bridgeTexture;

	private TextureRegion barrierTexture;
	private TextureRegion[] bodyTextures;
	/** The jump sound.  We only want to play once. */
	private Sound jumpSound;
	private long jumpId = -1;
	/** The weapon fire sound.  We only want to play once. */
	private Sound fireSound;
	private long fireId = -1;
	/** The weapon pop sound.  We only want to play once. */
	private Sound plopSound;
	private long plopId = -1;
	/** The default sound volume */
	private float volume;
	private RopeBridge rope;

	// Physics objects for the game
	/** Physics constants for initialization */
	private JsonValue constants;
	/** Reference to the character avatar */
	private DudeModel avatar;
	/** Reference to the goalDoor (for collision detection) */
	private BoxObstacle goalDoor;
	private final int BUBBLE_LIMIT = 10;

	/** Mark set to handle more sophisticated collision callbacks */
	protected ObjectSet<Fixture> sensorFixtures;

	private List<WheelObstacle> bubbles = new ArrayList<WheelObstacle>();

	/**
	 * Creates and initialize a new instance of the platformer game
	 *
	 * The game has default gravity and other settings
	 */
	public PlatformController() {
		super(DEFAULT_WIDTH,DEFAULT_HEIGHT,DEFAULT_GRAVITY);
		setDebug(false);
		setComplete(false);
		setFailure(false);
		world.setContactListener(this);
		sensorFixtures = new ObjectSet<Fixture>();
	}

	public List<TextureRegion> loadTexturesIntoLevelEditor() {
		textures.add(earthTile);
		textures.add(goalTile);
		return textures;
	}



	/**
	 * Gather the assets for this controller.
	 *
	 * This method extracts the asset variables from the given asset directory. It
	 * should only be called after the asset directory is completed.
	 *
	 * @param directory	Reference to global asset manager.
	 */
	public void gatherAssets(AssetDirectory directory) {
		avatarTexture  = new TextureRegion(directory.getEntry("platform:dude",Texture.class));
		bulletTexture = new TextureRegion(directory.getEntry("platform:bullet",Texture.class));
		bridgeTexture = new TextureRegion(directory.getEntry("platform:rope",Texture.class));
		barrierTexture = new TextureRegion(directory.getEntry("platform:barrier",Texture.class));
		jumpSound = directory.getEntry( "platform:jump", Sound.class );
		fireSound = directory.getEntry( "platform:pew", Sound.class );
		plopSound = directory.getEntry( "platform:plop", Sound.class );

		constants = directory.getEntry( "platform:constants", JsonValue.class );
		super.gatherAssets(directory);
	}

	/**
	 * Resets the status of the game so that we can play again.
	 *
	 * This method disposes of the world and creates a new one.
	 */
	public void reset() {
		Vector2 gravity = new Vector2(world.getGravity() );
		if(rope != null){
			destructRope(rope);
		}
		for(Obstacle obj : objects) {
			if(obj.getName() !="bridge"){
				obj.deactivatePhysics(world);
			}
			
		}


		objects.clear();
		bubbles.clear();

		addQueue.clear();

		
		world.dispose();
		
		world = new World(gravity,false);
		world.setContactListener(this);
		
		setComplete(false);
		setFailure(false);
		populateLevel();
	}

	/**
	 * Lays out the game geography.
	 */
	private void populateLevel() {
		// Add level goal
		float dwidth  = goalTile.getRegionWidth()/scale.x;
		float dheight = goalTile.getRegionHeight()/scale.y;
		//Vector2 scale2 = new Vector2(16f, 16f);
		//scale2.x /= 2;
		//scale2.y /= 2;
		JsonValue goal = constants.get("goal");
		JsonValue goalpos = goal.get("pos");
		goalDoor = new BoxObstacle(goalpos.getFloat(0),goalpos.getFloat(1),dwidth,dheight);
		goalDoor.setBodyType(BodyDef.BodyType.StaticBody);
		goalDoor.setDensity(goal.getFloat("density", 0));
		goalDoor.setFriction(goal.getFloat("friction", 0));
		goalDoor.setRestitution(goal.getFloat("restitution", 0));
		goalDoor.setSensor(true);
		goalDoor.setDrawScale(scale);
		goalDoor.setTexture(goalTile);
		goalDoor.setName("goal");
		//addObject(goalDoor);


		LevelEditor Level1 = new LevelEditor();
		loadTexturesIntoLevelEditor();
		Level1.readTextures(textures);
		Level1.readJson();
		List<BoxObstacle> BoxList = Level1.getBoxes();

		//BoxObstacle box = new BoxObstacle(0, 0, 20, 2);
		for (int i = 0; i < BoxList.size(); i++) {
			BoxObstacle box = BoxList.get(i);
			box.setTexture(earthTile);
			box.setBodyType(BodyDef.BodyType.StaticBody);
			box.setDensity(0);
			box.setFriction(0);
			box.setRestitution(0);
			box.setDrawScale(scale);
			box.setName("box");
			addObject(box);
		}

//		BoxObstacle box = BoxList.get(0);
//		box.setTexture(earthTile);
//		box.setBodyType(BodyDef.BodyType.StaticBody);
//		box.setDensity(0);
//		box.setFriction(0);
//		box.setRestitution(0);
//		box.setDrawScale(scale);
//		box.setName("box");
//		addObject(box);


		BoxObstacle box2 = new BoxObstacle(10, 10, 1, 1);
		box2.setBodyType(BodyDef.BodyType.StaticBody);
		box2.setDensity(0);
		box2.setFriction(0);
		box2.setRestitution(0);
		box2.setDrawScale(scale);
		box2.setName("box");
		addObject(box2);



		addZone(new Zone(10, 0, 10, 6, -1.0f, scale));
		Zone z = new Zone(20, 0 ,10 ,32, -1.0f, scale);
		//z.setMove(-0.01f, 0);
		addZone(z);

		WheelObstacle wo = new WheelObstacle(5, 5, 1f);
		wo.setName("Bubble");
		wo.setBodyType(BodyDef.BodyType.DynamicBody);
		wo.setStatic(true);
		wo.setDrawScale(scale);
		wo.activatePhysics(world);
		wo.setDensity(1000f);
		wo.setTexture(earthTile);
		bubbles.add(wo);
		addQueuedObject(wo);

		WheelObstacle wo2 = new WheelObstacle(25, 13, 1f);
		wo2.setName("Bubble");
		wo2.setStatic(true);
		wo2.setBodyType(BodyDef.BodyType.DynamicBody);
		wo2.setDrawScale(scale);
		wo2.activatePhysics(world);
		wo2.setDensity(1000f);
		wo2.setTexture(earthTile);
		bubbles.add(wo2);
		addQueuedObject(wo2);
		JsonValue defaults = constants.get("defaults");


	    // This world is heavier
		world.setGravity( new Vector2(0,defaults.getFloat("gravity",0)) );

		// Create dude
		dwidth  = avatarTexture.getRegionWidth()/scale.x;
		dheight = avatarTexture.getRegionHeight()/scale.y;
		avatar = new DudeModel(constants.get("dude"), dwidth, dheight);
		avatar.setDrawScale(scale);
		avatar.setTexture(avatarTexture);
		avatar.setName("Avatar");
		addObject(avatar);
		avatar.setGravityScale(-1);
		// Create rope bridge
		
		System.out.println(wo);
		System.out.println("change");

		volume = constants.getFloat("volume", 1.0f);
	}

	public WheelObstacle spawnBubble(Vector2 v, boolean b){
		if(bubbles.size() >= BUBBLE_LIMIT) return null;
		WheelObstacle wo2 = new WheelObstacle(v.x, v.y, 1f);
		wo2.setName("Bubble");
		wo2.setStatic(b);
		wo2.setBodyType(BodyDef.BodyType.DynamicBody);
		wo2.setDrawScale(scale);
		wo2.activatePhysics(world);
		wo2.setDensity(10000f);
		wo2.setTexture(earthTile);
		bubbles.add(wo2);
		addQueuedObject(wo2);
		return wo2;
	}
	
	/**
	 * Returns whether to process the update loop
	 *
	 * At the start of the update loop, we check if it is time
	 * to switch to a new game mode.  If not, the update proceeds
	 * normally.
	 *
	 * @param dt	Number of seconds since last animation frame
	 * 
	 * @return whether to process the update loop
	 */
	public boolean preUpdate(float dt) {
		if (!super.preUpdate(dt)) {
			return false;
		}
		
		if (!isFailure() && avatar.getY() < -1) {
			setFailure(true);
			return false;
		}
		
		return true;
	}

	/**
	 * The core gameplay loop of this world.
	 *
	 * This method contains the specific update code for this mini-game. It does
	 * not handle collisions, as those are managed by the parent class WorldController.
	 * This method is called after input is read, but before collisions are resolved.
	 * The very last thing that it should do is apply forces to the appropriate objects.
	 *
	 * @param dt	Number of seconds since last animation frame
	 */

	boolean sbubble = false;
	boolean betterSwinging = true;
	private int wait = 10;
	public void update(float dt) {
		// Process actions in object model
		moveZones();
		for(int i = 0; i < objects.size(); i++){
			Body o = objects.get(i).getBody();
			objects.get(i).setGrav(1.0f);
			for(int j = 0; j < zones.size(); j++){
				//System.out.println(o.getPosition());
				if(zones.get(j).inBounds(o.getPosition().x, o.getPosition().y)){

					objects.get(i).setGrav(zones.get(j).getGrav());
				}
			}
//			if(objects.get(i).getName().equals("Avatar")){
//				DudeModel d = (DudeModel) objects.get(i);
//				d.swapGravity(world, o.getGravityScale());
//			}
		}
		if(InputController.getInstance().didSecondary()){
			sbubble = !sbubble;
		}
		Vector2 crosshair = InputController.getInstance().getCrossHair();




		WheelObstacle closest = bubbles.get(0);
		float min = Float.MAX_VALUE;
		for(int i = 0; i < bubbles.size(); i++){
			WheelObstacle b = bubbles.get(i);
			if(b.statc){
				b.setLinearVelocity(new Vector2(0,0));
			}else{
				b.setLinearVelocity(new Vector2(0, b.grav));
			}

			float d = b.getPosition().dst(crosshair);
			b.setSelected(false);
			if(d < min){
				closest = b;
				min = d;
			}
		}

		avatar.setMovement(InputController.getInstance().getHorizontal() *avatar.getForce());
		avatar.setJumping(InputController.getInstance().didPrimary());
		avatar.setShooting(InputController.getInstance().didSecondary());

		boolean spawned = false;
		if(InputController.getInstance().didTertiary()){
			if(wait > 10) {
				closest = spawnBubble(crosshair, sbubble);
				wait = 0;
				spawned = true;
			}
		}

		if (closest != null) closest.setSelected(true);

		if(InputController.getInstance().didDebug()){
			betterSwinging = !betterSwinging;
		}
		Vector2 pos = avatar.getPosition();
		boolean destructRope = false;
		boolean constructRope = false;
		if(avatar.isGrappling()){
			if(InputController.getInstance().didBubble()){
				avatar.setGrappling(false);
				destructRope = true;
			}
			if(betterSwinging) {
				//if((InputController.getInstance().didBubble() || spawned) && avatar.getPosition().dst(closest.getPosition()) < 5 && !rope.bubble.equals(closest.getBody())){
				if (InputController.getInstance().didBubble() && avatar.getPosition().dst(closest.getPosition()) < 5 && !rope.bubble.equals(closest.getBody())) {
//				if(spawned){
//					System.out.println("Hi");
//					destructRope = true;
//				}
					avatar.setGrappling(true);
					constructRope = true;
				}
			}
		}else{
			if(InputController.getInstance().didBubble() && avatar.getPosition().dst(closest.getPosition()) < 5){
				avatar.setGrappling(true);
				constructRope = true;
			}
		}
		wait++;

		
		// Add a bullet if we fire
		if (avatar.isShooting()) {
			//createBullet();
		}
		if(destructRope){
			destructRope(rope);
		}
		if(constructRope){
			//System.out.println("B4: " + pos);
			rope = createGrapple(closest);
			//avatar.setPosition(pos);
		}
		
		avatar.applyForce();
	    if (avatar.isJumping()) {
	    	jumpId = playSound( jumpSound, jumpId, volume );
	    }
	}

	/**
	 * Add a new bullet to the world and send it in the right direction.
	 */
	private void createBullet() {
		JsonValue bulletjv = constants.get("bullet");
		float offset = bulletjv.getFloat("offset",0);
		offset *= (avatar.isFacingRight() ? 1 : -1);
		float radius = bulletTexture.getRegionWidth()/(2.0f*scale.x);
		WheelObstacle bullet = new WheelObstacle(avatar.getX()+offset, avatar.getY(), radius);
		
	    bullet.setName("bullet");
		bullet.setDensity(bulletjv.getFloat("density", 0));
	    bullet.setDrawScale(scale);
	    bullet.setTexture(bulletTexture);
	    bullet.setBullet(true);
	    bullet.setGravityScale(0);
		
		// Compute position and velocity
		float speed = bulletjv.getFloat( "speed", 0 );
		speed  *= (avatar.isFacingRight() ? 1 : -1);
		bullet.setVX(speed);
		addQueuedObject(bullet);

		fireId = playSound( fireSound, fireId );
	}

	private RopeBridge createGrapple(WheelObstacle bubble){
		float dwidth  = bridgeTexture.getRegionWidth()/scale.x;
		float dheight = bridgeTexture.getRegionHeight()/scale.y;
		RopeBridge bridge = new RopeBridge(constants.get("bridge"), dwidth, dheight, bubble.getBody(), avatar.getBody());
		bridge.setTexture(bridgeTexture);
		bridge.setDrawScale(scale);
		addQueuedObject(bridge);
		return bridge;
	}
	
	/**
	 * Remove a new bullet from the world.
	 *
	 * @param  bullet   the bullet to remove
	 */
	public void removeBullet(Obstacle bullet) {
	    bullet.markRemoved(true);
	    plopId = playSound( plopSound, plopId );
	}

	public void destructRope(Obstacle rope) {
		rope.markRemoved(true);
		avatar.setLinearVelocity(avatar.getLinearVelocity().scl(1.3f));
		rope = null;
	}

	
	/**
	 * Callback method for the start of a collision
	 *
	 * This method is called when we first get a collision between two objects.  We use 
	 * this method to test if it is the "right" kind of collision.  In particular, we
	 * use it to test if we made it to the win door.
	 *
	 * @param contact The two bodies that collided
	 */
	public void beginContact(Contact contact) {
		Fixture fix1 = contact.getFixtureA();
		Fixture fix2 = contact.getFixtureB();

		Body body1 = fix1.getBody();
		Body body2 = fix2.getBody();

		Object fd1 = fix1.getUserData();
		Object fd2 = fix2.getUserData();
		try {
			Obstacle bd1 = (Obstacle)body1.getUserData();
			Obstacle bd2 = (Obstacle)body2.getUserData();

			// Test bullet collision with world
			if (bd1.getName().equals("bullet") && bd2 != avatar) {
		        removeBullet(bd1);
			}

			if (bd2.getName().equals("bullet") && bd1 != avatar) {
		        removeBullet(bd2);
			}

			// See if we have landed on the ground.
			if ((avatar.getSensorName().equals(fd2) && avatar != bd1) ||
				(avatar.getSensorName().equals(fd1) && avatar != bd2)) {
				avatar.setGrounded(true);
				//sensorFixtures.add(avatar == bd1 ? fix2 : fix1); // Could have more than one ground
			}
			
			// Check for win condition
			if ((bd1 == avatar   && bd2 == goalDoor) ||
				(bd1 == goalDoor && bd2 == avatar)) {
				//setComplete(true);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Callback method for the start of a collision
	 *
	 * This method is called when two objects cease to touch.  The main use of this method
	 * is to determine when the characer is NOT on the ground.  This is how we prevent
	 * double jumping.
	 */ 
	public void endContact(Contact contact) {
		Fixture fix1 = contact.getFixtureA();
		Fixture fix2 = contact.getFixtureB();

		Body body1 = fix1.getBody();
		Body body2 = fix2.getBody();

		Object fd1 = fix1.getUserData();
		Object fd2 = fix2.getUserData();
		
		Object bd1 = body1.getUserData();
		Object bd2 = body2.getUserData();

		if ((avatar.getSensorName().equals(fd2) && avatar != bd1) ||
			(avatar.getSensorName().equals(fd1) && avatar != bd2)) {
			sensorFixtures.remove(avatar == bd1 ? fix2 : fix1);
			if (sensorFixtures.size == 0) {
				avatar.setGrounded(false);
			}
		}
	}
	
	/** Unused ContactListener method */
	public void postSolve(Contact contact, ContactImpulse impulse) {}
	/** Unused ContactListener method */
	public void preSolve(Contact contact, Manifold oldManifold) {}

	/**
	 * Called when the Screen is paused.
	 *
	 * We need this method to stop all sounds when we pause.
	 * Pausing happens when we switch game modes.
	 */
	public void pause() {
		jumpSound.stop(jumpId);
		plopSound.stop(plopId);
		fireSound.stop(fireId);
	}
}