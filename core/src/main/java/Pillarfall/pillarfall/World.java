package Pillarfall.pillarfall;

public class World {
    public static final int TILE_SIZE = 16;
    public static final int MAX_WIDTH = 200;
    public static final int MAX_HEIGHT = 100;

    Player player;
    Enemy enemy;

    Tile AIR = new Tile(0,false,false);
    Tile STONE = new Tile(1,true,false);
    Tile SPIKE = new Tile(2,true,true);

    private Tile[][] map;

    public void update()
    {
        initializeSimpleMap();
    }

    private void initializeSimpleMap() {
        // Boden am unteren Ende
        for (int y = 0; y < MAX_HEIGHT; y++)
        {
            for (int x = 0; x < MAX_WIDTH; x++) {
                map[y][x] = AIR;
            }
        }
        for (int i = 0; i < MAX_WIDTH; i++)
        {
            map[MAX_HEIGHT - 1][i] = STONE;
        }



    }
    public Tile getTile(int x, int y) {
        if (x >= 0 && x < MAX_WIDTH && y >= 0 && y < MAX_HEIGHT) {
            return map[y][x];
        }
        return null;
    }


}
