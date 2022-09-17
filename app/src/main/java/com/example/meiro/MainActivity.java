package com.example.meiro;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.gotocamera).setOnTouchListener(new OnSwipeTouchListner(MainActivity.this) {
            public void onSwipeTop() {
                startActivity(new Intent(MainActivity.this,CameraActivity.class));
            }
            public void onSwipeLeft() {
                startActivity(new Intent(MainActivity.this,CameraActivity.class));
            }
        });
    }
}