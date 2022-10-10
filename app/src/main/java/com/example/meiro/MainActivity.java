package com.example.meiro;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        findViewById(R.id.gotocamera).setOnTouchListener(new OnSwipeTouchListner(MainActivity.this) {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                animate();
                return super.onTouch(v, event);
            }

            public void onSwipeTop() {
                animate();
                startActivity(new Intent(MainActivity.this,CameraActivity.class));
            }
            public void onSwipeBottom() {
                animate();
                startActivity(new Intent(MainActivity.this,CameraActivity.class));
            }public void onSwipeRight() {
                animate();
                startActivity(new Intent(MainActivity.this,CameraActivity.class));
            }
            public void onSwipeLeft() {
                animate();
                startActivity(new Intent(MainActivity.this,CameraX.class));
            }
        });
    }

    public void animate() {
        ImageView image = (ImageView)findViewById(R.id.imageView);
        Animation animation =
                AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom);
        image.startAnimation(animation);
    }
}