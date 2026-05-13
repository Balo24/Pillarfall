package Pillarfall.pillarfall;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.renderers.OrthoCachedTiledMapRenderer;


import java.util.ArrayList;
import java.util.List;

public class World {
    private final Player player;
    //Temporär vielleicht gibt es ein besseres System mit der TiledMap
    private List<Enemy> enemies = new ArrayList<>();

    Assets assets = new Assets();
    //Die Map mit dem Format TiledMap --> Level Editor kann genutzt werden
    TiledMap map;
    TiledMapRenderer mapRenderer;

    public World(Player player, TiledMap map) {
        this.player = player;
        this.map = map;
        //Zum Rendern der Map
        mapRenderer = new OrthoCachedTiledMapRenderer(map, 1f/16f);
    }

    //Hier kommt die Collision Logik mithilfe der map



    public Player getPlayer() {
        return player;
    }
}
