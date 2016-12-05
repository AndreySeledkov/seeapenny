package com.seeapenny.client.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.MenuItem;
import com.seeapenny.client.General;
import com.seeapenny.client.R;
import com.seeapenny.client.quickaction.ActionItem;
import com.seeapenny.client.quickaction.QuickAction;

/**
 * Created by IntelliJ IDEA.
 * User: Sony
 * Date: 04.06.13
 * Time: 1:30
 * To change this template use File | Settings | File Templates.
 */
public class HistoryActivity extends SeeapennyActivity {

    private ActionBar actionBar;

    private View headerOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_lists);

        initActionBar();
    }

    private void initActionBar() {
        actionBar = getSupportActionBar();


        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayUseLogoEnabled(false);

        if (!app.getSessionState().isSessionValid()) {
            actionBar.setIcon(R.drawable.header_offline);
        } else {
            actionBar.setIcon(R.drawable.header_online);
        }

        LayoutInflater mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        headerOptions = mInflater.inflate(R.layout.header_options, null);

        actionBar.setCustomView(headerOptions);
        actionBar.setDisplayShowCustomEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }

        return true;
    }
}
