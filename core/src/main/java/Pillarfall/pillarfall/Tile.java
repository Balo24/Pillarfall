package Pillarfall.pillarfall;

public class Tile {

    //Mehr Objekt Orientierter
    public int id;
    public boolean solid;
    public boolean hazard;

    public Tile(int id, boolean solid, boolean hazard) {
        this.id = id;
        this.solid = solid;
        this.hazard = hazard;
    }


}
