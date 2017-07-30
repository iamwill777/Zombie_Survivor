package com.somoplay.zombie.sprite;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.somoplay.zombie.asset.SPAssetManager;
/**
 * Created by William Ruan on 2017-07-29.
 */

public class SPSlime {
    Rectangle hitBox;
    float angle;
    int speed;
    boolean mIsAlive;

    public SPSlime(float x, float y, float angle) {
        mIsAlive = true;
        this.angle = angle;
        speed = 150;
        hitBox = new Rectangle(x, y, 32, 32);
    }

    public Rectangle getHitBox() {
        return hitBox;
    }

    public void update(float delta) {
        hitBox.x += speed * (float)Math.cos(angle*Math.PI/180) * delta;
        hitBox.y += speed * (float)Math.sin(angle*Math.PI/180) * delta;
    }

    public void setDead() {
        mIsAlive = false;
    }

    public boolean isAlive() {
        return mIsAlive;
    }
    public void draw(SpriteBatch batch) {
        batch.draw(SPAssetManager.getInstance().getSlime(), hitBox.x, hitBox.y);
    }
}
