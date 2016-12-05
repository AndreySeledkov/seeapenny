package com.seeapenny.client.activity;

import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.*;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.view.*;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.seeapenny.client.AppState;
import com.seeapenny.client.General;
import com.seeapenny.client.R;
import com.seeapenny.client.SeeapennyApp;
import com.seeapenny.client.adapter.Good;
import com.seeapenny.client.adapter.ShopList;
import com.seeapenny.client.http.HttpCommand;
import com.seeapenny.client.http.HttpCommandListener;
import com.seeapenny.client.server.*;
import com.seeapenny.client.server.requests.LoginRequest;
import com.seeapenny.client.server.responses.LoginResponse;
import com.seeapenny.client.service.CategoryService;
import com.seeapenny.client.service.GoodService;
import com.seeapenny.client.service.Services;
import com.seeapenny.client.service.ShopListService;

import java.util.List;
import java.util.Locale;

public class SeeapennyActivity extends SherlockFragmentActivity {

    public static final int RESULT_SPEECH = 1;

    private boolean disable = true;

    private Dialog wait;
    private boolean shown;

    protected SessionState sessionState;
    protected AppState appState;
    protected SeeapennyApp app;
    protected Resources resources;

    private TelephonyManager telephonyManager;
    private String language;
    private String simCountryIso;

    protected ShopListService shopListService = Services.getListService();
    protected GoodService goodService = Services.getGoodService();
    protected CategoryService categoryService = Services.getCategoryService();

    public SeeapennyActivity() {
        app = SeeapennyApp.getInstance();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        // Check if billing is supported.
        language = Locale.getDefault().getLanguage();
        simCountryIso = telephonyManager.getSimCountryIso().toLowerCase();

        appState = app.getAppState();


        app.getSoundPlayer().initSounds(this);

        resources = getResources();

    }

    @Override
    protected void onResume() {
        super.onResume();

        app.setCurrentActivity(this);


        if (!app.getRotation()) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        if (app.getScreen()) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            getWindow().clearFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }

        if (app.getVoiceRecognitionScreen()) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }

        language = Locale.getDefault().getLanguage();
        shown = true;
    }

    @Override
    protected void onPause() {

        shown = false;

        app.setCurrentActivity(null);

//        PopupNotificationManager manager = EconomyApp.getPopupNotificationManager();
//        PopupNotificationMessage message = manager.getCurrent();
//        if (message != null) {
//            manager.hideCurrent();
//        }

        super.onPause();
    }


    public void showPopupNotification(PopupNotificationMessage notificationMessage) {
        if (!shown || isFinishing()) return;

        ViewGroup content = findView(android.R.id.content);
        if (content != null && content.getChildCount() > 0) {
            View anchor = content.getChildAt(0);
            SeeapennyApp.getPopupNotificationManager().show(notificationMessage, anchor,
                    Gravity.CENTER_HORIZONTAL | Gravity.TOP, 0, 0);
        }
    }

    public void disableWaitForNextCommand() {
        this.disable = false;
    }

    @SuppressWarnings("unchecked")
    public final <V extends View> V findView(int viewId) {
        V v = (V) findViewById(viewId);
        return v;
    }

    @SuppressWarnings("unchecked")
    public final <V extends View> V findView(int viewId, View.OnClickListener listener) {
        V v = (V) findViewById(viewId);
        v.setOnClickListener(listener);
        return v;
    }

    public void showToast(String message) {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast_message, null);

        TextView text = (TextView) layout.findViewById(R.id.text);
        text.setText(message);

        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();

    }

    public boolean isShown() {
        return shown;
    }

    public void makeSoundAdd() {
        if (app.getSound()) {
            app.getSoundPlayer().play(R.raw.button_1, 1.0f);
        }
    }

    public void makeSoundRemove() {
        if (app.getSound()) {
            app.getSoundPlayer().play(R.raw.button_2, 1.0f);
        }
    }

    protected void makeVibrate() {
        if (app.getVibrate()) {
            app.getVibrator().vibrate(300);
        }
    }

    protected abstract class RightDrawableOnTouchListener implements View.OnTouchListener {
        Drawable drawable;
        private int fuzz = 10;


        public RightDrawableOnTouchListener(TextView view) {
            super();
            final Drawable[] drawables = view.getCompoundDrawables();
            if (drawables != null && drawables.length == 4)
                this.drawable = drawables[2];
        }

        /*
        * (non-Javadoc)
        *
        * @see android.view.View.OnTouchListener#onTouch(android.view.View, android.view.MotionEvent)
        */
        @Override
        public boolean onTouch(final View v, final MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN && drawable != null) {
                final int x = (int) event.getX();
                final int y = (int) event.getY();
                final Rect bounds = drawable.getBounds();
                if (x >= (v.getRight() - bounds.width() - fuzz) && x <= (v.getRight() - v.getPaddingRight() + fuzz)
                        && y >= (v.getPaddingTop() - fuzz) && y <= (v.getHeight() - v.getPaddingBottom()) + fuzz) {
                    return SonDrawableTouch(event);
                }
            }
            return false;
        }

        public abstract boolean onDrawableTouch(final MotionEvent event);

    }

}
