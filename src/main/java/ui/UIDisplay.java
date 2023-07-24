package ui;

import game.EnemySpawner;
import game.TextureMap;
import game.gameobjects.player.Player;
import jangl.color.ColorFactory;
import jangl.coords.WorldCoords;
import jangl.graphics.font.Font;

public class UIDisplay implements AutoCloseable {
    private final Player player;
    private final EnemySpawner enemySpawner;

    private final Bar healthBar;
    private final Bar waveBar;
    private final TextWithIcon enemyCounter;

    public UIDisplay(Player player, EnemySpawner enemySpawner) {
        this.player = player;
        this.enemySpawner = enemySpawner;

        this.healthBar = new Bar("heart", new WorldCoords(0.05f, 0.1f), 0.3f, 0.05f, ColorFactory.fromNormalized(1, 0.1f, 0.1f, 1));
        this.waveBar = new Bar("clock", new WorldCoords(0.05f, 0.175f), 0.3f, 0.05f, ColorFactory.from255(102, 206, 255, 255));

        Font font = new Font(
                "src/main/resources/font/arial.fnt", "src/main/resources/font/arial.png"
        );

        font.setObeyCamera(false);

        this.enemyCounter = new TextWithIcon(new WorldCoords(0.05f, 0.25f), font, "0", 0.05f, "enemyUI");
    }

    public void draw() {
        this.healthBar.draw();
        this.waveBar.draw();
        this.enemyCounter.draw();
    }

    public void update() {
        this.healthBar.setPercentage(this.player.getHealth() / this.player.getMaxHealth());
        this.waveBar.setPercentage(this.enemySpawner.timeToNextWave() / this.enemySpawner.waveTime());
        this.enemyCounter.getText().setText(String.valueOf(this.enemySpawner.getEnemyList().size()));
    }

    @Override
    public void close() {
        this.healthBar.close();
        this.waveBar.close();
        this.enemyCounter.getText().getFont().close();
        this.enemyCounter.close();
    }
}
