package Pillarfall.pillarfall;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

//Hauptmenü im Minecraft-Stil: Titel + Spielen / Einstellungen / Verlassen
public class MainMenuScreen implements Screen {

    private static final float BUTTON_WIDTH = 380f;
    private static final float BUTTON_HEIGHT = 60f;

    private final Stage stage;
    private final MenuSkin skin;
    private final Label settingsHint;

    public MainMenuScreen(final Pillarfall_Game game) {
        this.skin = new MenuSkin();
        this.stage = new Stage(new ScreenViewport(), game.getBatch());

        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        Label title = skin.createTitle("PILLARFALL");

        TextButton playButton = skin.createButton("Spielen");
        TextButton settingsButton = skin.createButton("Einstellungen");
        TextButton quitButton = skin.createButton("Verlassen");

        //Platzhalter-Hinweis, solange es noch keine Einstellungen gibt
        settingsHint = skin.createText("Einstellungen sind noch nicht verfügbar.", Color.LIGHT_GRAY);
        settingsHint.setVisible(false);

        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.startGame();
            }
        });

        settingsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                settingsHint.setVisible(true);
            }
        });

        quitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        table.add(title).padBottom(60f).row();
        table.add(playButton).width(BUTTON_WIDTH).height(BUTTON_HEIGHT).padBottom(15f).row();
        table.add(settingsButton).width(BUTTON_WIDTH).height(BUTTON_HEIGHT).padBottom(15f).row();
        table.add(quitButton).width(BUTTON_WIDTH).height(BUTTON_HEIGHT).padBottom(25f).row();
        table.add(settingsHint);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        //Dunkler, erdiger Hintergrund wie das Minecraft-Dirt-Menü
        ScreenUtils.clear(new Color(0.16f, 0.11f, 0.07f, 1f));

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        if (width <= 0 || height <= 0) return;
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }
}
