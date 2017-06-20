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

                if (movingPositionArrayList.size() >= 1) {
                    posPrev = movingPositionArrayList.get(0);
                }
                else {
                    spritebatch.draw((TextureRegion) mAnimation.getKeyFrame(mTimePassed, true), getX(), getY());
                    posPrev = null;
                }

                if (posPrev != null) {
                    changeDirection(posPrev.fAngle);

                    float dx = posPrev.fX - getX();
                    float dy = posPrev.fY - getY();
                    double length = Math.sqrt(dx * dx + dy * dy);
                    if (length <= 0.5) {
                        movingPositionArrayList.remove(0);
                    }
                    else {
                        //float delta = Gdx.graphics.getDeltaTime();
                        Vector2 normal = new Vector2(dx / (float) length, dy / (float) length);
                        float x = getX();
                        x += normal.x;
                        float y = getY();
                        y += normal.y;

                        setX(x);
                        setY(y);

                        spritebatch.draw((TextureRegion) mAnimation.getKeyFrame(mTimePassed, true), getX(), getY());
                        Gdx.app.log("Moving Position: ", "PrevPos X : " + posPrev.fX + ", Y: " + posPrev.fY + ", length: " + length);
                        Gdx.app.log("Moving Position: ", "CurrPos X : " + getX() + ", Y: " + getY() + ", Angle: " + posPrev.fAngle);
                        Gdx.app.log("Moving Position: ", "MovingTime : " + mMovingTime + ", TimePassed: " + mTimePassed);
                    }
                }
            }
        }

        for(SPBullet bullet: bulletArrayList) {
            bullet.draw(spritebatch);
        }

        hpBar.setPosition(getX()+15,getY()+115);
        hpBarBorder.setPosition(getX()-240,getY()+19);
        hpBar.setScale(0.23f*(health/100.f),0.13f);

        hpBar.draw(spritebatch);
        hpBarBorder.draw(spritebatch);
    }

    public Boolean updateBullets(ArrayList<SPZombie> lstZombie) {
        for(SPBullet bullet: bulletArrayList) {
            bullet.update(Gdx.graphics.getDeltaTime());

            // check to collide any zombies
            for(SPZombie zombie: lstZombie) {
                if(zombie.getHitBox().overlaps(bullet.getHitBox())) {
                    zombie.hit(weaponPower);
                    if (zombie.getHealth()<=0) {
                        lstZombie.remove(zombie);
                        bulletArrayList.remove(bullet);
                        return true;
                    }
                    bulletArrayList.remove(bullet);
                    return false;
                }
            }

            // check bounds and remove it in bulletArrayList
            float bottomLeftX = 0.0f, bottomLeftY = 0.0f, topRightX = (float) SPMap.width*15, topRightY = (float) SPMap.height*15;
            if (bullet.getHitBox().getX() <= bottomLeftX || bullet.getHitBox().getX() >= topRightX) {
                bulletArrayList.remove(bullet);
                return false;
            }
            else if (bullet.getHitBox().getY() <= bottomLeftY || bullet.getHitBox().getY() >= topRightY) {
                bulletArrayList.remove(bullet);
                return false;
            }
        }
        return false;
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
}
