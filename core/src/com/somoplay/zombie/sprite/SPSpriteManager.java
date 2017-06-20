package com.somoplay.zombie.sprite;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.somoplay.zombie.main.SPGameScene;
import com.somoplay.zombie.scene.SPMap;
import com.somoplay.zombie.scene.SPMapConstant;
import com.somoplay.zombie.ui.SPJoyStick;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by yaolu on 2017-06-15.
 */

public class SPSpriteManager {

    private SPPlayer mPlayer;
    private ArrayList<SPPlayer> mlstPlayers;       // for other players
    private ArrayList<SPZombie> mlstZombie;
    private Random random;

    public SPSpriteManager(SPGameScene main) {
        mPlayer = new SPPlayer(true, main.UserName, 0, 0);       // for mSPain player
        mlstZombie = new ArrayList<SPZombie>();
        mlstPlayers = new ArrayList<SPPlayer>();
        random = new Random();

        for (int i=0; i<3; i++) {
            addZombie(Math.abs(random.nextInt() % SPMap.width * SPMapConstant.TILESIZE), Math.abs(random.nextInt() % SPMap.height * SPMapConstant.TILESIZE));
        }
    }

    public void addZombie(float fX, float fY) {
        mlstZombie.add(new SPZombie(fX, fY));
    }

    public void addPlayers(SPPlayer newPlayer) {
        if (newPlayer.getUserName() != mPlayer.getUserName())
            mlstPlayers.add(newPlayer);
    }

    public void render(OrthographicCamera camera, SpriteBatch batch) {
        for(SPZombie zombie: mlstZombie) {
            zombie.draw(batch);
        }

        mPlayer.draw(batch);

        for(SPPlayer player: mlstPlayers) {
            player.draw(batch);
        }
    }

    public SPPlayer getPlayer() {
        return mPlayer;
    }

    public void setPlayerPosition(String username, float X, float Y, float angle) {

        for(SPPlayer player: mlstPlayers) {
            if (player.getUserName().equals(username)) {
                player.addMovingPosition(X, Y, angle);
                break;
            }
        }
    }

    public void setPlayPositionLEFT(SPJoyStick stick) {
        mPlayer.setX(mPlayer.getX()-1);
        if (!stick.bIsShooting())
            mPlayer.changeDirection(180);
        else
            mPlayer.changeDirection(mPlayer.getShootingAngle());
    }

    public void setPlayPositionRIGHT(SPJoyStick stick) {
        mPlayer.setX(mPlayer.getX()+1);
        if (!stick.bIsShooting())
            mPlayer.changeDirection(0);
        else
            mPlayer.changeDirection(mPlayer.getShootingAngle());
    }

    public void setPlayPositionDOWN(SPJoyStick stick) {
        mPlayer.setY(mPlayer.getY()-1);
        if (!stick.bIsShooting())
            mPlayer.changeDirection(270);
        else
            mPlayer.changeDirection(mPlayer.getShootingAngle());
    }

    public void setPlayPositionUP(SPJoyStick stick) {
        mPlayer.setY(mPlayer.getY()+1);
        if (!stick.bIsShooting())
            mPlayer.changeDirection(90);
        else
            mPlayer.changeDirection(mPlayer.getShootingAngle());
    }

    public void updateBullets() {
        if (mPlayer.updateBullets(mlstZombie)) {
            //addZombie();
        }
    }

    public void updateEnemy(float dt) {
        for(SPZombie zombie: mlstZombie){
            zombie.update(dt, mPlayer);
        }
    }

    public ArrayList<SPZombie> getZombie() {
        return mlstZombie;
    }
}
