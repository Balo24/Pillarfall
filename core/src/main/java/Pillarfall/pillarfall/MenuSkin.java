package Pillarfall.pillarfall;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Disposable;

//Erzeugt Schriften und Buttons im Minecraft-Stil per Code, damit keine extra UI-Assets nötig sind
public class MenuSkin implements Disposable {

    public static final String FONT_BOLD = "ui/FiraSans-Bold.ttf";
    public static final String FONT_REGULAR = "ui/FiraSans-Regular.ttf";

    private final BitmapFont titleFont;
    private final BitmapFont buttonFont;
    private final BitmapFont textFont;

    private final Texture buttonUpTexture;
    private final Texture buttonOverTexture;
    private final Texture buttonDownTexture;

    private final TextButton.TextButtonStyle buttonStyle;

    public MenuSkin() {
        //Statt die kleine Standard-Schrift hochzuskalieren, wird die Schrift
        //per FreeType direkt in der richtigen Größe gerendert → scharfe Kanten
        titleFont = generateFont(FONT_BOLD, 56);
        buttonFont = generateFont(FONT_BOLD, 28);
        textFont = generateFont(FONT_REGULAR, 20);

        buttonUpTexture = createButtonTexture(new Color(0.42f, 0.42f, 0.42f, 1f));
        buttonOverTexture = createButtonTexture(new Color(0.36f, 0.42f, 0.62f, 1f));
        buttonDownTexture = createButtonTexture(new Color(0.26f, 0.26f, 0.26f, 1f));

        buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = buttonFont;
        buttonStyle.fontColor = Color.WHITE;
        buttonStyle.overFontColor = new Color(1f, 1f, 0.65f, 1f);
        buttonStyle.up = new NinePatchDrawable(new NinePatch(buttonUpTexture, 3, 3, 3, 3));
        buttonStyle.over = new NinePatchDrawable(new NinePatch(buttonOverTexture, 3, 3, 3, 3));
        buttonStyle.down = new NinePatchDrawable(new NinePatch(buttonDownTexture, 3, 3, 3, 3));
    }

    //Erzeugt eine scharfe Schrift in der gewünschten Pixelgröße; der Aufrufer muss sie disposen
    public static BitmapFont generateFont(String fontPath, int size) {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal(fontPath));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = size;
        parameter.minFilter = Texture.TextureFilter.Linear;
        parameter.magFilter = Texture.TextureFilter.Linear;
        BitmapFont font = generator.generateFont(parameter);
        generator.dispose();
        return font;
    }

    //Grauer Button mit dunklem Rand, heller Ober- und dunkler Unterkante (3D-Look wie in Minecraft)
    private Texture createButtonTexture(Color base) {
        Pixmap pixmap = new Pixmap(16, 16, Pixmap.Format.RGBA8888);

        pixmap.setColor(0f, 0f, 0f, 1f);
        pixmap.fill();

        pixmap.setColor(base);
        pixmap.fillRectangle(1, 1, 14, 14);

        pixmap.setColor(Math.min(1f, base.r * 1.4f), Math.min(1f, base.g * 1.4f), Math.min(1f, base.b * 1.4f), 1f);
        pixmap.fillRectangle(1, 1, 14, 2);

        pixmap.setColor(base.r * 0.6f, base.g * 0.6f, base.b * 0.6f, 1f);
        pixmap.fillRectangle(1, 13, 14, 2);

        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }

    public TextButton createButton(String text) {
        return new TextButton(text, buttonStyle);
    }

    public Label createTitle(String text) {
        return new Label(text, new Label.LabelStyle(titleFont, Color.WHITE));
    }

    public Label createText(String text, Color color) {
        return new Label(text, new Label.LabelStyle(textFont, color));
    }

    @Override
    public void dispose() {
        titleFont.dispose();
        buttonFont.dispose();
        textFont.dispose();
        buttonUpTexture.dispose();
        buttonOverTexture.dispose();
        buttonDownTexture.dispose();
    }
}
