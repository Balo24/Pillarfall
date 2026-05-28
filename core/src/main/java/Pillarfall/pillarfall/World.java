package Pillarfall.pillarfall;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;
import java.util.List;

public class World {
    private final Player player;
    private final Pillarfall_Game game;
    private final List<Enemy> enemies = new ArrayList<>();

    private static final int TILE_SIZE = 32;
    private static final String COLLISION_LAYER = "Collision";

    private final TiledMap map;
    final TiledMapRenderer mapRenderer;

    public World(Player player, Pillarfall_Game game, TiledMap map) {
        this.player = player;
        this.game = game;
        this.map = map;

        mapRenderer = new TextureMapObjectRenderer(map, 1f/32f, game.getBatch());
        initializeEnemies();
    }

    private void initializeEnemies() {
        Texture enemyTexture = game.getAssets().manager.get(Assets.enemyTexture);
        Texture enemyDeathTexture = game.getAssets().manager.get(Assets.enemyDeathTexture);
        Enemy enemy = new Enemy(75, 3, 50, 5, 6, 10, 8f, 1.2f, 1.0f, enemyTexture, enemyDeathTexture);

        TiledMapTileLayer collisionLayer =
                (TiledMapTileLayer) map.getLayers().get(COLLISION_LAYER);
        enemy.setCollisionLayer(collisionLayer);

        enemies.add(enemy);
    }


    public void update(float delta) {
        player.update();

        for (Enemy enemy : enemies) {
            enemy.update(player, delta);
        }

        if (!player.isDead()) {
            player.attackEnemies(enemies);
        }

        handleCollision();
    }


    // =========================================
    // COLLISION LOGIC
    // =========================================

    private void handleCollision() {

        Rectangle playerBounds = player.getBounds();

        // Mittelpunkt des Players
        int tileX = (int)(playerBounds.x / TILE_SIZE);
        int tileY = (int)(playerBounds.y / TILE_SIZE);

        TiledMapTileLayer layer =
                (TiledMapTileLayer) map.getLayers().get(COLLISION_LAYER);

        if(layer == null) return;

        // Alle Tiles um den Spieler prüfen
        for(int x = tileX - 1; x <= tileX + 1; x++) {
            for(int y = tileY - 1; y <= tileY + 1; y++) {
                // Prüfe, ob das Tile eine Kollision hat
                if(layer.getCell(x, y) != null) {
                    // Füge hier die Kollisionslogik hinzu
                }
            }
        }
    }


    public List<Enemy> getEnemies() {
        return enemies;
    }

    public Player getPlayer() {
        return player;
    }


}
