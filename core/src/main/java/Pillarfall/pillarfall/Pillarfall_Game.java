package Pillarfall.pillarfall;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.ExtendViewport;



/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Pillarfall_Game extends Game {

    private World world;
    private final Assets assets = new Assets();

    private SpriteBatch batch;
    private ExtendViewport viewport;

    @Override
    public void create() {

        batch = new SpriteBatch();
        //Units der Welt die angezeigt wird --> 1 Unit ist 32px
        viewport = new ExtendViewport(16, 8);

        //Direkt im Loading Screen ist nur temporär normalerweise ein Menu
        setScreen(new LoadingScreen(this, assets));
    }

    public void startGame() {
        world = new World(new Player(
            150, 9, 12, 40,
            assets.manager.get(Assets.playerTexture)
        ), this ,assets.manager.get(Assets.map));
        // Wechseln des Screens
        setScreen(new GameScreen(this, assets));


    }

    public World getWorld() { return world; }
    public Assets getAssets() { return assets; }
    public SpriteBatch getBatch() { return batch; }
    public ExtendViewport getViewport() { return viewport; }
}
