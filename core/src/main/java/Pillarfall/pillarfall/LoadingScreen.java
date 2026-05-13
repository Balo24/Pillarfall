package Pillarfall.pillarfall;


import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.ScreenUtils;

//Zwischendurch um zu garantieren, dass alles geladen ist
public class LoadingScreen implements Screen {

    private final Pillarfall_Game game;
    private final Assets assets;

    public LoadingScreen(Pillarfall_Game game, Assets assets) {
        assets.load();
        this.game = game;
        this.assets = assets;
    }

    @Override
    public void render(float delta) {
        //Wenn True dann ist alles geladen
        if (assets.manager.update()) {
            game.startGame();
        }
        //Hintergrund
        ScreenUtils.clear(Color.BLACK);
    }

    @Override public void show() {}
    @Override public void resize(int w, int h) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() {}
}
