package game;

import game.gameobjects.player.Player;
import jangl.JANGL;
import jangl.coords.WorldCoords;
import jangl.graphics.Camera;
import jangl.graphics.font.Font;
import jangl.graphics.font.Text;
import jangl.io.Window;
import jangl.io.keyboard.KeyEvent;
import jangl.io.keyboard.Keyboard;
import jangl.time.Clock;
import org.lwjgl.glfw.GLFW;
import ui.UIDisplay;

import java.util.List;

public class Game implements AutoCloseable {
    private final Player player;
    private final EnemySpawner enemySpawner;
    private final GameMap gameMap;
    private final Text enemiesCounter;
    private boolean paused;
    private final UIDisplay uiDisplay;

    public Game() {
        this.gameMap = new GameMap();
        this.enemySpawner = new EnemySpawner(null, this.gameMap);
        this.player = new Player(this.gameMap.getWalls(), this.enemySpawner.getEnemyList(), 0.8f);
        this.enemySpawner.setPlayer(this.player);

        Font arial = new Font("src/main/resources/font/arial.fnt", "src/main/resources/font/arial.png");
        arial.setObeyCamera(false);
        this.enemiesCounter = new Text(new WorldCoords(0.05f, 0.95f), arial, 0.05f, "");
        this.paused = false;

        this.uiDisplay = new UIDisplay(this.player, this.enemySpawner);
    }

    public void update() {
        this.player.update();
        this.enemySpawner.update();

        String enemiesNumberString = "Enemies: " + this.enemySpawner.getEnemyList().size();
        if (!this.enemiesCounter.getText().equals(enemiesNumberString)) {
            this.enemiesCounter.setText(enemiesNumberString);
        }

        this.uiDisplay.update();
        Camera.setCenter(this.player.getRect().getTransform().getCenter());
    }

    public void draw() {
        Window.clear();

        this.gameMap.drawFloor();
        this.gameMap.drawWalls();

        this.player.draw();
        this.enemySpawner.draw();

        this.enemiesCounter.draw();

        this.uiDisplay.draw();
    }

    public void pauseCheck(List<KeyEvent> keyEvents) {
        for (KeyEvent event : keyEvents) {
            if (event.action == GLFW.GLFW_PRESS && event.key == GLFW.GLFW_KEY_P) {
                this.paused = !this.paused;
                this.enemiesCounter.setText(this.enemiesCounter.getText() + " | PAUSED");
            }
        }
    }

    public void run() {
        while (Window.shouldRun() && this.player.getHealth() > 0) {
            this.pauseCheck(Keyboard.getEvents());
            this.draw();

            if (!this.paused) {
                this.update();
            }

            Window.setTitle("Alien Shooter | FPS: " + Math.round(Clock.getSmoothedFps()));

            JANGL.update();
        }
    }

    @Override
    public void close() {
        this.player.close();
        this.enemySpawner.close();
        this.gameMap.close();
        this.enemiesCounter.getFont().close();
        this.enemiesCounter.close();
        this.uiDisplay.close();
    }
}
