package Pillarfall.pillarfall;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;

import java.util.ArrayList;
import java.util.List;

public class World {
    private final Player player;
    private final Pillarfall_Game game;
    private final List<Enemy> enemies = new ArrayList<>();

    private static final float TILE_SIZE = 1.0f;
    private static final String COLLISION_LAYER = "CollisionLayer";

    private int[][] collisionGrid;

    private final TiledMap map;
    final TiledMapRenderer mapRenderer;

    public World(Player player, Pillarfall_Game game, TiledMap map) {
        this.player = player;
        this.game = game;
        this.map = map;

        mapRenderer = new TextureMapObjectRenderer(map, 1f/32f, game.getBatch());

        loadCollisionArray();
    }

    public void update(float delta) {
        player.update();


        handleCollisionX();

        handleCollisionY();

        player.getPlayer_sprite().setPosition(player.getPositionX(), player.getPositionY());
        player.getPlayer_rect().set(
            player.getPositionX(),
            player.getPositionY(),
            player.getPlayer_sprite().getWidth(),
            player.getPlayer_sprite().getHeight()
        );

        for (Enemy enemy : enemies) {
            enemy.update(player, delta);
        }

        if (!player.isDead()) {
            player.attackEnemies(enemies);
        }
    }

    private void loadCollisionArray() {
        MapLayer layer = map.getLayers().get(COLLISION_LAYER);
        if (layer == null) return;
        MapObjects objects = layer.getObjects();

        TiledMapTileLayer baseLayer = (TiledMapTileLayer) map.getLayers().get(0);
        int mapWidth = baseLayer.getWidth();
        int mapHeight = baseLayer.getHeight();

        collisionGrid = new int[mapWidth][mapHeight];

        for(MapObject obj : objects) {
            if(obj instanceof TiledMapTileMapObject) {
                // REPARATUR: Math.floor + 0.15f fängt den Tiled-Pixelversatz ab
                // und zwingt die Kacheln in den mathematisch korrekten Grid-Quadranten!
                int objX = (int) Math.floor((((TiledMapTileMapObject) obj).getX() + 5f) / 32f);
                int objY = (int) Math.floor((((TiledMapTileMapObject) obj).getY() + 5f) / 32f);
                int gid = ((TiledMapTileMapObject) obj).getTile().getId();

                if (objX >= 0 && objX < mapWidth && objY >= 0 && objY < mapHeight) {
                    if(gid == 35) {
                        collisionGrid[objX][objY] = TileType.SOLID;
                    } else if (gid == 37) {
                        collisionGrid[objX][objY] = TileType.CHECKPOINT;
                    } else if (gid == 36) {
                        collisionGrid[objX][objY] = TileType.HAZARD;
                    }
                }
            }
        }
    }

    private boolean groundUnderFeet() {
        int leftGridX   = (int) (player.getPositionX());
        int rightGridX  = (int) (player.getPositionX() + 0.5f);

        // prüfen unter den Füßen
        int checkGridY  = (int) ((player.getPositionY() - 0.05f) / TILE_SIZE);

        if (leftGridX < 0 || rightGridX >= collisionGrid.length || checkGridY < 0 || checkGridY >= collisionGrid.length) {
            return false;
        }

        // Schleife prüft nur den echten Bereich unter dem Spieler
        for (int x = leftGridX; x <= rightGridX; x++) {
            if (collisionGrid[x][checkGridY] == TileType.SOLID) {
                return true; // Es gibt festen Boden!
            }
        }
        return false; // Spieler läuft über eine Klippe -> Fallzustand!
    }



    private void handleCollisionX() {
        float delta = Gdx.graphics.getDeltaTime();
        float nextX = player.getPositionX() + player.getVelocityX() * delta;

        float playerWidth = 0.5f;
        float playerHeight = 1.0f;

        int leftGridX   = (int) (nextX / TILE_SIZE);
        int rightGridX  = (int) ((nextX + playerWidth) / TILE_SIZE);

        int bottomGridY = (int) (player.getPositionY() / TILE_SIZE);
        int topGridY    = (int) ((player.getPositionY() + playerHeight) / TILE_SIZE);

        // Sicherheits-Check gegen Array-Abstürze
        if (leftGridX < 0 || rightGridX >= collisionGrid.length || bottomGridY < 0 || topGridY >= collisionGrid[0].length) {
            return;
        }

        if (player.getVelocityX() > 0) {
            for (int y = bottomGridY; y <= topGridY; y++) {
                if(collisionGrid[rightGridX][y] == TileType.HAZARD)
                {
                    float threshold = (nextX + 0.5f) - rightGridX;
                    if (threshold > 0.15f) {
                        player.respawn();
                        return;
                    }
                }
                if (collisionGrid[rightGridX][y] == TileType.SOLID) {
                    player.setVelocityX(0);
                    float correctX = (rightGridX * TILE_SIZE) - playerWidth - 0.01f;
                    player.setPosition(correctX, player.getPositionY());
                    return;
                }
            }
        }
        else if (player.getVelocityX() < 0) {
            for (int y = bottomGridY; y <= topGridY; y++) {
                if (collisionGrid[leftGridX][y] == TileType.SOLID) {
                    player.setVelocityX(0);
                    float correctX = ((leftGridX + 1) * TILE_SIZE) + 0.01f;
                    player.setPosition(correctX, player.getPositionY());
                    return;
                }
            }
        }

        // Wenn der Weg frei ist, die Bewegung ausführen
        player.setPosition(nextX, player.getPositionY());
    }

    private void handleCollisionY() {
        float delta = Gdx.graphics.getDeltaTime();

        // 1. Klippen-Check: Wenn unter den Füßen Luft ist, entziehen wir den Bodenkontakt
        if (!groundUnderFeet()) {
            player.setIs_Grounded(false);
        } else {
            player.setIs_Grounded(true);
        }

        float nextY = player.getPositionY() + player.getVelocityY() * delta;


        int leftGridX   = (int) (player.getPositionX());
        int rightGridX  = (int) (player.getPositionX() + 0.5f);

        int bottomGridY = (int) (nextY / TILE_SIZE);
        int topGridY    = (int) ((nextY + 1.0f) / TILE_SIZE);

        if (leftGridX < 0 || rightGridX >= collisionGrid.length || bottomGridY < 0 || topGridY >= collisionGrid.length) {
            return;
        }

        // Aufwärtssprung (Deckenkollision)
        if (player.getVelocityY() > 0) {
            boolean deckeGefunden = false;
            for (int x = leftGridX; x <= rightGridX; x++) {
                if (collisionGrid[x][topGridY] == TileType.SOLID) {
                    player.setVelocityY(0);
                    float korrigiertesY = (topGridY * TILE_SIZE) - 1.0f - 0.001f;
                    player.setPosition(player.getPositionX(), korrigiertesY);
                    deckeGefunden = true;
                    break;
                }
            }
            if (!deckeGefunden) {
                player.setPosition(player.getPositionX(), nextY);
            }
        }
        // Abwärtsfall oder Stehen (Bodenkollision)
        else {
            boolean bodenGefunden = false;
            for (int x = leftGridX; x <= rightGridX; x++) {
                if (collisionGrid[x][bottomGridY] == TileType.HAZARD) {
                    // Berechne, wie tief die Füße des Spielers schon im Stachel-Tile stecken
                    float spiketop = bottomGridY + 1.0f;
                    float threshouldY = spiketop - nextY;


                    if (threshouldY > 0.25f) {
                        player.respawn();
                        return;
                    }
                }
                if (collisionGrid[x][bottomGridY] == TileType.SOLID) {
                    player.setVelocityY(0);
                    float korrigiertesY = ((bottomGridY + 1) * TILE_SIZE) + 0.001f;
                    player.setPosition(player.getPositionX(), korrigiertesY);

                    player.setIs_Grounded(true); // Landung registrieren
                    bodenGefunden = true;
                    break;
                }
            }

            if (!bodenGefunden) {
                player.setPosition(player.getPositionX(), nextY);
            }
        }
    }




    public List<Enemy> getEnemies() { return enemies; }
    public Player getPlayer() { return player; }
}
