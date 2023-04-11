package edu.cornell.gdiac.bubblebound;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import edu.cornell.gdiac.bubblebound.obstacle.BoxObstacle;
//import sun.tools.jstat.Scale;
import edu.cornell.gdiac.util.FilmStrip;

public class LevelEditor {

    private FileHandle file = Gdx.files.internal("levelTest.json");
    private JsonReader jsonReader = new JsonReader();
    public JsonValue jsonValue = jsonReader.parse(file);
    private List<BoxObstacle> boxes = new ArrayList<>();
    private List<Bubble> bubbles = new ArrayList<>();
    private List<Zone> gravityZones = new ArrayList<>();
    private List<Spike> spikes = new ArrayList<>();
    private List<Enemy> enemies = new ArrayList<>();
    private TextureRegion earthTile;
    private TextureRegion goalTile;
    private ArrayList<String> textureStrings;
    private ArrayList<TextureRegion> textureObjects;
    private BoxObstacle goal;

    public LevelEditor() {

    }


    public void readTextures(ArrayList<TextureRegion> textures) {
        textureObjects = textures;
    }


    public void readJson() {

        int xo = 0;
        int yo = 0;

        for (JsonValue object : jsonValue.get("Objects")) {
            // System.out.println(jsonValue);

            if (object == null) {
                return;
            }

            BoxObstacle myObject = new BoxObstacle(

                    object.get("x").asInt(),
                    object.get("y").asInt(),
                    object.get("width").asInt(),
                    object.get("height").asInt()


            );

            if ((object.get("repeat")).asInt() > 1) {
                // System.out.println("Test");
                // System.out.println((object.get("repeat")).asInt());
                for (int i = 1; i < (object.get("repeat")).asInt(); i++) {
                    if ((object.get("repeatx")).asInt() > 1) {
                        xo = i;
                    }

                    if ((object.get("repeaty")).asInt() > 1) {
                        yo = i;

                    }

                    BoxObstacle myObject2 = new BoxObstacle(
                            object.get("x").asInt() + xo,
                            object.get("y").asInt() + yo,
                            object.get("width").asInt(),
                            object.get("height").asInt()
                    );

                    if ((object.get("texture")).asInt() < 0) {
                        myObject2.setTexture(textureObjects.get((object.get("texture")).asInt()));
                    }

                    boxes.add(myObject2);


                }

            }


//            if (object.get("texture").asString() == "earthTexture") {
//                myObject.setTexture(earthTile);
//            }

            if ((object.get("texture")).asInt() < 0) {
                myObject.setTexture(textureObjects.get((object.get("texture")).asInt()));
            }


            boxes.add(myObject);

        }

        for (JsonValue object : jsonValue.get("Bubbles")) {

            Bubble wo = new Bubble(
                    new Vector2(object.get("x").asFloat(), object.get("y").asFloat()),
                    object.get("radius").asInt(),
                    Bubble.BubbleType.STATIC
            );

            bubbles.add(wo);

        }

        for (JsonValue object : jsonValue.get("Gravity Zones")) {

            Zone wo = new Zone(
                    object.get("x").asFloat(),
                    object.get("y").asFloat(),
                    object.get("width").asFloat(),
                    object.get("height").asFloat(),
                    object.get("gravity").asFloat(),
                    null
            );

            gravityZones.add(wo);

        }

        for (JsonValue object : jsonValue.get("Spikes")) {

            Spike wo = new Spike(
                    object.get("x").asFloat(),
                    object.get("y").asFloat(),
                    object.get("width").asFloat(),
                    object.get("height").asFloat()
            );

            spikes.add(wo);

        }


        for (JsonValue object : jsonValue.get("Enemies")) {

            Enemy wo = new Enemy(
                    object.get("x").asFloat(),
                    object.get("y").asFloat(),
                    object.get("width").asFloat(),
                    object.get("height").asFloat()
            );

            wo.setBounds( object.get("boundx").asFloat(), object.get("boundy").asFloat());

            enemies.add(wo);

        }

        for (JsonValue object : jsonValue.get("Goal")) {

            BoxObstacle wo = new BoxObstacle(
                    object.get("x").asFloat(),
                    object.get("y").asFloat(),
                    object.get("width").asFloat(),
                    object.get("height").asFloat()
            );

            wo.isGoal = true;

            goal = wo;

        }



    }

    public List getBoxes() {
        return boxes;
    }

    public List getBubbles() {
        return bubbles;
    }

    public List getGravityZones() {
        return gravityZones;
    }

    public List getSpikes() {
        return spikes;
    }

    public List getEnemies() {
        return enemies;
    }

    public BoxObstacle getGoal() {
        return goal;
    }


}



//    Spike sp = new Spike(1, 1, 1, 1);
//		sp.setBodyType(BodyDef.BodyType.StaticBody);
//                sp.setDrawScale(scale);
//                sp.setName("spike");
//                addObject(sp);