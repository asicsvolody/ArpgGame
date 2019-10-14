package com.arpg.game.units;

import com.arpg.game.*;
import com.arpg.game.armory.Weapon;
import com.arpg.game.utils.Direction;
import com.arpg.game.utils.Poolable;
import com.arpg.utils.Assets;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;

public class Monster extends Unit implements Poolable {
    public enum State {
        HUNT, IDLE, WALK
    }

    private State state;
    private Unit target;
    private String title;
    private float aiTimer;

    public String getTitle() {
        return title;
    }

    @Override
    public boolean isActive() {
        return stats.getHp() > 0;
    }

    public Monster(GameController gameController) {
        super(gameController);
        this.stats = new Stats();
        this.weapon = new Weapon("Bite", Weapon.Type.MELEE, 0.8f, 90.0f, 2, 5);
    }

    // ___title________,__base_att__,__base_def__,__base_hp__,__att_pl__,__def_pl__,__hp_pl__,__speed__
    public Monster(String line) {
        super(null);
        String[] tokens = line.split(",");
        this.title = tokens[0].trim();
        this.texture = new TextureRegion(Assets.getInstance().getAtlas().findRegion(title)).split(80, 80);
        this.stats = new Stats(
                0,
                Integer.parseInt(tokens[1].trim()),
                Integer.parseInt(tokens[2].trim()),
                Integer.parseInt(tokens[3].trim()),
                Integer.parseInt(tokens[4].trim()),
                Integer.parseInt(tokens[5].trim()),
                Integer.parseInt(tokens[6].trim()),
                Float.parseFloat(tokens[7].trim())
        );
        this.weapon = new Weapon("Bite", Weapon.Type.MELEE, 0.8f, 90.0f, 2, 5);
    }

    public void setup(int level, float x, float y, Monster pattern) {
        this.stats.set(level, pattern.stats);
        this.title = pattern.title;
        this.texture = pattern.texture;
        if (x < 0 && y < 0) {
            this.gc.getMap().setRefVectorToEmptyPoint(position);
        } else {
            this.position.set(x, y);
        }
        this.area.setPosition(position);
        if (pattern.getTitle().equals("Tiger")) {
            weapon = new Weapon("Bite", Weapon.Type.MELEE, 0.8f, 90.0f, 2, 5);
        } else {
            weapon = new Weapon("Bow", Weapon.Type.RANGED, 0.6f, 500.0f, 3, 8);
        }
    }

    @Override
    public void update(float dt) {
        aiTimer -= dt;
        attackTime += dt;

        if (damageTimer > 0.0f) {
            damageTimer -= dt;
        }

        if (aiTimer <= 0.0f) {
            state = State.values()[MathUtils.random(1, 2)]; // IDLE or WALK
            aiTimer = MathUtils.random(2.0f, 4.0f);
            if (state == State.IDLE) {
                aiTimer /= 4.0f;
            }
            direction = Direction.values()[MathUtils.random(0, 3)];
        }

        if (state == State.HUNT && position.dst(target.getPosition()) > weapon.getAttackRange() * 1.2f) {
            if (Math.abs(target.getPosition().x - this.position.x) > 30.0f) {
                if (target.getPosition().x > this.position.x) {
                    direction = Direction.RIGHT;
                }
                if (target.getPosition().x < this.position.x) {
                    direction = Direction.LEFT;
                }
            }
            if (Math.abs(target.getPosition().y - this.position.y) > 30.0f) {
                if (target.getPosition().y > this.position.y) {
                    direction = Direction.UP;
                }
                if (target.getPosition().y < this.position.y) {
                    direction = Direction.DOWN;
                }
            }
        }

        if (state != State.IDLE) {
            tmp.set(position).add(direction.getX() * stats.getSpeed() * dt, direction.getY() * stats.getSpeed() * dt);
            if (gc.getMap().isCellPassable(tmp)) {
                position.set(tmp);
                walkTimer += dt;
                area.setPosition(position);
            }

            tryToAttack();
        }
    }

    @Override
    public void takeDamage(Unit attacker, int amount, Color color) {
        super.takeDamage(attacker, amount, color);
        if (MathUtils.random(0, 100) < 20) {
            stateToHunt(attacker);
        }
    }

    public void stateToHunt(Unit target) {
        this.state = State.HUNT;
        this.target = target;
        this.aiTimer = 15.0f;
    }

    @Override
    public void render(SpriteBatch batch, BitmapFont font) {
        super.render(batch, font);
        font.draw(batch, state.name(), position.x + 20, position.y + 60);
    }

    public void tryToAttack() {
        if (attackTime > weapon.getAttackPeriod()) {
            attackTime = 0.0f;

            if (weapon.getType() == Weapon.Type.MELEE && target != null) {
                float dst = position.dst(target.getPosition());
                if (dst <= weapon.getAttackRange()) {
                    if (direction == Direction.LEFT && position.x > target.getPosition().x ||
                            direction == Direction.RIGHT && position.x < target.getPosition().x ||
                            direction == Direction.UP && position.y < target.getPosition().y ||
                            direction == Direction.DOWN && position.y > target.getPosition().y) {
                        gc.getEffectController().setup(target.getPosition().x, target.getPosition().y, 1);
                        target.takeDamage(this, BattleCalc.calculateDamage(this, target), Color.RED);
                    }
                }
            }
            if (weapon.getType() == Weapon.Type.RANGED) {
                gc.getProjectileController().setup(this, position.x, position.y + 15, 400.0f, 0, weapon.getAttackRange(), direction.getAngle() + MathUtils.random(-10, 10));
            }
        }
    }
}