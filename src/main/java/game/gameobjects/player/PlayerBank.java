package game.gameobjects.player;

/**
 * Stores money made by killing enemies.
 */
public class PlayerBank {
    private float money;

    public PlayerBank() {
        this.money = 0;
    }

    public float getMoney() {
        return this.money;
    }

    public void addMoney(float add) {
        this.money += add;
    }
}
