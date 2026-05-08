package Pillarfall.pillarfall;

import com.badlogic.gdx.Gdx;

public class Enemy {

    //Attribute / Felder
     private float x;
     private float y; 

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
            //this.x += speed;
            x += speed * Gdx.graphics.getDeltaTime();
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



