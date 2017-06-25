package com.somoplay.zombie.web;
import com.somoplay.zombie.main.SPGameScene;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by shoong on 2017-06-06.
 * For define packet message between clients and servers
 * For reqeust and answer message to use request() method on SocketManager class
 * For notify message to use notifyMessage() method on SocketManager class
 */

public class SPMessageHandler extends SPSocketManager{

    public SPMessageHandler(SPGameScene main) {
        super(main);
        initOnReviceDataHandler();
    }

    public boolean isConnected() {
        return mSocket.connected();
    }

    public void initOnReviceDataHandler() {
        onNotifyLogin();
        onNotifyPlayerLocation();
        onUserLeaveFromRoom();
        onNotifyMonsters();
        onNotifyPlayerShooting();
        onChasePlayer();
    }

    // receive messages from server
    public void onNotifyLogin() {
        List<SPDataListener> lstListener = new ArrayList<SPDataListener>();
        lstListener.add(new SPDataListener() {
            @Override
            public void receiveData(SPDataEvent event) {
                SPMessageHandler.super.mMain.socketHandler("notify login", event.getMessage());
            }
        });
        super.mMapListeners.put("onNotifyLogin", lstListener);
    }

    public void onUserLeaveFromRoom() {
        List<SPDataListener> lstListener = new ArrayList<SPDataListener>();
        lstListener.add(new SPDataListener() {
            @Override
            public void receiveData(SPDataEvent event) {
                SPMessageHandler.super.mMain.socketHandler("notify user left", event.getMessage());
            }
        });
        super.mMapListeners.put("onUserLeaveFromRoom", lstListener);
    }

    public void onNotifyPlayerLocation() {
        List<SPDataListener> lstListener = new ArrayList<SPDataListener>();
        lstListener.add(new SPDataListener() {
            @Override
            public void receiveData(SPDataEvent event) {
                SPMessageHandler.super.mMain.socketHandler("notify moving", event.getMessage());
            }
        });
        super.mMapListeners.put("onNotifyPlayerLocation", lstListener);
    }

    public void onNotifyMonsters() {
        List<SPDataListener> lstListener = new ArrayList<SPDataListener>();
        lstListener.add(new SPDataListener() {
            @Override
            public void receiveData(SPDataEvent event) {
                SPMessageHandler.super.mMain.socketHandler("notify monsters", event.getMessage());
            }
        });
        super.mMapListeners.put("onNotifyMonsters", lstListener);
    }

    public void onNotifyPlayerShooting() {
        List<SPDataListener> lstListener = new ArrayList<SPDataListener>();
        lstListener.add(new SPDataListener() {
            @Override
            public void receiveData(SPDataEvent event) {
                SPMessageHandler.super.mMain.socketHandler("notify player shooing", event.getMessage());
            }
        });
        super.mMapListeners.put("onNotifyPlayerShooting", lstListener);
    }

    public void onChasePlayer() {
        List<SPDataListener> lstListener = new ArrayList<SPDataListener>();
        lstListener.add(new SPDataListener() {
            @Override
            public void receiveData(SPDataEvent event) {
                SPMessageHandler.super.mMain.socketHandler("notify chase player", event.getMessage());
            }
        });
        super.mMapListeners.put("onChasePlayer", lstListener);
    }

    // request messages to server
    public void requestConnectorInfo(SPDataCallback func) {
        Random random = new Random();
        JSONObject requestConnectorInfo = new JSONObject();
        try {
            requestConnectorInfo.put("uid", random.nextInt() % 1000000);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        request("gate.gateHandler.queryEntry", requestConnectorInfo, func);
    }

    public void requestAttempLogin(SPDataCallback func) {


        JSONObject requestLogin = new JSONObject();
        try {
            requestLogin.put("rid", 1);
            requestLogin.put("username", mMain.getSPSpriteManager().getPlayer().getUserName());
            requestLogin.put("X", mMain.getSPSpriteManager().getPlayer().getX());
            requestLogin.put("Y", mMain.getSPSpriteManager().getPlayer().getY());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        request("connector.entryHandler.entry", requestLogin, func);
    }

    public void notifyPlayerPosition(float fX, float fY, float angle) {
        JSONObject ntfUserPosition = new JSONObject();
        try {
            ntfUserPosition.put("X", fX);
            ntfUserPosition.put("Y", fY);
            ntfUserPosition.put("angle", angle);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        notifyMessage("room.roomHandler.notifyPlayerLocation", ntfUserPosition);
    }

    public void notifyPlayerShooting(float fX, float fY, float angle) {
        JSONObject ntfPlayerShooting = new JSONObject();
        try {
            ntfPlayerShooting.put("X", fX);
            ntfPlayerShooting.put("Y", fY);
            ntfPlayerShooting.put("angle", angle);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        notifyMessage("room.roomHandler.notifyPlayerShooting", ntfPlayerShooting);
    }

    public void requestKilledMonster(int monsterIndex, SPDataCallback func) {
        JSONObject requestKilledMonster = new JSONObject();
        try {
            requestKilledMonster.put("idKilledMonster", monsterIndex);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        request("room.roomHandler.requestKilledMonster", requestKilledMonster, func);
    }
}
