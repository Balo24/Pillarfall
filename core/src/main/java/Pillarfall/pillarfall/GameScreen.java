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

    GameScreen(Pillarfall_Game game, Assets assets)
    {
        this.game = game;
        this.assets = assets;


        camera = (OrthographicCamera) game.getViewport().getCamera();
        player = game.getWorld().getPlayer();

        player.setPosition(0,10);

    }

    @Override
    public void show() {

    }



    @Override
    public void render(float delta) {

        player.update();


        //Kamera
        float lerpx = 8f;
        float lerpy = 5f;
        Vector3 position = this.camera.position;
        position.x += (player.getPositionX() - position.x + 2) * lerpx * delta;
        position.y += (player.getPositionY() - position.y + 2) * lerpy * delta;
        camera.position.set(position);
        camera.update();

        //Rendering
        game.getViewport().apply();

        ScreenUtils.clear(Color.WHITE);
        //Die Map
        game.getWorld().mapRenderer.setView(camera);
        game.getWorld().mapRenderer.render();

        game.getBatch().setProjectionMatrix(camera.combined);
        //Alle geladene Sprites
        game.getBatch().begin();
        player.getPlayer_sprite().draw(game.getBatch());
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
}
