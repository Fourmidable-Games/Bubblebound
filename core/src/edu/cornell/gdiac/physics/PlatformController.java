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
import edu.cornell.gdiac.audio.AudioEngine;
import edu.cornell.gdiac.audio.AudioSource;
import edu.cornell.gdiac.audio.EffectFilter;
import edu.cornell.gdiac.audio.MusicQueue;
import edu.cornell.gdiac.physics.obstacle.*;
import java.util.*;

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

	private TextureRegion poisonTexture;
	private TextureRegion lucenTexture;


	private final Vector2 ROPE_LAUNCH_SPEED = new Vector2(1.7f, 7);
	private TextureRegion[] bodyTextures;
	/** The jump sound.  We only want to play once. */
	private Sound jumpSound;
	private long jumpId = -1;
	/** The weapon fire sound.  We only want to play once. */
	private Sound fireSound;
	private int printnum = 0;
	private long fireId = -1;
	/** The weapon pop sound.  We only want to play once. */
	private Sound plopSound;

	private Sound popSound;
	private long popID = -1;
	private long plopId = -1;
	/** The shoot rope sound.  We only want to play once. */
	private Sound shootRopeSound;
	private long shootRopeSoundId = -1;
	/** The release rope sound.  We only want to play once. */
	private Sound releaseRopeSound;
	private long releaseRopeSoundId = -1;

	private Sound windSound;
	private long windSoundID = -1;
	/** The level 1 background music sound.  We want it to loop. */
	private Sound level1MusicSunset;
	private long level1MusicSunsetID;

	private Sound level1MusicCave;
	private long level1MusicCaveID;
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
	private int BUBBLE_LIMIT = 4;

	private int bubbles_left = 4;

	private int bubble_regen_timer_max = 100;

	private  int bubble_regen_timer = bubble_regen_timer_max;



	/** Mark set to handle more sophisticated collision callbacks */
	protected ObjectSet<Fixture> sensorFixtures;

	private List<Bubble> bubbles = new ArrayList<Bubble>();

	private List<Enemy> enemies = new ArrayList<Enemy>();

	private List<LucenglazeSensor> lucens = new ArrayList<>();

	private List<PoisonGas> poisons = new ArrayList();

	/**
	 * Creates and initialize a new instance of the platformer game
	 *
	 * The game has default gravity and other settings
	 */
	public PlatformController() {
		super(DEFAULT_WIDTH*2,DEFAULT_HEIGHT*2,DEFAULT_GRAVITY);
		setDebug(false);
		setComplete(false);
		setFailure(false);
		world.setContactListener(this);
		sensorFixtures = new ObjectSet<Fixture>();
	}

	public List<TextureRegion> loadTexturesIntoLevelEditor() {
		textures.add(earthTile);
		textures.add(iceTile);
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
		poisonTexture = new TextureRegion(directory.getEntry("platform:gas", Texture.class));
		lucenTexture = new TextureRegion(directory.getEntry("platform:lucenglaze", Texture.class));

		jumpSound = directory.getEntry( "bubbleboundsfx:jump", Sound.class );
		fireSound = directory.getEntry( "bubbleboundsfx:ropeshoot", Sound.class );
		plopSound = directory.getEntry( "bubbleboundsfx:plop", Sound.class );
		popSound = directory.getEntry("bubbleboundsfx:pop", Sound.class);

		shootRopeSound = directory.getEntry( "bubbleboundsfx:ropeshoot", Sound.class );
		releaseRopeSound = directory.getEntry( "bubbleboundsfx:roperelease", Sound.class );
		windSound = directory.getEntry( "bubbleboundsfx:wind", Sound.class );
		level1MusicSunset = directory.getEntry( "bubbleboundsfx:level1sunsettheme", Sound.class );
		level1MusicCave = directory.getEntry( "bubbleboundsfx:level1cavetheme", Sound.class );
		constants = directory.getEntry( "platform:constants", JsonValue.class );
		volume = 1.0f;
		super.gatherAssets(directory);

	}

	/**
	 * Resets the status of the game so that we can play again.
	 *
	 * This method disposes of the world and creates a new one.
	 */
	public void reset() {
		bubbles_left = BUBBLE_LIMIT;
		bubble_regen_timer = bubble_regen_timer_max;
		updateBubbleCount(bubbles_left);
		Vector2 gravity = new Vector2(world.getGravity() );
		zones.clear();
		life = 1; //reset health
		if(rope != null){
			destructRope(rope);
		}
		for(Obstacle obj : objects) {
			if(obj.getName() !="bridge"){
				obj.deactivatePhysics(world);
			}
			
		}
		//actually find a way to delete and reinitialize these later
		level1MusicSunset.stop();
		level1MusicCave.stop();
		windSound.stop();

		objects.clear();
		bubbles.clear();
		enemies.clear();
		lucens.clear();
		poisons.clear();
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
		setSounds();



		LevelEditor Level1 = new LevelEditor();
		loadTexturesIntoLevelEditor();
		Level1.readTextures(textures);
		Level1.readJson();
		List<BoxObstacle> BoxList = Level1.getBoxes();
		List<Bubble> bubbleList = Level1.getBubbles();
		List<Zone> gravityZoneList = Level1.getGravityZones();
		List<Spike> spikes = Level1.getSpikes();
		goalDoor = Level1.getGoal();
		enemies = Level1.getEnemies();


		// Add level goal
		float dwidth  = goalTile.getRegionWidth()/scale.x;
		float dheight = goalTile.getRegionHeight()/scale.y;
		//Vector2 scale2 = new Vector2(16f, 16f);
		//scale2.x /= 2;
		//scale2.y /= 2;

		goalDoor.setBodyType(BodyDef.BodyType.StaticBody);
		goalDoor.setSensor(true);
		goalDoor.setDrawScale(scale);
		goalDoor.setTexture(goalTile);
		goalDoor.setName("goal");
		goalDoor.isGoal = true;
		addObject(goalDoor);
		for (int i = 0; i < gravityZoneList.size(); i++) {
			Zone gravZone = gravityZoneList.get(i);
			gravZone.scale = scale;
			addZone(gravZone);
		}

		for (int i = 0; i < BoxList.size(); i++) {
			BoxObstacle box = BoxList.get(i);
			box.setTexture(localeToTexture(box));
			box.setBodyType(BodyDef.BodyType.StaticBody);
			box.setDensity(0);
			box.setFriction(0);
			box.setRestitution(0);
			box.setDrawScale(scale);
			box.setName("box");
			addObject(box);
		}




		for (int i = 0; i < spikes.size(); i++) {
			Spike spike = spikes.get(i);
			spike.setBodyType(BodyDef.BodyType.StaticBody);
			spike.setDrawScale(scale);
			spike.setName("spike");
			spike.setTexture(spikeTexture);
			addObject(spike);
		}

		for (int i = 0; i < bubbleList.size(); i++) {
			Bubble wo = bubbleList.get(i);
			wo.setName("Bubble");
			wo.setBodyType(BodyDef.BodyType.DynamicBody);
			wo.setStatic(true);
			wo.setDrawScale(scale);
			wo.activatePhysics(world);
			wo.setDensity(1000f);
			wo.setTexture(bubble);
			bubbles.add(wo);
			addQueuedObject(wo);
		}

		for (int i = 0; i < enemies.size(); i++) {
			Enemy enemy = enemies.get(i);
			enemy.setDrawScale(scale);
			enemy.setTexture(dudeModel);
			addObject(enemy);
//			enemies.add(enemy); CRASHES GAME
//			addQueuedObject(enemy); //idk dif between add queued vs add
		}


		createLucenGlaze(12, 8);



		JsonValue defaults = constants.get("defaults");


	    // This world is heavier
		world.setGravity( new Vector2(0,defaults.getFloat("gravity",0)) );

		// Create dude
//		dwidth  = avatarTexture.getRegionWidth()/scale.x;
//		dheight = avatarTexture.getRegionHeight()/scale.y;


		dwidth  = avatarTexture.getRegionWidth()/scale.x;
		dheight = avatarTexture.getRegionHeight()/scale.y;
		avatar = new DudeModel(constants.get("dude"), dwidth, dheight);
		avatar.setDrawScale(scale);
		avatar.setTexture(avatarTexture);
		avatar.setName("avatar");
		addObject(avatar);


		//avatar.setGravityScale(-1);
		//avatar.setDensity(0.2F);
		// Create rope bridge
		setCamera(avatar.getX(), avatar.getY() + 0.5f);
		//System.out.println(wo);
		// System.out.println("change");

		volume = constants.getFloat("volume", 1.0f);
	}

	public void createPoisonGas(float x, float y, boolean fade){
		PoisonGas gas = new PoisonGas(x,y);
		gas.setFade(fade);
		gas.setDrawScale(scale);
		gas.setTexture(poisonTexture);
		addObject(gas);
		poisons.add(gas);
	}

	public void createLucenGlaze(float x, float y){ //takes in coords of lucenglaze itself(will adjust for sensor automatically)
		LucenglazeSensor lgs = new LucenglazeSensor(x, y);
		lgs.setLucen(createLucenObject(x, y));
		lgs.setDrawScale(scale);
		addObject(lgs);
		lucens.add(lgs);
	}
	public Lucenglaze createLucenObject(float x, float y){ //called by prev
		Lucenglaze lg = new Lucenglaze(x, y);
		lg.setDrawScale(scale);
		lg.setTexture(lucenTexture);
		addObject(lg);
		return lg;
	}

	public Bubble spawnBubble(Vector2 v){
		if(bubbles_left == 0) return null;
		Bubble wo2 = new Bubble(v,1, Bubble.BubbleType.FLOATING);
		//System.out.println("isFiniteBubbles: "+ InputController.getInstance().isFiniteBubbles());
		if(InputController.getInstance().isFiniteBubbles()){
			bubbles_left--;
		}
		//System.out.println("SPAWNED BUBBLE!, BUBBLES LEFT: " + bubbles_left);
		wo2.setName("Bubble" + (bubbles_left));
		System.out.print("Timer set for bubble");
		wo2.setStatic(false);
		wo2.setBodyType(BodyDef.BodyType.DynamicBody);
		wo2.setDrawScale(scale);
		wo2.activatePhysics(world);
		wo2.setDensity(10000f);
		wo2.setTexture(bubble);
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
		//System.out.println("preupdate");
		if (!super.preUpdate(dt)) {
			return false;
		}
		if(!avatar.isAlive()){
			setFailure(true);
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
	private int wait = 0;
	public void update(float dt) {
		updateBubbles();
		moveZones();
		updateSounds();
		updateCamera(avatar.getX()*scale.x, avatar.getY()*scale.y);
		updateObjectGravs();
		for(Enemy e : enemies){e.update();}
		updateLucens();
		updatePoisons();
		updateAvatar();
	}

	private void updateLucens(){
		for(LucenglazeSensor l : lucens) {
			List<Vector2> list = l.update();
			if(list != null){
				for(int i = 0; i < list.size(); i++){
					createPoisonGas(list.get(i).x, list.get(i).y, true);
				}
			}
		}
	}

	private void updatePoisons(){

		for(int i = 0; i < poisons.size(); i++){
			if(poisons.get(i) == null){
				continue;
			}
			poisons.get(i).update();
			if(poisons.get(i).faded){
				poisons.get(i).markRemoved(true);
			}
		}
	}

	private void updateBubbles(){
		/*System.out.println("NEXT CYCLE");
		System.out.print("[" + bubble_timer[0]);
		for(int i = 1; i < bubble_timer.length; i++){
			System.out.print(", " + bubble_timer[i]);
		}
		System.out.println("]");*/
		for(int i = 0; i < bubbles.size(); i++){
			Bubble b = bubbles.get(i);
			b.update();
			if(b.isPopped()){
				if(b.isGrappled()){
					destructRope(rope);
					avatar.setGrappling(false);
				}
				popBubble(b);
				i--;
			}
		}
	}


	//TODO more efficient
	private void updateObjectGravs(){
		for(int i = 0; i < objects.size(); i++){
			Body o = objects.get(i).getBody();
			objects.get(i).setGrav(1.0f);
			for(int j = 0; j < zones.size(); j++){
				//System.out.println(o.getPosition());
				if(zones.get(j).inBounds(o.getPosition().x, o.getPosition().y)){
					objects.get(i).setGrav(zones.get(j).getGrav());
				}
			}
		}
	}

	private TextureRegion localeToTexture(BoxObstacle box){
		Obstacle o = box;
			for(int j = 0; j < zones.size(); j++){
				//System.out.println(o.getPosition());
				if(zones.get(j).inBounds(o.getPosition().x, o.getPosition().y)){
					return iceTile;
				}
			}
			return earthTile;
	}

	private void updateAvatar(){
		Vector2 placeLocation;
		if(InputController.getInstance().isMouseControlls()){
//			System.out.println("MOUSE");
			placeLocation = InputController.getInstance().getCrossHair();
			float xoffset = (cameraCoords.x / scale.x) - (CAMERA_WIDTH / 2f); //find bottom left corner of camera
			float yoffset = (cameraCoords.y / scale.y) - (CAMERA_HEIGHT / 2f);
			placeLocation.x += xoffset;
			placeLocation.y += yoffset;
		}else{
			if(!avatar.isGrounded() && !avatar.isGrappling()){
				if(avatar.grav > 0){
					placeLocation = avatar.getPosition().add(avatar.getVX() * 0.6f,2);
				}else{
					placeLocation = avatar.getPosition().add(avatar.getVX() * 0.6f,-2);
				}

			}else if(avatar.isFacingRight()){
				if(avatar.grav > 0){
					placeLocation = avatar.getPosition().add(2.5f,1);
				}else{
					placeLocation = avatar.getPosition().add(2.5f,-1);
				}

			}else{
				if(avatar.grav > 0){
					placeLocation = avatar.getPosition().add(-2.5f,1);
				}else{
					placeLocation = avatar.getPosition().add(-2.5f,-1);
				}
			}
		}

		//update bubbles
		Bubble closest = bubbles.get(0);
		float min = Float.MAX_VALUE;
		for(int i = 0; i < bubbles.size(); i++){
			Bubble b = bubbles.get(i);
			if(b.statc){
				b.setLinearVelocity(new Vector2(0, 0));
			}else{
				b.setLinearVelocity(new Vector2(0, b.grav));
			}
			float d = b.getPosition().dst(placeLocation);
			b.setSelected(false);
			if(d < min){
				closest = b;
				min = d;
			}
		}

		avatar.setMovement(InputController.getInstance().getHorizontal() *avatar.getForce());
		avatar.setJumping(InputController.getInstance().didPrimary());
		avatar.setShooting(InputController.getInstance().didSecondary());
		//System.out.println("got to before bubble check");

		//do bubble stuff
		boolean spawned = false;
		if(InputController.getInstance().didTertiary()){
			//System.out.println("Did Tertiary Action, wait:" + wait);
			if(wait > 20) {
				if(!InputController.getInstance().isFiniteBubbles() || bubbles_left > 0){
					closest = spawnBubble(placeLocation);
					updateBubbleCount(bubbles_left);
				}
				wait = 0;
				spawned = true;
			}
		}

		//regen bubble
		if(InputController.getInstance().isFiniteBubbles()){
//			System.out.println("Grounded: " + avatar.isGrounded());
			if(avatar.isGrounded() && InputController.getInstance().isReloadBubblesOnGround()){
				if(bubble_regen_timer <= 0 && bubbles_left < BUBBLE_LIMIT){
					bubbles_left++;
					updateBubbleCount(bubbles_left);
					bubble_regen_timer = bubble_regen_timer_max;
				}
			}else{
				bubble_regen_timer = bubble_regen_timer_max;
			}
		}else{
			bubbles_left = BUBBLE_LIMIT;
		}
		bubble_regen_timer--;
		//System.out.println("Finite Bubbles?: " + InputController.getInstance().isFiniteBubbles());
		//System.out.println("Bubbles: " + bubbles_left);
		//System.out.println("Regen Bubbles?: " + InputController.getInstance().isReloadBubblesOnGround());

		if (closest != null) closest.setSelected(true);
		//System.out.println("got to after bubble check");
		Vector2 pos = avatar.getPosition();
		boolean destructRope = false;
		boolean constructRope = false;
		if(!spawned) { //temp prevents people from left and right clicking at same time (which breaks for some reason)
			if (avatar.isGrappling()) {
				if (InputController.getInstance().didBubble()) {
					avatar.setGrappling(false);
					destructRope = true;
				}
				if (InputController.getInstance().didBubble() && avatar.getPosition().dst(closest.getPosition()) < 5 && !rope.bubble.equals(closest.getBody())) {
					//				if(spawned){
					//					System.out.println("Hi");
					//					destructRope = true;
					//				}
					avatar.setGrappling(true);
					constructRope = true;
				}
			} else {
				if (InputController.getInstance().didBubble() && avatar.getPosition().dst(closest.getPosition()) < 5) {
					avatar.setGrappling(true);
					constructRope = true;
				}
			}
		}
		wait++;
		//System.out.println("After destruct construct stuff");
		// Add a bullet if we fire
		if (avatar.isShooting()) {
			//createBullet();
		}
		if(destructRope){
			destructRope(rope);
			releaseRopeSoundId = playSound(releaseRopeSound, releaseRopeSoundId, volume );
		}
		//System.out.println("After destruct");
		if(constructRope){
			//System.out.println("B4: " + pos)
			rope = createGrapple(closest);
			shootRopeSoundId = playSound( shootRopeSound, shootRopeSoundId, volume );
			//avatar.setPosition(pos);
		}
		//System.out.println("after construct");

		avatar.breathe(); //used for poison gas stuff
		avatar.applyForce();
		life = avatar.health / (float)avatar.MAX_HEALTH;//update health bar

		//bubblesleft = bubbles_left - 2;

	}

	private void setSounds(){
		level1MusicSunsetID = level1MusicSunset.loop(0.0f);
		level1MusicCaveID = level1MusicCave.loop(0.0f);
		windSoundID = windSound.loop(0.0f);
	}

	public void updateSounds(){
		if(avatar.getGravZone() == 1){
			level1MusicSunset.setVolume(level1MusicSunsetID,volume * 1f);
			level1MusicCave.setVolume(level1MusicCaveID,0.0f);
		}
		if(avatar.getGravZone() == -1){
			level1MusicSunset.setVolume(level1MusicSunsetID,0.0f);
			level1MusicCave.setVolume(level1MusicCaveID,1f);
		}
		if (avatar.justJumped()) {
			jumpSound.setVolume(jumpId,volume * 2f);
			jumpId = playSound( jumpSound, jumpId);
		}
		if (avatar.justGrounded()) {
			plopSound.setVolume(plopId,volume * 2f);
			plopId = playSound( plopSound, jumpId);
		}
			windSound.setVolume(windSoundID, Math.min((float) Math.abs((avatar.getVX() + (avatar.getVY() * 0.5)) * 0.06f),0.4f));

	}

	private RopeBridge createGrapple(Bubble bubble){
		bubble.setGrappled(true);
		float dwidth  = bridgeTexture.getRegionWidth()/scale.x;
		float dheight = bridgeTexture.getRegionHeight()/scale.y;
		RopeBridge bridge = new RopeBridge(constants.get("bridge"), dwidth,dheight,bubble.getBody(), avatar.getBody());
		bridge.setTexture(bridgeTexture);
		bridge.setDrawScale(scale);
		addQueuedObject(bridge);
		avatar.setGrounded(false);
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
//		avatar.setLinearVelocity(avatar.getLinearVelocity().scl(ROPE_LAUNCH));
		avatar.setLinearVelocity(new Vector2(avatar.getLinearVelocity().scl(ROPE_LAUNCH_SPEED.x).x,avatar.getLinearVelocity().scl(ROPE_LAUNCH_SPEED.y).y));
		rope = null;
		//avatar.setGrappling(false);
	}

	public void popBubble(Bubble bubble){
		bubble.setActive(false);
		bubble.stopDraw();
		bubbles.remove(bubble);
		popSound.setVolume(popID, volume * 10f);
		popID = playSound(popSound,popID,0.5f);
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
			//System.out.println("bd1: " + bd1.getName());
			//System.out.println("bd2: " + bd2.getName());

			// Test bullet collision with world
			if (bd1.getName().equals("bullet") && bd2 != avatar) {
		        removeBullet(bd1);
			}

			if (bd2.getName().equals("bullet") && bd1 != avatar) {
		        removeBullet(bd2);
			}


			if ((bd1 == avatar && (bd2.getName().equals("spike") || bd2.getName().equals("enemy"))) ||
				(bd2 == avatar && (bd1.getName().equals("spike") || bd2.getName().equals("enemy")))){
				if(!avatar.isInvincible()) {
					avatar.hurt();

				}
			}

			// See if we have landed on the ground.
			if ((avatar.getSensorName().equals(fd2) && avatar != bd1 && !bd1.getName().contains("Bubble") && !bd1.getName().contains("bridge")) ||
				(avatar.getSensorName().equals(fd1) && avatar != bd2 && !bd2.getName().contains("Bubble")&& !bd2.getName().contains("bridge"))) {
//				System.out.println("here");
//				System.out.println("BD1: " + bd1.getName());
//				System.out.println("BD2: " + bd2.getName());
				//printnum++;
				//System.out.print("PRINT NUM: " + printnum + "\navatar: " + avatar + "\navtarSensorName: " + avatar.getSensorName() +"\n FD1: " + fd1 + "\n fd2: " + fd2 + "\n bd1N: " + bd1 + " \n bd2: " + bd2 +"\n bd1.name: " + bd1.getName() + "\n bd2 name: " + bd2.getName() + "\n");


				avatar.setGrounded(true);
				sensorFixtures.add(avatar == bd1 ? fix2 : fix1); // Could have more than one ground
			}
			
			// Check for win condition
			if ((bd1 == avatar   && bd2 == goalDoor) ||
				(bd1 == goalDoor && bd2 == avatar)) {
				setComplete(true);
			}

			if ((bd1 == avatar && bd2.getName().equals("gas")) || (bd1.getName().equals("gas") && bd2 == avatar)){
				assert avatar.gas >= 0;
				avatar.gas++;
				if(avatar.gas > 0){
					avatar.setInGas(true);
				}
			}

			if ((bd1 == avatar && bd2.getName().equals("lucenglazesensor")) ){
				((LucenglazeSensor) bd2).activate();
			}else if((bd1.getName().equals("lucenglazesensor") && bd2 == avatar)){
				((LucenglazeSensor) bd1).activate();
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
		try {

			Obstacle cd1 = (Obstacle) body1.getUserData(); //idk man
			Obstacle cd2 = (Obstacle) body2.getUserData(); //copied begin contact but bd was already taken so used cd
			if ((cd1 == avatar && cd2.getName().equals("gas")) || (cd1.getName().equals("gas") && cd2 == avatar)) {
				avatar.gas--;
				assert avatar.gas >= 0;
				if(avatar.gas == 0){
					avatar.setInGas(false);
				}

			}
		}catch(Exception e){
			e.printStackTrace();
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
		popSound.stop(popID);
		fireSound.stop(fireId);
	}
}