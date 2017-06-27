package com.somoplay.zombie.sprite;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.somoplay.zombie.asset.SPAssetManager;
import com.somoplay.zombie.scene.SPMap;
import com.somoplay.zombie.ui.SPJoyStick;

import java.util.ArrayList;

/**
 * Created by yaolu on 2017-06-15.
 */

public class SPPlayer extends Sprite {

    public class MovingPosition {
        public float fX;
        public float fY;
        public float fAngle;

        MovingPosition(float x, float y, float angle) {
            fX = x;
            fY = y;
            fAngle = angle;
        }
    }

    private String mUserName;
    private Animation mAnimation;
    private float mTimePassed = 0;
    private boolean bIsPlayingAnimation = false;
    private ArrayList<SPBullet> bulletArrayList = new ArrayList<SPBullet>();
    private float textureSize = 0.0f;
    private float mSaveShootingAngle = 0.0f;
    private ArrayList<MovingPosition> movingPositionArrayList  = new ArrayList<MovingPosition>();
    private float mMovingTime = 3.0f;
    private boolean mIsMainPlayer = false;
    private float saveShootingAngle = 0.0f;

    private Rectangle hitBox;
    private Integer health;
    private Integer weaponPower;
    private MovingPosition posPrev = null;
    private boolean isAlive = true;

    //Hp Bar
    private Texture hpBarTexture;
    private Texture hpBarBoarderTexture;
    private Sprite hpBar;
    private Sprite hpBarBorder;

    public SPPlayer(boolean isMainPlayer, String username, double posX, double posY) {
        mIsMainPlayer = isMainPlayer;
        mUserName = username;
        mAnimation = SPAssetManager.getInstance().getAniCharRight();

        // just for getting image length
        Texture img1 = new Texture("players/character10/09.png");
        textureSize = img1.getWidth();

        this.setX((float) posX);
        this.setY((float) posY);

        //Debug method is in the "draw()" function;
        health = 100;
        weaponPower = 2;
        hitBox = new Rectangle(getX(),getY(),100.f,100.f);


        hpBarTexture = SPAssetManager.getInstance().getHealthBar();
        hpBarBoarderTexture = SPAssetManager.getInstance().getHealthBarBorder();
        hpBar = new Sprite(hpBarTexture,400,50);
        hpBarBorder = new Sprite(hpBarBoarderTexture);

        hpBar.setOrigin(0,0);
        hpBarBorder.setScale(0.2f,0.2f);
    }

    public void addMovingPosition(float X, float Y, float angle) {
        if (movingPositionArrayList.size() <= 0) {
            movingPositionArrayList.add(new MovingPosition(X, Y, angle));
        }
    }

    public String getUserName() { return mUserName; }
    public float getTextureSize() {
        return textureSize;
    }

    public void AddBullet(float x, float y, float angle) {
        //bulletArrayList.add(new Bullet(getX(), getY(), angle));
        bulletArrayList.add(new SPBullet(x, y, angle));
        //Gdx.app.log("Position: ", "X : " +  x + ", Y: " + y + ", Angle: " + angle);
    }

    public void draw(SpriteBatch spritebatch) {
        //update hitbox's position.
        hitBox.x = getX();
        hitBox.y = getY();

        //Debug purpose.
        if(health<=0){
            health = 100;
        }

        if(bIsPlayingAnimation) {
            mTimePassed += Gdx.graphics.getDeltaTime();
        }

        if (mAnimation == null) {
            mAnimation = SPAssetManager.getInstance().getAniCharRight();
        }
        else {
            if (mIsMainPlayer) {
                spritebatch.draw((TextureRegion) mAnimation.getKeyFrame(mTimePassed, true), getX(), getY());
            }
            else {
                mTimePassed += Gdx.graphics.getDeltaTime();
                if (movingPositionArrayList.size() > 0) {
                    MovingPosition data = movingPositionArrayList.get(0);
                    setX(getX() + data.fX* SPJoyStick.blockSpeed);
                    setY(getY() + data.fY*SPJoyStick.blockSpeed);
                    Vector2 v = new Vector2(data.fX, data.fY);
                    changeDirection(v.angle());
                    movingPositionArrayList.remove(0);
                }
                spritebatch.draw((TextureRegion) mAnimation.getKeyFrame(mTimePassed, true), getX(), getY());
            }
        }

        for(int i=0; i<bulletArrayList.size(); i++) {
            if (!bulletArrayList.get(i).isAlive()) {
                bulletArrayList.remove(i);
            }
            else
                bulletArrayList.get(i).draw(spritebatch);
        }

        hpBar.setPosition(getX()+15,getY()+115);
        hpBarBorder.setPosition(getX()-240,getY()+19);
        hpBar.setScale(0.23f*(health/100.f),0.13f);

        hpBar.draw(spritebatch);
        hpBarBorder.draw(spritebatch);
    }

    public int updateBullets(ArrayList<SPZombie> lstZombie) {
        for(SPBullet bullet: bulletArrayList) {
            bullet.update(Gdx.graphics.getDeltaTime());

            // check to collide any zombies
            for(SPZombie zombie: lstZombie) {
                if(zombie.getHitBox().overlaps(bullet.getHitBox())) {
                    zombie.hit(weaponPower);
                    if (zombie.getHealth()<=0) {
                        bullet.setDead();

                        // send message to server, to kill zombies with index
                        return zombie.getMonsterIndex();
                    }
                    bullet.setDead();
                    return -1;
                }
            }

            // check bounds and remove it in lstBullet
            float bottomLeftX = 0.0f, bottomLeftY = 0.0f, topRightX = (float) SPMap.width*15, topRightY = (float) SPMap.height*15;
            if (bullet.getHitBox().getX() <= bottomLeftX || bullet.getHitBox().getX() >= topRightX) {
                bullet.setDead();
                return -1;
            }
            else if (bullet.getHitBox().getY() <= bottomLeftY || bullet.getHitBox().getY() >= topRightY) {
                bullet.setDead();
                return -1;
            }
        }
        return -1;
    }

    public float getShootingAngle() { return saveShootingAngle; }

    public void changeDirection(float angle) {

        this.saveShootingAngle = angle;

        if (angle < 0) {
            bIsPlayingAnimation = false;
            return;
        }

        bIsPlayingAnimation = true;

        // for example using texture from files
        if (angle >= 25 && angle <= 70) {  // right-down
            mAnimation = SPAssetManager.getInstance().getAniCharRightDown();
        }
        else if (angle >= 70 && angle <= 115) { // down
            mAnimation = SPAssetManager.getInstance().getAniCharDown();
        }
        else if (angle >= 115 && angle <= 160) { // down-left
            mAnimation = SPAssetManager.getInstance().getAniCharDownLeft();
        }
        else if (angle >= 160 && angle <= 205) { // left
            mAnimation = SPAssetManager.getInstance().getAniCharLeft();
        }
        else if (angle >= 205 && angle <= 250) { // left-up
            mAnimation = SPAssetManager.getInstance().getAniCharLeftUp();
        }
        else if (angle >= 250 && angle <= 295) { // up
            mAnimation = SPAssetManager.getInstance().getAniCharUp();
        }
        else if (angle >= 295 && angle <= 340) {  // up-right
            mAnimation = SPAssetManager.getInstance().getAniCharUpRight();
        }
        else {  // right        // for example using atlas
            mAnimation = SPAssetManager.getInstance().getAniCharRight();
        }
    }

    public Integer getHealth(){
        return health;
    }
    public Rectangle getHitBox(){
        return hitBox;
    }

    public void Hit(int i) {
        health-=i;
    }
    public void setDead() { isAlive = false; }
    public boolean isAlive() { return isAlive; }
}
