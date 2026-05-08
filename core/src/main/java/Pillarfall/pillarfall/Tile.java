package Pillarfall.pillarfall;

public class Tile {
    // Konstanten, bleiben unverändert
    public static final int TILE_SIZE = 32;
    public static final int MAX_WIDTH = 200;
    public static final int MAX_HEIGHT = 100;
    
    // Attribute
    private int[][] map;
    private int width;
    private int height;
     
    //Konstruktor - initialisiert die Tile-Map
     
    public Tile() {
        this.width = 200;
        this.height = 50;
        this.map = new int[height][width];
        initializeSimpleMap();
    }
    
     
    //Initialisiert eine einfache Test-Map mit Blöcken
    
    private void initializeSimpleMap() {
        // Boden am unteren Ende
        for (int x = 0; x < width; x++) {
            map[height - 1][x] = 1;  // Block-Typ 1 = Boden
        }

    }
    
    // Getter
    public int[][] getMap() {
        return map;
    }
    
    public int getWidth() {
        return width;
    }
    
    public int getHeight() {
        return height;
    }
    
    public int getTile(int x, int y) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            return map[y][x];
        }
        return 0;
    }
}
