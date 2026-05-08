package Pillarfall.pillarfall;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

public class Enemy {

    //Attribute / Felder

    //Unterscheidung bei der Bewegung
    private enum movestate{
        IDLE, RUNNING, JUMPING
    }
    //Verschieden Aktionen für die Logik
    private enum action{
        ATTACK, IDLE, MOVE, JUMP
    }

    movestate Movestate = movestate.IDLE;

    private Vector2 velocity;
    private Vector2 position;
    private Vector2 acceleration;

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
        acceleration = new Vector2(0,0);
        MAX_HEALTH = maxHealth;

    }
    //Update Methode, die die Bewegung und andere Logiken des Gegners aktualisiert
    public void update()
    {
        move();
    }


     //Bewegung des Gegners
     public void move()
     {
         float delta = Gdx.graphics.getDeltaTime();
         if(Movestate == movestate.RUNNING)
         {
             velocity.add(acceleration.cpy().scl(delta));
             position.add(velocity.cpy().scl(delta));
         }

         if(Movestate == movestate.JUMPING || !is_jumping)
         {
             velocity.add(acceleration.cpy().scl(delta));
             position.add(velocity.cpy().scl(delta));
         }

     }
     //Logik zur Auswahl der Aktion --> Springen, Bewegen oder Wenn Spieler in der Nähe Angreifen
     public void AI()
     {

     }



}



