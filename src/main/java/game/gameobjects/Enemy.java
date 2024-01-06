package game.gameobjects;

import helper.Consts;
import helper.Cooldown;
import helper.HealthContainer;
import game.gameobjects.player.Player;
import jangl.coords.WorldCoords;
import jangl.shapes.Rect;
import jangl.shapes.Shape;
import jangl.shapes.TileSheetRect;
import jangl.shapes.Transform;
import jangl.time.Clock;
import org.lwjgl.glfw.GLFW;

import java.util.List;

public class Enemy extends GameObject implements Destroyable {
    private final static float COOLDOWN_SPEED_MULTIPLIER = 0.5f;
    private final float speed;
    private final Player player;
    private final List<Wall> walls;
    private float angle;
    private final HealthContainer healthContainer;
    private final float angleOffset;
    /**
     * Run away after hitting the player.
     */
    private final Cooldown runAwayDamagedCooldown;
    private final Cooldown animationSwitch;
    private boolean animationState;

    public Enemy(WorldCoords center, Player player, List<Wall> walls, float speed) {
        super(new TileSheetRect(new WorldCoords(0, 0), 0.075f, 0.075f, 1, 2), "enemy");

        this.getRect().getTransform().setPos(center);
        this.player = player;
        this.speed = speed;
        this.walls = walls;
        this.angle = 0;
        this.healthContainer = new HealthContainer(
                Consts.SETTINGS.getFloat("enemy/health"),
                Consts.SETTINGS.getFloat("enemy/invincibility"),
                Consts.SETTINGS.getFloat("enemy/regen"),
                null
        );
        this.runAwayDamagedCooldown = new Cooldown(1);
        this.animationSwitch = new Cooldown(0.05f);
        this.animationState = true;

        this.angleOffset = Consts.RANDOM.nextFloat((float) -Math.PI / 18, (float) Math.PI / 18);
    }

    @Override
    public void update() {
        this.healthContainer.update();
        this.runAwayDamagedCooldown.update();

        this.getRect().getTransform().setRotation(0);
        this.move();
        this.setRotation();
        this.dealDamage();
        this.animate();
    }

    public void animate() {
        this.animationSwitch.update();
        if (this.animationSwitch.onCooldown()) {
            return;
        }

        TileSheetRect tileSheetRect = (TileSheetRect) this.getRect();
        this.animationState = !this.animationState;

        tileSheetRect.setTilePos(0, this.animationState ? 2 : 1);
        this.animationSwitch.activate();
    }

    /**
     * This draw method requires that the enemy texture is already bound
     */
    @Override
    public void draw() {
        if (this.healthContainer.onCooldown() && Math.round(GLFW.glfwGetTime() * 20) % 2 == 0) {
            return;
        }

        super.draw();
    }

    private void setRotation() {
        WorldCoords thisLocation = this.getRect().getTransform().getCenter();
        WorldCoords targetCoords = this.player.getRect().getTransform().getCenter();

        this.angle = (float) (Math.atan2(thisLocation.y - targetCoords.y, thisLocation.x - targetCoords.x)) + this.angleOffset;

        // Prevent the alien from running towards you upside-down.
        float realAngle = this.angle > Math.PI / 2 || this.angle < -Math.PI / 2 ? (float) (this.angle + Math.PI) : this.angle;
        this.getRect().getTransform().setRotation(realAngle);
    }

    /**
     * @param movement The WorldCoords movement. WARNING: only have one axis at a time set to a non-zero value.
     */
    private void moveInDirection(WorldCoords movement) {
        Transform transform = this.getRect().getTransform();

        transform.shift(movement);

        for (Wall wall : this.walls) {
            // Treat the two objects as circles, where their radii is the farthest point from the center.
            // If the two circles are not colliding, then the rectangles cannot be colliding.
            // JANGL already provides an optimization similar to this, but since it needs to be general purpose
            // (i.e., for all shapes), it's about 3x slower than doing this.
            Rect wallRect = wall.getRect();
            Rect thisRect = this.getRect();

            WorldCoords wallCenter = wallRect.getTransform().getCenter();
            WorldCoords thisCenter = transform.getCenter();

            double distSquared = Math.pow(wallCenter.x - thisCenter.x, 2) + Math.pow(wallCenter.y - thisCenter.y, 2);

            double wallRadiusSquared = Math.pow(wallRect.getWidth() / 2, 2) + Math.pow(wallRect.getHeight() / 2, 2);
            double playerRadiusSquared = Math.pow(thisRect.getWidth() / 2, 2) + Math.pow(thisRect.getHeight() / 2, 2);

            if (Math.sqrt(playerRadiusSquared) + Math.sqrt(wallRadiusSquared) < Math.sqrt(distSquared)) {
                continue;
            }

            if (Shape.collides(wall.getRect(), this.getRect())) {
                transform.shift(-movement.x, -movement.y);
                break;
            }
        }
    }

    public void dealDamage() {
        if (Shape.collides(this.getRect(), this.player.getRect())) {
            this.player.dealDamage(1);
            this.runAwayDamagedCooldown.activate();
        }
    }

    public void takeDamage(float damage) {
        this.healthContainer.takeDamage(damage);
    }

    private void move() {
        float speed = this.healthContainer.onCooldown() ? this.speed * COOLDOWN_SPEED_MULTIPLIER : this.speed;

        float moveX = (float) (Math.cos(this.angle) * speed * Clock.getTimeDelta());
        float moveY = (float) (Math.sin(this.angle) * speed * Clock.getTimeDelta());

        if (this.runAwayDamagedCooldown.onCooldown()) {
            moveX *= -1;
            moveY *= -1;
        }

        this.moveInDirection(new WorldCoords(-moveX, 0));
        this.moveInDirection(new WorldCoords(0, -moveY));
    }

    public boolean onCooldown() {
        return this.healthContainer.onCooldown();
    }

    @Override
    public boolean shouldDestroy() {
        return this.healthContainer.getHealth() <= 0;
    }
}
