package com.example.joyeria_nunbelem;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends Activity {

    private Handler handler = new Handler();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);


        handler.postDelayed(() -> {

            Intent loginIntent = new Intent(SplashActivity.this, login.class);
            startActivity(loginIntent);

        }, 1000);
    }
}
