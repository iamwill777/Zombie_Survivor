package com.somoplay.zombie.sprite;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.somoplay.zombie.asset.SPAssetManager;

/**
 * Created by yaolu on 2017-06-15.
 */

public class SPBullet {
    Rectangle hitBox;
    float angle;
    float time;
    int speed;
    boolean mIsAlive;
    boolean upgraded = false;

    public SPBullet(float x, float y, float angle) {
        mIsAlive = true;
        this.angle = angle;
        time = 2;
        speed = 300;
        hitBox = new Rectangle(x, y, 10, 10); //needImprove why hitbox size(10 10) different from draw() 50 50
    }

    public Rectangle getHitBox() {
        return hitBox;
    }

    public void update(float delta) {
        hitBox.x += speed * (float)Math.cos(angle*Math.PI/180) * delta;
        hitBox.y += speed * (float)Math.sin(angle*Math.PI/180) * delta;
        time -= delta;
    }

    public void setDead() {
        mIsAlive = false;
    }

    public boolean isAlive() {
        return mIsAlive;
    }
    public void draw(SpriteBatch batch) {
        if (!upgraded)
            batch.draw(SPAssetManager.getInstance().getBullet(), hitBox.x, hitBox.y, 50, 50);
        else
            batch.draw(SPAssetManager.getInstance().getUpgradedBullet(), hitBox.x, hitBox.y, 50, 50);
    }
    public void setUpgrade(){upgraded = true;}
}