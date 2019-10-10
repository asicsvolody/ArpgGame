/**
 * Created by IntelliJ Idea.
 * User: Якимов В.Н.
 * E-mail: yakimovvn@bk.ru
 */

package com.arpg.game;

import com.arpg.game.utils.ObjectPool;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;

public class PowerUpsController extends ObjectPool<PowerUp> {
    @Override
    protected PowerUp newObject() {
        return new PowerUp();
    }

    private TextureRegion[][] texture;

    public PowerUpsController() {
        this.texture = new TextureRegion(Assets.getInstance().getAtlas().findRegion("powerUps")).split(30, 30);
    }

    public void setup(float x, float y, float probability, int count, int level) {
        for (int i = 0; i < count; i++) {
            if (MathUtils.random() <= probability) {
                getActiveElement().setup(x, y, level);
            }
        }
    }

    public void render(SpriteBatch batch) {
        for (int i = 0; i < activeList.size(); i++) {
            PowerUp p = activeList.get(i);
            batch.draw(texture[p.getType().index][0], p.getPosition().x - 15, p.getPosition().y - 15, 15, 15, 30, 30, 2, 2, 0);
        }
    }

    public void update(float dt) {
        for (int i = 0; i < activeList.size(); i++) {
            activeList.get(i).update(dt);
        }
        checkPool();
    }
}

