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

        put("player", new Texture(new TextureBuilder().setImagePath("src/main/resources/textures/game_objects/player.png")));
        put("enemy", new Texture(new TextureBuilder().setImagePath("src/main/resources/textures/game_objects/enemy.png")));
        put("wall", new Texture(new TextureBuilder().setImagePath("src/main/resources/textures/game_objects/wall.png")));
        put("floor", new Texture(new TextureBuilder().setImagePath("src/main/resources/textures/game_objects/floor.png")));

        put("heart", new Texture(new TextureBuilder().setImagePath("src/main/resources/textures/ui/heart.png")));
        put("clock", new Texture(new TextureBuilder().setImagePath("src/main/resources/textures/ui/clock.png")));
    }};

    public static Texture get(String id) {
        return TEXTURE_MAP.get(id);
    }
}
