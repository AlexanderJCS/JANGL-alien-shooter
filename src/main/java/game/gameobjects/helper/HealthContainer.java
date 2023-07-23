package game.gameobjects.helper;

public class HealthContainer {
    private final float maxHealth;
    private float health;
    private final Cooldown cooldown;

    public HealthContainer(float maxHealth, float cooldownTime) {
        this.cooldown = new Cooldown(cooldownTime);
        this.maxHealth = maxHealth;
        this.health = maxHealth;
    }

    public void update() {
        this.cooldown.update();
    }

    public void takeDamage(float damage) {
        if (this.cooldown.onCooldown()) {
            return;
        }

        this.health -= damage;
        this.cooldown.activate();
    }

    public boolean onCooldown() {
        return this.cooldown.onCooldown();
    }

    public float getHealth() {
        return this.health;
    }
}
