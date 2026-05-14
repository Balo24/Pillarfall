package Pillarfall.pillarfall;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;


import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;

/** First screen of the application. Displayed after the application is created. */
public class GameScreen implements Screen {

    private final Pillarfall_Game game;
    private final Assets assets;
    private final OrthographicCamera camera;
    private final Player player;
    private final World world;
    private final Background background;




    GameScreen(Pillarfall_Game game, Assets assets)
    {
        this.game = game;
        this.assets = assets;


        camera = (OrthographicCamera) game.getViewport().getCamera();

        player = game.getWorld().getPlayer();
        world = game.getWorld();
        player.setPosition(3,3);
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

        player.update();

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

        game.getBatch().end();

        world.mapRenderer.setView(camera);
        world.mapRenderer.render();


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
}
