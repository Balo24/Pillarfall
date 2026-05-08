package Pillarfall.pillarfall;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;

public class Player {

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

    //Vektoren führen zu einer besseren Bewegung --> nicht steif
    private Vector2 velocity;
    private Vector2 position;
    private Vector2 acceleration;

    public Player(int maxHealth, float x, float y, int speed, int jump_power) {
        this.MAX_HEALTH = maxHealth;
        this.position = new Vector2(x,y);
        velocity = new Vector2(0,0);
        acceleration = new Vector2(0,0);
        this.SPEED = speed;
        this.JUMP_POWER = jump_power;
    }

    public void update()
    {
        Inputhandler();
        move();
    }
    //Bewegung des Spielers
    private void move()
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
    //Inputs des Spielers
    private void Inputhandler()
    {
        if(Gdx.input.isKeyPressed(Input.Keys.D))
        {
            Movestate = movestate.RUNNING;
            acceleration.set(SPEED, 0);
        }
        else if(Gdx.input.isKeyPressed(Input.Keys.A))
        {
            Movestate = movestate.RUNNING;
            acceleration.set(-SPEED, 0);

        }
        else if(Gdx.input.isKeyPressed(Input.Keys.SPACE))
        {
            Movestate = movestate.JUMPING;
            is_jumping = true;
            acceleration.set(0, JUMP_POWER);
        }
        else if(!Gdx.input.isKeyPressed(Input.Keys.D) || !Gdx.input.isKeyPressed(Input.Keys.A))
        {
            Movestate = movestate.IDLE;
        }
    }


}
