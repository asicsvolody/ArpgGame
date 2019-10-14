package com.arpg.game.units;

import com.arpg.game.*;
import com.arpg.game.armory.Weapon;
import com.arpg.game.map.MapElement;
import com.arpg.game.utils.Direction;
import com.arpg.utils.Assets;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;

public abstract class Unit implements MapElement {
    protected GameController gc;
    protected TextureRegion[][] texture;
    protected TextureRegion hpTexture;
    protected Vector2 position;
    protected Direction direction;
    protected Vector2 tmp;
    protected Circle area;
    protected Stats stats;
    protected float damageTimer;
    protected Weapon weapon;
    protected float attackTime;
    protected float walkTimer;
    protected float timePerFrame;

    public Stats getStats() {
        return stats;
    }

    public Weapon getWeapon() {
        return weapon;
    }

    @Override
    public int getCellX() {
        return (int) (position.x / 80);
    }

    @Override
    public int getCellY() {
        return (int) (position.y / 80);
    }

    public Vector2 getPosition() {
        return position;
    }

    public Direction getDirection() {
        return direction;
    }

    public Circle getArea() {
        return area;
    }

    public Unit(GameController gameController) {
        this.gc = gameController;
        this.hpTexture = Assets.getInstance().getAtlas().findRegion("monsterHp");
        this.position = new Vector2(0.0f, 0.0f);
        this.area = new Circle(0, 0, 32);
        this.tmp = new Vector2(0.0f, 0.0f);
        this.timePerFrame = 0.1f;
        this.direction = Direction.DOWN;
    }

    public void takeDamage(Unit attacker, int amount, Color color) {
        stats.decreaseHp(amount);
        damageTimer = 1.0f;
        gc.getInfoController().setup(position.x, position.y + 30, "-" + amount, color);
        if (stats.getHp() <= 0) {
            int exp = BattleCalc.calculateExp(attacker, this);
            attacker.getStats().addExp(exp);
            if (attacker instanceof Monster) {
                attacker.getStats().fillHp();
            }
            gc.getInfoController().setup(attacker.getPosition().x, attacker.getPosition().y + 40, "exp +" + exp, Color.YELLOW);
            gc.getPowerUpsController().setup(position.x, position.y, 1.2f, 2, stats.getLevel());
        }
    }

    public TextureRegion getCurrentTexture() {
        return texture[direction.getImageIndex()][(int) (walkTimer / timePerFrame) % texture[direction.getImageIndex()].length];
    }

    public void render(SpriteBatch batch, BitmapFont font) {
        if (damageTimer > 0.0f) {
            batch.setColor(1.0f, 1.0f - damageTimer, 1.0f - damageTimer, 1.0f);
        }
        batch.draw(getCurrentTexture(), position.x - 40, position.y - 20);
        if (stats.getHp() < stats.getHpMax()) {
            batch.setColor(1.0f, 1.0f, 1.0f, 0.9f);
            batch.draw(hpTexture, position.x - 40, position.y + 40, 80 * ((float) stats.getHp() / stats.getHpMax()), 12);
        }
        batch.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        font.draw(batch, "" + stats.getLevel(), position.x, position.y + 50);
    }

    public abstract void update(float dt);
}