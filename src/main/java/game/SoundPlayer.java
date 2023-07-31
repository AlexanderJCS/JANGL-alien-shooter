package game;

import helper.Consts;
import jangl.sound.Sound;

import java.util.HashMap;
import java.util.Map;

public class SoundPlayer {
    private SoundPlayer() {

    }

    private static final java.util.Map<String, Sound> SOUND_MAP = new HashMap<>(){{
        put("shoot", new Sound("src/main/resources/sounds/shoot.ogg"));
        put("round_almost_done", new Sound("src/main/resources/sounds/round_almost_done.ogg"));
        put("round_done", new Sound("src/main/resources/sounds/round_done.ogg"));
        put("overheat", new Sound("src/main/resources/sounds/overheat.ogg"));
        put("hurt", new Sound("src/main/resources/sounds/hurt.ogg"));
        put("buy_item", new Sound("src/main/resources/sounds/buy_item.ogg"));
        put("cant_buy_item", new Sound("src/main/resources/sounds/cant_buy_item.ogg"));
    }};

    public static void playSound(String id) {
        SOUND_MAP.get(id).play();
    }

    public static Sound getSound(String id) {
        return SOUND_MAP.get(id);
    }

    public static void init() {
        for (Map.Entry<String, Sound> entry : SOUND_MAP.entrySet()) {
            entry.getValue().setVolume(Consts.SETTINGS.getFloat("volume/" + entry.getKey()));
        }
    }
}
