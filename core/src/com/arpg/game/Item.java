package com.arpg.game;

public interface Item {
    enum Type {
        POTION, WEAPON, ARMOR
    }

    Type getItemType();
    String getTitle();
    boolean isUsable();
    boolean isWearable();
}
