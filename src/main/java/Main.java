import game.Game;
import jangl.JANGL;
import jangl.graphics.textures.TextureBuilder;
import jangl.io.Window;

public class Main {
    public static void main(String[] args) {
        JANGL.init(1600, 900);
        Window.setTitle("Enemy Shooter");
        Window.setVsync(false);
        Window.setIcon(new TextureBuilder().setImagePath("src/main/resources/icon.png"));

        Game game = new Game();
        game.run();
        game.close();

        Window.close();
    }
}
