package com.seeapenny.client.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.seeapenny.client.General;
import com.seeapenny.client.R;
import com.seeapenny.client.quickaction.ActionItem;
import com.seeapenny.client.quickaction.QuickAction;

/**
 * Created by IntelliJ IDEA.
 * User: Sony
 * Date: 16.04.13
 * Time: 23:53
 * To change this template use File | Settings | File Templates.
 */
public class GuideActivity extends SeeapennyActivity implements View.OnClickListener {

    private ActionBar actionBar;
    private View headerOptions;


    Gallery ga;
    int width, height;
    LinearLayout linear;
    LinearLayout layout;
    Integer[] pics = {
            R.drawable.gallery_photo_1,
            R.drawable.gallery_photo_2,
            R.drawable.gallery_photo_3,
            R.drawable.gallery_photo_4,
            R.drawable.gallery_photo_5
    };
    ImageView paging;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.guide_layout);

        initActionBar();

        layout = (LinearLayout) findViewById(R.id.imageLayout1);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        width = displaymetrics.heightPixels;
        height = displaymetrics.widthPixels;

        for (int i = 0; i < pics.length; i++) {
            paging = new ImageView(this);
            paging.setId(i);
            paging.setBackgroundResource(R.drawable.icon);
            layout.addView(paging);
        }

        ga = (Gallery) findViewById(R.id.thisgallery);
        ga.setAdapter(new ImageAdapter(this));

        ga.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                System.out.println("SELECTED : " + arg2);
            }
        });
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
    public boolean onCreateOptionsMenu(Menu menu) {
        com.actionbarsherlock.view.MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.menu_guide, menu);

        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (app.getSessionState().isSessionValid()) {
            actionBar.setIcon(R.drawable.header_online);
        } else {
            actionBar.setIcon(R.drawable.header_offline);
        }
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

        }
    }

    public class ImageAdapter extends BaseAdapter {

        private Context ctx;
        int imageBackground;
        int pre = -1;

        public ImageAdapter(Context c) {
            ctx = c;
        }

        public int getCount() {

            return pics.length;
        }

        public View getView(int arg0, View convertView, ViewGroup arg2) {

            ImageView iv;
            LinearLayout layoutnew = new LinearLayout(getApplicationContext());
            layoutnew.setOrientation(LinearLayout.VERTICAL);

            if (convertView == null) {
                iv = new ImageView(ctx);
                iv.setImageResource(pics[arg0]);
                iv.setScaleType(ImageView.ScaleType.FIT_XY);
                int temp = (int) (height / 1.7f);
                int temp_y = (int) ((3 * temp) / 2.0f);
                iv.setLayoutParams(new Gallery.LayoutParams(temp, temp_y));
                iv.setBackgroundResource(imageBackground);
            } else {
                iv = (ImageView) convertView;
            }
            TextView tv = new TextView(ctx);
            tv.setText("Page " + (arg0 + 1));
            tv.setTextColor(0xFFFFFFFF);
            tv.setPadding(0, 15, 0, 0);
            tv.setTextSize(18);
            tv.setGravity(Gravity.CENTER);
            layoutnew.addView(iv);
            layoutnew.addView(tv);

            return layoutnew;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            if (pre != -1) {
                ImageView img = (ImageView) findViewById(pre);
                img.setBackgroundResource(R.drawable.icon);
            }
            ImageView img1 = (ImageView) findViewById(position);
            img1.setBackgroundResource(R.drawable.icon);
            this.pre = position;
            return position;
        }
    }
}