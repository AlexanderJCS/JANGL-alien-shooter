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
        put(
                "white",
                new Texture(
                        new TextureBuilder().fill(
                                ColorFactory.fromNormalized(1, 1, 1, 1), 1, 1)
                )
        );

        put(
                "green",
                new Texture(
                        new TextureBuilder().fill(
                                ColorFactory.from255(0, 214, 49, 255), 1, 1)
                )
        );

        // Game objects
        put("player", new Texture(new TextureBuilder().setImagePath("src/main/resources/textures/game_objects/player.png")));
        put("enemy", new Texture(new TextureBuilder().setImagePath("src/main/resources/textures/game_objects/enemy.png")));
        put("wall", new Texture(new TextureBuilder().setImagePath("src/main/resources/textures/game_objects/wall.png")));
        put("floor", new Texture(new TextureBuilder().setImagePath("src/main/resources/textures/game_objects/floor.png")));

        // UI
        put("heart", new Texture(new TextureBuilder().setImagePath("src/main/resources/textures/ui/heart.png").setObeyCamera(false)));
        put("clock", new Texture(new TextureBuilder().setImagePath("src/main/resources/textures/ui/clock.png").setObeyCamera(false)));
        put("enemyUI", new Texture(new TextureBuilder().setImagePath("src/main/resources/textures/ui/enemy.png").setObeyCamera(false)));
    }};

    public static Texture get(String id) {
        return TEXTURE_MAP.get(id);
    }
}
