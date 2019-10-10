/**
 * Created by IntelliJ Idea.
 * User: Якимов В.Н.
 * E-mail: yakimovvn@bk.ru
 */

package com.arpg.game;

public class Potion implements Item {
    public enum Type {
        HP
    }

    private String title;
    private Type type;
    private int power;

    @Override
    public Item.Type getItemType() {
        return Item.Type.POTION;
    }

    public Type getType() {
        return type;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public boolean isUsable() {
        return true;
    }

    @Override
    public boolean isWearable() {
        return false;
    }

    public int getPower() {
        return power;
    }

    public Potion(String title, Type type, int power) {
        this.title = title;
        this.type = type;
        this.power = power;
    }
}
