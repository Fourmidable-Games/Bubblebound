package edu.cornell.gdiac.physics;

import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.io.FileReader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import edu.cornell.gdiac.assets.JsonValueParser;
import edu.cornell.gdiac.physics.obstacle.BoxObstacle;
import edu.cornell.gdiac.physics.obstacle.WheelObstacle;
import sun.tools.jstat.Scale;

public class LevelEditor {

    private FileHandle file = Gdx.files.internal("levelTest.json");
    private JsonReader jsonReader = new JsonReader();
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

    public LevelEditor() {

    }


    public void readTextures(ArrayList<TextureRegion> textures) {
        textureObjects = textures;
    }


    public void readJson() {

        int xo = 0;
        int yo = 0;

        for (JsonValue object : jsonValue.get("Objects")) {
            System.out.println(jsonValue);

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
                System.out.println("Test");
                System.out.println((object.get("repeat")).asInt());
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

            WheelObstacle wo = new WheelObstacle(
                    object.get("x").asInt(),
                    object.get("y").asInt(),
                    object.get("radius").asInt()
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

            enemies.add(wo);

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


}



//    Spike sp = new Spike(1, 1, 1, 1);
//		sp.setBodyType(BodyDef.BodyType.StaticBody);
//                sp.setDrawScale(scale);
//                sp.setName("spike");
//                addObject(sp);