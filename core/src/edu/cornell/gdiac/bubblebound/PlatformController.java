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
package edu.cornell.gdiac.bubblebound;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.utils.*;
import com.badlogic.gdx.audio.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.physics.box2d.*;

import edu.cornell.gdiac.assets.AssetDirectory;
import edu.cornell.gdiac.bubblebound.obstacle.BoxObstacle;
import edu.cornell.gdiac.bubblebound.obstacle.Obstacle;
import edu.cornell.gdiac.bubblebound.obstacle.*;
import edu.cornell.gdiac.util.PooledList;
import edu.cornell.gdiac.util.ScreenListener;

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
public class PlatformController implements ContactListener, Screen {
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

	private int BUBBLE_LIMIT = 4;

	private int bubbles_left = 4;

	private int bubble_regen_timer_max = 100;

	private  int bubble_regen_timer = bubble_regen_timer_max;



	/** Mark set to handle more sophisticated collision callbacks */
	protected ObjectSet<Fixture> sensorFixtures;


	//WORLD CONTROLLERRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRR

	protected TextureRegion earthTile;
	protected TextureRegion iceTile;
	/** The texture for the exit condition */
	protected TextureRegion goalTile;
	protected TextureRegion bubble;
	/** The font for giving messages to the player */
	protected TextureRegion background;
	protected Texture background2;
	protected TextureRegion losing;
	protected TextureRegion dudeModel;
	protected TextureRegion spikeTexture;
	protected BitmapFont displayFont;

	/** Exit code for quitting the game */
	public static final int EXIT_QUIT = 0;
	/** Exit code for advancing to next level */
	public static final int EXIT_NEXT = 1;
	/** Exit code for jumping back to previous level */
	public static final int EXIT_PREV = 2;
	/** How many frames after winning/losing do we continue? */
	public static final int EXIT_COUNT = 120;

	/** The amount of time for a physics engine step. */
	public static final float WORLD_STEP = 1/60.0f;
	/** Number of velocity iterations for the constrain solvers */
	public static final int WORLD_VELOC = 6;
	/** Number of position iterations for the constrain solvers */
	public static final int WORLD_POSIT = 2;

	/** Width of the game world in Box2d units */
	protected static final float DEFAULT_WIDTH  = 32.0f;
	/** Height of the game world in Box2d units */
	protected static final float DEFAULT_HEIGHT = 18.0f;
	/** The default value of gravity (going down) */
	protected static final float DEFAULT_GRAVITY = -4.9f;


	/** Reference to the game canvas */
	protected GameCanvas canvas;
	/** All the objects in the world. */
	protected PooledList<Obstacle> objects  = new PooledList<Obstacle>();
	/** Queue for adding objects */
	protected PooledList<Obstacle> addQueue = new PooledList<Obstacle>();
	/** Listener that will update the player mode when we are done */
	private ScreenListener listener;

	/** The Box2D world */
	protected World world;
	/** The boundary of the world */
	protected Rectangle bounds;
	/** The world scale */
	protected Vector2 scale;

	/** Whether or not this is an active controller */
	private boolean active;
	/** Whether we have completed this level */
	private boolean complete;
	/** Whether we have failed at this world (and need a reset) */
	private boolean failed;
	/** Whether or not debug mode is active */
	private boolean debug;
	/** Countdown active for winning or losing */
	private int countdown;

	public ArrayList<TextureRegion> textures = new ArrayList<TextureRegion>();

	private List<Bubble> bubbles = new ArrayList<Bubble>();

	private List<Enemy> enemies = new ArrayList<Enemy>();
	/** Reference to the character avatar */
	private DudeModel avatar;
	/** Reference to the goalDoor (for collision detection) */
	private BoxObstacle goalDoor;

	private List<LucenglazeSensor> lucens = new ArrayList<>();

	private List<PoisonGas> poisons = new ArrayList();


	/**
	 * Creates and initialize a new instance of the platformer game
	 *
	 * The game has default gravity and other settings
	 */
	public PlatformController() {
		Rectangle worldBounds = new Rectangle(0,0,DEFAULT_WIDTH*2,DEFAULT_HEIGHT*2);
		Vector2 worldGravityVector = new Vector2(0, DEFAULT_GRAVITY);
		world = new World(worldGravityVector,false);
		this.bounds = new Rectangle(worldBounds);
		this.scale = new Vector2(1,1);
		complete = false;
		failed = false;
		debug  = false;
		active = false;
		countdown = -1;
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

		earthTile = new TextureRegion(directory.getEntry( "shared:earth", Texture.class ));
		iceTile = new TextureRegion(directory.getEntry("shared:ice", Texture.class));
		dudeModel = new TextureRegion(directory.getEntry( "platform:dude2", Texture.class ));
		spikeTexture = new TextureRegion(directory.getEntry( "platform:spike", Texture.class ));
		goalTile  = new TextureRegion(directory.getEntry( "shared:goal", Texture.class ));
		background = new TextureRegion(directory.getEntry("background:underground", Texture.class));
		bubble = new TextureRegion(directory.getEntry( "shared:bubble", Texture.class ));
		displayFont = directory.getEntry( "shared:retro" ,BitmapFont.class);
		background2 = directory.getEntry("background:temp", Texture.class);
		losing = new TextureRegion(directory.getEntry("losing", Texture.class));
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

		Spike sp = new Spike(1, 1, 1, 1);
		sp.setBodyType(BodyDef.BodyType.StaticBody);
		sp.setDrawScale(scale);
		sp.setName("spike");
		sp.setTexture(spikeTexture);
		addObject(sp);



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
		wo2.setName("bubble");
		//System.out.print("Timer set for bubble");
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
		if (!preUpdateHelper(dt)) {
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
			if(b.timedOut()){
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
		if(bubble.isGrappled()){
			destructRope(rope);
			avatar.setGrappling(false);
		}
		bubble.markRemoved(true);
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

				avatar.hurt();
				life = avatar.getLife();

				if(!avatar.isInvincible()) {
					//avatar.hurt();
					if(bd1 == avatar){ //move it to player controller
						//TODO look prev comment

						Vector2 v2 = body1.getPosition().sub(body2.getPosition()).scl(15);
						body1.applyLinearImpulse(new Vector2(v2.x, v2.y), body1.getPosition(), true);

					}else{
						Vector2 v2 = body2.getPosition().sub(body1.getPosition()).scl(15);
						body2.applyLinearImpulse(new Vector2(v2.x, v2.y), body2.getPosition(), true);

					}
				}
			}

			// See if we have landed on the ground.
			if ((avatar.getSensorName().equals(fd2) && avatar != bd1 && !bd1.getName().equals("bubble") && !bd1.getName().contains("bridge") && !bd1.getName().equals("lucenglazesensor") && !bd1.getName().contains("gas")) ||
				(avatar.getSensorName().equals(fd1) && avatar != bd2 && !bd2.getName().equals("bubble") && !bd2.getName().contains("bridge") && !bd2.getName().equals("lucenglazesensor") && !bd2.getName().contains("gas"))) {

				avatar.setGrounded(true);
				sensorFixtures.add(avatar == bd1 ? fix2 : fix1); // Could have more than one ground
			}

			if ((bd1.getName().equals("bubble") && (bd2.getName().equals("enemy") || bd2.getName().equals("spike"))) ||
					(bd2.getName().equals("bubble") && (bd1.getName().equals("enemy") || bd1.getName().equals("spike")))){
				if(bd1.getName().equals("bubble")){
					popBubble((Bubble) bd1);

				}else{
					popBubble((Bubble) bd2);
				}
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



	/**
	 * Returns true if debug mode is active.
	 *
	 * If true, all objects will display their physics bodies.
	 *
	 * @return true if debug mode is active.
	 */
	public boolean isDebug( ) {
		return debug;
	}

	/**
	 * Sets whether debug mode is active.
	 *
	 * If true, all objects will display their physics bodies.
	 *
	 * @param value whether debug mode is active.
	 */
	public void setDebug(boolean value) {
		debug = value;
	}

	/**
	 * Returns true if the level is completed.
	 *
	 * If true, the level will advance after a countdown
	 *
	 * @return true if the level is completed.
	 */
	public boolean isComplete( ) {
		return complete;
	}

	/**
	 * Sets whether the level is completed.
	 *
	 * If true, the level will advance after a countdown
	 *
	 * @param value whether the level is completed.
	 */
	public void setComplete(boolean value) {
		if (value) {
			countdown = EXIT_COUNT;
		}
		complete = value;
	}

	/**
	 * Returns true if the level is failed.
	 *
	 * If true, the level will reset after a countdown
	 *
	 * @return true if the level is failed.
	 */
	public boolean isFailure( ) {
		return failed;
	}

	/**
	 * Sets whether the level is failed.
	 *
	 * If true, the level will reset after a countdown
	 *
	 * @param value whether the level is failed.
	 */
	public void setFailure(boolean value) {
		if (value) {
			countdown = EXIT_COUNT;
		}
		failed = value;
	}

	/**
	 * Returns true if this is the active screen
	 *
	 * @return true if this is the active screen
	 */
	public boolean isActive( ) {
		return active;
	}

	/**
	 * Returns the canvas associated with this controller
	 *
	 * The canvas is shared across all controllers
	 *
	 * @return the canvas associated with this controller
	 */
	public GameCanvas getCanvas() {
		return canvas;
	}

	/**
	 * Sets the canvas associated with this controller
	 *
	 * The canvas is shared across all controllers.  Setting this value will compute
	 * the drawing scale from the canvas size.
	 *
	 * @param canvas the canvas associated with this controller
	 */

	public static int CAMERA_WIDTH = 32;
	public static int CAMERA_HEIGHT = 18;

	public void setCanvas(GameCanvas canvas) {
		this.canvas = canvas;
		this.scale.x = canvas.getWidth()/CAMERA_WIDTH;
		this.scale.y = canvas.getHeight()/CAMERA_HEIGHT;
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
	public void resume(){

	}

	/**
	 * Dispose of all (non-static) resources allocated to this mode.
	 */
	public void dispose() {
		for(Obstacle obj : objects) {
			obj.deactivatePhysics(world);
		}
		objects.clear();
		addQueue.clear();
		zones.clear();
		world.dispose();
		objects = null;
		addQueue = null;
		bounds = null;
		scale  = null;
		world  = null;
		canvas = null;
	}


	/**
	 *
	 * Adds a physics object in to the insertion queue.
	 *
	 * Objects on the queue are added just before collision processing.  We do this to
	 * control object creation.
	 *
	 * param obj The object to add
	 */
	public void addQueuedObject(Obstacle obj) {
		assert inBounds(obj) : "Object is not in bounds";
		addQueue.add(obj);
	}

	/**
	 * Immediately adds the object to the physics world
	 *
	 * param obj The object to add
	 */
	protected void addObject(Obstacle obj) {
		assert inBounds(obj) : "Object is not in bounds";
		objects.add(obj);
		obj.activatePhysics(world);
	}

	/**
	 * Returns true if the object is in bounds.
	 *
	 * This assertion is useful for debugging the physics.
	 *
	 * @param obj The object to check.
	 *
	 * @return true if the object is in bounds.
	 */
	public boolean inBounds(Obstacle obj) {
		boolean horiz = (bounds.x <= obj.getX() && obj.getX() <= bounds.x+bounds.width);
		boolean vert  = (bounds.y <= obj.getY() && obj.getY() <= bounds.y+bounds.height);
		return horiz && vert;
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
	public boolean preUpdateHelper(float dt) {
		InputController input = InputController.getInstance();
		input.readInput(new Rectangle(0,0,CAMERA_WIDTH,CAMERA_HEIGHT), scale);//use camera rect instead of bounds
		if (listener == null) {
			return true;
		}

		// Toggle debug
		if (input.didDebug()) {
			debug = !debug;
		}

		// Handle resets
		if (input.didReset()) {
			reset();
		}

		// Now it is time to maybe switch screens.
		if (input.didExit()) {
			pause();
			listener.exitScreen(this, EXIT_QUIT);
			return false;
		} else if (input.didAdvance()) {
			pause();
			listener.exitScreen(this, EXIT_NEXT);
			return false;
		} else if (input.didRetreat()) {
			pause();
			listener.exitScreen(this, EXIT_PREV);
			return false;
		} else if (countdown > 0) {
			countdown--;
		} else if (countdown == 0) {
			if (failed) {
				reset();
			} else if (complete) {
				pause();
				listener.exitScreen(this, EXIT_NEXT);
				return false;
			}
		}
		return true;
	}

	/**
	 * Processes physics
	 *
	 * Once the update phase is over, but before we draw, we are ready to handle
	 * physics.  The primary method is the step() method in world.  This implementation
	 * works for all applications and should not need to be overwritten.
	 *
	 * @param dt	Number of seconds since last animation frame
	 */


	public void postUpdate(float dt) {
		// Add any objects created by actions
		while (!addQueue.isEmpty()) {
			addObject(addQueue.poll());
		}

		// Turn the physics engine crank.
		world.step(WORLD_STEP,WORLD_VELOC,WORLD_POSIT);

		// Garbage collect the deleted objects.
		// Note how we use the linked list nodes to delete O(1) in place.
		// This is O(n) without copying.
		Iterator<PooledList<Obstacle>.Entry> iterator = objects.entryIterator();
		while (iterator.hasNext()) {
			PooledList<Obstacle>.Entry entry = iterator.next();
			Obstacle obj = entry.getValue();
			if (obj.isRemoved()) {
				obj.deactivatePhysics(world);
				entry.remove();
			} else {
				// Note that update is called last!
				obj.update(dt);
			}
		}
	}
	public Vector2 cameraCoords = new Vector2(0, 0);

	public void setCamera(float x, float y){
		cameraCoords.set(x*scale.x, y*scale.y);
		cameraCoords.x += CAMERA_WIDTH*scale.x/3;
		cameraCoords.y += CAMERA_HEIGHT *scale.y/5; //put in leftish middle of screen
		canvas.camera.position.set(cameraCoords, 0);
		canvas.camera.update();
	}

	public void updateCamera(float x, float y){
		Vector2 temp = new Vector2(x + CAMERA_WIDTH*scale.x/10, y + CAMERA_HEIGHT *scale.y/5);
		temp.sub(cameraCoords).scl(0.1f, 0.5f); //0.01 is how much it lags in terms of x (smaller means it mvoes slower)
		boolean movex = true;					       //0.5 is how much it lags in terms of y
		boolean movey = true;

		if((temp.x > 0 && cameraCoords.x + (scale.x * CAMERA_WIDTH / 2) >= bounds.getWidth() * scale.x) || (temp.x < 0 && cameraCoords.x - (scale.x * CAMERA_WIDTH / 2) <= 0) ){
			movex = false; //check if camera reached left or right edge
		}
		if((temp.y > 0 && cameraCoords.y + (scale.y * CAMERA_HEIGHT / 2) >= bounds.getHeight() * scale.y) || (temp.y < 0 && cameraCoords.y - (scale.y * CAMERA_HEIGHT / 2) <= 5) ){
			movey = false; //check if camera reached top or bottom
		}
		if(movex){
			cameraCoords.x += temp.x;
		}
		if(movey){
			cameraCoords.y += temp.y;
		}

		canvas.camera.position.set(cameraCoords, 0);
		canvas.camera.update();
	}

	public List<Zone> zones = new ArrayList<>();

	float life = 1;

	public void addZone(Zone z){
		zones.add(z);
	}
	int bubblesleft = 8;
	/**
	 * Draw the physics objects to the canvas
	 *
	 * For simple worlds, this method is enough by itself.  It will need
	 * to be overriden if the world needs fancy backgrounds or the like.
	 *
	 * The method draws all objects in the order that they were added.
	 *
	 * @param dt	Number of seconds since last animation frame
	 */

	public void updateBubbleCount(int bubbles_left){
		bubblesleft = bubbles_left;
	}


	public void draw(float dt) {
		canvas.clear();
		canvas.begin();
		canvas.resetColor();
		canvas.drawWrapped(background, cameraCoords.x, 0f);


//		canvas.shape.setProjectionMatrix(canvas.camera.combined); TEST

		canvas.end();

		//TODO: parallaxing and stuff kinda relies on pixel size not ideal for diff screen sizes


		canvas.begin();
		for(Zone z: zones){ //draws the backgrounds of the zones
			z.drawBackground(background2, canvas, cameraCoords.x);
//			int y = background2.getHeight() - (int)(z.ypos * scale.y) - (int)(z.height * scale.y); //finds y coord
//			int x = canvas.wrapX(cameraCoords.x, background2.getWidth()) + (int)(z.xpos*scale.x); //find parallaxed x coord
//			TextureRegion temp = new TextureRegion(text, x, y,(int)(z.width*scale.x), (int)(z.height * scale.y)); //select only needed part of image
//			canvas.draw(temp, z.xpos * scale.x, z.ypos * scale.y);
		}
		canvas.resetColor();
		canvas.end();
		canvas.begin();
//		canvas.shape.begin(ShapeRenderer.ShapeType.Filled); testttttt

		for(Obstacle obj : objects) {
			obj.draw(canvas); ////!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
			//obj.sdraw(canvas);
			canvas.resetColor();
		}

		canvas.end();
//		canvas.shape.end();
		//canvas.shape.setProjectionMatrix(canvas.camera.combined);
		canvas.shape.begin(ShapeRenderer.ShapeType.Line);
		for(Zone z : zones){
			canvas.shape.setColor(Color.RED);
			z.sDraw(canvas);
		}
		canvas.shape.end();
		// Draw life bar



		canvas.shape.setProjectionMatrix(canvas.camera.combined);
		canvas.shape.begin(ShapeRenderer.ShapeType.Filled);
		canvas.shape.setColor(Color.RED);
		canvas.shape.rect( cameraCoords.x - (canvas.getWidth() / 2) + 10, cameraCoords.y + (canvas.getHeight() / 2) - 30, 200 * life, 20);
		canvas.shape.end();

		// Draw life bar label
		displayFont.setColor(Color.WHITE);
		displayFont.getData().setScale(0.4f);
		canvas.begin(); // DO NOT SCALE
		canvas.drawText("Life", displayFont, cameraCoords.x - (canvas.getWidth() / 2) + 20, cameraCoords.y + (canvas.getHeight() / 2) -  34);
		canvas.end();

		// Draw energy bar
		//TODO: implement energy bar usage


		// Draw energy bar label
		displayFont.setColor(Color.WHITE);
		displayFont.getData().setScale(0.4f);
		canvas.begin(); // DO NOT SCALE
		canvas.drawText("Current Bubbles: " + bubblesleft, displayFont, cameraCoords.x + (canvas.getWidth() / 2) - 400, cameraCoords.y + (canvas.getHeight() / 2) - 30);
		canvas.end();

//		canvas.end();
		//debug = false;
		if (debug) {
			canvas.beginDebug();
			for(Obstacle obj : objects) {
				obj.drawDebug(canvas);
			}
			canvas.endDebug();
		}

		// Final message
		if (complete && !failed) {
			displayFont.setColor(Color.YELLOW);
			canvas.begin(); // DO NOT SCALE
			canvas.drawText("VICTORY", displayFont, cameraCoords.x-90, cameraCoords.y);
			canvas.end();
		} else if (failed) {
			displayFont.setColor(Color.RED);
			canvas.begin(); // DO NOT SCALE
			canvas.drawText("FAILURE!", displayFont, cameraCoords.x-90, cameraCoords.y);
			canvas.draw(losing,cameraCoords.x - canvas.getWidth()/2, cameraCoords.y - canvas.getHeight()/2);
			canvas.end();
		}
	}

	/**
	 * Method to ensure that a sound asset is only played once.
	 *
	 * Every time you play a sound asset, it makes a new instance of that sound.
	 * If you play the sounds to close together, you will have overlapping copies.
	 * To prevent that, you must stop the sound before you play it again.  That
	 * is the purpose of this method.  It stops the current instance playing (if
	 * any) and then returns the id of the new instance for tracking.
	 *
	 * @param sound		The sound asset to play
	 * @param soundId	The previously playing sound instance
	 *
	 * @return the new sound instance for this asset.
	 */
	public long playSound(Sound sound, long soundId) {
		return playSound( sound, soundId, 1.0f );
	}


	/**
	 * Method to ensure that a sound asset is only played once.
	 *
	 * Every time you play a sound asset, it makes a new instance of that sound.
	 * If you play the sounds to close together, you will have overlapping copies.
	 * To prevent that, you must stop the sound before you play it again.  That
	 * is the purpose of this method.  It stops the current instance playing (if
	 * any) and then returns the id of the new instance for tracking.
	 *
	 * @param sound		The sound asset to play
	 * @param soundId	The previously playing sound instance
	 * @param volume	The sound volume
	 *
	 * @return the new sound instance for this asset.
	 */
	public long playSound(Sound sound, long soundId, float volume) {
		if (soundId != -1) {
			sound.stop( soundId );
		}
		return sound.play(volume);
	}


	/**
	 * Called when the Screen is resized.
	 *
	 * This can happen at any point during a non-paused state but will never happen
	 * before a call to show().
	 *
	 * @param width  The new width in pixels
	 * @param height The new height in pixels
	 */
	public void resize(int width, int height) {
		// IGNORE FOR NOW
	}
	/**
	 * Called when the Screen should render itself.
	 *
	 * We defer to the other methods update() and draw().  However, it is VERY important
	 * that we only quit AFTER a draw.
	 *
	 * @param delta Number of seconds since last animation frame
	 */
	public void render(float delta) {
		if (active) {
			if (preUpdate(delta)) {
				update(delta); // This is the one that must be defined.
				postUpdate(delta);
			}
			draw(delta);
		}
	}

	/**
	 * Called when this screen becomes the current screen for a Game.
	 */
	public void show() {
		// Useless if called in outside animation loop
		active = true;
	}
	/**
	 * Called when this screen is no longer the current screen for a Game.
	 */
	public void hide() {
		// Useless if called in outside animation loop
		active = false;
	}

	public void moveZones(){
		for(int i = 0; i < zones.size(); i++){
			zones.get(i).move();
		}
	}

	/**
	 * Sets the ScreenListener for this mode
	 *
	 * The ScreenListener will respond to requests to quit.
	 */
	public void setScreenListener(ScreenListener listener) {
		this.listener = listener;
	}

}