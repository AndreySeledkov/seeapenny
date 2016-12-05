package com.seeapenny.client.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.seeapenny.client.R;


public class SplashActivity extends SeeapennyActivity implements Runnable {

    private static final long MIN_TIME = 500;

    private ImageView loadingView;

    private volatile boolean alive;

    public SplashActivity() {
        super(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.logo);

        loadingView = findView(R.id.loading);
    }

    @Override
    protected void onStart() {
        super.onStart();

        Thread thread = new Thread(this);
        thread.start();
    }

    @Override
    protected void onStop() {
        super.onStop();

        alive = false;
    }

    @Override
    protected void onResume() {
        super.onResume();

        Animation animation = AnimationUtils.loadAnimation(this, R.anim.cycled_fade);
        loadingView.startAnimation(animation);
    }

    @Override
    protected void onPause() {
        super.onPause();

        loadingView.clearAnimation();
    }

    @Override
    public void run() {
        alive = true;
        while (alive) {
            try {
                Thread.sleep(MIN_TIME);
            } catch (InterruptedException e) {
            }
            if (!alive) {
                break;
            }

            Intent intent = new Intent(SplashActivity.this, ShopListActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
