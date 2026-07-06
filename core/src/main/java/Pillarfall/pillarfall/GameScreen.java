package Pillarfall.pillarfall;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class GameScreen implements Screen {

    private final Pillarfall_Game game;
    private final Assets assets;
    private final OrthographicCamera camera;
    private final Player player;
    private final World world;
    private final Background background;
    private final TextureRegion halfHeartRegion;

    //Pausenmenü: wird über das eingefrorene Spiel gelegt
    private final MenuSkin menuSkin;
    private final Stage pauseStage;
    private final Texture pauseOverlayTexture;
    private boolean paused = false;

    //3 Herzen à 33 HP → 100 HP gesamt, halbe Herzen als Zwischenstufe
    private static final int HEART_COUNT = 3;
    private static final int HP_PER_HEART = 33;
    private static final int HEART_SIZE = 64;
    private static final int HEART_SPACING = 5;
    private static final int HEART_MARGIN = 5;

    GameScreen(Pillarfall_Game game, Assets assets)
    {
        this.game = game;
        this.assets = assets;

        //Linke Hälfte des vollen Herzens, wird über ein leeres Herz gelegt
        Texture fullHeartTexture = assets.manager.get(Assets.fullHeart);
        halfHeartRegion = new TextureRegion(fullHeartTexture, 0, 0, fullHeartTexture.getWidth() / 2, fullHeartTexture.getHeight());


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

        //Halbtransparentes Schwarz, um das Spiel im Pausenmenü abzudunkeln
        Pixmap overlayPixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        overlayPixmap.setColor(0f, 0f, 0f, 0.55f);
        overlayPixmap.fill();
        pauseOverlayTexture = new Texture(overlayPixmap);
        overlayPixmap.dispose();

        menuSkin = new MenuSkin();
        pauseStage = new Stage(new ScreenViewport(), game.getBatch());

        Table pauseTable = new Table();
        pauseTable.setFillParent(true);
        pauseStage.addActor(pauseTable);

        Label pauseTitle = menuSkin.createTitle("Pause");
        TextButton resumeButton = menuSkin.createButton("Weiterspielen");
        TextButton menuButton = menuSkin.createButton("Zurück ins Hauptmenü");

        resumeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                setPaused(false);
            }
        });

        menuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                GameScreen.this.game.showMainMenu();
            }
        });

        pauseTable.add(pauseTitle).padBottom(50f).row();
        pauseTable.add(resumeButton).width(380f).height(60f).padBottom(15f).row();
        pauseTable.add(menuButton).width(380f).height(60f);
    }

    private void setPaused(boolean value) {
        paused = value;
        //Im Pausenmenü übernimmt die Stage die Maus-Eingaben
        Gdx.input.setInputProcessor(paused ? pauseStage : null);
    }

    @Override
    public void show() {

    }



    @Override
    public void render(float delta) {

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            setPaused(!paused);
        }

        //Solange das Pausenmenü offen ist, wird die Welt nicht weiter simuliert
        if (!paused) {
            world.update(delta);

            //Keine Herzen mehr → Death-Screen anzeigen
            if (player.isDead()) {
                game.showDeathScreen(player.getDeathCause());
                return;
            }
        }

        ScreenUtils.clear(Color.WHITE);

        if (!paused) {
            float lerpx = 8f;
            float lerpy = 5f;

            camera.position.x += (player.getPositionX() - camera.position.x + 0.5f) * lerpx * delta;
            camera.position.y += (player.getPositionY() - camera.position.y + 2f) * lerpy * delta;
            camera.update();
        }

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

        if (paused) {
            game.getBatch().draw(pauseOverlayTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        }

        game.getBatch().end();

        if (paused) {
            pauseStage.act(delta);
            pauseStage.draw();
        }

    }

    @Override
    public void resize(int width, int height) {
        // If the window is minimized on a desktop (LWJGL3) platform, width and height are 0, which causes problems.
        // In that case, we don't resize anything, and wait for the window to be a normal size before updating.
        if(width <= 0 || height <= 0) return;

        // Resize your screen here. The parameters represent the new window size.
        game.getViewport().update(width, height, false);
        pauseStage.getViewport().update(width, height, true);

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
        //Die Assets gehören dem AssetManager und werden zentral in Pillarfall_Game entsorgt
        menuSkin.dispose();
        pauseStage.dispose();
        pauseOverlayTexture.dispose();
    }

    private void renderHealthBar(com.badlogic.gdx.graphics.g2d.SpriteBatch batch) {
        int screenWidth = Gdx.graphics.getWidth();
        int screenHeight = Gdx.graphics.getHeight();
        int startX = screenWidth - HEART_COUNT * HEART_SIZE - (HEART_COUNT - 1) * HEART_SPACING;
        int startY = screenHeight - HEART_MARGIN - HEART_SIZE;

        int health = player.getHealth();
        Texture fullHeart = assets.manager.get(Assets.fullHeart);
        Texture emptyHeart = assets.manager.get(Assets.emptyHeart);

        //Die HP werden in halbe Herzen (je 16,5 HP) umgerechnet und aufgerundet,
        //damit auch 1 HP noch als halbes Herz sichtbar bleibt
        int halfHearts = (int) Math.ceil(health / (HP_PER_HEART / 2f));
        halfHearts = Math.min(halfHearts, HEART_COUNT * 2);

        for (int i = 0; i < HEART_COUNT; i++) {
            int x = startX + i * (HEART_SPACING + HEART_SIZE);
            int halvesInHeart = Math.max(0, Math.min(2, halfHearts - i * 2));

            if (halvesInHeart == 2) {
                batch.draw(fullHeart, x, startY, HEART_SIZE, HEART_SIZE);
            } else {
                batch.draw(emptyHeart, x, startY, HEART_SIZE, HEART_SIZE);
                if (halvesInHeart == 1) {
                    batch.draw(halfHeartRegion, x, startY, HEART_SIZE / 2f, HEART_SIZE);
                }
            }
        }
    }
}
