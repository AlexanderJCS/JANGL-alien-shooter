package game.gameobjects.player;

import helper.Consts;

/**
 * Stores money made by killing enemies.
 */
public class PlayerBank {
    private float money;

    public PlayerBank() {
        this.money = Consts.SETTINGS.getFloat("start/money");
    }

    public float getMoney() {
        return this.money;
    }

    public void addMoney(float add) {
        this.money += add;
    }
}
