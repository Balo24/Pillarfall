package Pillarfall.pillarfall;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.renderers.OrthoCachedTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;


import java.util.ArrayList;
import java.util.List;

public class World {
    private final Player player;
    private final Pillarfall_Game game;
    //Temporär vielleicht gibt es ein besseres System mit der TiledMap
    private List<Enemy> enemies = new ArrayList<>();

    Assets assets = new Assets();
    //Die Map mit dem Format TiledMap --> Level Editor kann genutzt werden
    TiledMap map;
    TiledMapRenderer mapRenderer;

    public World(Player player, Pillarfall_Game game, TiledMap map) {
        this.player = player;
        this.game = game;
        this.map = map;
        //Zum Rendern der Map
        mapRenderer = new TextureMapObjectRenderer(map, 1f/32f, game.getBatch() );
    }

    //Hier kommt die Collision Logik mithilfe der map



    public Player getPlayer() {
        return player;
    }
}
