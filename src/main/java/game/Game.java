package game;

import game.gameobjects.player.Player;
import helper.Consts;
import helper.EventsManager;
import jangl.Jangl;
import jangl.color.ColorFactory;
import jangl.coords.WorldCoords;
import jangl.graphics.Camera;
import jangl.graphics.font.Text;
import jangl.graphics.font.TextBuilder;
import jangl.graphics.shaders.ShaderProgram;
import jangl.graphics.shaders.premade.ColorShader;
import jangl.io.Window;
import jangl.io.keyboard.KeyEvent;
import jangl.io.keyboard.Keyboard;
import jangl.shapes.Rect;
import jangl.time.Clock;
import org.lwjgl.glfw.GLFW;
import ui.hud.UIDisplay;

import java.util.List;

public class Game implements AutoCloseable {
    private final Player player;
    private final EnemySpawner enemySpawner;
    private final GameMap gameMap;
    private final Text infoText;
    private boolean paused;
    private final UIDisplay uiDisplay;

    public Game() {
        this.gameMap = new GameMap();
        this.enemySpawner = new EnemySpawner(null, this.gameMap);
        this.player = new Player(this.gameMap.getWalls(), this.enemySpawner.getEnemyList(), Consts.SETTINGS.getFloat("player/base_speed"));
        this.enemySpawner.setPlayer(this.player);

        this.infoText = new TextBuilder(Consts.FONT, "").setCoords(new WorldCoords(0.05f, 0.975f)).toText();
        this.paused = false;

        this.uiDisplay = new UIDisplay(this.player, this.enemySpawner);
    }

    public void update() {
        EventsManager.getEvents();
        this.uiDisplay.update();

        if (!Keyboard.getKeyDown(GLFW.GLFW_KEY_TAB)) {
            this.player.update();
            this.enemySpawner.update();

            this.infoText.setText("");

            Camera.setCenter(this.player.getRect().getTransform().getCenter());
        } else {
            this.infoText.setText("PAUSED");
            this.player.updateItemShop();
        }
    }

    public void draw() {
        Window.clear();

        this.gameMap.drawFloor();
        this.gameMap.drawWalls();

        this.player.draw();
        this.enemySpawner.draw();

        this.infoText.draw();

        this.uiDisplay.draw();

        if (Keyboard.getKeyDown(GLFW.GLFW_KEY_TAB)) {
            this.player.drawItemShop();
        }
    }

    public void pauseCheck(List<KeyEvent> keyEvents) {
        for (KeyEvent event : keyEvents) {
            if (event.action == GLFW.GLFW_PRESS && (event.key == GLFW.GLFW_KEY_P || event.key == GLFW.GLFW_KEY_ESCAPE)) {
                this.paused = !this.paused;
                this.infoText.setText("PAUSED");
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

            System.gc();
            Jangl.update();
        }
    }

    public void diedScreen() {
        Text diedText = new TextBuilder(Consts.FONT, "You died!\nWAVE No. " + this.enemySpawner.getWaveNumber() + "\nPress G to continue")
                .setCoords(new WorldCoords(0.1f, 0.6f))
                .setYHeight(0.07f)
                .toText();

        Rect background = new Rect(new WorldCoords(0, 1), WorldCoords.getMiddle().x * 2, 1);
        ShaderProgram backgroundShader = new ShaderProgram(new ColorShader(ColorFactory.fromNorm(0, 0, 0, 0.4f)));
        backgroundShader.getVertexShader().setObeyCamera(false);

        while (!Keyboard.getKeyDown(GLFW.GLFW_KEY_G) && Window.shouldRun()) {
            this.draw();
            background.draw(backgroundShader);
            diedText.draw();
            Jangl.update();
        }

        diedText.close();
        background.close();
        backgroundShader.close();
    }

    @Override
    public void close() {
        this.player.close();
        this.enemySpawner.close();
        this.gameMap.close();
        this.infoText.close();
        this.uiDisplay.close();
    }
}
