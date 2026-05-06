package Pillarfall.pillarfall;

import com.badlogic.gdx.Game;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Pillarfall_Game extends Game {
    @Override
    public void create() {
        setScreen(new GameScreen());
    }
}
