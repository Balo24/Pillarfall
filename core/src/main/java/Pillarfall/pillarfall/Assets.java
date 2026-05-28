package Pillarfall.pillarfall;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.tiled.AtlasTmxMapLoader;
import com.badlogic.gdx.maps.tiled.TiledMap;



//Zum Laden aller Sprites, Maps, Sounds, Music, etc.
// Lese die Dokumentation der AssetManager Klasse für mehr Informationen
public class Assets {
    public AssetManager manager = new AssetManager();

    //Die Assets die geladen werden sollen mit dem Pfad
    public static final AssetDescriptor<Texture> playerTexture =
        new AssetDescriptor<Texture>("player/Peter.png", Texture.class);

    public static final AssetDescriptor<Texture> fullHeart =
        new AssetDescriptor<Texture>("player/full_heart.png", Texture.class);
        
    public static final AssetDescriptor<Texture> emptyHeart =
        new AssetDescriptor<Texture>("player/empty_heart.png", Texture.class);
     
    public static final AssetDescriptor<Texture> enemyTexture =
        new AssetDescriptor<Texture>("enemy/Enemy.png", Texture.class);   
        
    public static final AssetDescriptor<Texture> enemyDeathTexture =
        new AssetDescriptor<Texture>("enemy/Enemydead.png", Texture.class);    

    public static final AssetDescriptor<TiledMap> map =
        new AssetDescriptor<TiledMap>("world/output/Simple_Map.tmx", TiledMap.class );

    public static final AssetDescriptor<Texture> bgLayer0 =
        new AssetDescriptor<Texture>("world/tiles/background/BACKGROUND.png", Texture.class );

    public static final AssetDescriptor<Texture> bgLayer1 =
        new AssetDescriptor<Texture>("world/tiles/background/WOODS - Fourth.png", Texture.class );

    public static final AssetDescriptor<Texture> bgLayer2 =
        new AssetDescriptor<Texture>("world/tiles/background/WOODS - Third.png", Texture.class );

    public static final AssetDescriptor<Texture> bgLayer3 =
        new AssetDescriptor<Texture>("world/tiles/background/WOODS - Second.png", Texture.class );

    public static final AssetDescriptor<Texture> bgLayer4 =
        new AssetDescriptor<Texture>("world/tiles/background/WOODS - First.png", Texture.class );


    Assets()
    {
        manager.setLoader(TiledMap.class, new AtlasTmxMapLoader(new InternalFileHandleResolver()));
    }

    public void load()
    {
        //Hier geladen
        manager.load(playerTexture);
        manager.load(fullHeart);
        manager.load(emptyHeart);
        manager.load(enemyTexture);
        manager.load(enemyDeathTexture);
        manager.load(map);
        manager.load(bgLayer0);
        manager.load(bgLayer1);
        manager.load(bgLayer2);
        manager.load(bgLayer3);
        manager.load(bgLayer4);


    }

    public void dispose()
    {
        //Hier wird alles gelöscht
        manager.dispose();
    }
}
