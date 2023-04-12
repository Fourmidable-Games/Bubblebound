package edu.cornell.gdiac.bubblebound;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import edu.cornell.gdiac.bubblebound.obstacle.BoxObstacle;
import edu.cornell.gdiac.bubblebound.obstacle.Lucenglaze;
import edu.cornell.gdiac.bubblebound.obstacle.WheelObstacle;

import java.util.ArrayList;
import java.util.List;

public class LevelEditorV2 {

    private String jsonName = "lvlA.json";
    private FileHandle file = Gdx.files.internal(jsonName);
    private FileHandle file2 = Gdx.files.internal("propertytypes.json");
    private FileHandle file3 = Gdx.files.internal("platform/constants.json");
    private JsonReader jsonReader = new JsonReader();
    private JsonValue constants = jsonReader.parse(file3);
    public JsonValue jsonValue = jsonReader.parse(file);
    private List<BoxObstacle> boxes = new ArrayList<>();
    private List<Bubble> bubbles = new ArrayList<>();
    private List<Zone> gravityZones = new ArrayList<>();
    private List<Spike> spikes = new ArrayList<>();
    private List<Enemy> enemies = new ArrayList<>();
    private List<Lucenglaze> glazeList = new ArrayList<>();
    private ArrayList<Integer> glazeRotations = new ArrayList<>();
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
    private BoxObstacle goal;
    private List<List<Integer>> tileMap = new ArrayList<>();
    private DudeModel player;

    public LevelEditorV2() {


    }

    public void readTileTextures(ArrayList<TextureRegion> textures) {
        textureObjects = textures;
    }


    public void readJson() {

        JsonReader json = new JsonReader();
        JsonValue base = json.parse(Gdx.files.internal(jsonName));

        JsonReader json1 = new JsonReader();
        JsonValue base1 = json.parse(Gdx.files.internal("propertytypes.json"));

        int mapWidth = base.getInt("width")*64;
        int mapHeight = base.getInt("height")*64;

        System.out.println("Testing Tile -1");

        JsonValue firstLayers = base.get("layers");


        for (JsonValue obj : firstLayers) {
            if (obj.getInt("id") == 6) {
                JsonValue secondLayers = obj.get("layers");
                System.out.println("Testing");

                for (JsonValue obj1 : secondLayers) {

                    if (obj1.getInt("id") == 7) {
                        JsonValue go = obj1.get("objects");



                        for (JsonValue goals : go) {


                            BoxObstacle wo = new BoxObstacle(
                                    (goals.getFloat("x")) / 64,
                                    ((mapHeight - (goals.getFloat("y"))) / 64)+2,
                                    goals.getFloat("width")/64,
                                    goals.getFloat("height")/64
                            );


                            wo.isGoal = true;
                            System.out.println("GOOOALLL");
                            goal = wo;
                        }

                    }

                    if (obj1.getInt("id") == 10) {
                        JsonValue sp = obj1.get("objects");


                        for (JsonValue pl : sp) {


                            DudeModel wo = new DudeModel(
                                    constants.get("dude"),
                                    pl.getFloat("width")/64,
                                    pl.getFloat("height")/64,
                                    (pl.getFloat("x")) / 64,
                                    ((mapHeight - (pl.getFloat("y")))/ 64)+1
                            );

                            player = wo;
                        }

                    }

                    if (obj1.getInt("id") == 9) {
                        JsonValue sp = obj1.get("objects");


                        for (JsonValue goals : sp) {


                            Spike wo = new Spike(
                                    (goals.getFloat("x")) / 64,
                                    ((mapHeight - (goals.getFloat("y"))) / 64)+1,
                                    goals.getFloat("width")/64,
                                    goals.getFloat("height")/64
                            );



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

                            else if (w.getFloat("rotation") == -180) {
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
                            }




                            glazeList.add(wo);

                        }


                    }

                    if (obj1.getInt("id") == 8) {

                        JsonValue bubbleList = obj1.get("objects");

                        System.out.println("Testing bub");

                        for (JsonValue bub : bubbleList) {

                            for (JsonValue type : base1) {

                                if (type.getInt("id") == 4) {


                                    System.out.println(type.get("members"));

                                    Vector2 v = new Vector2((bub.getFloat("x")) / 64, ((mapHeight - (bub.getFloat("y"))) / 64)+1);

                                    Bubble wo = new Bubble(
                                            v,
                                            type.get("members").get(0).getInt("value"),
                                            Bubble.BubbleType.STATIC
                                    );


                                    bubbles.add(wo);
                                }
                            }
                        }

                    }


                    if (obj1.getInt("id") == 1) {

                        System.out.println("Testing 2");

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
                                if (tileMap.get(j).get(i) == 18) {
                                    BoxObstacle wo = new BoxObstacle(
                                            i,
                                            (mapHeight/64) - j,
                                            1,
                                            1
                                    );

                                    wo.setTexture(textureObjects.get(0));

                                    boxes.add(wo);
                                }
                                else if (tileMap.get(j).get(i) == 19) {
                                    BoxObstacle wo = new BoxObstacle(
                                            i,
                                            (mapHeight/64) - j,
                                            1,
                                            1
                                    );

                                    wo.setTexture(textureObjects.get(1));

                                    boxes.add(wo);
                                }

                                else if (tileMap.get(j).get(i) == 20) {
                                    BoxObstacle wo = new BoxObstacle(
                                            i,
                                            (mapHeight/64) - j,
                                            1,
                                            1
                                    );

                                    wo.setTexture(textureObjects.get(2));

                                    boxes.add(wo);
                                }

                                else if (tileMap.get(j).get(i) == 21) {
                                    BoxObstacle wo = new BoxObstacle(
                                            i,
                                            (mapHeight/64) - j,
                                            1,
                                            1
                                    );

                                    wo.setTexture(textureObjects.get(3));

                                    boxes.add(wo);
                                }

                                else if (tileMap.get(j).get(i) == 22) {
                                    BoxObstacle wo = new BoxObstacle(
                                            i,
                                            (mapHeight/64) - j,
                                            1,
                                            1
                                    );

                                    wo.setTexture(textureObjects.get(4));

                                    boxes.add(wo);
                                }

                                else if (tileMap.get(j).get(i) == 24) {
                                    BoxObstacle wo = new BoxObstacle(
                                            i,
                                            (mapHeight/64) - j,
                                            1,
                                            1
                                    );

                                    wo.setTexture(textureObjects.get(6));

                                    boxes.add(wo);
                                }

                                else if (tileMap.get(j).get(i) == 25) {
                                    BoxObstacle wo = new BoxObstacle(
                                            i,
                                            (mapHeight/64) - j,
                                            1,
                                            1
                                    );

                                    wo.setTexture(textureObjects.get(7));

                                    boxes.add(wo);
                                }

                                else if (tileMap.get(j).get(i) == 26) {
                                    BoxObstacle wo = new BoxObstacle(
                                            i,
                                            (mapHeight/64) - j,
                                            1,
                                            1
                                    );

                                    wo.setTexture(textureObjects.get(8));

                                    boxes.add(wo);
                                }

                                else if (tileMap.get(j).get(i) == 27) {
                                    BoxObstacle wo = new BoxObstacle(
                                            i,
                                            (mapHeight/64) - j,
                                            1,
                                            1
                                    );

                                    wo.setTexture(textureObjects.get(9));

                                    boxes.add(wo);
                                }

                                else if (tileMap.get(j).get(i) == 2) {
                                    BoxObstacle wo = new BoxObstacle(
                                            i,
                                            (mapHeight/64) - j,
                                            1,
                                            1
                                    );

                                    wo.setTexture(textureObjects.get(10));

                                    boxes.add(wo);
                                }

                            }
                        }

                    }

                }


            }


        }


    }


    public List getBoxes() {
        return boxes;
    }

    public List getBubbles() {
        return bubbles;
    }

    public List getEnemies() {
        return enemies;
    }
    public DudeModel getPlayer() {
        return player;
    }

    public BoxObstacle getGoal() {
        return goal;
    }

    public List getSpikes() {
        return spikes;
    }

    public List getGravityZones() {
        return gravityZones;
    }

    public List getGlazes() {return glazeList; }

    public List getGlazeRotations() {return glazeRotations;}



}