package Pillarfall.pillarfall;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class BackgroundLayer {

    private final Texture texture;
    private final float parallaxFactor;

    private final float worldWidth;
    private final float worldHeight;

    public BackgroundLayer(Texture texture, float parallaxFactor) {
        this.texture = texture;
        this.parallaxFactor = parallaxFactor;

        this.worldWidth = texture.getWidth() / 32f;
        this.worldHeight = texture.getHeight() / 32f;
    }

    public void render(SpriteBatch batch, OrthographicCamera camera) {

        float camX = camera.position.x - camera.viewportWidth / 2f;
        float camY = camera.position.y - camera.viewportHeight / 2f;

        float startX = 0;
        float x = startX + camX * parallaxFactor;
        float startY = 0;
        float y = startY + camY * parallaxFactor;

        batch.draw(texture, x, y, worldWidth, worldHeight);
    }
}
