package com.somoplay.zombie.sprite;

/**
 * Created by William Ruan on 2017-07-28.
 */
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.somoplay.zombie.asset.SPAssetManager;

public class SPItem {
    Rectangle hitBox;
    private boolean isThere;
    private Texture healthPack;
    private Texture playerDamage;
    private int drop;

    public SPItem(SPZombie zombieDrop){
        isThere = true;
        healthPack = SPAssetManager.getInstance().getHealthPack();
        playerDamage = SPAssetManager.getInstance().getPlayerDamage();
        hitBox = new Rectangle(zombieDrop.getHitBox().getX(), zombieDrop.getHitBox().getY() + 50, 48, 48);
        drop = zombieDrop.getDrop();
    }
    public void drawItem(SpriteBatch batch){
        if (drop == 1)
            batch.draw(healthPack, hitBox.x, hitBox.y, hitBox.getWidth(), hitBox.getHeight());
        else if (drop == 2)
            batch.draw(playerDamage, hitBox.x, hitBox.y, hitBox.getWidth(), hitBox.getHeight());
    }
    public Rectangle getHitBox(){
        return hitBox;
    }
    public int getDrop(){
        return drop;
    }
    public void setDead(){
        isThere = false;
    }
    public boolean isItemThere(){
        return isThere;
    }
}
