package com.seeapenny.client.activity;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.MenuItem;
import com.seeapenny.client.R;
import com.seeapenny.client.adapter.DrawerMenu;
import com.seeapenny.client.adapter.MenuAdapter;

import java.util.ArrayList;

public class AboutAppActivity extends SeeapennyActivity {

    private ActionBar actionBar;
    private View headerOptions;

    private TextView rateApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_app);

        rateApp = (TextView) findViewById(R.id.rate_app);
        rateApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.seeapenny.client"));
                browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(browserIntent);
            }
        });

        initActionBar();
    }

    private void initActionBar() {
        actionBar = getSupportActionBar();


        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayUseLogoEnabled(false);

        if (app.getSessionState().isSessionValid()) {
            actionBar.setIcon(R.drawable.header_online);
        } else {
            actionBar.setIcon(R.drawable.header_offline);
        }

        LayoutInflater mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        headerOptions = mInflater.inflate(R.layout.header_options, null);
        actionBar.setCustomView(headerOptions);
        actionBar.setDisplayShowCustomEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }
}
