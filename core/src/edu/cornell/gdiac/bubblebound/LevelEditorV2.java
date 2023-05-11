package edu.cornell.gdiac.bubblebound;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import edu.cornell.gdiac.bubblebound.obstacle.*;

import org.w3c.dom.Text;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class LevelEditorV2 {

    private String jsonName;
//    private FileHandle file = Gdx.files.internal(jsonName);
    private FileHandle file2 = Gdx.files.internal("propertytypes.json");
    private FileHandle file3 = Gdx.files.internal("platform/constants.json");
    private JsonReader jsonReader = new JsonReader();
    private JsonValue constants = jsonReader.parse(file3);
//    public JsonValue jsonValue = jsonReader.parse(file);
    private List<BoxObstacle> boxes = new ArrayList<>();
    private List<Bubble> bubbles = new ArrayList<>();
    private List<Zone> gravityZones = new ArrayList<>();
    private List<Spike> spikes = new ArrayList<>();
    private List<Enemy> enemies = new ArrayList<>();
    private List<Lucenglaze> glazeList = new ArrayList<>();
    private ArrayList<Integer> glazeRotations = new ArrayList<>();
    private ArrayList<Door> doors = new ArrayList<>();
    private ArrayList<ProjEnemy> projEnemies = new ArrayList<>();
    private List<List<Float>> projEnemyData = new ArrayList<>();
    private TextureRegion earthTile;
    private TextureRegion goalTile;
    private TextureRegion one;
    private TextureRegion two;
    private TextureRegion three;
    private TextureRegion four;
    private TextureRegion five;
    private TextureRegion six;
    private TextureRegion seven;
    private TextureRegion eight;
    private TextureRegion nine;
    private ArrayList<String> textureStrings;
    private ArrayList<TextureRegion> textureObjects;
//    private BoxObstacle goal;
    private List<List<Integer>> tileMap = new ArrayList<>();
    private DudeModel player;
    private PlayerController playerController;
    private ArrayList<TextureRegion> spikeTextures;

    public LevelEditorV2(PlayerController pc) {
        jsonName = "lvl1.json";
        playerController = pc;
    }

    public LevelEditorV2(PlayerController pc, String filename){
        jsonName =filename;
        playerController = pc;
    }

    public void readTileTextures(ArrayList<TextureRegion> textures, ArrayList<TextureRegion> spike) {
        textureObjects = textures;
        spikeTextures = spike;
    }


    public void readJson() {

        // 0 is no, 1 is yes to randomize solid ice and non corner snow blocks
        int randomizeTiles = 0;

        JsonReader json = new JsonReader();
        JsonValue base = json.parse(Gdx.files.internal(jsonName));

        JsonReader json1 = new JsonReader();
        JsonValue base1 = json.parse(Gdx.files.internal("propertytypes.json"));

        int mapWidth = base.getInt("width")*64;
        int mapHeight = base.getInt("height")*64;

        JsonValue mapProperties = base.get("properties");

        for (JsonValue prop : mapProperties) {

            if (prop.getString("name").equals("ran")) {
                randomizeTiles = prop.getInt("value");
            }
        }

        //////System.out.println("Testing Tile -1");

        JsonValue firstLayers = base.get("layers");


        for (JsonValue obj : firstLayers) {
            if (obj.getInt("id") == 6) {
                JsonValue secondLayers = obj.get("layers");
                //////System.out.println("Testing");

                for (JsonValue obj1 : secondLayers) {

                    if (obj1.getInt("id") == 7) {
                        JsonValue go = obj1.get("objects");



                        for (JsonValue goals : go) {
//                            ////System.out.println(go.toString());
                            JsonValue prop = goals.get("properties");
                            int targetLevel = 1;
                            Door.SpawnDirection player_spawn_direction = Door.SpawnDirection.RIGHT;

                            for (JsonValue prop1 : prop) {
                                if (prop1.getString("name").equals("targetLevel")) {
                                    targetLevel = prop1.getInt("value");
                                }
                                if (prop1.getString("name").equals("direction")) {
                                    player_spawn_direction = (prop1.getString("value").equals("right")) ? Door.SpawnDirection.RIGHT : Door.SpawnDirection.LEFT;
                                }
                            }

//                            BoxObstacle wo = new BoxObstacle(
//                                    (goals.getFloat("x")) / 64,
//                                    ((mapHeight - (goals.getFloat("y"))) / 64)+2,
//                                    goals.getFloat("width")/64,
//                                    goals.getFloat("height")/64
//                            );
                            Vector2 door_loc = new Vector2((goals.getFloat("x"))/64,((mapHeight - (goals.getFloat("y")))/64)+2);
                            Door d = new Door(door_loc,player_spawn_direction,targetLevel);

//                            wo.isGoal = true;
                            //////System.out.println("GOOOALLL");
//                            goal = wo;
                            doors.add(d);
                        }

                    }

                    if (obj1.getInt("id") == 10) {
                        JsonValue sp = obj1.get("objects");


                        for (JsonValue pl : sp) {

                            DudeModel wo = new DudeModel(playerController,
                                    constants.get("dude"),
                                    pl.getFloat("width")/64,
                                    pl.getFloat("height")/64,
                                    (pl.getFloat("x")) / 64,
                                    ((mapHeight - (pl.getFloat("y")))/ 64)+1
                            );


                            player = wo;
                        }

                    }


                    if (obj1.getInt("id") == 15) {

                        JsonValue prop = obj1.get("objects");

                        for (JsonValue ene : prop) {

                            float rotation = 0;
                            int x_offset= 0;
                            int y_offset = 0;


                            if (ene.getFloat("rotation") == 90) {
                                rotation = 1;
                                y_offset = y_offset;
                            }

                            else if (ene.getFloat("rotation") == -180 || ene.getFloat("rotation") == 180) {
                                rotation = 2;
                                x_offset = -1;
                                y_offset = y_offset;
                            }

                            else if (ene.getFloat("rotation") == -90) {
                                rotation = 3;
                                x_offset = -1;
                                y_offset = 1;
                            }

                            else if (ene.getFloat("rotation") == 0) {
                                y_offset = 1;
                            }

                            List<Float> data = new ArrayList<>();
                            data.add((ene.getFloat("x")/64) + x_offset);
                            data.add(((mapHeight - ene.getFloat("y"))/64) + y_offset);
                            data.add(rotation);

                            projEnemyData.add(data);


                        }

                    }

                    if (obj1.getInt("id") == 9) {
                        JsonValue sp = obj1.get("objects");


                        for (JsonValue goals : sp) {
                            float rotation = goals.getFloat("rotation");
                            Spike wo = new Spike(
                                    (goals.getFloat("x")) / 64,
                                    ((mapHeight - (goals.getFloat("y"))) / 64)+1,
                                    goals.getFloat("width")/64,
                                    goals.getFloat("height")/64,
                                    rotation
                            );
                            if (rotation == 180 || rotation == -180){
                                wo.setX(wo.getX() - 1);
                                wo.setY(wo.getY() - 1);
                            }
                            if (rotation == 90){
                                wo.setY(wo.getY() - 1);
                            }
                            if (rotation == -90){
                                wo.setX(wo.getX() - 1);
                            }

                            if (goals.getInt("gid") == 552) {

                                wo.setTexture(spikeTextures.get(0));
                                wo.setName("spike");


                            }

                            else if (goals.getInt("gid") == 595) {

                                wo.setTexture(spikeTextures.get(1));
                                wo.setInstantKill();
                                wo.setName("spike");

                            }

                            else {

                                wo.setTexture(spikeTextures.get(2));
                                wo.setInstantKill();
                                wo.setName("spike");
                            }

                            spikes.add(wo);

                        }



                    }

                    if (obj1.getInt("id") == 11) {
                        JsonValue gr = obj1.get("objects");


                        for (JsonValue grav : gr) {

                            float zone_width = (grav.getFloat("width"))/64;
                            float zone_height = (grav.getFloat("height"))/64;

                            Zone wo = new Zone(
                                    (grav.getFloat("x")) / 64 - 0.5f,
                                    (mapHeight - (grav.getFloat("y"))) / 64 - zone_height +0.5f,
                                    zone_width,
                                    zone_height,
                                    -1,
                                    new Vector2(0,0)
                            );

                            gravityZones.add(wo);
                        }

                    }




                    if (obj1.getInt("id") == 5) {
                        JsonValue enemyList = obj1.get("objects");

                        for (JsonValue en : enemyList) {

                            Enemy ene = new Enemy(
                                    (en.getFloat("x")) / 64,
                                    ((mapHeight - en.getFloat("y"))) / 64,
                                    en.getFloat("width")/64,
                                    en.getFloat("height")/64
                            );

                            JsonValue prop = en.get("properties");

                            int left_displacement = 0;
                            int right_displacement = 0;

                            for (JsonValue prop1 : prop) {
                                if (prop1.getString("name").equals("leftBound")) {
                                    left_displacement = prop1.getInt("value");
                                }
                                if (prop1.getString("name").equals("rightBound")) {
                                    right_displacement = prop1.getInt("value");
                                }
                            }

                            ene.setBounds(left_displacement, right_displacement);

                            enemies.add(ene);

                        }
                    }

                    if (obj1.getInt("id") == 13) {

                        JsonValue g = obj1.get("objects");

                        for (JsonValue w : g) {


                            Lucenglaze wo;
                            wo = new Lucenglaze(0,0);

                            if (w.getFloat("rotation") == 90) {
                                wo= new Lucenglaze((w.getFloat("x")) / 64,
                                        (((mapHeight - w.getFloat("y"))) / 64));
                                glazeRotations.add(1);
                            }

                            else if (w.getFloat("rotation") == -180 || w.getFloat("rotation") == 180) {
                                wo= new Lucenglaze(((w.getFloat("x")) / 64)-1,
                                        (((mapHeight - w.getFloat("y"))) / 64));
                                glazeRotations.add(2);
                            }

                            else if (w.getFloat("rotation") == -90) {
                                wo= new Lucenglaze(((w.getFloat("x")) / 64)-1,
                                        (((mapHeight - w.getFloat("y"))) / 64)+1);
                                glazeRotations.add(3);
                            }

                            else if (w.getFloat("rotation") == 0) {
                                wo = new Lucenglaze((w.getFloat("x")) / 64,
                                        (((mapHeight - w.getFloat("y"))) / 64)+1);
                                glazeRotations.add(0);
                            }else{
                                ////System.out.println("OH DANG WE MISSED ONE HERE! at " + glazeRotations.size() + " the rot was " + w.getFloat("rotation"));
                            }




                            glazeList.add(wo);

                        }


                    }

                    if (obj1.getInt("id") == 8 || obj1.getString("name").equals("bubbles")) {



                        JsonValue bubbleList = obj1.get("objects");

                        //////System.out.println("Testing bub");

                        for (JsonValue bub : bubbleList) {

                            //System.out.println("bubblecreat");





                                //////System.out.println(type.get("members"));

                            Vector2 v = new Vector2((bub.getFloat("x")) / 64, ((mapHeight - (bub.getFloat("y"))) / 64)+1);

                            Bubble wo = new Bubble(
                                    v,
                                    1,
                                    Bubble.BubbleType.STATIC
                            );


                            bubbles.add(wo);


                        }

                    }


                    if (obj1.getInt("id") == 1) {

                        //////System.out.println("Testing 2");

                        JsonValue tileData1 = obj1.get("data");

                        int[] tileData = tileData1.asIntArray();

                        for (int i = 0; i < mapHeight/64; i++) {
                            List<Integer> row = new ArrayList<>();
                            for (int j = 0; j < mapWidth/64; j++) {
                                int index = i * (mapWidth/64) + j;
                                if (index < tileData.length) {
                                    row.add(tileData[index]);
                                } else {
                                    row.add(0);
                                }
                            }
                            tileMap.add(row);
                        }

                        for (int i = 0; i < mapWidth/64; i++) {
                            for (int j = 0; j < mapHeight/64; j++) {
                                int k = tileMap.get(j).get(i);
                                if(tileMap.get(j).get(i) < 60){
                                    continue;
                                }

                                if (randomizeTiles == 1) {

                                    Random ran = new Random();
                                    int randomInt;
                                    int offset;
                                    if ((k >= 69 && k <= 72) || (k >= 83 && k <= 86)) {
                                        // We will create a random int to choose a solid ice block at random
                                        // which is a block with no snow and is a square
                                        randomInt = ran.nextInt(2);
                                        offset = ran.nextInt(3);

                                        switch (randomInt) {

                                            case 0:
                                                k = offset + 69;
                                                break;

                                            case 1:
                                                k = offset + 83;

                                        }

                                    } else if ((k >= 63 && k <= 66) || (k >= 77 && k <= 80)) {
                                        // We will create a random int to choose a snow block at random
                                        // This excludes the corner snow block with ID 61 and 75
                                        // Also excludes sloped snow blocs
                                        randomInt = ran.nextInt(2);
                                        offset = ran.nextInt(3);

                                        switch (randomInt) {

                                            case 0:
                                                k = offset + 63;
                                                break;

                                            case 1:
                                                k = offset + 77;

                                        }

                                    }
                                }

                                BoxObstacle wo = new BoxObstacle(
                                        i,
                                        (mapHeight/64) - j,
                                        1,
                                        1
                                );

                                int arrayOffset = 450;

                                boolean flip = false;


                                if (k >= 517 && k <= 522) {
                                    arrayOffset = 488;
                                }

                                if (k == 710) {
                                    arrayOffset = 543;
                                }

                                else if (k >= 711 && k <= 718) {
                                    arrayOffset = 543;
                                }

                                else if (k >= 720 && k <= 776) {
                                    arrayOffset = 681+4;
                                }

                                else if (k >= 631 && k <= 705) {

                                    if (k == 643) {
                                        arrayOffset = 541-1;
                                    }

                                    else {
                                        arrayOffset = 541-2;
                                    }


                                }

                                else if (k >= 507 && k <= 510) {
                                    arrayOffset = 332-1;
                                }



                                if (!flip) {
                                    if(k == 642){
                                        wo.setTexture(textureObjects.get(k+1-arrayOffset));
                                        boxes.add(wo);
                                    }else{
                                        wo.setTexture(textureObjects.get(k-arrayOffset));
                                        boxes.add(wo);
                                    }


                                }

                                else {
                                    wo.setTexture(textureObjects.get(textureObjects.size()-1));
                                    boxes.add(wo);
                                }

                            }
                        }

                    }

                }


            }


        }


        //////System.out.println("Finished Parsing");
    }


    public List<BoxObstacle> getBoxes() {
        return boxes;
    }

    public List<Bubble> getBubbles() {
        return bubbles;
    }

    public List<Enemy> getEnemies() {
        return enemies;
    }
    public DudeModel getPlayerAtLocation(Vector2 location, Door.SpawnDirection sd) {
        player.setPosition(location);
        player.setFacingRight(sd == Door.SpawnDirection.RIGHT);
        return player;
    }
    public DudeModel getPlayer(Door.SpawnDirection sd){
        player.setFacingRight(sd == Door.SpawnDirection.RIGHT);
        return player;
    }

    public ArrayList<Door> getDoors() { return doors;}

    public List<Spike> getSpikes() {
        return spikes;
    }

    public List<Zone> getGravityZones() {
        return gravityZones;
    }

    public List<Lucenglaze> getGlazes() {return glazeList; }

    public List getGlazeRotations() {return glazeRotations;}

    public List getProjEnemyData() {return projEnemyData;}



}