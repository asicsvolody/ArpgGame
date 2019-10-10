/**
 * Created by IntelliJ Idea.
 * User: Якимов В.Н.
 * E-mail: yakimovvn@bk.ru
 */

package com.arpg.game;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.ArrayList;
import java.util.List;

public class Inventory {
    private Hero hero;
    private List<Item> items;
    private int coins;
    private int selected;

    public int getCoins() {
        return coins;
    }

    public void addCoins(int amount) {
        coins += amount;
    }

    public void selectPrev() {
        selected--;
        if (selected < 0) {
            selected = items.size() - 1;
        }
    }

    public void selectNext() {
        selected++;
        if (selected >= items.size()) {
            selected = 0;
        }
    }

    public Item getCurrentItem() {
        return items.get(selected);
    }

    public void destroyCurrentItem() {
        items.remove(selected);
        if (selected >= items.size()) {
            selected = 0;
        }
    }

    public void takeCurrentWeapon() {
        if (!(getCurrentItem() instanceof Weapon)) {
            return;
        }
        Weapon tmp = hero.weapon;
        hero.weapon = (Weapon) getCurrentItem();
        items.set(selected, tmp);
    }

    public Inventory(Hero hero) {
        this.hero = hero;
        this.items = new ArrayList<>();
    }

    public void render(SpriteBatch batch, BitmapFont font) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < items.size(); i++) {
            if (i == selected) {
                builder.append("> ");
            }
            builder.append(items.get(i).getTitle()).append("\n");
        }
        font.draw(batch, builder, 1000, 700);
    }

    public void add(Item item) {
        items.add(item);
    }
}
