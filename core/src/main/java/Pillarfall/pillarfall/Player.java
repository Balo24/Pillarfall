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
    private final int ATTACK_DAMAGE = 10;
    private final float ATTACK_RANGE = 1.3f;
    private final float ATTACK_COOLDOWN = 0.5f;

    private boolean is_jumping = false;
    private float direction = 1;
    private float dash_dir;
    private float dash_cd;
    private float attackTimer = 0f;
    private boolean is_dashing = false;

    private final Vector2 velocity;
    private final Vector2 position = new Vector2(0,0);
    private final Vector2 spawnPosition = new Vector2(0,0);

    private final Sprite player_sprite;
    private final Rectangle player_rect;

    private final String deathMessage = "Du wurdest getötet! Starte neu.";
    private final float DEATH_MESSAGE_DURATION = 5.0f;
    private float deathMessageTimer = 0f;

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
        player_sprite.setSize(1,1);

        this.player_rect = new Rectangle(player_sprite.getX(), player_sprite.getY(),player_sprite.getWidth(), player_sprite.getHeight());
    }

    public void update()
    {
        float delta = Gdx.graphics.getDeltaTime();

        attackTimer += delta;

        if (deathMessageTimer > 0f) {
            deathMessageTimer -= delta;
            if (deathMessageTimer < 0f) {
                deathMessageTimer = 0f;
            }
        }

        if(position.y <= 3)
        {
            position.y = 3;
            velocity.y = 0;
            is_jumping = false;
        }
        Inputhandler();
        move();

        player_sprite.setPosition(position.x,position.y);
        player_rect.set(player_sprite.getX(), player_sprite.getY(),player_sprite.getWidth(), player_sprite.getHeight());

        if(is_dashing)
        {
            dash_cd += delta;

        }

        if(dash_cd >= 2f)
        {
            is_dashing = false;
            dash_cd = 0;
        }
    }

    private void move()
    {
        float delta = Gdx.graphics.getDeltaTime();

        velocity.y -= 40f * delta;

        float targetSpeed = direction * SPEED;
        float acceleration = is_jumping || is_dashing ? 4f : 12f;
        float differenz = targetSpeed - velocity.x;
        velocity.x += acceleration * differenz * delta;

        position.add(velocity.x * delta, velocity.y * delta);
    }

    private void Inputhandler() {

        direction = 0f;

        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            direction = 1f;
            dash_dir = 1f;
            if(!is_jumping|| !is_dashing)
            {
                Movestate = movestate.RUNNING;
            }

        }

        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            direction = -1f;
            dash_dir = -1f;
            if(!is_jumping || !is_dashing)
            {
                Movestate = movestate.RUNNING;
            }

        }
        if(Gdx.input.isKeyPressed(Input.Keys.E) && !is_dashing)
        {
            velocity.x = DASH_POWER * dash_dir;
            Movestate = movestate.DASHING;
            is_dashing = true;
        }
        if (direction == 0 && !is_jumping && !is_dashing ) {
            Movestate = movestate.IDLE;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && !is_jumping) {
            velocity.y = JUMP_POWER;
            is_jumping = true;
            Movestate = movestate.JUMPING;
        }
    }

    public void attackEnemies(List<Enemy> enemies) {
        if (!Gdx.input.isKeyJustPressed(Input.Keys.F)) {
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

    public Rectangle getBounds() {
        return player_rect;
    }

    public Sprite getPlayer_sprite() {
        return player_sprite;
    }

    public void setPosition(float x, float y) {
        this.spawnPosition.set(x, y);
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

    public void damage(int amount)
    {
        health -= amount;
        if(health < 0)
        {
            health = 0;
            onDeath();
        }
    }

    private void onDeath() {
        deathMessageTimer = DEATH_MESSAGE_DURATION;
        respawn();
    }

    public void respawn() {
        position.set(spawnPosition);
        velocity.set(0, 0);
        health = MAX_HEALTH;
        is_jumping = false;
        is_dashing = false;
        dash_cd = 0f;
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

    public boolean shouldShowDeathMessage() {
        return deathMessageTimer > 0f;
    }

    public String getDeathMessage() {
        return deathMessage;
    }
}


