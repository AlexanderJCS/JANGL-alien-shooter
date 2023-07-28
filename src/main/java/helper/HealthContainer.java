package helper;

import game.SoundPlayer;
import jangl.time.Clock;

public class HealthContainer {
    private final float maxHealth;
    private float health;
    private final Cooldown cooldown;
    private float regen;
    private final String soundID;

    public HealthContainer(float maxHealth, float cooldownTime, float regen, String soundID) {
        this.cooldown = new Cooldown(cooldownTime);
        this.maxHealth = maxHealth;
        this.health = maxHealth;
        this.regen = regen;
        this.soundID = soundID;
    }

    public void update() {
        this.cooldown.update();

        if (!this.cooldown.onCooldown()) {
            this.health = (float) Math.min(this.health + this.regen * Clock.getTimeDelta(), this.maxHealth);
        }
    }

    public void takeDamage(float damage) {
        if (this.cooldown.onCooldown()) {
            return;
        }

        if (damage > 0 && this.soundID != null) {
            SoundPlayer.playSound(this.soundID);
        }

        this.health = Math.max(this.health - damage, 0);
        this.cooldown.activate();
    }

    public boolean onCooldown() {
        return this.cooldown.onCooldown();
    }

    public float getHealth() {
        return this.health;
    }

    public float getMaxHealth() {
        return maxHealth;
    }

    public void setRegen(float regen) {
        this.regen = regen;
    }
}
