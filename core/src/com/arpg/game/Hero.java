package com.arpg.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class Hero extends Unit {
    private int score;
    private Inventory inventory;
    // private Sound soundSwordSwipe;

    public boolean isActive() {
        return stats.getHp() > 0;
    }

    public Hero(GameScreen gameScreen) {
        super(gameScreen);
        this.inventory = new Inventory(this);
        this.inventory.add(new Potion("HP Potion", Potion.Type.HP, 25));
        this.inventory.add(new Potion("HP Potion", Potion.Type.HP, 25));
        this.inventory.add(new Potion("HP Potion", Potion.Type.HP, 25));
        this.texture = new TextureRegion(Assets.getInstance().getAtlas().findRegion("Hero")).split(80, 80);
        do {
            this.position.set(MathUtils.random(0, Map.MAP_SIZE_X_PX), MathUtils.random(0, Map.MAP_SIZE_Y_PX));
        } while (!gameScreen.getMap().isCellPassable(position));
        this.area.setPosition(position);
        this.stats = new Stats(1, 1, 1, 20, 1, 1, 10, 320.0f);
        this.weapon = new Weapon("Short Sword", 0.5f, 2, 6);
        // this.soundSwordSwipe=Gdx.audio.newSound(Gdx.files.internal("sounds/swordSwipe.wav"));
    }

    @Override
    public void update(float dt) {
        float speedMod = 1.0f;
        attackTime += dt;

        if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
            speedMod = 1.2f;
        }

        if (damageTimer > 0.0f) {
            damageTimer -= dt;
        }

        boolean btnPressed = false;
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            direction = Direction.LEFT;
            btnPressed = true;
        } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            direction = Direction.RIGHT;
            btnPressed = true;
        } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            direction = Direction.DOWN;
            btnPressed = true;
        } else if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            direction = Direction.UP;
            btnPressed = true;
        }

        if (btnPressed) {
            tmp.set(position);
            tmp.add(direction.getX() * stats.getSpeed() * speedMod * dt, direction.getY() * stats.getSpeed() * speedMod * dt);
            if (gs.getMap().isCellPassable(tmp)) {
                walkTimer += dt * speedMod;
                position.set(tmp);
                area.setPosition(position);
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.Q)) {
            inventory.selectPrev();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            inventory.selectNext();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            Item item = inventory.getCurrentItem();
            if (item.isUsable()) {
                inventory.destroyCurrentItem();
                if (item.getItemType() == Item.Type.POTION) {
                    Potion p = (Potion) item;
                    if (p.getType() == Potion.Type.HP) {
                        int restored = stats.restoreHp(p.getPower());
                        gs.getInfoController().setup(position.x, position.y, "HP +" + restored, Color.GREEN);
                    }
                }
            }
            if (item.isWearable()) {
                if (item.getItemType() == Item.Type.WEAPON) {
                    inventory.takeCurrentWeapon();
                }
            }
        }

        if (Gdx.input.isKeyPressed(Input.Keys.F)) {
            attack();
        }
    }

    public void renderHUD(SpriteBatch batch, BitmapFont font) {
        font.draw(batch, "SCORE: " + score + "\nLEVEL: " + stats.getLevel() + "\nHP: " + stats.getHp() + " / " + stats.getHpMax() + "\nCOINS: " + inventory.getCoins(), 20, 700);
        inventory.render(batch, font);
    }

    public void consume(PowerUp p) {
        switch (p.getType()) {
            case COINS:
                int amount = MathUtils.random(1, 3);
                gs.getInfoController().setup(position.x, position.y, "Coins +" + amount, Color.YELLOW);
                inventory.addCoins(amount);
                break;
            case MEDKIT:
                inventory.add(new Potion("HP Bottle", Potion.Type.HP, MathUtils.random(15, 25)));
                break;
            case WEAPON:
                int minDmg = MathUtils.random(1, p.getLevel());
                int maxDmg = MathUtils.random(2, p.getLevel() * 2);
                for (int i = 0; i < 10; i++) {
                    if (MathUtils.random() < 0.03f) {
                        maxDmg += MathUtils.random(0, p.getLevel());
                    }
                }
                if (maxDmg < minDmg) {
                    maxDmg = minDmg;
                }
                inventory.add(new Weapon("Sword", 0.5f, minDmg, maxDmg));
                break;
        }
        p.deactivate();
    }

    public void attack() {
        if (attackTime > weapon.getAttackPeriod()) {
            attackTime = 0.0f;
            tmp.set(position).add(direction.getX() * 60, direction.getY() * 60);
            gs.getEffectController().setup(tmp.x, tmp.y, 0);
            // soundSwordSwipe.play();
            for (int i = 0; i < gs.getMonsterController().getActiveList().size(); i++) {
                Monster m = gs.getMonsterController().getActiveList().get(i);
                if (m.getArea().contains(tmp)) {
                    m.takeDamage(this, BattleCalc.calculateDamage(this, m), Color.WHITE);
                    break;
                }
            }
        }
    }
}
