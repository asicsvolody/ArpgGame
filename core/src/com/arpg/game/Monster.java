package com.arpg.game;
import com.arpg.game.utils.Poolable;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class Monster extends Unit implements Poolable {
    private String title;
    private float aiTimer;
    private float aiTimerTo;

    //HT
    private boolean isFury = true;

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

        if (damageTimer > 0.0f && !isFury) {
            damageTimer -= dt;
        }
        if(damageTimer > 0.0f && isFury){
            damageTimer -= dt/15;
        }else {
            isFury = false;
        }


        if (aiTimer > aiTimerTo && !isFury) {
            aiTimer = 0.0f;
            aiTimerTo = MathUtils.random(2.0f, 4.0f);
            direction = Direction.values()[MathUtils.random(0, 3)];
        }


        if(isFury) {
            Vector2 vec = new Vector2(gs.getHero().position.x - this.position.x, gs.getHero().position.y - this.position.y).nor();
            tmp.set(position).add(vec.x * stats.getSpeed() * dt, vec.y * stats.getSpeed() * dt);
        }else{
            tmp.set(position).add(direction.getX() * stats.getSpeed() * dt, direction.getY() * stats.getSpeed() * dt);

        }

        if (gs.getMap().isCellPassable(tmp)) {
            position.set(tmp);
            walkTimer += dt;
            area.setPosition(position);
        }

        tryToAttack();
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

    public void setFury(boolean fury) {
        int pros = MathUtils.random(0, 5);
        if(pros == 2){
            isFury = fury;
        }
    }


    //    public void takeDamage(Unit attacker, int amount, Color color) {
//        stats.decreaseHp(amount);
//        damageTimer = 1.0f;
//        gs.getInfoController().setup(position.x, position.y + 30, "-" + amount, color);
//        if (stats.getHp() <= 0) {
//            int exp = BattleCalc.calculateExp(attacker, this);
//            attacker.getStats().addExp(exp);
//            gs.getInfoController().setup(attacker.getPosition().x, attacker.getPosition().y + 40, "exp +" + exp, Color.YELLOW);
//        }
//    }

//    public void render(SpriteBatch batch, BitmapFont font) {
//        if (damageTimer > 0.0f) {
//            batch.setColor(1.0f, 1.0f - damageTimer, 1.0f - damageTimer, 1.0f);
//        }
//
//        if(dangerous >= 0.0f){
//            batch.setColor(1.0f, 1.0f - dangerous, 1.0f - dangerous, 1.0f);
//        }
//        batch.draw(getCurrentTexture(), position.x - 40, position.y - 20);
//
//        if (stats.getHp() < stats.getHpMax()) {
//            batch.setColor(1.0f, 1.0f, 1.0f, 0.9f);
//            batch.draw(hpTexture, position.x - 40, position.y + 40, 80 * ((float) stats.getHp() / stats.getHpMax()), 12);
//        }
//        batch.setColor(1.0f, 1.0f, 1.0f, 1.0f);
//        font.draw(batch, "" + stats.getLevel(), position.x, position.y + 50);
//    }
}