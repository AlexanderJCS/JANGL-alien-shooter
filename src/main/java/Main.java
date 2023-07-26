import game.Game;
import helper.Consts;
import jangl.JANGL;
import jangl.graphics.textures.TextureBuilder;
import jangl.io.Window;

public class Main {
    public static void main(String[] args) {
        JANGL.init(1600, 900);
        Window.setTitle("Enemy Shooter");
        Window.setVsync(true);
        Window.setIcon(new TextureBuilder().setImagePath("src/main/resources/icon.png"));

        Consts.FONT.setObeyCamera(false);

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
