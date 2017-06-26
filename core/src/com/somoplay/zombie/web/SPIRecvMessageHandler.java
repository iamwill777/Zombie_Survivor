package com.somoplay.zombie.web;

import org.json.JSONObject;

/**
 * Created by bback99 on 2017-06-26.
 */

public interface SPIRecvMessageHandler {
    void socketHandler(String type, JSONObject message);
    void createStandaloneZombies(int num);
    void getRequestLoginInfo(JSONObject requestLogin);
}
