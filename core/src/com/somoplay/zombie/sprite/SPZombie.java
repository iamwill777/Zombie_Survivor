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

/**
 * Created by yaolu on 2017-06-15.
 */

public class SPZombie extends Sprite {

    private Texture textureZombie;
    private Texture textureHpBar;
    private Rectangle hitBox;
    private Integer health;


    private Animation animation;
    private float timePassed = 0;
    private float faceRight = 1; //faceRight == 1; faceLeft == -1;
    private boolean canMove = true;


    //Enemy State;
    public enum State{
        MOVE_RIGHT,
        MOVE_LEFT,
        ATTACK
    };
    private State currentState;
    private State previousState;


    public SPZombie(float x, float y) {
        textureZombie = SPAssetManager.getInstance().getZombie();
        textureHpBar = SPAssetManager.getInstance().getHealthBar();
        hitBox = new Rectangle(x, y, 50, 100);
        health = 5;

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
        timePassed = currentState ==previousState? timePassed+= Gdx.graphics.getDeltaTime():0;
        previousState = currentState;

        //Draw Hp bar;
        spritebatch.draw(textureHpBar,hitBox.getX()-18,hitBox.getY()+100,80*(health/5.f),10);

    }


    public void update(float delta,SPPlayer player) {

        switch (currentState){
            case MOVE_LEFT:
                faceRight = -1;
                canMove = true;
                animation = SPAssetManager.getInstance().getAniZombieRight();
                break;
            case MOVE_RIGHT:
                faceRight = 1;
                canMove = true;
                animation = SPAssetManager.getInstance().getAniZombieRight();
                break;
            case ATTACK:
                canMove = false;
                animation = SPAssetManager.getInstance().getAniZombieAttack();
                break;
        }


        if(canMove) {
            chasePlayer(player);
        }

        OverlapPlayer(player,delta);

    }

    private void OverlapPlayer(SPPlayer player, float delta) {

        if(hitBox.overlaps(player.getHitBox())
            //Math.abs(hitBox.x-player.getX())<=20&&
            //Math.abs(hitBox.y-player.getY())<=25
                ){
            switch (currentState){
                case MOVE_LEFT:
                    currentState = State.ATTACK;
                    break;
                case MOVE_RIGHT:
                    currentState = State.ATTACK;
                    break;
                case ATTACK:
                    if(animation.isAnimationFinished(timePassed)){
                        player.Hit(10);
                        timePassed = 0;
                    }
                    break;
            }
        }else {
            if(currentState ==State.ATTACK&&animation.isAnimationFinished(timePassed)){
                currentState = State.MOVE_RIGHT;
            }
        }
    }

    private void chasePlayer(SPPlayer player) {
        Vector2 direction = caculateDirection(player);

        if (direction.x<0){
            currentState = State.MOVE_LEFT;
        }else {
            currentState = State.MOVE_RIGHT;
        }

        hitBox.x += direction.x;
        hitBox.y += direction.y;
    }

    private Vector2 caculateDirection(SPPlayer player) {
        float pX;
        float pY;
        float dx;
        float dy;

        pX = player.getX();
        pY = player.getY();
        dx = pX-hitBox.x;
        dy = pY-hitBox.y;

        double length = Math.sqrt(dx*dx+dy*dy);

        Vector2 normalizedDirection = new Vector2(dx/(float)length, (float) (dy/length));

        return normalizedDirection;
    }

    public void hit(int damage){
        health-=damage;
    }

    public Integer getHealth(){
        return health;
    }
    public Rectangle getHitBox() {
        return hitBox;
    }


}
