package Pillarfall.pillarfall;

public class Enemy {

    //Attribute / Felder
    private int x;
    private int y;

    private int speed;

    //Konstruktor
    public Enemy(int x, int y)
    {
        //Initialisierung der Attribute
        this.x = x;
        this.y = y;
        this.speed = 1;
        
    }


  
        //Bewegung des Gegners
        public void move()
        {
            this.x += speed;
        }

        //Update Methode, die die Bewegung und andere Logiken des Gegners aktualisiert
        public void update()
        {
            move();
           
        }
           
        //Rendering der position und des Gegners
        public void render()
        {
            System.out.println("Gegner bei: " + x + ", " + y);
        }

}



