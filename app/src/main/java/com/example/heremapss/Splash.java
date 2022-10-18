package com.example.heremapss;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import java.util.Timer;
import java.util.TimerTask;

public class Splash extends AppCompatActivity {

    ImageView im;
    long delay = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        //no night mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        //create and set the timer

        Timer RunSplash = new Timer();

        //give the timer a task to do
        TimerTask ShowSplash = new TimerTask() {
            @Override
            public void run() {
                //close the splash screen
                finish();

                //launch the 2nd screen --> Login Screen
                //Intent service --> create an object of the second screen
                Intent FirstIntent = new Intent(Splash.this,Login.class);
                startActivity(FirstIntent);

            }
        };
        //Start the Timer
        RunSplash.schedule(ShowSplash,delay);

    }
}