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

public class LevelEditor {

    private FileHandle file = Gdx.files.internal("levelTest.json");
    private JsonReader jsonReader = new JsonReader();
    public JsonValue jsonValue = jsonReader.parse(file);
    private List<BoxObstacle> boxes = new ArrayList<>();
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
                    BoxObstacle myObject2 = new BoxObstacle(
                            object.get("x").asInt()+i,
                            object.get("y").asInt(),
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

    }

    public List getBoxes() {
        return boxes;
    }

}



