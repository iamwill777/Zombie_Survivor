package com.somoplay.zombie;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.somoplay.zombie.web.SPSocketManager;

public class MainActivity extends AppCompatActivity {

    //public static final String CHAT_SERVER_URL = "https://socketio-chat.now.sh/";
    public static final String CHAT_SERVER_URL = "http://10.0.2.2:13337/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout linearLayout = new LinearLayout(this);
        LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);

        Button btnStartGame = new Button(this);
        linearLayout.addView(btnStartGame, buttonParams);
        btnStartGame.setText("Start Game");
        btnStartGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            startActivity(new Intent(getBaseContext(), AndroidLauncher.class));
            }
        });

        Button btnConnect = new Button(this);
        linearLayout.addView(btnConnect, buttonParams);
        btnConnect.setText("Connect to Server");
        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
           // SPSocketManager.getInstance().attempLogin();
            }
        });

        setContentView(linearLayout);
        //SocketManager.getInstance().connect(CHAT_SERVER_URL);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //SocketManager.getInstance().disconnect();
    }
}
