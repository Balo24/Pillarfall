package Pillarfall.pillarfall;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.ExtendViewport;



/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Pillarfall_Game extends Game {

    //Der Spieler hat 100 HP → 3 Herzen mit je 33 HP
    private static final int PLAYER_MAX_HEALTH = 100;

    private World world;
    private final Assets assets = new Assets();

    private SpriteBatch batch;
    private ExtendViewport viewport;

    @Override
    public void create() {

        batch = new SpriteBatch();
        //Units der Welt die angezeigt wird --> 1 Unit ist 32px
        viewport = new ExtendViewport(16, 8);

        //Erst laden, danach geht es ins Hauptmenü
        setScreen(new LoadingScreen(this, assets));
    }

    public void startGame() {
        world = new World(new Player(
            PLAYER_MAX_HEALTH, 9, 12, 40,
            assets.manager.get(Assets.playerTexture)
        ), this ,assets.manager.get(Assets.map));
        // Wechseln des Screens
        changeScreen(new GameScreen(this, assets));
    }

    public void showMainMenu() {
        changeScreen(new MainMenuScreen(this));
    }

    public void showDeathScreen(String deathCause) {
        changeScreen(new GameOverScreen(this, deathCause));
    }

    //Wechselt den Screen und räumt den alten sauber auf
    private void changeScreen(Screen next) {
        Screen old = getScreen();
        setScreen(next);
        if (old != null) {
            old.dispose();
        }
    }

    @Override
    public void dispose() {
        if (getScreen() != null) {
            getScreen().dispose();
        }
        batch.dispose();
        assets.dispose();
    }

    public World getWorld() { return world; }
    public Assets getAssets() { return assets; }
    public SpriteBatch getBatch() { return batch; }
    public ExtendViewport getViewport() { return viewport; }
}
