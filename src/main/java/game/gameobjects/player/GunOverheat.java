package game.gameobjects.player;

import game.gameobjects.helper.Cooldown;
import jangl.time.Clock;

public class GunOverheat {
    private final float increment;
    private final float decrement;
    private float overheatPercent;
    private boolean onCooldown;

    /**
     * @param increment The amount to increment when firing. If this gets to 1, you overheated
     * @param decrement The amount to decrement when not firing.
     */
    public GunOverheat(float increment, float decrement) {
        this.increment = increment;
        this.decrement = decrement;
        this.overheatPercent = 0;
        this.onCooldown = false;
    }

    public void fire() {
        this.overheatPercent += this.increment;
    }

    public void update() {
        this.overheatPercent = (float) Math.max(this.overheatPercent - Clock.getTimeDelta() * this.decrement, 0);

        if (this.overheatPercent == 0) {
            this.onCooldown = false;
        } else if (this.overheatPercent > 1) {
            this.onCooldown = true;
        }
    }

    public boolean canFire() {
        return !this.onCooldown;
    }

    public float getOverheatPercent() {
        return this.overheatPercent;
    }
}
