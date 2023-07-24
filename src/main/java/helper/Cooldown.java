package helper;

import jangl.time.Clock;

public class Cooldown {
    private float maxCooldown;
    private float currentCooldown;

    public Cooldown(float cooldown) {
        this.maxCooldown = cooldown;
        this.currentCooldown = 0;
    }

    public void update() {
        this.currentCooldown = Math.max(0, (float) (this.currentCooldown - Clock.getTimeDelta()));
    }

    public boolean onCooldown() {
        return this.currentCooldown != 0;
    }

    public void activate() {
        this.currentCooldown = this.maxCooldown;
    }

    public void setMaxCooldown(float maxCooldown) {
        this.maxCooldown = maxCooldown;
    }

    public float getCurrentCooldown() {
        return this.currentCooldown;
    }

    public void setCurrentCooldown(float cooldown) {
        this.currentCooldown = cooldown;
    }

    public float getMaxCooldown() {
        return maxCooldown;
    }
}
