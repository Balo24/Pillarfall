package Pillarfall.pillarfall;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Player {

    //Für Animationen
    private enum movestate{
        RUNNING, IDLE, JUMPING
    }

    private movestate Movestate = movestate.IDLE;

    //Für die Health-Bar
    private final int MAX_HEALTH;
    private int health;

    private final int SPEED;
    private final int JUMP_POWER;
    private boolean is_jumping = false;
    private float direction = 1;


    //Vektoren führen zu einer besseren Bewegung --> nicht steif
    private Vector2 velocity;
    private Vector2 position = new Vector2(0,0);



    private final Sprite player_sprite;
    private final Rectangle player_rect;

    public Player(int maxHealth, int speed, int jump_power, Texture player_tex) {
        this.MAX_HEALTH = maxHealth;


        velocity = new Vector2(0,0);


        this.SPEED = speed;
        this.JUMP_POWER = jump_power;
        this.player_sprite = new Sprite(player_tex);

        player_sprite.setPosition(position.x,position.y);
        //Größe in World Units --> 1 : 16px
        player_sprite.setSize(1,1);

        this.player_rect = new Rectangle(player_sprite.getX(), player_sprite.getY(),player_sprite.getWidth(), player_sprite.getHeight());
    }

    public void update()
    {

        //Simple Ground Collision damit Springen und Gravitation funktioniert --> Austauschen mit dem richtigen System
        if(position.y <= 0)
        {
            position.y = 0;
            velocity.y = 0;
            is_jumping = false;
        }
        Inputhandler();
        move();

        player_sprite.setPosition(position.x,position.y);
        player_rect.set(player_sprite.getX(), player_sprite.getY(),player_sprite.getWidth(), player_sprite.getHeight());

        //System.out.println("POS: " + position + " VEL: " + velocity); --> Zum debuggen

    }
    //Falls der Spieler sich nicht bewegt und man das Movement-System geändert hat
    private void debug_Movement()
    {
        float delta = Gdx.graphics.getDeltaTime();

        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            position.x += 200 * delta;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            position.x -= 200 * delta;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            position.y += 200 * delta;
        }

        player_sprite.setPosition(position.x, position.y);

        System.out.println(position);
    }


    //Bewegung des Spielers
    private void move()
    {
        float delta = Gdx.graphics.getDeltaTime();

        //Gravitation
        velocity.y -= 40f * delta;

        float targetSpeed = direction * SPEED;

        //Geschwindigkeit
        float acceleration = is_jumping ? 4f : 12f; //Wenn is_jumping true dann 12f sonst 4f
        float differenz = targetSpeed - velocity.x;
        velocity.x += acceleration * differenz * delta;

        //Bewegung
        position.add(velocity.x * delta, velocity.y * delta);


    }
    //Inputs des Spielers
    private void Inputhandler() {

        direction = 0f;

        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            direction = 1f;
            if(!is_jumping)
            {
                Movestate = movestate.RUNNING;
            }

        }

        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            direction = -1f;
            if(!is_jumping)
            {
                Movestate = movestate.RUNNING;
            }

        }
        //Keine Bewegung
        if (direction == 0 && !is_jumping) {
            Movestate = movestate.IDLE;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && !is_jumping) {
            velocity.y = JUMP_POWER;
            is_jumping = true;
            Movestate = movestate.JUMPING;
        }
    }

    public Sprite getPlayer_sprite() {
        return player_sprite;
    }
    //Startposition kann durch checkpoints ausgetauscht werden
    public void setPosition(float x, float y) {
        this.position.set(x, y);
        this.velocity.set(0, 0);

    }

    public float getPositionX() {
        return position.x;
    }

    public float getPositionY() {
        return position.y;
    }

    public Vector2 getPosition() {
        return position;
    }
}
