package game;

import game.gameobjects.player.Player;
import jangl.JANGL;
import jangl.coords.WorldCoords;
import jangl.graphics.Camera;
import jangl.graphics.font.Font;
import jangl.graphics.font.Text;
import jangl.io.Window;
import jangl.time.Clock;
import org.lwjgl.glfw.GLFW;

public class Game implements AutoCloseable {
    private final Player player;
    private final EnemySpawner enemySpawner;
    private final Map map;
    private final Text enemiesCounter;

    public Game() {
        this.map = new Map();
        this.enemySpawner = new EnemySpawner(null, this.map);
        this.player = new Player(this.map.getWalls(), this.enemySpawner.getEnemyList(), 0.8f);
        this.enemySpawner.setPlayer(this.player);

        Font arial = new Font("src/main/resources/font/arial.fnt", "src/main/resources/font/arial.png");
        arial.setObeyCamera(false);
        this.enemiesCounter = new Text(new WorldCoords(0.05f, 0.95f), arial, 0.05f, "");
    }

    public void update() {
        this.player.update();
        this.enemySpawner.update();

        String enemiesNumberString = "Enemies: " + this.enemySpawner.getEnemyList().size() + " | Time to next wave: " + Math.round(this.enemySpawner.timeToNextWave());
        if (!this.enemiesCounter.getText().equals(enemiesNumberString)) {
            this.enemiesCounter.setText(enemiesNumberString);
        }

        Camera.setCenter(this.player.getRect().getTransform().getCenter());
    }

    public void draw() {
        Window.clear();

        this.map.drawFloor();
        this.map.drawWalls();

        this.player.draw();
        this.enemySpawner.draw();

        this.enemiesCounter.draw();
    }

    public void run() {
        while (Window.shouldRun()) {
            this.update();
            this.draw();

            Window.setTitle(String.valueOf(Clock.getSmoothedFps()));

            JANGL.update();
        }
    }

    @Override
    public void close() {
        this.player.close();
        this.enemySpawner.close();
        this.map.close();
    }
}
