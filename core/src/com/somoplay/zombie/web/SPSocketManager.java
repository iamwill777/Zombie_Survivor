package com.somoplay.zombie.web;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.somoplay.zombie.main.SPGameScene;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Created by bback99 on 2017-05-27.
 */

public class SPSocketManager {

    private final static String JSONARRAY_FLAG = "[";
    protected Socket mSocket;
    private Boolean mIsConnected = false;
    private SPIRecvMessageHandler mRecvMessageHandler;
    private int mReqId;
    protected Map<Integer, SPDataCallback> mMapCbs;
    protected Map<String, List<SPDataListener>> mMapListeners;
    private int mState = -1;  // for gate server and connector server

    public static final String GATE_SERVER_URL = "http://10.0.2.2:3014/";
    //public static final String GATE_SERVER_URL = "http://10.51.205.75:3014/";
    //public static final String GATE_SERVER_URL = "http://192.168.0.103:3014/";

    public SPSocketManager(SPIRecvMessageHandler recvMessage) {

        mMapCbs = new HashMap<Integer, SPDataCallback>();
        mMapListeners = new HashMap<String, List<SPDataListener>>();
        mRecvMessageHandler = recvMessage;

        onMessage();
    }

    public void connectToGate() {
        connect(GATE_SERVER_URL);
    }

    public void connect(String url) {
        try {
            mSocket = IO.socket(url);
            mSocket.on(Socket.EVENT_CONNECT, onConnect);
            mSocket.on(Socket.EVENT_DISCONNECT, onDisconnect);
            mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
            mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
            mSocket.on(Socket.EVENT_MESSAGE, onMessage);
            mSocket.connect();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public void disconnect() {
        mSocket.disconnect();
        mSocket.off(Socket.EVENT_CONNECT, onConnect);
        mSocket.off(Socket.EVENT_DISCONNECT, onDisconnect);
        mSocket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.off(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
    }

    public void request(Object... args) {
        if (args.length < 2 || args.length > 3) {
            throw new RuntimeException("the request arguments is error.");
        }
        // first argument must be string
        if (!(args[0] instanceof String)) {
            throw new RuntimeException("the route of request is error.");
        }

        String route = args[0].toString();
        JSONObject msg = null;
        SPDataCallback cb = null;

        if (args.length == 2) {
            if (args[1] instanceof JSONObject)
                msg = (JSONObject) args[1];
            else if (args[1] instanceof SPDataCallback)
                cb = (SPDataCallback) args[1];
        } else {
            msg = (JSONObject) args[1];
            cb = (SPDataCallback) args[2];
        }
        msg = filter(msg);
        mReqId++;
        mMapCbs.put(mReqId, cb);
        sendMessage(mReqId, route, msg);
    }

    private void sendMessage(int reqId, String route, JSONObject msg) {
        mSocket.send(SPProtocol.encode(reqId, route, msg));
    }

    public void notifyMessage(String route, JSONObject msg) {
        request(route, msg);
    }

    private void reconnectToConnector(JSONObject message) {
        String connector_url = "http://";
        try {
            String host = message.getString("host");
            int port = message.getInt("port");
            if (host.equals("127.0.0.1")) {
                host = "10.0.2.2";
            }
            connector_url += host;
            connector_url += ":";
            connector_url += Integer.toString(port);
            connector_url += "/";

            disconnect();
            connect(connector_url);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private JSONObject filter(JSONObject msg) {
        if (msg == null) {
            msg = new JSONObject();
        }
        long date = System.currentTimeMillis();
        try {
            msg.put("timestamp", date);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return msg;
    }

    // When Server is running
    private Emitter.Listener onConnect = new Emitter.Listener() {

        @Override
        public void call(Object... args) {
            mIsConnected = true;
            if (mState < 1) {
                mState = 3;
                requestConnectorInfo(new SPDataCallback() {
                    @Override
                    public void responseData(JSONObject message) {
                        reconnectToConnector(message);
                    }
                });
            } else {
                mState = -1;
                requestAttempLogin(new SPDataCallback() {
                    @Override
                    public void responseData(JSONObject message) {
                        mRecvMessageHandler.socketHandler(message);
                    }
                });
            }
        }
    };

    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            mIsConnected = false;
            Gdx.app.log("DEBUG", "Socket is disconnected.");
        }
    };

    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Gdx.app.log("DEBUG", "connection is terminated. : " + args.toString());
            mIsConnected = false;
            mRecvMessageHandler.createStandaloneZombies(5);
        }
    };

    private Emitter.Listener onMessage = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            String data = args[0].toString();
            Gdx.app.log("DEBUG", data);
            if (data.indexOf(JSONARRAY_FLAG) == 0) {
                processMessageByArray(data);
            } else {
                processMessageByObject(data);
            }
        }
    };

    private void processMessageByObject(String msg) {
        int id;
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(msg);
            // request message
            if (jsonObject.has("id")) {
                id = jsonObject.getInt("id");
                SPDataCallback cb = mMapCbs.get(id);
                if(cb != null)
                    cb.responseData(jsonObject.getJSONObject("body"));
                else {
                    if (!jsonObject.isNull("body")) {
                        JSONObject body = jsonObject.getJSONObject("body");
                        if (!body.isNull("code")) {
                            emit(jsonObject.getJSONObject("body").getString("route"), jsonObject);
                        }
                    }
                }
                mMapCbs.remove(id);
            }
            // broadcast message
            else
                emit(jsonObject.getString("route"), jsonObject.getJSONObject("body"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void processMessageByArray(String msgs) {
        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(msgs);
            for (int i = 0; i < jsonArray.length(); i++) {
                processMessageByObject(jsonArray.getJSONObject(i).toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void emit(String route, JSONObject message) {
        List<SPDataListener> list = mMapListeners.get(route);
        if (list == null) {
            Gdx.app.log("DEBUG", "there is no listeners.");
            return;
        }
        for (SPDataListener listener : list) {
            SPDataEvent event = new SPDataEvent(this, message);
            listener.receiveData(event);
        }
    }

    // receive messages from server
    public void onMessage() {
        List<SPDataListener> lstListener = new ArrayList<SPDataListener>();
        lstListener.add(new SPDataListener() {
            @Override
            public void receiveData(SPDataEvent event) {
                mRecvMessageHandler.socketHandler(event.getMessage());
            }
        });
        mMapListeners.put("onMessage", lstListener);
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
            mRecvMessageHandler.getRequestLoginInfo(requestLogin);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        request("connector.entryHandler.entry", requestLogin, func);
    }

    private void sendData(int msgNum, String username, JSONObject message) {

        try {
            message.put("code", 10004);
            message.put("route", "onMessage");
            message.put("msgNum", msgNum);
            message.put("username", username);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        notifyMessage("room.roomHandler.notifyData", message);
    }

    public void notifyData(int msgNum, String username, float fX, float fY, float posX, float posY, float angle) {
        JSONObject ntfUserPosition = new JSONObject();
        try {
            ntfUserPosition.put("username", username);
            ntfUserPosition.put("X", fX);
            ntfUserPosition.put("Y", fY);
            ntfUserPosition.put("posX", posX);
            ntfUserPosition.put("posY", posY);
            ntfUserPosition.put("angle", angle);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        sendData(msgNum, username, ntfUserPosition);
    }

    public void requestKilledMonster(int msgNum, String username, int monsterIndex, SPDataCallback func) {
        JSONObject requestKilledMonster = new JSONObject();
        try {
            requestKilledMonster.put("idKilledMonster", monsterIndex);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        sendData(msgNum, username, requestKilledMonster);
    }

    public Boolean isConnected() {
        return mIsConnected;
    }
}
