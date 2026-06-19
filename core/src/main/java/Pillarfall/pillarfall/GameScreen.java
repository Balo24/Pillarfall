package Pillarfall.pillarfall;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Matrix4;



import com.badlogic.gdx.utils.ScreenUtils;

public class GameScreen implements Screen {

    private final Pillarfall_Game game;
    private final Assets assets;
    private final OrthographicCamera camera;
    private final Player player;
    private final World world;
    private final Background background;
    private final BitmapFont deathFont = new BitmapFont();

    private static final int HEART_COUNT = 3;
    private static final int HEART_SIZE = 64;
    private static final int HEART_SPACING = 5;
    private static final int HEART_MARGIN = 5;

    GameScreen(Pillarfall_Game game, Assets assets)
    {
        this.game = game;
        this.assets = assets;


        camera = (OrthographicCamera) game.getViewport().getCamera();

        player = game.getWorld().getPlayer();
        world = game.getWorld();
        player.setSpawnPosition(5,5);
        background = new Background();

        background.addLayer(new BackgroundLayer(
            assets.manager.get(Assets.bgLayer0),
            1f

        ));

        background.addLayer(new BackgroundLayer(
            assets.manager.get(Assets.bgLayer1),
            0.1f

        ));
        background.addLayer(new BackgroundLayer(
            assets.manager.get(Assets.bgLayer2),
            0.3f

        ));
        background.addLayer(new BackgroundLayer(
            assets.manager.get(Assets.bgLayer3),
            0.6f

        ));
        background.addLayer(new BackgroundLayer(
            assets.manager.get(Assets.bgLayer4),
            0.8f

        ));


    }

    @Override
    public void show() {

    }



    @Override
    public void render(float delta) {

        world.update(delta);

        ScreenUtils.clear(Color.WHITE);

        float lerpx = 8f;
        float lerpy = 5f;

        camera.position.x += (player.getPositionX() - camera.position.x + 0.5f) * lerpx * delta;
        camera.position.y += (player.getPositionY() - camera.position.y + 2f) * lerpy * delta;
        camera.update();

        game.getViewport().apply();


        game.getBatch().setProjectionMatrix(camera.combined);
        game.getBatch().begin();

        background.render(game.getBatch(), camera);
        player.getPlayer_sprite().draw(game.getBatch());

        for (Enemy enemy : world.getEnemies()) {
            enemy.getEnemy_sprite().draw(game.getBatch());
        }

        game.getBatch().end();

        world.mapRenderer.setView(camera);
        world.mapRenderer.render();

        game.getBatch().setProjectionMatrix(new Matrix4().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
        game.getBatch().begin();
        renderHealthBar(game.getBatch());

        if (world.shouldShowDeathMessage()) {
            deathFont.setColor(Color.RED);
            deathFont.draw(game.getBatch(), world.getDeathMessage(), 20, Gdx.graphics.getHeight() - 20);
        }

        game.getBatch().end();

    }

    @Override
    public void resize(int width, int height) {
        // If the window is minimized on a desktop (LWJGL3) platform, width and height are 0, which causes problems.
        // In that case, we don't resize anything, and wait for the window to be a normal size before updating.
        if(width <= 0 || height <= 0) return;

        // Resize your screen here. The parameters represent the new window size.
        game.getViewport().update(width, height, false);

    }

    @Override
    public void pause() {
        // Invoked when your application is paused.
    }

    @Override
    public void resume() {
        // Invoked when your application is resumed after pause.
    }

    @Override
    public void hide() {
        // This method is called when another screen replaces this one.
    }

    @Override
    public void dispose() {
        //Alles gelöscht am Ende
        assets.dispose();
    }

    private void renderHealthBar(com.badlogic.gdx.graphics.g2d.SpriteBatch batch) {
        int screenWidth = Gdx.graphics.getWidth();
        int screenHeight = Gdx.graphics.getHeight();
        int startX = screenWidth - HEART_COUNT * HEART_SIZE - (HEART_COUNT - 1) * HEART_SPACING;
        int startY = screenHeight - HEART_MARGIN - HEART_SIZE;

        int health = player.getHealth();
        int maxHealth = player.getMaxHealth();
        Texture fullHeart = assets.manager.get(Assets.fullHeart);
        Texture emptyHeart = assets.manager.get(Assets.emptyHeart);

        for (int i = 0; i < HEART_COUNT; i++) {
            int threshold = (i + 1) * 50;
            Texture currentHeart = health >= threshold ? fullHeart : emptyHeart;
            batch.draw(currentHeart, startX + i * (HEART_SPACING + HEART_SIZE), startY, HEART_SIZE, HEART_SIZE);
        }
    }
}
