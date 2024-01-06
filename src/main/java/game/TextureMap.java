package game;

import jangl.color.ColorFactory;
import jangl.graphics.textures.Texture;
import jangl.graphics.textures.TextureBuilder;

import java.util.HashMap;
import java.util.Map;

public class TextureMap {
    private TextureMap() {

    }

    private static final Map<String, Texture> TEXTURE_MAP = new HashMap<>(){{
        // Colors
        put("white", new Texture(new TextureBuilder().fill(ColorFactory.fromNorm(1, 1, 1, 1), 1, 1)));
        put("green", new Texture(new TextureBuilder().fill(ColorFactory.from255(0, 214, 49, 255), 1, 1)));

        // Game objects
        put("player", new Texture(new TextureBuilder("src/main/resources/textures/game_objects/player.png").setPixelatedScaling()));
        put("enemy", new Texture(new TextureBuilder("src/main/resources/textures/game_objects/enemy.png").setPixelatedScaling()));
        put("wall", new Texture(new TextureBuilder("src/main/resources/textures/game_objects/wall.png").setPixelatedScaling()));
        put("floor", new Texture(new TextureBuilder("src/main/resources/textures/game_objects/floor.png").setPixelatedScaling()));

        // UI
        put("heart", new Texture(new TextureBuilder("src/main/resources/textures/ui/heart.png").setObeyCamera(false).setPixelatedScaling()));
        put("clock", new Texture(new TextureBuilder("src/main/resources/textures/ui/clock.png").setObeyCamera(false).setPixelatedScaling()));
        put("enemyUI", new Texture(new TextureBuilder("src/main/resources/textures/ui/enemy.png").setObeyCamera(false).setPixelatedScaling()));
        put("pierce", new Texture(new TextureBuilder("src/main/resources/textures/ui/pierce.png").setObeyCamera(false).setPixelatedScaling()));
        put("coin", new Texture(new TextureBuilder("src/main/resources/textures/ui/coin.png").setObeyCamera(false).setPixelatedScaling()));
        put("wave", new Texture(new TextureBuilder("src/main/resources/textures/ui/wave.png").setObeyCamera(false).setPixelatedScaling()));
        put("speed_up", new Texture(new TextureBuilder("src/main/resources/textures/ui/speed_up.png").setObeyCamera(false).setPixelatedScaling()));
        put("regen", new Texture(new TextureBuilder("src/main/resources/textures/ui/regen.png").setObeyCamera(false).setPixelatedScaling()));
        put("damage_up", new Texture(new TextureBuilder("src/main/resources/textures/ui/damage_up.png").setObeyCamera(false).setPixelatedScaling()));
    }};

    public static Texture get(String id) {
        return TEXTURE_MAP.get(id);
    }
}
