package Pillarfall.pillarfall;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

public class Enemy {



    //Unterscheidung bei der Bewegung für Animationen
    private enum movestate{
        IDLE, RUNNING, JUMPING
    }
    //Verschieden Aktionen für die Logik
    private enum action{
        ATTACK, IDLE, MOVE, JUMP
    }

    movestate Movestate = movestate.IDLE;
    //Attribute / Felder
    private Vector2 velocity;
    private Vector2 position;


    //Für die Health-Bar
    private final int MAX_HEALTH;
    private int health;

    private final int SPEED;
    private final int JUMP_POWER;
    private boolean is_jumping = false;

    //Konstruktor
    public Enemy(int x, int y, int maxHealth, int speed, int jumpPower)
    {
        SPEED = speed;
        JUMP_POWER = jumpPower;
        this.position = new Vector2(x,y);
        velocity = new Vector2(0,0);

        MAX_HEALTH = maxHealth;

    }
    //Update Methode, die die Bewegung und andere Logiken des Gegners aktualisiert
    public void update()
    {
        move();
        AI();
    }


     //Bewegung des Gegners --> Ändere es so dass es wie beim Spieler ist
     public void move()
     {


     }
     //Logik zur Auswahl der Aktion --> Springen, Bewegen oder Wenn Spieler in der Nähe Angreifen
     public void AI()
     {

     }



}



