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
        spawnEnemies();
        loadCollisionArray();
    }

    public void update(float delta) {

        player.resetWallState();

        handleCollision_PlayerX();
        handleCollision_PlayerY();

        for (Enemy enemy : enemies) {
            handleCollision_EnemyX(enemy);
            handleCollision_EnemyY(enemy);
            enemy.update(player, delta);
            handleAttack(enemy);
        }

        player.update();

        player.getPlayer_sprite().setPosition(player.getPositionX(), player.getPositionY());
        player.getPlayer_rect().set(
            player.getPositionX(),
            player.getPositionY(),
            player.getPlayer_sprite().getWidth(),
            player.getPlayer_sprite().getHeight()
        );
    }

    private void spawnEnemies()
    {
        Enemy enemy = new Enemy(75,
            3, 50,
            5, 6,
            10, 8f,
            1.2f, 1.0f,
            game.getAssets().manager.get(Assets.enemyTexture),
            game.getAssets().manager.get(Assets.enemyDeathTexture));
        enemies.add(enemy);
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

    private boolean groundUnderPlayer() {
        return groundUnder(player.getPositionX(), player.getPositionY());
    }

    private boolean groundUnderEnemy(Enemy enemy) {
        return groundUnder(enemy.getPositionX(), enemy.getPositionY());
    }

    private void cliffAhead(Enemy enemy)
    {
        int checkX;

        if (enemy.getDirection() > 0) {
            checkX = (int)((enemy.getPositionX() + enemy.getBounds().getWidth()) / TILE_SIZE) + 1;
        } else {
            checkX = (int)(enemy.getPositionX() / TILE_SIZE) - 1;
        }

        int checkY = (int)((enemy.getPositionY() - 0.05f) / TILE_SIZE);

        if (checkX < 0 || checkX >= collisionGrid.length ||
            checkY < 0 || checkY >= collisionGrid[0].length)
        {
            enemy.setIs_cliffAhead(true); // Kartenrand = Klippe
        }

        if(collisionGrid[checkX][checkY] != TileType.SOLID)
        {
            enemy.setIs_cliffAhead(true);
        }
        else {
            enemy.setIs_cliffAhead(false);
        }
    }


    private boolean groundUnder(float posX, float posY) {
        int leftGridX  = (int) posX;
        int rightGridX = (int) (posX + 0.5f);

        int checkGridY = (int) ((posY - 0.05f) / TILE_SIZE);

        if (leftGridX < 0 || rightGridX >= collisionGrid.length ||
            checkGridY < 0 || checkGridY >= collisionGrid[0].length) {
            return false;
        }

        for (int x = leftGridX; x <= rightGridX; x++) {
            if (collisionGrid[x][checkGridY] == TileType.SOLID) {
                return true;
            }
        }
        return false;
    }


    private void handleCollision_PlayerX() {
        float delta = Gdx.graphics.getDeltaTime();



        float posY = player.getPositionY();
        float posX = player.getPositionX();

        float velX = player.getVelocityX();
        // Theoretische Position im nächsten Frame
        float nextX = posX + velX * delta;


        // Tiles um den Spieler
        int leftGridX   = (int) (nextX / TILE_SIZE);
        int rightGridX  = (int) ((nextX + player.getPlayer_rect().getWidth()) / TILE_SIZE);

        int bottomGridY = (int) (posY / TILE_SIZE);
        int topGridY    = (int) ((posY + player.getPlayer_rect().getHeight()) / TILE_SIZE);


        // Sicherheit-Check gegen Array-Abstürze
        if (leftGridX < 0 || rightGridX >= collisionGrid.length || bottomGridY < 0 || topGridY >= collisionGrid[0].length) {
            return;
        }

        if (velX > 0) {
            for (int y = bottomGridY; y <= topGridY; y++) {
                if(collisionGrid[rightGridX][y] == TileType.HAZARD)
                {
                    // Collision Checking für Spikes/Hazard Tiles mit einem Threshold
                    float threshold = (nextX + player.getPlayer_rect().getWidth()) / TILE_SIZE - rightGridX;
//                    System.out.println(threshold);
                    if (threshold > 0.25f) {
                        hazardHit();
                        return;
                    }
                }
                if (collisionGrid[rightGridX][y] == TileType.SOLID) {
                    // Falls collision die Position korrigieren → An der Kante der Wand
                    player.setVelocityX(0);
                    float correctX = (rightGridX * TILE_SIZE) - player.getPlayer_rect().getWidth() - 0.01f;
                    player.setPosition(correctX, posY);

                    player.setOnRightWall(true);
                    return;
                }
            }
        }
        else if (velX < 0) {
            for (int y = bottomGridY; y <= topGridY; y++) {
                if(collisionGrid[rightGridX][y] == TileType.HAZARD)
                {
                    // Collision Checking für Spikes/Hazard Tiles mit einem Threshold
                    float threshold = nextX / TILE_SIZE - leftGridX;
//                    System.out.println(threshold);
                    if (threshold > 0.25f) {
                        hazardHit();
                        return;
                    }
                }
                if (collisionGrid[leftGridX][y] == TileType.SOLID) {
                    // Falls collision die Position korrigieren → An der Kante der Wand → andere Seite
                    player.setVelocityX(0);
                    float correctX = ((leftGridX + 1) * TILE_SIZE) + 0.01f;
                    player.setPosition(correctX, posY);

                    player.setOnLeftWall(true);
                    return;
                }
            }
        }

        // Wenn der Weg frei ist, die Bewegung ausführen
        player.setPosition(nextX, posY);
    }

    private void handleCollision_PlayerY() {
        float delta = Gdx.graphics.getDeltaTime();

        // 1. Klippen-Check: Wenn unter den Füßen Luft ist, entziehen den Bodenkontakt
        if (!groundUnderPlayer()) {
            player.setIs_Grounded(false);
        } else {
            player.setIs_Grounded(true);
        }
        float posY = player.getPositionY();
        float posX = player.getPositionX();

        float velY = player.getVelocityY();

        float nextY = posY + velY * delta;


        int leftGridX   = (int) (posX);
        int rightGridX  = (int) (posX + 0.5f);

        int bottomGridY = (int) (nextY / TILE_SIZE);
        int topGridY    = (int) ((nextY + 1.0f) / TILE_SIZE);

        if (leftGridX < 0 || rightGridX >= collisionGrid.length || bottomGridY < 0 || topGridY >= collisionGrid.length) {
            return;
        }

        // Aufwärtssprung (Deckenkollision)
        if (velY > 0) {
            boolean ceilingFound = false;
            for (int x = leftGridX; x <= rightGridX; x++) {
                if(collisionGrid[x][topGridY] == TileType.HAZARD)
                {
                    // Collision Checking für Spikes/Hazard Tiles mit einem Threshold
                    float threshold = (nextY + player.getPlayer_rect().getHeight()) / TILE_SIZE - topGridY;
//                    System.out.println(threshold);
                    if (threshold > 0.25f) {
                        hazardHit();
                        return;
                    }
                }
                if (collisionGrid[x][topGridY] == TileType.SOLID) {
                    player.setVelocityY(0);
                    float correctY = (topGridY * TILE_SIZE) - 1.0f - 0.001f;
                    player.setPosition(posX, correctY);
                    ceilingFound = true;
                    break;
                }
            }
            if (!ceilingFound) {
                player.setPosition(posX, nextY);
            }
        }
        // Abwärtsfall oder Stehen (Bodenkollision)
        else {
            boolean groundFound = false;
            for (int x = leftGridX; x <= rightGridX; x++) {
                if(collisionGrid[x][bottomGridY] == TileType.HAZARD)
                {
                    // Collision Checking für Spikes/Hazard Tiles mit einem Threshold
                    float threshold = nextY / TILE_SIZE - bottomGridY;
//                    System.out.println(threshold);
                    if (threshold > 0.25f) {
                        hazardHit();
                        return;
                    }
                }
                if (collisionGrid[x][bottomGridY] == TileType.SOLID) {
                    player.setVelocityY(0);
                    float correctY = ((bottomGridY + 1) * TILE_SIZE) + 0.001f;
                    player.setPosition(posX, correctY);

                    player.setIs_Grounded(true); // Landung registrieren
                    groundFound = true;

                    player.setWallJumpLockTimer(0f);

                    player.setSliding(false);
                    break;
                }
            }

            if (!groundFound) {
                player.setPosition(posX, nextY);
            }
        }
    }

    private void handleCollision_EnemyX(Enemy enemy)
    {
        float delta = Gdx.graphics.getDeltaTime();

        float posY = enemy.getPositionY();
        float posX = enemy.getPositionX();

        float velX = enemy.getVelocityX();

        // Theoretische Position im nächsten Frame
        float nextX = posX + velX * delta;



        // Tiles um den Gegner
        int leftGridX   = (int) (nextX / TILE_SIZE);
        int rightGridX  = (int) ((nextX + enemy.getBounds().getWidth()) / TILE_SIZE);

        int bottomGridY = (int) (posY / TILE_SIZE);
        int topGridY    = (int) ((posY + enemy.getBounds().getHeight()) / TILE_SIZE);

        // Sicherheit-Check gegen Array-Abstürze
        if (leftGridX < 0 || rightGridX >= collisionGrid.length || bottomGridY < 0 || topGridY >= collisionGrid[0].length) {
            return;
        }

        if (velX > 0) {
            for (int y = bottomGridY; y <= topGridY; y++) {
                if (collisionGrid[rightGridX][y] == TileType.SOLID) {
                    // Falls collision die Position korrigieren → An der Kante der Wand
                    enemy.setVelocityX(0);
                    float correctX = (rightGridX * TILE_SIZE) - enemy.getBounds().getWidth() - 0.01f;
                    enemy.setPosition(correctX, posY);
                    enemy.setIs_wallAhead(true);
                    return;
                }
            }
        }
        else if (velX < 0) {
            for (int y = bottomGridY; y <= topGridY; y++) {
                if (collisionGrid[leftGridX][y] == TileType.SOLID) {
                    // Falls collision die Position korrigieren → An der Kante der Wand → andere Seite
                    enemy.setVelocityX(0);
                    float correctX = ((leftGridX + 1) * TILE_SIZE) + 0.01f;
                    enemy.setPosition(correctX, posY);
                    enemy.setIs_wallAhead(true);
                    return;
                }
            }
        }

        // Wenn der Weg frei ist, die Bewegung ausführen
        enemy.setPosition(nextX, posY);
        enemy.setIs_wallAhead(false);

    }
    private void handleCollision_EnemyY(Enemy enemy)
    {
        float delta = Gdx.graphics.getDeltaTime();

        // 1. Klippen-Check: Wenn unter den Füßen Luft ist, entziehen den Bodenkontakt
        if (!groundUnderEnemy(enemy)) {
            enemy.setIs_Grounded(false);
        } else {
            enemy.setIs_Grounded(true);
        }
        float posY = enemy.getPositionY();
        float posX = enemy.getPositionX();

        float velY = enemy.getVelocityY();

        float nextY = posY + velY * delta;


        int leftGridX   = (int) (posX);
        int rightGridX  = (int) (posX + 0.5f);

        int bottomGridY = (int) (nextY / TILE_SIZE);
        int topGridY    = (int) ((nextY + 1.0f) / TILE_SIZE);

        if (leftGridX < 0 || rightGridX >= collisionGrid.length || bottomGridY < 0 || topGridY >= collisionGrid.length) {
            return;
        }

        // Aufwärtssprung (Deckenkollision)
        if (velY > 0) {
            boolean deckeGefunden = false;
            for (int x = leftGridX; x <= rightGridX; x++) {
                if (collisionGrid[x][topGridY] == TileType.SOLID) {
                    enemy.setVelocityY(0);
                    float korrigiertesY = (topGridY * TILE_SIZE) - 1.0f - 0.001f;
                    enemy.setPosition(posX, korrigiertesY);
                    deckeGefunden = true;
                    break;
                }
            }
            if (!deckeGefunden) {
                enemy.setPosition(posX, nextY);
            }
        }
        // Abwärtsfall oder Stehen (Bodenkollision)
        else {
            boolean bodenGefunden = false;
            for (int x = leftGridX; x <= rightGridX; x++) {
                if (collisionGrid[x][bottomGridY] == TileType.SOLID) {
                    enemy.setVelocityY(0);
                    float korrigiertesY = ((bottomGridY + 1) * TILE_SIZE) + 0.001f;
                    enemy.setPosition(posX, korrigiertesY);

                    enemy.setIs_Grounded(true); // Landung registrieren
                    bodenGefunden = true;
                    break;
                }
            }

            if (!bodenGefunden) {
                enemy.setPosition(posX, nextY);
            }
        }
    }

    private void handleAttack(Enemy enemy)
    {
        if(player.getAttackHitbox().overlaps(enemy.getBounds()))
        {
            System.out.println("Attack: HIT");
            enemy.damage(player.getATTACK_DAMAGE());

        }

    }
    private void hazardHit()
    {
        //Eine Berührung mit Stacheln ist sofort tödlich → der Death-Screen übernimmt
        player.kill("Stacheln");
    }


    public List<Enemy> getEnemies() { return enemies; }
    public Player getPlayer() { return player; }
}
