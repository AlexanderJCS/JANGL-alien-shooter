package game;

import game.gameobjects.Enemy;
import helper.Consts;
import helper.Cooldown;
import game.gameobjects.player.Player;
import jangl.coords.WorldCoords;
import jangl.sound.SoundState;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class EnemySpawner implements AutoCloseable {
    private final List<Enemy> enemies;
    private final Cooldown waveCooldown;
    private final Random random;
    private int waveNumber;

    // References
    private final GameMap gameMap;
    private Player player;


    public EnemySpawner(Player player, GameMap gameMap) {
        this.waveNumber = 1;
        this.enemies = new ArrayList<>();
        this.random = new Random();

        this.player = player;
        this.gameMap = gameMap;

        this.waveCooldown = new Cooldown(Consts.SETTINGS.getFloat("wave/cooldown"));
    }

    public float timeToNextWave() {
        return this.waveCooldown.getCurrentCooldown();
    }

    public float waveTime() {
        return this.waveCooldown.getMaxCooldown();
    }

    public int getWaveNumber() {
        return this.waveNumber - 1;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public List<Enemy> getEnemyList() {
        return this.enemies;
    }

    private void spawnNextWave() {
        float speed = Consts.SETTINGS.getFloat("enemy/base_speed") * (float) (Math.pow(this.waveNumber, 1.1) / 100 + 1);
        float randomness = speed / 10;

        int numEnemies = (int) Math.round(
                Math.pow(this.waveNumber, 1.3) * Consts.SETTINGS.getFloat("enemy/multiplier")
        );

        List<WorldCoords> spawnLocations = this.gameMap.getSpawnLocations();

        for (int i = 0; i < numEnemies; i++) {
            int spawnLocationIndex = this.random.nextInt(spawnLocations.size());
            WorldCoords spawnLocation = new WorldCoords(
                    spawnLocations.get(spawnLocationIndex).x,
                    spawnLocations.get(spawnLocationIndex).y
            );

            // Add some randomness so large clumps of enemies aren't perfectly stacked atop one another
            float randX = this.random.nextFloat(GameMap.CUBE_DIMENSIONS / 2);
            spawnLocation.x += randX - randX / 2;
            float randY = this.random.nextFloat(GameMap.CUBE_DIMENSIONS / 2);
            spawnLocation.y += randY - randY / 2;

            this.enemies.add(
                    new Enemy(
                            spawnLocation,
                            this.player, this.gameMap.getWalls(),
                            speed - this.random.nextFloat(randomness)
                    )
            );
        }

        this.waveNumber++;
        this.waveCooldown.activate();
    }

    public void update() {
        this.waveCooldown.update();

        float currentCooldown = this.waveCooldown.getCurrentCooldown();
        if (currentCooldown <= 5 && currentCooldown % 1 < 0.05 && SoundPlayer.getSound("round_almost_done").getState() != SoundState.PLAYING) {
            SoundPlayer.playSound("round_almost_done");
        }

        if (!this.waveCooldown.onCooldown()) {
            SoundPlayer.playSound("round_done");
            this.spawnNextWave();
        }

        if (this.enemies.size() == 0 && this.waveCooldown.getCurrentCooldown() > 5) {
            this.waveCooldown.setCurrentCooldown(5);
        }

        for (int i = this.enemies.size() - 1; i >= 0; i--) {
            Enemy enemy = this.enemies.get(i);

            enemy.update();

            if (enemy.shouldDestroy()) {
                enemy.close();
                this.enemies.remove(i);
            }
        }
    }

    public void draw() {
        if (this.enemies.size() == 0) {
            return;
        }

        for (Enemy enemy : this.enemies) {
            enemy.draw();
        }
    }

    @Override
    public void close() {
        for (Enemy enemy : this.enemies) {
            enemy.close();
        }
    }
}
