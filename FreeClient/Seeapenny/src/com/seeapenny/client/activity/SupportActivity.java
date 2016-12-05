package com.seeapenny.client.activity;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.MenuItem;
import com.seeapenny.client.General;
import com.seeapenny.client.R;
import com.seeapenny.client.quickaction.ActionItem;
import com.seeapenny.client.quickaction.QuickAction;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by IntelliJ IDEA.
 * User: Sony
 * Date: 14.04.13
 * Time: 16:00
 * To change this template use File | Settings | File Templates.
 */
public class SupportActivity extends SeeapennyActivity implements View.OnClickListener {

    private ActionBar actionBar;
    private View headerOptions;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help_layout);

        final EditText messageComplain = (EditText) findViewById(R.id.messageComplain);

        View next = findView(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkConnection()) {

                    String complain = messageComplain.getText().toString().trim();
                    Intent mailClient = new Intent(Intent.ACTION_VIEW);
                    mailClient.setClassName("com.google.android.gm", "com.google.android.gm.ComposeActivityGmail");

                    mailClient.setType("text/plain");
                    mailClient.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"support@seeapenny.net"});
                    mailClient.putExtra(Intent.EXTRA_SUBJECT, resources.getString(R.string.feedback));
                    mailClient.putExtra(android.content.Intent.EXTRA_TEXT, complain);

                    try {
                        Process process = Runtime.getRuntime().exec("logcat -d");
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));

                        StringBuilder log = new StringBuilder();
                        String line;
                        while ((line = bufferedReader.readLine()) != null) {
                            log.append(line);
                        }

                        File root = new File(Environment.getExternalStorageDirectory(), "logs.txt");
                        if (!root.exists()) {
                            root.mkdirs();
                        }
                        File gpxfile = new File(root, "logs.txt");
                        FileWriter writer = new FileWriter(gpxfile);
                        writer.append(log);
                        writer.flush();
                        writer.close();

                        Uri uri = Uri.fromFile(gpxfile);

                        mailClient.putExtra(Intent.EXTRA_STREAM, uri);

                    } catch (IOException e) {
                    }


                    try {
                        startActivity(mailClient);
                    } catch (ActivityNotFoundException err) {
                        Toast t = Toast.makeText(getApplicationContext(),
                                resources.getString(R.string.gmail_not_found), Toast.LENGTH_SHORT);
                        t.show();
                    }
                } else {
                    showNoConnectionDialog();
                }
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

        LayoutInflater mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        headerOptions = mInflater.inflate(R.layout.header_options, null);
        actionBar.setCustomView(headerOptions);
        actionBar.setDisplayShowCustomEnabled(true);
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {

        com.actionbarsherlock.view.MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.menu_help, menu);

        return true;
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

        }
    }
}


