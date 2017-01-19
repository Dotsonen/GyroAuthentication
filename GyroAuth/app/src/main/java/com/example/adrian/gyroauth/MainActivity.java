package com.example.adrian.gyroauth;

import android.app.ActivityOptions;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Explode;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        final ImageView iv = (ImageView) findViewById(R.id.imageView);
        final Animation an = AnimationUtils.loadAnimation(MainActivity.this, R.anim.rotate);
        final Animation anFade = AnimationUtils.loadAnimation(MainActivity.this, R.anim.fadeout);


      //  getWindow().setExitTransition(new Explode());

        iv.startAnimation(an);
        an.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                iv.startAnimation(anFade);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        anFade.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                finish();
                Intent i = new Intent(MainActivity.this, Measurements.class);
                startActivity(i);


            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }
}
