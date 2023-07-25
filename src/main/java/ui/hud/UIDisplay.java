package ui.hud;

import game.EnemySpawner;
import game.gameobjects.player.Player;
import helper.Consts;
import jangl.color.ColorFactory;
import jangl.coords.WorldCoords;

public class UIDisplay implements AutoCloseable {
    private final Player player;
    private final EnemySpawner enemySpawner;

    private final Bar healthBar;
    private final Bar waveBar;
    private final TextWithIcon enemyCounter;
    private final TextWithIcon coinCounter;

    public UIDisplay(Player player, EnemySpawner enemySpawner) {
        this.player = player;
        this.enemySpawner = enemySpawner;

        this.healthBar = new Bar("heart", new WorldCoords(0.05f, 0.1f), 0.3f, 0.05f, ColorFactory.fromNormalized(1, 0.1f, 0.1f, 1));
        this.waveBar = new Bar("clock", new WorldCoords(0.05f, 0.175f), 0.3f, 0.05f, ColorFactory.from255(102, 206, 255, 255));

        this.enemyCounter = new TextWithIcon(new WorldCoords(0.05f, 0.25f), Consts.FONT, "0", 0.05f, "enemyUI");
        this.coinCounter = new TextWithIcon(new WorldCoords(0.05f, 0.325f), Consts.FONT, "0", 0.05f, "coin");
    }

    public void draw() {
        this.healthBar.draw();
        this.waveBar.draw();
        this.enemyCounter.draw();
        this.coinCounter.draw();
    }

    public void update() {
        this.healthBar.setPercentage(this.player.getHealth() / this.player.getMaxHealth());
        this.waveBar.setPercentage(this.enemySpawner.timeToNextWave() / this.enemySpawner.waveTime());
        this.enemyCounter.getText().setText(String.valueOf(this.enemySpawner.getEnemyList().size()));
        this.coinCounter.getText().setText(String.valueOf(Math.round(this.player.getBank().getMoney())));
    }

    @Override
    public void close() {
        this.healthBar.close();
        this.waveBar.close();
        this.enemyCounter.close();
    }
}
