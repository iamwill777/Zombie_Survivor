package com.somoplay.zombie.web;
import com.somoplay.zombie.main.SPGameScene;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

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

    public void initOnReviceDataHandler() {
        onNotifyLogin();
        onNotifyPlayerLocation();
        onUserLeaveFromRoom();
    }

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
                SPMessageHandler.super.mMain.socketHandler("notify moving", event.getMessage());
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

    public void requestAttempLogin(String userName, float X, float Y, SPDataCallback func) {

        JSONObject requestLogin = new JSONObject();
        try {
            requestLogin.put("rid", 1);
            requestLogin.put("username", userName);
            requestLogin.put("X", X);
            requestLogin.put("Y", Y);
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
}
