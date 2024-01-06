import game.Game;
import game.SoundPlayer;
import helper.Consts;
import helper.ini.IniParser;
import jangl.Jangl;
import jangl.graphics.textures.TextureBuilder;
import jangl.io.Window;

public class Main {
    public static void main(String[] args) {
        IniParser settings = new IniParser("src/main/resources/settings.ini");

        Jangl.init(settings.getInt("window/width"), settings.getInt("window/height"));

        Window.setTitle("Enemy Shooter");
        Window.setVsync(true);
        Window.setIcon(new TextureBuilder().setImagePath("src/main/resources/icon.png"));

        Consts.FONT.setObeyCamera(false);

        SoundPlayer.init();

        while (Window.shouldRun()) {
            Game game = new Game();
            game.run();

            if (Window.shouldRun()) {
                game.diedScreen();
            }

            game.close();
        }

        Window.close();
    }
}
