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
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
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
import edu.cornell.gdiac.util.FilmStrip;
import org.w3c.dom.Text;

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
	protected Texture dudeText;
	protected FilmStrip dude;
	protected Texture swingText;
	protected FilmStrip swingStrip;
	protected Texture idleText;
	private Vector2 crosshairLoc;

	public int[] keyboard_bindings = InputController.getInstance().buttons;

	Cursor emptyCursor;
	Pixmap emptyCursorPixmap;
	Texture emptyCursorTexture;
	TextureData emptyCursorData;

	Cursor defaultCursor;

	TextureRegion crosshair;

	protected FilmStrip idleStrip;
	protected Texture jumpText;
	protected FilmStrip jumpStrip;
	protected Texture fallText;
	protected FilmStrip fallStrip;
	protected Texture upText;
	protected FilmStrip upStrip;
	protected Texture downText;
	protected FilmStrip downStrip;
	protected Texture topText;
	protected FilmStrip topStrip;
	protected Texture sunText;
	protected FilmStrip sunStrip;
	protected Texture fallingText;
	protected FilmStrip fallingStrip;
	protected Texture spikeText;
	protected FilmStrip spikeStrip;
	protected Texture bubblecooldownText;
	protected TextureRegion emptyBubbleCooldown;
	protected TextureRegion fullBubbleCooldown;
	protected FilmStrip bubblecooldownStrip;
	protected ArrayList<TextureRegion> buttonTextures = new ArrayList<>();

	private Vector2 originalRopePlayerPos;

	/** Texture asset for the bullet */
	private TextureRegion bulletTexture;
	/** Texture asset for the bridge plank */
	private TextureRegion bridgeTexture;

	private TextureRegion barrierTexture;

	private TextureRegion poisonTexture;
	private Texture lucenTexture;
	private FilmStrip lucenStrip;

	private int death_count = 0;

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
	private int nextLevelID;

	private Sound popSound;
	private boolean doored = false;
	private long popID = -1;
	private long plopId = -1;
	/** The shoot rope sound.  We only want to play once. */
	private Sound shootRopeSound;
	private long shootRopeSoundId = -1;
	/** The release rope sound.  We only want to play once. */
	private Sound releaseRopeSound;
	private long releaseRopeSoundId = -1;

	private boolean assetsLoaded = false;
	private Sound windSound;
	private long windSoundID = -1;
	/** The level 1 background music sound.  We want it to loop. */
	private Sound level1MusicSunset;
	private long level1MusicSunsetID;

	private boolean playingTheme = false;

	private Sound level1MusicCave;
	private long level1MusicCaveID;
	/** The default sound volume */
	private float volume = 1.0f;
	private float soundvolume = 1.0f;
	private RopeBridge rope;


	public void setVolume(float f){
		volume = f;
	}
	public void setSoundvolume(float f){
		soundvolume = f;
	}
	// Physics objects for the game
	/** Physics constants for initialization */
	private JsonValue constants;

	private int BUBBLE_LIMIT = 2;
	private int constructRopeNextCycle;

	private int bubbles_left = 0;

	private int bubble_regen_timer_max = 40;

	private  int bubble_regen_timer = bubble_regen_timer_max;


	/** Mark set to handle more sophisticated collision callbacks */
	protected ObjectSet<Fixture> sensorFixtures;


	//WORLD CONTROLLERRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRR

	protected TextureRegion earthTile;
	protected TextureRegion iceTile;
	FilmStrip goalStrip;
	/** The texture for the exit condition */
	protected Texture goalText;
	protected FilmStrip bubble;
	protected FilmStrip bubble2;
	protected FilmStrip enemyStrip;
	protected Texture enemyText;

	protected Texture tokenText;
	protected FilmStrip tokenStrip;
	protected Texture bubbleText;
	protected Texture bubbleText2;
	/** The font for giving messages to the player */
	protected TextureRegion skybackground;
	protected Texture icebackground;

	protected Texture paperfilter;
	protected Texture paperfilter1;
	protected Texture paperfilter2;
	protected Texture paperfilter3;
	protected Texture paperfilter4;

	private ArrayList<Texture> filterlist;
	protected TextureRegion losing;
	protected TextureRegion spikeTexture;
	protected TextureRegion spikeTexture2;
	protected BitmapFont displayFont;

	protected TextureRegion deathLeft;
	protected TextureRegion deathRight;


	protected TextureRegion heart;
	protected TextureRegion brokenheart;
	protected TextureRegion dormantlucen;
	protected TextureRegion sundropTexture;

	protected Texture jumpBP;
	protected Texture rightBP;
	protected Texture leftBP;
	protected Texture restartBP;
	protected Texture toggleBP;
	protected Texture pauseBP;
	protected Texture placeBP;
	protected Texture grappleBP;
	protected Texture detachBP;
	protected Texture[] letters = new Texture[40];
	protected Texture leftMouse;
	protected Texture rightMouse;

	protected Texture pauseScreen;
	protected Texture playButton;
	protected Texture quitButton;
	protected Texture settingsButton;

	protected Texture[] borderTextures = new Texture[3];
	protected FilmStrip[] borderStrips = new FilmStrip[3];

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
	protected static final float DEFAULT_WIDTH  = 64.0f;
	/** Height of the game world in Box2d units */
	protected static final float DEFAULT_HEIGHT = 32.0f;
	/** The default value of gravity (going down) */
	protected static final float DEFAULT_GRAVITY = -4.9f;

	private final int MAX_LEVELS = 17;

	private int currLevel;

	private int targetLevel;

	private Vector2 avatarSpawnLocation;
	private Door.SpawnDirection avatarSpawnDirection;
	private boolean needToInitializeSpawn;

	private boolean switchLevel;

	private PlayerController playerController = new PlayerController();

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

	public ArrayList<TextureRegion> textures = new ArrayList<>();

	private List<Bubble> bubbles = new ArrayList<Bubble>();

	private List<ButtonPrompt> prompts = new ArrayList<ButtonPrompt>();


	private List<Enemy> enemies = new ArrayList<Enemy>();

	private ArrayList<TextureRegion> spikeTextureList = new ArrayList<TextureRegion>();

	private List<ProjEnemy> projenemies = new ArrayList<>();
	/** Reference to the character avatar */
	private DudeModel avatar;

	private List<BoxObstacle> platforms = new ArrayList<>();

	private ArrayList<Door> doors;

	private List<LucenglazeSensor> lucens = new ArrayList<>();

	private List<PoisonGas> poisons = new ArrayList();

	private List<Border> borders = new ArrayList<>();

	private Token level6Token;

	private Token level12Token;


	private boolean level6TokenCollected = false;
	private boolean level12TokenCollected = false;




	/**
	 * Creates and initialize a new instance of the platformer game
	 *
	 * The game has default gravity and other settings
	 */
	public PlatformController() {
		constructRopeNextCycle = 0;
		Rectangle worldBounds = new Rectangle(0,0,DEFAULT_WIDTH,DEFAULT_HEIGHT);
		Vector2 worldGravityVector = new Vector2(0, DEFAULT_GRAVITY);
		world = new World(worldGravityVector,false);
		scale = new Vector2(1920/CAMERA_WIDTH,1080/CAMERA_HEIGHT);
		this.bounds = new Rectangle(worldBounds);
		complete = false;
		failed = false;
		debug  = false;
		active = false;
		switchLevel = false;
		currLevel = 1;
		targetLevel = 1;
		playingTheme = false;
		avatarSpawnLocation = new Vector2();
		avatarSpawnDirection = Door.SpawnDirection.RIGHT;

		needToInitializeSpawn = true;
		countdown = -1;
		setDebug(false);
		setComplete(false);
		setFailure(false);
		world.setContactListener(this);
		sensorFixtures = new ObjectSet<Fixture>();
	}

	public void setDefaultCursor(Cursor defaultCursor) {
		this.defaultCursor = defaultCursor;
	}

	/**
	 * Gather the assets for this controller.
	 *
	 * This method extracts the asset variables from the given asset directory. It
	 * should only be called after the asset directory is completed.
	 *
	 * @param directory    Reference to global asset manager.
	 */
	public void gatherAssets(AssetDirectory directory) {

		avatarTexture  = new TextureRegion(directory.getEntry("platform:dude",Texture.class));
		dudeText = directory.getEntry("platform:dude3", Texture.class);
		dude = new FilmStrip(dudeText, 1, 11, 11);
		swingText = directory.getEntry("platform:dude4", Texture.class);
		swingStrip = new FilmStrip(swingText, 1, 3, 3);
		idleText = directory.getEntry("platform:dude5", Texture.class);
		idleStrip = new FilmStrip(idleText, 1, 3, 3);
		jumpText = directory.getEntry("platform:dude6", Texture.class);
		jumpStrip = new FilmStrip(jumpText, 1, 3, 3);
		fallText = directory.getEntry("platform:dude7", Texture.class);
		fallStrip = new FilmStrip(fallText, 1, 3, 3);
		topText = directory.getEntry("platform:dude8", Texture.class);
		topStrip = new FilmStrip(topText, 1 ,1 ,1);
		upText = directory.getEntry("platform:dudeUp", Texture.class);
		upStrip = new FilmStrip(upText, 1 ,1 ,1);
		downText = directory.getEntry("platform:dudeDown", Texture.class);
		downStrip = new FilmStrip(downText, 1 ,1 ,1);
		sunText = directory.getEntry("platform:sundrop2", Texture.class);
		sunStrip = new FilmStrip(sunText, 1, 9, 9);
		fallingText = directory.getEntry("platform:dudeFalling", Texture.class);
		fallingStrip = new FilmStrip(fallingText, 1, 3, 3);
		spikeText = directory.getEntry("shared:plantspike", Texture.class);
		spikeStrip = new FilmStrip(spikeText, 1, 4, 4);
		bubblecooldownText = directory.getEntry("platform:bubblecooldown", Texture.class);
		bubblecooldownStrip = new FilmStrip(bubblecooldownText, 1, 8, 8);
		emptyBubbleCooldown = new TextureRegion(directory.getEntry("platform:emptyCooldownBubble", Texture.class));
		fullBubbleCooldown = new TextureRegion(directory.getEntry("platform:fullCooldownBubble", Texture.class));
		tokenText = directory.getEntry("platform:token",Texture.class);
		tokenStrip = new FilmStrip(tokenText, 1, 5, 5);
		bulletTexture = new TextureRegion(directory.getEntry("platform:bullet",Texture.class));
		bridgeTexture = new TextureRegion(directory.getEntry("platform:rope",Texture.class));
		barrierTexture = new TextureRegion(directory.getEntry("platform:barrier",Texture.class));
		poisonTexture = new TextureRegion(directory.getEntry("platform:gas", Texture.class));
		lucenTexture = directory.getEntry("platform:activatedlucen", Texture.class);
		lucenStrip = new FilmStrip(lucenTexture, 1, 18, 18);
		dormantlucen = new TextureRegion(directory.getEntry("platform:dormantlucen",Texture.class));
		emptyCursorTexture = directory.getEntry("platform:platformcursor",Texture.class);
		crosshair = new TextureRegion(directory.getEntry("platform:crosshair",Texture.class));
		emptyCursorData = emptyCursorTexture.getTextureData();
		emptyCursorData.prepare();
		emptyCursorPixmap = emptyCursorData.consumePixmap();
		emptyCursor = Gdx.graphics.newCursor(emptyCursorPixmap, emptyCursorTexture.getWidth()/2, emptyCursorTexture.getHeight()/2);


		deathLeft = new TextureRegion(directory.getEntry("platform:leftdeath", Texture.class));
		deathRight = new TextureRegion(directory.getEntry("platform:rightdeath", Texture.class));


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

		earthTile = new TextureRegion(directory.getEntry( "shared:earth", Texture.class ));
		iceTile = new TextureRegion(directory.getEntry("shared:ice", Texture.class));
		spikeTexture = new TextureRegion(directory.getEntry( "platform:spike", Texture.class ));
		spikeTexture2 = new TextureRegion(directory.getEntry("platform:spike2", Texture.class));
		goalText  = directory.getEntry( "shared:goal", Texture.class );
		goalStrip = new FilmStrip(goalText, 1, 8, 8);

		bubbleText = directory.getEntry( "shared:bubble2", Texture.class );
		bubbleText2 = directory.getEntry( "shared:bubblerange", Texture.class );
		displayFont = directory.getEntry( "shared:retro" ,BitmapFont.class);
		skybackground = new TextureRegion(directory.getEntry("background:sky", Texture.class));
		icebackground = directory.getEntry("background:ice", Texture.class);
		paperfilter = directory.getEntry("platform:paperfilter", Texture.class);
		paperfilter1 = directory.getEntry("platform:paperfilter1", Texture.class);
		paperfilter2 = directory.getEntry("platform:paperfilter2", Texture.class);
		paperfilter3 = directory.getEntry("platform:paperfilter3", Texture.class);
		paperfilter4 = directory.getEntry("platform:paperfilter4", Texture.class);
		filterlist = new ArrayList<Texture>();
		filterlist.add(paperfilter);
		filterlist.add(paperfilter1);
		filterlist.add(paperfilter2);
		filterlist.add(paperfilter3);
		filterlist.add(paperfilter4);
		losing = new TextureRegion(directory.getEntry("losing", Texture.class));
		bubble = new FilmStrip(bubbleText, 1, 8, 8);
		bubble2 = new FilmStrip(bubbleText2, 1, 8, 8);
		enemyText = directory.getEntry( "platform:dude2", Texture.class );
		enemyStrip = new FilmStrip(enemyText, 1, 9, 9);

		pauseScreen = directory.getEntry("platform:pause", Texture.class);
		quitButton = directory.getEntry("platform:quitbutton", Texture.class);
		playButton = directory.getEntry("platform:playbutton", Texture.class);
		settingsButton = directory.getEntry("platform:settingsbutton", Texture.class);

		for(int i = 1; i < 93; i++){ //load in ice tiles
			textures.add(new TextureRegion(directory.getEntry("shared:ice" + i, Texture.class)));
		}
		for(int i = 1; i < 85; i++){
			textures.add(new TextureRegion(directory.getEntry("shared:sky" + i, Texture.class)));
		}
		for(int i = 1; i < 5; i++){
			textures.add(new TextureRegion(directory.getEntry("shared:con" + i, Texture.class)));
		}
		for(int i = 1; i < 42; i++){
			textures.add(new TextureRegion(directory.getEntry("shared:new" + i, Texture.class)));
		}


		for(int i = 0; i < letters.length; i++){
			letters[i] = directory.getEntry("input" + i, Texture.class);
		}

		jumpBP = directory.getEntry("jumpBP", Texture.class);
		rightBP = directory.getEntry("rightBP", Texture.class);
		leftBP = directory.getEntry("leftBP", Texture.class);
		restartBP = directory.getEntry("restartBP", Texture.class);
		toggleBP = directory.getEntry("toggleBP", Texture.class);
		pauseBP = directory.getEntry("pauseBP", Texture.class);
		placeBP = directory.getEntry("placeBP", Texture.class);
		grappleBP = directory.getEntry("grappleBP", Texture.class);
		detachBP = directory.getEntry("detachBP", Texture.class);
		leftMouse = directory.getEntry("leftmouse", Texture.class);
		rightMouse = directory.getEntry("rightmouse", Texture.class);



		spikeTextureList.add(spikeStrip);
		spikeTextureList.add(new TextureRegion(directory.getEntry("shared:skyspike", Texture.class)));
		spikeTextureList.add(new TextureRegion(directory.getEntry("shared:icespike", Texture.class)));


		for(int i = 0; i < 3; i++){
			borderTextures[i] = directory.getEntry("platform:border" + i, Texture.class);
			borderStrips[i] = new FilmStrip(borderTextures[i], 1, 12,12);
		}




		heart = new TextureRegion(directory.getEntry("platform:heart", Texture.class));
		brokenheart = new TextureRegion(directory.getEntry("platform:brokenheart",Texture.class));
		sundropTexture = new TextureRegion(directory.getEntry("platform:sundrop",Texture.class));

		textures.add(new TextureRegion(directory.getEntry("shared:error", Texture.class)));


		assetsLoaded = true;
	}

	public int getTexture(int x){

		if(x<7 ){
			System.out.println("arrow Test");
			System.out.println("Button Texture Test");
			System.out.println(x);
			return x+1; //up,down,left,right return 0-3
		}
		if(x <= 7 && x <= 16){ // numbers 0-9
			System.out.println("Button Texture Test");
			System.out.println(x);
			return x-3+2;
		}
		if(x >= 19 && x <= 22 ){
			System.out.println("arrow Test");
			System.out.println("Button Texture Test");
			System.out.println(x);
			return x+30-8+2-1-1-1+3+1+1-2; //up,down,left,right return 0-3
		}
		if(x > 22 ){
			System.out.println("arrow Test");
			System.out.println("Button Texture Test");
			System.out.println(x);
			return x-3; //up,down,left,right return 0-3

		}

		System.out.println("Button Texture Test");
		System.out.println(x);
		return x-9; //alphabet return 4-29


	}



	/**
	 * Resets the status of the game so that we can play again.
	 *
	 * This method disposes of the world and creates a new one.
	 */
	public void reset(int targetLevelID) {
		Gdx.graphics.setCursor(emptyCursor);
		death_count = 0;
		doored = false;
		if(currLevel == targetLevelID){
			//reset bubbles
			bubbles_left = BUBBLE_LIMIT;
			bubble_regen_timer = bubble_regen_timer_max;

			//reset health
			life = 1;
			playerController.health= playerController.MAX_HEALTH;

		}
		Vector2 gravity = new Vector2(world.getGravity() );
		zones.clear();
		if(rope != null){
			//////System.out.println("DESTRUCT ROPE 1");
			destructRope();
			rope = null;
		}
		for(Obstacle obj : objects) {
			if(obj.getName() !="bridge"){
				obj.deactivatePhysics(world);
			}

		}
		//actually find a way to delete and reinitialize these later
		windSound.stop();

		objects.clear();
		bubbles.clear();
		enemies.clear();
		lucens.clear();
		poisons.clear();
		addQueue.clear();
		borders.clear();
		projenemies.clear();
		platforms.clear();
		spikelist.clear();
		bullets.clear();
		if(doors!=null) doors.clear();




		world.dispose();

		world = new World(gravity,false);
		world.setContactListener(this);
		if (complete == true){
			Gdx.app.exit(	);
		}
		setComplete(false);
		setFailure(false);
		String nextJsonPath = "lvl" + targetLevelID + ".json";
		populateLevel(nextJsonPath);
	}

	private List<Spike> spikelist = new ArrayList<>();

	/**
	 * Lays out the game geography.
	 */
	private void populateLevel(String jsonPath) {
		System.out.println("canvas width " + canvas.getWidth());
		System.out.println("canvas height" + canvas.getHeight());
		setSounds();



		LevelEditorV2 Level2 = new LevelEditorV2(playerController,jsonPath);
		Level2.readTileTextures(textures, spikeTextureList);
		Level2.readJson();
		List<BoxObstacle> BoxList = Level2.getBoxes();
		List<Bubble> bubbleList = Level2.getBubbles();
		List<Zone> gravityZoneList = Level2.getGravityZones();
		List<Spike> spikes = Level2.getSpikes();
		List<Lucenglaze> glazes = Level2.getGlazes();
		List<Integer> glazeRotations = Level2.getGlazeRotations();
		List<List<Float>> projEnemyData = Level2.getProjEnemyData();
		doors = Level2.getDoors();
		enemies = Level2.getEnemies();
		setParallax(skybackground);


		float dwidth  = goalStrip.getRegionWidth()/scale.x;
		float dheight = goalStrip.getRegionHeight()/scale.y;
//		//////System.out.println("currLevel: " + currLevel);
//		//////System.out.println("targetLevel: " + targetLevel);
		// Add level goal
		for(int i = 0; i < doors.size(); i++){
			Door door = doors.get(i);
			door.initialize(goalStrip);
			door.setBodyType(BodyDef.BodyType.StaticBody);
			door.setSensor(true);
			door.setDrawScale(scale);
			door.setTexture(goalStrip);
			door.setName("door_" + targetLevel + "_to_" + door.getTargetLevelID());
			door.isGoal = true;
			addObject(door);
			if(door.getTargetLevelID() == currLevel){
//				//////System.out.println("TARGET DOOR FOUND!");
				avatarSpawnLocation = door.getPlayerSpawnLocation();
				avatarSpawnDirection = door.getSpawnDirection();
				needToInitializeSpawn = false;
			}
		}
		for (int i = 0; i < glazes.size(); i++) {

			createLucenGlaze(glazes.get(i).getX(), glazes.get(i).getY(), glazeRotations.get(i));
		}






		currLevel = targetLevel;

		avatar = needToInitializeSpawn ? Level2.getPlayer(Door.SpawnDirection.RIGHT) : Level2.getPlayerAtLocation(avatarSpawnLocation, avatarSpawnDirection);
//		//////System.out.println("PRESPAWN LOC: "+ avatar.getPosition());
		if(needToInitializeSpawn) {
			avatarSpawnLocation = avatar.getPosition();
			avatarSpawnDirection = Door.SpawnDirection.RIGHT;
			needToInitializeSpawn = false;
		}
//		//////System.out.println("AFTERCHECK LOC: "+ avatar.getPosition());

		avatar.setGrappling(false);
		avatar.setDrawScale(scale);
		avatar.setTexture(avatarTexture);
		avatar.setLeftDeathTexture(deathLeft);
		avatar.setRightDeathTexture(deathRight);
		avatar.restoreHealth();
		avatar.setName("avatar");
		avatar.initialize(dude, swingStrip, idleStrip, jumpStrip, fallStrip,
				topStrip, upStrip, downStrip, fallingStrip);
		addObject(avatar);


		for (int i = 0; i < gravityZoneList.size(); i++) {
			Zone gravZone = gravityZoneList.get(i);
			gravZone.scale = scale;
			addZone(gravZone);
		}

		for (int i = 0; i < BoxList.size(); i++) {
			BoxObstacle box = BoxList.get(i);
			box.setBodyType(BodyDef.BodyType.StaticBody);
			box.setDensity(0);
			box.setFriction(0);
			box.setRestitution(0);
			box.setDrawScale(scale);
			box.setName("box");
			addObject(box);
			platforms.add(box);
		}


		for (int i = 0; i < spikes.size(); i++) {
			Spike spike = spikes.get(i);
			spike.setBodyType(BodyDef.BodyType.StaticBody);
			spike.setDrawScale(scale);
			spike.setName("spike");
			spike.setTexture2(spikeTexture2);
			addObject(spike);
			spike.initialize(spikeStrip);
			spikelist.add(spike);
		}

		for (int i = 0; i < bubbleList.size(); i++) {
			Bubble wo = bubbleList.get(i);




			wo.setName("Bubble");
			wo.setBodyType(BodyDef.BodyType.DynamicBody);
			wo.setStatic(true);
			wo.setDrawScale(scale);
			wo.setDensity(1000f);
			wo.setTexture(bubble);
			wo.initialize(bubble,bubble2);
			bubbles.add(wo);
			addObject(wo);
		}

		for (int i = 0; i < enemies.size(); i++) {
			Enemy enemy = enemies.get(i);
			enemy.setTexture(enemyStrip);
			enemy.setDrawScale(scale);
			enemy.initialize(enemyStrip);
			addObject(enemy);
		}


		for (int i = 0; i < glazes.size(); i++) {

			createLucenGlaze(glazes.get(i).getX(), glazes.get(i).getY(), glazeRotations.get(i));
		}

		for (int i = 0; i < projEnemyData.size(); i++) {
			createProjEnemy(projEnemyData.get(i).get(0),projEnemyData.get(i).get(1), Math.round(projEnemyData.get(i).get(2)));
		}



//
//
//		ButtonPrompt bp = new ButtonPrompt(2, 8, jumpBP, 0, 1);
//		//ButtonPrompt bp2 = new ButtonPrompt(5, 5, jumpBP, 8);
//		bp.setScale(scale);
//		bp.setMouse(leftMouse, rightMouse);
//		bp.setLetters(letters);
//		prompts.add(bp);
//
//		ButtonPrompt bp2 = new ButtonPrompt(5, 8, jumpBP, 2, 3); //jump down
//		bp2.setScale(scale);
//		bp2.setMouse(leftMouse, rightMouse);
//		bp2.setLetters(letters);
//		prompts.add(bp2);
//
//		ButtonPrompt bp3 = new ButtonPrompt(8, 8, leftBP, 4, 5);
//		bp3.setScale(scale);
//		bp3.setMouse(leftMouse, rightMouse);
//		bp3.setLetters(letters);
//		prompts.add(bp3);
////
//		ButtonPrompt bp4 = new ButtonPrompt(11, 8, rightBP, 6, 7);
//		bp4.setScale(scale);
//		bp4.setMouse(leftMouse, rightMouse);
//		bp4.setLetters(letters);
//		prompts.add(bp4);

		ButtonPrompt bp5 = new ButtonPrompt(14, 8, grappleBP, 8);
		bp5.setScale(scale);
		bp5.setMouse(leftMouse, rightMouse);
		bp5.setLetters(letters);
		prompts.add(bp5);

		ButtonPrompt bp6 = new ButtonPrompt(17, 8, detachBP, 8);
		bp6.setScale(scale);
		bp6.setMouse(leftMouse, rightMouse);
		bp6.setLetters(letters);
		prompts.add(bp6);

		ButtonPrompt bp7 = new ButtonPrompt(20, 8, placeBP, 9);
		bp7.setScale(scale);
		bp7.setMouse(leftMouse, rightMouse);
		bp7.setLetters(letters);
		prompts.add(bp7);





		JsonValue defaults = constants.get("defaults");

		world.setGravity( new Vector2(0,defaults.getFloat("gravity",0)) );

		if(currLevel == 6 && !level6TokenCollected){

			level6Token = new Token(new Vector2(55,12), 1);

			level6Token.setName("token6");
			level6Token.setBodyType(BodyDef.BodyType.StaticBody);
			level6Token.setSensor(true);
			level6Token.setDrawScale(scale);
			level6Token.setTexture(tokenStrip);
			level6Token.initialize(tokenStrip);

//			//////System.out.println("ADDING TOKEN" + level6Token.getPosition());

			addObject(level6Token);
		}

		if(currLevel == 12 && !level12TokenCollected){

			level12Token = new Token(new Vector2(36,22), 1);

			level12Token.setName("token12");
			level12Token.setBodyType(BodyDef.BodyType.StaticBody);
			level12Token.setSensor(true);
			level12Token.setDrawScale(scale);
			level12Token.setTexture(tokenStrip);
			level12Token.initialize(tokenStrip);
//			//////System.out.println("ADDING TOKEN" + level12Token.getPosition());

			addObject(level12Token);
		}




		// Create rope bridge
		setCamera(avatar.getX(), avatar.getY() + 0.5f);


//		volume = constants.getFloat("volume", 1.0f);
	}

	public Vector2 getOrgRopePlayerPos() {
		return originalRopePlayerPos;
	}

	public void setOrgRopePlayerPos(Vector2 newVector) {
		originalRopePlayerPos = newVector;
	}

	public void createProjEnemy(float x, float y, int rotation){
		float xx = 0;
		float yy = 0;
		int w = 10;
		int h = 10;

		float offset = (h / 2f) - 0.5f;
		switch (rotation){
			case 0:
				yy = offset;
				break;
			case 1:
				xx = offset;
				break;
			case 2:
				yy = -offset;
				break;
			case 3:
				xx = -offset;
				break;
		}
		h = (rotation % 2 == 0) ? h : w;
		w = (rotation % 2 == 0) ? w : h;
		ProjEnemySensor pes = new ProjEnemySensor(x + xx, y + yy, w, h, rotation);
		pes.setPE(createPE(x, y, rotation));
		pes.setDrawScale(scale);
		addObject(pes);

	}

	public ProjEnemy createPE(float x, float y, int rotation){
		ProjEnemy pe = new ProjEnemy(x, y, rotation);
		pe.setDrawScale(scale);

		pe.setTexture(sunStrip);
        pe.initialize(sunStrip);
		addObject(pe);
		projenemies.add(pe);
		//////////System.out.println("pe pos" + pe.getPosition());
		return pe;
	}

	public void createPoisonGas(float x, float y, boolean fade){
		PoisonGas gas = new PoisonGas(x,y);
		gas.setFade(fade);
		gas.setDrawScale(scale);
		gas.setTexture(poisonTexture);
		addObject(gas);
		poisons.add(gas);
	}

	public void createLucenGlaze(float x, float y, int rotation){ //takes in coords of lucenglaze itself(will adjust for sensor automatically)
		float xx = 0;
		float yy = 0;
		switch (rotation){
			case 0:
				yy = 1.5f;
				break;
			case 1:
				xx = 1.5f;
				break;
			case 2:
				yy = -1.5f;
				break;
			case 3:
				xx = -1.5f;
				break;
		}
		int w = (rotation % 2 == 0) ? 3 : 4;
		int h = (rotation % 2 == 0) ? 4 : 3;
		LucenglazeSensor lgs = new LucenglazeSensor(x + xx, y + yy, (int) x, (int) y, w, h, rotation);
		lgs.setLucen(createLucenObject(x, y, rotation));
		lgs.setDrawScale(scale);
		addObject(lgs);
		lucens.add(lgs);
	}

	public Lucenglaze createLucenObject(float x, float y, int rotation){ //called by prev
		Lucenglaze lg = new Lucenglaze(x, y);
		lg.setRotation(rotation);
		lg.setDrawScale(scale);
		lg.setTexture(lucenStrip);
		lg.initialize(lucenStrip);
		lg.setTexture2(dormantlucen);
		addObject(lg);
		return lg;
	}

	public Bubble spawnBubble(Vector2 v){
		if(bubbles_left == 0) return null;
		Bubble wo2 = new Bubble(v,1, Bubble.BubbleType.FLOATING);
		if(InputController.getInstance().isFiniteBubbles()){
			bubbles_left--;
		}

		wo2.setName("bubble");
		wo2.setStatic(false);
		wo2.setBodyType(BodyDef.BodyType.DynamicBody);
		wo2.setDrawScale(scale);
		wo2.setDensity(10000f);
		wo2.setTexture(bubble);
		wo2.setTexture(bubble);
		wo2.initialize(bubble, bubble2);
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
	 * @param dt    Number of seconds since last animation frame
	 *
	 * @return whether to process the update loop
	 */
	public boolean preUpdate(float dt) {
		if (!preUpdateHelper(dt)) {
			return false;
		}
		if(!avatar.isAlive()){
			death_count++;
			avatar.setShowDeath(true);
			if(death_count == 50){
				setFailure(true);
			}
			return false;
		}

		if (!isFailure() && !inBounds(avatar)) {
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
	 *
	 */

	private int wait = 0;

	public void update(float dt) {
		updateBubbles();
		updateSpike();
		updateEnemies();
		moveZones();

		updateCamera(avatar.getX()*scale.x, avatar.getY()*scale.y);
		updateMouse();
		updateObjectGravs();
		updateLucens();
		updatePoisons();
		updateBorders();
		updateDoors();
		updateAvatar();
		updateSounds();
		if (rope != null && rope.getJointLength()>=2){

			rope.updateJoint();
		}
        if (level6Token != null)level6Token.update();
		if (level12Token != null) level12Token.update();
	}

	private void updateMouse(){
		crosshairLoc = InputController.getInstance().getCrossHair();
		float xoffset = (cameraCoords.x / scale.x) - (CAMERA_WIDTH / 2f); //find bottom left corner of camera
		float yoffset = (cameraCoords.y / scale.y) - (CAMERA_HEIGHT / 2f);
		crosshairLoc.x += xoffset;
		crosshairLoc.y += yoffset;

//		//////System.out.println("CAMERA: " +cameraCoords.scl(1/scale.x,1/scale.y));
//		//////System.out.println("Crosshair before: " +crosshairLoc);
		Vector2 playerLoc = avatar.getPosition();
//		//////System.out.println("Player Pos: " + playerLoc);
		float dist = Vector2.dst(crosshairLoc.x,crosshairLoc.y, playerLoc.x, playerLoc.y);
		if(dist > 4){
			Vector2 tempCrosshair = crosshairLoc.cpy();
			crosshairLoc.lerp(playerLoc, (dist-4f)/dist);
			Vector2 normed_loc = crosshairLoc.cpy();
			normed_loc.add(-1f*playerLoc.x, -1f*playerLoc.y);
			normed_loc.nor();
			if(dist > 5){
				Vector2 av_screen = avatar.getPosition().cpy().add(-1f*xoffset, -1f*yoffset);
				normed_loc.x *= 4.5f;
				normed_loc.y *= 4.5f;
				av_screen = av_screen.add(normed_loc);
				Gdx.input.setCursorPosition((int)(scale.x*av_screen.x),canvas.getHeight()-(int)(scale.y*av_screen.y));
			}
		}
		crosshairLoc = new Vector2(crosshairLoc);
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
				poisons.remove(poisons.get(i));
			}
		}
	}

	private void updateBubbles(){
		for(int i = 0; i < bubbles.size(); i++){
			Bubble b = bubbles.get(i);
			/*if(b.canRopeTo) {
				b.setTexture(bubble2);
				b.initialize(bubble2);
			}*/

			b.update();
			b.canRopeTo = false;
			if(b.timedOut()){
				if(b.isGrappled()){
					//////System.out.println("DESTRUCT ROPE 2");

					destructRope();
					avatar.setGrappling(false);
					b.setGrappled(false);
				}
				popBubble(b);
				i--;
			}
		}
	}

	private void updateUI(){

		Vector2 drawPos = new Vector2(cameraCoords.x,cameraCoords.y);

		int f =8- (bubble_regen_timer/(bubble_regen_timer_max/8));
		if(f == 8){ f = 7;}

		bubblecooldownStrip.setFrame(f);
		Texture t = bubblecooldownStrip.getTexture();
		float ox = fullBubbleCooldown.getRegionWidth() / 2f;
		float oy = fullBubbleCooldown.getRegionHeight() / 2f;
		int curr_bubble = 1;
		float sx = scale.x / 128;
		float sy = scale.y / 128;

		Vector2 current_bubble_pos = new Vector2(drawPos.x + canvas.getWidth()/2 - (3 * scale.x), drawPos.y + canvas.getHeight()/2 - scale.y);
		while(curr_bubble <= bubbles_left){

			canvas.draw(fullBubbleCooldown,Color.WHITE,ox, oy, current_bubble_pos.x,current_bubble_pos.y,0, sx, sy);
			curr_bubble ++;
			current_bubble_pos.add(1.25f * scale.x,0);
		}
		if(bubbles_left <BUBBLE_LIMIT){
			canvas.draw(bubblecooldownStrip,Color.WHITE,ox, oy, current_bubble_pos.x,current_bubble_pos.y,0, sx, sy);
			curr_bubble ++;
			current_bubble_pos.add(1.25f * scale.x,0);
		}
		while(curr_bubble <= BUBBLE_LIMIT){
			canvas.draw(emptyBubbleCooldown,Color.WHITE,ox, oy, current_bubble_pos.x,current_bubble_pos.y,0, sx, sy);
			curr_bubble ++;
			current_bubble_pos.add(1.25f * scale.x,0);
		}


		Vector2 curr_heart_pos = new Vector2(drawPos.x - (canvas.getWidth() / 2) + scale.x, drawPos.y + (canvas.getHeight() / 2) - scale.y);
		sx = scale.x / 64;
		sy = scale.x / 64;
		ox = heart.getRegionWidth() / 2f;
		oy = heart.getRegionHeight() / 2f;
		for(int i = 0; i < avatar.getMaxHealth(); i++){
			if(i < avatar.getHealth()){
				canvas.draw(heart,Color.WHITE, ox, oy, curr_heart_pos.x, curr_heart_pos.y, 0, sx, sy);
			}else{
				canvas.draw(brokenheart,Color.WHITE, ox, oy, curr_heart_pos.x, curr_heart_pos.y, 0, sx, sy);
			}
			curr_heart_pos.add(scale.x,0);

		}
		avatar.getMaxHealth();

	}
	private void updateDoors(){
		for(int i = 0; i < doors.size(); i++){
			Door door = doors.get(i);
			door.update();
		}
	}
	private void updateSpike() {
		for(Spike s : spikelist) {
			s.update();
		}
	}
	private void updateBorders(){
		for(int i = 0; i<borders.size(); i++){
			Border border = borders.get(i);
			border.update();
		}
	}

	private void updateEnemies(){
		for(int i = 0; i < enemies.size(); i++){
			Enemy enemy = enemies.get(i);
			enemy.initialize(enemyStrip);
			enemy.update(avatar);
		}

		for(int i = 0; i < projenemies.size(); i++){
			ProjEnemy pe = projenemies.get(i);

			if(pe.update()){
				if(canShoot(pe)) {
					createBullet(pe);
				}
			}
		}
	}

	//TODO more efficient
	private void updateObjectGravs(){
		//iterate player
		//iterate enemies
		//iterate bubble
		for(int i = 0; i < enemies.size() ; i++){
			//get the body of the object
			Body o = enemies.get(i).getBody();
			//set the gravity to 1
			enemies.get(i).setGrav(1.0f);
			for(int j = 0; j < zones.size(); j++){
				//if the body is in the gravity zone
				if(zones.get(j).inBounds(o.getPosition().x, o.getPosition().y)){
					//set that bodies gravity zone to that zone
					enemies.get(i).setGrav(zones.get(j).getGrav());
				}
			}
		}
		for(int i = 0; i < bubbles.size() ; i++){
			//get the body of the object
			Body o = bubbles.get(i).getBody();
			//set the gravity to 1
			bubbles.get(i).setGrav(1.0f);
			for(int j = 0; j < zones.size(); j++){
				//if the body is in the gravity zone
				if(zones.get(j).inBounds(o.getPosition().x, o.getPosition().y)){
					//set that bodies gravity zone to that zone
					bubbles.get(i).setGrav(zones.get(j).getGrav());
				}
			}
		}
		avatar.setGrav(1);
		for(int j = 0; j < zones.size(); j++){
			//if the body is in the gravity zone
			if(zones.get(j).inBounds(avatar.getPosition().x, avatar.getPosition().y)){
				//set that bodies gravity zone to that zone
				avatar.setGrav(zones.get(j).getGrav());
			}
		}

	}

	public Vector2 pl;
	public Vector2 ploffset;


	public boolean camera = false;
    private float oldY = 0;
	private void updateAvatar(){
		Vector2 placeLocation;
		if(InputController.getInstance().cameraMovement){
			camera = true;
		}else{
			camera = false;
		}
		if(InputController.getInstance().isMouseControlls()){
			placeLocation = crosshairLoc;
		}else{
			if(!avatar.isGrappling()){
				if(Math.abs(avatar.getMovement()) < 0.5){
					placeLocation = avatar.getPosition().add(avatar.getVX() * 0.3f,  2.5f * avatar.grav);
				}else {
					placeLocation = avatar.getPosition().add(avatar.getVX() * 0.3f, 2f * avatar.grav);
				}

			}else{
				placeLocation = avatar.getPosition().add(avatar.getVX() * 0.3f,  1f * avatar.grav);

			}
			crosshairLoc.set(placeLocation);
		}

		Bubble closest = null;
		float min = Float.MAX_VALUE;
		for(int i = 0; i < bubbles.size(); i++){
			Bubble b = bubbles.get(i);
			if(closest == null){
				closest = b;
			}

			float d = b.getPosition().dst(placeLocation);
			b.setSelected(false);
			if(d < min){
				closest = b;
				min = d;
			}
		}
		if(!camera) {
			avatar.setMovement(InputController.getInstance().getHorizontal() * avatar.getForce());
			if(avatar.getGravZone() == 1){
				avatar.setJumping(InputController.getInstance().didPrimary());
			}else{
				avatar.setJumping(InputController.getInstance().didPrimaryUpsideDown() || InputController.getInstance().didPrimary());
			}
		}else{
			avatar.setMovement(0);
			avatar.setJumping(false);
		}

		//do bubble stuff
		boolean spawned = false;
		if(InputController.getInstance().didTertiary()){
			Vector2 bp = canBubble2(placeLocation);
			//////System.out.println(bp);
			if(bp != null) {
				if (wait > 20) {
					if (!InputController.getInstance().isFiniteBubbles() || bubbles_left > 0) {
						closest = spawnBubble(bp);
					}
					wait = 0;
					spawned = true;
				}
			}
		}
		if(closest != null){
			if (avatar.getPosition().dst(closest.getPosition()) < 4.5 && canShoot(closest)) {
				closest.canRopeTo = true;
			}
		}


		if(InputController.getInstance().isFiniteBubbles()){
			if(avatar.isGrounded() && InputController.getInstance().isReloadBubblesOnGround()){
				if(bubbles_left < BUBBLE_LIMIT){
					bubbles_left++;
					/*if(bubble_regen_timer <=0){
						bubbles_left++;
						bubble_regen_timer = bubble_regen_timer_max;
					}
					bubble_regen_timer--;*/
				}
			}else{
				bubble_regen_timer = bubble_regen_timer_max;
			}
		}else{
			bubbles_left = BUBBLE_LIMIT;
		}

		wait++;


		if (closest != null) closest.setSelected(true);

		Vector2 ropeDir = new Vector2(0,0);
		Vector2 pos = avatar.getPosition();
		boolean destructRope = false;
		boolean constructRope = false;
		if(!spawned && closest != null) { //temp prevents people from left and right clicking at same time (which breaks for some reason)
			if (avatar.isGrappling()) {
				ropeDir = closest.getPosition().sub(avatar.getPosition());
				if (InputController.getInstance().didBubble()) {
					destructRope = true;
				}
				if (InputController.getInstance().didBubble() && closest.canRopeTo && !rope.bubble.equals(closest.getBody())) {
					constructRope = true;
				}
			} else {
				if (InputController.getInstance().didBubble() && closest.canRopeTo) {
					constructRope = true;
				}
			}
		}



		if(destructRope){
			avatar.setGrappling(false);
			avatar.setGrappleBoost(true);
			Bubble b = (Bubble) rope.bubble.getUserData();
			b.setGrappled(false);
			//////System.out.println("DESTRUCT ROPE 3");

			destructRope();
			rope = null;
			releaseRopeSoundId = playSound(releaseRopeSound, releaseRopeSoundId, soundvolume );
		}
		if (constructRope || constructRopeNextCycle>0) {
			constructRopeNextCycle++;
		}

		if(constructRopeNextCycle >=5 && closest != null){

			if(canShoot(closest)) { //TODO:: make this good
				avatar.setGrappling(true);
				avatar.setGrappledBubble(closest);
				avatar.setGrappledBubbleDist(avatar.getPosition().dst(closest.getPosition()));
				rope = createGrapple(closest);
				shootRopeSoundId = playSound(shootRopeSound, shootRopeSoundId, soundvolume);
			}
			constructRopeNextCycle = 0;

		}

		avatar.breathe(); //used for poison gas stuff
		avatar.applyForce(ropeDir);
		life = avatar.getLife();//update health bar

		avatar.initialize(dude, swingStrip, idleStrip, jumpStrip, fallStrip,
				topStrip, upStrip, downStrip, fallingStrip);

		if(avatar.isGrounded()) oldY = avatar.getY();

		if(avatar.isGrappling()) avatar.setTexture(swingStrip);
		else if(avatar.isGrounded() && avatar.getMovement() == 0.0) avatar.setTexture(idleStrip);
		else if ((avatar.getY() < oldY && avatar.getGravZone() == 1) ||
				(avatar.getY() > oldY && avatar.getGravZone() == -1)) avatar.setTexture(fallingStrip);
		//Jumping up
		else if ((avatar.getGravZone() == 1 && !avatar.isGrounded() && avatar.getVY() > 0f) ||
				(avatar.getGravZone() == -1 && !avatar.isGrounded() && avatar.getVY() < 0f)) {
			if(Math.abs(avatar.getVX()) < 0.1) avatar.setTexture(upStrip);
			else avatar.setTexture(jumpStrip);
		}
		else if (!avatar.isGrounded() && avatar.getVY() > -0.01f && avatar.getVY() < 0.01f) avatar.setTexture(topStrip);
		//Falling down
		else if ((avatar.getGravZone() == 1 && !avatar.isGrounded() && avatar.getVY() < 0f) ||
				(avatar.getGravZone() == -1 && !avatar.isGrounded() && avatar.getVY() > 0f)) {
			if(Math.abs(avatar.getVX()) < 0.1) avatar.setTexture(fallingStrip);
			else avatar.setTexture(fallStrip);
		}
		else avatar.setTexture(dude);
		avatar.update();

	}

	private void setSounds(){
		if(!playingTheme){
			level1MusicSunsetID = level1MusicSunset.loop(0.0f);
			level1MusicCaveID = level1MusicCave.loop(0.0f);
			playingTheme = true;
		}
		windSoundID = windSound.loop(0.0f);
	}

	public void updateSounds(){
		if(avatar.getGravZone() == 1){
			level1MusicSunset.setVolume(level1MusicSunsetID,volume * 0.2f);
			level1MusicCave.setVolume(level1MusicCaveID,0.0f);
		}
		if(avatar.getGravZone() == -1){
			level1MusicSunset.setVolume(level1MusicSunsetID,0.0f);
			level1MusicCave.setVolume(level1MusicCaveID,volume * 0.2f);
		}
		if (avatar.justJumped()) {
			//jumpSound.setVolume(jumpId,soundvolume * 2f);
			jumpId = playSound( jumpSound, jumpId, soundvolume);
		}
		if (avatar.justGrounded()) {
			//plopSound.setVolume(plopId,soundvolume * 2f);
			plopId = playSound( plopSound, jumpId, soundvolume);
		}
		windSound.setVolume(windSoundID, soundvolume * Math.min((float) Math.abs((avatar.getVX() + (avatar.getVY() * 0.5)) * 0.06f),0.4f));

	}

	private RopeBridge createGrapple(Bubble bubble){

		bubble.setGrappled(true);
		float dwidth  = bridgeTexture.getRegionWidth()/64f;
		float dheight = bridgeTexture.getRegionHeight()/64f;
		dwidth = 0.3125f;
		dheight = 0.125f;
		RopeBridge bridge = new RopeBridge(constants.get("bridge"), dwidth / 2,dheight,bubble.getBody(), avatar);
		bridge.setTexture(bridgeTexture);
		bridge.setDrawScale(scale);
		addQueuedObject(bridge);
		avatar.setGrounded(false);
		return bridge;
	}


	private List<Bullet> bullets = new ArrayList<>();


	public void createBullet(ProjEnemy pe){
		Vector2 dir = avatar.getPosition().sub(pe.getPosition());

		float radius = 0.3f;

		int[][] offsets = {{0,1}, {1,0}, {0,-1}, {-1,0}};
		int[] offset = offsets[pe.getRotation()];
		Bullet bullet = new Bullet(pe.getX() + offset[0], pe.getY() + offset[1], radius);
		bullet.setGravityScale(0f);
		bullet.setDrawScale(scale);

		bullet.setTexture(bulletTexture);
		bullet.setBullet(true);

		float speed = 5f;

		bullet.setLinearVelocity(dir.nor().scl(speed));
		addQueuedObject(bullet);
		bullets.add(bullet);

	}


	/**
	 * Remove a new bullet from the world.
	 *
	 * @param  bullet   the bullet to remove
	 */
	public void removeBullet(Obstacle bullet) {
		bullet.markRemoved(true);
		bullets.remove((Bullet)bullet);
		plopId = playSound( plopSound, plopId );
	}

	public void destructRope() {
		if(rope != null) {
			rope.markRemoved(true);
			avatar.setLinearVelocity(new Vector2(avatar.getLinearVelocity().scl(ROPE_LAUNCH_SPEED.x).x, avatar.getLinearVelocity().scl(ROPE_LAUNCH_SPEED.y).y));
			rope = null;
		}
	}

	public void popBubble(Bubble bubble){
		bubble.setActive(false);
		bubble.stopDraw();
		bubbles.remove(bubble);
		//popSound.setVolume(popID, soundvolume * 10f);
		popID = playSound(popSound,popID,0.5f * soundvolume);

	}

	public Vector2 collidePos = new Vector2();
	public Body collidebody = null;
	public int collidedbodies = 0;

	public void setCollidebodies(int update) {
		collidedbodies = update;
	};

	public Vector2 checkBubbleVertical(Vector2 point, float dst){
		boolean found = false;
		Vector2 bp = new Vector2(0,0);
		point.y = point.y + dst; //sets to two to the left
		for(int i = 0; i < 5; i++){
			dst /= 2f;
			if(canBubble(point)){
				found = true;
				bp.set(point);
				point.y -= dst;
			}else{
				if(!found){
					break;
				}
				point.y += dst;
			}
		}
		if(found){
			return bp;
		}else{
			return null;
		}
	}


	public Vector2 checkBubbleHorizontal(Vector2 point, float dst){
		boolean found = false;
		Vector2 bp = new Vector2(0,0);
		point.x = point.x + dst; //sets to two to the left
		for(int i = 0; i < 5; i++){
			dst /= 2f;
			if(canBubble(point)){
				found = true;
				bp.set(point);
				point.x -= dst;
			}else{
				if(!found){
					break;
				}
				point.x += dst;
			}
		}
		if(found){
			return bp;
		}else{
			return null;
		}
	}



	public Vector2 canBubble2(Vector2 p){

		if(canBubble(p)){
			return p;
		}
		float dst = 2;

		Vector2 bp = null;
		Vector2 point = new Vector2(p);
		point.x -= 1; //if something on the left

		if(!canBubble(point)){ //checks if something on left
			bp = checkBubbleHorizontal(p.cpy(), dst); //finds spot on right
			if(bp != null){
				return bp;
			}
		}

		point.set(p);
		point.x += 1;

		if(!canBubble(point)){ //check if something on right
			bp = checkBubbleHorizontal(p.cpy(), -dst); //finds spot on left
			if(bp != null){
				return bp;
			}
		}

		point.set(p);
		point.y -= 1;

		if(!canBubble(point)) { //checks bottom
			bp = checkBubbleVertical(p.cpy(), dst); //finds spot on top
			if(bp != null){
				return bp;
			}
		}

		point.set(p);
		point.y += 1;

		if(!canBubble(point)) { //checks top
			bp = checkBubbleVertical(p.cpy(), -dst); //finds spot on bottom
			if(bp != null){
				return bp;
			}
		}
		return null;
	}




	public boolean canBubble(Vector2 p){

		RayCastCallback rcc = new RayCastCallback() {
			@Override
			public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
				collidePos = fixture.getBody().getPosition();
				collidebody = fixture.getBody();

				if (!fixture.isSensor() && !collidebody.isBullet()) {
					// && collidebody.getUserData() != avatar
					collidedbodies++;
				}

				return 1;
			}
		};
		collidedbodies = 0;

		Vector2 left = p.cpy(); //bottom left
		left.x -= 1;
		left.y -= 1;

		Vector2 right = p.cpy(); //top right
		right.x += 1;
		right.y += 1;

		world.rayCast(rcc, left, right); //bottom left to top right
		left.y += 2;
		right.y -= 2;
		world.rayCast(rcc, left, right); //top left to bottom right
		return collidedbodies < 1;
	}


	public boolean canShoot(Obstacle b) {
		RayCastCallback rcc = new RayCastCallback() {
			@Override
			public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
				collidePos = fixture.getBody().getPosition();
				collidebody = fixture.getBody();
				if (avatar != collidebody.getUserData() && !fixture.isSensor()) {

					if(!((Obstacle)fixture.getBody().getUserData()).getName().contains("plank")){
						collidedbodies++;
					}

				}

				return 1;
			}
		};
		collidedbodies = 0;

		world.rayCast(rcc, b.getPosition(), avatar.getPosition());
		return collidedbodies < 1;
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




		try {
			Fixture fix1 = contact.getFixtureA();
			Fixture fix2 = contact.getFixtureB();

			Body body1 = fix1.getBody();
			Body body2 = fix2.getBody();

			Object fd1 = fix1.getUserData();
			Object fd2 = fix2.getUserData();

			Obstacle bd1 = (Obstacle)body1.getUserData();
			Obstacle bd2 = (Obstacle)body2.getUserData();

			// Test bullet collision with world

			if (bd1.getName().equals("bullet") && !bd2.getName().contains("projenemy") && !bd2.isSensor() && !bd2.getName().contains("plank")) {
				removeBullet(bd1);
			}

			if (bd2.getName().equals("bullet") && !bd1.getName().contains("projenemy") && !bd1.isSensor() && !bd1.getName().contains("plank")) {

				removeBullet(bd2);
			}
			if((bd1 == avatar && bd2.getName().equals("projenemysensor")) || (bd2 == avatar && bd2.getName().equals("projenemysensor"))){
				if (bd1 == avatar) {
					((ProjEnemySensor)bd2).activate();
				} else {
					((ProjEnemySensor)bd2).activate();
				}
			}

			if((bd1 == avatar && bd2.getName().equals("bubble")) || bd2 == avatar && bd1.getName().equals("bubble")){
				Vector2 temp;
				if(bd1 == avatar){
					temp = avatar.getPosition().sub(bd2.getPosition());
					if(temp.y * bd2.grav >  0.8f){
						avatar.getBody().applyLinearImpulse(new Vector2(0, 15), avatar.getPosition(), true);
						Bubble b = (Bubble) bd2;
						if(!b.statc){
							b.pop_timer = 8;
						}
					}
				}
				else{
					temp = avatar.getPosition().sub(bd1.getPosition());
					if(temp.y * bd1.grav > 0.8f){
						avatar.getBody().applyLinearImpulse(new Vector2(0, 15), avatar.getPosition(), true);
						Bubble b = (Bubble) bd1;
						if(!b.statc){
							b.pop_timer = 8;
						}
					}
				}
			}

			if ((bd1 == avatar && (bd2.getName().equals("spike") || bd2.getName().equals("enemy") || bd2.getName().equals("bullet"))) ||
					(bd2 == avatar && (bd1.getName().equals("spike") || bd2.getName().equals("enemy") || bd2.getName().equals("bullet")))){


				if(!avatar.isInvincible()) {
						avatar.hurt();
						life = avatar.getLife();
						if (bd1 == avatar) { //move it to player controller
							//TODO look prev comment
							if(bd2.getName().equals("spike")){
								if (bd2.getIsinstantKill()) {
									avatar.kill();
								}

								else {
									avatar.hurt();
								}

								life = avatar.getLife();
							}
							Vector2 v2 = body1.getPosition().sub(body2.getPosition()).nor().scl(10);
							body1.applyLinearImpulse(new Vector2(v2.x, v2.y), body1.getPosition(), true);

						} else {
							if(bd1.getName().equals("spike")){
								if (bd1.getIsinstantKill()) {
									avatar.kill();
								}

								else {
									avatar.hurt();
								}
								life = avatar.getLife();
							}
							Vector2 v2 = body2.getPosition().sub(body1.getPosition()).nor().scl(10);
							body2.applyLinearImpulse(new Vector2(v2.x, v2.y), body2.getPosition(), true);

						}

				}
			}

			// See if we have landed on the ground.
			if ((avatar.getSensorName().equals(fd2) && avatar != bd1 && !bd1.getName().equals("bubble") && !bd1.getName().contains("plank") && !bd1.getName().contains("sundropBullet") && !bd1.getName().equals("lucenglazesensor") && !bd1.getName().contains("gas") && !bd1.isSensor()) ||
					(avatar.getSensorName().equals(fd1) && avatar != bd2 && !bd2.getName().equals("bubble") && !bd2.getName().contains("plank") && !bd1.getName().contains("sundropBullet") && !bd2.getName().equals("lucenglazesensor") && !bd2.getName().contains("gas") && !bd2.isSensor())) {

				avatar.setGrounded(true);
				sensorFixtures.add(avatar == bd1 ? fix2 : fix1); // Could have more than one ground
			}

			if ((bd1.getName().equals("bubble") && (bd2.getName().equals("enemy") || bd2.getName().equals("spike") || bd2.getName().equals("bullet"))) ||
					(bd2.getName().equals("bubble") && (bd1.getName().equals("enemy") || bd1.getName().equals("spike") || bd1.getName().equals("bullet")))){
				if(bd1.getName().equals("bubble")){
					((Bubble) bd1).pop_timer = 1;

				}else{
					((Bubble) bd2).pop_timer = 1;
				}
			}

			// Check for win condition
			if (((bd1 == avatar   && bd2.getName().contains("door")) ||
					(bd1.getName().contains("door") && bd2 == avatar))) {

				doored = true;
				Door door = (bd1 == avatar) ? (Door)bd2: (Door)bd1;

				//////////System.out.println("COLLISION WITH " + door.getName());
				nextLevelID = door.getTargetLevelID();
				//////////System.out.println("Next Level: " + nextLevelID);

			}

			if ((bd1 == avatar   && bd2.getName().contains("token6")) ||
					(bd1.getName().contains("token6") && bd2 == avatar)){
				Token token = (bd1 == avatar) ? (Token)bd2: (Token)bd1;
				int old_bubble_limit = BUBBLE_LIMIT;

				BUBBLE_LIMIT = token.getBubbleLimitValue();
				bubbles_left = bubbles_left + (BUBBLE_LIMIT-old_bubble_limit);
				if (bd1 == avatar){
					bd2.markRemoved(true);
				}else{
					bd1.markRemoved(true);
				}
				level6TokenCollected = true;
				level6Token = null;
			}

			if ((bd1 == avatar   && bd2.getName().contains("token12")) ||
					(bd1.getName().contains("token12") && bd2 == avatar)){
				Token token = (bd1 == avatar) ? (Token)bd2: (Token)bd1;
				int old_bubble_limit = BUBBLE_LIMIT;

				BUBBLE_LIMIT = 2;
				bubbles_left = 2;
				if (bd1 == avatar){
					bd2.markRemoved(true);
				}else{
					bd1.markRemoved(true);
				}
				level12TokenCollected = true;
				level12Token = null;
			}

			if ((bd1 == avatar && bd2.getName().equals("gas")) || (bd1.getName().equals("gas") && bd2 == avatar)){
				assert avatar.gas >= 0;
				avatar.gas++;
				if(avatar.gas > 0){
					avatar.setInGas(true);
				}
			}

			if (((bd1 == avatar || bd1.getName().equals("enemy")) && bd2.getName().equals("lucenglazesensor")) ){
				((LucenglazeSensor) bd2).activate();
			}else if((bd1.getName().equals("lucenglazesensor") && (bd2 == avatar || bd2.getName().equals("enemy")))){
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



		try {
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

			Obstacle cd1 = (Obstacle) body1.getUserData(); //idk man
			Obstacle cd2 = (Obstacle) body2.getUserData(); //copied begin contact but bd was already taken so used cd
			if (((cd1 == avatar   && cd2.getName().contains("door")) ||
					(cd1.getName().contains("door") && cd2 == avatar))) {
				doored = false;
			}



			if ((cd1 == avatar && cd2.getName().equals("gas")) || (cd1.getName().equals("gas") && cd2 == avatar)) {
				avatar.gas--;
				assert avatar.gas >= 0;
				if(avatar.gas == 0){
					avatar.setInGas(false);
				}
			}

			if((cd1 == avatar && cd2.getName().equals("projenemysensor")) || (cd2 == avatar && cd2.getName().equals("projenemysensor"))){
				if (bd1 == avatar) {
					((ProjEnemySensor)bd2).deactivate();
				} else {
					((ProjEnemySensor)bd2).deactivate();
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
		Gdx.graphics.setCursor(defaultCursor);
		jumpSound.stop(jumpId);
		plopSound.stop(plopId);
		popSound.stop(popID);
		fireSound.stop(fireId);
		level1MusicCave.pause(level1MusicCaveID);
		level1MusicSunset.pause(level1MusicSunsetID);

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
		lucens.clear();
		platforms.clear();
		spikelist.clear();
		bullets.clear();
		if(rope != null){
			//////System.out.println("DESTRUCT ROPE 4");

			destructRope();
			rope = null;
		}
		objects = null;
		addQueue = null;
		bounds = null;
		scale  = null;
		world  = null;
		canvas = null;
	}
	public void setCurrLevel(int i){
		currLevel = i;
	}
	public void setTargetLevel(int i){
		targetLevel = i;
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
	public boolean pause_state = false;
	/**
	 * Returns whether to process the update loop
	 *
	 * At the start of the update loop, we check if it is time
	 * to switch to a new game mode.  If not, the update proceeds
	 * normally.
	 *
	 * @param dt    Number of seconds since last animation frame
	 *
	 * @return whether to process the update loop
	 */
	public boolean preUpdateHelper(float dt) {
		InputController input = InputController.getInstance();
		input.readInput(new Rectangle(0,0,CAMERA_WIDTH,CAMERA_HEIGHT), scale);//use camera rect instead of bounds
		if (listener == null) {
			return true;
		}

//		if (doored && input.didDoor()){
		if (doored){
				if (nextLevelID > MAX_LEVELS){

					setComplete(true);
				}else{

					switchLevel = true;
					targetLevel = nextLevelID;

				}
		}
		// Toggle debug
		if (input.didDebug()) {
			debug = !debug;
		}

		// Handle resets
		if (input.didReset()) {

			reset(currLevel);
		}

		if (input.didHealthRestore()){
			avatar.restoreHealth();
		}
		if (switchLevel){

			switchLevel = false;
			reset(targetLevel);
		}
		if(failed){
			reset(currLevel);
		}
		if(quit){
			listener.exitScreen(this, EXIT_QUIT);
			return false;
		}
		// esc to pause screen
		if (input.didExit()) {
			delay = 0;
			pause_state = !pause_state;
			return true;
			//listener.exitScreen(this, EXIT_QUIT);
			//return false;


		} else if (countdown > 0) {
			countdown--;
		} else if (countdown == 0) {
			if (failed) {
				reset(currLevel);
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
	 * @param dt    Number of seconds since last animation frame
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

		if(InputController.getInstance().cameraMovement){
			x = cameraCoords.x + InputController.getInstance().getHorizontal() * 0.3f * scale.x;
			y = cameraCoords.y + InputController.getInstance().getVertical() * 0.3f * scale.y;
		}

		if(x + (scale.x * (CAMERA_WIDTH + 1) / 2) >= bounds.getWidth() * scale.x){
			x = (bounds.getWidth() - ((CAMERA_WIDTH + 1) / 2f)) * scale.x; //right side
		}else if(x - (scale.x * (CAMERA_WIDTH + 1) / 2) <= 0){
			x = ((CAMERA_WIDTH + 1) / 2f) * scale.x; //left side
		}
		if(y + (scale.y * (CAMERA_HEIGHT + 1) / 2) >= bounds.getHeight() * scale.y){
			y = (bounds.getHeight() - ((CAMERA_HEIGHT + 1) / 2f)) * scale.y;
		} else if(y - (scale.y * (CAMERA_HEIGHT + 1) / 2) <= 0){
			y = ((CAMERA_HEIGHT + 1) / 2f) * scale.y;
		}

		cameraCoords.x = Math.round(x);
		cameraCoords.y = Math.round(y);

		canvas.camera.position.set(cameraCoords, 0);
		canvas.camera.update();
	}

	public List<Zone> zones = new ArrayList<>();

	float life = 1;

	public void addZone(Zone z){
		zones.add(z);

		for(int i = 0; i < z.height; i++){
			if(checkAndRemoveBorder(z.xpos, z.ypos + i, true)){ //left side
				Border b = new Border(z.xpos, z.ypos + i, true, (i%3));
				b.setDrawScale(scale);
				b.setTexture(borderStrips[i % 3]);
				b.initialize(borderStrips[b.getBorderStripNum()]);
				borders.add(b);
			}
			if(checkAndRemoveBorder(z.xpos + z.width, z.ypos + i, true)){
				Border b = new Border(z.xpos + z.width, z.ypos + i, true, (i%3));
				b.setDrawScale(scale);
				b.setTexture(borderStrips[i % 3]);
				b.initialize(borderStrips[b.getBorderStripNum()]);
				borders.add(b);
			}
		}

		for(int i = 0; i < z.width; i++){
			if(checkAndRemoveBorder(z.xpos + i, z.ypos, false)){ //bottom side
				Border b = new Border(z.xpos + i, z.ypos, false, (i%3));
				b.setDrawScale(scale);
				b.setTexture(borderStrips[i % 3]);
				b.initialize(borderStrips[b.getBorderStripNum()]);
				borders.add(b);
			}
			if(checkAndRemoveBorder(z.xpos + i, z.ypos + z.height, false)){ //top side
				Border b = new Border(z.xpos + i, z.ypos + z.height, false, (i%3));
				b.setDrawScale(scale);
				b.setTexture(borderStrips[i % 3]);
				b.initialize(borderStrips[b.getBorderStripNum()]);
				borders.add(b);
			}
		}
	}

	public boolean checkAndRemoveBorder(float x, float y, boolean vertical){
		for(int i = 0; i < borders.size(); i++){
			Border b = borders.get(i);
			if(b.compare(x, y, vertical)){
				borders.remove(b);
				return false;
			}
		}
		return true;
	}



	private float horizontal_parallax = 0.8f;
	private float vertical_parallax = 2;

	public void setParallax(TextureRegion bg){
		float wpixels = bounds.getWidth()*scale.x;
		float hpixels = bounds.getHeight()*scale.y;
		if(canvas.isFullscreen()) {
			horizontal_parallax = (float) (bg.getRegionWidth()) / wpixels;
			vertical_parallax = (float) (bg.getRegionHeight()) / hpixels;
		}else{
			horizontal_parallax = 0.1f;
		}
	}

	public void drawPrimaryBackground(TextureRegion bg){
		Vector2 temp = cameraCoords.cpy();

		temp.x -= canvas.getWidth() / 2f;
		temp.y -= canvas.getHeight() / 2f;

		temp.x *= horizontal_parallax;
		temp.x -= 250; //offset so player doesn'
		temp.y -= 250;
		canvas.draw(bg, temp.x, temp.y);
	}

	public void drawSecondaryBackground(Texture bg, Zone z){
		Vector2 temp = cameraCoords.cpy();
		temp.x -= canvas.getWidth() / 2f;
		temp.y -= canvas.getHeight() / 2f;
		temp.x *= horizontal_parallax;
		temp.x -= 250; //offset so player doesn'
		temp.y -= 250;
		temp.x = (z.xpos * scale.x) - temp.x;
		temp.y = (z.ypos * scale.y) - temp.y;
		float y = bg.getHeight() - temp.y - (z.height * scale.y); //finds y coord
		TextureRegion br = new TextureRegion(bg, (int)temp.x, (int)y, (int)(z.width * scale.x), (int)(z.height * scale.y));
		canvas.draw(br, Color.WHITE, z.xpos * scale.x, z.ypos * scale.y,br.getRegionWidth(), br.getRegionHeight());

	}

	int scuffed_counter = 0;
	int scuffed_delay = 15;
	public void drawFilter(){
		Texture filter;
		if (scuffed_counter >=5 * scuffed_delay) {
			scuffed_counter = 0;
		}
		//filter = filterlist.get((int)(scuffed_counter / scuffed_delay));
		filter = filterlist.get(0);
		scuffed_counter++;
		canvas.draw(filter, new Color(0.5f, 0.5f, 0.5f, 0.13f), cameraCoords.x - canvas.getWidth()/2, cameraCoords.y- canvas.getHeight()/2, filter.getWidth(), filter.getHeight());
	}



	int delay = 0;

	public void drawPause(){
		Gdx.graphics.setCursor(defaultCursor);
		level1MusicCave.resume(level1MusicCaveID);
		level1MusicSunset.resume(level1MusicSunsetID);
		cameraCoords.x = canvas.getWidth() / 2f;
		cameraCoords.y = canvas.getHeight() / 2f;
		canvas.camera.position.set(canvas.getWidth() / 2f, canvas.getHeight() / 2f, 0);
		canvas.camera.update();
		delay++;
		if(delay < 2){
			return;
		}
		InputController input = InputController.getInstance();
		input.readInput(new Rectangle(0,0,CAMERA_WIDTH,CAMERA_HEIGHT), scale);
		if(input.didExit()){
			pause_state = false;
			Gdx.graphics.setCursor(emptyCursor);
		}
		canvas.begin();
		float sx = (float)canvas.getWidth()/pauseScreen.getWidth();
		float sy = (float)canvas.getHeight()/pauseScreen.getHeight();


		float x = cameraCoords.x - canvas.getWidth()/2f;
		float y = cameraCoords.y + canvas.getHeight()/2f;

		Vector2 playPos = new Vector2(x + (590 * sx), y - (540 * sy));
		Vector2 settingPos = new Vector2(x + (871 * sx), y - (540 * sy));
		Vector2 quitPos = new Vector2(x + (1152 * sx), y - (540 * sy));

		canvas.draw(pauseScreen, Color.WHITE, 0, 0, x, y - canvas.getHeight(), 0, sx, sy);
		canvas.draw(playButton, Color.WHITE, 0, playButton.getHeight(), playPos.x, playPos.y, 0, sx, sy);
		canvas.draw(settingsButton, Color.WHITE, 0, settingsButton.getHeight(), settingPos.x, settingPos.y, 0, sx, sy);
		canvas.draw(quitButton, Color.WHITE, 0, quitButton.getHeight(), quitPos.x, quitPos.y, 0, sx, sy);
		canvas.end();
		if(input.didClick()){
			Vector2 ch = input.getCursor();
			if(pressedButton((int)ch.x, (int)ch.y, playButton, playPos)){
				pause_state = false;
				Gdx.graphics.setCursor(emptyCursor);


			}
			if(pressedButton((int)ch.x, (int)ch.y, settingsButton, settingPos)){
				level1MusicCave.pause();
				level1MusicSunset.pause();
				listener.exitScreen(this, -1);
			}
			if(pressedButton((int) ch.x, (int)ch.y, quitButton, quitPos)){
				level1MusicCave.stop();
				level1MusicSunset.stop();
				pause_state = false;
				listener.exitScreen(this, targetLevel);
				Gdx.input.setInputProcessor(null);
			}
		}
	}


	public long getMusicID(){
		if(avatar != null){
			if(avatar.grav == -1){
				return level1MusicCaveID;
			}else{
				return level1MusicSunsetID;
			}
		}
		return 0l;
	}

	public boolean pressedButton(int screenX, int screenY, Texture texture, Vector2 button_pos){
		float button_w = texture.getWidth() * scale.x;
		float button_h = texture.getHeight() * scale.y;
		////////System.out.println(button_center);
		screenY = canvas.getHeight() - screenY;
		if(screenX >= button_pos.x && screenX <= button_pos.x + button_w){
			if(screenY >= button_pos.y - button_h && screenY <= button_pos.y){
				return true;
			}
		}
		return false;
	}



	public boolean quit = false;

	private void drawCrosshair(){
		float crosshair_width = crosshair.getRegionWidth();
		float crosshair_height = crosshair.getRegionHeight();


		Vector2 temp = canBubble2(crosshairLoc);
		if(temp != null){
			if(!InputController.getInstance().isMouseControlls()){
				crosshairLoc.set(temp);
			}
			canvas.draw(crosshair,Color.GREEN,crosshair_width/2, crosshair_height/2, crosshairLoc.x*scale.x, crosshairLoc.y*scale.y,crosshair_width,crosshair_height);
		}
		else{
			canvas.draw(crosshair,Color.RED,crosshair_width/2, crosshair_height/2, crosshairLoc.x*scale.x, crosshairLoc.y*scale.y,crosshair_width,crosshair_height);

		}
	}



	public void draw(float dt) {
		canvas.clear();


		//TODO: parallaxing and stuff kinda relies on pixel size not ideal for diff screen sizes


		canvas.begin();



		drawPrimaryBackground(skybackground);
		for(Zone z: zones){ //draws the backgrounds of the zones
			drawSecondaryBackground(icebackground, z);
		}
		boolean drawTileOutlines = true;
		if(drawTileOutlines == true) {
			for (BoxObstacle b : platforms) {
				b.drawOutline(canvas);
			}
		}
		for(BoxObstacle b: platforms){
			b.draw(canvas);
		}
		for(Door d: doors){
			d.draw(canvas);
		}
		for(Border b: borders){
			b.draw(canvas);
		}
		for(Enemy e : enemies){
			e.draw(canvas);
		}
		for(LucenglazeSensor lg : lucens){
			lg.draw(canvas);
		}
		for(ProjEnemy pe : projenemies){
			pe.draw(canvas);
		}
		for(Spike s : spikelist){
			s.draw(canvas);
		}
		if(rope != null){
			rope.draw(canvas);
		}
		for(Bullet b: bullets){
			b.draw(canvas);
		}
		for (ButtonPrompt b: prompts) {
			b.draw(canvas);
		}
		if(avatar != null){
			avatar.draw(canvas);
		}
		for(Bubble b: bubbles){
			b.draw(canvas);
		}
		for(PoisonGas pg : poisons){
			pg.draw(canvas);
		}
		if(level6Token != null){level6Token.draw(canvas);}
		if(level12Token != null){level12Token.draw(canvas);}
		if (currLevel == 1) {
		}
		drawCrosshair();


		canvas.resetColor();
		canvas.end();

		// Draw life bar


		canvas.shape.setProjectionMatrix(canvas.camera.combined);

		// Draw energy bar
		//TODO: implement energy bar usage


		// Draw energy bar label
		displayFont.setColor(Color.WHITE);
		displayFont.getData().setScale(0.4f);
		canvas.begin(); // DO NOT SCALE
		String additional_part = (BUBBLE_LIMIT == 0) ? "None" : "";
		if(assetsLoaded){
			updateUI();
		}






		canvas.end();

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
		}
		boolean drawFilter = true;
		if(drawFilter == true) {
			canvas.begin();
			drawFilter();
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
	 * @param sound        The sound asset to play
	 * @param soundId    The previously playing sound instance
	 *
	 * @return the new sound instance for this asset.
	 */
	public long playSound(Sound sound, long soundId) {
		return playSound( sound, soundId, soundvolume );
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
	 * @param sound        The sound asset to play
	 * @param soundId    The previously playing sound instance
	 * @param volume    The sound volume
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

		if(pause_state){
			drawPause();
		}else if (active) {
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