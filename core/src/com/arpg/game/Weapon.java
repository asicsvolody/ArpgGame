package com.arpg.game;

import com.badlogic.gdx.math.MathUtils;

public class Weapon implements Item {
    private String title;
    private float attackPeriod;
    private int minDamage;
    private int maxDamage;

    @Override
    public Type getItemType() {
        return Type.WEAPON;
    }

    @Override
    public String getTitle() {
        return title + " [" + minDamage + "-" + maxDamage + "]";
    }

    @Override
    public boolean isUsable() {
        return false;
    }

    @Override
    public boolean isWearable() {
        return true;
    }

    public float getAttackPeriod() {
        return attackPeriod;
    }

    public int getDamage() {
        return MathUtils.random(minDamage, maxDamage);
    }

    public Weapon(String title, float attackPeriod, int minDamage, int maxDamage) {
        this.title = title;
        this.attackPeriod = attackPeriod;
        this.minDamage = minDamage;
        this.maxDamage = maxDamage;
    }
}
