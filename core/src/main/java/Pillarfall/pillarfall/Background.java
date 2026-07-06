package Pillarfall.pillarfall;


import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;

public class Background {

    private Array<BackgroundLayer> layers = new Array<>();

    public void addLayer(BackgroundLayer layer) {
        layers.add(layer);
    }

    public void render(SpriteBatch batch, OrthographicCamera camera) {
        for (BackgroundLayer layer : layers) {
            layer.render(batch, camera);
        }
    }
}
