package edu.cornell.gdiac.physics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import edu.cornell.gdiac.physics.obstacle.BoxObstacle;
import edu.cornell.gdiac.physics.obstacle.WheelObstacle;

import java.util.ArrayList;
import java.util.List;

public class LevelEditorV2 {

    private FileHandle file = Gdx.files.internal("lvl2.json");
    private FileHandle file2 = Gdx.files.internal("propertytypes.json");
    private FileHandle file3 = Gdx.files.internal("constants.json");
    private JsonReader jsonReader = new JsonReader();
    private JsonValue constants = jsonReader.parse(file3);
    public JsonValue jsonValue = jsonReader.parse(file);
    private List<BoxObstacle> boxes = new ArrayList<>();
    private List<WheelObstacle> bubbles = new ArrayList<>();
    private List<Zone> gravityZones = new ArrayList<>();
    private List<Spike> spikes = new ArrayList<>();
    private List<Enemy> enemies = new ArrayList<>();
    private TextureRegion earthTile;
    private TextureRegion goalTile;
    private ArrayList<String> textureStrings;
    private ArrayList<TextureRegion> textureObjects;
    private BoxObstacle goal;
    private List<List<Integer>> tileMap = new ArrayList<>();
    private DudeModel player;

    public LevelEditorV2() {


    }

    public void readTextures(ArrayList<TextureRegion> textures) {
        textureObjects = textures;
    }


    public void readJson() {

        JsonReader json = new JsonReader();
        JsonValue base = json.parse(Gdx.files.internal("lvl2.json"));

        JsonReader json1 = new JsonReader();
        JsonValue base1 = json.parse(Gdx.files.internal("propertytypes.json"));

        int mapWidth = base.getInt("width");
        int mapHeight = base.getInt("height");

        System.out.println("Testing Tile -1");

        JsonValue firstLayers = base.get("layers");


        for (JsonValue obj : firstLayers) {
            if (obj.getInt("id") == 6) {
                JsonValue secondLayers = obj.get("layers");
                System.out.println("Testing");

                for (JsonValue obj1 : secondLayers) {

                        if (obj1.getInt("id") == 7) {
                            JsonValue go = obj1.get("objects");


                            System.out.println(go.toString());

                            for (JsonValue goals : go) {


                                BoxObstacle wo = new BoxObstacle(
                                        (goals.getFloat("x")) / 64,
                                        mapHeight - (goals.getFloat("y")) / 64,
                                        1,
                                        1
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
                                        (pl.getFloat("x")) / 64,
                                        mapHeight - (pl.getFloat("y")) / 64,
                                        1F,
                                        1F
                                );

                                player = wo;
                            }

                        }

                        if (obj1.getInt("id") == 9) {
                            JsonValue sp = obj1.get("objects");


                            for (JsonValue goals : sp) {


                                Spike wo = new Spike(
                                        (goals.getFloat("x")) / 64,
                                        mapHeight - (goals.getFloat("y")) / 64,
                                        1,
                                        1
                                );

                                spikes.add(wo);
                            }

                        }




                        if (obj1.getInt("id") == 5) {
                            JsonValue enemyList = obj1.get("objects");

                            for (JsonValue en : enemyList) {

                                Enemy ene = new Enemy(
                                        (en.getFloat("x")) / 64,
                                        mapHeight - (en.getFloat("y")) / 64,
                                        0.9F,
                                        1
                                );

                                JsonValue prop = en.get("properties");

                                int left = 0;
                                int right = 0;

                                for (JsonValue prop1 : prop) {
                                    if (prop1.getString("name").equals("leftBound")) {
                                        left = prop1.getInt("value");
                                    }
                                    if (prop1.getString("name").equals("rightBound")) {
                                        right = prop1.getInt("value");
                                    }
                                }

                                ene.setBounds(left, right);

                                enemies.add(ene);

                            }
                        }

                        if (obj1.getInt("id") == 8) {

                            JsonValue bubbleList = obj1.get("objects");

                            System.out.println("Testing bub");

                            for (JsonValue bub : bubbleList) {

                                for (JsonValue type : base1) {

                                    if (type.getInt("id") == 4) {


                                        System.out.println(type.get("members"));

                                        WheelObstacle wo = new WheelObstacle(
                                                (bub.getFloat("x")) / 64,
                                                mapHeight - (bub.getFloat("y")) / 64,
                                                type.get("members").get(0).getInt("value")
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

                            for (int i = 0; i < mapWidth; i++) {
                                List<Integer> row = new ArrayList<>();
                                for (int j = 0; j < mapHeight; j++) {
                                    int index = i * mapWidth + j;
                                    if (index < tileData.length) {
                                        row.add(tileData[index]);
                                    } else {
                                        row.add(0);
                                    }
                                }
                                tileMap.add(row);
                            }

                            for (int i = 0; i < mapHeight; i++) {
                                for (int j = 0; j < mapWidth; j++) {
                                    if (tileMap.get(j).get(i) > 0) {
                                        BoxObstacle wo = new BoxObstacle(
                                                i,
                                                mapHeight - j,
                                                1,
                                                1
                                        );

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



}
