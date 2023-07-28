package game.gameobjects.player;

import game.SoundPlayer;
import game.gameobjects.Enemy;
import helper.Consts;
import helper.Cooldown;
import game.gameobjects.Wall;
import jangl.coords.WorldCoords;
import jangl.io.keyboard.Keyboard;
import jangl.shapes.Transform;
import org.lwjgl.glfw.GLFW;
import ui.upgrades.UpgradeShop;

import java.util.ArrayList;
import java.util.List;

public class LaserGun implements AutoCloseable {
    private static final float LASER_DELETION_RANGE = Math.max(WorldCoords.getMiddle().x, WorldCoords.getMiddle().y) * 2 + 1;
    private final Cooldown cooldown;
    private final float speed;
    private final List<Laser> lasers;
    private final List<Wall> walls;
    private final List<Enemy> aliens;
    private final UpgradeShop upgradeShop;
    private final GunOverheat overheat;

    public LaserGun(List<Wall> walls, List<Enemy> aliens, UpgradeShop upgradeShop) {
        this.walls = walls;
        this.aliens = aliens;

        this.lasers = new ArrayList<>();
        this.speed = Consts.SETTINGS.getFloat("bullet/speed");

        // Set the cooldown to 0.05 for a machine gun. Set the cooldown to 0 (or near 0)
        // for the laser equivalent of the A-10 Warthog
        this.cooldown = new Cooldown(Consts.SETTINGS.getFloat("gun/fire_rate"));
        SoundPlayer.getSound("shoot").setVolume(0.3f);
        this.overheat = new GunOverheat(
                Consts.SETTINGS.getFloat("gun/cooldown_increment"),
                Consts.SETTINGS.getFloat("gun/cooldown_decrement")
        );
        this.upgradeShop = upgradeShop;
    }

    public void draw() {
        for (Laser laser : this.lasers) {
            laser.draw();
        }
    }

    public GunOverheat getOverheat() {
        return this.overheat;
    }

    public void update(Transform playerTransform, PlayerBank bank) {
        this.cooldown.update();
        this.overheat.update();

        this.spawnLaser(bank, playerTransform);
        this.cleanUpLasers(playerTransform);

        for (int i = this.lasers.size() - 1; i >= 0; i--) {
            Laser laser = this.lasers.get(i);

            laser.update();
            if (laser.shouldDestroy()) {
                laser.close();
                this.lasers.remove(i);
            }
        }
    }

    private void spawnLaser(PlayerBank bank, Transform playerTransform) {
        if (Keyboard.getKeyDown(GLFW.GLFW_KEY_SPACE) && !this.cooldown.onCooldown()) {
            if (!this.overheat.canFire()) {
                SoundPlayer.playSound("overheat");
                this.cooldown.activate();
                return;
            }

            this.lasers.add(
                    new Laser(
                            this.walls,
                            this.aliens,
                            bank,
                            playerTransform.getCenter(),
                            playerTransform.getLocalRotationAngle(),
                            this.speed,
                            this.upgradeShop.getUpgradeLevel("pierce")
                    )
            );

            this.cooldown.activate();
            this.overheat.fire();
            SoundPlayer.playSound("shoot");
        }
    }

    private void cleanUpLasers(Transform playerTransform) {
        for (int i = this.lasers.size() - 1; i >= 0; i--) {
            Laser laser = this.lasers.get(i);

            WorldCoords laserCenter = laser.getRect().getTransform().getCenter();
            WorldCoords playerCenter = playerTransform.getCenter();

            double distSquared = Math.pow(laserCenter.x - playerCenter.x, 2)
                    + Math.pow(laserCenter.y - playerCenter.y, 2);

            if (distSquared > Math.pow(LASER_DELETION_RANGE, 2)) {
                laser.close();
                this.lasers.remove(i);
            }
        }
    }

    @Override
    public void close() {
        for (Laser laser : this.lasers) {
            laser.close();
        }
    }
}
