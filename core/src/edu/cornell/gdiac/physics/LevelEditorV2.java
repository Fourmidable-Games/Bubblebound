package edu.cornell.gdiac.physics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import edu.cornell.gdiac.physics.obstacle.BoxObstacle;
import edu.cornell.gdiac.physics.obstacle.WheelObstacle;

import java.util.ArrayList;
import java.util.List;

public class LevelEditorV2 {

    private FileHandle file = Gdx.files.internal("lvl2.json");
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
    private BoxObstacle goal;

    public LevelEditorV2() {

    }

    public void readTextures(ArrayList<TextureRegion> textures) {
        textureObjects = textures;
    }


    public void readJson() {

        TiledMap tiledMap = new TmxMapLoader().load("lvl2.tmx");

        MapLayers layers = tiledMap.getLayers();

        TiledMapTileLayer layer = (TiledMapTileLayer) layers.get("Tile Layer 1");

        for (int row = 0; row < layer.getHeight(); row++) {
            for (int col = 0; col < layer.getWidth(); col++) {
                TiledMapTileLayer.Cell cell = layer.getCell(col, row);
                if (cell == null) continue;

                float x = col * layer.getTileWidth();
                float y = (layer.getHeight() - row - 1) * layer.getTileHeight();

                BoxObstacle wo = new BoxObstacle(x,y,1,1);
                boxes.add(wo);


            }

        }


    }

    public List getBoxes() {
        return boxes;
    }

}
