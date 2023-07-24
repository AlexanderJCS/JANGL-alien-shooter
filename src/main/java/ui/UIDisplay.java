package ui;

import game.EnemySpawner;
import game.TextureMap;
import game.gameobjects.player.Player;
import jangl.color.ColorFactory;
import jangl.coords.WorldCoords;

public class UIDisplay implements AutoCloseable {
    private final Player player;
    private final EnemySpawner enemySpawner;

    private final Bar healthBar;
    private final Bar waveBar;

    public UIDisplay(Player player, EnemySpawner enemySpawner) {
        this.player = player;
        this.enemySpawner = enemySpawner;

        this.healthBar = new Bar(TextureMap.get("heart"), new WorldCoords(0.05f, 0.1f), 0.3f, 0.05f, ColorFactory.fromNormalized(1, 0.1f, 0.1f, 1));
        this.waveBar = new Bar(TextureMap.get("clock"), new WorldCoords(0.05f, 0.175f), 0.3f, 0.05f, ColorFactory.from255(102, 206, 255, 255));
    }

    public void draw() {
        this.healthBar.draw();
        this.waveBar.draw();
    }

    public void update() {
        this.healthBar.setPercentage(this.player.getHealth() / this.player.getMaxHealth());
        this.waveBar.setPercentage(this.enemySpawner.timeToNextWave() / this.enemySpawner.waveTime());
    }

    @Override
    public void close() {
        this.healthBar.close();
        this.waveBar.close();
    }
}
