package com.somoplay.zombie;

import android.os.Bundle;
import android.util.Log;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.somoplay.zombie.demo1.Drop;
import com.somoplay.zombie.main.SPGameCtrl;
import com.somoplay.zombie.protocol.SPMainListener;

public class AndroidLauncher extends AndroidApplication implements SPMainListener {
	private static final String TAG = "AndroidLauncher";

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		//initialize(new MyGdxGame(), config);
		//initialize(new PixmapDemo(), config);
		//initialize(new com.somoplay.zombie.demo.GraphicsDemo(), config);
		initialize(new SPGameCtrl(this), config);

		//initialize(new Drop(), config);
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	public void logPrint(String data) {
		Log.d(TAG, "example: " + data);
	}
}
