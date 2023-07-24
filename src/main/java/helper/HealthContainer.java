package helper;

import jangl.time.Clock;

public class HealthContainer {
    private final float maxHealth;
    private float health;
    private final Cooldown cooldown;
    private final float regen;

    public HealthContainer(float maxHealth, float cooldownTime, float regen) {
        this.cooldown = new Cooldown(cooldownTime);
        this.maxHealth = maxHealth;
        this.health = maxHealth;
        this.regen = regen;
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

        this.health = Math.max(this.health - damage, 0);
        this.cooldown.activate();
    }

    public boolean onCooldown() {
        return this.cooldown.onCooldown();
    }

    public float getHealth() {
        return this.health;
    }
}
