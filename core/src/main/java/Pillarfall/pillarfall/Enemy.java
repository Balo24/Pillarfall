package Pillarfall.pillarfall;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Enemy {

    private enum MovementState {
        IDLE, RUNNING, JUMPING
    }

    private enum AIState {
        PATROL, CHASE, ATTACK
    }

    private MovementState movementState = MovementState.IDLE;
    private AIState aiState = AIState.PATROL;

    private final Vector2 velocity = new Vector2();
    private final Vector2 position;

    private final int MAX_HEALTH;
    private int health;

    private final int SPEED;
    private final int JUMP_POWER;
    private final int ATTACK_DAMAGE;
    private final float DETECTION_RANGE;
    private final float ATTACK_RANGE;
    private final float ATTACK_COOLDOWN;

    private final Sprite enemySprite;
    private final Sprite normalSprite;
    private final Sprite deathSprite;
    private final Rectangle bounds;
    private final Texture normalTexture;
    private final Texture deathTexture;

    private final float patrolCenterX;
    private final float patrolDistance;
    private final float groundY;

    private TiledMapTileLayer collisionLayer;
    private float direction = 1f;
    private float attackTimer = 0f;
    private boolean isJumping = false;

    public Enemy(int x, int y, int maxHealth, int speed, int jumpPower, Texture texture, Texture deathTexture)
    {
        this(x, y, maxHealth, speed, jumpPower, 10, 1f, 0.8f, 0.5f, texture, deathTexture);
    }

    public Enemy(int x, int y, int maxHealth, int speed, int jumpPower, int attackDamage, float detectionRange, float attackRange, float attackCooldown, Texture texture, Texture deathTexture)
    {
        this.position = new Vector2(x, y);
        this.patrolCenterX = x;
        this.patrolDistance = 4f;
        this.groundY = y;

        this.normalTexture = texture;
        this.deathTexture = deathTexture;

        this.MAX_HEALTH = maxHealth;
        this.health = maxHealth;
        this.SPEED = speed;
        this.JUMP_POWER = jumpPower;
        this.ATTACK_DAMAGE = attackDamage;
        this.DETECTION_RANGE = detectionRange;
        this.ATTACK_RANGE = attackRange;
        this.ATTACK_COOLDOWN = attackCooldown;


        this.normalSprite = new Sprite(texture);
        this.normalSprite.setOriginCenter();
        this.normalSprite.setSize(1f, 1f);

        this.deathSprite = new Sprite(deathTexture);
        this.deathSprite.setOriginCenter();
        this.deathSprite.setSize(10f, 10f);

        this.enemySprite = new Sprite(normalSprite);
        this.enemySprite.setPosition(position.x, position.y);

        this.bounds = new Rectangle(
            enemySprite.getX(),
            enemySprite.getY(),
            enemySprite.getWidth(),
            enemySprite.getHeight()
        );
    }

//    public void setCollisionLayer(TiledMapTileLayer collisionLayer)
//    {
//        this.collisionLayer = collisionLayer;
//    }

    public void update(Player player, float delta)
    {
        if (player == null)
            {
            return;
        }

        attackTimer += delta;

        if (isDead())
            {
            enemySprite.set(deathSprite);
            enemySprite.setPosition(position.x, position.y);
            enemySprite.setOriginCenter();
            enemySprite.setSize(1f, 1f);
            movementState = MovementState.IDLE;
            velocity.x = 0;
            enemySprite.setRotation(0f);
            enemySprite.setColor(1f, 1f, 1f, 1f);
            updateBounds();
            return;
        }

        float distanceToPlayer = position.dst(player.getPosition());
        boolean canSeePlayer = canSeePlayer(player.getPosition());

        if (distanceToPlayer <= ATTACK_RANGE && canSeePlayer)
            {
            aiState = AIState.ATTACK;
            handleAttack(player);
        } else if (distanceToPlayer <= DETECTION_RANGE && canSeePlayer)
            {
            aiState = AIState.CHASE;
            chasePlayer(player);
        } else {
            aiState = AIState.PATROL;
            patrol();
        }

        applyMovement(delta);
        updateBounds();
    }

    private void patrol()
    {
        if (position.x >= patrolCenterX + patrolDistance)
            {
            direction = -1f;
        } else if (position.x <= patrolCenterX - patrolDistance)
            {
            direction = 1f;
        }

        movementState = direction == 0f ? MovementState.IDLE : MovementState.RUNNING;
    }

    private void chasePlayer(Player player)
    {
        direction = player.getPositionX() > position.x ? 1f : -1f;
        movementState = MovementState.RUNNING;
    }

    private void handleAttack(Player player)
    {
        direction = player.getPositionX() > position.x ? 1f : -1f;
        movementState = MovementState.RUNNING;

        if (attackTimer >= ATTACK_COOLDOWN)
            {
            attackTimer = 0f;
            player.damage(ATTACK_DAMAGE);
        }
    }

    private void applyMovement(float delta)
    {
        velocity.y -= 40f * delta;

        float targetSpeed = direction * SPEED;
        float acceleration = isJumping ? 4f : 12f;
        float difference = targetSpeed - velocity.x;
        velocity.x += acceleration * difference * delta;

        position.add(velocity.x * delta, velocity.y * delta);

        if (position.y <= groundY)
             {
            position.y = groundY;
            velocity.y = 0f;
            isJumping = false;
        }

        enemySprite.setPosition(position.x, position.y);

        if (Math.abs(velocity.x) > 0.01f)
             {
            movementState = MovementState.RUNNING;
        } else {
            movementState = MovementState.IDLE;
        }
    }

    private boolean canSeePlayer(Vector2 playerPosition)
    {
        if (collisionLayer == null)
             {
            return true;
        }

        Vector2 lineOfSight = playerPosition.cpy().sub(position);
        float distance = lineOfSight.len();

        if (distance == 0f)
            {
            return true;
        }

        lineOfSight.nor();

        Vector2 probe = new Vector2(position.x, position.y);
        float stepSize = 0.25f;

        for (float travelled = 0f; travelled < distance; travelled += stepSize)
            {
            probe.add(lineOfSight.x * stepSize, lineOfSight.y * stepSize);

            int tileX = (int) Math.floor(probe.x);
            int tileY = (int) Math.floor(probe.y);

            if (tileX < 0 || tileY < 0 || tileX >= collisionLayer.getWidth() || tileY >= collisionLayer.getHeight())
                 {
                continue;
            }

            if (collisionLayer.getCell(tileX, tileY) != null)
                 {
                return false;
            }
        }

        return true;
    }

    private void updateBounds()
    {
        enemySprite.setPosition(position.x, position.y);
        bounds.set(enemySprite.getX(), enemySprite.getY(), enemySprite.getWidth(), enemySprite.getHeight());
    }

    public void damage(int amount)
    {
        health -= amount;
        if (health < 0)
            {
            health = 0;
        }
    }

    public void heal(int amount)
    {
        health += amount;
        if (health > MAX_HEALTH)
            {
            health = MAX_HEALTH;
        }
        if (health > 0)
            {
            enemySprite.set(normalSprite);
            enemySprite.setPosition(position.x, position.y);
            enemySprite.setOriginCenter();
            enemySprite.setSize(1f, 1f);
            enemySprite.setColor(1f, 1f, 1f, 1f);
            enemySprite.setRotation(0f);
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

    public Sprite getEnemy_sprite()
     {
        return enemySprite;
    }

    public Rectangle getBounds()
    {
        return bounds;
    }

    public Vector2 getPosition()
    {
        return position;
    }
}



