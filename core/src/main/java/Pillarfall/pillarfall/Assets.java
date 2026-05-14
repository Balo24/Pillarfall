package Pillarfall.pillarfall;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.tiled.AtlasTmxMapLoader;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;


//Zum Laden aller Sprites, Maps, Sounds, Music, etc.
// Lese die Dokumentation der AssetManager Klasse für mehr Informationen
public class Assets {
    public AssetManager manager = new AssetManager();

    //Die Assets die geladen werden sollen mit dem Pfad
    public static final AssetDescriptor<Texture> playerTexture =
        new AssetDescriptor<Texture>("player/Peter.png", Texture.class);
    public static final AssetDescriptor<TiledMap> map =
        new AssetDescriptor<TiledMap>("world/output/Simple_Map.tmx", TiledMap.class );


    Assets()
    {
        manager.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));
    }

    public void load()
    {
        //Hier geladen
        manager.load(playerTexture);
        manager.load(map);

    }

    public void dispose()
    {
        //Hier wird alles gelöscht
        manager.dispose();
    }
}
