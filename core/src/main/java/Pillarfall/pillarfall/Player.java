package Pillarfall.pillarfall;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.List;

public class Player {



    private enum movestate{
        RUNNING, IDLE, JUMPING, DASHING
    }

    private movestate Movestate = movestate.IDLE;

    private final int MAX_HEALTH;
    private int health;

    private final int SPEED;
    private final int JUMP_POWER;
    private final int DASH_POWER;

    private final int ATTACK_DAMAGE = 25;
    private final float ATTACK_RANGE = 1.3f;
    private final float ATTACK_COOLDOWN = 0.5f;

    private boolean is_jumping = false;
    private boolean is_Grounded = false;
    private boolean is_dashing = false;
    private boolean onLeftWall = false;
    private boolean onRightWall = false;
    private boolean isSliding = false;

    private float direction = 1;
    private float dash_dir;

    private float dashTimer;
    private float attackTimer = 0f;
    private float wallJumpLockTimer = 0f;



    private Vector2 velocity;
    private final Vector2 position = new Vector2(0,0);
    private final Vector2 spawnPosition = new Vector2(0,0);

    private final Sprite player_sprite;
    private final Rectangle player_rect;


    public Player(int maxHealth, int speed, int jump_power, int dashPower, Texture player_tex) {
        this.MAX_HEALTH = maxHealth;
        this.health = maxHealth;
        DASH_POWER = dashPower;
        this.position.set(0,0);
        this.spawnPosition.set(0,0);

        velocity = new Vector2(0,0);

        this.SPEED = speed;
        this.JUMP_POWER = jump_power;
        this.player_sprite = new Sprite(player_tex);

        player_sprite.setPosition(position.x,position.y);
        player_sprite.setSize(0.5f,1f);

        this.player_rect = new Rectangle(position.x,position.y,0.5f,1f);
    }

    public void update()
    {

        float delta = Gdx.graphics.getDeltaTime();

        attackTimer += delta;

        Inputhandler();


        if(is_dashing)
        {
            dashTimer += delta;
        }


        if(dashTimer >= 2f)
        {
            is_dashing = false;
            dashTimer = 0;
        }
        applyGravity();
        move();
    }

    private void move()
    {
        float delta = Gdx.graphics.getDeltaTime();

        if (wallJumpLockTimer > 0) {
            wallJumpLockTimer -= delta;
            if(velocity.x > 0 && direction > 0 || velocity.x < 0 && direction < 0)
            {
                wallJumpLockTimer = 0f;
            }
            else {
                return;
            }
        }

        float targetSpeed = direction * SPEED;
        float acceleration = is_jumping || is_dashing ? 8f : 35f;
        float difference = targetSpeed - velocity.x;
        velocity.x += acceleration * difference * delta;

    }

    public void applyGravity()
    {
        float delta = Gdx.graphics.getDeltaTime();

        if(!is_Grounded)
        {
            velocity.y -= 40f * delta;
        }
        else
        {
            velocity.y -= 0;
        }
        if ((onLeftWall || onRightWall) && velocity.y < 0)
        {
            isSliding = true;
            float slideSpeed = -0.5f;
            if (velocity.y < slideSpeed) {
                velocity.y = slideSpeed;
            }
        }
        else
        {
            isSliding = false;
        }
    }

    private void Inputhandler() {

        direction = 0f;

        if(wallJumpLockTimer <= 0)
        {
            if (Gdx.input.isKeyPressed(Input.Keys.D)) {
                direction = 1f;
                dash_dir = 1f;
                if (!is_jumping || !is_dashing) {
                    Movestate = movestate.RUNNING;
                }
            }
            if (Gdx.input.isKeyPressed(Input.Keys.A)) {
                direction = -1f;
                dash_dir = -1f;
                if (!is_jumping || !is_dashing) {
                    Movestate = movestate.RUNNING;
                }
            }
        }

        if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) && !is_dashing) {
            velocity.x = DASH_POWER * dash_dir;
            Movestate = movestate.DASHING;
            is_dashing = true;
        }
        if (direction == 0 && !is_jumping && !is_dashing) {
            Movestate = movestate.IDLE;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            if(is_Grounded)
            {
                velocity.y = JUMP_POWER;
                is_jumping = true;
                is_Grounded = false;
                Movestate = movestate.JUMPING;
            }
            else
            {
                float pushForceX = 7.0f;
                if (onLeftWall) {
                    velocity.y = JUMP_POWER;
                    velocity.x = pushForceX;
                    onLeftWall = false;
                    is_jumping = true;
                    is_Grounded = false;
                    wallJumpLockTimer = 0.2f;
                }
                else if (onRightWall) {
                    velocity.y = JUMP_POWER;
                    velocity.x = -pushForceX;
                    onRightWall = false;
                    is_jumping = true;
                    is_Grounded = false;
                    wallJumpLockTimer = 0.2f;
                }
            }


        }
    }

    public void attackEnemies(List<Enemy> enemies) {
        if (!Gdx.input.isKeyJustPressed(Input.Buttons.LEFT)) {
            return;
        }

        if (attackTimer < ATTACK_COOLDOWN) {
            return;
        }

        attackTimer = 0f;

        Enemy closestEnemy = null;
        float closestDistance = Float.MAX_VALUE;

        for (Enemy enemy : enemies) {
            if (enemy == null || enemy.isDead()) {
                continue;
            }

            float distance = enemy.getPosition().dst(position);
            if (distance <= ATTACK_RANGE && distance < closestDistance) {
                closestEnemy = enemy;
                closestDistance = distance;
            }
        }

        if (closestEnemy != null) {
            closestEnemy.damage(ATTACK_DAMAGE);
        }
    }



    public void damage(int amount)
    {
        health -= amount;
        if(health < 0)
        {
            health = 0;
        }
    }



    public void respawn() {
        position.set(spawnPosition);
        velocity.set(0, 0);
        health = MAX_HEALTH;
        is_jumping = false;
        is_dashing = false;
        dashTimer = 0f;
        attackTimer = 0f;
        direction = 1f;
        dash_dir = 1f;
        Movestate = movestate.IDLE;

        player_sprite.setPosition(position.x, position.y);
        player_rect.set(player_sprite.getX(), player_sprite.getY(), player_sprite.getWidth(), player_sprite.getHeight());
    }

    public void heal(int amount)
    {
        health += amount;
        if(health > MAX_HEALTH)
        {
            health = MAX_HEALTH;
        }
    }



    public Rectangle getPlayer_rect() {
        return player_rect;
    }

    public Sprite getPlayer_sprite() {
        return player_sprite;
    }

    public void setSpawnPosition(float x, float y) {
        this.spawnPosition.set(x, y);
        this.position.set(x, y);
        this.velocity.set(0, 0);

    }

    public void setPosition (float x, float y)
    {
        this.position.set(x, y);
    }

    public float getPositionX() {
        return position.x;
    }

    public float getPositionY() {
        return position.y;
    }

    public float getVelocityX() {
        return velocity.x;
    }
    public float getVelocityY() {
        return velocity.y;
    }

    public Vector2 setVelocity(float x, float y) {
        return velocity.set(x,y);
    }

    public void setVelocityX(float x)
    {
        this.velocity.x = x;
    }

    public void setVelocityY(float y)
    {
        this.velocity.y = y;
    }


    public void setIs_Grounded(boolean is_Grounded) {
        this.is_Grounded = is_Grounded;
    }
    public boolean Is_Grounded()
    {
        return is_Grounded;
    }

    public Vector2 getPosition() {
        return position;
    }

    public boolean isDead()
    {
        return health <= 0;
    }

    public int getHealth()
    {
        return health;
    }

    public int getMaxHealth()
    {
        return MAX_HEALTH;
    }

    public boolean isOnLeftWall() {
        return onLeftWall;
    }

    public void setOnLeftWall(boolean onLeftWall) {
        this.onLeftWall = onLeftWall;
    }

    public boolean isOnRightWall() {
        return onRightWall;
    }

    public void setOnRightWall(boolean onRightWall) {
        this.onRightWall = onRightWall;
    }

    public boolean isSliding() {
        return isSliding;
    }

    public void setSliding(boolean sliding) {
        isSliding = sliding;
    }

    public void setWallJumpLockTimer(float wallJumpLockCd)
    {
        this.wallJumpLockTimer = wallJumpLockCd;
    }



}


