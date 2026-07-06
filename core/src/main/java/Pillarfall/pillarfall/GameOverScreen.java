package Pillarfall.pillarfall;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

//Death-Screen: erscheint sobald der Spieler keine Herzen mehr hat
public class GameOverScreen implements Screen {

    private static final float BUTTON_WIDTH = 380f;
    private static final float BUTTON_HEIGHT = 60f;

    //Pixel-Art Totenkopf, '#' = Knochenfarbe, '.' = transparent
    private static final String[] SKULL_PATTERN = {
        "..########..",
        ".##########.",
        "############",
        "############",
        "##..####..##",
        "##..####..##",
        "############",
        "#####..#####",
        "############",
        ".##########.",
        ".#.#.##.#.#.",
        ".#.#.##.#.#."
    };

    private final Stage stage;
    private final MenuSkin skin;
    private final Texture skullTexture;

    public GameOverScreen(final Pillarfall_Game game, String deathCause) {
        this.skin = new MenuSkin();
        this.stage = new Stage(new ScreenViewport(), game.getBatch());
        this.skullTexture = createSkullTexture();

        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        Image skull = new Image(skullTexture);

        Label title = skin.createTitle("Du bist gestorben!");
        Label message = skin.createText("Du bist gestorben wegen " + deathCause + ".", new Color(1f, 0.8f, 0.8f, 1f));

        TextButton restartButton = skin.createButton("Restarten");
        TextButton menuButton = skin.createButton("Hauptmenü");
        TextButton quitButton = skin.createButton("Verlassen");

        restartButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.startGame();
            }
        });

        menuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.showMainMenu();
            }
        });

        quitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        table.add(skull).size(144f, 144f).padBottom(25f).row();
        table.add(title).padBottom(15f).row();
        table.add(message).padBottom(40f).row();
        table.add(restartButton).width(BUTTON_WIDTH).height(BUTTON_HEIGHT).padBottom(15f).row();
        table.add(menuButton).width(BUTTON_WIDTH).height(BUTTON_HEIGHT).padBottom(15f).row();
        table.add(quitButton).width(BUTTON_WIDTH).height(BUTTON_HEIGHT);
    }

    private Texture createSkullTexture() {
        int height = SKULL_PATTERN.length;
        int width = SKULL_PATTERN[0].length();

        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        pixmap.setColor(0.95f, 0.95f, 0.9f, 1f);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (SKULL_PATTERN[y].charAt(x) == '#') {
                    pixmap.drawPixel(x, y);
                }
            }
        }

        Texture texture = new Texture(pixmap);
        //Nearest-Filter, damit der Totenkopf beim Hochskalieren pixelig-scharf bleibt
        texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        pixmap.dispose();
        return texture;
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        //Rötlich gefärbter Hintergrund
        ScreenUtils.clear(new Color(0.35f, 0.05f, 0.05f, 1f));

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
        skullTexture.dispose();
    }
}
