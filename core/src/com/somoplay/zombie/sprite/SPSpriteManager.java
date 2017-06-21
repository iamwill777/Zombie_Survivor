package com.somoplay.zombie.sprite;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.somoplay.zombie.main.SPGameScene;
import com.somoplay.zombie.scene.SPMap;
import com.somoplay.zombie.scene.SPMapConstant;
import com.somoplay.zombie.ui.SPJoyStick;
import com.somoplay.zombie.web.SPDataCallback;

import org.json.JSONObject;

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
    private SPGameScene mMain;

    public SPSpriteManager(SPGameScene main) {
        mMain = main;
        mPlayer = new SPPlayer(true, "John Snow", 0, 0);       // for mSPain player
        mlstZombie = new ArrayList<SPZombie>();
        mlstPlayers = new ArrayList<SPPlayer>();
        random = new Random();
    }

    public void createStandaloneZombies(int count) {
        for (int i=0; i<=count; i++) {
            addZombie(i, Math.abs(random.nextInt() % SPMap.width * SPMapConstant.TILESIZE), Math.abs(random.nextInt() % SPMap.height * SPMapConstant.TILESIZE), 5);
        }
    }

    public void addZombie(int index, float fX, float fY, int health) {
        mlstZombie.add(new SPZombie(index, fX, fY, health));
    }

    public void addPlayers(SPPlayer newPlayer) {
        if (newPlayer.getUserName().equals(mPlayer.getUserName()) == false)
            mlstPlayers.add(newPlayer);
    }

    public void chasePlayer(int mobIndex, float fX, float fY) {
        for(SPZombie zombie: mlstZombie) {
            if (zombie.getMonsterIndex() == mobIndex) {
                zombie.setHitBox(fX, fY);
                return;
            }
        }
    }

    public void addBullets(String username, float x, float y, float angle) {
        for(SPPlayer player: mlstPlayers) {
            if (player.getUserName().equals(username)) {
                player.AddBullet(x, y, angle);
            }
        }
    }

    public void removePlayers(String username) {
        for(SPPlayer player: mlstPlayers) {
            if (player.getUserName().equals(username)) {
                player.setDead();
                break;
            }
        }
    }

    public void removeMonster(int index) {
        for(SPZombie zombie: mlstZombie) {
            if (index == zombie.getMonsterIndex()) {
                Gdx.app.log("ERROR", "Zombie killed: " + index);
                zombie.setDead();
                break;
            }
        }
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
        int monsterIndex = mPlayer.updateBullets(mlstZombie);
        if (monsterIndex >= 0) {
            removeMonster(monsterIndex);
            mMain.getSPMessageHandler().requestKilledMonster(monsterIndex, new SPDataCallback() {
                @Override
                public void responseData(JSONObject message) {
                }
            });
        }

        for(SPPlayer player: mlstPlayers) {
            player.updateBullets(mlstZombie);
        }
    }

    public void updateEnemy(float dt) {
        for(SPZombie zombie: mlstZombie){
            zombie.update(dt, mPlayer, mMain.getSPMessageHandler().isConnected());
        }
    }

    public ArrayList<SPZombie> getZombie() {
        return mlstZombie;
    }
}