package com.example.fireauthlog;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
//Splash Screen For showing our screen logo
public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        Thread thread = new Thread() {

            @Override
            public void run() {
                super.run();
                try {
                    sleep(5000);
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


            }
        };
        thread.start();
    }
}
