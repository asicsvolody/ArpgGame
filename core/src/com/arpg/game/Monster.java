package com.arpg.game;

import com.arpg.game.utils.Poolable;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Shape2D;
import com.badlogic.gdx.math.Vector2;

public class Monster extends Unit implements Poolable {
    public enum State {
        HUNT, IDLE, WALK
    }

    private State state;
    private Unit target;
    private String title;
    private float aiTimer;
    private float aiTimerTo;

    public String getTitle() {
        return title;
    }

    @Override
    public boolean isActive() {
        return stats.getHp() > 0;
    }

    public Monster(GameScreen gameScreen) {
        super(gameScreen);
        this.stats = new Stats();
        this.weapon = new Weapon("Bite", 0.8f, 2, 5);
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
        this.weapon = new Weapon("Bite", 0.8f, 2, 5);
    }

    public void setup(int level, float x, float y, Monster pattern) {
        this.stats.set(level, pattern.stats);
        this.title = pattern.title;
        this.texture = pattern.texture;
        if (x < 0 && y < 0) {
            this.gs.getMap().setRefVectorToEmptyPoint(position);
        } else {
            this.position.set(x, y);
        }
        this.area.setPosition(position);
    }

    @Override
    public void update(float dt) {
        aiTimer += dt;
        attackTime += dt;

        if (damageTimer > 0.0f) {
            damageTimer -= dt;
        }

        if (aiTimer > aiTimerTo) {
            state = State.values()[MathUtils.random(1, 2)]; // IDLE or WALK
            aiTimer = 0.0f;
            aiTimerTo = MathUtils.random(2.0f, 4.0f);
            if (state == State.IDLE) {
                aiTimerTo /= 4.0f;
            }
            direction = Direction.values()[MathUtils.random(0, 3)];
        }

        if (state == State.HUNT) {
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
            if (gs.getMap().isCellPassable(tmp)) {
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
        this.aiTimerTo = 15.0f;
    }

    @Override
    public void render(SpriteBatch batch, BitmapFont font) {
        super.render(batch, font);
        font.draw(batch, state.name(), position.x + 20, position.y + 60);
    }

    public void tryToAttack() {
        if (attackTime > weapon.getAttackPeriod()) {
            attackTime = 0.0f;
            tmp.set(position).add(direction.getX() * 60, direction.getY() * 60);
            if (gs.getHero().getArea().contains(tmp)) {
                gs.getEffectController().setup(tmp.x, tmp.y, 1);
                gs.getHero().takeDamage(this, BattleCalc.calculateDamage(this, gs.getHero()), Color.RED);
            }
        }
    }
}