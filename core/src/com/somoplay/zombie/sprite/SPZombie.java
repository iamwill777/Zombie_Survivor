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

public class SPZombie extends Sprite {

    private Texture textureZombie;
    private int zType;
    private Texture textureHpBar;
    private Rectangle hitBox;
    private Integer mHealth;
    private int mIndex;
    private int zombieDamage;
    private int speed;
    // 0 for no drop, 1 for health back, 2 for extra damage
    private int zombieDrop;
    private ArrayList<SPSlime> slimes = new ArrayList<SPSlime>();

    private Animation animation;
    private float timePassed = 0;
    private float faceRight = 1; //faceRight == 1; faceLeft == -1;
    private boolean canMove = true;
    private boolean isAlive = true;


    //Enemy State;
    public enum State{
        MOVE_RIGHT,
        MOVE_LEFT,
        ATTACK
    };
    private State currentState;
    private State previousState;


    public SPZombie(int index, int type, float x, float y, int health, int damage, int drop, int zSpeed) {
        mIndex = index;
        zType = type;
        textureZombie = SPAssetManager.getInstance().getZombie();
        textureHpBar = SPAssetManager.getInstance().getHealthBar();

        if (type == 0)
            hitBox = new Rectangle(x, y, 100, 100);
        else if (type == 1)
            hitBox = new Rectangle(x, y, 50, 100);
        mHealth = health;
        zombieDamage = damage;
        zombieDrop = drop;
        speed = zSpeed;
        if (type == 0)
            animation = SPAssetManager.getInstance().getWalkAnimation();
        else if (type == 1)
            animation = SPAssetManager.getInstance().getAniZombieRight();
        currentState = State.MOVE_RIGHT;
        previousState = State.MOVE_RIGHT;
    }

    public void draw(SpriteBatch spritebatch) {
        //Draw enemy;
        spritebatch.draw(
                (TextureRegion) animation.getKeyFrame(timePassed), //TextureRegion
                hitBox.getX(),hitBox.getY(),                       //Position
                hitBox.getWidth()/2,hitBox.getHeight()/2,          //Origin
                hitBox.getWidth(),hitBox.getHeight(),              //Size
                faceRight,1,0                                      //Scale
        );
        timePassed = currentState ==previousState? timePassed+=Gdx.graphics.getDeltaTime():0;
        previousState = currentState;

        //Draw Hp bar;
        if (zType == 0)
            spritebatch.draw(textureHpBar,hitBox.getX()+10,hitBox.getY()+100,20*(mHealth/5.f),10);
        else if (zType == 1)
            spritebatch.draw(textureHpBar,hitBox.getX()-18,hitBox.getY()+100,20*(mHealth/5.f),10);
        // Draws slime
        for (int i = 0; i < slimes.size(); i++){
            if (!slimes.get(i).isAlive())
                slimes.remove(i);
            else
                slimes.get(i).draw(spritebatch);
        }

    }

    public void update(float delta, SPPlayer player, boolean isConnected) {

        switch (currentState){
            case MOVE_LEFT:
                faceRight = -1;
                canMove = true;
                if (zType == 0)
                    animation = SPAssetManager.getInstance().getWalkAnimation();
                else if (zType == 1)
                    animation = SPAssetManager.getInstance().getAniZombieRight();
                break;
            case MOVE_RIGHT:
                faceRight = 1;
                canMove = true;
                if (zType == 0)
                    animation = SPAssetManager.getInstance().getWalkAnimation();
                else if (zType == 1)
                    animation = SPAssetManager.getInstance().getAniZombieRight();
                break;
            case ATTACK:
                canMove = false;
                if (zType == 0)
                    animation = SPAssetManager.getInstance().getAttackAnimation();
                else if (zType == 1)
                    animation = SPAssetManager.getInstance().getAniZombieAttack();
                break;
        }


        if(canMove) {
            chasePlayer(player);
        }

        OverlapPlayer(player,delta);

    }
    public void updateSlime(SPPlayer player){
        for (SPSlime slime:slimes){
            slime.update(Gdx.graphics.getDeltaTime());
            if (player.getHitBox().overlaps(slime.getHitBox())){
                player.Hit(zombieDamage);
                slime.setDead();
            }
            double originX = hitBox.getX();
            double originY = hitBox.getY();
            double slimeX = slime.getHitBox().getX();
            double slimeY = slime.getHitBox().getY();
            if (Math.abs(originX - slimeX) >= 400 || slimeX <= 0.0f || slimeX >= SPMap.width * 15)
                slime.setDead();
            if (Math.abs(originY - slimeY) >= 400 || slimeY <= 0.0f || slimeY >= SPMap.width * 15)
                slime.setDead();
        }
    }
    public void addSlime(float x, float y, float angle){
        slimes.add(new SPSlime(x, y, angle));
    }

    private void OverlapPlayer(SPPlayer player, float delta) {
        if (zType == 0){
            if (direction(player) < 200){
                    switch (currentState) {
                        case MOVE_LEFT:
                            currentState = State.ATTACK;
                            break;
                        case MOVE_RIGHT:
                            currentState = State.ATTACK;
                            break;
                        case ATTACK:
                            if (animation.isAnimationFinished(timePassed)) {
                                addSlime(hitBox.getX() + 30, hitBox.getY() + 30, calculateAngle(player));
                                if (faceRight == -1 && player.getHitBox().getX() > hitBox.getX())
                                    faceRight = 1;
                                if (faceRight == 1 && player.getHitBox().getX() < hitBox.getX())
                                    faceRight = -1;
                                timePassed = 0;
                            }
                            break;
                    }
                } else {
                    if (currentState == State.ATTACK && animation.isAnimationFinished(timePassed)) {
                        currentState = State.MOVE_RIGHT;
                    }
            }
        }
        else if (zType == 1) {
            if (hitBox.overlaps(player.getHitBox())) {
                switch (currentState) {
                    case MOVE_LEFT:
                        currentState = State.ATTACK;
                        break;
                    case MOVE_RIGHT:
                        currentState = State.ATTACK;
                        break;
                    case ATTACK:
                        if (animation.isAnimationFinished(timePassed)) {
                            player.Hit(getDamage());
                            timePassed = 0;
                        }
                        break;
                }
            } else {
                if (currentState == State.ATTACK && animation.isAnimationFinished(timePassed)) {
                    currentState = State.MOVE_RIGHT;
                }
            }
        }
    }

    private void chasePlayer(SPPlayer player) {
        Vector2 direction = calculateDirection(player);

        if (direction.x<0){
            currentState = State.MOVE_LEFT;
        }else {
            currentState = State.MOVE_RIGHT;
        }

        hitBox.x += (speed * direction.x);
        hitBox.y += (speed * direction.y);
    }

    private Vector2 calculateDirection(SPPlayer player) {
        float pX;
        float pY;
        float dx;
        float dy;

        pX = player.getX();
        pY = player.getY();
        dx = pX - hitBox.x;
        dy = pY - hitBox.y;

        double length = Math.sqrt(dx * dx + dy * dy);

        Vector2 normalizedDirection = new Vector2(dx / (float) length, (float) (dy / length));

        return normalizedDirection;
    }
    private float direction(SPPlayer player){
        float length = (float)(Math.sqrt(Math.pow(player.getX() - hitBox.x, 2) + Math.pow(player.getY() - hitBox.y, 2)));
        return length;
    }
    private float calculateAngle(SPPlayer player){
        float angle = (float)Math.toDegrees(Math.atan2(player.getY() - hitBox.getY(), (player.getX() - hitBox.getX())));
        return angle;
    }

    public void hit(int damage){
        mHealth-=damage;
    }

    public Integer getHealth(){
        return mHealth;
    }
    public Rectangle getHitBox() {
        return hitBox;
    }
    public void setHitBox(float x, float y) { hitBox.setX(x); hitBox.setY(y); }
    public int getMonsterIndex() { return mIndex; }
    public void setDead() { isAlive = false; }
    public boolean isAlive() { return isAlive; }
    public int getDamage() {return zombieDamage;}
    public int getDrop(){return zombieDrop;}
    public int getType(){return zType;}
}