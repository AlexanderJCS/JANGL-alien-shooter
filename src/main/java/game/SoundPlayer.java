package game;

import jangl.sound.Sound;

import java.util.HashMap;

public class SoundPlayer {
    private SoundPlayer() {

    }

    private static final java.util.Map<String, Sound> SOUND_MAP = new HashMap<>(){{
        put("shoot", new Sound("src/main/resources/sounds/shoot.ogg"));
        put("round_almost_done", new Sound("src/main/resources/sounds/round_almost_done.ogg"));
        put("round_done", new Sound("src/main/resources/sounds/round_done.ogg"));
    }};

    public static void setVolume(String id, float volume) {
        SOUND_MAP.get(id).setVolume(volume);
    }

    public static void playSound(String id) {
        SOUND_MAP.get(id).play();
    }
}
